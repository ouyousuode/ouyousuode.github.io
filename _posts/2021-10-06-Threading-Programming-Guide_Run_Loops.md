---
layout: post
title: 「Apple文档」Threading Programming Guide之Run Loops
---
{{page.title}}
========================

Run Loop是与线程相关之基础设施的一部分。一个run loop就是一个事件处理循环，用于调度任务及协调到来事件的接收。run loop的目的是，在有工作要做时保持线程忙碌，当无事可做时让线程进入休眠状态。<br/>

Run Loop管理并非完全自动的。你仍(必)须设计线程的代码以便在合适的时间启动run loop并响应到来的事件。Cocoa和Core Foundation均提供run loop对象来助你配置和管理线程之run loop。你的应用程序无需显式地创建这些对象；包括应用程序之**主线程**在内的每个线程都有一个相关联的run loop对象。然而，只有辅助线程需显示地把它们的run loop运行起来。作为应用程序启动过程的一部分，应用程序框架会自动设置并于**主线程**之上运行run loop。<br/>

以下部分提供了关于run loop的更多信息，以及如何为应用程序配置它们。有关run loop对象的其它信息，可见[NSRunLoop Class Reference](https://developer.apple.com/documentation/foundation/nsrunloop)及[CFRunLoop Reference](https://developer.apple.com/documentation/corefoundation/cfrunloopref?language=objc)。<br/>

## 3.1 剖析Run Loop
其实，run loop字面本身就可以表达其意了。它是线程进入的一个循环，并用于运行事件处理程序(handler)来响应到来的事件。你的代码需提供用于实现run loop之实际循环部分的控制语句——换言之，你的代码提供驱动run loop所需的**while**或**for**循环。在循环内，使用run loop对象「运行」对应的事件处理代码，该代码接收事件并调用已安装的处理程序。<br/>

run loop从两种不同类型的源头接收事件。输入源(input source)传递异步事件，通常是来自另一个线程或另一不同应用程序的消息。定时器源(timer source)提供同步事件，它们在预定的时间或以重复的间隔发生。两种类型的源都使用特定于应用本身的处理程序来处理到来的事件。<br/>

图3-1展示了run loop的概念结构及各种输入源。输入源将异步事件传递给相应的处理程序(handler)，并触发`runUntilDate:`方法(在线程的关联`NSRunLoop`对象上调用)退出。定时器源将事件交付给它们的处理程序后，并不会导致run loop退出。<br/>

<img src="/images/posts/2021-10-06/Figure_3-1.png"> <br/>
除了处理输入源之外，run loop还生成有关run loop之行为的通知。已注册的run-loop观察者可以接收这些通知，并使用它们在线程上执行额外的处理工作。可利用**Core Foundation**在线程上安装run-loop观察者。<br/>

接下来的几节内容提供有关run loop之组件及其运行模式的更多信息，此外，还描述了事件处理期间在不同时间点生成的各类通知。<br/>
