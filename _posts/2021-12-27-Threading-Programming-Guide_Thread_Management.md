---
layout: post
title: 「Apple文档」Threading Programming Guide之线程管理
---
{{page.title}}
===============================

OS X和iOS中的每个进程(应用)均由一个或多个线程组成，这其中的每个线程代表应用程序代码之一条执行路径。每个应用程序均从一个单一线程开始，该线程运行应用程序的`main`函数。应用程序可以派生(出)额外的线程，这其中的每个线程则执行一特定函数的代码。<br/>

当应用程序派生出一个新线程时，该线程便成为应用程序进程空间中的一个独立实体。每个线程都有自己的执行堆栈(stack)，并由内核单独为其安排运行时。一线程可以和其它线程及其它进程通信，执行**IO**操作，以及执行你可能需要它完成的任何任务。但是呢，也因为它们在同一进程空间内，所以，单个应用程序中的所有线程共享相同的虚拟内存空间，并享有与进程本身相同的访问权限。<br/>

本章节概述了OS X和iOS中可用的线程技术，并举例证明如何在你自己的应用程序中使用这些技术。<br/>

## 2.1 线程成本
就内存使用和性能而言，线程对你的程序(和系统)来说是有实际成本的。每个线程都需要在内核内存空间(**kernel memory space**)及程序自身的内存空间中(申请)分配内存资源。管理线程和协调其调度所需的核心结构使用wired内存储存在内核中。线程的堆栈(stack)空间及每个线程的数据都存储在程序的内存空间。这些结构中的大多数都是在你首次创建线程时创建和初始化的──由于需要与内核进行交互，该过程的开销可能相对较高。<br/>

Table 2-1量化了在应用程序中创建新的用户级(user-level)线程之大约成本。其中一些开销是可配置的，例如，为辅助线程分配的堆栈空间之大小。创建线程的时间成本是一个粗略的近似，应当只用于相对比较。根据处理器负载情况、计算机速度及可用的系统和程序内存之数量的不同，线程的实际创建时间可能会有较大差异。<br/>

<img src="/images/posts/2021-12-27/Table_2-1.png"> <br/>
**注：**因底层内核之支持，operation对象通常可以更快地创建线程。它们(operation对象)使用已经驻留在内核的线程池来节省分配所用之时间，而非每次都从头创建线程。有关使用operation对象的更多信息，可见Concurrency Programming Guide。<br/>

编写线程代码时，需要考虑的另一个成本是生产成本。设计一个线程化应用程序有时需要对组织应用程序的数据结构之方式进行根本性的更改。为避免使用同步，做出这些更改是必要的，因为同步本身会对设计不佳的应用程序造成巨大的性能损失！设计这些数据结构，并调试线程化代码中的问题，会增加开发线程应用程序所需的时间。然而，如果线程耗费太多时间在等待锁或什么都不做，则避免所提及的这些时间成本的后果，是可能会在运行时产生更大的问题。<br/>

## 2.2 创建线程
创建低级(low-level)线程是相对简单的。在任一情况下，都必须有一个函数或方法作为线程的主入口点，并且必须使用一个可用的线程routines来启动线程。以下部分展示了常用线程技术的基本创建过程。使用这些技术创建的线程会继承一组默认属性，至于是何属性，由你使用的技术决定。有关如何配置线程之信息，见2.3节(配置线程属性/Configuring Thread Attributes)之内容。<br/>

### 2.2.1 使用NSThread创建线程

<img src="/images/posts/2021-12-27/Using_NSThread.png"> <br/>
使用NSThread类创建线程有两种方法：<br/>
- 利用类方法`detachNewThreadSelector:toTarget:withObject:`生成新线程。
- 创建一个新的NSThread对象，并调用它的`start`方法。

这两种技术都会在程序中创建一个**detached**线程，望文生义，此种线程意味着「当线程退出后，系统会自动回收它占用的资源」。这也意味着，你的代码不必在之后显式地与该线程进行连接(join)。<br/>

因为所有版本的OS X均支持`detachNewThreadSelector:toTarget:withObject:`方法，所以它经常出现在使用线程的现有**Cocoa**应用程序中。要**detach**一个新线程，只需提供你想要用作线程入口点的方法之名称(指定为选择器selector)、定义该方法的对象，以及启动时要传递给线程的任何数据。下面的示例展示了此方法的基本调用，它使用当前对象的自定义方法生成一个线程。<br/>

<img src="/images/posts/2021-12-27/NSThread_detachNewThreadSelector.png"> <br/>
在OS X v10.5之前，你主要使用**NSThread**类来生成线程。虽然你可以获得一个NSThread对象并访问一些线程属性，但是你只能在线程运行后，从线程本身进行访问。在OS X v10.5中，添加了创建NSThread对象而不立即生成相应的新线程之支持。(iOS也支持此功能)这项支持使得在启动线程之前获取和设置各种线程之属性成为可能；还使得之后使用该线程对象引用正在运行的线程之行为成为可能！<br/>

<img src="/images/posts/2021-12-27/initWithTarget_selector_object.png"> <br/>
在OS X v10.5及之后的版本中，初始化NSThread对象的简单方式是使用`initWithTarget:selector:object:`方法。此方法采用与`detachNewThreadSelector:toTarget:withObject:`方法完全相同的信息，并使之来初始化一个新的NSThread实例。但是呢，它并不会直接启动线程。要启动线程，你需显式地调用该线程对象的`start`方法，如下例所示：<br/>

<img src="/images/posts/2021-12-27/myThread_start.png"> <br/>
**注意：**使用`initWithTarget:selector:object:`方法的另一种替代选项是**子类化**NSThread并重写其`main`方法。你将使用这个方法的重写版本来实现线程的**主入口点**(main entry point)。<br/>

<img src="/images/posts/2021-12-27/performSelector_onThread_withObject_waitUntilDone.png"> <br/>
若你有一个NSThread对象并且它的线程正处于运行状态，那么你可以向这个线程发送消息的一种方式是，使用应用程序中几乎任何对象的`performSelector:onThread:withObject:waitUntilDone:`方法。在OS X v10.5中引入了对线程(主线程除外)上执行选择器(selector)的支持，这是线程间通信的一种便捷方式。(iOS亦支持此功能)使用此技术发送的消息由其它线程直接执行，作为其正常**run-loop**事务处理的一部分。(当然，这意味着目标线程必须在它自己的runloop中运行。)当你以这种方式进行通信时，你可能仍然需要某种形式的同步，但是这也比在线程之间设置**通信端口**简单得多。<br/>

**注：**虽然这对于线程间的偶尔通信比较友好，但是对于时间紧迫型或线程间频繁通信等情况，还是要慎用(或者说不应该使用)`performSelector:onThread:withObject:waitUntilDone:`方法。<br/>

### 2.2.2 使用POSIX函数创建线程
OS X和iOS为使用**POSIX**线程**API**创建线程提供了基于**C**语言的支持。实际上，这项技术可以用于任何类型的应用程序(也包括**Cocoa**和**Cocoa Touch**应用程序)；如果你在为多个平台编写软件，这项技术会更方便些。我们将用以创建线程的POSIX routine恰当地称为`pthread_create`。<br/>

<img src="/images/posts/2021-12-27/pthread.png"> <br/>
Listing 2-1显示了两个使用POSIX调用创建线程的自定义函数。LaunchThread函数创建了一个新线程，该线程的主routine是在PosixThreadMainRoutine函数中实现的。因为默认情况下，POSIX将线程创建为**joinable**，所以你会在这个例子中看到代码逻辑更改了线程的属性以创建一个**detached**线程。将线程标记为**detached**，这样系统就有机会在该线程退出时立即收回它占用的资源。<br/>

<img src="/images/posts/2021-12-27/Listing_2-1.png"> <br/>
如果将上述清单中的代码添加到你自己的源文件中并调用LaunchThread函数，此举将在你的应用程序中创建一个新的**detached**线程。当然了，利用这段代码创建的新线程并不会做什么有用的事儿。该线程会启动并几乎立即退出。为了让事情变得更有趣一些，你需要向PosixThreadMainRoutine函数添加代码以完成一些实际工作。为了确保线程清楚要做什么工作，可以在(线程)创建时向它传递一个指向某些数据的指针。更官方的说法是，将此指针作为`pthread_create`函数的最后一个参数以传递！<br/>

若将新创建的线程之信息传递回应用程序的主线程，则需要在目标线程之间建立一条通信路径。对基于**C**语言的应用程序，线程之间有多种通信方式，包括使用端口(port)、条件变量(condition)或者共享内存。对于长时间存在的线程，你几乎总应设置某种线程间通信机制，以便让应用程序的主线程能够检查新创建线程之状态，或者在应用程序退出时可以干净利落地关掉它！ <br/>

有关POSIX线程函数的更多信息，可参阅[pthread](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man3/pthread.3.html#//apple_ref/doc/man/3/pthread)手册页。<br/>

<待续...>


