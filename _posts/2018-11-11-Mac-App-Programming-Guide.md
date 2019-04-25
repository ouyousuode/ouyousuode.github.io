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
OS X吸收了最新的技术用以创建强劲且有趣的应用。但是，技术本身并不足以使每个应用都伟大(great)。将一个app与其同类区分开来的是它怎样帮用户实现某些确定的目标。毕竟，用户不会去关心一个app使用了什么技术，只要它能帮助完成需要的任务即可。妨碍用户的app将被遗忘，但是把工作变得容易或把游戏变得更有趣的app将被记住。

你使用Cocoa为OS X编写应用。Cocoa给你所有OS X特征的使用权限，并允许你将app与系统的剩余部分干净地集成在一起。本章节涉及帮你创建伟大app的OS X关键部分。特别是，此章描述了在OS X v10.7引入的一些重要易用(ease-of-use)技术。若想浏览OS X中更多可用的技术，可参考Mac Technology Overview。
## An Environment Designed for Ease of Use
---
OS X努力提供的环境是对用户尽可能易懂易用的。通过将困难任务简单化，系统使用户更具创造力变得容易起来，并且花费更少的时间来担心需要使电脑工作的工作。当然，简化任务意味着你的app不得不做更多的工作，但是OS X也会在这些方面提供帮助。

随着你设计app，你应考虑用户通常执行的任务以及找到使它们变简单的方式。OS X支持强劲的(powerful)易用性(ease-of-use)特征以及设计原则。比如：
- 用户不应手动保存他们的工作。Cocoa的文档模型为无须用户交互即保存基于文件的文档提供支持；可见The Document Architecture Provides Many Capabilities for Free。
- Apps应在登陆时恢复用户的工作环境。Cocoa为解析应用界面当前状态及在启动期恢复此状态提供支持；见User Interface Preservation。
- Apps应当支持自动终止以便用户勿需不得不退出他们。自动终止意味着当用户关闭应用窗口时，应用好像退出了，但实际上只是静静地移动到后台。此优势是随后的启动可接近瞬时，因为应用只是又退回到前台；具体可见Automatic and Sudden Terminationof Apps Improve the User Experience。
- 你应考虑给用户提供一个逼真的全屏体验，怎么实现呢？通过实现一个用户界面的全屏版本即可。此全屏体验可消除外部的干扰，允许用户集中注意力到他们的内容上；可参考Implementing the Full-Screen Experience。
- 在应用中为合适的动作提供触控板手势。手势可为常见任务提供简单的快捷方式，也可用于补充现存的控件及菜单项命令。OS X为报告手势给应用提供自动支持，此过程经由正常的事件处理(event-handling)机制；可见Cocoa Event Handling Guide。
- 考虑最小化或消除用户与原始文件系统的交互。有些以iPhoto和iTunes方式的应用，通过以一个简化版的精心设计过的浏览器来提供一个更好的用户体验，而不是通过open和save面板把整个文件系统暴露给用户。OS X使用一个定义明确的文件系统结构，此结构允许你容易地放置及找到文件，此结构包括为访问这些文件的很多技术；见The File System。
- 为向app支持特定的文档类型，提供一个Quick Look插件以便用户可从app外部查看你的文档；可参考Quick Look Programming Guide。
- Apps应支持为OS X用户体验的基本特征，正是它们才使得应用得以优雅且直观。用户应一直拥有控制权，收到持续的反馈，因为应用一直在原谅那些可撤销的动作；见OS X Human Interface Guidelines。

以上种种特征，均由Cocoa支持；付出少许努力便可集成到开发的应用中。

## 久经考验的图形化环境
---
高质量的图形图像及动画可使app看起来great，并传达很多信息给用户(一图胜千言)。尤其，动画是提供关于用户界面变化的反馈的良好方式。因此，当你设计app时，可将以下观点谨记于心：
- 使用动画提供反馈及传达变换。Cocoa为快速创建复杂的动画提供支持，这些支持包含在AppKit和Core Animation框架内。想了解有关创建基于视图的动画信息，见Cocoa Drawing Guide。关于利用Core Animation创建动画，可参考Core Animation Programming Guide。
- 包括美术图片及图像的高分辨率版本。当app运行在一个尺度因子大于1.0的屏幕时，OS X自动加载高分辨率图像资源。包含这样的图像资源可使app在高分辨率屏幕上的图像看起来更锐利(sharper)、更脆(crisper)。

关于适用于OS X的图像技术，参见Media Layer in Mac Technology Overview。
## Runtime环境的底层细节
---
当你准备编写实际代码时，有很多促使编码工作变容易的可用技术。OS X支持全部的基本功能，比如内存管理，文件管理，网络及并发；你需要使用它们来编写自己的代码。在某些情况下，虽然OS X也提供更为复杂的服务，但遵此行事，可使编写代码更容易。
### 基于UNIX
OS X由一个64位的Mach内核提供动力，此内核管理处理器资源、内存以及其它的底层行为。在内核的顶部，坐落着一个修改过的Berkeley Software Distribution(BSD)操作系统版本，它向app提供用以与底层系统打交道的接口(interfaces)。Mach和BSD的组合为app提供以下的系统级支持：
- 抢占式多任务 —— 全部进程更有效地共享CPU。内核调度进程，以确保它们皆能接收需要运行的时间的方式。甚至，后台app也能继接收CPU时间来执行正运行的任务。
- 受保护的内存 —— 每个进程运行在它们自己的受保护内存空间内，如此可阻止进程间相互打扰。(Apps可以共享部分内存空间以实现快速的进程间通信，但是它们必须负责恰当地同步及锁定此内存。)
- 虚拟内存 —— 64位app有一个大概18 billion billion字节大小的虚拟地址空间。(如果创建一个32位的app，其虚拟内存大小仅有4GB)当一个app的内存使用量超过了超过了剩余的物理内存，系统会透明地将页写到硬盘，以生成更多可用的内存。被写出到硬盘的页会一直待着，直至它们再次被内存需要或者app退出了。
- 网络和Bonjour —— OS X为标准的网络协议及现今使用的服务提供支持。BSD socket为app提供底层的通信机制，但是高级接口也存在。通过提供一种通知及连接TCP/IP之上的网络服务的动态方式，Bonjour简化了用户交流的体验(networking experience)。

为OS X底层环境的详细信息，可参考Mac Technology Overview中的Kernel and Device Drivers Layer一节。
### 并发与线程化
每个进程都以一个单一的线程开始，而后根据所需创建更多线程。虽然可以利用POSIX和其它高级接口来直接创建线程，但是对大部分工作而言，利用block对象与Grand Central Dispatch(GCD)或operation对象非直接地创建它们更好！其中，operation对象是用NSOperation类实现的一种Cocoa并发技术。

GCD与operaion对象是针对原始线程的一种替代选择，它们可以简化或者消除与线程化编程相关的诸多问题，比如同步和加锁。尤其是，它们定义了一个异步编程模型；在此模型内，你可指定想执行的工作以及执行它的顺序。系统随即处理需要调度必需线程的繁杂工作，以及在当前硬件上尽可能高效地执行你的任务。对于时间敏感性的数据处理任务，你不应使用GCD或者operation，但是，你可以利用它们处理大部分其它类型的任务。

关于在app中使用GCD和operation对象实现并发操作的更多信息，可以参考Concurrency Programming Guide。
### 文件系统
OS X的文件系统被精心组织为用户提供更好的体验。Finder将普通用户不应使用的文件及目录隐藏起来，比如底层UNIX目录的内容；而不是向用户暴露整个文件系统。对终端用户而言，此举可提供一个更简单的界面。Apps仍然可以访问它们有权限的任意文件及目录，不管它们是否被Finder隐藏。

当创建app时，你应理解并遵循这些与OS X文件系统有关的惯例。清楚在哪放文件以及如何从文件系统获得信息确保一个更好的用户体验。
#### 几个重要的App目录
OS X文件系统的组织方式是把相关的文件及数据分组进一个指定的位置。文件系统中的每个文件有它自己的位置，app们需要知道将它们创建的文件置于何处。如果你正由App Store分发app，此举就尤为重要了；这种分发方式期望你放置数据在特定位置。

Table1-1列出了apps经常打交道的目录。其中有些在home目录内，它要么是用户的home目录，要么是app的容器目录(如果app采用App Sandbox的话)。因为实际路径可根据这些条件区分，所以，利用NSFileManager类的URLsForDirectory:inDomains:方法获取实际的目录路径。你可以给返回的URL对象添加任意特定的目录及文件名信息来完善此路径。

<img src="/images/posts/2018-11-11/Table1-1.png">
下面这个程序展示如何获取到Application Support目录的基础路径以及向它追加一个特定的app目录。

<img src="/images/posts/2018-11-11/List1-1.png">
关于如何访问系统目录中的文件，可进一步查看File System Programming Guide。
#### Coordinating File Access with Other Process
在OS X，其它进程可能会访问你正在访问的某个文件。因此，当处理文件时，你应当用OS X v10.7引入的文件协调接口(file coordination interfaces)来接收某种通知，具体是什么呢？即，当其它进程试图读取或修改你的app当前正使用的文件时，你的app进程能得到通知。比如，当你的app采用iCloud存储时，协调文件访问便是至关紧要的。

这些文件协调APIs允许你维护对所关心的文件及目录的所有权。在任意时间点，其它进程试图访问这些项目中的任意一员时，你的app会有一个响应的机会。比如，当一个app试图读取你的app正在编辑的某个文档的内容时，在其它进程被允许做读取操作前，你可以将尚未保存的修改写入硬盘中。

又比如，使用iCloud文档存储时，你必须包含文件协调功能，因为多个app均可访问存于iCloud的文档文件。包含文件协调功能的最简单方式为使用NSDocument类，它会为你处理所有文件相关的管理工作。见Document-Based App Programming Guide for Mac。

另一方面，如果你正在编写一个library-style(或者“鞋盒”)app，你必须直接使用文件协调接口，像在File System Programming Guide描述的那样。
#### Interacting with the File System
虽然Macintosh电脑上的硬盘默认使用HFS+文件系统，但此电脑仍能与使用其它文件系统的硬盘打交道。Table1-2列出了一些你可能需要考虑的基本文件系统属性，以及你应如何处理它们。

<img src="/images/posts/2018-11-11/Table1-2.png">
#### 针对Mac App Store的文件系统使用要求
为促进一个更一致的用户体验，提交到Mac App Store的应用必须遵守在何处写文件的特定规则。当应用产生对文件系统的副作用时，用户可能会疑惑不解。比如，在用户的文档目录存储数据库，在用户的Library文件夹存储文件，在用户的Library文件夹存储用户数据，等等。

你的应用必须遵循以下的要求：
- 你可能会使用诸如User Defaults、Calendar Store及Address Book这样的Apple框架，这些框架会暗中写入某具体位置的文件，而其中有些位置是你的应用无法直接访问的。
- 你的应用可能写入某个利用Apple编程接口获取的临时路径,此编程接口可能是NSTemporaryDirectory函数。
- 你的应用可能向以下目录写入：
```Objctive-C
~/Library/Application Support/<app-identifier>
~/Library/<app-identifier>
~/Library/Caches/<app-identifier>
```
在这，app-identifier是应用的bundle identifier，它的名字或者公司名称。这个必须准确匹配。可以常用比如URLsForDirectory:inDomains:函数这样的Apple编程接口来定位这些路径，而非硬编码它们。更多信息，可见File System Programming Guide。
- 如果你的应用管理图片、音乐及视频库，它可能也向以下目录写入：
```Objective-C
~/Pictures/<app-identifier>
~/Music/<app-identifier>
~/Movies/<app-identifer>
```
- 如果用户显式地在一个备用位置存储数据，你的应用也可以写入此位置。

<br/>
### 安全性
OS X的安全技术帮你守护app创建或管理的敏感数据，以及最小化来自恶意代码攻击的伤害。这些技术影响你的app如何与系统资源及文件系统打交道。
#### App沙盒和XPC
通过遵照Secure Coding Guide中推荐的实践，你可以保护app以免受来自流氓软件的攻击。但是，攻击者只需找到防卫中的一个单一漏洞，或者你链接的框架和库中的任一漏洞，便可以获得对你的app的控制权。

如果恶意代码侵入了你的应用，App沙盒提供抵抗破坏或删除用户数据等的最后一道防线。App沙盒也能最小化来自编码错误的损害。它的策略是双重保险：
- 1.App沙盒使用能够描述你的app如何与系统打交道。系统随即授权app访问某些它需要完成的工作。为向app提供最高级别的损坏防卫，最佳实践是尽可能采用最牢固的沙盒。
- 2.App沙盒允许用户显式地给app授权其它访问，通过Open与Save,drag与drop，以及其它常见的用户交互方式。

通过在Xcode中设置权利(entitlement)的方式，描述app与系统间的交互。一条权利是定义在属性列表文件的一个键值对(key-value pair)，它授予针对某个目标的特殊能力及安全权限。比如，有些权限键暗示着你的应用需要访问摄像头、网络以及如Address Book般的用户数据。关于OS X中全部可用的权利，可见Entitlement Key Reference。

当你采用App沙盒后，系统为你的app提供一个特殊的目录，一个称之为container仅用于你的app之目录。你的应用可对此容器自由读/写。POSIX层之上的全部OS X路径查找APIs都是针对容器而言，而非用户的home目录。其它沙盒内的app无法访问你的app的容器。

你的沙盒之内的app能够通过以下三种方式访问容器之外的路径：
- 在特定的用户方向上。
- 利用针对特定文件系统位置的授权来配置app，比如Movies文件夹。
- 当一个路径位于对世界均可读的目录内。

与用户交互以扩张沙盒的OS X安全技术称为Powerbox。Powerbox没有API。你的app很直白地使用Powerbox，比如当你使用NSOpenPanel和NSSavePanel类时，又或者当用户使用drag及drop操作时。

某些app操作更可能成为恶意开发(exploitation)的靶子。这样的实例很多，比如，对来自网络数据的解析，以及对视频帧的解码。通过使用XPC，你可以提高App沙盒提供的抗破坏效力，怎么做呢？把这些潜在的危险活动分配到它们自己的地址空间内。

XPC是一项OS X进程间通信技术；通过赋能权限分离，它可以补充App沙盒。依次地，权限分离(privilege separation)是一项开发策略；在此策略下，根据每块需要的系统资源访问权，将app划分成若干块。你创建的这些组件被称为XPC服务。关于采用XPC的细节，可参见Daemons and Services Programming Guide。

若寻求App沙盒及如何使用的完整解释，读一读App Sandbox Design Guide。
#### 代码签名
OS X采用被称为代码签名(code signing)的安全技术来确保你的app确实由你创建。在为app签名后，系统可捕捉到针对此app的任何改动，无论是意外引入的，还是恶意代码造成的。包括App沙盒及家长控制(parental controls)在内的多种安全技术均仰仗代码签名。

在大多数情况下，你可以指望Xcode的自动代码签名功能，这仅需要你为工程在构建(build)设置中指定一个代码签名特性即可。关于代码签名app需采取的步骤均描述于Tools Workflow Guide for Mac。如果你需要将代码签名纳入到一个自动化构建系统内，或者将应用与第三方库链接起来，参考Code Signing Guide中描述的步骤！

当你采用App沙盒时，你必须对app进行代码签名。这是因为权利(entitlement)被固化为app代码签名的一部分。

OS X强化了app容器与代码签名间的连接。这项重要的安全特征确保没有其它沙盒内的app可访问你的容器。这项机制的工作流程如下：系统为一个应用创建容器后，每当拥有相同bundle ID的应用启动时，系统便会检查此应用的签名是否与容器期望的签名一致；如果系统探测到不一致，它将阻止应用的启动。

为App沙盒内涵下代码签名的完整解释，可阅读App Sandbox Design Guide的Depth节。
#### The Keychain
钥匙串(keychain)是存储用户密码及其它密码的安全、已加密容器。设计它用于帮助用户管理他们的多个登录项，每一对ID及密码。你应当经常利用钥匙串为app存储敏感的证书。

关于钥匙串的更多信息，可见Keychain Services Programming Guide中Keychain Services Concepts部分。
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
