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

    private int basicInt;

    private short basicShort;

    private char basicChar;

    private long basicLong;

    private double basicDouble;

    private float basicFloat;

    private boolean basicBoolean;

    private Integer packInteger;

    private Boolean packBoolean;

    private String str;

    private Object obj;

    private List<Integer> integerList;


}
