---
layout: post
title: Mac App Programming Guide
---
{{page.title}}
==================================
# 关于OS X App设计
本文档是学习如何创建Mac应用的起点。它包含有关OS X环境的基本信息，以及你的应用如何与环境交互。它也包含关于Mac应用架构的重要信息，和设计app关键部分的一些技巧。

<img src="/images/posts/2018-11-11/MacAppProgrammingGuide_0.png">
## At a Glance
---
Cocoa是解锁OS X全部力量的应用环境。Cocoa提供APIs、库以及运行时(runtimes)，此三者可帮你创建快速、令人惊叹的应用；这些应用自动继承OS X的漂亮外观及feel。

### Cocoa帮你创建OS X应用
开发者使用Cocoa为OS X编写应用，Cocoa为你的程序提供大量的基础设施。贯穿Cocoa的基本设计模式可使你的app与子系统框架无缝衔接，核心的应用对象提供关键行为以支持简单性及应用架构的可扩展性。Cocoa环境的关键部分专门设计成支持易用性；易用性是促使Mac app成功的最重要的一方面。通过消除在设备间同步数据的需要，很多应用应采纳iCould以提供一个更连贯的用户体验。
### Common Behaviors Make Apps Complete
在创建app的设计阶段，你需要考虑如何实现用户期望的某些特征。将这些特征集成进你的app架构可对用户体验留有影响：可访问性，偏好性，Spotlight，服务，分辨率无关性，快速用户切换以及Dock。使你的app采用全屏幕模式，占据整块屏幕，向用户提供一个更逼真的观影体验，也促使他们关注自己的内容而无暇他顾。
### 正确理解：满足系统及App Store的要求
恰当地配置app是开发过程的重要部分。Mac apps使用一个称为bundle的结构化目录管理他们的代码及资源文件。虽然大部分文件是定制的，且为支持app而存在；但是，有些却为系统或App Store所需，因此，必须恰当配置。应用bundle也包含一些你提供的用于国际化app以支持多种语言的资源。
### Finish Your App with Performance Tuning
随着你开发应用及工程代码稳定后，可以着手性能优化了。当然，你想你的app尽可能快速地启动及响应用户的命令。一个反应灵敏的app可容易地纳入进用户的工作流中，并且给人一种制作精良的印象。可通过加速启动及减小代码体积来提高app的性能。可参考的资料有：
- WWDC 2013 224 Designing Code for Performance
- WWDC 2013 408 Optimizing Your Code Using LLVM
- WWDC 2015 230 Performance on iOS and watchOS
- WWDC 2015 718 Building Responsive and Efficient Apps with GCD
- WWDC 2016 406 Optimizing app Startup Time
- WWDC 2016 719 Optimizing io for Performance and Battery Life
- WWDC 2017 706 Modernizing Grand Central Dispatch Usage

...

## 如何使用本文档
---
本指南向你介绍在写App时遇到的最重要的技术。在此指南中，你将看到写一个app需要的方方面面。也就是说，本文向你展现你需要的所有片段，以及如何把他们拼装在一起。仍有本文未谈及的app设计的重要部分，比如用户界面设计。然而，本文包括提供细节的其它文档的链接。

除此之外，本指南强调了在OS X v10.7引入的特定技术，这些技术提供了一些与老旧版本相区别的必要能力；这些能力来自iOS平台最出色的功能，可赋予应用卓越的易用性。
## See Also
---
下面的文档提供了关于设计Mac应用的额外信息，也包括本指南中涉及话题的更多详情:
- 为浏览一篇展示如何创建一个Cocoa app的教程，参见Start Developing Mac Apps Today。
- 为寻求创建高效率app的用户界面设计，参考OS X Human Interface Guidelines。
- 为理解如何创建明确app ID，创建描述文件(provisioning files)，以及为应用使能正确的权限(entitlements)，如此你才能通过Mac App Store出售应用或使用iCloud存储，参考App Distribution Guide。
- 为OS X技术的综合查访，看Mac Technology Overview。
- 为理解如何实现一个基于文档的app，参见Document-Based App Programming Guide For Mac。

<br/>
# The Mac Application Environment
## An Environment Designed for Ease of Use
---
## 复杂的图形化环境
---
## Runtime环境的底层细节
---
### 基于Unxi
### 并发与线程化
### 文件系统
### 安全性

<br/>
# The Core App Design
## The App Style Determines the Core Architecture
---
### 所有Cocoa Apps的核心对象
### Additional Core Objects for Multiwindow Apps
### Integrating iCloud Support Into Your App
### Shoebox-Style Apps Should Not Use NSDocument

## Document-Based Apps Are Based on an NSDocument Subclass
---
### OS X内的文档
### The Document Architecture Provides Many Capbilities for Free

## App的生命周期
---
### main函数是App入口点
### App的Main Event Loop驱动交互
### App的自动及快速终止提高用户体验
## 在应用中支持关键的运行时行为
---
### 自动终止
### 快速终止
### 用户界面保留

## Apps Are Built Using Many Different Pieces
---
### 用户界面
### 事件处理
### 图形、绘制及打印
### 文本处理

## 实现应用菜单栏
---
### Xcode模板提供菜单栏
### 连接菜单项到代码或第一响应者对象

<br/>
# 实现全屏幕体验
## NSApplication中的全屏幕API
---
## NSWindow中的全屏幕API
---
## NSWindowDelegate协议中的全屏幕API
---

<br/>
# 支持常见的App行为
## You Can Prevent the Automatic Relauch of Your App
---
## Making Your App Accessible Enables Many Users
---
## 为定制化提供用户偏好设置
---
## Integrate Your App with Spotlight Search
---
## Use Services to Increase Your App's Usefulness
---
## 为高分辨率优化
---
### 考虑点，而非像素
### 为图形提供高分辨率版本
### Use High-Resolution-Savvy Image-Loading Methods
### 使用支持高分辨率的API

## Preparce for Fast User Switching
---
## 利用Dock
---

<br/>
# Build-Time配置细节
## 配置Xcode工程
---
## The Information Property List File
---
## OS X应用Bundle
---
## 国际化App
---

<br/>
# Tuning for Performance and Responsiveness
## 加速启动App
---
### 推迟初始化代码
### 简化Main Nib文件
### 最小化全局变量
### 在启动期最小化文件访问
## 不要阻塞主线程
---
## 减小代码的体积
---
### 编译器级别的优化
### 使用Core Data处理大数据集合
### 清除内存泄漏
### Dead Strip Your Code
### Strip Symbol Information
