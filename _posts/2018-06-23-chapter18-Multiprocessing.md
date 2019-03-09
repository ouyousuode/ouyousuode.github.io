---
layout: post
title: 多进程
---
{{page.title}}
====================

所有的现代操作系统都是多进程的，这意味着多个独立的应用可同时运行并且共享系统的资源。遍布于可运行程序间的操作系统时间片，将可用的CPU时间划分给这些可运行程序。
### 进程调度
调度器是操作系统的一部分，它标出下一步哪个进程应获取使用CPU的权限。它经常参考一些信息来做这个决定，这些信息包括进程优先级、该进程以前获得的CPU时间量以及它是否刚刚完成一个I/O操作。

每个进程都有一个影响优先级的相关美好程度值。此值被指定为-20到20间的一个整数。默认情况下，进程有一个为0的美好程度值。美好程度值越大，进程的优先级越低；越美好的进程越不太需要多少CPU时间。

你可以启动一个利用nice命令指定美好值的进程。可使用renice命令修改一个已运行进程的美好值。你可以给自己的进程降优先级，但若想提升优先级，除非你拥有超级用户权限。

可以将调度器认为是拥有很多带优先级进程的一个列表。这些不被阻塞的进程以优先级的顺序放入一个运行队列(run loop)中。当要运行一个新进程时，调度器从队列头部取出一个进程，使之运行，直到因某些原因被阻塞或者时间片过期了，再将它放回到队列中。

<img src="/images/posts/2018-06-23/scheduler_Small.jpg">

你可利用uptime命令来检查run queue的平均长度。

<img src="/images/posts/2018-06-23/uptime.png">

macOS的load average只是报告run queue的深度。它并不包括阻塞在硬盘I/O上的时间，其它某些Unix类系统(包括Linux)会报告此时间。
### 一些便利的函数
开启新进程是编程库中一件强劲的工具。Unix附带有很多轻型命令行工具，它们可以执行有用的功能。有时，充分利用这些工具比重新实现它们相同的功能更有意义。举例来说，你可能有一个移除HTML内标签的Perl脚本文件。与利用Objective-C实现正则表达式代码相比较，启动一个Perl脚本再喂给它Html文件，容易得多。

启动其它程序的最简单方式是使用system()函数。它启动程序，等它结束，并随后返回结果代码(result code)。实际上，system()传递控制权给了一个新shell，/bin/sh，因此你可以使用重定向这样的shell功能。
``` Objective-C
int system(const char *string);
```
从system()得到的返回值即从启动的程序返回的结果值。否则，如果启动shell前有错误发生，我们会得到-1并且errno也会被设置。一个127的返回值意味着shell失败了(因某些原因)，可能因为命令字符串中的坏语法。system()以-c参数调用shell。如果你得到了一个错误并且想从Terminal试验你的命令行，可以直接在shell进行操作。如果你确实想向你启动的程序进行读写，那可以用popen()启动程序并打开一个到它的pipe。r然后用pclose关闭此连接：
``` Objective-C
FILE *popen(const char *command,const char *type);
int pclose(FILE *stream);
```
像system()一样，popen()调用一个shell来运行命令。下面是一个popen()开cal程序以获得当前日历的一个小程序。一切为了好玩的是，从cal的输出遭到了来自rev的行反转。

<img src="/images/posts/2018-06-23/pcalCode_Small.png">
./pcal 运行后，

<img src="/images/posts/2018-06-23/resultOfPcal_Small.png">

popen手册指出它用了一个双向pipe，所以你可以指定一个读写模式(r+)以替代仅仅的一个r或者w。不幸的是，在实践中使用返回的stream既读又写可能无效果，因为它需要被popen的进程的合作。Use of a bidirectional stream can easily run afoul of the buffering done under the hood by the stream I/O functions.它也会引起死锁的诡异现象：your process could block writing data to the child process while it blocks wirting already-processed data back.进程既不会得到排干满缓冲器的机会，也不能不阻塞其它操作。如果你需要对子进程既读又写，那么你最好的选择是，要么重定向输出到一个文件，随后处理之；要么自己创建一个子进程，用两个pipe，一个用来读，另一个用来写。
### fork
在Unix中创建一个新进程，你必须先制作当前进程的一份拷贝。此拷贝可继续执行原进程的代码，或者可开始执行其它程序。系统调用fork()执行拷贝的过程：
``` Objective-C
pid_t fork(void);
```
fork()制作一份当前运行进程的拷贝，并且它是很少的可返回两次的函数群中的一员。在称为父进程的的原进程中，fork()返回新进程的进程ID。在称为子进程的新进程中，fork()返回0。当发生错误时，没有生成的子进程，fork()在父进程中返回-1并且设置errno。

作为父进程的一份拷贝，子进程继承了父进程的内存、打开的文件、真实有效的用户及组ID、当前的工作目录、信号掩码、文件模式创建掩码、环境、资源限制以及任意附带的共享内存段。此拷贝行为可被展示为：

<img src="/images/posts/2018-06-23/fork_Small.jpg">

拷贝这许多数据听起来较费时，但是macOS采用快捷方式。它在父子进程间共享原始拷贝，延迟进行备份到非复制不可而非立即复制父进程的内存空间。此过程利用的是一种称为copy on write的技术，也叫COW。

<img src="/images/posts/2018-06-23/copyOnWrite.png">
所有与父进程相关的物理页标记为只读。当一个进程试图向“复制的”内存的一个页进行写入时，此页先被拷贝出来以专用，然后每份拷贝都变成分离状态，就拥有了进程的可读写属性。做出这修改的进程依旧做着它想要的改变，而其它进程对此一无所知。这种技术极大地减少了操作系统在fork()上的工作量。

前面提到的system()和open()这样的方便函数最终都要去调用fork(),但是，大多数情况下，与对fork()的恰当调用相比，它们使用更多资源，其原因在于它们需要调用一个shell来运行新进程。我们可以写fork一个子进程的极简程序：

<img src="/images/posts/2018-06-23/fork.png">

运行后，可看到：

<img src="/images/posts/2018-06-23/resultOfFork.png">
像Unix地面儿上的所有情况一样，这也有一些容易忽略的知识点。第一个就涉及到父子进程间的竞争条件：你无法保证谁会先运行。在子进程被调度前，你无法信赖父进程中运行于fork()之后的代码。

其它的知识点与打开的文件如何在两个进程间共享有关。父子进程在内核中共享相同的文件表入口，如下所示的那样。这就意味着打开文件的所有属性都是共享的，比如当前偏移量。这通常是一种被期望的行为——当你想让父子进程打印到相同的标准输出时，每当打印，每个进程会增长在文件的偏移量，因此它们可以避免相互改写。However,it can also be confusing when your file offsets move from underneath you and you were not expecting them to。与文件表事务相关的是the state of the buffers for buffered I/O。The buffered I/O buffers in the parent's 地址空间 get duplicated across the fork。如果fork前缓冲期中有数据，父子进程可以打印两次缓冲期中的数据，这可能不是我们想要的结果。

<img src="/images/posts/2018-06-23/filesAfterFork_Small.jpg">

注意上述极简程序中前有下划线的exit()方法。它行为如exit(),关闭文件描述符，做些一般性的清理工作，但是它不会冲刷文件流缓冲期，因此你不必担心它会为你冲刷带有复制数据的缓冲器。在此程序实例中，printf()中的新行(newline)为我们冲刷缓冲器。

也请注意在退出前父进程小睡了一会儿。当子进程记录信息时，这提高了它仍未子进程的父亲角色的可能性。在子进程写信息前，如果父进程退出了，那此子进程的父亲将为进程1。删除sleep()行，重新编译运行后，你可以观察到这种行为。由于输出以来竞争条件，所以有可能运行几次后才能看到这种情况。
### 父子进程的生命周期









