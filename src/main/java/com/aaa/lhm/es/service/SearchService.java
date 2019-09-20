package com.aaa.lhm.es.service;

import com.aaa.lhm.es.model.Index;
import com.aaa.lhm.es.utils.ESUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    public static Map<String, Object> resultMap = new HashMap<String, Object>();

    /**
     * 创建index
     */
    public Map<String, Object> createIndex(String index) {

        return ESUtil.createIndex(index);
    }

    /**
     * 删除索引
     * @param index
     * @return
     */
    public Map<String,Object> deleteIndex(String index){
        return  ESUtil.deleteIndex(index);
    }

    /**
     * 向ES的索引库添加一条数据
     *    java.lang.IllegalArgumentException: The number of object passed must be even but was [1]:
     *     参数不合法异常！！！
     *      在ES的6.x版本或者之上，废弃了JSON对象传递数据，只能使用Map对象
     * @return
     * @throws JSONException
     */
    public Map<String, Object> addData() throws JSONException {
        // ES中不能再使用实体类，只能通过JSONObject对象进行传递数据来代替实体类
       /* JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 20L);
        jsonObject.put("username", "lisi");
        jsonObject.put("password", "123456");
        jsonObject.put("age", 30);*/
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("id", 15L);
        dataMap.put("username", "mazi");
        dataMap.put("password", "123456");
        dataMap.put("age", "20");
        return ESUtil.addData(dataMap, "index1", "type1", "11");
    }

    public Map<String, Object> addES(IndexService indexService) throws JSONException {

        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<Index> lists = indexService.selectAll();
        Map<String, Object> map = null;
        if(lists != null){
            for (Index es :lists) {
                dataMap.put("id",es.getId());
                dataMap.put("username", es.getUsername());
                dataMap.put("password", es.getPassword());
                dataMap.put("age", es.getAge());
                String s = es.getId().toString();
                map = ESUtil.addData(dataMap, "index1", "type1", s);
            }

        }

        return map;
    }

    /**
     *  通过id进行查询数据
     *      (id:是ES给这一条数据所上的索引)
     *      searchDataById:一共需要传递四个参数
     *      index, type, id, feilds
     *     feilds:传递所要查询的字段(username,age)
     *      select username,age from user where id = 1
     *      如果需要查询所有的字段直接传null
     * @param id
     * @return
     */
    public Map<String, Object> selectOneById(String id) {
        return ESUtil.searchDataById("index1", "type1", id, null);
    }

    /**
     * 查询所有的数据
     *      index
     *      type
     *      QueryBuilder:定义了查询条件(是全部查询，还是模糊查询，还是分页查询...)
     *      size:所查询的条数(10)
     *      fields:所查询的字段(username,age...，如果查询所有就填null)
     *      sortField:id,age...(根据字段进行排序)
     *      highlightField:把搜索关键字进行高亮显示(如果不需要传null)
     * @return
     */

    public List<Map<String, Object>> selectAll() {
        // 1.创建QueryBuilder对象(BoolQueryBuilder是QueryBuilder的实现类)
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 2.创建所要搜索的条件(查询所有数据)
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        // 3.把搜索的条件放入到BoolQueryBuilder中
        BoolQueryBuilder must = boolQueryBuilder.must(matchAllQueryBuilder);
        // 4.返回
        return ESUtil.searchListData("index1", "type1", must, 10, null, null, null);
    }


    /**
     * 模糊查询
     *    在ES中默认如果单词之间没有连接符就会被当成一个单词
     *     zhangsan就被当成了一个词
     *     如果需要进行模糊匹配在ES中必须要使用连接词(_, -, =, ,.....)
     *     因为ES的分词器做的有点low，特别是中文(必须要整合自己的分词器(IK),如果做的是职业搜索(用的最多的是搜狗))
     *     IK分词器集成很简单，不需要任何配置:
     *      IK分词器
     *      在ES的服务器上的plugins目录中创建IK文件夹(一定要大写)
     *      把IK分词器解压在IK目录中
     *      再次对es文件夹进行授权！
     * @return
     */
    public List<Map<String, Object>> selectLikeAll() {
        // 1.创建QueryBuilder对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 2.创建查询条件
        // matchPhraseQuery:有两个参数
        // name:字段的名字() select * from user where username like '%zhang%'
        // text:所需要模糊匹配的值(也就是数据库中like后面所匹配的值)
        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("username", "小");
        // 3.把查询条件放到BoolQueryBuilder对象中
        BoolQueryBuilder must = boolQueryBuilder.must(matchPhraseQueryBuilder);
        return ESUtil.searchListData("index1", "type1", must, 10, null, null, "username");
    }

    /**
     * 中文模糊查询
     * @return
     */
    public List<Map<String, Object>> selectLike() {
        // 1.创建QueryBuilder对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 2.创建查询条件
        // matchPhraseQuery:有两个参数
        // name:字段的名字() select * from user where username like '%明%'
        // text:所需要模糊匹配的值(也就是数据库中like后面所匹配的值)
        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("username", "明");
        // 3.把查询条件放到BoolQueryBuilder对象中
        BoolQueryBuilder must = boolQueryBuilder.must(matchPhraseQueryBuilder);
        return ESUtil.searchListData("index1", "type1", must, 10, null, null, null);
    }
}
