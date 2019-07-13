---
layout: post
title: 第21章 Operations
---
{{page.title}}
================================

为获得当今计算机系统的最佳性能，需要并行编程，此举便经常意味着使用线程。线程编程是困难且易于出错的，这就使得充分利用当今计算机的计算优势变得更为困难。

Mac OS X 10.5引入了一些简化并行编程的类。**NSOperation**类代表一个并行化工作单元，**NSOperationQueue**类接管**NSOpertaion**并执行它们。虽然“queue”暗示着先进先出的执行顺序，但是**NSOperationQueue**更加灵活，因为你可以指定操作(operation)间的依赖关系。一个operation可以等着其它operation结束后，再执行。你也可以指定operation优先级，以此允许某一类的operation总是能能先于其它类执行。

在Mac OS X 10.5，有并发与非并发两类operation。非并发operation触发**NSOperationQueue**在它自己的线程中运行operation，然后多个并发的operation可活跃于一个单一线程中。

队列运作**NSOperation**。如果一个operation想将某线程据为己有，那么它就无法同其它operation并发地运行于相同的线程中；因此，它是一个非并发operation。类似地，如果一个operation可与其它operation同时运行(可能它们正做异步I/O操作)，此即它们可并发地运行于相同线程。

在Mac OS X 10.6，所有的操作均为**非并发的**。实际上，并发标记被**NSOperationQueue**忽略了。每个operation都将启动于它自己的线程。如果你有一些写于Leopard的并发操作(operation)却编译进了10.6及以后版本的应用中，会有意想不到的结果。iOS 4有Mac OS X 10.6的行为表现。

除了并发与非并发之区别，还有simple-lifetime和complex-lifetime操作。simple-lifetime operation重写**-main**方法，完成它们的任务，直到跳出此方法。**-main**一旦返回，operation queue既假设你的operation已完成，将会调度下一个去运行。对Mac OS X 10.5而言，所有的非并发operation都是simple operation。

complex-lifetime operation让你控制operation的完整生命周期。在此，重写**-start**而非**-main**。你可以退出**-start**，但是此operation仍被认为在运行。operation有一个**KVO**组件，随complex-lifetime opeartion而来的是，你负责履行**KVO**协议。实现complex-lifetime operation的细节会稍后讨论。对Mac OS X 10.5而言，所有的并发操作(多个操作共享同一个线程)皆complex-lifetime。

## 21.1  Simple-Lifetime Operations

### 21.1.1  NSOperationQueue

### 21.1.2  Threading issues

## 21.2  MandelOpper

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
