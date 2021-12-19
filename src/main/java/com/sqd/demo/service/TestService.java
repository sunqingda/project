package com.sqd.demo.service;

import com.sqd.demo.common.EntityQuery;
import com.sqd.demo.common.Result;
import com.sqd.demo.Entity.Entity;

import java.util.List;

public interface TestService {

    /**
     * @description 查询
     * @param entityQuery
     * @return
     */
    Result<List<Entity>> queryEntityById(EntityQuery entityQuery);

}
