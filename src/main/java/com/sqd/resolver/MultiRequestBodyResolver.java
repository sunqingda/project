package com.sqd.resolver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqd.annotation.MultiRequestBody;
import com.sqd.util.IOUtils;
import com.sqd.util.StringUtils;
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
 * 1、支持通过注解的 value 指定 JSON 的 key 来解析对象
 * 2、支持通过注解无 value，直接根据参数名来解析对象
 * 3、支持基本类型的注入
 * 4、支持通过注解无 value 且参数名不匹配 JSON 串的 key 时，根据属性解析对象
 *
 * @author sqd
 */
public class MultiRequestBodyResolver implements HandlerMethodArgumentResolver {

    private static final Set<Class> classSet = new HashSet<>(16);
    private static ObjectMapper objectMapper = new ObjectMapper();
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
     * 支持的方法参数类型
     *
     * @see MultiRequestBody
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MultiRequestBody.class);
    }

    /**
     * 参数解析
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Object result;
        Object value;

        // 获取请求体
        String requestBody = getRequestBody(webRequest);
        // 允许使用不带引号的字段
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 解析 JSON 串
        JsonNode rootNode = objectMapper.readTree(requestBody);
        // JSON 串为空抛出异常
        Assert.notNull(rootNode, String.format("param %s parsing failed", requestBody));

        // 获取注解
        MultiRequestBody multiRequestBody = parameter.getParameterAnnotation(MultiRequestBody.class);
        Assert.notNull(multiRequestBody, String.format("param %s parsing failed", requestBody));

        String key = multiRequestBody.value();
        // 根据注解 value 解析 JSON 串，如果没有根据参数的名字解析 JSON
        if (StringUtils.isNotBlank(key)) {
            value = rootNode.get(key);
            // 如果为参数必填但未根据 key 成功得到对应 value 抛出异常
            Assert.isTrue(multiRequestBody.required() && Objects.nonNull(value), String.format("required param %s is not present", key));
        } else {
            key = parameter.getParameterName();
            value = rootNode.get(key);
        }

        // 获取参数的类型
        Class<?> paramType = parameter.getParameterType();
        // 成功从 JSON 解析到对应 key 的 value
        if (Objects.nonNull(value)) {
            return objectMapper.readValue(value.toString(), paramType);
        }

        // 未从 JSON 解析到对应 key（可能是注解的 value 或者是参数名字） 的值，要么没传值，要么传的名字不对
        // 如果参数为基本数据类型，且为必传参数抛出异常
        Assert.isTrue(!(isBasicDataTypes(paramType) && multiRequestBody.required()), String.format("required param %s is not present", key));
        // 参数非基本数据类型，如果不允许解析外层属性，且为必传参数报错抛出异常
        Assert.isTrue(!(!multiRequestBody.parseAllFields() && multiRequestBody.required()), String.format("required param %s is not present", key));

        try {
            // 既然找不到对应参数，而且非基本类型，我们可以解析外层属性，将整个 JSON 作为参数进行解析。解析失败会抛出异常
            result = objectMapper.readValue(requestBody, paramType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 必填参数的话，看解析出来的参数是否对应，非必填直接返回吧
        if (multiRequestBody.required()) {
            Field[] declaredFields = paramType.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Assert.notNull(field.get(result), String.format("required param %s is not present", key));
            }
        }
        return result;
    }

    /**
     * 获取请求 JSON 字符串
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
