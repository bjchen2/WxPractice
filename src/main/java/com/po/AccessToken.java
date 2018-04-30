package com.po;

import lombok.Data;

/**
 * Created By Cx On 2018/4/29 23:02
 */
@Data
public class AccessToken {
    private String accessToken;
    private int expiresIn;
}
