# WxPractice
慕课网，微信公众号开发入门练习<br/>
慕课网微信公众号开发源码，大体写法和视频一样，部分地方以个人理解做了适当更改，参考资料：<br/>
    https://www.imooc.com/learn/368<br/>
    https://www.imooc.com/learn/401<br/>
    微信公众平台<br/>

代码写于：2018-05-01<br/>
  JDK：JDK1.8版本，<br/>
  IDE：IDEA 2017.3.3，<br/>
  操作系统为：Windows10<br/>
  公网映射工具（如果有服务器和域名/应用引擎请忽略）：natapp（免费版映射网址会经常发生变化，不能接受的可以花10元/月购买一个隧道）<br/>


**若要正常使用，需更改：**<br/>

  微信公众平台：公众号URL为：域名/wx（如：http://c683t4.natappfree.cc/wx （/wx是我定义的servlet访问地址），eclipse中好像需要加上项目名:http://c683t4.natappfree.cc/weixin/wx ）<br/>
  <br/>
  CheckUtil中：token变量(我设置的是：cccxd，若不更改，则需更改微信公众平台的token，反正二者必须一致)<br/>
  <br/>
  MessageUtil中：NGROK_URL变量（使用公网映射工具获得的域名，或者自己的域名）；THUMB_MEDIA_ID和IMAGE_MEDIA_ID变量（在WxUtilTest中上传图片时返回的mediaId，若已上传，可自行使用微信API查询mediaId）<br/>
  <br/>
  WxUtil中：APPID（微信的）/APPSECRET（微信的）/BAIDU_APPID/BAIDU_SECRET变量；使用前，先在WxUtilTest方法中调用createMenu方法，不然没有菜单<br/>


现在百度不提供百度词典API了，并且**翻译API已更新，视频中的使用方式已过时（不可使用）**，建议直接查看官方文档或阅读我的源码<br/>
  百度翻译API申请访问地址：http://api.fanyi.baidu.com/api/trans/product/index<br/>
  百度翻译API文档：http://api.fanyi.baidu.com/api/trans/product/apidoc<br/>

视频中有些地方，因为各种JDK、微信平台的更新所产生的坑，代码中已更改，如：直接用一级节点解析XML无法获取Label消息，翻译API不可用等。<br/>

虽然知道有些更改对新手很不友好（比如lombok/maven）,但这样更贴近实战，学到更多。（其实是本人太懒，这些工具太好用）建议大家都能了解一下。
如：<br/>
  用maven构造项目，导包简单<br/>
  为降低代码冗余，所有getset方法均用lombok包的Data注解，具体使用方法可自行百度<br/>
  为了代码的健壮性，易于修改，部分方法重构，如：将事件变量单独提取出来，作为一个enum类；MessageUtil中将reply和init等方法分离并归类<br/>
  因没有自己的服务器和域名，URL的公网映射使用的是natapp，可自行百度了解<br/>

若不愿使用lombok包可将源码中所有bean的@Data注解去掉，加上get/set方法，不想使用maven可删除pom.xml自行导入所需jar包（在此不再提供）<br/>

若学有余力：建议观看慕课：通过自动回复机器人学Mybatis：<br/>
  https://www.imooc.com/learn/154    <br/>
  https://www.imooc.com/learn/260<br/>
集成mybatis与数据库，做一个更强大的自动回复功能！！！<br/>
