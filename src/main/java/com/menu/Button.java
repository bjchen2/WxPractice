package com.menu;

import lombok.Data;

/**
 * Created By Cx On 2018/4/30 15:59
 */
@Data
public class Button {
    private String type;
    private String name;
    private Button[] sub_button;
}
