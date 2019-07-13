---
layout: post
title: 第21章 Operations
---
{{page.title}}
================================

为获得当今计算机系统的最佳性能，需要并行编程，此举便经常意味着使用线程。线程编程是困难且易于出错的，这就使得充分利用当今计算机的计算优势变得更为困难。

Mac OS X 10.5引入了一些简化并行编程的类。**NSOperation**类代表一个并行化工作单元，**NSOperationQueue**类接管**NSOpertaion**并执行它们。虽然“queue”暗示着先进先出的执行顺序，但是**NSOperationQueue**更加灵活，因为你可以指定操作(operation)间的依赖关系。一个operation可以等着其它operation结束后，再执行。你也可以指定operation优先级，以此允许某一类的operation总是能能先于其它类执行。

在Mac OS X 10.5，有并发与非并发两类操作(operation)。非并发operation触发**NSOperationQueue**在它自己的线程中运行operation，然后多个并发的operation可活跃于一个单一线程中。

队列运作**NSOperation**。如果一个operation想将某线程据为己有，那么它就无法同其它operation并发地运行于相同的线程中；因此，它是一个非并发operation。类似地，如果一个operation可与其它operation同时运行(可能它们正做异步I/O操作)，此即它们可并发地运行于相同线程。

在Mac OS X 10.6，所有的操作均为**非并发的**。实际上，并发标记被**NSOperationQueue**忽略了。每个operation都将启动于它自己的线程。如果你有一些写于Leopard的并发操作(operation)却编译进了10.6及以后版本的应用中，会有意想不到的结果。iOS 4有Mac OS X 10.6的行为表现。

除了并发与非并发之区别，还有simple-lifetime和complex-lifetime操作。simple-lifetime operation重写**-main**方法，完成它们的任务，直到跳出此方法。**-main**一旦返回，operation queue既假设你的operation已完成，将会调度下一个去运行。对Mac OS X 10.5而言，所有的非并发operation都是simple operation。

complex-lifetime operation让你控制operation的完整生命周期。在此，重写**-start**而非**-main**。你可以退出**-start**，但是此operation仍被认为在运行。operation有一个**KVO**组件，随complex-lifetime opeartion而来的是，你负责履行**KVO**协议。实现complex-lifetime operation的细节会稍后讨论。对Mac OS X 10.5而言，所有的并发操作(多个操作共享同一个线程)皆complex-lifetime。

## 21.1  Simple-Lifetime Operations
如果你有(像)大块计算的同步任务要完成，你会使用一个simple-lifetime operation。你需要做的就是新建一个**NSOperation**的子类并重写**-main**。
``` Objective-C
- (void) main;
```
运行你要的任务，一旦完成，仅是从此方法返回。告诉运行操作的**NSOperationQueue**，指定的operation已完工！这么做就已经足够了！operation可被随时随地地取消，所以方便时你应调查**-isCancelled**:
``` Objective-C
- (BOOL) isCancelled;
```
**NSOperation**的取消机制与pthread API提供的取消有所不同。pthread取消机制会终止线程，但是它被取消时thread可能正处于操作全局数据结构的中间阶段，这就会是一件麻烦事！**NSOperation**不利用线程的取消机制；相反，它依赖operation来注意它是否已被取消。当**-main**方法运行时，你应首先检查**-isCancelled**。纵使操作都被取消了，**NSOperationQueue**通常继续运行它们不误！你可以在operation间添加或移除依赖：
``` Objective-C
- (void) addDependency: (NSOperation *) op;
- (void) removeDependency: (NSOperation *) op;
```
环形依赖是不予支持的。**NSOperationQueue**是支持这些依赖关系的背后实体，所以there is nothing preventing you from manually running an NSOperation before its dependencies run。如果你想看一个特定操作拥有的全部依赖，可一窥它的依赖关系数组：
``` Objective-C
- (NSArray *) dependencies;
```
也可通过操控它的队列优先级来设定一个operation的相对优先级：
``` Objective-C
- (NSOperationQueuePriority) queuePriority;
- (void) setQueuePriority: (NSOperationQueuePriority) p;
```
其中，NSOperationQueuePriority是以下枚举中的一员：
``` Objective-C
enum {
  NSOperationQueuePriorityVeryLow,
  NSOperationQueuePriorityLow,
  NSOperationQueuePriorityNormal,
  NSOperationQueuePriorityHigh,
  NSOperationQueuePriorityVeryHigh
};
```
改变一个operation的优先级不会影响操作系统调度器，所以设置一个非常高的优先级也不会使你的operation分到更多的CPU时间。此值仅被**NSOpertaionQueue**用于决定下一个要执行的操作。

### 21.1.1  NSOperationQueue
**NSOperationQueue**是一个接受**NSOperation**实例并运行它们的类。在Mac OS X 10.5，如果operation说它是一个并发操作，队列就会在当前线程运行此operation。如果操作是非并发的，又或者在10.6，队列可自动创建并管理线程。

利用**-addOperation:**向**NSOperationQueue**添加一个operation：
``` Objective-C
- (void) addOperation: (NSOperation *) op;
```
**-operations**返回当前正运行或处于等待的操作集合：
``` Objective-C
- (NSArray *) operations;
```
默认情况下，**NSOperationQueue**根据处理器的核心数来选择它管理的并发线程数。你可以利用这些调用来微调和查询并发性因素：
``` Objective-C
- (NSInteger) maxConcurrentOperationCount;
- (void) setMaxConcurrentOperationCount: (NSInteger) count;
```
如果决定放弃一个队列正在做的工作，你可以告诉它取消所有操作：
``` Objective-C
- (void) cancelAllOperations;
```
如果想同步地等着所有operation都完成，可调用：
``` Objective-C
- (void) waitUntilAllOperationsAreFinished;
```

### 21.1.2  Threading issues
因为非并发操作运行在它们自己的线程上，你需要认识到在operation上使用的API线程安全性问题。比如，你不可能线程安全地从其它线程设置**NSTextField**的值；因为所有的**UI**操作必须在主线程完成。

一个需要记住的微妙知识点是：当布告一个**NSOperation**或处理一个KVO观察者通知时，当前使用的是哪个线程。如果你打算用一个**NSOperation**或**KVO**来通知其它对象一个operation已经完成了，你需要意识到这个其它对象的通知方法也得运行在运行了operation的那相同线程上，确非主线程。这经常是你不想发生的情况。你可以使用**-performSelectorOnMainThread:**族方法在主线程触发一个通知。
## 21.2  MandelOpper
Mandelbrot集合(曼德布洛特集合是在复平面上组成分形的点集合，一种分形图案，曾被称为“上帝的指纹”，可见何其瑰丽多姿)是一种在程序员群体间令人喜爱的图形化消遣；它是可产生特别酷图像的分形几何构造。生成Mandelbrot集合是一个计算敏感型的计算过程，这就引发了对并行计算的需要。MandelOpper应用会计算一个Mandelbrot集合并将之显示在一个窗口中；它使用simple-lifetime **NSOperation**类的实例来完成实际的计算任务。

<img src="/images/posts/2019-02-04/mandelOpper.png">
### 21.2.1  Bitmap

### 21.2.2  BitmapView

### 21.2.3  CalcOperation

### 21.2.4  MandelOpperAppDelegate

### 21.2.5  NSBlockOperation

## 21.3  Complex-Lifetime Operations

### 21.3.1  KVO属性

## 21.4  ImageSnarfer

### 21.4.1  ImageCanvas

### 21.4.2  SnarfOperation

### 21.4.3  NSURLConnection delegate方法

### 21.4.4  ImageSnarferAppDelegate

### 21.4.5  Mop-up
