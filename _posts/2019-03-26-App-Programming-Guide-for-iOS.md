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

<img src="/images/posts/2019-03-26/Table2-2.png">
有些事件，比如触摸及远程控制，由app的第一响应者对象处理。响应者对象遍布于app内。(**UIApplication**对象，视图对象，以及视图控制器对象，均为响应者对象的示例)绝大多数事件以某个特定的响应者对象为目标，但是也可传递给其它响应者对象(经由响应者链) if needed to handle a event。比如，未处理一事件的视图可以传递此事件给它的父视图或者一个视图控制器。


与发生在其它类型视图的触摸事件相比，发生于control(比如button)的触摸事件被处理起来会有所不同。有限的可能与control的一些交互被重新打包进action消息，并被递送给一个恰当的目标对象。此target-action设计模式将利用control触发自定义代码的执行变得容易起来！
## 2.4 App的执行状态
---
在任意给定时刻，你的app处于Table2-3所列状态之一。系统移动你的app从此状态到彼状态以响应遍及系统的动作。比如，当用户按下Home按钮，一个电话进来了，或者某一个其它中断发生了，当前运行的app改变状态以做出响应。Figure2-3显示了app从一个状态移动到另一状态采取的路径。

<img src="/images/posts/2019-03-26/Table2-3.png">
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
当用户不那么想用你的app时，系统将其移动到后台状态。对很多应用而言，后台状态只是进入app挂起状态过程中的一个短暂停留。挂起应用是提高电池寿命的一种方式，它还允许系统将重要的系统资源分配给新的引起用户注意的前台应用。

大多数应用可以很容易地进入挂起状态，但是仍有一些让应用继续运行于后台的原因。一个徒步应用或许希望全程追踪用户的位置，以便它能显示覆盖在徒步地图上的行程。一个音乐应用或许需要在锁屏时继续播放音乐。其它应用或许希望在后台下载内容，以便它能以最短的延时向用户呈现内容。当你发现必须保持应用运行于后台时，iOS会高效地帮你完成，并且不会浪费系统资源或用户电量的。iOS提供的技术分为三类：
- 在前台启动的一个简单任务的应用，能当应用进入后台时请求时间来完成此任务。
- 在前台启动下载任务的应用，能将这些下载任务的控制权交予系统，如此在下载期间允许应用被挂起或终止。
- 需要运行于后台以支持特定类型任务的应用，可声明对一或多个后台执行模式的支持。

尽量避免执行任何的后台任务，除非此举可改善用户的综合体验。App进入后台的原因可能是用户启动了另一个应用、或用户锁定了设备且未在使用。在这两种情况下，用户均表明现在你的应用不必做任何有意义的事。在此前提下，继续运行只能浪费(消耗)设备的电量，并有可能导致用户强制退出此应用。因此，要当心你在后台进行的工作，并尽量避免这种情况。
## 3.1 执行有限长度的任务
---
进入后台的应用被期望能尽快地进入休眠(quiescent)状态，以便它们能被系统挂起。如果你的应用正在任务中并需要一点额外的时间来完成此任务，它可以调用**UIApplication**对象的beginBackgroundTaskWithName:expirationHanlder:或beginBackgroundTaskWithExpirationHandler:方法来请求一些额外的执行时间。调用这两种方法可以临时推迟应用的挂起，其结果给完成任务争取一点额外的时间。一旦工作结束，你的应用必须调用**endBackgroundTask:**方法来让系统知道任务已完成并可以被挂起了。

针对beginBackgroundTaskWithName:expirationHandler:或beginBackgroundTaskWithExpirationHandler方法的每次调用产生一个与相应任务关联的唯一标记(token)。当你的应用完成任务时，它必须随对应的标记调用**endBackgroundTask:**方法来让系统知道这个任务完成了。为后台任务调用**endBackgroundTask:**方法的失败会导致应用终止。如果开启任务时提供一个expiration handler，系统调用此处理器并给你最后一个结束任务以及避免应用终止的机会。

你不必等到app进入后台再来指定后台任务。一个更有用的设计是于任务开始之前调用beginBackgroundTaskWithName:expirationHandler:或beginBackgroundTaskWithExpirationHandler:方法，并且一旦完成任务即调用**endBackgroundTask:**方法。你甚至可以在应用尚在前台执行时遵循此模式。

List3-1展示了当应用切换到后台时如何开始一个长时间运行(long-running)的任务。在这个例子中，启动后台任务的请求包含一个expiration handler，以防止任务耗时过长。任务自身随即被提交给dispatch queue(调度队列)以异步执行，以便**applicationDidEnterBackground:**方法能够正常返回。block的使用简化了用于维持对重要变量引用所需的代码，比如后台任务标识符。bgTask变量是类的一个成员变量，存储到当前后台任务标识符的指针，并且被方法使用前已初始化。

<img src="/images/posts/2019-03-26/List3-1.png">
注意：当任务开始时，总是提供一个expiration handler，但如果你想知道app还有多少可运行的时间，请获取UIApplication对象的backgroundTimeRemaining属性值。

在你自己的expiration处理器中，你可以包含用于结束任务的额外代码。然而，任何包含的代码都不能耗费太长时间方能执行，因为等到你的expiration处理器被调用，应用已经非常接近它的限定时间了！为此，仅执行最少量的状态信息清理工作并结束任务。
## 3.2 在后台下载内容
---
当下载文件时，应用应当利用**NSURLSession**对象来启动下载，如此系统可控制下载过程以防app被挂起或终止。当为后台传输配置**NSURLSession**对象时，系统在一个独立的进程里管理这些传输并以常规方式向app报告状态。如果app于传输过程中被终止，系统会在后台继续这个传输，并在传输结束或者一及多个任务需要app的注意力时，以合适的方式启动此app。

为支持后台传输，你必须恰当地配置**NSURLSession**对象。为配置会话，你必须先创建一个**NSURLSessionConfiguration**对象并给几个属性设置恰当值。随即创建会话时，将此配置好的对象传递给**NSURLSession**恰当的初始化方法。

创建支持后台下载的配置对象的过程如下所示：
* 1 **利用NSURLSessionConfiguration**的backgroundSessionCongfigurationWithIdentifier:方法创建配置对象。
* 2 将配置对象的sessionSendsLaunchEvents属性设为YES。
* 3 如果app尚在前台时便启动传输，建议将配置对象的discretionary属性也设为YES。
* 4 恰当地配置configuration对象的任意其它属性。
* 5 利用此配置对象创建你的**NSURLSession**对象。

一旦配置完成，你的**NSURLSession**对象会在恰当的时间无缝地将上传和下载任务交予系统。如果在应用仍处于运行时(无论前台还是后台)任务完成了，会话对象以常规方式通知它的委托(delegate)对象。如果任务没有完成而系统终止了你的应用，系统会自动地在后台继续管理此任务。如果用户终止了应用，系统会取消任何等待中的任务。

当所有与后台会话关联的任务完成时，系统重启已终止的应用(假设sessionSendsLaunchEvents属性被设为YES并且用户未强制退出app)，并调用app委托对象的application:handleEventsForBackgroundURLSession:completionHandler:方法。(系统也可能重启app以处理验证，或其它需要app注意力的任务相关的事件。)在你的委托方法实现中，利用提供的标识符创建一个新的与之前配置相同的**NSURLSessionConfiguration**及NSURLSession对象。系统会重新连接你的新会话对象到之前的任务，并将它们的状态报告给会话对象的委托对象。
## 3.3 实现长时间运行的任务
---
对需要更多时间才得以执行的任务而言，你必须请求特有的权限在后台运行它们，而不被挂起！在iOS，仅有特定类型的app被允许运行于后台：
- 虽在后台却向用户播放有声内容的应用，比如音乐播放器app
- 虽在后台却记录音频内容的应用
- 随时通知用户位置的应用，比如导航app
- 支持网络电话(Voice over Internet Protocol,VoIP)的应用
- 需要定期下载及处理新内容的应用
- 从外部附件接收定期更新的应用

实现这些服务的应用必须声明它们支持的服务，并利用系统框架(system frameworks)来实现这些服务的相关方面。声明此类服务让系统清楚你利用哪些服务，但是在某些情况下，正是系统框架实际阻止你的应用被挂起。
### 3.3.1 声明App支持的后台任务
对某些类型的后台执行的支持必须提前由app声明。在Xcode 5及其后的版本中，可从工程设置的Capabilities标签页声明app支持的后台模型。使能Background Modes选项添加UIBackgroundModes键到app的Info.plist文件。选中一或多个复选框添加对应的后台模型值。Table3-1列出了你可以指定的后台模型，以及Xcode在app的Info.plist文件委任给UIBackgroundModes键的值。

<img src="/images/posts/2019-03-26/Table3-1.png">
每个之前的模型让系统知道你的app在恰当时刻应被唤醒或启动以响应相关的事件。比如，开始播放音乐且接下来进入后台的应用仍需要执行事件以填充音频输出缓冲器。使能Audio模式告诉系统框架，它们应继续在恰当的时间间隔给app生成必须的回调(callback)。如果应用未选择此模式，当app进入后台后，任何由app播放或录制的音频均停止。
### 3.3.2 追踪用户的位置
有多种在后台追踪用户的方式，其中大部分实际上并不需要你的应用在后台持续运行：
- significant-change location服务(推荐)
- foreground-only location服务
- background location服务

高度推荐significant-change location服务给此类应用，它们并不需要高精度的位置数据。有了这个服务，仅当用户的位置显著改变时，位置更新才产生；如此这般，对社交应用或提供用户非关键、位置相关信息的应用而言，它是比较理想的！当更新发生时，如果应用被挂起了，系统在后台将其唤醒以处理此更新。如果应用开始此服务并随后被终止，当一个新位置变得可用时，系统自动重启此app。此服务适用于iOS 4及其后的版本，并且仅在包含蜂窝无线电的设备中可用。

foreground-only和background location服务皆利用标准的位置Core Location服务来检索位置数据。仅有的区别是，如果应用被ever挂起了，foreground-only位置服务停止传递更新；如果app未支持其它后台服务或任务，此情况还是可能发生的！foreground-only位置服务倾向于服务于此类应用，(可**望文生义**)它们仅当运行于前台时才需要位置数据。

你可在Xcode工程的Capabilities标签页使能自Background模式的位置支持。(通过在应用的Info.plist文件包括UIBackgroundModes键及其location值，也可使能此支持)使能此模式并不阻止系统挂起你的app，但是它确会告诉系统每当有新数据传递时，系统应唤醒app。如此，此键高效地允许应用运行于后台以处理位置更新。

关于如何在你的app中利用以上各服务，参考Location and Maps Programming Guide。
### 3.3.3 播放及录制后台音频
持续播放或录制音频的应用(尽管应用正运行于后台)可注册以在后台执行这些任务。你在Xcode工程的Capabilities标签页使能自Background模式的支持。(也可以在应用的Info.plist文件包括UIBackgroundModes键及其audio值来使能此支持)于后台播放音频内容的应用必须播放有声内容且不能静音。

后台音频应用的典型实例包括：
- 音乐播放器app
- 录音机(音频录制)app
- 支持于AirPlay音频及视频录制的app
- VoIP app

当UIBackgroundModes键包含audio值后，当应用移动到后台时，系统的media框架自动阻止对应的app被被挂起。只要它正在播放音频或视频内容或录制音频内容，应用便继续运行于后台。然而，如果录制或播放停止了，系统则挂起应用。

你可以利用任一系统audio框架来work with后台的音频内容，并且利用这些框架的过程不会改变(is unchanged)。(对于over AirPlay的视频播放，你可以利用Media Player或AV Foundation框架来呈现你的视频。)因为当播放媒体文件时，你的app未被挂起，所以尽管app处于后台，回调操作却正常。但是，在你的回调内部，你应仅做必要的工作来提供用于播放的数据。比如说，一个数据流音频应用将需要从它的服务器下载音乐流数据并推出用于播放的当前音频样例。应用不应执行任何与播放无关的多余任务。

因为不止一个应用可能支持音频，系统要决定在任意给定的时刻，哪个app被允许播放或录制视频。前台应用通常有对音频操作的优先级。可能有不止一个后台app被允许播放音频，那么此决定就基于每个app音频会话对象的配置。你应当恰当地配置app的音频会话对象，并与系统框架谨慎合作以处理中断和其它与音频类型相关的通知。关于如何为后台执行配置音频会话对象，见Audio Session Programming Guide。
### 3.3.4 实现一个VoIP应用
一个VoIP应用允许用户利用网络连接而非设备的蜂窝服务拨打电话。如此，这样一款app需要维持一个稳定的网络连接以便它能接入呼入电话及其它相关数据。系统允许VoIP应用被挂起并为它们提供监测socket的能力，而非保持它们一直处于唤醒状态。当呼入的traffic被探测到，系统唤醒此VoIP应用并返回socket的控制予它。

为配置一个VoIP应用，你必须完成以下几件事：
- 1.在Xcode工程的Capabilities标签页之Background模式部分，使能对VoIP的支持。(也可以在app的Info.plist文件包含UIBackgroundModes键及voip值来使能此支持。)
- 2.配置一个app socket用于VoIP。
- 3.在切换到后台模式前，调用**setKeepAliveTimeout:handler:**方法来安装一个周期性执行的处理器(handler)。你的应用可利用此处理器为维持它的服务连接。
- 4.配置你的音频会话以处理出/入的转变(transition to and from active use)。

在**UIBackgroundModes**键包含voip值让系统知道，它应允许app根据管理网络socket的需要运行于后台。有此key的应用在系统启动后也能立即重启于后台以确保VoIP服务经常可用。

大多数VoIP应用也需要配置为后台audio应用以传递音频while in the background。所以，你应当给UIBackgroundModes键包含audio和voip值。如果你未做此步骤，当应用处于后台时，它不能播放或录制音频。关于UIBackgroundModes键的更多信息，见Information Property List Key Reference。

关于实现一个VoIP应用所必须采取的步骤(信息)，见“开发一款VoIP应用的窍门”一节。 
### 3.3.5 适时地取回少量内容
需要周期性检查新内容的应用(app)可以请求系统唤醒它们以便它们能够为此内容初始化一个取回操作。为支持此模式，在**Xcode**工程内Capabilities标签页之Background模式部分使能Background取回项。(也可以在应用的Info.plist文件包含UIBackgroundModes键及其fetch值来使能此支持。)使能此模式并不能保证系统会于任意时刻给你的app执行后台取回任务的机会。系统必须平衡你的app取回内容的需要、其它app的需要以及系统自身三者之间的关系。在访问这些信息后，当有行事的好机会时，系统会给这些应用时间。

当一个好机会出现，系统唤醒或启动你的app进入后台并调用应用委托(app delegate)的**application:performFetchWithCompletionHandler:**方法。利用此方法检查新内容，并且如果内容可用，便初始化一个下载操作。下载新内容一经完成，你必须执行提供的completion handler块(block)，传入一个暗示内容是否可用的结果。执行此块告诉系统它可以将你的app移回挂起状态，并评估其功耗使用量。Apps that download small amounts of content quickly,and accurately reflect when they had content available to download,are more likely to receive execution time in the future than apps that take a long time to download their content or that claim content was available but then do not download anything.

当下载任意内容时，推荐的做法是你利用NSURLSession类来初始化并管理你的下载。关于如何利用此类来管理上传及下载任务，见**URL Loading System Programming Guide**。
### 3.3.6 使用推送通知初始化一项下载
当新内容针对你的app可用时，如果你的服务器向用户的设备发送推送(push)通知，你可以请求系统于后台运行你的app以便它可以立即开始下载新内容。此后台模式意欲何为呢？其意图是最小化流逝的时间量，即当用户看到一个push通知于你的app能够展示相关内容之间的时间。大抵用户看到通知的同时，app便被唤醒，但是仍然给予多于你拥有的时间。

为支持此后台模式，在Xcode工程内Capabilities标签页的Background模式部分使能Remote通知选项。(也可以在应用的Info.plist文件包含UIBackgroundModes键及其remote-notification值来使能此支持。)

为一推送通知触发下载操作，此通知的负载必须包括对应值为1的content-available键。当这个键存在时，系统在后台唤醒此应用(或启动其进入后台)并调用应用委托(delegate)的**application:didReceiveRemoteNotification:fetchCompletionHandler:**方法。此方法的实现部分应下载相关的内容并将其融入app。

当下载任意内容时，推荐的做法是利用**NSURLSession**类来初始化并管理你的下载。关于如何利用此类来管理上传与下载任务，见**URL Loading System Programming Guide**。
### 3.3.7 在后台下载Newsstand内容
一个下载新杂志或报纸的Newsstand app可注册于后台执行这些下载。在Xcode工程内Capabilities标签页的Background模式部分使能对报刊下载的支持。(也可以在应用的Info.plist文件包含**UIBackgroundModes**键及其newsstand-content值来使能此支持。)系统启动你的app，如果你的app尚未运行，当此键存在时，以便它可以初始化此新事务的下载！

当利用Newsstand Kit框架来初始化一次下载任务时，系统为你的app处理此下载过程。即使你的app被挂起或终止了，系统仍继续下载文件。当下载操作完成后，系统迁移此文件至你的app沙盒(sandbox)并通知你的app。如果应用当前未运行，此通知将其唤醒并给它一个机会以处理这新下载的文件。如若下载过程有错误，你的app也被唤醒以处理它们。

关于如何利用Newsstand Kit框架下载内容，见NewsstandKit Framework Reference。
### 3.3.8 与外接附件通信
当app被挂起后，如果附件传递了一个更新，与外接附件合作的app可请求被唤醒。对某类以规律间隔传递数据的附件而言，如心率监测器，此支持的重要性不言而喻。在Xcode工程内Capabilities标签页的Background模式部分使能对外部附件通信的支持。(也可以在应用的Info.plist文件包含**UIBackgroundModes**键及其external-accessory值来使能此支持。)当你使能此模式后，外部附件框架并不关闭与附件的活跃会话。(在iOS 4及其更早的版本中，当app被挂起后，这些会话会被自动关闭。)当来自附件的新数据到达后，框架(framework)唤醒你的app以便它可以处理此数据。系统也唤醒应用来处理附件连接及断开通知。

支持后台处理附件更新情形的任意app必须遵循少许基本准则：
- 应用必须提供一个借口，此接口允许用户来开始及停止附件更新事件的传送。此接口应当视情况打开或关闭此附件会话。
- 一经唤醒，app有大概10秒钟来处理数据。理想情形下，它应当尽可能快地处理数据并允许它自身再次被挂起。然而，如果需要更多时间，app可利用**beginBackgroundTaskWithExpirationHandler:**方法来申请额外的时间；但是，仅当情况非当不可时，才如此行事。

<br/>
### 3.3.9 与一台蓝牙附件通信
当app被挂起后，如果外设传递了一个更新消息，与蓝牙外设合作的app可请求被唤起。对某类以规律间隔传递数据的蓝牙附件而言，如蓝牙式心率带，此支持非常重要。在Xcode工程内Capabilities标签页的Background模式部分使能对使用蓝牙附件的支持。(也可以在应用的Info.plist文件包含**UIBackgroundModes**键及其bluetooth-central值来使能此支持。)当使能此模式后，**Core Bluetooth**框架为相应外设保持开放活跃的会话。除此之外，自外设到达的新数据引起设备唤醒app以便它可以处理此数据。系统也唤醒app以处理附件连接及断开的通知。

在iOS 6，因蓝牙附件，app也可以外设模式运行。为充当一台蓝牙附件设备，必须在Xcode工程内Capabilities标签页的Background模式部分使能对此模式的支持。(也可以在应用的Info.plist文件包含**UIBackgroundModes**键及其bluetooth-peripheral值来使能此支持。)使能此模式让Core Bluetooth框架于后台暂时唤醒app，以便它可以处理与附件相关的请求。因此类事件被唤醒的app应当尽可能快地处理它们并返回，以便应用可被再次挂起。

支持于后台处理Bluetooth数据的任何app必须是基于会话的并遵循少许基本准则：
- 应用必须提供一个接口，此接口允许用户开始及停止蓝牙事件的传递。此接口应当于恰当时打开或关闭会话。
- 一经被唤醒，app有大约10秒钟来处理此数据。理想情况下，它应当尽可能快地处理此数据并允许自身能再次被挂起。然而，如果需要更多的时间，app可以利用**beginBackgroundTaskWithExpirationHandler:**方法来申请额外的时间；但是，这属于最后的机会，非到万不得已，勿用。

<br/>
## 3.4 Getting the User's Attention While in the Background
---
对被挂起、在后台或未在运行的app而言，通知是获取用户注意力的一种方式。应用可以利用本地通知(local notification)来展示警告框，播放声音，badge应用图标，或以上三者的组合。比如说，一款闹钟app也许利用本地通知来播放一段闹铃并展示一个告警窗口以关掉闹钟。当一个通知传递给用户后，用户必须决定此信息是否将app带回到前台。(如果app正运行于前台，本地通知被悄悄地传递给app而非用户。)

为安排一个本地通知的传送，创建**UILocalNotification**类的一个实例，配置通知参数，并利用**UIApplication**类的方法做好日程安排。本地通知对象包含递送通知的类型信息以及传递发生的时间点。**UIApplication**类的方法提供选项，比如立即传递通知或在已定时的时刻。

List3-2显示了一个实例，它利用用户设置的日期及时间安排了一个闹钟。此实例一次仅配置一个闹钟，并且在预定一个新闹钟之前，将以前的闹钟取消。(你自己的应用在任意时刻仅拥有128个本地通知，其每一个均可配置为以特定间隔来重复。)当alarm启动时，如果应用未运行或处于后台，alarm自身包含一个告警框和一个播放的声音文件。如果应用处于活跃状态(运行于前台)，相反，应用委托的**application:didReceiveLocalNotification:**方法得以调用。

<img src="/images/posts/2019-03-26/List3-2.png">
用于本地通知的声音文件有与推送通知相同的必要条件。定制的声音文件必须位于app的主bundle并支持以下某一种格式:Linear PCM,MA4,u-Law或a-Law。你也可以明确指定**UILocalNotificationDefaultSoundName**常量来为设备播放默认的告警声音。当发送通知并播放声音后，系统也触发于设备上的一个震动。

你可以取消已预定好的通知或利用**UIApplication**类的方法得到一通知列表。有关这些方法的更多信息，见**UIApplication Class Reference**。关于配置本地通知的额外信息，见**Local and Remote Notification Programming Guide**。
## 3.5 理解App何时启动到后台
---
支持后台执行的应用有可能被系统重启以处理到来的事件。如果一个app因其它原因而非用户强制退出得以终止，当任一以下事件发生时，系统重启此app。

1.对位置app：
- 系统收到一个位置更新，此更新满足为传送之app的配置标准。
- 设备进入或退出了注册区域。

2.对audio应用，audio framework需要应用处理一些数据。(audio应用包括那些播放音频或使用麦克风的app)。

3.对蓝牙应用：
- 充当核心角色的app收到来自连接外设的数据。
- 充当外设角色的app收到来自连接核心的命令。

4.对后台下载类app:
- 一推送通知到达，并且此通知的负载包含其值为1的content-available键。
- 系统在有机会时唤醒app以开始下载新内容。
- 对在后台利用**NSURLSession**类下载内容的app而言，与此会话对象相关的所有任务要么成功完成，要么收到一个错误。
- 由一个Newsstand应用初始化的下载任务完成。

一般情况下，在应用被用户强制退出后，系统并不重启它们。其中的一个例外是位置类app，在iOS 8及其后的版本中，被用户强制退出的它们可被系统重启。在其它情形，在应用可被自动启动进入后台前，用户必须显式地启动app或者重启设备。
## 3.6 做一个有担当的后台App
---
在碰到系统资源及硬件的使用情形时，前台app经常比后台app有优先权。运行于后台的app需要为此种差异有所准备；当运行于后台时，并调整其行为。特别地，移动至后台的app应当遵循这些基本原则：
- **Do not make any OpenGL ES calls from your code.**当运行于后台时，你一定不能创建EAGLContext对象或处理任何OpenGL ES绘制命令。使用这些调用会导致你的app被立即杀掉。应用也必须确保在移向后台前任何已提交的命令已执行完毕。当移入及移出后台时，关于如何处理OpenGL ES的信息，见**OpenGL ES Programming Guide for iOS**中Implementing a Multitasking-aware OpenGL ES Application一节。
- **被挂起前,取消任何Bonjour相关的服务.**当你的应用移向后台时，且在被挂起之前，它应当取消注册**Bonjour**并关闭与任意网络服务相关的监听socket。一款被挂起的app无论如何也无法响应到来的服务请求。如果你不自己动手关掉Bonjour服务，当你的app被挂起后，系统会自动关闭这些服务。
- **在基于网络的socket，准备处理连接失败.**在你的app被挂起时，系统可能解除socket连接。只要你的基于socket代码对其它类型的网络失败有所准备，诸如信号丢失或网络切换，这均不应导致任何不常见的问题。当你的app回复后，如果它使用socket时遇到了失败情况，简单地重建连接即可！
- **移向后台前,保存你的应用状态.**在低内存状况，后台应用可能被从内存中清除以解放空间。被挂起的应用首当其冲，被清除；在它被清除前，没有通知给app。因此，应用应当充分利用iOS 6中的状态保留机制并稍后保存界面状态于硬盘。关于如何支持此特征，见“保留App的可视外观Across Launches”一节。
- **移向后台时,去除针对不需要资源的强引用.**如果你的app维护了一个大块内存中对象缓存(尤其是图像)，当移向后台时，去除针对这些缓存的强引用。为更多详情，见“降低内存占用”一节。
- **被挂起前,停止使用共享式系统资源.**
- **避免更新你的窗口及视图.**当你的app处于后台时，因为你的app之窗口及视图均不可见，所以你应当避免更新它们。例外是在为app生成快照之前，你需要更新一个窗口的内容。
- **为外部附件响应连接及断开连接的通知.**
- **移向后台时,为活跃告警框清理资源.**
- **移向后台前,去除自视图的敏感信息.**当应用切换至后台时，系统生成app主窗口的一张快照；当切换app至前台时，此快照短暂显示。在自applicationDidEnterBackground:方法返回前，你应当隐藏或模糊掉密码和其它敏感的个人信息；这些信息有可能被捕捉为快照的一部分。
- **运行于后台期间,做最少量的工作.**给后台app的执行时间比给予前台app执行时间量受到更严格的限制。花费太多时间执行于后台的app可能会被系统减速或终止。

如果你正在实现一个后台音频app，或任意其它被允许运行于后台的app，你的app以通常的方式响应到来的消息。换句话说，当低内存警告发生时，系统可能通知你的app此情况。并且在需要终止app以释放更多内存的情形，在退出前，app调用它的委托的**applicationWillTerminate:**方法来执行任意最终的任务。
## 3.7 选择退出后台执行
---
如果你一点也不想app运行于后台，通过向app的Info.plist文件添加UIApplicationExitsOnSuspend键(值为YES)，你可以显式地退出后台。当你的app退出后台模式后，它的生命周期便在未运行、非活跃、活跃状态、从不进入后台或挂起状态之间周旋。当用户按下**Home**按钮以退出app后，应用委托的**applicationWillTerminate:**方法即被调用；在app被终止以及退回非运行状态前，app有大约5秒钟来清理环境并退出。

选择退出后台执行是被强烈阻止的，但是在某种情况下，这种选择可能是优选项。特别是，如果为后台执行编码给你的app增加难以想象的复杂性，终止app可能是一项更简单的解决方案。此外，如果你的app耗费了大量内存并且不易释放，系统也许会迅速地杀掉你的app为其它app创造更多的可用空间。如此一来，选择终止，而不是切换到后台，可能会产生相同的结果并节省你的开发时间及努力。

关于可以包含进应用之Info.plist文件的键信息，见Information Property List Key Reference。
# 四、处理App状态转换的策略
## 4.1 在启动期做什么
---

### 4.1.1 启动周期
### 4.1.2 在横屏模式启动
### 4.1.3 于首次启动时安装App特定的数据文件

## 4.2 当App被暂时打断时做什么
---

### 4.2.1 响应临时中断

## 4.3 当App进入前台时做什么
---

### 4.3.1 准备处理排队等候的通知
### 4.3.2 处理iCloud变化
### 4.3.3 处理Locale变化
### 4.3.4 处理App Setting变化

## 4.4 当App进入后台时做什么
---

### 4.4.1 后台转变周期
### 4.4.2 准备App快照
### 4.4.3 降低内存占用

# 五、实现特定App特点的策略
## 5.1 隐私策略
---

### 5.1.1 使用On-Disk加密技术保护数据
### 5.1.2 确认App的Unique用户
## 5.2 支持多版本iOS
---

## 5.3 保留App的可视外观Across Launches
---

### 5.3.1 在App内使能状态保留与恢复
### 5.3.2 状态保留与恢复过程
### 5.3.3 当排除视图控制器组时发生什么
### 5.3.4 实现状态保留及恢复的清单
### 5.3.5 在App内使能状态保留与恢复
### 5.3.6 保留视图控制器的状态
### 5.3.7 保留视图的状态
### 5.3.8 保留App的高层次状态
### 5.3.9 保存及恢复状态信息的小窍门

## 5.4 开发一款VoIP应用的窍门
---

### 5.4.1 为VoIP使用配置Socket
### 5.4.2 安装一个Keep-Alive Handler
### 5.4.3 配置App的音频会话
### 5.4.4 利用可达性接口提高用户体验

# 六、应用间通信
## 6.1 支持AirDrop
---

### 6.1.1 向其它App发送文件和数据
### 6.1.2 接收发向己方的文件和数据

## 6.2 使用URL方案与App通信
---

### 6.2.1 向其它App发送URL
### 6.2.2 实现定制的URL方案
### 6.2.3 当URL被打开时展示一个定制的启动图片


# 七、优化技巧
## 7.1 降低App的功耗
---

## 7.2 高效使用内存
---

### 7.2.1 监视低内存警告
### 7.2.2 减少App的内存占用
### 7.2.3 合理分配内存

## 7.3 调整网络代码
---

### 7.3.1 Tips for Efficient Networking
### 7.3.2 Using Wi-Fi
### 7.3.3 The Airplane Mode Alert

## 7.4 提高文件管理
---

## 7.5 Make App Backups More Efficient
---

### 7.5.1 App备份最佳实践
### 7.5.2 App更新期间的文件保存

## 7.6 将工作从主线程转移走
---
