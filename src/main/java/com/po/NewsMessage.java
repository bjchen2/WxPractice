package com.po;

import lombok.Data;

import java.util.List;

/**
 * Created By Cx On 2018/4/27 11:58
 */
@Data
public class NewsMessage extends BaseMessage {
    private int ArticleCount;
    private List<News> Articles;
}
