
### 该项目可以直接打包供其它项目使用，项目遵循最小依赖原则，引入依赖：

spring-boot-starter-web

lombok

### 该项目可去除 controller、pojo、query 后打包供其它项目引入使用

### @MultiRequestBody 注解支持的功能
-  1、支持通过注解的 value 指定 JSON 的 key 来解析对象
- 2、支持通过注解无 value，直接根据参数名来解析对象
- 3、支持基本类型的注入
- 4、支持通过注解无 value 且参数名不匹配 JSON 串的 key 时，根据属性解析对象
