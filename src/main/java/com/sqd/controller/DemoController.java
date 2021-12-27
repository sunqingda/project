package com.sqd.controller;

import com.sqd.annotation.MultiRequestBody;
import com.sqd.pojo.Pojo;
import com.sqd.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo")
public class DemoController {

    /**
     * @description 单一对象
     */
    @RequestMapping(value = "/object", method = RequestMethod.POST)
    public String test1(@MultiRequestBody(required = false) Param param) {
        return param.toString();
    }

    /**
     * @description 多对象
     */
    @RequestMapping(value = "/multiObject", method = RequestMethod.POST)
    public String test2(@MultiRequestBody("param1") Param param1, @MultiRequestBody(value = "param2", required = false) Pojo param2) {
        return String.format("param1=%s\nparam2=%s", param1.toString(), param2);
    }

    /**
     * @description 基本数据类型
     */
    @RequestMapping(value = "/basicType", method = RequestMethod.POST)
    public String test3(@MultiRequestBody int param1) {
        return String.format("param1=%S", param1);
    }

    /**
     * @description 多个基本数据类型
     */
    @RequestMapping(value = "/multiBasicType", method = RequestMethod.POST)
    public String test4(@MultiRequestBody int param1, @MultiRequestBody boolean param2, @MultiRequestBody double param3) {
        return String.format("param1=%S\nparam2=%S\nparam3=%S", param1, param2, param3);
    }

    /**
     * @description 包装类
     */
    @RequestMapping(value = "/packType", method = RequestMethod.POST)
    public String test5(@MultiRequestBody("id") Integer param) {
        return param.toString();
    }

    /**
     * @description 包装类
     */
    @RequestMapping(value = "/multiPackType", method = RequestMethod.POST)
    public String test6(@MultiRequestBody("id") Integer param) {
        return param.toString();
    }

}