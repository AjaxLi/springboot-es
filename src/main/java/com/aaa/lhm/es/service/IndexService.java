package com.aaa.lhm.es.service;

import com.aaa.lhm.es.mapper.IndexMapper;
import com.aaa.lhm.es.model.Index;
import com.aaa.lhm.es.status.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {
    @Autowired
    private IndexMapper indexMapper;


    /**
     *查询数据
     * @param
     * @return
     */
    public List<Index> selectAll(){

        List<Index> lists = indexMapper.selectAll();
        if(lists != null) {
            //有数据
            return lists;
        }
        return  null;
    }
}
