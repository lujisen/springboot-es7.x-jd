package com.ljs.hadoop.springbootes.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Created By lujisen
 * @company China JiNan
 * @date: 2021-07-22 16:17
 * @version: v1.0
 * @description: com.ljs.hadoop.springbootes.config
 */
@Configuration
public class ElasticSearchClientConfig {
    @Bean
    public RestHighLevelClient  restHighLevelClient(){

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.0.111", 9200, "http"),
                        new HttpHost("192.168.0.112", 9200, "http"),
                        new HttpHost("192.168.0.113", 9200, "http")));
            return client;
    }

}
