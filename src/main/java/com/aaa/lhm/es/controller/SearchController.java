package com.aaa.lhm.es.controller;

import com.aaa.lhm.es.service.IndexService;
import com.aaa.lhm.es.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @Autowired
    private IndexService indexService;
    /**
     * 创建索引
     * @param index
     * @return
     */
    @RequestMapping("/createIndex")
    public Map<String, Object> createIndex(String index) {

        return searchService.createIndex(index);
    }

    //TODO 完成删除索引

    /**
     * 删除索引
     * @param index
     * @return
     */
    @RequestMapping("/deleteIndex")
    public  Map<String, Object> deleteIndex(String index){
        return searchService.deleteIndex(index);
    }

    /**
     *向ES的索引库添加一条数据
     * @return
     * @throws JSONException
     */
    @RequestMapping("/addData")
    public Map<String, Object> addData() throws JSONException {
        return searchService.addData();
    }

    //TODO 完成从数据库中查询对象信息，然后转换为Map进行添加在ES的索引库中

    /**
     * 查询数据添加es
     */
    @RequestMapping("/selectAll")
  public Map<String,Object> selectId() throws JSONException {
        return searchService.addES(indexService);
  }


    // 1.通过id进行查询数据
    /**
     * 通过id进行查询数据
     **/
    @RequestMapping("/one")
    public Map<String, Object> selectOneById(String id) {

        return searchService.selectOneById(id);
    }
    // 2.查询所有的数据信息
    @RequestMapping("/all")
    public List<Map<String, Object>> selectAll() {

        return searchService.selectAll();
    }
    // 3.模糊查询
    @RequestMapping("/allLike")
    public List<Map<String, Object>> selectLikeAll() {

        return searchService.selectLikeAll();
    }

    //TODO 使用中文分词进行模糊查询！！！！

    // 3.中文模糊查询
    @RequestMapping("/like")
    public List<Map<String, Object>> selectLike() {

        return searchService.selectLike();
    }
}
