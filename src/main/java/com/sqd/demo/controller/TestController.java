package com.sqd.demo.controller;

import com.sqd.demo.common.EntityQuery;
import com.sqd.demo.common.Result;
import com.sqd.demo.Entity.Entity;
import com.sqd.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    /**
     * @description 通过 id 查询
     * @param query
     * @return
     */
    @GetMapping("/query")
    public Result<List<Entity>>  queryEntityById(EntityQuery query){
        return testService.queryEntityById(query);
    }

}
