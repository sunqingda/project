package com.sqd.controller;

import com.sqd.annotation.MultiRequestBody;
import com.sqd.pojo.Pojo;
import com.sqd.query.Param;
import org.springframework.web.bind.annotation.*;

/**
 * @author sqd
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    /**
     * 单一对象非必填参数，根据属性解析对象
     */
    @RequestMapping(value = "/object", method = RequestMethod.POST)
    public String test1(@MultiRequestBody(required = false) Param param) {

        return param != null ? param.toString() : "param is null";
    }

    /**
     * 单一对象，填写 value，根据 value 或者参数名解析对象
     */
    @RequestMapping(value = "/object2", method = RequestMethod.POST)
    public String test2(@MultiRequestBody("param") Param param) {
        return param != null ? param.toString() : "param is null";
    }

    /**
     * 多个对象 @RequestBody 测试，返回 400
     */
    @RequestMapping(value = "/multiObject", method = RequestMethod.POST)
    public String test2(@RequestBody Param param1, @RequestBody Pojo param2) {
        return String.format("param1=%s\nparam2=%s", param1.toString(), param2);
    }

    /**
     * 单个基本数据类型，根据参数名解析对象
     */
    @RequestMapping(value = "/basicType", method = RequestMethod.POST)
    public String test3(@MultiRequestBody int param1) {
        return String.format("param1=%S", param1);
    }

    /**
     * 多个基本数据类型，根据参数名解析对象
     */
    @RequestMapping(value = "/multiBasicType", method = RequestMethod.POST)
    public String test4(@MultiRequestBody int param1, @MultiRequestBody boolean param2, @MultiRequestBody double param3) {
        return String.format("param1=%S\nparam2=%S\nparam3=%S", param1, param2, param3);
    }

    /**
     * 包装类，根据 value 解析对象
     */
    @RequestMapping(value = "/packType", method = RequestMethod.POST)
    public String test5(@MultiRequestBody("id") Integer param) {
        return param.toString();
    }

}
