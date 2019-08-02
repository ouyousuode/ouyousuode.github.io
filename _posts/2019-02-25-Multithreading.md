---
layout: post
title: 第20章 多线程
---
{{page.title}}
===========================
多线程是在你的应用中达成并发效果的另一个方法。虽然多进程使用多个独立的进程并带有它们自己的地址空间及资源，线程却是运行于一个单一应用内的多执行流。此应用有单一的地址空间，这些线程则共享应用的可用资源。

像多进程一样，多线程也可利用多CPU的优势。你也可以用之来简化某种编程工作。每个线程可以继续它自己的快乐生活，计算数值以及调用函数；同时，其它线程们皆可独立运行并相互无干扰。线程的一个非常常见的用途是在网络服务器中处理请求(如一台web服务器)。一个新链接被**accept()**之后，则创建一个线程来处理此请求。此线程可随即利用**read()**来获得请求并**write()**以发送数据回去。它也可以打开文件以及执行loop循环，因此没有必要利用**select()**多路复用I/O，也没有必要经由曲线救国的方式来一丢丢地进行计算。
## 20.1 Posix线程
Mac OS X使用Posix thread API,更广为人知的是“pthreads”，作为它的原生线程模型。不幸的是，pthreads与Unix API的其它部分在报告错误条件方面有不同的约定。尽管如你所期望，它在成功时返回零，但是，失败时返回错误码，而非返回-1及设置errno。
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
线程就像进程，因为它们有一个返回值给创建线程者。利用类似于为进程的**waitpid()**机制，你可使用**pthread_join()**来与特定线程会和。
``` Objective-C
int pthread_join(pthread_t threadID,void **valuePtr);
```
**pthread_join()**将阻塞至指示的线程退出。其返回值会被写入valuePtr。为确定你自己的threadID，可利用**pthread_self()**。有时，你想分离(detach)一个线程以便你不必**pthread_join()**它。分离后，此线程会运行到结束、退出及被删除。若达成次目的，使用**pthread_detach()**:
``` Objective-C
int pthread_detach(pthread_t threadID);
```
有时，称这个被分离的线程为“守护线程”，因为它们像守护进程般独立运行！成为这样一个线程的常用语法是调用pthread_detach(pthread_self());以将自身转变为一个守护线程。

以下basics.m这个小程序便衍生出了几个线程。其中，有些被分离(detached)了；有些未如此，便需被等待：

<img src="/images/posts/2019-02-25/basics_0.png">
<img src="/images/posts/2019-02-25/basics_1.png">
我们编译，运行后的结果为：

<img src="/images/posts/2019-02-25/basics_result_0.png">
<img src="/images/posts/2019-02-25/basics_result_1.png">
这其中有几件有趣的事。首先，没有预定义的线程运行顺序。它们全仰赖于操作系统调度器！其次，运行main()的主线程是比较特殊的。在所有其它线程内，return等同于pthread_exit()。然而，当main()返回时，却等同于exit()，它会终止整个进程。如果**main()**调用**pthread_exit()**，应用将直到所有线程退出才终止。这就是为什么第5个线程有时在程序退出时尚未完成它的工作。
### 20.1.2 同步
还记得谈论信号时，关于竞争条件和并发性的讨论吗？线程面临同样的问题。这也是上文提到的一些重复问题：没有预定义的线程运行顺序；它们全仰赖于操作系统调度器。此性质可引发诸多问题，并引入了很多复杂性才能确保这种随机执行之顺序不会破坏数据。

比如说，在上文的basics.m中，首先每个线程需要的所有的信息初始化为一个ThreadInfo结构体数组，而后，每个线程收到一个指向自己数组元素的指针。
``` Objective-C
ThreadInfo info;
for(i = 0;i < Thread_COUNT;i++) {
	info->index = i;
	info->numberToCountTo = (i + 1) * 2;
	...
	result = pthread_create(&threads[i].threadID,NULL,
							threadFunction,&info);
}
```
接着，**threadFunction**将拷贝它想要的数据。但是，这里需要考虑三种情况：
- 1.**threadFunction** 立即执行。该线程根据它的参数指针将数据复制到自己这，然后快乐地行驶于自己的道路上。在此模式下，所有的事情一切顺利。
- 2.**threadFunction** 稍后开始执行，就像在循环的顶部一般。当循环变量i为2时，该线程得以创建。循环接着到索引3，创建一线程，现在将执行循环数字4。“线程2”最终得以调度且开始执行，查看内存以取得相应的控制信息，并使用为线程4准备的相同配置信息。
- 3.**threadFunction** 稍后开始执行，且在循环中间。这是最坏的情形：污染数据。当index和numberToCountTo为i = 4更新，但detachYourself和sleepTime仍有i = 3的值时，如果线程2醒来了，它将获得“线程3”的一半数据及“线程4”信息的一半。

沿着相同的思路往下想，在某个线程环境中，对数据结构的未受保护之操作可导致数据破坏。想象一条链表正处于添加新节点的指针操作中间。此线程旋即被其它线程抢占了，而这个其它线程视图向此链表中添加自己的内容。最好的情况是，你得到一条崩溃消息，因为正被修改的指针正指向一个坏地址。最坏的情况是，一个或其它插入操作被丢掉了，并且你也有了受到轻微污染的数据。

正确同步化湿难以做到的，并且问题也会是难以调试的。在安全数据访问与高效访问之间有一条线，这也是线程化编程比人们臆想的难得多的主要原因。为帮助解决这些问题，pthread API提供了一些同步机制，特别是互斥锁和条件变量。
### 20.1.3 互斥锁
互斥锁用于序列化访问代码的关键部分，这意味着当互斥锁被恰当使用时，只有一个正执行的线程可访问那段代码，如A mutex图示那样。所有想运行此代码的其它线程将被阻塞至原线程结束。结束后，一任意线程即被选中来运行此代码片段。

<img src="/images/posts/2019-02-25/a_mutex.jpeg">
对一段代码使用互斥锁消除了此代码可能有的任何并发性，而这恰恰是起初使用线程的主要动机。因此，你希望此锁尽可能地短！需清醒认识到，虽然互斥锁控制对代码的访问，但是，你只是用它来控制对数据的访问！

互斥锁的数据类型为pthread_mutex_t。你可以声明它们为局部变量、全局变量、或者为它们**malloc()**内存。有两种方式来初始化互斥锁。第一种方式为使用一个静态初始化器，对一个单例形式的互斥锁较为方便，当然此单例是你想在某函数外部持有的。
``` Objective-C
static pthread_mutex_t myMutex = PTHREAD_MUTEX_INITIALIZER;
```
另一种方法是取一块pthread_mutex_t大小的内存，利用**pthread_mutex_init()**初始化此内存块：
``` Objective-C
int pthread_mutex_init(pthread_mutex_t *mutex,
					   const pthread_mutexattr_t *attr);
```
像**pthread_create()**一样，不在此处讨论具体的属性。当你为每个数据结构加一把互斥锁时，你将会用到**pthread_mutex_init()**。当你用**pthread_mutex_init()**初始化完成一个锁时，需使用**pthread_mutex_destroy()**以释放它的资源：
``` Objective-C
int pthread_mutex_destroy(pthread_mutex_t *mutex);
```
利用**pthread_mutex_lock()**取得锁：
``` Objective-C
int pthread_mutex_lock(pthread_mutex_t *mutex);
```
若此锁不可用(处于忙状态)，此调用将阻塞至它可用为止。当调用(以0为返回值)后后执行鸡血，你要明白你在独占此锁。为释放某互斥锁，利用**pthread_mutex_unlock()**：
``` Objective-C
int pthread_mutex_unlock(pthread_mutex_t *mutex);
```
当获取锁时，若你不想阻塞，可用**pthread_mutex_trylock()**:
``` Objective-C
int pthread_mutex_trylock(pthread_mutex_t *mutex);
```
若此调用返回值为0，你便锁定了此互斥锁。如果它返回BUSY，此互斥锁正被其它势力锁定，那你需要在此尝试。

mutex.m展示了实战中的互斥锁。首先把basics.m的内容拷贝到mutex.m，然后将threadFunction更改为如下:

<img src="/images/posts/2019-02-25/mutex.png">
<img src="/images/posts/2019-02-25/mutex_result_0.png">
<img src="/images/posts/2019-02-25/mutex_result_1.png">
从结果中，我们可以看到，其执行被序列化了。也应注意到，既然关键部分被序列化了，整个程序便慢了下来。
### 20.1.4 死锁
如果你正为某单一操作处理多个互斥体(比如，在操作两个数据结构前锁定它们)，并且每次使用互斥体时，都不小心地以相同顺序获得互斥体，你可能会陷入死锁境地！

假定线程A有：
``` Objective-C
pthread_mutex_lock(mutexA);
pthread_mutex_lock(mutexB);
```
以及线程B有：
``` Objective-C
pthread_mutex_lock(mutexB);
pthread_mutex_lock(mutexA);
```

<img src="/images/posts/2019-02-25/deadlock.jpeg">
图Deadlock展示了像如下到执行路径:
- 线程1锁定了A。它得以抢占。
- 线程2锁定了B。它得以抢占。
- 线程1尝试去锁定B。它阻塞了。
- 线程2尝试去锁定A。它也阻塞了。

现在，这两个线程都被死锁了，每个都在等对方去释放它的资源，并且没有方式打破此怪圈。每个线程都应做这样的事：
``` Objective-C
while(1) {
	pthread_mutex_lock(mutexA);
	if(pthread_mutex_trylock(mutexB) == EBUSY) {
		// 如果B正忙的话，也把A释放了吧
		pthread_mutex_unlock(mutexA);
	}
}
```
这正是锁定第一个互斥锁，再来尝试锁定第二个。如果第二个处于被锁状态，其他人正拥有它，因此释放第一个；然后，再试一次，以防有人拥有mutexB且正等待mutexA。
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
