package com.sqd.controller;

import com.sqd.annotation.MultiRequestBody;
import com.sqd.pojo.Pojo;
import com.sqd.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Pojo test(@RequestBody Param param) {
        return Pojo.builder()
                .id(param.getId())
                .name(param.getName())
                .build();
    }

    @RequestMapping(value = "/test1", method = RequestMethod.POST)
    public Pojo test1(@MultiRequestBody("param") Param param) {
        return Pojo.builder()
                .id(param.getId())
                .name(param.getName())
                .build();
    }

}
