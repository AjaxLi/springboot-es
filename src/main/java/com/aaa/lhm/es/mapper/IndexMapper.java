package com.aaa.lhm.es.mapper;

import com.aaa.lhm.es.model.Index;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IndexMapper {
    @Select("select id,username,password,age from index1")
    List<Index> selectAll();
}
