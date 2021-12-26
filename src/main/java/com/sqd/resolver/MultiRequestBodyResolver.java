package com.sqd.resolver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqd.annotation.MultiRequestBody;
import com.sqd.query.Param;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.objectweb.asm.*;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.core.MethodParameter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sun.nio.ch.IOUtil;
import sun.reflect.MethodAccessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.Native;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ATHROW;

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

    private static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    /**
     * @param parameter 方法参数
     * @description 支持的方法参数类型
     * @see MultiRequestBody
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MultiRequestBody.class);
    }

    /**
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     * @description 参数解析
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Object result;
        String requestBody = getRequestBody(webRequest);

//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//        JsonNode jsonNode = objectMapper.readTree(requestBody);
//        if (null == jsonNode) {
//            return null;
//        }

        JacksonJsonParser jacksonJsonParser = new JacksonJsonParser();
        Map<String, Object> stringObjectMap = jacksonJsonParser.parseMap(requestBody);

        MultiRequestBody parameterAnnotation = parameter.getParameterAnnotation(MultiRequestBody.class);
        String key = parameterAnnotation.value();
        Object value;
        if (StringUtils.isNotBlank(key)) {
            value = stringObjectMap.get(key);
            if (ObjectUtils.isEmpty(value) && parameterAnnotation.required()) {
                throw new IllegalAccessException(String.format("required param %s is not present", key));
            }
        } else {
            key = parameter.getParameterName();
            value = stringObjectMap.get(key);
        }

        Class<?> paramType = parameter.getParameterType();

        if (null != value) {
            if (paramType.isPrimitive()) {

            }
            Type type = Type.getType(paramType);
            switch (type.getSort()) {
                case Type.BOOLEAN:
                    break;
                case Type.BYTE:
                    break;
                case Type.CHAR:
                    break;
                case Type.SHORT:
                    break;
                case Type.INT:
                    break;
                case Type.FLOAT:
                    break;
                case Type.LONG:
                    break;
                case Type.DOUBLE:
                    break;
                case Type.ARRAY:
                    break;
                case Type.OBJECT:
                    break;
            }
        }
        return value;
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
//    {
//        ArrayList<Method> methods = new ArrayList<Method>();
//        boolean isInterface = type.isInterface();
//        if (!isInterface) {
//            Class nextClass = type;
//            while (nextClass != Object.class) {
//                addDeclaredMethodsToList(nextClass, methods);
//                nextClass = nextClass.getSuperclass();
//            }
//        } else {
//            recursiveAddInterfaceMethodsToList(type, methods);
//        }
//
//        int n = methods.size();
//        String[] methodNames = new String[n];
//        Class[][] parameterTypes = new Class[n][];
//        Class[] returnTypes = new Class[n];
//        for (int i = 0; i < n; i++) {
//            Method method = methods.get(i);
//            methodNames[i] = method.getName();
//            parameterTypes[i] = method.getParameterTypes();
//            returnTypes[i] = method.getReturnType();
//        }
//
//        String className = type.getName();
//        String accessClassName = className + "MethodAccess";
//        if (accessClassName.startsWith("java.")) accessClassName = "reflectasm." + accessClassName;
//        Class accessClass;
//
//        AccessClassLoader loader = AccessClassLoader.get(type);
//        synchronized (loader) {
//            try {
//                accessClass = loader.loadClass(accessClassName);
//            } catch (ClassNotFoundException ignored) {
//                String accessClassNameInternal = accessClassName.replace('.', '/');
//                String classNameInternal = className.replace('.', '/');
//
//                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//                MethodVisitor mv;
//                cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, "com/esotericsoftware/reflectasm/MethodAccess",
//                        null);
//                {
//                    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
//                    mv.visitCode();
//                    mv.visitVarInsn(ALOAD, 0);
//                    mv.visitMethodInsn(INVOKESPECIAL, "com/esotericsoftware/reflectasm/MethodAccess", "<init>", "()V");
//                    mv.visitInsn(RETURN);
//                    mv.visitMaxs(0, 0);
//                    mv.visitEnd();
//                }
//                {
//                    mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "invoke",
//                            "(Ljava/lang/Object;I[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
//                    mv.visitCode();
//
//                    if (!methods.isEmpty()) {
//                        mv.visitVarInsn(ALOAD, 1);
//                        mv.visitTypeInsn(CHECKCAST, classNameInternal);
//                        mv.visitVarInsn(ASTORE, 4);
//
//                        mv.visitVarInsn(ILOAD, 2);
//                        Label[] labels = new Label[n];
//                        for (int i = 0; i < n; i++)
//                            labels[i] = new Label();
//                        Label defaultLabel = new Label();
//                        mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
//
//                        StringBuilder buffer = new StringBuilder(128);
//                        for (int i = 0; i < n; i++) {
//                            mv.visitLabel(labels[i]);
//                            if (i == 0)
//                                mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] {classNameInternal}, 0, null);
//                            else
//                                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//                            mv.visitVarInsn(ALOAD, 4);
//
//                            buffer.setLength(0);
//                            buffer.append('(');
//
//                            Class[] paramTypes = parameterTypes[i];
//                            Class returnType = returnTypes[i];
//                            for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
//                                mv.visitVarInsn(ALOAD, 3);
//                                mv.visitIntInsn(BIPUSH, paramIndex);
//                                mv.visitInsn(AALOAD);
//                                Type paramType = Type.getType(paramTypes[paramIndex]);
//
//
//                            buffer.append(')');
//                            buffer.append(Type.getDescriptor(returnType));
//                            int invoke;
//                            if (isInterface)
//                                invoke = INVOKEINTERFACE;
//                            else if (Modifier.isStatic(methods.get(i).getModifiers()))
//                                invoke = INVOKESTATIC;
//                            else
//                                invoke = INVOKEVIRTUAL;
//                            mv.visitMethodInsn(invoke, classNameInternal, methodNames[i], buffer.toString());
//
//                            switch (Type.getType(returnType).getSort()) {
//                                case Type.VOID:
//                                    mv.visitInsn(ACONST_NULL);
//                                    break;
//                                case Type.BOOLEAN:
//                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
//                                    break;
//                                case Type.BYTE:
//                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
//                                    break;
//                                case Type.CHAR:
//                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
//                                    break;
//                                case Type.SHORT:
//                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
//                                    break;
//                                case Type.INT:
//                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//                                    break;
//                                case Type.FLOAT:
//                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
//                                    break;
//                                case Type.LONG:
//                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
//                                    break;
//                                case Type.DOUBLE:
//                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
//                                    break;
//                            }
//
//                            mv.visitInsn(ARETURN);
//                        }
//
//                        mv.visitLabel(defaultLabel);
//                        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//                    }
//                    mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
//                    mv.visitInsn(DUP);
//                    mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
//                    mv.visitInsn(DUP);
//                    mv.visitLdcInsn("Method not found: ");
//                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
//                    mv.visitVarInsn(ILOAD, 2);
//                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
//                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
//                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
//                    mv.visitInsn(ATHROW);
//                    mv.visitMaxs(0, 0);
//                    mv.visitEnd();
//                }
//                cw.visitEnd();
//                byte[] data = cw.toByteArray();
//                accessClass = loader.defineClass(accessClassName, data);
//            }
//        }
//        try {
//            MethodAccess access = (MethodAccess)accessClass.newInstance();
//            access.methodNames = methodNames;
//            access.parameterTypes = parameterTypes;
//            access.returnTypes = returnTypes;
//            return access;
//        } catch (Throwable t) {
//            throw new RuntimeException("Error constructing method access class: " + accessClassName, t);
//        }
//    }
}
