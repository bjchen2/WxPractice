package com.servlet;

import com.enums.MessageEnum;
import com.util.CheckUtil;
import com.util.MessageUtil;
import com.util.WxUtil;
import org.dom4j.DocumentException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created By Cx On 2018/4/26 16:46
 */
@WebServlet("/wx")
public class WeixinServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");
        PrintWriter out = resp.getWriter();
        if(CheckUtil.checkSignature(signature,timestamp,nonce)){
            out.print(echostr);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            Map<String,String> m = MessageUtil.xmlToMap(req);
            String s = null, toUserName = m.get("ToUserName"), fromUserName = m.get("FromUserName"), type = m.get("MsgType");
            if (MessageEnum.TEXT.getType().equals(type)){
                //如果是文本
                String src = m.get("Content").trim();
               if (src.length()>=3&&("翻译:".equals(src.substring(0,3))|| "翻译：".equals(src.substring(0,3)))){
                    src = src.substring(3);
                    src.trim();
                    if (src.length() < 1) s = MessageUtil.userDefinedReplyXml(toUserName,fromUserName,"您翻译的内容为空");
                    else {
                        String result = WxUtil.translate(src);
                        s = MessageUtil.userDefinedReplyXml(toUserName,fromUserName,"翻译结果为："+result);
                    }
               }else {
                   try{
                       int i = Integer.valueOf(m.get("Content"));
                       s = MessageUtil.successReplyXml(toUserName,fromUserName,i);
                   }catch (NumberFormatException e){
                       s = MessageUtil.failedReplyXml(toUserName,fromUserName);
                   }
               }
            }else if (MessageEnum.EVENT.getType().equals(type)){
                //如果是事件
                String event = m.get("Event");
                if (MessageEnum.SUBSCRIBE.getType().equals(event)){
                    //如果是订阅事件
                    s = MessageUtil.helloReplyXml(toUserName,fromUserName);
                }else if (MessageEnum.MESSAGE_VIEW.getType().equals(event)){
                    s = MessageUtil.userDefinedReplyXml(toUserName,fromUserName,m.get("EventKey"));
                }else if (MessageEnum.SCAN_CODE.getType().equals(event)){
                    //扫码,VIEW和发送位置事件不会有回复，但能获取到信息，ScanResult，ScanType在ScanCodeInfo的子层中
                    s = MessageUtil.userDefinedReplyXml(toUserName,fromUserName,m.get("ScanResult"));
                }else if (MessageEnum.LOCATION.getType().equals(event)){
                    s = MessageUtil.userDefinedReplyXml(toUserName,fromUserName,m.get("Label"));
                }else if (MessageEnum.MESSAGE_CLICK.getType().equals(event)){
                    s = MessageUtil.menuReplyXml(toUserName,fromUserName);
                }
            }
            System.out.println(s);
            out.print(s);
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
}