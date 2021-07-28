package com.ljs.hadoop.springbootes.controller;

import com.ljs.hadoop.springbootes.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: Created By lujisen
 * @company China JiNan
 * @date: 2021-07-27 14:13
 * @version: v1.0
 * @description: com.ljs.hadoop.springbootes.controller
 */
@RestController
public class ContentController {

    @Autowired
    ContentService contentService;

    @GetMapping("/parseContent/{keyword}")
    public boolean parseContent(@PathVariable("keyword") String keyword) throws Exception {
        return contentService.parseContent(keyword);
    }
    @GetMapping("/search/{keyword}/{pageNum}/{pageSize}")
    public List<Map<String, Object>> searchPage(@PathVariable("keyword") String keyword,
                                                @PathVariable("pageNum") int pageNum,
                                                @PathVariable("pageSize") int pageSize
                              ) throws Exception {
        return contentService.searchPageHighlightBuilder(keyword,pageNum,pageSize);
    }
}
