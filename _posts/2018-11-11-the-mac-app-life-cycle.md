---
layout: default
title: macOS App生命周期
---
{{page.title}}
===================

App生命周期是它从启动到终止的过程。App可通过用户或系统来启动。用户可通过双击App图标，用Launchpad，抑或打开一个类型与App绑定到文件来启动此应用。在OS X v10.7及以后的版本中，当需要恢复用户的桌面到上一个状态时，系统会在用户登陆时启动此App。

当App启动后，系统为它创建一个进程及所有标准的系统相关的数据结构。在进程内部，它创建一个主线程并用它来执行App代码。至此时，App代码接管全部工作且App处于运行中。

### main函数是应用入口点
像任意基于C语言的应用一样，启动时Mac App的主要入口点是main函数。在Mac App内，main函数仅被最低限度地使用。它的主要任务是交控制权予AppKit框架。任意在Xcode中创建的新工程都附带一个默认如下所示的main函数。一般来说，你勿需改变其实现。
```Objective-C
#import <Cocoa/Cocoa.h>

int main(int argc, const char * argv[]) {
    return NSApplicationMain(argc, argv);
}
```
NSApplicationMain函数初始化app并筹备其运行。作为初始化过程的一部分，此函数得做几件事：

* 创建NSApplication类的一个实例。可用sharedApplication类方法在app内任意处访问此对象。
* 加载Info.plist文件中键为NSMainNibFile的nib文件并实例化此文件内的全部对象。这是应用的主nib文件，它应当包含应用delegate及其它任何必须在启动周期早期加载的关键对象。在启动期不需要的对象应放置在单独的nib文件中且稍后加载。
* 调用application对象的run方法来完成启动并且开始处理事件。

直到run方法被调用，应用的主要对象被夹在进入内存，但是app仍未完全启动。run方法告知应用程序delegate应用即将启动，显示应用程序菜单栏，打开传给应用的任何文件，做一些框架管理工作，然后开启事件处理循环。所有这些工作发生在应用主线程内。如果对应NSDocument对象的canConcurrentlyReadDocumentsOfType:类方法返回值为YES，文件可被打开于第二个线程。

如果应用在启动周期间保留了用户界面，Cocoa会在启动期加载任何被保留的数据并用它来重建最后一次打开的窗口。
### 应用的Main Event Loop驱动交互
随着用户与App交互，应用的main event loop处理到来的事件并把它们分发给合适的对象以处理。当NSApplication对象初次创建时，它建立一个与系统窗口服务器的连接，窗口服务器收到来自底层硬件的事件后将其转发给App。App也会建立一个FIFO的事件队列来存储发自窗口服务器的事件。此main event loop随即负责出队及处理队列中正等待的事件，如下图所示。
<img src="/images/posts/2018-11-11/main event loop.png">
NSApplication对象的run方法是main event loop的主力。在一个封闭的循环中，此方法执行以下步骤直至App终止：

1.提供window-update通知的服务，此会引起任何标记为dirty的窗口的重绘。

2.利用nextEventMatchingMask:untilDate:inMode:dequeue:方法出队来自内部event queue中的事件，并将事件数据转化为NSEvent对象。

3.利用NSApplication对象的sendEvent:方法把事件分发给合适的目标对象。

当app分发事件时，sendEvent:方法利用利用事件的类型来决定合适的目标。总体来说，主要有两大类输入事件，即key events和mouse events。key事件被发送给key window，即当前正接受key按压的窗口。mouse事件被分发给事件发生的窗口。

对mouse事件而言，窗口首先寻找事件发生处的view并将事件分发给此对象。View是响应者对象，可以相应任意类型的事件。如果此view是一个contorl，它通常用此事件为它的关联target生成一个action消息。
### 事件处理
正如前文所言，系统窗口服务器负责追踪mouse、键盘及其它事件并将它们传递给App。当系统启动应用时，它为此App创建一个进程及一个单一的线程。这个初始的线程就变成了应用的主线程。在主线程内，NSApplication对象建立main run loop及配置它的事件处理代码，如下图展示的那样。

<img src="/images/posts/2018-11-11/processing events in the main run loop.png">
随着窗口服务器传送事件，App将它们入队继而在App的main run loop中处理它们。处理事件涉及到将事件分发给最适于处理它的对象。比如，mouse事件通常被分发给事件发生的view处。

 >  run loop监视一个指定执行线程上的输入源。应用的event queue代表这些输入源中的某一个。当event queue为空时，主线程会休眠。当一个事件到达后,run loop唤醒主线程并将控制权分给NSApplication对象以便处理此事件。待处理完毕，控制权返还给run loop，它才能再处理其它事件，处理其它输入源，将线程放回休眠状态(若无事可做)。
 
 分发和处理事件是响应者对象的职责所在，它们是NSResponder类的实例。类NSApplication,NSWindow,NSDrawer,NSView,NSWindowController及NSViewController均为NSResponder的派生类。在从event queue取出一个事件后，app分发此事件给发生地的窗口对象(Window object)，窗口对象依次转发此事件给它的第一响应者。就mouse事件而言，第一响应者通常是触碰发生处的NSView对象。例如，发生在按钮上的mouse事件被传递给对应的按钮对象。
 
 如果第一相应者不能处理此事件，它就转发此事件给它的下一个响应者，下一个响应者可能是父类视图，视图控制器，也或者是窗口对象。如果此级响应者也无法处理此事件，它转发此事件给它的下一个响应者，如此类推，直至事件被处理。此一系列连接起来的响应对象被称为响应链。消息继续前行经过响应链——朝向更高级的响应者对象，比如window controller或者Application对象——直到此事件被处理。如果走到头，此事件都未能得到处理，那么它只有被丢弃了。
 
 处理事件的响应者对象经常启动一系列程式化的动作。比方说，控制器对象(NSControl的子类)处理事件是通过发送action消息给另一对象，此对象通常是管理当前活跃视图(View)的控制器。当处理action消息时，控制器可能改变用户界面或者调整视图的位置以重绘自身。当此发生时，视图和图形基础设施接管此任务并且以尽可能最高效的方式处理此事件。
 
 当然，若想了解关于responder,responder chain及处理事件的更多详情，可参见Cocoa Event Handling Guide.
 
 




















