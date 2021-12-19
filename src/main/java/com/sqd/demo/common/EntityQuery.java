package com.sqd.demo.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityQuery extends BaseQuery{
    Integer id;
}
