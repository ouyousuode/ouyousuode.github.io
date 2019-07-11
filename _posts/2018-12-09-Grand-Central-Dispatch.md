---
layout: post
title: 第22章  Grand Central Dispatch
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
## 22.1  GCD术语
GCD任务是需要处理的事务，像渲染一个网页或显示一条来自社交网络的新信息。一个任务是你的应用想要达成的一个个分立目标。任务可依赖其他任务，它们也可被拆分为子任务。任务最终会被拆分为work项(work items)。

work项是需要完成的独立工作块。对网页而言，这些工作将会是解析请求、访问数据库、运行模板引擎以及合并结果等等。

任务构建自work项，work项又被放入队列。当work项运行时，分派(dispatching)发生。当它到达队列首位时，它会被委派给一个线程以使得工作可实际执行。GCD运行于thread(线程)之上的一层抽象。线程就变成了具体的实现细节。
## 22.2  队列
Dispatch队列是GCD的基础数据结构。一个队列就是包含work项的一个列表。根据定义，队列以FIFO(先进者先出)的方式排空。GCD维护一个线程池，在池内运行从队列取出的work项。work可被提交为一个block或者一个"函数指针/上下文"对。每个串行队列均由一个全局队列来支持。

这有两种队列，即串行队列和全局队列。串行队列以FIFO方式分派和完成它们的work项。串行队列不会有两个同时执行的不同work项。这也是能串行访问敏感数据之所在。在主线程干活的主队列(main queue)便是一个串行队列。

全局队列以FIFO的顺序处理工作，但是它不能保证工作完成的顺序。实际的工作块(work block)运行在GCD维护的一个线程池内，并且它们会受到来自OS内核的系统级均衡。这有不同优先级(高/high，默认/default，低/low)的三个全局队列。全局队列有高效且无限制的快读。而所谓宽度，即可同时运行的work项。很显然，串行队列的宽度为1.

记住这三个全局队列是仅有的并发运行队列是非常重要的。串行队列以FIFO方式来分派及完成任务，而全局队列只是以FIFO的方式分派它们。每个串行队列都需要一个目标队列，这个目标队列是工作最终得到调度以运行的地方。可以让串行队列靶定(target)其它的串行队列。只要队列链条到达了全局队列，分派的工作就能得以完成。像下图展示的这般：

<img src="/images/posts/2018-12-09/dispatchQueueArchitecture.jpg">
一个串行队列的优先级取决于它的目标队列的优先级。通过靶定一个串行队列到默认优先级全局队列上，它的work项就终以调度在默认优先级队列的工作队列中。重新靶定串行队列到高优先级全局队列将导致任意已入队却还未以默认优先级调度的work项运行在高优先级全局队列中(这句太长了)！

串行队列是非常轻量级的数据结构，其体积以比特计而非几KB。但凡对设计有益，可创建任意多有专门用途的队，因为无须担心对其数据结构的管理工作。线程却没这么让人省心，它属于相当重量级的数据结构。那就是说，除非你有理由非得要串行运行work项，比如确保同一时间仅有单独一个线程操纵资源；否则，你应当直接将work项入队到一个全局队列中。
## 22.3  面向对象设计
虽然GCD纯以C语言实现，但是它是一个面向对象库。基本的GCD类型是指向不透明(opaque)数据结构的指针，然而也涉及到一个继承模型。dispatch对象的基类是dispatch_object_t。你从不会直接创建一个dispatch_object_t对象；相反，你创建比如队列(source)、源(source)及组(group)这样具体的对象。这也有几个类似dispatch_time_t、dispatch_once_t这样的类型，它们并非对象却属于半透明的标量类型。

<img src="/images/posts/2018-12-09/dispatchClassesAndFunctions.jpg">
## 22.4  Dispatch API
### 22.4.1 API之队列
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
### 22.4.2 API之分派
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
### 22.4.3  API之内存管理
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
队列和dispatch源对象均可被暂停及恢复。这些对象有一个"暂停计数(suspend count)"，当此数非0时，会导致队列停止处理work项。在执行一个block(也就是从队列取出一个block)前，需检查它的暂停状态。正在执行的block或者函数不会被抢占，因此，暂停仅仅控制某某开启执行与否。
``` Objective-C
void dispatch_suspend(dispatch_object_t object);
void dispatch_resume(dispatch_object_t object);
```
经常用resume摆平(平衡)suspend是重要的；否则，像终结函数和canclelation handler就得不到调用。过度恢复一个对象也会导致程序崩溃；释放一个暂停的(挂起的)对象属于未定义范畴。
## 22.5  WordCounter
<img src="/images/posts/2018-12-09/wordCounter.png">
此WordCounter程序不仅能计算文本域内单词的总数，还能计算到底有多少个不同的单词。这两个值更新在文本域中。此应用的代码是比较明确的，在Xcode中创建一个名叫WordCounter的新Cocoa App。像应用界面中那样，从对象库中取一个NSTextView，一个count按钮，一个进度提示器以及两个为不同计算的标签。简单配置对象的布局，更新工程代码：

<img src="/images/posts/2018-12-09/viewControllerHeader.png">
在Interface Builder中完成关联。ViewController.m中有真正干活的代码，当然文件的开头都是标准的样板戏：

<img src="/images/posts/2018-12-09/getWordCounter.png">
我们需要在getWordCount方法中获取两个值，即单词总数和一个记录单词频率的NSCountedSet。让函数一次返回两个值，其一，构造一个包含此二值的结构体，计算结束后，将其返回；其二，给函数传入指针，对地址进行操作。从代码中可看到，我们采取后者。实际上，由应用的功能看，此函数承担了比较繁重的工作。紧接着，在按钮handler中调用getWordCount方法。

<img src="/images/posts/2018-12-09/count_0.png">
这个方法得到文本总字数及各单词出现的频率，并且将它们置于标签上。现在，构建、运行应用。(正常工作)效果立现。但是因为getWordCount耗时数秒，你将得到一个提示UI已被锁定的Spinning Pizza of Death。在桌面系统中，一个被锁定的(locked up)应用虽有些烦人但也不算大事。你可先去干点儿其它事，随后再回来！但若在移动设备，这就是大问题了。如果你的应用UI被锁定了，用户除了盯着屏幕(执手相看泪眼)外，将一筹莫展。而且如果锁定时间太长，比如超过15或30秒，iOS系统的看门狗(watchdog)将杀掉你的程序。

WordCounter放入2秒睡眠时间，来模拟有很多需要完成的工作情况。现如今的机器都太快以至于不会被这样的简单代码绊住脚步。一台MacBook Pro用不了1/4秒便可计算此中(/usr/share/dict/words)的全部文本，哪怕其中235,000个单词之多。但是，这仍有可能阻塞主线程。

解决方案是不在主线程同时做如此多繁重的工作。针对此，可以用多种方法实现。你可以将任务"切片"，然后在主线程以异步方式处理这些任务片。计算1000个单词，而后返回到RunLoop。再计算1000个单词，而后再返回到RunLoop，如此一而再再而三地往复执行，知道全部计算完成。当然，也可以派生一个pthread或者NSThread来处理这任务。

以上两方法的负面影响是皆有较大的工作量。针对第一种方法，需要重写代码；在线程方案中，需要一个与主线程汇合且得到结果的方式，更别提还须设置一个运行于实际线程上的线程函数。

Grand Central Dispatch 使“扔些任务到后台线程，再扔些任务到主线程”类工作变得很容易。一个通常的"GCD is Cool !" demo是放一个dispatch_async()到后台线程以计算(getWordCount),一个dispat_async()到主线程以更新标签。实际的工作可能稍有复杂，但也复杂不到哪去。并且，肯定比手动派生一个其他的线程简单得多！

你将要处理的一件事是阻止用户与窗口的控件进行交互。别误会,仅指当计算进行时，你肯定不想用户再去点按"Count !"。当然也不会想用户去改变窗口内的文本内容。当然了，在窗口放一个进度提示器就更用户友好了。它可以让用户知道某些事正在进行中！我们用两个辅助方法来完成这件事：

<img src="/images/posts/2018-12-09/disableEnable.png">
现在，代码GCD化之前，把disableEverything与enableEverything代码块放入IBAction方法内：

<img src="/images/posts/2018-12-09/count_1.png">
现在，弄明白：什么需要运行在后台，什么需要运行在主线程。UI操纵相关的代码需运行在主线程，-(void)getWordCount:addFrequencies:forString:需要发生在后台线程。对此了然后，便可编写最终的GCD增强版了。

<img src="/images/posts/2018-12-09/count_2.png">
现在，可以一究此方法如何运行了。首先，-disableEverything被调用。IBAction得到处理于主线程，所以为了此方法，代码正在主线程。UI被禁用，接着，方法的剩余部分是一个置于默认优先级全局队列的block，接着，此方法退出并且控制权返还到RunLoop手里。

在将来的某个时间点，此block会被从队列头部取出并运行。它做的第一件事就是调用-(void)getWordCount:addFrequencies:forString:。接着，另一个block被放进主队列。在未来的某个未指定的时刻，主队列的block开始运行，更新UI。

使用GCD时，这是一种常见的编码模式。利用嵌套的block：外层的block放入队列，任务完成后，像洋葱般被剥掉；内层的block被放进队列，待此block运行后，它也被剥掉；一个更内层的block开始运行！
## 22.6  Iteration
甚至简单如for循环迭代处理数据的任务也可由GCD增强。迭代一个数组并于每个元素执行操作的经典方式是通过使用循环(loop)。
``` Objective-C
enum {
	kNumberCount = 200000
}
static int numbers[kNumberCount];
static int results[kNumberCount];

for(int i = 0;i < kNumberCount;i++) {
	result[i] = Work(numbers,i);
}
```
其实，你可以利用dispatch_apply()并行化这些操作。调用(call)本身是同步的，所以你清楚当函数返回后，所有的工作便全部完成了。但是dispatch_apply()会找出尽可能多的CPU来把这事做到最好。
``` Objective-C
dispatch_queue_t queue = 
               dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT,0);
dispatch_apply(kNumberCount,queue,^(size_t index) {
	results[index] = Work(numbers,index);
});
```
这与NSArray的-enumerateObjectsWithOptions:usingBlock:很类似，当然在给它NSEnumerationCouncurrent选项的前提下。须确保的是，为dispatch_apply()使用一个全局队列。使用串行队列会串行化(序列化)你全部的工作，使任意并行化操作的尝试劳而无功。
## 22.7  安全的全局初始化(Safe Global Initialization)
有时你需要执行只有一次的(one-time)初始化，并且无论有多少线程在想着初始化发生，你都只能执行一次此操作。这经常用于懒初始化，或使用单例模式时，创建那唯一的真对象。虽然有一个称为Double-Clicked Locking技术，但难在正确使用。可pthread_one()来序列化一个只有一次的(one-time)初始化。当然，也可以使用dispatch_once()。
``` Objective-C
void dispatch_once(dispatch_once_t *predicate,void (^block)(void));

static dispatch_once_t initializationPredicate;
static blah *stuffToInitialize;

dispatch_once(&initializationPredicate,^{
	stuffToInitialize = goop();
});
```
其中，dipatch_once_t被用作守护变量，不论有多少线程和内核想着初始化发生，dispatch_once()将仅会准确地执行一次它的block！如果可能的话，尽量使用与pthread功能相似的dispatch族函数。这些dispatch函数有着最好的性能表现。
## 22.8  Time,Time,Time
当处理真实世界的代码时，经常需要和时间打交道，无论是有一些过会儿便超时的调用，还是安排发生在特定时间的事儿。GCD称这些时间为“时间里程碑”，并且以dispatch_time_t展现它们。此类型是一个“semi-opaque(半透明)”整型；它有一个整型(数)值，但是其内容易受改变。时间可被表达为针对其它时刻的相对时间，也可以是两个非常易分辨的时刻——现在(now)和永远(forever)：
``` Objective-C
static const dispatch_time_t DISPATCH_TIME_NOW = 0;
static const dispatch_time_t DISPATCH_TIME_FOREVER = ~0ull;
```
可将DISPATCH_TIME_FOREVER作一个无限超时(infinite timeout)。

有两种不同的时间用在GCD中。“主机时钟(host clock)”，也被称为系统时钟，它基于操作系统内的一个计数器。只要机器在运行，计数器就会增长。休眠机器也会冻结主机时钟。如果设置一个未来1小时的超时接着休眠30分钟，则此操作实际上超时于1.5小时后。

另一种时钟是“挂钟(wall clock)”，它代表了可被手表或书桌上的日历报告的时间。即便机器睡眠了，或者向前/后调了时间，wall clock仍会一如既往地向前走。用户有时会旅行至一个不同的时区，或会切换夏令时的区域。因此，用wall clock time设置一个未来1小时的超时，休眠机器30分钟，结果是，超时会发生在30分钟后。

dispatch_time()会返回针对已知里程碑的一个时间值，当然，发生在喂给它一个纳秒值后。
``` Objective-C
dispatch_time_t dispatch_time(dispatch_time_t base,int64_t nanoseconds);
```
如果基准时间是挂钟时间，那么返回值也为挂钟时间。DISPATCH_TIME_NOW作为主机时钟的基准。

利用dispatch_walltime()来创建一个针对固定时间点的里程碑时刻。
``` Objective-C
dispatch_time_t dispatch_walltime(struct timespec *base,
                                  int64_t nanoseconds);
```
可传入一个针对特定时间的struct timespec，它为自Unix时间戳1970之后的多少秒及多少纳秒。当然，传入NULL后，即表示当前时间。

未来的第23秒可以这样创建：
``` Objective-C
dispatch_time_t milestone = dispatch_time(DISPATCH_TIME_NOW,
                                          23ull * NSEC_PER_SEC);
```
对常量的修改符ull避免数值被截断为32位。USEC_PER_SEC表示微秒；NSEC_PER_SEC则表示纳秒。它们保证了在微秒与纳秒间的转换。

通过把时间放入结构体tm并利用mktime()将其转化为struct timespec后，可设置一个针对某绝对时刻的超时：
``` Objective-C
struct tm tm = {0,};
strptime("2016-09-13 13:13","%Y-%m-%d %H:%H",&tm);

strtuct timespec ts = {};
ts.tv_sec = mktime(&tm);

dispatch_time_t time_out = dispatch_walltime(&ts,0);
```
可在对dispatch_after()的调用中使用dispatch_time_t:
``` Objective-C
void dispatch_after(dispatch_time_t when,
                    dispatch_queue_t queue,
					void (^block)(void));
```
此函数入队一个指定时间后才运行的work项(work item)。向dispatch_afer()传入DISPATCH_TIME_NOW与调用diaptch_async()有异曲同工之妙。
## 22.9  Dispatch群组
有时，你想把工作分散于几个不同的队列中，然后擎等着它们完成。比如说,(在服务器)渲染页面可能涉及到访问数据库、渲染文本以及追加任意留在页面上的评论。

一个dispatch_group就是包含block的一个集合。这些块被异步地分散于队列中。为创建一个追踪块集合地group，可用：
``` Objective-C
dispatch_group_t dispatch_group_create(void);
```
``` Objective-C
void dispatch_group_async(dispatch_group_t group,
                          dispatch_queue_t queue,
						  void (^block)(void));
```
此情景中，添加blcok到gorup中，然后将它dispatch_async()到给定的队列。当组内的所有这些block被完成后，这有两种得到通知的方式。可指定一个blcok运行于dispatch_group_notify().
``` Objective-C
void dispatch_group_notify(dispatch_group_t group,
						   dispatch_queue_t queue,
						   void (^block)(void));
```
在group完成安排给它的最后一个block后，它会dispatch_async()此通知块(block)进队列。也可阻塞至group抽空时，若达成此目的，可赋予dispatch_group_wait()一个超时。
``` Objective-C
long dispatch_group_wait(dispatch_group_t group,
                         dispatch_time_t timeout);
```
如果在timeout前，group已抽空，此函数返回0；如timeout已过，则返回一个非0值。如若想达用不超时之效果，可给它一个DISPATCH_TIME_FOREVER。可利用dispatch_group_enter及dispatch_group_leave来控制组内的当前work项个数：
``` Objective-C
void dispatch_group_enter(dispatch_group_t group);
void dispatch_group_leave(dispatch_group_t group);
``` 
进入dispatch_group可将work项个数加1，离开则减1。直到此值变为0，dispatch_group才会发个通知。

现在，展示几个实际应用的dispatch group。

<img src="/images/posts/2018-12-09/groupCode_0.png">
<img src="/images/posts/2018-12-09/groupCode_1.png">
创建一个dispatch组，将其中的两个block分派给全局队列，剩余的一个分派给主队列。添加一个通知handler，像work项般等在dispatch组中。“等待”自然运行在全局队列(即后台线程)以便主线程可以畅快地运行分派到主队列的block。

最后，运转runloop十秒钟以抽空主队列中的所有work项。程序运行(./group)后，将产生以下输出：

<img src="/images/posts/2018-12-09/resultOfGroup.png">
这有一些需要注意的趣事。在starting to wait发生前，第一个work项("I seem to be a verb")就已完成了。最可能已开始处理的work项("background grovvy!")却尚未结束。也可以观察到，background groovy项在主线程结束休眠前已结束。仍需要注意的是，分派给主队列的work项直到runloop启动才得到运行。

其实，从运行结果去理解代码逻辑，还是很清晰的。
- 默认优先级的全局队列首先运行，得 I seem to be a verb 及 starting to wait。
- 5秒后，低优先级的全局队列输出 background groovy。
- 又5秒后，启动主线程的RunLoop，故输出运行于主队列的main queue groovy！
- 所有的work项执行完毕后，输出Wait returned with result: 0。

dispatch_group_async()的一个实现可显示使用dispatch_group_enter()和leave()的方法。
``` Objective-C
void groovy_dispatch_group_async(dispatch_group_t group,
								 dispatch_queue_t queue,
								 dispatch_block_t work) {
	dispatch_retain(group);
	dispatch_group_enter(group);

	dispatch_async(queue,^{
		work();
		dispatch_group_leave(group);
		dispatch_release(group);
	});
}
```
此处有一个有意思的关键点：在enter前，group被持有(retain),并且直到leave后才释放(release)。这会阻止group在被调用和销毁前becoming empty。在分派work项前进入组，在实质work项结束后离开。并且，需要清醒认识的是：在dispatch组内仍有work项时就归置它，应用会崩溃。重复离开group也会引起崩溃。

## 22.10  Dispatch源
Dispatch源(source)代表事件流。当一个与dispatch source相关的事件发生时，一个事件handler(比如一个block)会被安排到一个设定好的队列中。可用GCD函数生成一个新的dispatch源：
``` Objective-C
dispatch_source_t dispatch_source_create(dispatch_source_type_t type,
						 uintptr_t handle,
						 unsigned long mask,
						 dispatch_queue_t queue);
```
type是以下几个常量中的一员：
``` Objective-C
DISPATCH_SOURCE_TYPE_DATA_ADD
DISPATCH_SOURCE_TYPE_DATA_OR
DISPATCH_SOURCE_TYPE_MACH_SEND
DISPATCH_SOURCE_TYPE_MACH_RECV
DISPATCH_SOURCE_TYPE_PROC
DISPATCH_SOURCE_TYPE_READ
DISPATCH_SOURCE_TYPE_SIGNAL
DISPATCH_SOURCE_TYPE_TIMER
DISPATCH_SOURCE_TYPE_VNODE
DISPATCH_SOURCE_TYPE_WRITE
```
鉴于你已在kqueue()见到了signal、sockets、files以及process，所以这些常量应该挺相似的。当然，也有不同的，如mach消息。也可以创造自己的定制source以处理GCD内其它种类的事件。Dispatch源就像使用kqueue()但无需创建你自己的kernel队列。当有新事件发生时，你的block就被自动调度到队列中。

handle与mask参数到期望值依赖你正创建的dispatch源类型。比如，signal源类型会用signal号码作为handle。

当有趣的事情发生为dispatch source后，queue就是事件处理block将入队的队列。需用**dispatch_source_set_event_handler()**设置handler block：
``` Objective-C
void dispatch_source_set_event_handler(dispatch_source_t source,
                         void (^block)(void));
```
dispatch source创建于挂起状态(suspended)状态。在为它们调用dispatch_resume()前，它们一直无所事事。这让你完成所有关于source的配置工作而无须担心它们背着你擅作主张。当你处理完某个dispatch source之后，你需要**dispatch_source_cancel()**它：
``` Objective-C
void dispatch_source_cancel(dispatch_source_t source);
```
也可以设置一个handler block用于dispatch source被取消时：
``` Objective-C
void dispatch_source_set_cancel_handler(dispatch_source_t source,
                         void (^block)(void));
```
像监测mach端口和文件描述符这样的源(source)类型需要一个"取消handler"，可于其中关闭文件描述符。如果在"取消handler"运行前关闭文件描述符，这可能会导致竞争条件，此种情况下，此文件描述符重用于另一个文件，而恰逢有一个要运行的block，如此便可读写这个完全不同的文件了。

取消(cancellation)不会中断任意当前正在运行的handler block。故而，通过调用**dispatch_source_testcancel()**在handler内部测试取消(cancellation)与否:
``` Objective-C
void dispatch_source_testcancel(dispatch_source_t source);
```
如果源(source)已被取消，此方法会返回一个非零值。也需要为你的dispatch源设置一个handler。这个handler是可以安排到你于**dispatch_source_create()**指定队列中的一个block。
``` Objective-C
void dispatch_source_set_event_handler(dispatch_source_t source,
                          void (^block)(void));
```
在此handler内部，你可以查询此源。比方说，你可以得到用以创建此handler的handle和mask参数值：
``` Objective-C
uintptr_t dispatch_source_get_handle(dispatch_source_t source);
unsigned long dispatch_source_get_mask(dispatch_source_t source);
```
你也可以利用**dispatch_source_get_data()**获得针对每个handler的pending "data"，比如，在一个fd上可读的字节数或者某信号已发出的次数。
``` Objective-C
unsigned long dispatch_source_get_data(dispatch_source_t source);
```
下图列出了不同的dispatch源以及它们的moving部分。

<img src="/images/posts/2018-12-09/dispatchSourceTypes.jpg">

### 22.10.1 Dispatch源之信号
彷佛我们已经没有了足够的信号处理API，GCD则添加了它自己的。那就是说，这可能是在OS X和iOS上处理信息的最简单方式。源类型为DISPATCH_SOURCE_SIGNAL，handle是信号数字，并且mask未用到，故留置为0。当一个SIGUSR1信号传递给应用后，此代码会将其打印在主队列上。

<img src="/images/posts/2018-12-09/dispatch_source_signal.png">
即使与kqueue相较，这也是一种处理信号的非常简洁的方式。注意：这并未取代现存的信号处理机制。你仍旧可以kqueue此信号。不要忘记**SIG_IGN**此信号以便默认的信号处理行为不会杀掉你的进程。仍须注意到：在handler开始工作前，不得不**dispatch_resume()**它。
### 22.10.2 Dispatch源之文件读取
很像kqueue的EVFILT_READ，DISPATCH_SOURCE_TYPE_READ将监测文件描述符(file descriptor)并且告诉你何时可读。handle是文件描述符；未曾用到mask，所以给它传个zero。**dispatch_source_get_data()**返回的数据是可从descriptor读取的统计完毕的字节数！此为一个最小的read buffer大小，但系统并不保证它将实际读取这个数目的字节。Apple建议使用非阻塞I/0并处理可能出现的错误情况。这与kqueue不同，在阻塞前，它告诉你可以读取的字节数。以下代码从标准输入读取并随它们进入，打印读取的字符：

<img src="/images/posts/2018-12-09/dispatch_source_file_read.png">
注意：标准输入文件描述符被设定成了**非阻塞模式**。当有需读取的数据时，事件handler block最终运行于一个后台线程上。在这，它只是读取放入buffer的最大数量；当然，如果可用字节数比buffer size小，那就只读取这些可用字节。一旦到达文件末尾，此源(dispatch source)会取消它自己，导致cancellation handler开始运行，其结果为handler关闭文件描述符且释放dispatch source ！
### 22.10.3 Dispatch源之文件写入
文件写入源工作起来像kqueue的EVFILT_WRITE。这些源监测文件描述符，为了可供写入点buffer空间。dispatch source handle是文件描述符；未用到mask参数，所以置为0.

使用write源比用kqueue困难了点儿，因为源不会告诉你阻塞前可写入多少数据。负责DISPATCH_SOURCE_TYPE_WRITE dispatch源的文件描述符应被设置为非阻塞I/O，并且你应处理任何被删减的写入或可能发生的错误。
### 22.10.4 Dispatch源之定时器
定时器源是一种需将event handler block周期性提供给queue的dispatch源，利用给予**dispatch_source_set_timer()**的interval参数：
``` Objective-C
void dispatch_source_set_timer(dispatch_source_t source,
                               dispatch_time_t start,
							   uint64_t interval,uint64_t leeway);
```
系统会尽最大努力及时地将event handler block提交到queue，但是，block实际运行的时间可能稍晚些！start是定时器的初始启动时间。使用DISPATCH_TIME_NOW以立即启动定时器。interval以纳秒计，并且告诉定时器源其定时器的时间间隔。leeway是给系统的一个提示，即它可推迟定时器的调度以帮助全局的系统性能。此值也以纳秒计。当然，对所有的定时器而言，即使你用一个0秒的leeway，也会有一些延迟；因为，从根上讲，OS X就不是一款实时操作系统。

定时器会一直重复直到程序退出或**dispatch_source_cancel()**被调用。如果你想要一款一次即休的定时器，让此定时器的event handler取消它自己即可！

当创建dispatch源时，未曾用到handle和mask参数，故均应设为0。自**dispatch_source_get_data()**返回的数据是定时器被激发的次数，当然从evnet handler block最后一次调用算起。以下代码将以2秒的频率向标准输出打印“timer”。当然了，直到dispatch源被resume，定时器才真正启动。

<img src="/images/posts/2018-12-09/dispatch_source_timer.png">
### 22.10.5 Dispatch源之定制
通过指定一种DISPATCH_SOURCE_TYPE_DATA_ADD源类型来创建定制的源。也要像配置其它几种源那样，配置它们。利用**dispatch_source_merge_data()**来触发它们：
``` Objective-C
void dispatch_source_merge_data(dispatch_source_t source,
                                unsigned long data);
```
data是一个无符号长整型数，它被合并到已经积累的数据中。只有被合并的数据量非0时，源才被触发。因此，如果你想确保source会触发，就必须确保提供的数据量非零。数据源的event handler每次调用后，数据缓冲器即被清零。

如果被并入的数据与你并无相干，并且你仅想触发此源，那么，自不必担心类型或数据。仅仅创建一个定制源，草建handlers并用一个非零值触发它即可！
### 22.10.6 Dispatch源之表象之下
Dispatch源实际上是附带些小物件儿的dispatch queue。这就解释了它们有一个目标队列属性以及一个意义非凡的suspend count属性。除了定时器和定制化source之外的每个源，均以被表示为一个添加到一个单一kqueue中的kevent结束。这个kqueue被一名GCD队列管理者拥有。如果你尝试去追踪一枚kqueue并不支持的文件描述符，GCD会败下阵来，只能利用**select()**。

管理者队列自己负责处理定时器及定制化源。它们利用伪-kevent以适配管理者队列的kevent中的设计。Dispatch定时器被保存在两个列表中，分别对应walltime或clocktime中的一种。列表以升序排列；以一名哨兵值作终止，此哨兵有一个DISPATCH_TIME_NOW的启动值！

## 22.12  GCD或者NSOperation ?
现在，已见识到了构建代码以支持面向任务并行化的两种不同方式，即NSOperation和Grand Central Dispatch。

使用GCD时，针对某个特定的代码倾向于集中于一处。你有一个干活的block，然后放一些block到其它队列来完成其它工作，简直是一完美的剥洋葱故事。GCD执行地很快，几乎没有与之有牵连的累赘。也没有对KVO的依赖。GCD调用倾向于更点对点化，处理"I will just put this here to run this on the main queue"类的事。另外，复制、粘贴及创建处理相同任务的类似block是非常容易的。

NSOperation倾向于使代码更分散。实现一个操作的代码通常在它自己的类中且有自身的源代码。Operation(操作)位于一种更高层级的抽象中。NSOperation允许Operation间的依赖。在GCD中构建类似功能可能会比较复杂，因为需要构建dispatch组，利用可计算的信号量来进行控制。Operation也易于取消，但在Group中，你须创造自己的取消机制。最后，Operation经常表示为类，所以它们是更具体的可重用对象。

如果你需要在后台完成一些工作，你需要慎重考虑使用GCD还是NSOperation以拆分这些工作。当然了，离原生线程越远，你会越高兴。

以上内容是围绕GCD"是什么"以及"怎样用"来展开的，是站在client的角度；然则，我们也可以从实现者的角度，深入理解其内部实现原理。怎么办呢？Apple开放了这部分源代码，可以先找一个早期版本拆解其实现思路，比如[libdispatch-84.5.1](https://opensource.apple.com/tarballs/libdispatch/)；然后，再打开最新版，比较二者间差别。

<img src="/images/posts/2018-12-09/libdispatch-84.5.1_0.png">
<img src="/images/posts/2018-12-09/libdispatch-84.5.1_1.png">
