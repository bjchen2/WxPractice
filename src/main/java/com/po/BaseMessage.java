package com.po;

import lombok.Data;

/**
 * Created By Cx On 2018/4/27 11:58
 */
@Data
public class BaseMessage {
    private String ToUserName;
    private String FromUserName;
    private Long CreateTime;
    private String MsgType;
}
