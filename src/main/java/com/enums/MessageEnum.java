package com.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型
 * Created By Cx On 2018/4/26 19:53
 */
@AllArgsConstructor
public enum MessageEnum {
    TEXT("text"),
    NEWS("news"),
    IMAGE("image"),
    MUSIC("music"),
    VOICE("voice"),
    VIDEO("video"),
    LINK ("link"),
    LOCATION("location_select"),
    SCAN_CODE("scancode_push"),
    EVENT("event"),
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
    MESSAGE_CLICK("CLICK"),
    MESSAGE_VIEW("VIEW"),
    CLICK("click"),
    VIEW("view");
    @Getter
    String type;
}
