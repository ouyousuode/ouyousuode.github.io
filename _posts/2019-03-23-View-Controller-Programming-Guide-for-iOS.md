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
一个视图控制器管理应用用户界面的一个分立部分。一经请求，它便提供可供显示与交互的视图。通常，此视图为一个复杂视图层次的根视图——按钮、开关、以及其它现存于iOS中的用户界面元素。视图控制器为视图层次充任中间代理人的角色，来处理视图与任意相关控制器或数据对象的信息交流。
### You Manage Your Content Using Content View Controllers
为呈现特定于你的app的内容，你需要实现你自己的内容视图控制器。通过继承UIViewCOntroller或UITableViewController类，你可以创建新的视图控制器类；实现一些呈现和控制内容的必需方法。
### Container View Controller Manage Other View Controllers
容器视图控制器显示一些其它视图控制器的内容。这些其它的视图控制器与此容器相互关联，由此便形成了父-子关系。容器与内容视图控制器的组合创建一个视图控制器对象的层级，它们有一个单一的根视图控制器。

每种类型的容器定义它自己管理儿子的一套接口。容器的方法有时定义儿子间的明确的导航关系。一个容器也可以设置一些明确的控制器类型规定，看那些控制器可当它的儿子。它也可能期望它的儿子提供一些其它的内容来配置容器。
iOS提供很多内置的容器视图控制器类型，你可以用它们来组织应用的用户界面。
### Presenting a View Controller Temporarily Brings Its View Onscreen
有时，某视图控制器想显示一些额外的信息给用户。又或者，它想让用户提供一些额外的信息或者执行一个任务。iOS设备的屏幕空间是有限的；设备可能没有足够的空间同时显示用户的所有界面元素。那么，一个iOS app可以临时提供其它视图给用户以进行交互。待用户完成了请求的动作，此视图便完成任务了。

为简化实现此类界面所需的努力，iOS允许视图控制器呈现其它视图控制器的内容。当被呈现时，此新视图控制器的视图显示在屏幕的某部分中，通常是整个屏幕。稍后，当用户完成了任务，被显示的控制器告诉显示它的显示器任务已完成。呈现控制器(presenting view controller)解除它刚呈现的新控制器，恢复屏幕到原始状态。

呈现行为必须包含在视图控制器到设计中，以便它可被其它控制器呈现。
### Storyboards Link User Interface Elements into an App Interface
用户界面的设计可能是非常复杂的。每个视图控制器要引用多个视图、手势识别器以及其它的界面对象。反过来，这些对象也维持着对控制器的引用，执行某块代码来响应用户执行的动作。而且控制器较少孤立行动。多个控制器的合作也定义了其它的关联。简言之，创建用户界面意味着实例化且配置很多对象、建立这些对象间的管理，这个过程是耗时且易出错的。

相反，用Interface Builder来创建**storyboards**。一个storyboard拥有视图控制器的预配置实例以及它们的相关对象。每个对象的属性均可在Interface Builder配置，当然它们之间的关联也可这么办。

在运行时，app加载storyboards并用它们驱动app的界面。当对象加载自storyboard时，它们便恢复到在storyboard配置好的状态。UIKit也提供一些可重写的方法来定制行为；这些行为是不能直接在Interface Builder配置的。

通过使用storyboard，你可以容易地看清app用户界面中的对象是如何配合的。也可以用更少的代码来创建和配置app用户界面的对象。
# View Controller Basics













































