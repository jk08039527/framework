package com.jerry.baselib.util;

import java.util.List;

import org.greenrobot.greendao.converter.PropertyConverter;

import com.alibaba.fastjson.JSON;

public class StringConverter implements PropertyConverter<List<String>, String> {

    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        return JSON.parseArray(databaseValue, String.class);
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        return JSON.toJSONString(entityProperty);
    }
}