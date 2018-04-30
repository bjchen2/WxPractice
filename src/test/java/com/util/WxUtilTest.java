package com.util;

import java.io.IOException;

/**
 * Created By Cx On 2018/4/29 23:10
 */
public class WxUtilTest {
    private static final String NGROK_URL = "c683t4.natappfree.cc";

    public static void main(String[] args) throws IOException {
//        AccessToken accessToken = WxUtil.getAccessToken();
//        System.out.println(accessToken);
        //上传filePath是本地文件路径
//        System.out.println(WxUtil.upload("D:/MyEclipse 2016 CI/WorkSpace/weixin/src/main/webapp/image/4.jpg",accessToken.getAccessToken(),"thumb"));
        //添加或修改菜单
//        if (WxUtil.createMenu(accessToken.getAccessToken())==0){
//            System.out.println("创建菜单成功");
//        }
//        查询菜单的json格式
//        System.out.println(WxUtil.queryMenu(accessToken.getAccessToken()));
        //删除菜单
//        if (WxUtil.delMenu(accessToken.getAccessToken())==0){
//            System.out.println("删除菜单成功");
//        }
        System.out.println(WxUtil.translate("w        "));
    }
}
