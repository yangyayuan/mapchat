# MapChat

  
  APK下载
  [点我下载](/mapchat.apk)
  ![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/1522761370.png)

## 一 项目界面    


![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/chat.png)
![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/contacts.png)
![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/friend.jpg)
![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/listview.png)
![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/mapLocation.png)
![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/me.png)
![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/more.png)
![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/submit.png)
![image](https://github.com/yangyayuan/mapchat/blob/master/screenshots%26apk/web.png)


## 二 简介  

mapchat主要功能模块为

1. 即时通讯  

2. 地图实时定位  

3. 发布浏览信息  


##### 1.即时通讯

API 使用的是leancloud的即时通讯后端  

会话 支持单聊，群聊，以及发送文本，语音，图片形式的消息  

通讯录 支持查找好友，添加好友，通讯录，侧滑栏根据名字首字母快速定位联系人  

##### 2.地图实时定位

API 使用的是百度提供的地图API和和定位API  

地图  查询通讯录好友位置并显示在地图上  

定位  支持用户以及好友的位置实时定位，点击好友头像即可开启聊天  


##### 3.发布浏览信息

API 使用的是LeanStorage提供的后端存储服务  

发布信息 通过FloatingActionButton进入发布信息页面上传用户填写的信息并刷新  

浏览信息 列表使用tablayout+RecyclerView+cardview来显示信息  


## 三 开源库

volley       google开源的网络请求库  

butterknife  View注入框架  

jpinyin      汉字转拼音开源库  

baidulbs     地图定位功能  

leancloud    移动开发后端云  


## 四 技术点  

在实现项目过程中，学到的一些知识  

·MVP模式的特点以及使用  

·baidu地图API以及定位API的使用  

·代码重构要点  

###  遇到的问题  

·framework层的架构不熟悉，之后多研习android 源码。  

·设计模式不能够熟练应用，多思考。  

·android碎片化带来的问题使得程序不够健壮，需要后期优化。  

·android应用的性能问题。  

## 五 To Do List  

APP UI 绘制以及逻辑优化  

信息浏览页面tab标签实现可定制化（如今日头条，可以定制自己喜欢的信息流）  

优化定位算法，最小化定位耗电问题  

群聊详细信息页增加功能

