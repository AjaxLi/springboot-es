package com.aaa.lhm.es.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * 具体实现了ES的配置
 * TransportClient:
 *  使用Java对ES进行增删改查的API
 *  cluster.name:固定的，配置了集群的名称
 *  node.name:固定的，配置了节点的名称
 *  client.transport.sniff:固定的，当ES集群中有新的节点进入，项目会自动把这个节点配置在properties属性中，不需要再进行手动操作
 *  客户端(java项目)请求连接ES(集群)的时候,在集群中又新增加了一个节点，客户端会自动发现该节点并且添加到Java项目中，不需要再进行手动配置
 *  thread_pool.search.size:固定的，相当于数据库中的连接池
 */
@SpringBootApplication
public class ESConfig {

    @Autowired
    private ESProperties esProperties;

    @Bean("transportClient")
    public TransportClient getTransportClient(){
        // 1.创建TransportClient对象(空对象)
        TransportClient transportClient = null;
        try {
            // 2.配置ES的集群信息
            Settings settings =Settings.builder().put("cluster.name",esProperties.getClusterName())
                    .put("node.name",esProperties.getNodeName())
                    .put("client.transport.sniff",true)
                    .put("thread_pool.search.size",esProperties.getPool()).build();
            // 3.对TransportClient进行初始化
            transportClient = new PreBuiltTransportClient(settings);
            // 4.配置ES的连接信息
           TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(esProperties.getIp()),esProperties.getPort());
            // 5.把ES的连接信息放入TransportClient对象中
            transportClient.addTransportAddress(transportAddress);
        }catch(UnknownHostException e) {
            e.printStackTrace();

        }
        // 6.返回TransportClient对象
        return transportClient;
    }
}
