基于Java的聊天程序（含服务器）

# 特性

- 好友私密对话

- RSA-4096加密通信

- 消息漫游

- 美观易用的客户端程序

# 安装与使用

## 客户端

1. [下载地址](https://disk.pku.edu.cn:443/link/2E896D75C0A4AA11D67CE27617043C1F)
2. 解压后使用记事本打开state.conf，在第四行，第五行分别填写服务器的地址和端口号
3. Windows用户：可以运行lightChat.exe，弹出exe4j对话框后点击确定，即可启动
4. 其他平台用户：如果安装了JRE，且支持JavaFX，那么可以直接运行GUI.jar

## 服务器

### 环境配置

1. 安装[Java运行环境](https://www.java.com/zh_CN/)
2. 安装[MySQL Server](https://dev.mysql.com/downloads/mysql/)，记住设定的数据库密码。
3. 下载[服务器jar](https://github.com/LeoHLee/JavaChatRoom/blob/master/chatServer.jar)

### 启动

1. 双击运行chatServer.jar
2. 输入数据库密码
3. 若密码正确，将出现服务器主窗口，将窗体最上方的IP和端口（通常是13060）告诉您的用户即可
4. 若IP地址不能从因特网(Internet)访问，会给出警告，此时仅有相同局域网内的客户端可以连接。解决方法：使用[北大VPN](https://its.pku.edu.cn/service_1_vpn_client.jsp)或其他方法获取因特网IP后重启服务器，或者使用内网穿透工具如[NATAPP](https://natapp.cn/article/natapp_newbie)将本地端口映射到域名。

### 重置

1. 打开MySQL Command Line Client，输入密码
2. 输入以下指令

   ```mysql
   drop database account;
   ```
3. 重新启动服务器

# 配置Java项目

## 服务器

1. 将src/server文件夹作为项目的源代码目录
2. 将lib文件夹下的以下jar设置为外部库
   ```
   activation.jar
   javax.mail.jar
   mysql-connector-java-8.0.20.jar
   ```
3. 运行设置：主类为serverMain.Main

## 客户端
1. 将src/client文件夹作为项目的源代码目录
2. 下载界面显示用的Fxml和图片文件，[下载链接](https://disk.pku.edu.cn:443/link/D98ED05E9154D818F7B4B3462D6B9775)
3. 将Fxml文件夹放入client/View目录中；png_40文件夹放入client/com/uz/emojione/fx目录中；qq.mp3放至源代码同目录文件夹下
4. 将lib文件夹下的以下jar设置为外部库
   ```
   jl1.0.1.jar
   JavaFX目录下的所有内容
   ```
5. 运行设置：主类为View.Main
6. 其他：为了避免不必要的编译器配置，强烈建议使用[IDEA](https://www.jetbrains.com/idea/)编译器运行客户端项目
