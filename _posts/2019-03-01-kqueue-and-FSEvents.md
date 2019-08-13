---
layout: post
title: 第16章 kqueue和FSEvents
---
{{page.title}}
===============================
迄今为止，看到的几项技术均可追溯到Unix的早期。一些API是笨重且难以正确使用到，比如信号处理；有些则是笨重且难以扩展到，比如**select()**和**poll()**。

在2000年，FreeBSD工程团队创建了缩写为kqueue的Kernel Queues来解决早期API中的短处。Apple在Mac OS X 10.3纳入了kqueues并在10.6中对其做了扩展。kqueues是内核使用的一种统一通知机制，它可以通知你的程序之所感兴趣的事件。

<img src="/images/posts/2019-03-01/kqueue_kernel_filters.jpeg">
如上图所示，在内核内部是很多的过滤器。当内核工作时，它告诉这些过滤器正发生着什么。图中有一个过滤器用于进程处理，另一个用于网络栈，再一个则与文件系统相关，诸如此类。每一个过滤器都有一组它观测的感兴趣的事件，比如出现在某socket上的数据，一个程序已经**fork()**了，某信号已被发出了，又或者一个文件已添加到某目录！

你的程序向内核注册它感兴趣的实体，像哪个文件描述符或信号编号；以及它想了解的与这些实体相关的确切事件，比如“我想知道socket上要读取的新数据，但是我并不关心能写入与否”。事件发生后即被放进队列，程序可在得空时将它们取出。
## 16.1 kqueue()
首先，通过使用**kqueue()**函数，请求内核为你创建一则新队列：
``` Objective-C
int kqueue();
```
**kqueue()**返回一枚文件描述符(fd)，代表创建的队列。你的应用可以有任意数目的活跃kqueue，为某库或框架创建kqueue以专用也是非常ok的。当你用完某个kqueue后，要像其它文件描述符一样，**close()**它。期间，如果有错误出现，**kqueue()**返回-1并且恰当地设置errno。

kqueue的线程安全性尚未记录在册，因此不要同时在多个线程中使用同一个kqueue。但是，在不同于kqueue监视对象的线程中操作对象(如文件)是安全的。这些操作(如移除一个已关闭文件的event)是自动发生的。

虽然，**kqueue()**返回一枚文件描述符，但是你不能将它用于**read()**或**write()**。相反，你通过接下来描述的**kevent()**函数与fd交互。因为kqueue是由文件描述符表示的，所以你可以将其放进**select()**或**poll()**调用，或者放入另一个kqueue，也可以放进某个runloop来查看是否有待定的通知。如果有将要发生的通知，文件描述符会表现得好像有字节可供读取，比如唤醒**select()**或触发一个runloop通知。
## 16.2 Events
你通过kevent与kqueue交互，前者描述了你感兴趣的实体及过滤器。struct kevent起效于10.3之后的所有Mac OS X系统：
``` Objective-C
struct kevent {
	uintptr_t    ident;  /* identifier for this event */
	int16_t      filter; /* filter for event */
	uint16_t     flags;  /* general flags */
	uint32_t     fflags; /* filter-specific flags */
	intptr_t     data;   /* filter-specific data */
	void        *udata;  /* opaque user data identifier */ 
}
```
此结构既用于程序到内核的通信，也用于反方向的通信。当注册对某事件的兴趣时，你需填充此结构的对应区域。当相应事件时，你查看这些区域以确认发生的事件并找到关于此事件的任意相关数据。

Snow Leopard引入了对kevent结构及调用的64位版本。struct kevent64_s应用于Mac OS X 10.6及iOS上：
``` Objective-C
struct kevent64_s {
    uint64_t     ident;  /* identifier for this event */
    int16_t      filter; /* filter for event */
    uint64_t     flags;  /* general flags */
    uint32_t     fflags; /* filter-specific flags */
    int64_t      data;   /* filter-specific data */
    uint64_t     udata;  /* opaque user data identifier */
    uint64_t     ext[2]; /* filter-specific extensions */
}
```
除了更大的指针大小之外，主要区别是struct kevent64_s有两个额外的64位值，此二者用于传递特定于过滤器的信息。以下是所有这些数字域的介绍：
- ident 用于事件的标识符。对于给定的kqueue，只能有一对特定的过滤器及标识符。标识符是据过滤器而变的。文件描述符用于大多数过滤器，此举甚好，因为文件描述符可清晰无误地标识文件系统内的某个文件。socket由文件描述符表示。信号过滤器在这用信号编号，比如SIGINT或SIGHUP。
- filter 为内核提供的每个过滤器定义的一个常数。一些可用的过滤器包括EVFILT_READ(在文件描述符上有可读的数据)、EVFILT_WRITE(可以无阻塞地写向文件描述符)、EVFILT_VNODE(监测文件及目录的变化)、EVFILE_PROC(追踪进程活动)、EVFILT_SIGNAL(以同步方式接收信号)，以及用于Mach端口的EVFILT_MACHPORT。
- flags 一个位掩码，告诉kqueue如何处理正在注册的事件。有效的标志包括EV_ADD(添加事件到kqueue)、EV_ENABLE(开始发布关于此事件到通知)、EV_DISABLE(停止发布关于此事件的通知)、EV_DELETE(从kqueue移除此事件)、EV_ONESHOT(仅通知一次此事件，随即从kqueue删除它)以及EV_CLEAR(重置事件到状态)。事件通知上也设置flags。
- fflags “Filter Flags”是一个位掩码，包含针对特定过滤器的参数。vnode过滤器有些标志可以暗示针对特定文件操作的兴趣，比如，文件被删除了，内容改变了，体积增长了，等等。某些过滤器则没有额外的标志。
- data 包含任何特定于过滤器数据的一个整数。比方说，data有于内核的读缓冲器之待读取字节数，读缓冲器用于EVFILT_READ通知。在为某EVFILT_WRITE通知阻塞前，data会有你将写入的字节数！
- udata "user data"是一枚可用于任意目的之指针。它像一块可于其下隐藏数据的岩石！你可用它来存储指向字符串、数据结构、或你想与事件相关联对象之指针。你也可以用它存储一枚函数指针，用于一种灵活的事件通知机制。内核传回此值于你(为每个事件)，因此你可将其用于自己的目的！
- ext 为过滤器特定的扩展。它们是两个64位的值，过滤器可用它们来处理那些标志不够充分的场景。当取回事件时，Mach port过滤器同ext[0]返回一枚指向消息的指针。其它过滤器则原封不动地通过ext[0]。ext[1]未用于任何过滤器，因此它的值也是原封不动地通过，想第二个udata域。
<br/>

**EV_SET**宏用于此结构体的快速初始化：
``` Objective-C
EV_SET(&key,ident,filter,flags,fflags,data,udata);
```
对应的64位版本为：
``` Objective-C
EV_SET(&key,ident,filter,flags,fflags,data,udata,ext[0],ext[1]);
```
拿struct kevent的地址作为第一个参数，随着的参数匹配kevent结构体中相同名称的入口即可!
## 16.3 注册及处理Events
**kevent()**向kqueue注册你对特定过滤器和事件的兴趣；并且，它也是这样的一个函数，用于阻塞在一个kqueue上等着事件通知！**kevent()**的签名是有点儿长，因为它让你同时处理以上两种操作。

<img src="/images/posts/2019-03-01/kevent.png">
针对**kevent()**的第一个参数是你想操纵的kqueue！changeList持有kqueue要监测的新事件集合，也监测对事件的任何变化。如果changeList非空且numChanges大于0，那么changeList内的事件将应用于kqueue，“告诉我关于它们发生的所有事”！eventList持有**kevent()**正在报告的事件。如果eventList非空并且numEvents大于0，则**kevent()**用任何挂起的事件填充事件列表。

timeout是一个struct timesepc；它的两个域以秒及纳秒指定了你想等待的时间：
```Objective-C
struct timespec {
	time_t tv_sec;  /* seconds */
	long   tv_nsec; /* nanoseconds */
}
```
系统将尽最大努力尽可能准确地遵守您的超时时间，但是系统时钟的粒度比1纳秒要粗糙得多！如果**kevent()**的timeout参数为空，**kevent()**将无限期地等待，除非eventList也为空；在这种情况下，你只注册新事件却未询问是否有新事件发生。在这种情形下，**kevent()**将立即返回。如果正等待事件，**kevent()**将阻塞至事件发生。如果你想调查kqueue来查看那里是否有什么，给它一个{0,0}的超时 ！

**kevent()**返回写入eventList的事件。如果在超时无效前没有事件发生，则**kevent()**返回0。若在处理changeList期间错误出现了，**kevent()**尝试在flags字段中设置EV_ERROR位，在data字段中设置errno。否则，**kevent()**返回-1，errno也将得以设置。以下是创建kqueue、注册事件及阻塞等待事件发生的方法：

<img src="/images/posts/2019-03-01/create_a_kqueue.png">
这里在重复使用event。**kevent()**复制出来它需要的数据，因此，你不必抓住用来注册事件的struct kevent不放。你只需填写此数据结构，并经由**kevent()**交给内核即可！

EV_ADD和EV_ENABLE标志是将事件添加到队列中及使能事件通知所必需的。你可以传入EV_CLERA来表明你不关心那些可能被记录的任意以前的事件。内核资源的状态(比如，socket读取缓冲器中的数据)也可触发一个事件。使用EV_CLERA将禁止此种情形中的事件通知！

利用**kevent64**注册及接收struct kevent64_s事件：

<img src="/images/posts/2019-03-01/kevent64.png">
它几乎与**kevent()**一样，但是采用了一个64位kevent结构，和一个flags参数，此参数只用途尚未登记在册；所以，仅传入0即可！
## 16.4 kqueues用于信号处理
Unix信号由于其异步特性，是难以处理的。竞争条件是可能的，外加你需要了解信号中断时刻可调用或不可调用的函数。绝大多数时间，你真的不需要在信号发出的瞬间就处理它。你可以在稍后更方便的时间处理之。这也是为什么常见的信号处理技术是设置全局变量并稍后检查之。

你可以使用kqueue和**kevent()**来处理信号，此举可将你从信号处理API引起的头疼中解救出来。用于信号的过滤器也是最简单的一种kqueue过滤器。

kqueue事件处理可与典型的信号处理API协同视同。像**signal()**和**sigaction()**的调用优先于向kqueue注册的事件，所以，如果有人安装了信号处理器并将其置为SIG_DFL,它便得以调用，而kqueue event则不会。因此，你将需要调用 signal(signum,SIG_IGN); 来一处任意默认的信号处理器。

为处理信号，将感兴趣的信号之编号放入struct kevent的ident部分。设置filter为EVFILT_SIGNAL。当**kevent()**报告信号相关的事件时，kevent结构体的data成员将有一个信号自上次出现在**kevent()**以来出现的次数计数。这是针对传统信号API的巨大提升，在传统API中，因信号聚结，你无法确切知道信号到底发生多少次。

sigwatcher.m是一款命令行工具，它向kqueue注册了多个信号，然后阻塞于**kevent()**，等待信号发生。程序工作时，信号的名称，与自上次在**kevent()**中出现以来信号出现的次数一起打印出来。5秒的超时设置用于显示程序的“心跳”——为表明程序还活着及信号被处理时程序保持活跃状态。SIGINT信号将促使程序干净利落地退出！

<img src="/images/posts/2019-03-01/sigwatcher_0.png">
<img src="/images/posts/2019-03-01/sigwatcher_1.png">
编译运行！一则终端窗口用于运行程序；再打开一则终端窗口，向**sigwatcher**发送信号。

<img src="/images/posts/2019-03-01/sigwatcher_result_0.png">
<img src="/images/posts/2019-03-01/sigwatcher_result_1.png">
## 16.5 用于Socket监控的kqueue
与信号处理API一样，用于监控套接字和其它文件描述符的API也可能是笨重且低效的。**select()**是Mac OS X平台上最常用的函数，用于确定套接字的活跃度(它还连接着吗?)及查看套接字上是否有任何活动(我能否无阻塞地从此处读取)。

**select()**及其功能等效的对应**poll()**之问题在于它是无状态调用。每次你调用**select()**或**poll()**，你都得告诉内核你感兴趣的文件描述符是什么。内核随后拷贝描述符列表进自己的内存空间，做它的任意工作以测试其活性和活动情况，然后再把内容拷贝回程序的地址空间来让程序知道正发生着什么。

这方面的主要问题是可伸缩型。如果你正在处理几百或成千上万个文件描述符，你必须每次都向内核询问所有这些事，即便只有很小一部分真的有什么事发生。大容器服务器应用程序通常会发现自己花费大量的CPU时间在对**select()**的FD_SET维护上。

**select()**技术的其它问题是便利性。当**select()**告诉你文件描述符有数据要读取时，你不得不循环检查返回的FD_SET以查找感兴趣的文件描述符。接着，你必须尝试一次**read()**操作看看连接是否仍开放着。如果连接仍处于打开状态，你却不清楚有多少可读取的数据。你不得不猜测要读取多少，通常利用一个硬编码参数，且如果超过该数量，则循环读取。

当利用kqueue来监控套接字时，这两类问题都得以解决。你用**kevent()**把所有感兴趣的套接字都注册上，接着，随后的**kevent()**调用将准确地告诉你哪个套接字有活动。事件中包括要读取的数据量。然后，你可以通过一次**read()**来吃下这些数据，而不是利用多次读取并将读回的数据拼接在一起。

为套接字(或文件、或pipe)上的读操作，利用EVFILT_READ过滤器。它利用一枚文件描述符作为struct kevent标识符。你可以利用**listen()**套接字和正被**accept()**的套接字。当**kevent()**返回后，事件的data字段包含内核缓冲区中等待读取的数据量！

这种行为被称为“级别触发(level-triggered)”事件，因为该事件是基于缓冲器中的数据级别触发的。如果有任何数据要从套接字读取，你将会得到来自EVFILT_READ过滤器的通知。这允许你从套接字读取方便的字节数，说出消息的大小，把剩余的数据放在那！下一次**kevent()**被调用时，你将被告之未读取数据的剩余部分。

其它过滤器被称为使用“边缘触发(edge-triggered)”事件，当感兴趣的实体改变状态时，你会收到通知。这在EVFILT_VNODE过滤器中更常用，如下所述。

说回EVFILT_READ，如果套接字已经关闭，过滤器回在flags字段设置EV_EOF标志，并在事件的fflags字段设置errno。EV_EOF被设置在事件标志中但是套接字缓冲器中仍有待处理的数，是可能的！

EVFILT_WRITE过滤器以类似的方式工作。当收到文件描述符的写事件时，事件结构的data字段将包含写缓冲区中剩余的空间量，也就是阻塞前可以写入的空间大小。当读取断开连接时，该过滤器也将设置EV_EOF。
## 16.6 用于文件系统监控的kqueue
通常当你听到于网络讨论的kqueue时，其十有八九是关于监控文件系统的。你可以用EVFILT_VNODE来查看文件或目录中的更改，并且对这些变化作出反应。你可能想查看目录中的更改，随即挑选出已放入当前目录的任何文件。你还可以通过等待向文件中写入数据来实现高效的**tail -f**功能。

用到的过滤器是EVFILT_VNODE。vnode(虚拟节点的简称)是内核数据结构；它包含有关文件或文件夹的信息；内核为每个活跃文件或文件夹分配了唯一的vnode。vnode是VFS(内核中的虚拟文件系统)的一部分；它提供关于特定文件系统实现的抽象。你可以在vnode上监视许多事件；这些事件有位标志表示；可以在事件结构的过滤器标志中按位“或”感兴趣的事件。当收到通知时，你可以按位“与”过滤器标志来查看究竟发生了什么事。

以下是一些与EVFILT_VNODE相关的不同事件：
- NOTE_DELETE  对文件调用了**unlink()**函数。
- NOTE_WRITE   由于**write()**操作，文件内容已被更改。
- NOTE_EXTEND  文件的体积变大了。
- NOTE_ATTRIB  文件的属性发生了改变。
- NOTE_LINK    文件的硬链接计数已更改。如果对文件创建了新的符号链接，则不会触发此操作。
- NOTE_RENAME  文件被重命名了。
- NOTE_REVOKE  通过**revoke()**系统调用撤销了对文件的访问，或者底层文件系统已卸载。
<br/>

**dirwatcher**是一件命令行工具，它采用一组目录作为程序参数。打开这些目录中的每一个成员，使用NOTE_WRITE过滤器标志将结果文件描述符放入kqueue中。当目录改变时，比如有文件加入或移除，**kevent()**将返回报告哪个目录已更改！本程序随即打印出已更改目录的名称。

因为**dirwatcher**将指向目录名称的指针放入了事件的用户data指针字段，所以映射更改事件到目录名称很简单。程序也能捕获SIGINT信号来完成干净的关闭动作。很多种类的事件可以混合匹配在相同的kqueue中。你可以用事件结构的user数据字段来确定事件种类是什么，**dirwatcher**做了什么，又或者查看过滤器域来判别哪个filter产生了这个事件！
<img src="/images/posts/2019-03-01/dirwatcher_0.png">
<img src="/images/posts/2019-03-01/dirwatcher_1.png">
<img src="/images/posts/2019-03-01/dirwatcher_2.png">

<img src="/images/posts/2019-03-01/dirwatcher_result.png">
## 16.7 kqueues和Runloops

## 16.8 fsevents

## 16.9 fseventsd

## 16.10 Watching Directories

## 16.11 Events

## 16.12 Histroy

## 16.13 Visibility

## 16.14 FSEvents API

### 16.14.1 Creating the stream

### 16.14.2 Hook up to the runloop

## 16.15 实例
fsevents.m显示自上次运行以来启动驱动器发生的所有历史事件。然后，随着新事件发生，也会显示。为使事情简单些，在看到20件新鲜事后，程序退出。当程序退出时，它保存设备UUID和最后一个事件ID到用户默认值(user defaults)。

<img src="/images/posts/2019-03-01/fsevents_0.png">
<img src="/images/posts/2019-03-01/fsevents_1.png">
<img src="/images/posts/2019-03-01/fsevents_2.png">
<img src="/images/posts/2019-03-01/fsevents_3.png">
<img src="/images/posts/2019-03-01/fsevents_4.png">
<img src="/images/posts/2019-03-01/fsevents_5.png">
运行后，开始监测新事件：

<img src="/images/posts/2019-03-01/fsevents_result_0.png">
<img src="/images/posts/2019-03-01/fsevents_result_1.png">
## 附：拓展阅读

- [kqueue - Wikipedia](https://en.wikipedia.org/wiki/Kqueue)
- [Kqueue: A generic and scalable event notification facility](https://en.wikipedia.org/wiki/Kqueue)
- [FreeBSD source code of the **kqueue()** system call](http://bxr.su/FreeBSD/sys/kern/kern_event.c#sys_kqueue)
- [kqueue(2)-FreeBSD System Calls Manual](https://www.freebsd.org/cgi/man.cgi?query=kqueue&sektion=2)
- [Kernel Queues: An Alternative to File System Events - Apple Developer](https://developer.apple.com/library/archive/documentation/Darwin/Conceptual/FSEvents_ProgGuide/KernelQueues/KernelQueues.html)
- [Kernel Queues and Events by Jonathan Lemon of the FreeBSD project](https://people.freebsd.org/~jmg/kq.html)
- ......
