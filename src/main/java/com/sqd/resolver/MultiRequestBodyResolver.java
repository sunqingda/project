package com.sqd.resolver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqd.annotation.MultiRequestBody;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Sets;
import org.objectweb.asm.*;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * MultiRequestBody 解析器
 * 解决的问题：
 * 1、单个字符串等包装类都要写一个对象才可以用 @RequestBody 接收
 * 2、多个对象需要封装到一个对象里才可以用 @RequestBody 接收
 * 主要优势：
 * 1、支持通过注解的 value 指定 JSON 的 key 来解析对象
 * 2、支持通过注解无 value，直接根据参数名来解析对象
 * 3、支持基本类型的注入
 * 4、支持 GET 和其他请求方式注入
 * 5、支持通过注解无 value 且参数名不匹配 JSON 串 key 时，根据属性解析对象
 * 6、支持多余属性（不解析，不报错）、支持参数 “公用”（不指定 value 时，参数名不为 JSON 串的 key）
 * 7、支持当 value 和属性名找不到匹配的 key 时，对象是否匹配所有属性
 *
 * @author sqd
 */
public class MultiRequestBodyResolver implements HandlerMethodArgumentResolver {

    private static final Set<Class> classSet = Sets.newHashSet();
    private static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    static {
        classSet.add(Integer.class);
        classSet.add(Long.class);
        classSet.add(Short.class);
        classSet.add(Float.class);
        classSet.add(Double.class);
        classSet.add(Boolean.class);
        classSet.add(Byte.class);
        classSet.add(Character.class);
    }

    /**
     * @description 支持的方法参数类型
     * @see MultiRequestBody
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MultiRequestBody.class);
    }

    /**
     * @description 参数解析
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Object result = null;
        try {
            String requestBody = getRequestBody(webRequest);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            Assert.notNull(jsonNode, String.format("参数 %s 解析失败", requestBody));

            MultiRequestBody parameterAnnotation = parameter.getParameterAnnotation(MultiRequestBody.class);
            String key = parameterAnnotation.value();
            Object value;
            if (StringUtils.isNotBlank(key)) {
                value = jsonNode.get(key);
                Assert.isTrue(!(parameterAnnotation.required() && Objects.isNull(value)),
                        String.format("required param %s is not present", key));
            } else {
                key = parameter.getParameterName();
                value = jsonNode.get(key);
            }

            Class<?> paramType = parameter.getParameterType();
            Type type = Type.getType(paramType);
            if (Objects.nonNull(value)) {
                // 基本数据类型
                if (paramType.isPrimitive()) {
                    switch (type.getSort()) {
                        case Type.BOOLEAN:
                            return Boolean.valueOf(value.toString());
                        case Type.BYTE:
                            return Byte.valueOf(value.toString());
                        case Type.CHAR:
                            return value.toString().charAt(0);
                        case Type.SHORT:
                            return Short.valueOf(value.toString());
                        case Type.INT:
                            return Integer.valueOf(value.toString());
                        case Type.FLOAT:
                            return Float.valueOf(value.toString());
                        case Type.LONG:
                            return Long.valueOf(value.toString());
                        case Type.DOUBLE:
                            return Double.valueOf(value.toString());
                    }
                }
                // 基本数据类型包装类
                Number number;
                if (isBasicDataTypes(paramType)) {
                    switch (paramType.getTypeName()) {
                        case "java.lang.Boolean":
                            return Boolean.valueOf(value.toString());
                        case "java.lang.Byte":
                            return Byte.valueOf(value.toString());
                        case "java.lang.Character":
                            return value.toString().charAt(0);
                        case "java.lang.Short":
                            return Short.valueOf(value.toString());
                        case "java.lang.Integer":
                            return Integer.valueOf(value.toString());
                        case "java.lang.Float":
                            return Float.valueOf(value.toString());
                        case "java.lang.Long":
                            return Long.valueOf(value.toString());
                        case "java.lang.Double":
                            return Double.valueOf(value.toString());
                    }
                }
                // 字符串
                if (paramType == String.class) {
                    return value.toString();
                }
                // 其它他杂对象
                return objectMapper.readValue(value.toString(), paramType);
            }

            // 未解析到 value，将整个 JSON 串作为当前参数类型
            // 参数为基本数据类型，且为必传参数
            Assert.isTrue(!(isBasicDataTypes(paramType) && parameterAnnotation.required()),
                    String.format("required param %s is not present", key));

            // 非基本数据类型，不允许解析所有字段，且为必传参数
            Assert.isTrue(!(!parameterAnnotation.parseAllFields() && parameterAnnotation.required()),
                    String.format("required param %s is not present", key));

            result = objectMapper.readValue(requestBody, paramType);
            if (parameterAnnotation.required()) {
                Field[] declaredFields = paramType.getDeclaredFields();
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    Assert.isTrue(Objects.isNull(field.get(result)), String.format("required param %s is not present", key));
                }
            }
        } catch (Exception e) {
            return null;

        }
        return result;
    }

    /**
     * @description 获取请求 JSON 字符串
     */
    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String jsonBody = (String) webRequest.getAttribute(JSON_REQUEST_BODY, NativeWebRequest.SCOPE_REQUEST);
        if (StringUtils.isEmpty(jsonBody)) {
            try (BufferedReader reader = servletRequest.getReader()) {
                jsonBody = IOUtils.toString(reader);
                webRequest.setAttribute(JSON_REQUEST_BODY, jsonBody, NativeWebRequest.SCOPE_REQUEST);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonBody;
    }

    /**
     * 判断是否为基本数据类型包装类
     */
    private boolean isBasicDataTypes(Class clazz) {
        return classSet.contains(clazz);
    }

}
