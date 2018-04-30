package com.po;

import lombok.Data;

/**
 * Created By Cx On 2018/4/26 17:23
 */
@Data
public class TextMessage extends BaseMessage {
    private String Content;
    private Long MsgId;
}
