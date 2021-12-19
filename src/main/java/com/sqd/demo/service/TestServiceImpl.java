package com.sqd.demo.service;

import com.sqd.demo.common.EntityQuery;
import com.sqd.demo.common.Result;
import com.sqd.demo.dao.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class TestServiceImpl implements TestService {

    private static final Logger log = LoggerFactory.getLogger(TestServiceImpl.class);

    private static List<Entity> entityList;

    static {
        entityList = Arrays.asList(
                new Entity(1, "entity1"),
                new Entity(2, "entity2"),
                new Entity(3, "entity3")
        );
    }

    @Override
    public Result<List<Entity>> queryEntityList(EntityQuery entityQuery) {
        if (!CollectionUtils.isEmpty(entityList)) {
            List<Entity> entityResult = entityList.stream().filter(x -> x.getId() == entityQuery.getId()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(entityResult)) {
                log.info("查询成功，查询条件{}查询结果{}", entityQuery, entityResult);
                return Result.success(entityResult);
            }
            return Result.success(Collections.emptyList());
        }
        log.error("查询失败，entity 列表为空，查询条件：{}", entityQuery);
        return Result.fail("ENTITY_LIST_IS_NULL", "entity 列表为空");
    }

}
