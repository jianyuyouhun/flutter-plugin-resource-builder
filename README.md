# README #

## 原理

此插件会自动解析flutter项目中的pubspec.yaml文件中的`flutter/assets`。比如：

	flutter:
		assets:
			- lib/module/app/res/images/

然后插件会自动在当前文件夹同级目录下生成一个x-res.dart文件，自动关联资源文件生成静态变量。节省开发时间

## 安装步骤 ##

1. 拉取本项目到你的**WorkSpace**目录.

	git clone http://gitlab.gallops.cn/study/FlutterResourceBuilder.git

2. 打开Android Studio->setting->Plugins.

旧版点击
![](images/old_idea.png)

新版点击

![](images/new_idea.png)

选择你的**WorkSpace**/FlutterResourceBuilder/ResourceBuilder.zip

安装完成后重启Android Studio即可