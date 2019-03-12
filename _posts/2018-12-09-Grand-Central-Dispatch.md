---
layout: post
title: Grand Central Dispatch
---
{{page.title}}
=======================

多线程编程时常是非常困难的。你有竞争条件，虽说可通过加锁以修正，但是选取一个恰当锁粒度以阻止所有线程序列化同一个代码路径却非常困难。锁太大，它们会强制序列化；锁太小，则必须重复加锁/解锁。而且并行算法也比串行算法更难以理解，或者至少不像对串行算法那般熟悉。

<img src="/images/posts/2018-12-09/theTraditionalLockingModel.jpg">
上图即展示了一个典型场景。这有多个线程，它们都在等着处理一些敏感的数据结构。通过使用锁，临界区(critical sections)就被序列化了。此后，线程阻塞至锁可用，方才操作这些敏感的数据结构。

Mac OS X 10.6及iOS 4引入了Grand Central Dispatch(GCD),它本质上使用了队列机制。它提供一种从事大块工作且序列化它们的方式，这些大块工作表现为block的形式。But does this undo all the work you have done with threads以充分利用多核的优势吗？纯粹地看，是的！

<img src="/images/posts/2018-12-09/workingWithASerialQueue.jpg">
上图展示了GCD的世界观。你有一个串行队列，这个队列呢控制着对敏感数据结构的访问。操纵数据结构的所有工作仅发生在一个线程中，因此你无需处理加锁事项。通过把工作块(block)放入队列，单个线程即可执行在数据结构上的操作。

如果操作是用后即不理类型，the thread can go off an do more work while the sensitive queue gets drained。如果线程需要等着工作完成，它就可以等待直至工作干完，再做其它事。即使在这种情形下，性能也不比等着一个鹬蚌相争的锁差！实际上，GCD的性能优越性是板上钉钉的。一旦你把工作放入队列(以FIFO/先进先出顺序处理工作流)了，你的工作定能得到处理。非但如此，如果你添加了更多的队列，那你就有更多作并行化的机会。

<img src="/images/posts/2018-12-09/multipleSerialQueues.jpg">
此图展现了两个不同的有队列保护的敏感数据结构及把工作单元放入队列的几个线程。每一个数据结构都是串行化访问且未加锁的。通过一些这样面对队列的数据结构，你可以有很好的程序范围内的并行化实现！Apple称此种结构为"并发性海洋中的串行化小岛(islands of serialization in a sea of concurrency)"。程序可优化多处理器的使用以便充分利用用户机器上的可用多核心。随着时间的推移，相信越来越多的应用会这么做。

个别的程序没有系统级的资源消耗。扩展并发度以最优化使用机器上的可用计算资源需要对硬件有充足的认识。GCD与内核合作，因此，它可从系统层面了解谁在竞争计算资源，以及可合理安排任务以便拥有最大吞吐量。简言之，GCD可以承诺的是：如果你给他工作了，它会确保(合理范围内)尽可能快地执行它们。

Grand Central Dispatch是libdispatch库的营销(marketing)名称，它被内置于OS X C库，并且它的所有公开函数和类型均以dispatch为前缀。你可用任一程序来获取GCD的数据类型及函数，而且还不必牵扯任意额外的库及框架。开始使用GCD的所有条件仅是包含<dispatch/dispatch.h>头文件及调用它的函数。也需特殊的编译器和链接器标志。

GCD整合了Foundation及Core Foundation的runloop架构。运行主线程的runloop抽空(drain)GCD的主队列。如果没在使用runloop，将不得不在主线程上调用dispatch_main()以抽空其主队列。
## GCD术语
GCD任务是需要处理的事务，像渲染一个网页或显示一条来自社交网络的新信息。一个任务是你的应用想要达成的一个个分立目标。任务可依赖其他任务，它们也可被拆分为子任务。任务最终会被拆分为work项(work items)。

work项是需要完成的独立工作块。对网页而言，这些工作将会是解析请求、访问数据库、运行模板引擎以及合并结果等等。

任务构建自work项，work项又被放入队列。当work项运行时，分派(dispatching)发生。当它到达队列首位时，它会被委派给一个线程以使得工作可实际执行。GCD运行于thread(线程)之上的一层抽象。线程就变成了具体的实现细节。
## 队列
Dispatch队列是GCD的基础数据结构。一个队列就是包含work项的一个列表。根据定义，队列以FIFO(先进者先出)的方式排空。GCD维护一个线程池，在池内运行从队列取出的work项。work可被提交为一个block或者一个"函数指针/上下文"对。每个串行队列均由一个全局队列来支持。

这有两种队列，即串行队列和全局队列。串行队列以FIFO方式分派和完成它们的work项。串行队列不会有两个同时执行的不同work项。这也是能串行访问敏感数据之所在。在主线程干活的主队列(main queue)便是一个串行队列。

全局队列以FIFO的顺序处理工作，但是它不能保证工作完成的顺序。实际的工作块(work block)运行在GCD维护的一个线程池内，并且它们会受到来自OS内核的系统级均衡。这有不同优先级(高/high，默认/default，低/low)的三个全局队列。全局队列有高效且无限制的快读。而所谓宽度，即可同时运行的work项。很显然，串行队列的宽度为1.

记住这三个全局队列是仅有的并发运行队列是非常重要的。串行队列以FIFO方式来分派及完成任务，而全局队列只是以FIFO的方式分派它们。每个串行队列都需要一个目标队列，这个目标队列是工作最终得到调度以运行的地方。可以让串行队列靶定(target)其它的串行队列。只要队列链条到达了全局队列，分派的工作就能得以完成。像下图展示的这般：

<img src="/images/posts/2018-12-09/dispatchQueueArchitecture.jpg">
一个串行队列的优先级取决于它的目标队列的优先级。通过靶定一个串行队列到默认优先级全局队列上，它的work项就终以调度在默认优先级队列的工作队列中。重新靶定串行队列到高优先级全局队列将导致任意已入队却还未以默认优先级调度的work项运行在高优先级全局队列中(这句太长了)！

串行队列是非常轻量级的数据结构，其体积以比特计而非几KB。但凡对设计有益，可创建任意多有专门用途的队，因为无须担心对其数据结构的管理工作。线程却没这么让人省心，它属于相当重量级的数据结构。那就是说，除非你有理由非得要串行运行work项，比如确保同一时间仅有单独一个线程操纵资源；否则，你应当直接将work项入队到一个全局队列中。
## 面向对象设计
虽然GCD纯以C语言实现，但是它是一个面向对象库。基本的GCD类型是指向不透明(opaque)数据结构的指针，然而也涉及到一个继承模型。dispatch对象的基类是dispatch_object_t。你从不会直接创建一个dispatch_object_t对象；相反，你创建比如队列(source)、源(source)及组(group)这样具体的对象。这也有几个类似dispatch_time_t、dispatch_once_t这样的类型，它们并非对象却属于半透明的标量类型。

<img src="/images/posts/2018-12-09/dispatchClassesAndFunctions.jpg">
现在可以一窥相关的Dispatch API了。
## API之队列
在你可以在队列上调度任务前，你需要掌握一个队列。可以利用
``` Objective-C
dispatch_queue_t dispatch_get_main_queue(void);
```
得到主队列。可利用此队列在主线程上调度任务。这是一个串行队列，所以工作的处理及完成均以FIFO顺序进行。
``` Objective-C
dispatch_queue_t dispatch_get_global_queue(long priority,unsigned long flags);
```
会给你全局并发队列中的一个，它的优先级是DISPATCH_QUEUE_PRIORITY_LOW、DEFAULT及HIGH三者中其一。给flag传一个0UL；此值现在被忽略，作为将来的一个可扩展点。这个优先级指的是哪个队列得到对block的调度权。只有高优先级队列为空时，默认优先级队列中的block才能在线程中运行。类似地，只有高优先级和默认优先级队列上没有待处理(挂起)的block，低优先级队列才会处理block给线程！

可通过
``` Objective-C
dipatch_queue_t dispatch_get_current_queue(void);
```
取得当前队列。“当前队列”是指处理当前工作运行片(running piece of work)的队列。如果你处在主线程或默认优先级的全局队列中，并且未于任务段段中间位置，它将返回主队列。此返回的实际队列可能是令人吃惊的，有一些像串行或并发这样期望之外的属性。Apple建议仅dispatch_get_current_queue()来确认测试结果("Am I really running on the mainqueue")或者调试。

利用
``` Objective-C
dispatch_queue_t dispatch_queue_create(const char *label,dispatch_queue_attr_r attr);
```
创建咱们自己的队列。其中label可以是任意值，但是Apple推荐使用反向DNS命名模式，比如像com.bignerdranch.BigGroovy.FacebookQueue这样。这个label是可选项，但是呢，因为它出现在Instrment调试器中，所以它是崩溃报告中非常有用的一则信息。为attr传入NULL。在OS X 10.6和iOS 4中，它未曾用到，但可作为未来的一个扩展点。自此调用返回的是一个串行队列。

使用
``` Objective-C 
void dispatch_set_target_queue(dispatch_object_t object,dispatch_queue_t target);
```
函数设置队列的目标队列。处理在彼队列中的work项会被放入此目标队列以获取最终的调度。可为任意GCD对象设置目标队列。对非队列的对象而言，这会告诉对象到哪去运行它的上下文指针指向的函数！
## API之分派
现在，你有了一个队列。拿它做点什么呢？你可把工作分给它。dispatch_async() 会扔一大摞工作到队列上，并随即返回。这些工作最终会找到通往全局队列的路，在那，这些要完成的工作运行于某线程中。因为这工作异步发生，所以有可能在dispatch_async()返回前，此要完成的工作已开始运行。如果工作量比较小，还有可能的是，在dispatch_async()返回前，要完成的工作不仅已开始，而且已经完成。

GCD中的很多调用都有两个flavor：一个喂block，另一个拿函数(指针)作参数：
``` Objective-C
void dispatch_async(dispatch_queue_t queue,void (^block)(void));
void dispatch_async_f(dispatch_queue_t queue,void *context,void (*function)(void *));
```
喂函数的变种遵循将f附加到名称末尾的传统，采用一个上下文指针和一个函数指针。它只是一种实现细节，但是GCD中的大部分均有f函数作为block版本的基础实现。你可以选择知道work项被完成才释放(不阻塞)你的线程。利用
``` Objective-C
void dispatch_sync(dispatch_queue_t queue,void (^block)(void));
```
可达成此目的。

需要小心的是，你不能对dispatch_sync()递归调用。若递归调用，则可导致死锁。此情形下，内层(innner)的同步分派不得不等着外层(outer)分派完成，而外层的dispatch_sync()直到内层结束才能完成。

主线程上的runloop会触发GCD为主队列开始处理任务块(block)。如果你手头没有runloop，或者正运行于Core Foundation之下的层级中，你可利用void dispatch_main(void);。这个函数从不返回，它将永远循环，处理添加到主队列中的work项。dispatch_after()允许你安排一个运行于将来某个时间点的block。
## API之内存管理
Grand Central Dispatch接近OS X食物链的底部，居于系统库中。你不会碰到像垃圾收集这样的好事，因此有管理对象生命周期的API。像在Cocoa中，GCD使用引用计数(reference counting)。通过retain一个对象，来表达对它长期的兴趣：
``` Objective-C
void dispatch_retain(dispatch_object_t object);
```
因为GCD对象与dispatch_object_t的继承关系，可用此函数来retain dispatch_queue_t或者dispatch_source_t。像从dispatch_queue_create()这样的调用返回的对象已然为你做了retain。当你处理完一个对象后，释放它。
``` Objective-C
void dispatch_release(dispatch_object_t object);
```
重复释放一个对象会引发程序崩溃，所以要确保在崩溃后检查log。函数库试图在崩溃报告中提供有用的应用方面的信息，比如：
``` Objective-C
BUG IN CLIENT OF LIBDISPATCH:
Over-release of an object
```
队列和dispatch源也有上下文指针，可以在这儿放入一个指向你数据的指针。可用这些函数设置和取回上下文指针：
``` Objective-C
void dispatch_set_context(dispatch_object_t object,void *context);
void *dispatch_get_context(dispatch_object_t object);
```
准确弄清楚一个对象被销毁的时间以便清理上下文指针这件事，并不容易！你倒是可以指定一个终结函数(finalizer function)，在对象被销毁后，它可被异步调用！
``` Objective-C
void dispatch_set_finalizer_f(dispatch_object_t object,
                              dispatch_function_t finalizer);
```
dispatch_function_t接受void指针作参数，并且并不返回什么内容。并且，此函数并没有block版本，奇怪得很！
``` Objective-C
typedef void (*dispatch_function_t)(void *);
```
队列和dispatch源对象均可被暂停及恢复。这些对象有一个"暂停计数(suspend count)"，当此数非0时，会触发队列停止处理work项。在执行一个block(也就是从队列取出一个block)前，需检查它的暂停状态。正在执行的block或者函数不会被抢占，因此，暂停仅仅控制某某开启执行与否。
``` Objective-C
void dispatch_suspend(dispatch_object_t object);
void dispatch_resume(dispatch_object_t object);
```
经常用resume摆平(平衡)suspend是重要的；否则，像终结函数和canclelation handler就得不到调用。过度恢复一个对象也会导致程序崩溃；释放一个暂停的(挂起的)对象属于为定义范畴。











