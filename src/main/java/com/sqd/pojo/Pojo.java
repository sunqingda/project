package com.sqd.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pojo {

    private Long id;

    private Long _IDD;

    private String name;

    private List<Integer> ids;

}
