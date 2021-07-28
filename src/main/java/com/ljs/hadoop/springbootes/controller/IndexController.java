package com.ljs.hadoop.springbootes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * @author: Created By lujisen
 * @company China JiNan
 * @date: 2021-07-27 10:17
 * @version: v1.0
 * @description: com.ljs.hadoop.springbootes.controller
 */
@Controller
public class IndexController {
    @GetMapping({"/","/index"})
    public  String index(){
        return  "index";
    }
}
