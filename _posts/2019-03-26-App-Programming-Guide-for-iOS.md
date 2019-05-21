---
layout: post
title: App Programming Guide for iOS
---
{{page.title}}
=========================

# 前言、iOS App结构
## 0.1 At a Glance
---
### 0.1.1 Apps Are Expected to Support Key Features
### 0.1.2 Apps Follow Well-Defined Execution Paths
### 0.1.3 Apps Must Run Efficiently in a Multitasking Environment
### 0.1.4 Communication Between Apps Follows Specific Pathways
### 0.1.5 性能优化对App非常重要
## 0.2 如何使用本文档
---

## 0.3 使用前提
---
## 0.4 See Also
---

# 一、期望的App行为
## 1.1 提供必需的资源
---
### 1.1.1 App Bundle
### 1.1.2 The Information Property List File
### 1.1.3 Declaring the Required Device Capabilities
### 1.1.4 App图标
### 1.1.5 App启动图片

## 1.2 支持用户隐私
---

## 1.3 对App实施国际化
---
<br/>
# 二、App生命周期
App是自定义代码与系统框架间一种复杂的相互作用。系统框架提供所有app需要运行的基础设施，你提供需要自定义设施的代码来给app你希望的外观及感觉。为高效完成此目的，理解一点iOS基础设施及工作原理是有帮助的。

iOS框架在它们的实现中依赖像model-view-controller以及delegation这样的设计模式。理解这些设计模式是app成功创建的关键。也有助于熟悉Objective-C语言及其特点。如果你是iOS编程的新手，读一读Start Developing iOS Apps Today作为对iOS应用及Objective-C语言的介绍。
## 2.1 Main函数
---
每个基于C的app入口点是main函数，当然iOS应用也概莫能外。不同的是，你不必亲自为iOS应用编写main函数。相反，Xcode创建此函数作为基本工程的一部分。List2-1显示了此函数的一个示例。少数例外的情况下，你绝对不应改变Xcode提供的main函数实现。

<img src="/images/posts/2019-03-26/List2-1.png">
关于main函数，唯一值得一提的是它的任务是将控制权交予UIKit框架。**UIApplicationMain**函数处理此过程，通过：创建app的核心对象，自可用的storyboard文件加载app的用户界面，调用开发者的自定义代码以便有机会做些初始化设置，以及使app的run loop开始运转。你必须提供的部分仅是storyboard文件和自定义的初始化代码。
## 2.2 App的结构
---
在启动期间，**UIApplicationMain**函数建立几个关键对象并开始app的运行。在每个app的心脏部分是**UIApplication**对象，它的工作是促进系统与app内其它对象的交互。Figure2-1显示了可在绝大多数app发现的对象，同时Table2-1列出了每个对象扮演的角色。需要注意的第一件事是iOS应用使用一个model-view-controller架构。这个模式将app的数据和业务逻辑与app的可视化展示分离开来。创建运行于有不同尺寸屏幕的不同设备上的app，此架构尤其重要。

<img src="/images/posts/2019-03-26/Figure2-1.png">
<img src="/images/posts/2019-03-26/Table2-1.png">
将一个iOS应用与其它应用加以区分的是它管理的数据(以及相应的业务逻辑)和它如何向用户呈现数据。与UIKit对象的绝大多数交互并不定义你的app但是能帮你改善它的行为。比如说，app委托(delegate)的方法让你清楚app正改变状态以便你的自定义代码恰当地响应。
## 2.3 Main Run Loop
---
App的**main run loop**处理所有与用户相关的事件。**UIApplication**对象在启动期建立main run loop，并用它处理事件和解决基于视图的用户界面更新。顾名思义，main run loop执行于应用的主线程。此行为确保了用户相关的事件以接收的顺序被连续地处理(are processed serially in the order which they were received)。

Figure2-2显示了main run loop的架构以及用户事件如何产生app采取的动作。当用户与设备交互时，系统产生与这些交互相关的事件，并经由UIKit建立的特殊port传递给app。应用程序在内部将事件排好队，然后一个接一个地发送给main run loop以执行。**UIApplication**对象是接收事件的第一个对象并决定下一步做什么。一个触摸事件通常发送给main窗口(window)对象，它再依次将其发送到事件发生的视图处。其它事件可能选取稍有不同的路径through various app objects。

<img src="/images/posts/2019-03-26/Figure2-2.png">
iOS app可以传递很多种事件。其最常见者列于Table2-2。其中，多种类型的事件利用app的main run loop传递，但是有些并不是。有些事件被发送给一个delegate对象或者被传递给你提供的一个block。关于如何处理绝大多数类型的事件(包括触摸、远程控制、motion、加速、以及陀螺仪事件)，见Event Handling Guide for iOS。

有些事件，比如触摸及远程控制，由app的第一响应者对象处理。响应者对象遍布于app内。(**UIApplication**对象，视图对象，以及视图控制器对象，均为响应者对象的示例)绝大多数事件以某个特定的响应者对象为目标，但是也可传递给其它响应者对象(经由响应者链) if needed to handle a event。比如，未处理一事件的视图可以传递此事件给它的父视图或者一个视图控制器。


与发生在其它类型视图的触摸事件相比，发生于control(比如button)的触摸事件被处理起来会有所不同。有限的可能与control的一些交互被重新打包进action消息，并被递送给一个恰当的目标对象。此target-action设计模式将利用control触发自定义代码的执行变得容易起来！
## 2.4 App的执行状态
---
在任意给定时刻，你的app处于Table2-3所列状态之一。系统移动你的app从此状态到彼状态以响应遍及系统的动作。比如，当用户按下Home按钮，一个电话进来了，或者某一个其它中断发生了，当前运行的app改变状态以做出响应。Figure2-3显示了app从一个状态移动到另一状态采取的路径。

<img src="/images/posts/2019-03-26/Figure2-3.png">
大部分状态切换伴随着一个对应用delegate对象方法的对应调用。这些方法是你以恰当方式响应状态改变的机会。这些方法均列在下面列出，并附带你可能如何使用它们的总结。
- **application:willFinishLaunchingWithOptions:** — 此方法是在启动期执行代码的第一次机会。
- **application:didFinishLaunchingWithOptions:** — 在app展示给用户前，此方法允许你执行任意最终的初始化。
- **applicationDidBecomeActive:** — 让你的app清楚它马上变成前台app。可用此方法作任意最后一分钟的准备。
- **applicationWillResignActive:** — 让你知道你的app将从前台app过渡走了。使用此方法将app放入一个不活动状态。
- **applicationDidEnterBackground:** — 让你知道你的app现在运行于后台并可能随时被挂起。
- **applicationWillEnterForeground:** — 让你知道你的app正从后台移动到前台，但是还尚未活跃。
- **applicationWillTerminate:** —— 让你知道你的app正在被终止。如果你的app被挂起了，此方法不会被调用。

<br/>
## 2.5 App终止
---
App必需随着准备终止的发生，并应当不等着保存数据或执行其它的关键任务。系统启动的终止是app生命周期的一个正常部分。系统经常终止app以便它能重新声明内存并为正被用户启动的其它app腾地方；但是系统可能也会终止这样的app，它行为不当或不能及时地响应事件。

当挂起的app被终止时，它们收不到通知；系统杀掉其进程并声明对应的内存。如果app当前运行于后台且未被挂起，系统会在终止之前调用它的app delegate的applicationWillTerminate:方法。当设备重启时，系统不调用此方法。

除了系统终止你的app，用户可以利用多任务UI显示地终止app。用户发起的终止动作与终止一个挂起态app有相同效果。app的进程被杀掉，并且没有通知被发送给app。
## 2.6 线程与并发
---
系统创建你的app主线程，并且你可以根据所需创建额外的线程以执行其它任务。对iOS应用而言，首选技术是利用Grand Central Dispatch(GCD)，operation对象以及其它的异步编程接口，而不是自己创建并管理线程。像GCD这样的技术允许你定义你想完成的工作以及完成的顺序，但是允许系统决定如何最优地在可用CPUs上执行那些工作。让系统处理线程管理简化你必须编写的代码，使保证代码的正确性变得容易，并且提供更好的整体性能。

当考虑线程及并发性时，仔细斟酌如下几点：
- 涉及视图、Core Animation以及很多其它UIKit类的工作必须发生在app的主线程。也有规律之外的情况—— 比如，基于图像的操作可通常发生于后台线程—— 但是抱有疑虑时，假设其工作需要发生在主线程。
- 冗长的任务应当执行于后台线程。牵涉到网络访问、文件访问、或者大量数据处理的任意任务全部应当(用GCD或operation对象)异步地执行。
- 在启动期，尽可能将任务移出主线程。在启动期，你的app应利用有效时间尽可能快地建立用户界面。只有那些对建立用户界面有贡献的任务才应被执行在主线程。所有其它的任务应当被异步地执行，with the results displayed to the user as soon as they are ready.

关于利用GCD和operation对象执行任务的更多信息，见Concurrency Programming Guide。
# 三、后台执行
## 3.1 执行有限长度的任务
## 3.2 在后台下载内容
## 3.3 实现长时间运行的任务
### 3.3.1 声明App支持的后台任务
### 3.3.2 追踪用户的位置
### 3.3.3 播放及录制后台的音频
### 3.3.4 实现一个VoIP App
### 3.3.5 Fetching Small Amounts of Content Opportunistically
### 3.3.6 使用Push通知初始化一项下载
### 3.3.7 在后台下载Newsstand内容
### 3.3.8 与外接设备通信
### 3.3.9 与一台蓝牙设备通信

## 3.4 Getting the User's Attention While in the Background
## 3.5 理解App合适投入后台
## 3.6 做一个有担当的后台App
## 3.7 退出后台执行

# 四、处理App状态转换的策略
## 4.1 在启动期做什么
### 4.1.1 启动周期
### 4.1.2 在横屏模式启动
### 4.1.3 于首次启动时安装App特定的数据文件

## 4.2 当App被暂时打断时做什么
### 4.2.1 响应临时中断

## 4.3 当App进入前台时做什么
### 4.3.1 准备处理排队等候的通知
### 4.3.2 处理iCloud变化
### 4.3.3 处理Locale变化
### 4.3.4 处理App Setting变化

## 4.4 当App进入后台时做什么
### 4.4.1 后台转变周期
### 4.4.2 准备App快照
### 4.4.3 降低内存占用

# 五、实现特定App特点的策略
## 5.1 隐私策略
### 5.1.1 使用On-Disk加密技术保护数据
### 5.1.2 确认App的Unique用户
## 5.2 支持多版本iOS
## 5.3 保留App的可视外观 Across Launches
### 5.3.1 在App内使能状态保留与恢复
### 5.3.2 状态保留与恢复过程
### 5.3.3 当排除视图控制器组时发生什么
### 5.3.4 实现状态保留及恢复的清单
### 5.3.5 在App内使能状态保留与恢复
### 5.3.6 保留视图控制器的状态
### 5.3.7 保留视图的状态
### 5.3.8 保留App的高层次状态
### 5.3.9 保存及恢复状态信息的小窍门

## 5.4 开发一款VoIP App的窍门
### 5.4.1 为VoIP使用配置Socket
### 5.4.2 安装一个Keep-Alive Handler
### 5.4.3 配置App的音频会话
### 5.4.4 利用可达性接口提高用户体验

# 六、应用间通信
## 6.1 支持AirDrop
### 6.1.1 向其它App发送文件和数据
### 6.1.2 接收发向己方的文件和数据

## 6.2 使用URL方案与App通信
### 6.2.1 向其它App发送URL
### 6.2.2 实现定制的URL方案
### 6.2.3 当URL被打开时展示一个定制的启动图片


# 七、优化技巧
## 7.1 降低App的功耗

## 7.2 高效使用内存
### 7.2.1 监视低内存警告
### 7.2.2 减少App的内存占用
### 7.2.3 合理分配内存

## 7.3 调整网络代码
### 7.3.1 Tips for Efficient Networking
### 7.3.2 Using Wi-Fi
### 7.3.3 The Airplane Mode Alert

## 7.4 提高文件管理
## 7.5 Make App Backups More Efficient
### 7.5.1 App备份最佳实践
### 7.5.2 App更新期间的文件保存

## 7.6 将工作从主线程转移走
