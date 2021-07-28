package com.ljs.hadoop.springbootes.service;

import com.alibaba.fastjson.JSON;
import com.ljs.hadoop.springbootes.pojo.Content;
import com.ljs.hadoop.springbootes.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 * @author: Created By lujisen
 * @company China JiNan
 * @date: 2021-07-27 13:53
 * @version: v1.0
 * @description: com.ljs.hadoop.springbootes.service
 */
@Service
public class ContentService {

    @Autowired
    @Qualifier("restHighLevelClient")
    RestHighLevelClient client;

    public boolean parseContent(String keyWord) throws Exception {

        List<Content> contents = new HtmlParseUtil().parseJD(keyWord);
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(new IndexRequest("jd_good").id("100"+i).source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulkResponse.hasFailures();

    }

    /*精确查询*/
    public List<Map<String,Object>> searchPageHighlightBuilder(String keyword,int pageNum,int pageSize) throws IOException {


      /*  System.out.println("keyword"+keyword);
        System.out.println("pageNum"+pageNum);
        System.out.println("pageSize"+pageSize);*/
        /*条件搜索*/
        SearchRequest searchRequest = new SearchRequest("jd_good");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /*精确查询*/
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("title", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));
        /*分页*/
        searchSourceBuilder.from(pageNum);
        searchSourceBuilder.size(pageSize);
        /*高亮显示*/
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);
        //高亮的样式前后缀
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        /*返回结果*/
        /*List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            maps.add(hit.getSourceAsMap());
        }*/
        /*高亮结果替换*/
        ArrayList<Map<String,Object>> resultList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            //解析高亮的字段
            //获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            /* System.out.println("highlightFields=========="+highlightFields);*/
            HighlightField title = highlightFields.get("title");
            /*System.out.println("高亮字段=========="+title);*/
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的结果
            //将原来的字段替换为高亮字段即可
            if (title!=null){
                Text[] fragments = title.fragments();
                String newTitle = "";
                for (Text text : fragments) {
                    newTitle +=text;
                }
                sourceAsMap.put("title",newTitle);//替换掉原来的内容
            }
            resultList.add(sourceAsMap);
        }
        System.out.println(resultList);
        return  resultList;
    }
}
