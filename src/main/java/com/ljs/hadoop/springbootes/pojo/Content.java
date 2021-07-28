package com.ljs.hadoop.springbootes.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * @author: Created By lujisen
 * @company China JiNan
 * @date: 2021-07-27 13:40
 * @version: v1.0
 * @description: com.ljs.hadoop.springbootes.pojo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Content {
    private String title;
    private String img;
    private String price;
    private String shop;
}
