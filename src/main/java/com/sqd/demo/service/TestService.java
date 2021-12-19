package com.sqd.demo.service;

import com.sqd.demo.common.EntityQuery;
import com.sqd.demo.common.Result;
import com.sqd.demo.dao.Entity;

import java.util.List;

public interface TestService {

    Result<List<Entity>> queryEntityList(EntityQuery entityQuery);

}
