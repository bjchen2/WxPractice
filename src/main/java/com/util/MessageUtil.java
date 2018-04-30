package com.util;

import com.enums.MessageEnum;
import com.po.*;
import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created By Cx On 2018/4/26 17:13
 */
public class MessageUtil {
    //因为是使用ngrok映射，所以导致服务器公网地址不稳定，需要经常更改，若有域名，则可更改为自己的域名
    private static final String NGROK_URL = "c683t4.natappfree.cc";
    private static final String FAILED_REPLY = "您说的话我怎么听不懂啊！！！\n点击菜单按钮可查询所有功能哦~";
    //回复5时，公众号回复的图片mediaId（在WxUtilTest中调用上传方法上传图片时会返回）
    private static final String IMAGE_MEDIA_ID = "W6_HWi16l9SeKCwHAD71eQfIoYX8sSYcWYN8CMr3IYkyoKr4SIjEsImfOKW2T7ct";
    //回复6时，公众号回复的音乐缩略图的mediaId，获取方法同上
    private static final String THUMB_MEDIA_ID = "4Jp9nUtLbAHz_dUlv42L2PEqfaG0wyQUrY7OGVI1yNyXl-huu8v-BGzV0A3mmrX0";
    /**
     * 将element e的所有子节点都解析并存储进m
     */
    private static void elementToMap(Element e,Map<String,String> m){
        if (e.isTextOnly()) m.put(e.getName(),e.getText());
        else {
            Iterator it = e.elementIterator();
            while(it.hasNext()){
                Element ee = (Element)it.next();
                elementToMap(ee,m);
            }
        }
    }
    //xml转换成map集合
    public static Map<String,String> xmlToMap(HttpServletRequest req) throws IOException, DocumentException {
        Map<String,String> map = new HashMap<String,String>();
        SAXReader reader = new SAXReader();

        InputStream ins = req.getInputStream();
        Document doc = reader.read(ins);

        Element root = doc.getRootElement();
        List<Element> list = root.elements();

        for (Element e : list){
            elementToMap(e,map);
        }
        ins.close();
        return map;
    }

    private static String messageToXml(Object message){
        if(message == null) return null;
        XStream xStream = new XStream();
        xStream.alias("xml",message.getClass());
        if (message.getClass() == NewsMessage.class){
            xStream.alias("item",News.class);
        }
        return xStream.toXML(message);
    }

    /**
     * 合法请求回复
     */
    public static String successReplyXml(String toUserName, String fromUserName, int reply){
        String s;
        if (reply == 1 || reply == 2 || reply==7){
            s = messageToXml(initText(toUserName,fromUserName, (String) reply(reply)));
        }else if(reply == 3 || reply == 4){
            s = messageToXml(initNews(toUserName,fromUserName,(List<News>) reply(reply)));
        }else if (reply == 5){
            s = messageToXml(initImage(toUserName,fromUserName,(ImageMessage)reply(reply)));
        }else if(reply==6){
            s = messageToXml(initMusic(toUserName,fromUserName,(MusicMessage)reply(reply)));
        }
        else{
            s = failedReplyXml(toUserName,fromUserName);
        }
        return s;
    }

    /**
     * 非法请求回复
     */
    public static String menuReplyXml(String toUserName, String fromUserName){
        return messageToXml(initText(toUserName, fromUserName,menuText()));
    }

    /**
     * 关注公众号回复
     */
    public static String helloReplyXml(String toUserName, String fromUserName){
        return messageToXml(initText(toUserName, fromUserName,helloText()));
    }

    public static String failedReplyXml(String toUserName, String fromUserName){
        return messageToXml(initText(toUserName, fromUserName,FAILED_REPLY));
    }

    /**
     * 自定义回复
     */
    public static String userDefinedReplyXml(String toUserName, String fromUserName, String content){
        return messageToXml(initText(toUserName,fromUserName,content));
    }


    private static TextMessage initText(String toUserName, String fromUserName, String content){
        TextMessage textMessage = new TextMessage();
        textMessage.setMsgType(MessageEnum.TEXT.getType());
        textMessage.setToUserName(fromUserName);
        textMessage.setFromUserName(toUserName);
        textMessage.setCreateTime(new Date().getTime());
        textMessage.setContent(content);
        return textMessage;
    }

    private static NewsMessage initNews(String toUserName, String fromUserName, List<News> articles){
        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setMsgType(MessageEnum.NEWS.getType());
        newsMessage.setToUserName(fromUserName);
        newsMessage.setFromUserName(toUserName);
        newsMessage.setCreateTime(new Date().getTime());
        newsMessage.setArticles(articles);
        newsMessage.setArticleCount(articles.size());
        return newsMessage;
    }

    private static ImageMessage initImage(String toUserName, String fromUserName, ImageMessage imageMessage){
        imageMessage.setMsgType(MessageEnum.IMAGE.getType());
        imageMessage.setToUserName(fromUserName);
        imageMessage.setFromUserName(toUserName);
        imageMessage.setCreateTime(new Date().getTime());
        return imageMessage;
    }

    private static MusicMessage initMusic(String toUserName, String fromUserName, MusicMessage musicMessage){
        musicMessage.setMsgType(MessageEnum.MUSIC.getType());
        musicMessage.setToUserName(fromUserName);
        musicMessage.setFromUserName(toUserName);
        musicMessage.setCreateTime(new Date().getTime());
        return musicMessage;
    }


    /**
     * 主菜单
     * @return
     */
    private static String menuText(){
        StringBuffer sb = new StringBuffer();
        sb.append("菜单！\n\n");
        sb.append("回复1、关于本公众号的介绍\n");
        sb.append("回复2、关于公众号开发\n");
        sb.append("回复3、单图文介绍\n");
        sb.append("回复4、多图文介绍\n");
        sb.append("回复5、查看彩蛋\n");
        sb.append("回复6、听音乐\n");
        sb.append("回复7、查看翻译功能使用说明\n");
        sb.append("回复    翻译：你要翻译的句子    即可执行翻译功能\n");
        return sb.toString();
    }

    private static String helloText(){
        StringBuffer sb = new StringBuffer();
        sb.append("hello，欢迎关注，本公众号有如下功能！！\n\n");
        sb.append("回复1、公众号介绍\n");
        sb.append("回复2、公众号开发介绍\n");
        sb.append("回复3、单图文介绍\n");
        sb.append("回复4、多图文介绍\n");
        sb.append("回复5、查看彩蛋\n");
        sb.append("回复6、听音乐\n");
        sb.append("回复7、查看翻译功能使用说明\n");
        sb.append("回复    翻译：你要翻译的句子    即可执行翻译功能\n");
        return sb.toString();
    }

    /**
     * 消息回复
     * @return
     */
    public static Object reply(int i){
        List<News> articles = new ArrayList<News>();
        if (i==1){
            return "本公众号用于了解微信公众号后端接入开发。" +
                    "很高兴你能关注，今后我慢慢的添加新东西，因为不是微信后台操作，所以会比较慢。";
        }
        else if(i==2){
            return "对于想有个自己的公众号的朋友们，其实申请很简单，自行百度或观看慕课教程：https://www.imooc.com/learn/368。";
        }else if(i==3){
            News news = new News();
            news.setTitle("吃饭");
            news.setDescription("吃饭就要吃肉");
            news.setPicUrl("http://"+NGROK_URL+"/image/1.jpg");
            news.setUrl("https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&tn=02049043_69_pg&wd=%E5%A5%BD%E5%90%83%E7%9A%84&oq=%25E5%2590%2583%25E9%25A5%25AD&rsv_pq=89fc0d9600016f51&rsv_t=78f2NoioMxn0LVrkiRS%2Bw4st2qgqRjvE1Hm%2FO0olo5v8es%2FoMpYyqqT5eLuaeq6POFhoF9A&rqlang=cn&rsv_enter=1&inputT=1801&rsv_sug3=165&rsv_sug1=111&rsv_sug7=100&rsv_sug2=0&rsv_sug4=1801");
            articles.add(news);
            return articles;
        }else if (i==4){
            News news = new News();
            news.setTitle("吃饭");
            news.setDescription("吃饭就要吃肉");
            news.setPicUrl("http://"+NGROK_URL+"/image/1.jpg");
            news.setUrl("https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&tn=02049043_69_pg&wd=%E5%A5%BD%E5%90%83%E7%9A%84&oq=%25E5%2590%2583%25E9%25A5%25AD&rsv_pq=89fc0d9600016f51&rsv_t=78f2NoioMxn0LVrkiRS%2Bw4st2qgqRjvE1Hm%2FO0olo5v8es%2FoMpYyqqT5eLuaeq6POFhoF9A&rqlang=cn&rsv_enter=1&inputT=1801&rsv_sug3=165&rsv_sug1=111&rsv_sug7=100&rsv_sug2=0&rsv_sug4=1801");
            articles.add(news);

            news = new News();
            news.setTitle("睡觉");
            news.setDescription("晚上要睡觉");
            news.setPicUrl("http://"+NGROK_URL+"/image/2.jpg");
            news.setUrl("https://www.imooc.com/learn/401");
            articles.add(news);
            return articles;
        }else if (i==5){
            Image image = new Image();
            image.setMediaId(IMAGE_MEDIA_ID);

            ImageMessage imageMessage = new ImageMessage();
            imageMessage.setImage(image);
            return imageMessage;
        }else if (i==6){
            //虽然傻逼开发文档写了Music中的参数非必须，但参数不全无法访问，手机端缩略图不可见而且要跳转页面播放，很傻逼的功能
            Music music = new Music();
            music.setTitle("Pretty Boy");
            music.setDescription("My memory");
            music.setMusicUrl("http://"+NGROK_URL+"/resource/PrettyBoy.mp3");
            music.setThumbMediaId(THUMB_MEDIA_ID);
            music.setHQMusicUrl("http://"+NGROK_URL+"/resource/PrettyBoy.mp3");

            MusicMessage musicMessage = new MusicMessage();
            musicMessage.setMusic(music);
            return musicMessage;
        }else if (i == 7){
            return "输入：翻译：hello\n回复：翻译结果为：你好\n";
        }
        else return menuText();
    }
}
