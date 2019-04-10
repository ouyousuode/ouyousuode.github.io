---
layout: post
title: iOS开发之View Controller编程指南
---
{{page.title}}
==========================
<img src="/images/posts/2019-03-23/viewControllerProgrammingGuide_0.png">
<img src="/images/posts/2019-03-23/viewControllerProgrammingGuide_1.png">
<img src="/images/posts/2019-03-23/viewControllerProgrammingGuide_2.png">
<img src="/images/posts/2019-03-23/viewControllerProgrammingGuide_3.png">
<img src="/images/posts/2019-03-23/viewControllerProgrammingGuide_4.png">
<img src="/images/posts/2019-03-23/viewControllerProgrammingGuide_5.png">
# About View Controllers
视图控制器(view controller)是应用的数据与它的可视外观之间的重要连接。每当iOS应用显示用户界面时，所显示的内容均由一个视图控制器或者相互配合的一组视图控制器管理。因此，视图控制器提供构建app的基本框架。

iOS提供许多内置的视图控制器类以提供标准的用户界面(片),比如导航与标签栏。作为开发app工作的一部分，你也需要实现一个或多个定制的控制器来显示针对你的应用的内容。

<img src="/images/posts/2019-03-23/aboutViewController.png">
## At a Glance
视图控制器是Model-View-Controller(MVC)设计模式中的传统控制器对象，但是它们也负责一些其它工作。视图控制器提供很多针对所有iOS应用都通用的行为。通常情况下，这些行为都内置为基类(base class)。对有些行为，基类提供解决方案的部分内容；你的视图控制器子类实现定制的代码以提供剩余内容。比方说，当用户旋转设备时，标准实现便试图去旋转用户界面；你的子类决定此用户界面是否应当被旋转，以及如果旋转，视图配置应如何在新方向上改变。因此，当遵照平台的设计纲要时，一个结构化基类与指定子类的组合使得定制化app行为变得容易起来。
### A View Controller Manages a Set of Views

