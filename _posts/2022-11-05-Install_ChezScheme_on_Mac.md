---
layout: post
title: 在Mac上安装ChezScheme时碰到的问题及解决方案
---
{{page.title}}
===============================

照理说，安装软件这活儿理应是傻瓜式，无需什么记录。不过，在Mac上安装ChezScheme时还真碰到了问题，而且也算是有价值的问题。首先，在VPS端(FreeBSD系统)用一条命令`pkg install chez-scheme`搞定；然后，在本地Mac根据BUILDING文件中指示进行安装，依次`./configure`、`sudo make install`。<br/>

<img src="/images/posts/2022-11-05/pkg_install_chez-scheme.png"> <br/>
<img src="/images/posts/2022-11-05/Install_ChezScheme_1.png"> <br/>
问题不大，可「暴力」解决之，而后重新执行命令`./configure`，<br/>
<img src="/images/posts/2022-11-05/Install_ChezScheme_2.png"> <br/>
接着，`sudo make install`，<br/>
<img src="/images/posts/2022-11-05/Install_ChezScheme_3.png"> <br/>
因Mac系统无**X11**，故有找不到头文件之错。解决方案也简单，找到`c/version.h`文件，做些**注释**! <br/>
<img src="/images/posts/2022-11-05/Install_ChezScheme_4.png"> <br/>
重新执行 `sudo make install`，成矣。<br/>
<img src="/images/posts/2022-11-05/Install_ChezScheme_5.png"> <br/>
