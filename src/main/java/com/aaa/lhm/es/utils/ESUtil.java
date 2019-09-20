package com.aaa.lhm.es.utils;

import com.aaa.lhm.es.status.StatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class ESUtil {
    @Autowired
    private TransportClient transportClient;

    private static Map<String,Object> resultMap = new HashMap<String,Object>();

    /**
     * 因为该工具类中所有的方法都是静态方法，静态方法只能调用静态变量
     *  所以使用@Autowired注解所注入进的对象静态方法不能直接调用，因为static修饰的方式不能使用普通变量
     *  下面的@PostConstruct注解就是来解决了以上的问题
     */
    private static TransportClient client;

    /**
     * @PostContruct是spring框架的注解 spring容器初始化的时候执行该方法
     */
    @PostConstruct
    public void init(){
        client = this.transportClient;
    }

    /**
     * 创建索引
     */
    public static Map<String, Object> createIndex(String index) {
        // isIndexExist:判断索引是否存在
        if (!isIndexExist(index)) {
            resultMap.put(StatusEnum.EXIST.getCodeName(), StatusEnum.EXIST.getCode());
            resultMap.put(StatusEnum.EXIST.getMsgName(), StatusEnum.EXIST.getMsg());
        }
        CreateIndexResponse indexresponse = client.admin().indices().prepareCreate(index).execute().actionGet();
        // indexresponse.isAcknowledged():创建索引是否成功，return Boolean类型(true:表示成功，false:失败)
        if(indexresponse.isAcknowledged()) {
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getCodeName(), StatusEnum.OPRATION_SUCCESS.getCode());
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getMsgName(), StatusEnum.OPRATION_SUCCESS.getMsg());
        } else {
            resultMap.put(StatusEnum.OPRATION_FAILED.getCodeName(), StatusEnum.OPRATION_FAILED.getCode());
            resultMap.put(StatusEnum.OPRATION_FAILED.getMsgName(), StatusEnum.OPRATION_FAILED.getMsg());
        }
        return resultMap;
    }

    /**
     * 判断索引是否存在
     * @param index
     * @return
     */

    public static boolean isIndexExist(String index) {
        IndicesExistsResponse inExistsResponse = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
        return inExistsResponse.isExists();
    }

    /**
     * 删除索引
     */
    public static Map<String, Object> deleteIndex(String index) {
        if (!isIndexExist(index)) {
            resultMap.put(StatusEnum.EXIST.getCodeName(), StatusEnum.EXIST.getCode());
            resultMap.put(StatusEnum.EXIST.getMsgName(), StatusEnum.EXIST.getMsg());
        }
        DeleteIndexResponse dResponse = client.admin().indices().prepareDelete(index).execute().actionGet();
        //删除索引是否成功
        if (dResponse.isAcknowledged()) {
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getCodeName(), StatusEnum.OPRATION_SUCCESS.getCode());
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getMsgName(), StatusEnum.OPRATION_SUCCESS.getMsg());
        } else {
            resultMap.put(StatusEnum.OPRATION_FAILED.getCodeName(), StatusEnum.OPRATION_FAILED.getCode());
            resultMap.put(StatusEnum.OPRATION_FAILED.getMsgName(), StatusEnum.OPRATION_FAILED.getMsg());
        }
        return resultMap;
    }

    /**
     * 判断index下指定type是否存在
     * @param index
     * @param type
     * @return
     */
    public boolean isTypeExist(String index, String type) {
        return isIndexExist(index)
                ? client.admin().indices().prepareTypesExists(index).setTypes(type).execute().actionGet().isExists()
                : false;
    }

    /**
     * 数据添加，正定ID
     *
     * @param mapObj 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public static Map<String, Object> addData(Map<String, Object> mapObj, String index, String type, String id) {
        IndexResponse response = client.prepareIndex(index, type, id).setSource(mapObj).get();
        // response.getId():就是添加数据后ES为这条数据所生成的id
        // 需要返回添加数据是否成功
        String status = response.status().toString();
        // 添加数据后所返回的状态(如果成功就是code:200-->OK)
        // eq:sacii --> 小写字母和大写字母不一样
        // status:-->OK
        // ok
        if("OK".equals(status.toUpperCase())) {
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getCodeName(), StatusEnum.OPRATION_SUCCESS.getCode());
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getMsgName(), StatusEnum.OPRATION_SUCCESS.getMsg());
        } else {
            resultMap.put(StatusEnum.OPRATION_FAILED.getCodeName(), StatusEnum.OPRATION_FAILED.getCode());
            resultMap.put(StatusEnum.OPRATION_FAILED.getMsgName(), StatusEnum.OPRATION_FAILED.getMsg());
        }
        return resultMap;
    }

    /**
     * 数据添加
     *
     * @param mapObj 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @return
     */
    public static Map<String, Object> addData(Map<String, Object> mapObj, String index, String type) {
        return addData(mapObj, index, type, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * 通过ID删除数据
     *
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     */
    public static Map<String, Object> deleteDataById(String index, String type, String id) {

        DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
        if("OK".equals(response.status().toString().toUpperCase())) {
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getCodeName(), StatusEnum.OPRATION_SUCCESS.getCode());
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getMsgName(), StatusEnum.OPRATION_SUCCESS.getMsg());
        } else {
            resultMap.put( StatusEnum.OPRATION_FAILED.getCodeName(), StatusEnum.OPRATION_FAILED.getCode());
            resultMap.put( StatusEnum.OPRATION_FAILED.getMsgName(), StatusEnum.OPRATION_FAILED.getMsg());
        }
        return resultMap;

    }

    /**
     * 通过ID 更新数据
     *
     * @param mapObj 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public static Map<String, Object> updateDataById(Map<String, Object> mapObj, String index, String type, String id) {

        UpdateRequest updateRequest = new UpdateRequest();

        updateRequest.index(index).type(type).id(id).doc(mapObj);

        ActionFuture<UpdateResponse> update = client.update(updateRequest);

        if("OK".equals(update.actionGet().status().toString().toUpperCase())) {
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getCodeName(), StatusEnum.OPRATION_SUCCESS.getCode());
            resultMap.put(StatusEnum.OPRATION_SUCCESS.getMsgName(), StatusEnum.OPRATION_SUCCESS.getMsg());
        } else {
            resultMap.put(StatusEnum.OPRATION_FAILED.getCodeName(), StatusEnum.OPRATION_FAILED.getCode());
            resultMap.put(StatusEnum.OPRATION_FAILED.getMsgName(), StatusEnum.OPRATION_FAILED.getMsg());
        }
        return resultMap;
    }

    /**
     * 通过ID获取数据
     *
     * @param index  索引，类似数据库
     * @param type   类型，类似表
     * @param id     数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    public static Map<String, Object> searchDataById(String index, String type, String id, String fields) {

        GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);

        if (StringUtils.isNotEmpty(fields)) {
            getRequestBuilder.setFetchSource(fields.split(","), null);
        }

        GetResponse getResponse = getRequestBuilder.execute().actionGet();

        return getResponse.getSource();
    }

    /**
     * 使用分词查询
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param query          查询条件
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param highlightField 高亮字段
     * @return
     */
    public static List<Map<String, Object>> searchListData(
            String index, String type, QueryBuilder query, Integer size,
            String fields, String sortField, String highlightField) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }

        if (StringUtils.isNotEmpty(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            // 设置高亮字段
            highlightBuilder.field(highlightField);
            searchRequestBuilder.highlighter(highlightBuilder);
        }

        searchRequestBuilder.setQuery(query);

        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(","), null);
        }
        searchRequestBuilder.setFetchSource(true);

        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }

        if (size != null && size > 0) {
            searchRequestBuilder.setSize(size);
        }

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        long totalHits = searchResponse.getHits().totalHits;
        long length = searchResponse.getHits().getHits().length;

        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            return setSearchResponse(searchResponse, highlightField);
        }
        return null;

    }


    /**
     * 高亮结果集 特殊处理
     *
     * @param searchResponse
     * @param highlightField
     */
    private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        StringBuffer stringBuffer = new StringBuffer();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());

            if (StringUtils.isNotEmpty(highlightField)) {

                System.out.println("遍历 高亮结果集，覆盖 正常结果集" + searchHit.getSourceAsMap());
                Text[] text = searchHit.getHighlightFields().get(highlightField).getFragments();

                if (text != null) {
                    for (Text str : text) {
                        stringBuffer.append(str.string());
                    }
                    //遍历 高亮结果集，覆盖 正常结果集
                    searchHit.getSourceAsMap().put(highlightField, stringBuffer.toString());
                }
            }
            sourceList.add(searchHit.getSourceAsMap());
        }
        return sourceList;
    }
}
