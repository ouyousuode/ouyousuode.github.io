---
layout: post
title: 第一个iOS App
---
{{page.title}}
=================================
<img src="/images/posts/2018-07-18/index_0.png">
<img src="/images/posts/2018-07-18/index_1.png">
# About Creating Your First iOS App
第一个iOS应用向你介绍iOS app开发的3T:
- **Tools**.如何利用Xcode创建以及管理一个工程。
- **Technologies**.如何创建一个可以响应用户输入动作的应用。
- **Techniques**.如何利用所有iOS应用开发的基础设计模式。

在完成了指导中的所有步骤后；你可以得到这样一个app，它接收你的输入并将其输出到屏幕上:

<img src="/images/posts/2018-07-18/First_iOS_App_3.png">

为从此指导中获益，你必须已熟悉了计算机编程的基本概念，尤其是Objective-C语言。如果你以前没用过Objective-C,在阅读此指南前，读一读Learning Objective-C:A Primer。
## At a Glance
---
跟着此指南走，会帮你踏上开发伟大iOS应用的康庄大道。虽然你在此指南中创建的app非常简单，但是它会帮助你适应iOS开发环境，并且向你介绍一些塑造伟大iOS应用的强大设计模式。
### Becoming Familiar with the Tools and the Design Patterns
iOS应用在Xcode中开发，它是Apple的集成开发环境。Xcode包含了多个开发者工具，并且集成了用于编码app的编程框架。

随着你开始为app编码，你需要学习成功的app是如何构成的以及一些关键设计模式如何帮你写出更棒的代码。
### Learning About the Roles of View Controllers and Views
在一个iOS应用中，一个视图(view)仅仅是屏幕上的用户可以看见并与之交互的一块区域。一个视图对象可以是任意大小，它可以包含其它视图对象，比如按钮或者图像。视图控制器是控制一个或多个视图的一个对象。在此指南中，你会学到：app中不同对象的角色，如何创建视图控制器与添加到它视图中对象的连接。
### Writing Code to Perform a Custom Task
Xcode与编程框架(programming framework)合作为你提供很多功能，但是仍然有一些你的app必须完成的特定任务。在本指导中，你会学到：如何通过写一个方法来实现某个特定的任务；当一个按钮被点击时，此方法被调用。
### Solving Problems and Choosing Your Next Steps
随着你完成本指导中的任务，你可能遇到你不知如何解决的问题。Your First iOS Application粗略描述了一些可查找的常见错误，它也包括一个用于对比的代码列表。

在完成本指南后，你应当考虑可提高app质量以及增长知识面的方式。可选择的方向很多，并且Xcode和iOS编程框架可帮你实现你可设计的方方面面。
## See Also
---
开发iOS应用是一个多步骤的过程，从决定app应做什么到将它提交App Store。Your First iOS Application不能帮你决定你的app应当做什么，也未描述如何把他们提交到App Store，但是有很多可帮你实现这些任务的资源。这些资源中的部分列在此处：
- 想学习设计iOS应用的用户界面及体验，可参见iOS Human Interface Guidelines。
- 想对创建一个全特征iOS应用有综合认识，参见iOS App Programming Guide。
- 当准备提交app到App Store时，为了解需要执行的全部任务，可参考Developing for the App Store。

<br/>
# Getting Started
为创建本指导中的iOS应用，需要Xcode 4.2(及以上)。Xcode是为iOS及Mac OS X开发的Apple集成开发环境。当在Mac上安装Xcode时，也会得到iOS SDK，它包括iOS平台的编程接口。
## Create and Test a New Project
---
为开启开发应用之路，创建一个新Xcode工程。

1.启动Xcode,

<img src="/images/posts/2018-07-18/Xcode_00.png">
2.在对Xcode的欢迎界面，选择"Create a new Xcode project"。Xcode会打开一个新窗口，并且展示一个对话页面，开发者可从此选择一个模版。Xcode包括几个内置的应用模版，开发者可以利用这些模版开发通常样式的iOS应用。比如，Tabbed App模版创建一个类似于iTunes的应用，而根据Master-Detail App模版创建的app之外观与Mail相差无几。

<img src="/images/posts/2018-07-18/Xcode_01.png">
3.在iOS部分，选择Application条目下的Single View App模版后，点击click。

<img src="/images/posts/2018-07-18/Xcode_02.png">
4.填写Production Name、Organization Name以及Organization Identifier，选择开发使用的编程语言。它们可以使用以下值：
- Language : Objective-C
- Production Name : HelloWorld
- Organization Name及 Identifier : 如有，可如实填写；若无，可以使用edu.self

完成后，选择Next,即得一个新工程。

<img src="/images/posts/2018-07-18/newProjectDone.png">
虽然你连一行代码都没写，但是可以构建应用并在模拟器中运行它。正如它名字所暗示的那样，模拟器(Simulator)允许你得到app如何表现及运行的直观印象，就好像它运行在基于iOS设备上一样。

<img src="/images/posts/2018-07-18/deviceChoice.png">
选择一款称心的模拟设备，然后点按Run。在Xcode完成工程构建后，模拟器会自动启动。因指定了一款iPhone，模拟器会展现一个类似此iPhone的窗口。在模拟的iPhone屏幕上，模拟器打开应用。

<img src="/images/posts/2018-07-18/defaultApp.png">
现在看，这款应用并不有趣：它仅显示一块空白屏幕。为理解此空白屏幕来自何处，需要学习代码中的对象以及它们是如何合作来启动app的。现在，可以退出模拟器了(要确保，不要退出Xcode)。

## Find Out How an Application Starts Up
---
因为基于Xcode模版构建工程，所以当你运行应用时，大部分基本的应用环境已自动建立。比如，Xcode创建一个application对象，它连接窗口服务器(window server)，建立run loop以及其它的准备工作。此工作的大部分均由**UIApplicationMain**函数完成，它在main.m文件中完成调用。
可在工程目录中查看main.m源文件：

<img src="/images/posts/2018-07-18/mainFile.png">
main.m文件内的**main**函数在一个自动释放池内调用**UIApplicationMain**函数。
``` Objective-C
@autoreleasepool {
	return UIApplicationMain(argc,argv,nil,NSStringFromClass([HelloWorldAppDelegate class]));
}
```
此@autoreleasepool声明支持Automatic Reference Counting(ARC)系统。ARC为应用提供自动化的对象生命周期管理。对UIApplicationMain的调用创建一个UIApplication实例以及一个application delegate实例(本例中为HelloWorldAppDelegate)。应用delegate的主要功能是提供绘制应用内容的窗口。

对UIApplicationMain的调用也会扫描应用的Info.plist文件。Info.plist文件是一个属性列表，它包含诸如应用名称、图标等信息。
可在工程中查看此属性列表：

<img src="/images/posts/2018-07-18/Info_plist.png">
因为选择在工程中使用storyboard，Info.plist文件也包含应用对象应当加载的story file的名字。一个**storyboard file**包含一个存档文件，此文件内含对象、过渡以及定义应用用户界面的连接。

在此应用中，storyboard文件被命名为Main.storyboard。当应用启动时，加载此文件，并且实例化其内的initial视图控制器。

为查看storyboard文件，在项目导航器中选择Main.stroyboard；Xcode在编辑域打开故事板(storyboard)文件。打开默认故事板文件，项目窗口应与下图类似：

<img src="/images/posts/2018-07-18/defaultStoryboardFile.png">
故事板文件包含场景(scenes)和segues。一个**scene**代表一个视图控制器，它管理一个内容区域；一个**segue**代表两个场景间的过渡。

因为Single View App模板仅提供一块占据整个屏幕的内容区域，所以app的Main.stroyboard文件仅含有一个场景且不含segue。画布中指向场景左侧的箭头是**initial scene indicator**，它确定app中的初始场景。

因应用中的HelloWorldViewController对象管理此画布中的场景，所以它被命名为Hello World View Controller Scene。Hello World View Controller场景包含显示在Xcode **outline view**的几个条目，即夹在画布与项目导航器中间的这块面板。
- **first responder**是一个动态占位符对象，它代表第一个收到事件的响应者对象，这些事件包括键盘、手势变化以及动作消息等。在本指导中，你不会利用第一响应者做些什么，但是可以在Event Handling Guide for iOS中学习更多有关的内容。
- HelloWorldViewController对象由一个内空的黄色球形表示。当故事板文件中的某个场景被加载时，它便创建一个视图控制类的实例。
- 还有一个View。这个view的白色背景即在模拟器运行应用所看到的现象。

在设置过程，应用对象执行以下任务：
- 加载主故事板文件。
- 从应用delegate处获取窗口对象(window object),或者创建一个UIWindow实例并把它与应用delegate关联起来。
- 实例化故事板的初始视图控制器，并将其委任为窗口对象的根视图控制器。

当应用对象完成这些任务后，它发送一个application:didFinishLauchingWithOptions:消息给它delegate。此消息给此delegate一个执行其它任务的机会，比如应用显示前的额外配置。

在本指导中，应用委托由Single View App模板提供，它的名字是HelloWorldAppDelegate。你不会改变此工程内的默认应用委托(delegate)类，但是你会在随后的步骤中使用委托(delegation)。你可在Delegation Helps You Add Functionality Without Subclassing中学到更多关于委托的内容。

## Recap
---
在本章，利用Xcode创建了一个基于Single View模板的新工程。接着，构建且运行了模板定义的默认应用。也查看了工程的一些基本组成，比如main.m源文件、Info.plist文件以及故事板文件；学习了一个应用是如何起动的。下一章，将学习遍及iOS应用开发中的设计模式以及如何将它们适用于自己的app。

<br/>
# Understanding Fundamental Design Patterns
## Cocoa Touch Provides the Frameworks You Need for iOS App Development
---
## Delegation Helps You Add Functionality Without Subclassing
---
## Model-View-Controller Makes Code Easier to Write and Reuse
---
## The Target-Action Pattern Enables Responses to User Interactions
---
## Other Design Patterns
---

# Inspecting the View Controller and Its View
## Use the Inspector to Examine the View Controller
---
## Change the View's Background Color
---
## Recap
---

# Configuring the View
## Add the User Interface Elements
---
## Create an Action for the Button
---
## Create Outlets for the Text Field and the Label
---
## Make the Text Field's Delegate Connection
---
## Test the Application
---
## Recap
---

# Implementing the View Controller
## Add a Property for the User's Name
---
## Implement the changeGreeting: Method
---
## Configure the View Controller as the Text Field's Delegate
---
## Text the Application
---
## Recap
---

# Troubleshooting
## Code and Compiler Warnings
---
## Check the Storyboard File
---
## Delegate Method Names
---
# Next Steps
## Improve the User Interface
---
## Install the Application on a Device
---
## Add More Functionality
---
## Add Unit Tests

# Code Listings
## The Interface File : HelloWorldViewController.h
## The Implementation File : HelloWorldViewController.m 
