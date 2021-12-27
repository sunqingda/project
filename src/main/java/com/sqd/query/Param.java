package com.sqd.query;

import com.sqd.pojo.Pojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Param {

    private Long id;

    private String name;

    private Pojo pojo;
}
