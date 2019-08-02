---
layout: post
title: 第20章 多线程
---
{{page.title}}
===========================
多线程是在你的应用中达成并发效果的另一个方法。虽然多进程使用多个独立的进程并带有它们自己的地址空间及资源，线程却是运行于一个单一应用内的多执行流。此应用有单一的地址空间，这些线程则共享应用的可用资源。

像多进程一样，多线程也可利用多CPU的优势。你也可以用之来简化某种编程工作。每个线程可以继续它自己的快乐生活，计算数值以及调用函数；同时，其它线程们皆可独立运行并相互无干扰。线程的一个非常常见的用途是在网络服务器中处理请求(如一台web服务器)。一个新链接被**accept()**之后，则创建一个线程来处理此请求。此线程可随即利用**read()**来获得请求并**write()**以发送数据回去。它也可以打开文件以及执行loop循环，因此没有必要利用**select()**多路复用I/O，也没有必要经由曲线救国的方式来一丢丢地进行计算。
## 20.1 Posix线程
Mac OS X使用Posix thread API,更广为人知的是"pthreads"，作为它的原生线程模型。不幸的是，pthreads与Unix API的其它部分在报告错误条件方面有不同的约定。尽管如你所期望，它在成功时返回零，但是，失败时返回错误码，而非返回-1及设置errno。
### 20.1.1 创建线程
**pthread_create()**用于创建一个新线程：
<img src="/images/posts/2019-02-25/pthread_create.png">
此函数成功时返回零，失败时返回一个错误值。如下时它采用的参数：
- threadID  指向一个pthread_t的一枚指针。新线程的threadID写于此处。
- attr  一组属性值。传入NULL以使用默认属性。不于此处讨论具体的属性值；它们会使对线程化编程的基础变得难懂起来。
- startRoutine  指向带有签名的函数的指针，函数为
``` Objective-C
void *someFunction(void *someArg);
```
这也是线程中执行开始之地。此函数返回了，就意味着线程终止了。someArg是传给**pthread_create()**的参数值。返回值是返回状态的指针。你可为此二者传送你想的任意数据结构。
- arg  给予startRoutine的参数。
<br/>
当某个线程被创建时，系统为此线程创建一个私有栈，类似于main函数调用栈。像Thread stacks图中所示的那样。线程用此栈作函数调用管理及局部变量存储之用。

<img src="/images/posts/2019-02-25/thread_stacks.jpeg">
### 20.1.2 同步

### 20.1.3 互斥锁

### 20.1.4 死锁

### 20.1.5 条件变量
<img src="/images/posts/2019-02-25/webserve-thread_0.png">
<img src="/images/posts/2019-02-25/webserve-thread_1.png">
<img src="/images/posts/2019-02-25/webserve-thread_2.png">
<img src="/images/posts/2019-02-25/webserve-thread_3.png">
<img src="/images/posts/2019-02-25/webserve-thread_4.png">
<img src="/images/posts/2019-02-25/webserve-thread_5.png">
<img src="/images/posts/2019-02-25/webserve-thread_6.png">
<img src="/images/posts/2019-02-25/webserve-thread_7.png">
<img src="/images/posts/2019-02-25/webserve-thread_8.png">
<img src="/images/posts/2019-02-25/webserve-thread_9.png">
<img src="/images/posts/2019-02-25/webserve-thread_10.png">
## 20.2 Cocoa及线程

### 20.2.1 NSThread
### 20.2.2 Cocoa及线程安全
### Objective-C @synchronized块

## 20.3 深入学习：Thread Local Storage
## 20.4 深入学习：读/写锁
