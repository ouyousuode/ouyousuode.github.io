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
- threadID 为指向一个pthread_t的一枚指针。新线程的threadID写于此处。
- attr 是一组属性值。传入NULL以使用默认属性。不于此处讨论具体的属性值；它们会使对线程化编程的基础变得难懂起来。
- startRoutine 是指向带有签名的函数的指针，函数为
``` Objective-C
void *someFunction(void *someArg);
```
这也是线程中执行开始之地。此函数返回了，就意味着线程终止了。someArg是传给**pthread_create()**的参数值。返回值是返回状态的指针。你可为此二者传送你想的任意数据结构。
- arg 为给予startRoutine的参数。
<br/>

当某个线程被创建时，系统为此线程创建一个私有栈，类似于main函数调用栈。像Figure 20.1中所示的那样，线程用此栈作函数调用管理及局部变量存储之用。

<img src="/images/posts/2019-02-25/Figure_20_1_Thread_stacks.png">
线程神似进程，因为它们有一个返回值给创建线程者。利用类似于为进程的**waitpid()**机制，你可使用**pthread_join()**来与特定线程会和。
``` Objective-C
int pthread_join(pthread_t threadID,void **valuePtr);
```
**pthread_join()**将阻塞至指示的线程退出。其返回值会被写入valuePtr。为确定你自己的threadID，可利用**pthread_self()**。有时，你想分离一个线程以便你不必**pthread_join()**它。分离后，此线程会运行到结束、退出及被删除。若达成此目的，使用**pthread_detach()**:
``` Objective-C
int pthread_detach(pthread_t threadID);
```
有时，称这个被分离的线程为“守护线程”，因为它们像守护进程般独立运行！成为这样一个线程的常用语法是调用pthread_detach(pthread_self());以将自身转变为一个守护线程。以下basics.m这个小程序便衍生出了几个线程。其中，有些被分离(detached)了；有些未如此，便需被等待：

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
	result = pthread_create(&threads[i].threadID,NULL,threadFunction,&info);
}
```
接着，**threadFunction**将拷贝它想要的数据。但是，这里需要考虑三种情况：
- 1.**threadFunction** 立即执行。该线程根据它的参数指针将数据复制到自己这，然后快乐地行驶于自己的道路上。在此模式下，所有的事情一切顺利。
- 2.**threadFunction** 稍后开始执行，就像在循环的顶部一般。当循环变量i为2时，该线程得以创建。循环接着到索引3，创建一线程，现在将执行循环数字4。“线程2”最终得以调度且开始执行，查看内存以取得相应的控制信息，并使用为线程4准备的相同配置信息。
- 3.**threadFunction** 稍后开始执行，且在循环中间。这是最坏的情形：污染数据。当index和numberToCountTo为i = 4更新，但detachYourself和sleepTime仍有i = 3的值时，如果线程2醒来了，它将获得“线程3”的一半数据及“线程4”信息的一半。

沿着相同的思路往下想，在某个线程环境中，对数据结构的未受保护之操作可导致数据破坏。想象一条链表正处于添加新节点的指针操作中间。此线程旋即被其它线程抢占了，而这个其它线程视图向此链表中添加自己的内容。最好的情况是，你得到一条崩溃消息，因为正被修改的指针正指向一个坏地址。最坏的情况是，一个或其它插入操作被丢掉了，并且你也有了受到轻微污染的数据。

正确同步化是难以做到的，并且问题也会是难以调试的。在安全数据访问与高效访问之间有一条线，这也是线程化编程比人们臆想的难得多的主要原因。为帮助解决这些问题，pthread API提供了一些同步机制，特别是互斥锁和条件变量。
### 20.1.3 互斥锁
互斥锁用于序列化访问代码的关键部分，这意味着当互斥锁被恰当使用时，只有一个正执行的线程可访问那段代码，如Figure 20.2图示那样。所有想运行此代码的其它线程将被阻塞至原线程结束。结束后，一任意线程即被选中来运行此代码片段。

<img src="/images/posts/2019-02-25/Figure_20_2_A_mutex.png">
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
如果你正为某单一操作处理多个互斥锁(比如，在操作两个数据结构前锁定它们)，并且每次使用互斥锁时，都不小心地以相同顺序获得互斥锁，你可能会陷入死锁境地！

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

<img src="/images/posts/2019-02-25/Figure_20_3_Deadlock.png">
Figure 20.3展示了像如下到执行路径:
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
互斥锁非常适合他们所做的事情，即保护关键的代码区域。但是，有时候，你会有这样的境遇，在锁定你的互斥锁之前，你想等待直到某些条件为真(成立)，比如你在处理一个请求前，检查队列中是否已有这样的事项。如果你为此种情况使用互斥锁，你最终会编写循环来测试条件并接着释放互斥锁。换句话说，这是一个浪费CPU时间的polling操作。

条件变量(pthread_cond_t)解决此问题。条件变量让感兴趣的各方阻塞于此变量。此阻塞会经由来自另个线程的信号而停止。signal是个不幸的文字选择，因为此信号与我们之前讨论过的Unix信号无丝毫瓜葛！

像互斥锁一样，你可以利用PTHREAD_COND_INITIALIZER来静态地初始化条件变量，或者可以用：
``` Objective-C
int pthread_cond_init(pthread_cond_t *cond,
				const pthread_condattr_t *attr);
```
类似地，如果你初始化了一个条件变量，需利用
``` Objective-C
int pthread_cond_destroy(pthread_cond_t *cond);
```
来销毁它！互斥锁与条件变量是相关联的。为使用条件变量，你锁定相关联的互斥锁，接着当你感兴趣的条件是false时，调用**pthread_cond_wait()**:
``` Objective-C
int pthread_cond_wait(pthread_cond_t *cond,
                      pthread_mutex_t *mutex);
```
互斥锁被自动解开，且此调用阻塞。当其它线程调用**pthread_cond_signal()**时，
``` Objective-C
int pthread_cond_signal(pthread_cond_t *cond);
```
当前被阻塞在**pthread_cond_wait()**的单个线程会醒来(利用pthread_cond_broadcast()可唤醒所有被阻塞的线程)。需认识到，**pthread_cond_wait()**可以假返回。在继续执行之前，你应经常检查value of your condition。好了，那么“value of your condition”到底是什么呢？考虑Figure 20.4中的场景，一台web服务器的请求队列！

<img src="/images/posts/2019-02-25/Figure_20_4_Server_request_queue.png">
一个线程阻塞在**accept()**调用上，等待新链接。当一个新链接进入时，它被放在请求队列的尾端。同时，connection线程在闲转，将最顶端的条目拉出队列并处理他们。这是经典的“生产者/消费者”问题的一个版本，也是每本并发编程书籍都会讨论的知识点。

相对于请求队列，“accept”和“connection”线程可以各有两种状态。
accept线程接受一项新链接：
- 队列有富余空间来存放一个新请求，则(1)将请求放入队列；(2)发信号唤醒一个connection线程。
- 队列已满，则(1)阻塞条件变量，直至队列中有空间；(2)当它醒来后，将请求放入队列。以伪代码形式示例：
``` Objective-C
pthread_mutex_lock(queueLock);
while(queue is full) {
	pthread_cond_wait(g_queueCond,queueLock);
}
put item on the queue;
pthread_mutex_unlock(queueLock);
signal a connection thread;
go back to accept();
```
**pthread_cond_wait()**仅发生于队列为满时，你闲逛于loop直到队列中有空儿。此while循环是为防范假返回。
<br/>

而connection线程，则：
- 队列上有新请求，则(1)从队列中获取请求；(2)如果队列处于完全填满状态，发信号通知accept线程，就说现在又有可用空间了，以防它被阻塞以等待队列中的空闲空间。
- 队列为空，则(1)阻塞在一条件变量上，直到队列中有空；(2)当它醒来时，从队列中获取请求。同样地，以伪代码示例：
``` Objective-C
pthread_mutex_lock(queueLock);
while(queue is empty) {
	pthread_cond_wait(g_queueCond,queueLock);
}
get an item from the queue;
pthread_mutex_unlock(queueLock);
signal the accept thread;
process the request;
```
<br/>

如果你不想无限期地阻塞，你可利用**pthread_cond_timedwait()**为条件变量的等待指定一个超时：
``` Objective-C
int pthread_cond_timedwait(pthread_cond_t *cond,pthread_mutex_t *mutex,
	const struct timespec *abstime);
```
通过填写这个数据结构来指定timeout：
``` Objective-C
struct timespec {
	time_t tv_sec;  // seconds
	long  tv_nsec;  // nanoseconds
}
```
**pthread_timed_condwait()**与**select()**类似调用之不同之处在于前者的timeout是一个绝对值，而非相对值。这就使得处理虚假唤醒简单得多，因为你不必每次重新计算其等待间隔。webserve-thread.m代码描述了一台简单的web服务器，它有一个使用条件变量的请求队列：

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
编译及运行程序。你可使用**telnet**来与服务器通信，或者可以启动一个网络浏览器再使用像http://localhost:8080/file/webserve-thread.m的URLs来返回一个文件，又或者http://localhost:8080/numbers/37来看到数字。Safari在显示之前会主动缓存一些数据，因此在生成所有数字之前，您可能看不到显示的单个数字。像Firefox或Camino之其它浏览器会随着数字的生成而即时显示！
## 20.2 Cocoa及线程
你可以在Cocoa程序中使用多个线程。像往常一样，Cocoa带来了一组很好的提供了线程功能的纯净API。当使用Cocoa时，以上关于竞争条件和性能之所有警告均适用，以及其它一些问题也同样。
### 20.2.1 NSThread
**NSThread**是抽象化线程的类。为创建一个新线程，使用类方法：
``` Objective-C
+ (void)detachNewThreadSelector: (SEL)aSelector
                       toTarget: (id)aTarget
                     withObject: (id)anArgument;
```
aSelector是一个选择器，它描述了aTarget可以接收的方法。aSelector的签名是：
``` Objective-C
- (void)aSelector: (id)anArgument;
```
anArgument是传给这个方法的内容。此调用像**pthread_create()**一样工作，具体来说，线程开始启动与aSelector的第一条指令，且方法退出时线程终止。这个是一款守护线程，因此没有必要等待它结束。

当第一个**NSThread**对象创建后，**NSThread**类发布一个NSWillBecomeMultiThreadedNotification的通知。而后，调用[NSThread isMultiThreaded]会返回YES。你可利用此调用及通知来决定是否需要为你的数据结构使用同步。如果是单线程程序，使用同步除拖慢你之外毫无益处。**pthread_create()**既不发布这样的通知，也不触发**isMultiThreaded**返回YES，因此，如果你混用pthread和**NSThread**，需清醒认识之。

当你创建一个**NSThread**时，你也会得到一个用于事件处理及分布式对象操作的**NSRunLoop**。如果将在此线程中使用任何Cocoa调用，你应创建一个**NSAutoreleasePool**。**NSApplication**的**detachDrawingThread:toTarget:withObject:**方便于做一个新线程及建立一个自动释放池。并且你并不局限于创建的线程中执行绘制操作！

**NSLock**类表现得非常像pthread_mutex_t。你可以锁定、释放它，尝试锁定，并且有一个锁定超时。同样地，**NSConditionLock**充任了pthread_cond_t的角色。当使用**pthread_cond_wait()**时，Cocoa的条件锁API隐藏了你不得不用的循环。取而代之的是，它是一个跟踪所处状态的小型状态机，并且你可以告诉它等待至一个特定状态发生，以及告诉它移向什么状态。

OS X 10.2之前，子线程不能进入Cocoa视图。Jagur添加了这项能力。在执行绘制前，你需要调用**-(BOOL)lockFocusIfCanDraw**。在视窗(口)服务器将显示已更新的内容前，你还必须显示地刷新窗口。
``` Objective-C
if([drawView lockFoucsIfCanDraw]) {
	// set colors,use NSBezierPath and NSString drawing functions
	[[drawView window] flushWindow];
	[drawView unlockFocus];
}
```
<br/>
### 20.2.2 Cocoa及线程安全
Foundation及AppKit框架的一部分是线程安全的，然有些却不是。通常情况下，不可变对象是线程安全的，可以被多个线程使用。可变对象不是线程安全的，也不应被多个线程使用。在假定对象的可变性之前，你必须对如何创建对象小心翼翼，例如，考虑一个采用**NSString**作参数的方法。通常，**NSString**是不可变的。但是，鉴于继承关系，一个调用者可以合法地创建一个**NSMutableString**并把它喂给一个采用**NSString**作参数的方法。同样的方式，线程间共享不可变容器是安全的，可变容器则不可！

用户界面对象是非线程安全，应仅用于主线程。通常情况下，你应假设一个对象为非线程安全的，除非它是有明确记录的(线程安全)。
### 20.2.3 Objective-C @synchronized块
当你告诉编译器使用原生Objective-C异常后，无论在Xcode中进行勾选还是给编译器-fobjc-exceptions标记，你都要使用Objective-C线程同步操作器。

@synchronized()指令锁了操作于某个特定对象的一段代码。一次只能有一个线程操作此块代码中的对象。使用此同一对象的其它线程将被阻塞至@synchronized块退出。

@synchronized()采用单个变量，它可以是任意Objective-C对象。你可以使用存储于实例变量中的对象，你可以使用self，又或者你甚至可以使用一个类对象(class object)。如果你正在类方法中使用@synchronized(self)，你将与类对象@synchronized。@synchronized(someObject)行为就像一个递归的互斥锁。一个线程可以在不同的块中使用相同的对象，期间，这些块与相同的对象一起同步。扔自@synchronized()块内的异常将自动释放锁。
## 20.3 深入学习：Thread Local Storage
有时，拥有一个专用于线程的存储是很方便的。在上文的basics.m程序，你可以把ThreadInfo栈的内容藏进线程局部存储而不是在函数调用栈上。线程局部存储行为类似于全局变量，但对于线程而言，它是私有的！errno被存储于线程局部存储以便每一个线程有它自己的errno！

为使用线程局部存储，利用**pthread_key_create()**初始化一个pthread_key_t：
``` Objective-C
int pthread_key_create(pthread_key_t *key,void (*destructor)(void *));
```
这会创建一个抽象键。通常，你用描述性名称命名变量，或者将此键与字典中的描述性字符串相关联，像Figure 20.5展示的那样！当线程退出时，析构函数被调用，以便动态内存(或其它资源)可被清理。

通过**pthread_setspecific()**设置一个线程局部值：
``` Objective-C
int pthread_setspecific(pthread_key_t key,const void *value);
```
以及利用**pthread_getspecific()**获得对应的值：
``` Objective-C
void *pthread_getspecific(pthread_key_t key);
```
<img src="/images/posts/2019-02-25/Figure_20_5_Thread_local_storage.png">
如果你用着**NSThread**呢，可以利用
``` Objective-C
- (NSMutableDictionary *)threadDictionary;
```
要将内容放入线程局部存储内，只需将内容存储在字典中即可！此为一**NSThread**实例方法，因此你需要通过[NSThread currentThread]找到你当前的**NSThread**对象。
## 20.4 深入学习：读/写锁
Cocoa和pthreads都提供读/写锁。此二者允许同时由许多读取器读取数据结构，但是一次仅允许一个线程写访问。

pthread_rwlock_t工作起来像pthread_mutex_t，但是伴有一对额外的调用。这有细节:
``` Objective-C
pthread_rwlock_init(pthread_rwlock_t *lock,
                    const pthread_rwlockattr_t *attr);
```
OS X为定义PTHREAD_RWLOCK_INITIALIZER，因此你不能静态地创建它们。
``` Objective-C
int pthread_rwlock_destroy(pthread_rwlock_t *lock);
```
你可以指定想要的锁定，而不是使用**lock()**：
``` Objective-C
int pthread_rwlock_rdlock(pthread_rwlock_t *rwlock);
int pthread_rwlock_tryrdlock(pthread_rwlock_t *rwlock);
int pthread_rwlock_wrlock(pthread_rwlock_t *rwlock);
int pthread_rwlock_trywrlock(pthread_rwlock_t *rwlock);
```
解除锁定，倒是简单，利用**pthread_rwlock_unlock()**即可：
``` Objective-C
int pthread_rwlock_unlock(pthread_rwlock_t *rwlock);
```
一般而言，使用读/写锁可能是一个坏想法，因为它会加倍实际锁操作的次数。读/写锁可以用一把互斥锁及两个条件变量来实现。除非你有一些确乎“昂贵”的数据结构，否则，读/写锁定操作的开销将超过你正在保护的操作。考虑到这一情况，有些时候它们非常有用，特别是当有很多“读者”，却没有很多修改，恰恰访问数据结构很费时的时候！


