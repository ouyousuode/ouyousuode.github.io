---
layout: post
title: Mac App Programming Guide
---
{{page.title}}
==================================
# 前言、关于OS X App设计
本文档是学习如何创建Mac应用的起点。它包含有关OS X环境的基本信息，以及你的应用如何与环境交互。它也包含关于Mac应用架构的重要信息，和设计app关键部分的一些技巧。

<img src="/images/posts/2018-11-11/MacAppProgrammingGuide_0.png">
## 0.1  At a Glance
---
Cocoa是解锁OS X全部力量的应用环境。Cocoa提供APIs、库以及运行时(runtimes)，此三者可帮你创建快速、令人惊叹的应用；这些应用自动继承OS X的漂亮外观及feel。

### 0.1.1  Cocoa帮你创建OS X应用
开发者使用Cocoa为OS X编写应用，Cocoa为你的程序提供大量的基础设施。贯穿Cocoa的基本设计模式可使你的app与子系统框架无缝衔接，核心的应用对象提供关键行为以支持简单性及应用架构的可扩展性。Cocoa环境的关键部分专门设计成支持易用性；易用性是促使Mac app成功的最重要的一方面。通过消除在设备间同步数据的需要，很多应用应采纳iCould以提供一个更连贯的用户体验。
### 0.1.2  Common Behaviors Make Apps Complete
在创建app的设计阶段，你需要考虑如何实现用户期望的某些特征。将这些特征集成进你的app架构可对用户体验留有影响：可访问性，偏好性，Spotlight，服务，分辨率无关性，快速用户切换以及Dock。使你的app采用全屏幕模式，占据整块屏幕，向用户提供一个更逼真的观影体验，也促使他们关注自己的内容而无暇他顾。
### 0.1.3  正确理解：满足系统及App Store的要求
恰当地配置app是开发过程的重要部分。Mac apps使用一个称为bundle的结构化目录管理他们的代码及资源文件。虽然大部分文件是定制的，且为支持app而存在；但是，有些却为系统或App Store所需，因此，必须恰当配置。应用bundle也包含一些你提供的用于国际化app以支持多种语言的资源。
### 0.1.4  Finish Your App with Performance Tuning
随着你开发应用及工程代码稳定后，可以着手性能优化了。当然，你想你的app尽可能快速地启动及响应用户的命令。一个反应灵敏的app可容易地纳入进用户的工作流中，并且给人一种制作精良的印象。可通过加速启动及减小代码体积来提高app的性能。可参考的资料有：
- WWDC 2013 224 Designing Code for Performance
- WWDC 2013 408 Optimizing Your Code Using LLVM
- WWDC 2015 230 Performance on iOS and watchOS
- WWDC 2015 718 Building Responsive and Efficient Apps with GCD
- WWDC 2016 406 Optimizing app Startup Time
- WWDC 2016 719 Optimizing io for Performance and Battery Life
- WWDC 2017 706 Modernizing Grand Central Dispatch Usage

...

## 0.2  如何使用本文档
---
本指南向你介绍在写App时遇到的最重要的技术。在此指南中，你将看到写一个app需要的方方面面。也就是说，本文向你展现你需要的所有片段，以及如何把他们拼装在一起。仍有本文未谈及的app设计的重要部分，比如用户界面设计。然而，本文包括提供细节的其它文档的链接。

除此之外，本指南强调了在OS X v10.7引入的特定技术，这些技术提供了一些与老旧版本相区别的必要能力；这些能力来自iOS平台最出色的功能，可赋予应用卓越的易用性。
## 0.3  See Also
---
下面的文档提供了关于设计Mac应用的额外信息，也包括本指南中涉及话题的更多详情:
- 为浏览一篇展示如何创建一个Cocoa app的教程，参见Start Developing Mac Apps Today。
- 为寻求创建高效率app的用户界面设计，参考OS X Human Interface Guidelines。
- 为理解如何创建明确app ID，创建描述文件(provisioning files)，以及为应用使能正确的权限(entitlements)，如此你才能通过Mac App Store出售应用或使用iCloud存储，参考App Distribution Guide。
- 为OS X技术的综合查访，看Mac Technology Overview。
- 为理解如何实现一个基于文档的app，参见Document-Based App Programming Guide For Mac。

<br/>
# 一、The Mac Application Environment
OS X吸收了最新的技术用以创建强劲且有趣的应用。但是，技术本身并不足以使每个应用都伟大(great)。将一个app与其同类区分开来的是它怎样帮用户实现某些确定的目标。毕竟，用户不会去关心一个app使用了什么技术，只要它能帮助完成需要的任务即可。妨碍用户的app将被遗忘，但是把工作变得容易或把游戏变得更有趣的app将被记住。

你使用Cocoa为OS X编写应用。Cocoa给你所有OS X特征的使用权限，并允许你将app与系统的剩余部分干净地集成在一起。本章节涉及帮你创建伟大app的OS X关键部分。特别是，此章描述了在OS X v10.7引入的一些重要易用(ease-of-use)技术。若想浏览OS X中更多可用的技术，可参考Mac Technology Overview。
## 1.1  An Environment Designed for Ease of Use
---
OS X努力提供的环境是对用户尽可能易懂易用的。通过将困难任务简单化，系统使用户更具创造力变得容易起来，并且花费更少的时间来担心需要使电脑工作的工作。当然，简化任务意味着你的app不得不做更多的工作，但是OS X也会在这些方面提供帮助。

随着你设计app，你应考虑用户通常执行的任务以及找到使它们变简单的方式。OS X支持强劲的(powerful)易用性(ease-of-use)特征以及设计原则。比如：
- 用户不应手动保存他们的工作。Cocoa的文档模型为无须用户交互即保存基于文件的文档提供支持；可见The Document Architecture Provides Many Capabilities for Free。
- Apps应在登陆时恢复用户的工作环境。Cocoa为解析应用界面当前状态及在启动期恢复此状态提供支持；见User Interface Preservation。
- Apps应当支持自动终止以便用户勿需不得不退出他们。自动终止意味着当用户关闭应用窗口时，应用好像退出了，但实际上只是静静地移动到后台。此优势是随后的启动可接近瞬时，因为应用只是又退回到前台；具体可见Automatic and Sudden Terminationof Apps Improve the User Experience。
- 你应考虑给用户提供一个逼真的全屏体验，怎么实现呢？通过实现一个用户界面的全屏版本即可。此全屏体验可消除外部的干扰，允许用户集中注意力到他们的内容上；可参考Implementing the Full-Screen Experience
- 在应用中为合适的动作提供触控板手势。手势可为常见任务提供简单的快捷方式，也可用于补充现存的控件及菜单项命令。OS X为报告手势给应用提供自动支持，此过程经由正常的事件处理(event-handling)机制；可见**Cocoa Event Handling Guide**。
- 考虑最小化或消除用户与原始文件系统的交互。有些以iPhoto和iTunes方式的应用，通过以一个简化版的精心设计过的浏览器来提供一个更好的用户体验，而不是通过open和save面板把整个文件系统暴露给用户。OS X使用一个定义明确的文件系统结构，此结构允许你容易地放置及找到文件，此结构包括为访问这些文件的很多技术；见The File System。
- 为向app支持特定的文档类型，提供一个Quick Look插件以便用户可从app外部查看你的文档；可参考Quick Look Programming Guide。
- Apps应支持为OS X用户体验的基本特征，正是它们才使得应用得以优雅且直观。用户应一直拥有控制权，收到持续的反馈，因为应用一直在原谅那些可撤销的动作；见OS X Human Interface Guidelines。

以上种种特征，均由Cocoa支持；付出少许努力便可集成到开发的应用中。

## 1.2  久经考验的图形境
---
高质量的图形图像及动画可使app看起来great，并传达很多信息给用户(一图胜千言)。尤其，动画是提供关于用户界面变化的反馈的良好方式。因此，当你设计app时，可将以下观点谨记于心：
- 使用动画提供反馈及传达变换。Cocoa为快速创建复杂的动画提供支持，这些支持包含在AppKit和Core Animation框架内。想了解有关创建基于视图的动画信息，见Cocoa Drawing Guide。关于利用Core Animation创建动画，可参考Core Animation Programming Guide。
- 包括美术图片及图像的高分辨率版本。当app运行在一个尺度因子大于1.0的屏幕时，OS X自动加载高分辨率图像资源。包含这样的图像资源可使app在高分辨率屏幕上的图像看起来更锐利(sharper)、更脆(crisper)。

关于适用于OS X的图像技术，参见Media Layer in Mac Technology Overview。
## 1.3  Runtime环境的底层细节
---
当你准备编写实际代码时，有很多促使编码工作变容易的可用技术。OS X支持全部的基本功能，比如内存管理，文件管理，网络及并发；你需要使用它们来编写自己的代码。在某些情况下，虽然OS X也提供更为复杂的服务，但遵此行事，可使编写代码更容易。
### 1.3.1  基于UNIX
OS X由一个64位的Mach内核提供动力，此内核管理处理器资源、内存以及其它的底层行为。在内核的顶部，坐落着一个修改过的Berkeley Software Distribution(BSD)操作系统版本，它向app提供用以与底层系统打交道的接口(interfaces)。Mach和BSD的组合为app提供以下的系统级支持：
- 抢占式多任务 —— 全部进程更有效地共享CPU。内核调度进程，以确保它们皆能接收需要运行的时间的方式。甚至，后台app也能继接收CPU时间来执行正运行的任务。
- 受保护的内存 —— 每个进程运行在它们自己的受保护内存空间内，如此可阻止进程间相互打扰。(Apps可以共享部分内存空间以实现快速的进程间通信，但是它们必须负责恰当地同步及锁定此内存。)
- 虚拟内存 —— 64位app有一个大概18 billion billion字节大小的虚拟地址空间。(如果创建一个32位的app，其虚拟内存大小仅有4GB)当一个app的内存使用量超过了超过了剩余的物理内存，系统会透明地将页写到硬盘，以生成更多可用的内存。被写出到硬盘的页会一直待着，直至它们再次被内存需要或者app退出了。
- 网络和Bonjour —— OS X为标准的网络协议及现今使用的服务提供支持。BSD socket为app提供底层的通信机制，但是高级接口也存在。通过提供一种通知及连接TCP/IP之上的网络服务的动态方式，Bonjour简化了用户交流的体验(networking experience)。

为OS X底层环境的详细信息，可参考Mac Technology Overview中的Kernel and Device Drivers Layer一节。
### 1.3.2  并发与线程化
每个进程都以一个单一的线程开始，而后根据所需创建更多线程。虽然可以利用POSIX和其它高级接口来直接创建线程，但是对大部分工作而言，利用block对象与Grand Central Dispatch(GCD)或operation对象非直接地创建它们更好！其中，operation对象是用NSOperation类实现的一种Cocoa并发技术。

GCD与operaion对象是针对原始线程的一种替代选择，它们可以简化或者消除与线程化编程相关的诸多问题，比如同步和加锁。尤其是，它们定义了一个异步编程模型；在此模型内，你可指定想执行的工作以及执行它的顺序。系统随即处理需要调度必需线程的繁杂工作，以及在当前硬件上尽可能高效地执行你的任务。对于时间敏感性的数据处理任务，你不应使用GCD或者operation，但是，你可以利用它们处理大部分其它类型的任务。

关于在app中使用GCD和operation对象实现并发操作的更多信息，可以参考Concurrency Programming Guide。
### 1.3.3  文件系统
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
### 1.3.4  安全性
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
# 二、The Core App Design
为释放OS X的能量，你使用Cocoa应用环境开发apps。Cocoa呈现app的用户界面，并将它与操作系统的其它组件紧密集成起来。Cocoa提供一个面向对象的软件组件的集成套件，这些软件组件被打包进两个核心类库，AppKit和Foundation框架，还有许多提供支持技术的基础框架。Cocoa类是可重用及可扩展的，你可以使用它们作为或者为某些特定要求扩展它们。

创建这样的app，它采用了所有的约定及显露OS X的全部能量；Cocoa让这件事变得容易起来。实际上，无须添加任何代码，你便可以在Xcode创建一个新Cocoa应用，此应用具备些许功能。这样的一个app能够显示它的窗口(或创建新文档)以及实现很多标准的系统行为。虽然Xcode模板提供了一些使之发生的代码，但是它们提供的代码量是很少的。此中大部分行为是Cocoa自己提供的。

为制作一款好用的app，你应当站在巨人(Cocoa)的肩膀上，充分利用它为你提供的开发惯例及基础设施。为了高效地完成它，理解一个Cocoa应用如何组合在一起是非常重要的。
## 2.1  基本的设计模式
---
Cocoa在它的实现中纳入了很多设计模式。Table2-1列出了你应熟悉的关键设计模式。

<img src="/images/posts/2018-11-11/Table2-1_0.png">
<img src="/images/posts/2018-11-11/Table2-1_1.png">
## 2.2  The App Style Determines the Core Architecture
---
你的app风格规定了你在它的实现中必须使用哪种核心对象。Cocoa支持单窗口及多窗口app的创建。对多窗口设计而言，它还提供一个文档架构来帮助管理与每个app窗口相关联的文件。因此，apps可以有以下类型：
- 单窗口工具型app
- 单窗口库类型app
- 多窗口基于文档的app

在设计阶段，你应当尽早选择一种基本的app风格，因为此风格影响着你稍后要做的每件事。很多情形下，单窗口风格是首选，特别是对从iOS平台转过来的开发者。单窗口风格通常产生一种更为精简的用户体验，它也易于为app提供全屏模式的支持。然而，如果你的app广泛地处理复杂文档，那么多窗口风格可能是首选项，因为它提供更多与文档相关的架构来帮你实现app。

拥自OS X的计算器应用是单窗口工具型app的一个例子。工具型apps通常处理短暂存在的数据或管理系统进程。计算器不会创建或处理任何文档或持久存在的用户数据，仅处理用户在单窗口的文本域输入的数值型数据，以及在相同区域显示它的计算结果。当用户退出app时，它处理的那些数据也被简单地丢弃了。

<img src="/images/posts/2018-11-11/Figure2-1.png">
单窗口、库类型(或“鞋盒”)apps处理那些持久存在的用户数据。一个库类型应用的最突出例子是iPhoto，显示于Figure2-2。iPhoto处理的用户数据是照片及其相关的元数据；应用编辑、显示以及存储它们。所有与iPhoto的用户交互均发生在一个单一的窗口。虽然iPhoto存储数据于文件内，但是它并不呈现这些文件给用户。此app呈现一个简化的界面，以便用户不必为了使用app而管理文件。相反，他们直接操作照片。此外，通过将文件放置于某个单一的package内，iPhoto将它们从Finder内的常规操作中隐去。另外，app在合适的时机将用户的编辑更改保存到硬盘中。如此，用户便从手动保存、打开、或关闭文档的工作中解脱出来了。此简单性是库类型app设计的一个主要优势。

<img src="/images/posts/2018-11-11/Figure2-2.png">
一个多窗口基于文档的app的好例子是TextEdit，它创建、显示、以及编辑那些包含纯文本或样式文本和图像的文档。TextEdit不会组织或管理它的文档；用户利用Finder做这件事。每个TextEdit文档展现在它自己的窗口内，多个文档可同时展现，并且用户利用window工具栏及app菜单栏中的控件与最前端的文档交互。Figure2-3显示了一个创建自TextEdit的文档。关于基于文档app设计的更多信息，可见Document-Based Apps Are Based on an NSDocument Subclass。

<img src="/images/posts/2018-11-11/Figure2-3.png">
单窗口和多窗口app两个都可以呈现一个高效的全屏幕模式，此模式提供一种沉浸式体验，而此体验可使用户专注于他们的工作而不分心(无暇他顾)。关于全屏幕模式，可见Implementing the Full-Screen Experiences。
### 2.2.1  所有Cocoa Apps的核心对象
不管你是使用单窗口还是多窗口风格，所有的apps均使用相同的核心对象集。Cocoa为为大多数这些对象提供默认行为。你可以为这些对象提供一定程度的定制化以实现app的定制行为。

Figure2-4展示了单窗口app格式之核心对象间的关系。根据对象是model、view或controller的哪一部分，他们被分隔开来。正如你从表中看到的，Cocoa提供的对象为app提供了大部分controller和view层。

<img src="/images/posts/2018-11-11/Figure2-4.png">
Table2-2描述了图表中各对象扮演的角色。

<img src="/images/posts/2018-11-11/Table2-2_0.png">
<img src="/images/posts/2018-11-11/Table2-2_1.png">
### 2.2.2  Additional Core Objects for Multiwindow Apps
与单窗口app相反，一个多窗口app使用多个窗口来呈现它的主要内容。对多窗口apps的Cocoa支持构建在一个基于文档的模型周围，此模型由一个称为**document architecture**的子系统实现。在此模型内，每个文档对象管理它的内容，协调自硬盘对内容的读写，以及在窗口内呈现内容。所有的文档对象与Cocoa底层架构合作以协调事件的传输，但是文档之间是相互独立的。

Figure2-5显示了一个多窗口文档app的核心对象间的关系。此图内的很多对象与单窗口app使用的对象相同。主要区别是NSDocumentController的插入，以及在应用对象与管理用户界面的对象之间放置的NSDocument对象。

<img src="/images/posts/2018-11-11/Figure2-5.png">
Table2-3描述了插入的NSDocumentController及NSDocument对象的角色。关于图中其它对象的角色信息，见Table2-2。

<img src="/images/posts/2018-11-11/Table2-3.png">
### 2.2.3  向app集成iCloud支持
不论如何存储app的数据，iCloud是使数据在用户所有的设备均可用的一种便捷方式。为将iCloud集成进app，当存储用户文件时，你做些改变。你存储文件于称为ubiquity container的特殊文件系统处，而非用户的Home文件夹或App沙盒容器内。一个ubiquity container作为对应iCloud存储的本地代表。它位于App沙盒容器的外部，所以需要为app赋予相应的权利才可与之交互。

除了对文件系统位置的更改，你的app设计需要声明你的数据模型是服务于多进程的。以下的考虑事项：
- 基于文档的apps经由NSDocument类获得iCloud支持，此类处理需要管理硬盘上文件的大部分交互。
- 如果你实现了一个定制数据模型及自己管理文件，你必须显式地使用文件协调性以确保你做出的改动可以安全地同步到其它设备中。关于细节，可见File System Programming Guide中The Role of File Coordinators and Prsenters一节。
- 为存储少量数据于iCloud，你可使用键-值(key-value)存储。使用键-值存储为以下事物，诸如股票或天气信息，位置，书签，一个近期文档列表，设置及首选项，以及简单的游戏状态等。每个iCloud app都应充分利用key-value存储的优势。为与key-value存储交互，你使用共享的NSUbiquitousKeyValuesStore对象。

为学习如何在app中采用iCloud，读一读iCloud Design Guide。
### 2.2.4  鞋盒式Apps不应使用NSDocument
当实现一个单窗口、鞋盒式(有时也指“库”风格)app时，最好不用NSDocument对象管理你的内容。NSDocument类被专门设计在多窗口文档app中使用。相反，使用定制的控制器对象管理你的数据。这些定制化控制器将与某个视图控制器或应用的主窗口控制器合作以协调数据的显示。

虽然你通常仅使用一个NSDocumentController对象于多窗口apps，但是你可以继承它并在单窗口app中使用它，以调整Open Recent及类似行为。当继承时，你必须重写与NSDocument对象创建相关的任意方法。
## 2.3  基于文档的Apps建立在NSDocument子类的基础上
---
对存储于文件和iCloud的用户数据而言，文档是容器。在一个基于文档的设计内，app允许用户去创建及管理包含他们数据的文档。一个app通常处理多个文档，每个文档在它自己的窗口内；常常同时展示不止一个文档。比如，一个字处理器(word processor)提供用于创建新文档的命令；它呈现一个编辑环境，在此环境内，用户输入文本或者嵌入图形；它保存数据到硬盘(也可能是iCloud)；它提供其它与文档相关的命令，比如打印及版本管理。在Cocoa，基于文档的app设计由文档架构(document architecture)提供支持，此架构是AppKit框架的一部分。
### 2.3.1  OS X内的文档
可以有多种方式来看待文档。概念上讲，文档就是一个针对信息体的容器，此信息体可被命名及存储在一个硬盘文件或iCloud中。从这个角度说，文档(document)与文件(file)不同；它是位于内存中的一个对象，拥有并管理文档数据。对用户而言，文档是他们点信息，比如文本(text)及格式化在页面上的图形。从编程角度看，一份文档就是一定制NSDocument子类的一个实例，此子类清楚如何呈现这些可显示在窗口的内部持久性数据。此文档对象知道如何从文件读取文档数据；知道如何为文档数据模型在内存创建一个对象图。它也知道如何处理用户的编辑命令以修改数据模型，知道如何将文档数据写回到硬盘。因此，正如在Figure2-6显示的这般，文档对象调解于文档数据的不同表现形式之间。

<img src="/images/posts/2018-11-11/Figure2-6.png">
使用iCloud后，文档可在用户的电脑及iOS设备间自动共享。无须用户的干涉，对文档数据的更改即可自动同步。关于iCloud的信息，可见“向app集成iCloud支持”一节。
### 2.3.2  文档架构义务提供许多能力
当你设计app时，基于文档的风格是你应当考虑的一种设计选择。如果创建这样的多个离散数据集对用户意义非常，其中，每个数据皆可在图形化环境编辑，在文件或iCloud存储，此时，你的确应当计划开发一款基于文档的app。

Cocoa文档架构为基于文档的app提供了一个框架，执行以下任务：
- **Create new documents.** 用户首次选择保存一个新文档时，它呈现一个对话，允许用户在一个用户选定的位置重命名并保存文档于一个硬盘文件中。
- **Open existing documents stored in files.** 基于文档的app指定它可读可写的文档类型。它可于内部呈现不同形式的数据，并可恰当地显示这些数据。它也可以关闭文档。
- **Automatically save documents.** 基于文档的app可以采取原地自动保存，它的文档可在合适的时机自动保存，以便用户在屏幕看到的数据与硬盘中保存的一致。保存操作可被安全地完成，以便一个中断的保存操作不会致使数据前后不一致。
- **Asynchronously read and write document data.** 读写操作被异步完成于后台线程中，以便长时间操作不会使app的用户界面无响应。除此之外，使用NSFilePresenter协议及NSFileCoordinator类来协调读写操作以减少版本冲突。
- **Manage multiple versions of documents**
- **Print documents** 打印对话和页面设置对话允许用户选择各种不同的页面布局。
- **Monitor and set the document's edited status and validate menu items.**
- **Track changes.** 文档管理它的编辑状态并实现多级的undo和redo。
- **Handle app and window delegation.** 在重大生命周期事件时，比如app终止时，app将发送通知且调用delegate方法。

关于如何实现一个基于文档的app，更多细节可见Document-Based App Programming Guide for Mac。
## 2.4  App的生命周期
---
App生命周期是它从启动到终止的过程。App可通过用户或系统来启动。用户可通过双击App图标，用Launchpad，抑或打开一个类型与App绑定的文件来启动此应用。在OS X v10.7及以后的版本中，当需要恢复用户的桌面到上一个状态时，系统会在用户登录时启动此App。

当App启动后，系统为它创建一个进程和所有与之相关的系统数据结构。在进程内部，它创建一个主线程并用来执行App代码。至此时，App代码接管全部工作且App处于运行中。
### 2.4.1  main函数是App入口点
像任何基于C语言的应用一样，启动期Mac App的主入口点是main函数。在Mac App内，main函数仅被最低限度地使用。它的主要任务是交控制权予AppKit框架。任意在Xcode中创建的新工程都附带一个默认如List2-1所示的main函数。一般来说，你勿需改变其实现。

<img src="/images/posts/2018-11-11/List2-1.jpg">
NSApplicationMain函数初始化app并筹备其运行。作为初始化过程的一部分，此函数得做几件事：
- 创建NSApplication类的一个实例。可用sharedApplication类方法在app内任意处访问此对象。
- 加载Info.plist文件中键为NSMainNibFile的nib文件并实例化此文件内的全部对象。这是app的主nib文件，它应当包含应用delegate及其它任何必须在启动周期早期加载的关键对象。在启动期不需要加载的对象应放置在单独的nib文件中，且稍后需要时再加载。
- 调用application对象的run方法来完成启动并开始处理事件。

等到run方法被调用，应用的主要对象才被加载进内存，但是app仍未完全启动。run方法告知应用delegate应用即将启动，显示应用菜单栏，打开传给应用的任意文件，做一些框架管理工作，然后开启事件处理循环。所有这些工作均发生在应用主线程内。如果对应NSDocument对象的canConcurrentlyReadDocumentsOfType:类方法返回值为**YES**，文件可被打开于第二个线程。

如果应用在启动周期间保留了用户界面，Cocoa会在启动期加载任何被保留的数据并用它重建最后一次打开的窗口。
### 2.4.2  App的Main Event Loop驱动交互
随着用户与app交互，应用的main event loop处理到来的事件并将它们分发给合适的对象来处理。当NSApplication对象初次创建时，它建立一个与系统窗口服务器的连接，窗口服务器收到来自底层硬件的事件后转发给app。app也会建立一个FIFO的事件队列来存储来自窗口服务器的事件。此main event loop随即负责出队及处理队列中正等待的事件，如Figure2-7所示。

<img src="/images/posts/2018-11-11/Cocoa_0.jpg">
<img src="/images/posts/2018-11-11/Figure2-7.png">
NSApplication对象的run方法是main evnet loop的主力。在一个封闭的循环中，此方法执行以下步骤直至app终止：
- 提供window-update通知的服务，此会引起任何标记为dirty的窗口进行重绘。
- 利用nextEventMatchingMask:untilDate:inMode:dequeue:方法出队来自内部event队列中的事件，并将事件数据转化为NSEvent对象。
- 利用NSApplication对象的sendEvent:方法把事件分发给合适的目标对象。

在app分发事件时，sendEvent:方法利用事件的类型来决定合适的目标。总体来说，主要有两大类输入事件，即键盘事件和鼠标事件。key事件被发送给key window，即当前正接受key按压的窗口。鼠标事件被分发给事件发生的窗口。

对鼠标事件而言，窗口首先寻找事件发生处的view并将事件分发给此视图对象。view是响应者对象，可以响应任意类型的事件。如果此view是一个control，它通常用此事件为它的关联target生成一个action消息。
### 2.4.3  App的自动及快速终止提高用户体验
在OS X v10.7及以后的版本中，终止app的Quit命令之使用被弱化了很多，以支持更以用户为中心的技术。尤其是，Cocoa支持两种使app终止更为平滑且迅速的技术：
- Automatic termination为用户消除了退出app的需要。相反，系统在场景背后平滑地管理app终止，自动终止那些没在使用的app以重新声明比如内存这样的被需要资源。
- Sudden termination允许系统立即杀掉app的进程而无需等它执行某些最终的动作。系统用此技术来提高以下操作的速度，诸如退出、重启或关机。

虽然以上二者都被用来提高app终止的用户体验，但是automatic termination和sudden termination是相互独立的技术。虽然Apple推荐app支持此二者，但是一个app可以支持其中之一，也可以都不支持。支持这两项技术的app，在自身不参与的情况下，即可被系统终止。另一方面，如果一个app支持sudden termination而非automatic termination，它必收到一个Quit事件；处理此事件时，无需显示任何用户界面对话窗口。

Automatic termination将管理进程的工作从用户转移到系统，而系统确是处理此类工作的不二之选。用户无需人工管理进程。用户真正需要的是，运行apps以及它们随时可用。在确保系统性能不受损毁的情况下，automatic termination将此变为可能。

Apps必须选择此二者终止方式并为它们提供恰当的支持。在这两种情况下，app必须确保终止发生前用户数据已保存。因为用户不会退出一个可自动退出的app，这样一个app也应当利用内建的Cocoa支持保存用户界面的状态。保存及恢复界面状态为用户提供一种连续感。

关于如何在app中支持automatic termination，可见“自动终止(Automatic Termination)”。关于如何支持sudden termination，见“快速终止(Sudden Termination)”。
## 2.5  在App中支持关键的运行时行为
---
无论你正创建的app是什么风格，都存在一些所有app均应支持的特定行为。这些行为的目的是帮助用户聚焦它们正创建的内容，而不是关注app管理及其它并非内容创建的繁琐工作。
### 2.5.1  自动终止(Automatic Termination)
自动终止是你必须在app中显式编码的一个特征。声明对自动终止的支持容易，但是app需要与系统合作来保存用户界面的当前状态，以便随后根据所需再恢复它们。系统可随时为自动终止app杀掉底层的进程，因此保存此类信息可为app保持感官上的连续性。通常情况下，在用户关掉app的全部窗口一段时间后，系统杀掉此app的底层进程。不过，如果app当前未在屏幕显示，可能因用户将其隐藏了或者切换到其它空间了，无论什么原因，系统也可能杀掉这种带窗口的app。

为支持自动终止，你应当完成以下几件事：
- 声明应用对自动终止对支持，可编程实现或者使用一个Info.plist键。
- 支持保存与恢复你的窗口配置(window configuration)。
- 在合适的时机保存用户的数据。
``` Objective-C
<1> 单窗口、库类型app应当在合适的检查点为保存数据实现响应的策略。
<2> 多窗口、文档型app可以使用NSDocument的自动保存能力。
```
- 尽可能也为你的app提供快速终止(Sudden Termination)。

<br/>
#### Enabling Automatic Termination in Your App
对自动终止的支持声明让系统清楚它应当在合适的时机管理app的实际终止事务。应用有两种声明的方式：
- 在应用的Info.plist文件内包含NSSupportAutomaticTermination键(对应值为YES)。此举设置app的默认支持状态。
- 使用NSProcessInfo类动态地声明对自动终止的支持。使用这项技术更改app(在Info.plist文件包括NSSupportAutomaticTermination键)的默认行为。

<br/>
#### Automatic Data-Saving Strategies Relieve the User
你应当时常避免用户手动保存对数据的更改。相反，去实现自动数据保存。对一个基于NSDocument的多窗口app而言，自动保存是非常简单的，重写autosavesInPlace类方法返回YES即可。有关更多信息，可参见Document-Based App Programming Guide for Mac。

对一个单窗口、库类型app，在代码内确定保存恰当的点(where any user-related changes should be saved)以及将这些更改自动写入硬盘。通过消除总想手动保存更改的需要，此举使用户受益；完成后，如有问题发生，可确保用户不会丢失太多的数据。

你可以自动保存数据的几个时机为：
- 当用户关闭app窗口或者退出app时(applicationWillTerminate:)
- 当app停止工作时(applicationWillResignActive:)
- 当用户隐藏app时(applicationWillHide:)
- 每当用户对app数据做出有效更改时

最后一条意味着你有随时保存用户数据的自由。比如，如果用户正在编辑数据记录字段(fields of a data record)，当任一字段值改变时，你可以保存它；或者你可以等到用户显示新字段时，再保存全部字段。此类增量更改确保了数据总是最新的，但是也需要更细粒度地管理数据模型。在这样的一个实例中，Core Data可帮你更容易地做出改变。关于Core Data的信息，可见Core Data Starting Point。
### 2.5.2  快速终止(Sudden Termination)
快速终止让系统知道，在没有app任何额外的参与下，它可被系统直接杀掉。支持快速终止的好处是让系统更快速地关闭app；当用户在关机或退出时，此举非常重要。

app有两种方式以声明对快速终止的支持：
- 在app的Info.plist文件内包含NSSupportsSuddenTermination键(值为YES)。
- 使用NSProcessInfo类动态地声明对快速终止的支持。也可以使用此类来更改app的默认支持，即app已在Info.plist包含NSSupportsSuddenTermination键的前提下。

一种方案是整体地声明对此功能的全局性支持，随即在恰当的时间点手动重写此行为。因为快速终止意味着在app启动后，系统可以随时杀掉它；当执行某些可导致数据损毁的行为时，你应当废除此功能。在此行为完成后，再重新启用此功能。

可利用NSProcessInfo类的disableSuddenTermination及enableSuddenTermination方法程式化地启用和终止快速终止(Sudden Termination)。相应地，这些方法增加或减少由进程维护的计算器(值)。当此计数器的值为0时，进程符合快速终止的标准。当此值大于0时，快速终止被禁止。

动态地启用与禁止快速终止也意味着，你的app应当不断地保存数据，并且不能仅依赖用户动作来保存重要信息。确保app数据能被保存的最好方式为to support the interfaces in OS X v10.7 for saving your document and window state.这些界面帮助相关用户及app信息的自动化保存。关于保存用户界面状态的更多信息，可见"用户界面保存(User Interface Preservation)"。关于保存文档的更多信息，见"基于文档的Apps建立在NSDocument子类的基础上(Document-Based Apps Are Based on an NSDocument Subclass)"。

关于启用和禁止快速终止的额外信息，见NSProcessInfo Class Reference。
### 2.5.3  用户界面保存
在OS X v10.7及以后的版本中，Resume特性保存app窗口的状态，以及在随后的app启动中恢复它们。对窗口状态的保存允许你将app返回到用户最后使用时的状态。如果你的应用支持自动终止，尤其应使用Resume特性；因为自动终止条件下，当app运行于对用户隐藏的情况下，可引发app被系统终止。如果你的app支持自动终止，但是未保存它的界面，app进入它的默认状态。仅转离你app的用户可能会以为此app崩溃了。
#### Writing Out the State of Your Windows and Custom Objects
你必须完成以下几件事来保存用户界面的状态：
- 对每个窗口，你必须使用setRestorable:方法设置此窗口是否应被保存。
- 对每个被保存的窗口，你必须执行一个对象，此对象的工作是在启动期重建此窗口。
- 用户界面包含的任意对象必须誊写(write out)那些为稍后恢复状态的数据。
- 在启动期，必须用提供的数据来修复对象到前一个状态。

誊写(write out)应用数据到硬盘以及稍后恢复的时机过程均由Cocoa处理，但是你必须告诉Cocoa保存什么。app的窗口是所有保存操作的起点。Cocoa遍历所有的app窗口，为其中isRestorable方法返回值为YES的窗口保存数据。在默认情况下，绝大多数窗口是是被保存的，但是你可使用setRestorable:方法更改一个窗口的保存状态。

除了保存窗口，Cocoa也为与窗口相关的大部分响应者对象保存数据。特别是，它保存与窗口相关联的视图及窗口控制器对象。(对一个多窗口文档型app而言，窗口处理器也会保存来自相关文档对象的数据。)Figure2-8展示了当决定保存哪个对象时，Cocoa采取的路径。窗口(window)对象通常是起始点，但是，其它相关的对象也被保存。

<img src="/images/posts/2018-11-11/Figure2-8.png">
所有的Cocoa窗口和视图对象保存关于它们大小及位置的基本信息，另外加上可能影响当前显示方式的其它属性信息。比如，一个tab视图保存被选中标签(selected tab)的索引(index)，一个文本视图保存当前选中文本的位置及范围。然而，这些响应者对象并没有关于app数据结构的固有知识(即它们并不了解app自身的业务逻辑)。因此，保存app数据及任意其它额外(恢复窗口到当前状态的)信息是你的责任。这有几处位置，你可在其中誊写特定的状态信息：
- 如果你继承了NSWindow或者NSView，在子类中实现encodeRestorableStateWithCoder:方法并用它誊写(write out)任意相关的数据。
或者，你的自定义响应者对象可以重写restorableStateKeyPaths方法，并用它为需要保存的任意属性指定key path。Cocoa使用key path来定位且保存相关属性的数据。属性必须符合key-value coding及key-value observing。
- 如果你的窗口有一个delegate对象，为此对象实现window:willEncodeRestorableState:方法并用它存储任意相关的数据。
- 在你的窗口控制器(window controller)，使用encodeRestorableStateWithCoder:方法保存任意相关的数据或配置信息。

在决定保存什么数据时，要慎重；争取誊写(write out)最小数量的信息，这些信息都是重新配置窗口及相关对象所需。你应当保存窗口显示的实际信息，以及保存充足的信息以重新将窗口衔接到相同数据对象上。
``` Objective-C
注意：不要使用用户界面保存机制作为保存app实际数据的方式。
为界面保存而创建的存档文件可时常变化；
在恢复过程中，如果出现问题，这些存档文件可能一起被忽略掉了。
app数据应当独立存储于app管理的data文件内。
```
关于如何利用coder对象来归档状态信息，参见NSCoder Class Reference。关于在多窗口基于文档的app为保存状态而需要的额外信息，见Document-Based App Programming Guide for Mac。
#### Notifying Cocoa About Changes to Your Interface State
每当某个响应者的保存状态改变时，通过调用此对象的invalidateRestorableState方法将其标记为dirty。完成之后，在将来的某个时间点，encodeRestorableStateWithCoder:消息便会发送给你的响应者对象。将响应者对象标记为dirty可让Cocoa明白它需要在恰当的时机将它们的保存状态写入硬盘。将对象设定为无效是一项轻量级操作，因为数据不会立即写入硬盘。相反，(这些)改变会合并起来，在关键时刻完成写入，比如，当用户切换到其它app或退出时。

只有改变是与用户界面真正相关时，你才应当将一个响应者对象标记为dirty。举例来说，当用户选择了一个不同标签(tab)时，标签视图(tab view)标记自身为dirty。然而，你不必因许多内容相关的变化而使窗口或视图无效；除非内容变化引起了the window to be associated with a completely different set of data-providing objects。

如果你用restorableStateKeyPaths方法来声明想保存的属性，Cocoa保存及恢复响应者对象的这些属性值。因此，你提供的任何key path应当时key-value观测(observing)一致的，且形成恰当的通知。关于如何在对象内支持key-value观测的更多信息，见Key-Value Observing Programming Guide。
#### 在启动期恢复窗口及自定义对象
作为app正常启动周期的一部分，Cocoa检查是否存在保存下来的界面数据。若有，Cocoa用此数据尽力重新创建app的窗口。每个窗口必须指定一个恢复类(restoration class)；此类了解窗口，也能在Cocoa要求时在启动期代表自身去创建窗口。

此恢复类负责创建窗口以及此窗口需要的全部重要(critical)对象。对绝大多数app类型，恢复类通常也创建一或多个控制器对象。举例而言，在一个单窗口app内，恢复类可能会创建用于管理window的窗口控制器，随即从此对象取得(retrieve)窗口。因为它也创建这些控制器对象，你应当为恢复类使用一个更高级的应用(application)类。一个应用(app)可能利用应用委托(application delegate)，文档控制器，甚至(一个)窗口控制器作为恢复类(restoration class)。

在启动周期，Cocoa依下列各项恢复每个被保存的窗口：

1.Cocoa从被保存数据处取得窗口的恢复类，并调它的**restoreWindowWithIdentifier:state:completionHandler:**类方法。

2.**restoreWindowWithIdentifier:state:completionHandler:**类方法必须调用提供的  completion handler with the desired窗口对象。为达成此目的，须完成以下：
- 它创建任何可能用于显示窗口的相关控制器对象(包括窗口控制器)。
- 如果控制器对对象已经存在(可能因为它们已从nib文件中加载),此方法从这些存在的对象处得到窗口(window)。

如果窗口不能被创建，可能因为相关的文档已被用户删除，**restoreWindowWithIdentifier:state:completionHandler:**应传递一个error对象给completion handler。

3.Cocoa使用返回的窗口来恢复它及任意被保存的响应者对象到它们以前的状态。
- 在没有额外帮助下，标准的Cocoa窗口及视图对象便恢复到它们以前的状态。如果子类化(继承)了NSWindow或NSView，则实现restoreStateWithCoder:方法以恢复任意自定义状态。如果你在自定义的响应者对象实现了restoreStateKeyPaths方法，Cocoa自动将相关属性值设置为它们的保存值。因此，你不必实现restoreStateWithCoder:来恢复这些属性。
- 对window delegate对象而言，Cocoa调用window:didDecodeRestorableState:方法来恢复此对象的状态。
- 对窗口控制器而言，Cocoa调用restoreStateWithCoder:方法以恢复它的状态。

当重建每个窗口时，Cocoa向恢复类传递窗口对象的唯一标识符字符串。在保存窗口状态之前，你负责向窗口指派用户界面标识符字符串。你可以在窗口的nib文件内指派一个标识符，或者通过设置窗口对象的identifier属性(定义在NSUserInterfaceItemIdentification协议内)。举例来说，你可能给你的preferences窗口一个preferences标识符，并随后在实现中核查它。你的恢复类可以利用此标识符来决定创建哪些窗口及相应的对象。标识符字符串的内容可以是你想的任意字符，但应当是帮你稍后识别窗口的某些字符。

对主窗口控制器及窗口均加载自main nib文件的单窗口app来说，你的恢复类之工作是相当明确的。在这呢，你可以使用application delegate类作为恢复类，实现类似于List2-2(为单窗口app返回主窗口)中展示的restoreWindowWithIdentifier:state:completionHandler:方法。

<img src="/images/posts/2018-11-11/List2-2.png">
## 2.6  应用程序是由许多不同部件构建的
---
核心架构的对象非常重要，但是于app设计而言，并非需要考虑的仅有对象。这些核心对象管理app的高层次行为，但是app视图层的对象承担了大部分工作以显示自定义内容及响应事件。在创建有趣其急剧黏性的app时，其它对象也扮演了重要角色。
### 2.6.1  用户界面
app的用户界面由一个菜单栏，一或几个窗口，以及一或几个视图组成。菜单栏(menu bar)是用户可在app执行的命令仓库。命令可能适用于app as a whole，对当前活动窗口，或者对当前选中的对象。你负责定义app支持的命令，也负责提供事件驱动代码以响应它们。

你利用窗口和视图于屏幕呈现app的可视化内容，来管理与那内容的即时交互。一个窗口(window)是NSWindow类的一个实例。panel时NSPanel类的实例，其中NSPanel是NSWindow的派生类；你可使用panel呈现二级内容。单窗口app有一个主窗口，以及可能有一或多个二级窗口或面板(panels)。多窗口app有多个窗口显示它们的主要内容，也可能有一或多个二级窗口或面板。窗口的风格决定它在屏幕上的外观。Figure2-9展示了菜单栏，连同一些标准窗口及面板。

<img src="/images/posts/2018-11-11/Figure2-9.png">
视图(view)，是NSView类的一个实例，为窗口的一块矩形区域定义内容。视图是呈现内容及与用户交互的主要机制，有几项责任。比如：
- **Drawing and animation support.**视图在它们的矩形区域内绘制内容。支持Core Animation层的视图使用这些层给内容做动画。
- **Layout and subview management.**每个视图管理一个子视图列表，此举允许你创建任意的视图层次。每个视图定义布局及调整大小的行为以适应窗口大小的改变。
- **Event handling.**视图接收事件。视图在适当的时候转发事件给其它对象。

关于创建及配置窗口的信息，见**Window Programming Guide**。关于使用及创建视图层次的信息，见View Programming Guide。
### 2.6.2  事件处理
系统窗口服务器负责追踪鼠标、键盘及其它事件并将它们传递给app。当系统启动应用时，它为此app创建一个进程及一个单一的线程。这个初始线程就变成了应用的主线程。在主线程内，NSApplication对象建立main run loop及配置它的事件处理代码。如Figure2-10展示的那样。

<img src="/images/posts/2018-11-11/Figure2-10.png">
随着窗口服务器传送事件，app将这些事件加入队列，继而在app的main run loop处理它们。处理事件涉及到将事件分发给最适于处理它的对象。比如，鼠标事件通常被分发给事件发生处的视图处。

run loop监视一个指定执行线程的输入源。应用的event queue代表这些输入源中的某一个。当事件队列为空时，主线程会休眠。当一个事件到达后，run loop唤醒主线程并将控制权调配给NSApplication对象以便处理事件。待处理完毕后，控制权返还给run loop，它才能再处理其它事件，处理其它输入源；若无事可做，便将线程放回休眠状态。关于run loop及输入源如何工作的更多信息，可见**Threading Programming Guide**。

分发和处理事件是响应者对象的职责，它们是NSResponder类的实例。类NSApplication,NSWindow,NSDrawer,NSView,NSWindowController及NSViewController皆为NSResponder的派生类。从事件队列(event queue)取出一个事件后，app分发此事件给发生地的窗口对象(window object)，窗口对象依次转发此事件给它的第一响应者。就鼠标事件而言，第一响应者对象通常是触碰发生处的NSView对象。例如，发生在按钮上的鼠标事件就被传递给对应的按钮对象。

如果第一响应者对象不能处理此事件，则转发此事件给它的下一个响应者，下一个响应者可能是父类视图、视图控制器、也或者是窗口对象。如果此级响应者也无法处理此事件，则转发此事件给它的下一层响应者，依次类推，直至事件被处理。此一系列连接起来的响应对象被称为响应链。消息继续前行经过响应链——朝向更高级的响应者对象，比如window controller或者application对象——直到此事件被处理。如果走到尽头，此事件仍未能得到处理，那么它只能被丢弃了。

处理事件的响应者对象经常启动一系列程式化的动作。比方说，控制器对象(NSController的子类)处理事件是通过发送action消息给另一对象，此对象通常是管理当前活跃视图的控制器。当处理action消息时，控制器可能改变用户界面或者调整视图的位置以重绘自身。当此发生时，视图和图形基础设施接管此任务并且以尽可能最高效的方式处理此事件。

若想了解关于响应者、响应链以及处理事件的的更多详情，可见Cocoa Event Handling Guide。
### 2.6.3  图形、绘制及打印
这有两种基本方式，Mac应用可利用绘制它的内容：
- 原生的绘制技术(比如Core Graphics及AppKit)
- OpenGL

原生OS X绘制技术通常利用Cocoa视图和窗口提供的基础设施来渲染及呈现自定义内容。当一个视图首次显示时，系统要求它绘制自身的内容。视图视图自动绘制它们的内容，但是自定义视图必须实现一个**drawRect:**方法。在此方法内部，你利用原生的绘制技术来绘制形状、文本、图像、梯度或其它你想要的任意可视化内容。当你想更新视图(view)的可视化内容时，通过调用视图的**setNeedsDisplay:**或**setNeedsDisplayInRect:**方法以标记其无效！系统随即调用视图的**drawRect:**方法(在合适的时间)提供更新。此周期然后重复且继续贯穿于app的整个生命周期。

如果使用OpenGL绘制app的内容，你仍创建一个窗口及视图来管理你的内容，但是这些内容仅仅为一个OpenGL绘制上下文提供渲染的外表。一旦你拥有了绘制上下文，你的应用负责以合适的间隔初始化绘制更新。

关于如何在视图中绘制自定义内容，见**Cocoa Drawing Guide**。
### 2.6.4  文本处理
Cocoa文本系统，即OS X的主要文本处理系统，负责处理以及Cocoa中所有可视化文本的显示。它通过与文本相关的AppKit类提供一整套高质量的排版服务；AppKit类允许应用创建、编辑、显示以及存储文本with all the characteristics of fine typesetting。

Cocoa文本系统提供全部这些基本及高级的文本处理features，它也满足来自日益互联的计算世界的额外要求：对世界所有现存语言的字符集支持，处理多种文本方向性及非矩形文本容器的强劲布局能力，以及复杂的诸如字距调整、段落间隔控制等排字能力。Cocoa的面向对象文本系统被设计用于提供所有这些能力without requiring you to learn about or interact with more of the system than is necessary to meet the needs of your app。

Cocoa文本系统基础是Core Text，它向如Cocoa及WebKit高层引擎提供底层的基本文本布局及字体处理能力。Core Text为许多Cocoa文本技术提供实现。App开发者通常不必直接使用Core Text。然而，Core Text API向必须直接使用它的开发者公开，比如利用他们自己的布局引擎编写apps的开发者，以及将旧有基于ATSUI或QuickDraw代码库移植到现代世界的开发者们。

关于Cocoa文本系统的更多信息，见Cocoa Text Architecture Guide。
## 2.7  实现应用菜单栏
---
类NSMenu及NSMenuItem是所有类型菜单的基础。一个NSMenu实例管理一个菜单项集合，并(一个在另一个下方地)绘制它们。一个NSMenuItem实例代表一个菜单项，它封装NSMenu对象需要绘制及管理它的所有信息，但是它自身不做绘制或事件处理。你通常使用Interface Builder来创建和修改任意类型的菜单，所以通常没有编写代码的需要。

当app位于最前端时，应用菜单栏横亘于屏幕顶部，替换任意其它app的菜单栏。app的全部菜单归一个NSMenu实例所有，此实例是由app启动时创建的！
### 2.7.1  Xcode模板提供菜单栏
Xcode的Cocoa应用模版在一个称为MainMenu.xib的nib文件内提供那个NSMenu实例。这个nib文件包含一个application菜单(以app名字命名)，一个File菜单(以及全部关联命令)，一个Edit菜单(与文本编辑命令及Undo和Redo一起)，Format，View，Window以及Help菜单(与它们自己的代表命令的菜单项一起)。这些菜单项均被关联到适当的第一响应者action方法。举例来说，About菜单项被关联到File's Owner的orderFrontStandardAboutPanel:方法，此File's Owner显示一个标准About窗口。

模板有为Edit、Format、View、Window以及Help菜单的类似现成的连接。如果没有提供相应动作，你应从nib中移除关联的菜单项(或菜单)。或者，你可能想重新目的化和重命名菜单命令及动作方法以适合自己的app，则利用模板内的菜单机制以确保各司其职(everything is in the right place)。
### 2.7.2  关联菜单项到代码或第一响应者对象
对app的尚未关联到对象内action方法的自定义菜单项或nib文件内的占位符对象而言，有两种常用的技术来处理Mac app的菜单命令：
- 关联相应的菜单项到一个第一响应者方法。
- 关联菜单项到自定义应用对象或应用委托对象的一个方法。

这两项技术，假定许多菜单命令作用于当前文档或它的内容(响应链的部分)，第一项则更常用。第二项技术主要用于处理对app而言的全局性命令，比如显示preference，或创建一篇新文档。对一个自定义应用对象或它的delegate而言，分发事件给文档是可能的，但是此举通常比较麻烦且易于出错。除了实现action方法以相应菜单命令，你也必须实现NSMenuValidation协议的方法以使能这些菜单项for those commands。

在Xcode的Designing User Interfaces，有关联菜单项到代码内action方法的一步步指令，有关菜单生效及其它菜单话题的更多信息，见Application Menu and Pop-up List Programming Topics。

<br/>
# 三、实现全屏幕体验

## 3.1  NSApplication中的全屏幕API
---

## 3.2  NSWindow中的全屏幕API
---

## 3.3  NSWindowDelegate协议中的全屏幕API
---

<br/>
# 四、支持常见的App行为

## 4.1  You Can Prevent the Automatic Relauch of Your App
---

## 4.2  Making Your App Accessible Enables Many Users
---

## 4.3  为定制化提供用户偏好设置
---

## 4.4  Integrate Your App with Spotlight Search
---

## 4.5  Use Services to Increase Your App's Usefulness
---

## 4.6  为高分辨率优化
---

### 4.6.1  考虑点，而非像素

### 4.6.2  为图形提供高分辨率版本

### 4.6.3  Use High-Resolution-Savvy Image-Loading Methods

### 4.6.4  使用支持高分辨率的API

## 4.7  Preparce for Fast User Switching
---

## 4.8  利用Dock
---

<br/>
# 五、Build-Time配置细节

## 5.1  配置Xcode工程
---

## 5.2  The Information Property List File
---

## 5.3  OS X应用Bundle
---

## 5.4  国际化App
---

<br/>
# 六、Tuning for Performance and Responsiveness

## 6.1  加速启动App
---

### 6.1.1  推迟初始化代码

### 6.1.2  简化Main Nib文件

### 6.1.3  最小化全局变量

### 6.1.4  在启动期最小化文件访问

## 6.2  不要阻塞主线程
---

## 6.3  减小代码的体积
---

### 6.3.1  编译器级别的优化

### 6.3.2  使用Core Data处理大数据集合

### 6.3.3  清除内存泄漏

### 6.3.4  Dead Strip Your Code

### 6.3.5  Strip Symbol Information
