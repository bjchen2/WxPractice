package com.util;

import com.enums.MessageEnum;
import com.menu.Button;
import com.menu.ClickButton;
import com.menu.Menu;
import com.menu.ViewButton;
import com.po.AccessToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created By Cx On 2018/4/29 22:26
 */
public class WxUtil {
    //UPLOAD_URL为临时素材上传地址，三天失效（也可上传永久）。
    //ACCESS_TOKEN_URL获取accessToken，有效时长2小时（不可永久，需更新，但不能每次使用都调用，因为一天只能调用2000次，且过度调用会
    // 导致服务不稳定，所以一般是保留在会话中，过期才更新）
    private static final String APPID = "微信公众号APPID";
    private static final String APPSECRET = "微信公众号 APPSECRET";
    private static final String BAIDU_APPID = "百度开放平台 APPID";
    private static final String BAIDU_SECRET = "百度开放平台 SECRET";
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    private static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
    private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    private static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
    private static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
    private static final String TRANSLATE_URL = "http://api.fanyi.baidu.com/api/trans/vip/translate?q=KEYWORDS&from=auto&to=LANGUAGE&appid=APPID&salt=SALT&sign=RESULT";

    /**
     * get请求
     */
    private static JSONObject doGetStr(String url){
        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpGet httpGet = new HttpGet(url);
        JSONObject object = null;
        try {
            HttpResponse response = builder.build().execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null){
                String result = EntityUtils.toString(entity,"UTF-8");
                object = new JSONObject(result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    /**
     * post请求
     */
    private static JSONObject doPostStr(String url, String outStr){
        //TODO 这里没弄懂为什么要传outStr
        JSONObject object = null;
        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(outStr,"UTF-8"));
        try {
            HttpResponse response = builder.build().execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null){
                String result = EntityUtils.toString(entity);
                object = new JSONObject(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 获取accessToken
     */
    public static AccessToken getAccessToken(){
        AccessToken accessToken = new AccessToken();
        String url = ACCESS_TOKEN_URL.replace("APPID",APPID).replace("APPSECRET",APPSECRET);
        JSONObject object = doGetStr(url);
        if (object !=null){
            try {
                accessToken.setAccessToken(object.getString("access_token"));
                accessToken.setExpiresIn(object.getInt("expires_in"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accessToken;
    }

    /**
     * 上传临时文件
     * 媒体文件在微信后台保存时间为3天，即3天后media_id失效
     */
    public static String upload(String filePath, String accessToken, String type) throws IOException {
        File file = new File(filePath);
        if (!file.exists()){
            throw new IOException("文件不存在");
        }
        String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE",type);

        URL urlObj = new URL(url);
        //连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        //设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        //设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");
        //获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        //输出表头
        out.write(head);
        //文件正文部分
        //把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();
        //结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

        out.write(foot);

        out.flush();
        out.close();

        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        String result = null;
        try {
            //定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (reader != null) {
                reader.close();
            }
        }
        JSONObject jsonObj = new JSONObject(result);
        //只有image类型是直接叫media_id其他类型都需要一个前缀，如缩略图（thumb）：thumb_media_id
        String typeName = "media_id";
        if(!"image".equals(type)){
            typeName = type + "_media_id";
        }
        return jsonObj.getString(typeName);
    }

    private static Menu initMenu(){
        Menu menu = new Menu();
        ClickButton button11 = new ClickButton();
        button11.setType(MessageEnum.CLICK.getType());
        button11.setName("菜单");
        button11.setKey("11");

        ViewButton button21 = new ViewButton();
        button21.setType(MessageEnum.VIEW.getType());
        button21.setName("百度一下");
        button21.setUrl("https://www.baidu.com/");

        ClickButton button32 = new ClickButton();
        button32.setKey("32");
        button32.setName("扫一扫");
        button32.setType(MessageEnum.SCAN_CODE.getType());

        ClickButton button33 = new ClickButton();
        button33.setKey("33");
        button33.setName("发送位置");
        button33.setType(MessageEnum.LOCATION.getType());

        Button button31 = new Button();
        button31.setName("其它");
        button31.setSub_button(new Button[]{button32,button33});

        menu.setButton(new Button[]{button11,button21,button31});
        return menu;
    }

    public static int createMenu(String token){
        int result = -1;
        JSONObject jsonObject = new JSONObject(initMenu());
        String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
        jsonObject = doPostStr(url,jsonObject.toString());
        if (jsonObject != null){
            result = jsonObject.getInt("errcode");
            if (result != 0) System.out.println("错误码: "+result+" 错误信息："+jsonObject.getString("errmsg"));
        }
        return result;
    }

    public static String queryMenu(String token){
        JSONObject jsonObject;
        String url = QUERY_MENU_URL.replace("ACCESS_TOKEN", token);
        jsonObject = doGetStr(url);
        return jsonObject.toString();
    }

    public static int delMenu(String token){
        int result = -1;
        JSONObject jsonObject;
        String url = DELETE_MENU_URL.replace("ACCESS_TOKEN", token);
        jsonObject = doGetStr(url);
        if (jsonObject != null){
            result = jsonObject.getInt("errcode");
            if (result != 0) System.out.println("错误码: "+result+" 错误信息："+jsonObject.getString("errmsg"));
        }
        return result;
    }

    public static String translate(String source) throws IOException{
        String url = TRANSLATE_URL, salt = String.valueOf(System.currentTimeMillis()), to;
        String result = MD5Util.md5(BAIDU_APPID+source+salt+BAIDU_SECRET);
        if (isChinese(source)) to ="en";
        else to = "zh";
        url = url.replace("KEYWORDS", URLEncoder.encode(source, "UTF-8")).
                replace("SALT",salt).replace("RESULT",result).replace("LANGUAGE",to)
                .replace("APPID",BAIDU_APPID);

        JSONObject jsonObject = doGetStr(url);
        System.out.println(jsonObject);
        StringBuffer dst = new StringBuffer();
        if (jsonObject !=null){
            JSONArray jsonArray = (JSONArray) jsonObject.get("trans_result");
            for (int i = 0;i<jsonArray.length();i++){
                jsonObject = (JSONObject)jsonArray.get(i);
                dst.append(jsonObject.getString("dst"));
            }
        }
        return dst.toString();
    }

    private static boolean isChinese(String s){
        for (char c:s.toCharArray()){
            if (c >= 0x4E00 &&  c <= 0x9FA5) return true;
        }
        return false;
    }
}
