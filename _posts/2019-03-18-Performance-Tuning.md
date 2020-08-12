---
layout: post
title: 第10章  性能优化
---
{{page.title}}
=======================
性能问题通常来自以下5个领域的一或多个：算法、内存、CPU、硬盘以及图形(Graphics)。的确，这几个方面是你的程序通常与之打交道的。尽管某个问题可能出现在多个方面，你可利用性能工具孤立地查看某一方面带来的影响。
### 10.3.1  内存
虽然现代计算机有很足量的内存(我更愿称之为主存储器，因它未在CPU内部)，但是RAM仍然是一种很稀缺的资源。一旦你的应用或系统的其它应用填(占)满了内存，Mac OS X将开始发送内存页到硬盘，其结果必然是损毁性能。在iOS设备上，低内存情形下，你的程序可能会被杀掉。

典型地，如果你执行优化以减少内存使用量，你将得到在执行时内存占用量降低的结果，原因在于处理器不会等待自内存到达的额外数据(Typically if you optimize to reduce your memory usage,you will often get reductions in execution time because the processor is not waiting for that extra data to arrive from memory)。另外，因为Mac OS X是个共享式系统，可能有多个用户同时登录，也可能多个用户的很多应用在同时运行，因此有必要保守对待内存使用。当每个进程有它自己用于玩耍的wide-open地址空间，这会是一个严格规定(tough discipline)。
#### 内存之Locality of Reference
"Locality of Reference"描述了相互紧挨着的内存访问。从一个4k页中读取一百比特比从散布于地址空间的100个不同页各读取一比特快得多！当你从内存请求数据时，处理器实际上抓取称为cache line的一个比特串序列；它假定你将以共同的方式来访问内存。所以你设置循环来循环地操作内存，你可以看到一个性能提升。locality.m程序创建了一个巨大的二维全局数据，并以两种不同的方式访问它。

<img src="/images/posts/2019-03-18/codeOfLocality.png">
``` Objective-C
./locality 及本机配置
```
<img src="/images/posts/2019-03-18/resultOfLocality.png">
<img src="/images/posts/2019-03-18/computerForLocality.png">
for循环的一个简单颠倒便可导致10倍的性能惩罚！第一个循环遵循了数组内存管理的方式。此循环访问临近的字节，因此随着它走遍数组，它有良好的locality of reference表现。内存页仅被访问一次；在循环体停止操纵一个页上的内存后，此页将不再使用。第二个循环体工作起来像穿越横纹(Bad memory access pattern所示)。每次通过循环，使数组用到的每页都被访问一次。如此每页均处于warm状态(会一直处于内核的数据结构内)，导致内核持续刷新它的least-recently-used页列表，这种情况给虚拟内存系统造成很大压力。第一个循环对内核更友好的原因在于，一旦工作完成便不再接触当前页。一旦页被移除内核的数据结构，它便不再可见。

<img src="/images/posts/2019-03-18/Figure_10_1.png">
<img src="/images/posts/2019-03-18/Figure_10_2.png">
#### 内存之缓存
提高性能的一个常用手段是缓存化；此之后，可保持对已加载或计算过的数据访问。如果你不仔细些，这项技术也会有一些缺点，即系统占据过多的虚拟内存及分页化现象。回想：最近未被访问过的内存可被paged out到硬盘，那么RAM中的空间就可被其它进程使用了。iOS设备不会page数据到硬盘，因此dirty页是常驻内存的。

<img src="/images/posts/2019-03-18/Figure_10_3.png">
如果你选择缓存信息，最好将缓存数据和描述缓存数据的元数据(metadata)分开。你不会想使用像Figure 10.3那样的结构，它将缓存数据和元数据混合到了一块。相反，像Figure 10.4那样组织你的数据。保持元数据待在一起，因为当你穿越(walk through)缓存区域找寻期望的对象时，会有良好的locality of reference。你甚至可以做自己的虚拟内存形式：如果一个cache entry已经有段时间未被用到了，或者你得到了一个iOS内存告警，你可将此团数据移除内存；当然，当需要时，再次将其加载进内存。

<img src="/images/posts/2019-03-18/Figure_10_4.png">
#### 内存之Memory is the New I/O
促使程序员缓存读自硬盘数据的动因时访问硬盘I/O极耗时间资源。等待一次硬盘I/O读取可耗费数十万(甚至更多)个CPU循环，而这段时间本可另作其它更好的用途。

伴随着今天的处理器，内存子系统、总线结构以及RAM已然变得像I/O了。有时，与CPU速度像较，访问内存可能是极其缓慢的。比如，根据Apple技术笔记，G5利用从内存加载一个cache line的时间可做16到50次向量相加。而且，在现代处理器上，这种情况正变得越来越糟糕。

与强力计算相比较，“precalculate and store in memory”技术可能已到瓶颈。CPU可促使一些计算比从内存获取快得多，查找表可强制更多重要数据移出CPU缓存。技术笔记继续提到“In one example,vector code that converts unsigned char data to float and then applies a 9th order polynomial to it is still marginally faster than hand turned scalar code that does a lookup into a 256 entry lookup table containing floats.”

<img src="/images/posts/2019-03-18/cache_memories.png">
<img src="/images/posts/2019-03-18/example_of_memory_hierarchy.png">
距CPU逻辑单元最近到缓存(Level-1 Cache)有一个存放指令的区域，但是每个核仅有32到64KB大小。像会增加代码长度的优化操作可能会被移出此缓存。它成为了一个在代码体积与存取数据间的平衡艺术。有时，trial-and-error是观察何种技术可致最佳性能的一种方式，尤其是如果你有高性能科学建模或需要快速处理大数据(如处理视频)任务时。

C语言的Semantics(语义学、词义学)可达到智能优化的目的，尤其是与内存有关时。如果编译器清楚某块内存是被如何访问的，它便可以将数值缓存在寄存器中，甚至可以避免加载已被加载过的数据。因为C语言有指针，there can be aliasing problem。在进程的某处有个指针，它正指向编译器本可优化访问的一块内存。这也是为什么有些没有指针的语言(像FORTRAN)可以执行更彻底优化的原因。

当在一个循环中由指针或全局变量使用数据结构时，编译器会在每次执行循环体时加载它在内存中的位置。It does this just in case the value was changed by someone else,either in another thread or by a function called inside the loop。创建一个局部变量来持有对应全局变量的值，以便让编译器了解此数据不会改变，最终避免每次循环皆访问内存。
### 10.3.2  CPU
当面临优化事务(issue)时，大部分程序员第一个想到的度量标准便是CPU使用量。“我的应用正在拽住CPU,我需要给它加速。” 通常，当CPU使用量成为一个主导性因素时，其根本原因是一个缓慢低效的算法。它可能有一个高复杂性，也或者它只是一个糟糕的实现方式。在几乎所有的情形中，与其它种类代码或系统调整相比，改写你的算法会给你更大程度的加速。一个经典例子是将冒泡排序(N^2复杂度)改为快速排序(nlgn复杂度)。

有时，一个算法的坏实现可导致比较大的破坏。比方说，某个SunOS 4.1.x显示**strstr()**编程错误，竟将O(N)操作变成了O(N^2)操作：
``` Objective-C
	while(c < strlen(string)) {
		// do stuff with string[c]
	}
```
回想，C字符串并不存储它们自身的长度。一个字符串只是一个以0结尾的比特序列。**strlen()**不得不遍访整个字符串方才得以计算全部字符数。虽有很多小手段可用于此过程，但它仍是一个O(N)操作。在这个特定的例子中，字符串的长度不会发生改变，因此没有必要每次循环都去计算其长度。

所幸，通过注意固定在Activity Monitor中的CPU测量仪，或**top**正显示你的应用在耗费99%的可用CPU能量，高CPU使用量会很容易被发现。采样(sampling)和侧写(profiling)工具是追踪此类问题之原因的理想工具。
### 10.3.3  硬盘
硬盘访问是很缓慢的——比内存访问慢了几个数量级。一般而言，如果能避免硬盘I/O，则避免。如果你打算从硬盘缓存数据，要记得：虚拟内存系统也使用硬盘。如果你缓存了大量的数据，最后会导致VM系统去做硬盘I/O操作。这是一个很糟糕的情况，因为you have now exchanged one disk read (from disk into memory) into a read and a write to page it out and then another read to page it back in from the disk into memory。

在涉及VM分页化，当优化硬盘访问时，locality of reference扮演着重要角色。伴随着差的locality of reference，你做会接触(touch)很多页。这些页触发其它页page out出VM缓存，以便发送到硬盘上。最终你会再次touch它们以获取这些页上的数据，如此过程便又导致硬盘I/O。

通过不把所有工作都做完，你可以避免一些硬盘I/O耗费。一个常用的技巧是：将窗口们(windows)放入不同的.nib文件内，再按需加载。如果你不需要显示某个窗口，那就没理由把它加载进内存。

类似地，如果你有一个大信息数据库，渐进访问比把全部内容加载进内存获得更高效的加速。使用内存映射(memory-mapped)的文件可避免硬盘活动，因为只有文件被touch的部分才会被载入内存。
### 10.3.4  Graphics
Mac OS X的Quartz图形引擎放了很多担子再内存系统的肩上。Quartz使用很大的图形缓冲器，每个缓冲器负责一个现实在屏幕上的窗口。还有一些用于渲染用户桌面的组合操作。虽然这其中很多操作已被移植到图形卡的图形处理单元了，但是Quartz仍会利用CPU来实现一些绘制效果。还有些在GPU上完成得不好的操作，就必须在CPU上执行了！

优化graphics的关键是尽你所能避免绘制操作发生。利用[Quartz Debug](https://developer.apple.com/library/archive/documentation/GraphicsAnimation/Conceptual/HighResolutionOSX/Testing/Testing.html)工具可看清你正在何处做着不必要的绘制。它功能强大，比如，Autoflush drawing可使绘制操作一旦发生就显示在屏幕上。它可使屏幕上被绘制的区域闪烁起来，以便你能看到正绘制的地儿。相同屏幕上的更新可用不同颜色来突出分别，以便你能看到多余工作发生的区域。

<img src="/images/posts/2019-03-18/quartxDebug.png">
**NSView**有些特征；它们可以让你决定视图的哪些部分需要重绘、哪些部分则不必。你可点击测试那被传入**NSView**的**drawRect:**方法的矩形区域，并仅执行那些适用于这矩形区域的绘制调用。这个矩形倾向于成为所有需要重绘制区域的联合体，因此你可查阅**getRectsBeingDrawn:**以及**needsToDrawRect:**来测试那些需要重绘的区域。
### 10.3.5  在使用任何profiling工具之前
忘记任何关于性能问题出在何处的假设。程序员不应预测性能问题是什么；如果知道问题出在哪，问题就应该早被解决了，也就不会有所谓的性能问题了。与我一起工作过的某程序员确信：当加载文件时，文件加载与硬盘I/O操作是他程序中较慢的那部分；并且他付出了很大努力来优化硬盘访问。After a quick session with **Shark**，将数据整理成**NSOutlineView**可以使用的树形结构才是导致问题的实际原因。实际花费在文件I/O上的时间是微乎其微的。

Keep good notes on what you do and the measurements you make so that you can apply the optimizations to other situations.By keeping a record of execution times(for instance),you can tell if your optimization attempts are helping or are actually making the problem worse.

当追踪性能问题时，朝你的应用扔一个大数据集。像刚提到的文件加载问题，某些数据是花费1到2秒可加载的5k文件。这开的探视窗口太小以至于不能发现问题所在。如果你的应用被设计成编辑50页研究文稿，那就扔500或5000页文档给它。比较大的数据集应当会使O(N^2)算法表现得令人难以忍受地慢！如果编辑5000页文档时，你的应用都有很好的响应能力；那么，当用户使用它编辑50页文档时，自然能给用户一个良好的体验。不要用两或三个数量级以上的数据集来掺合这事；由于过多的数据可能需要数据结构的一次重新设计，如此对于小一些的数据集并非最优(成次最优效果了)。

有些许关于应该何时优化的争论。一种学院派观点是：“过早优化是全部不幸的根源(premature optimization is the root of all evil)”，应当等到开发周期结束才去确定及解决性能问题。不幸的是，如果真有一个根本上的瓶颈问题，就需要重建产品的大部分功能。另一种学院派是：你要表现得节食一般，并一直紧绷性能问题这根弦。如此做的消极方面是：过早优化可能将设计和代码变得晦涩难懂，将追踪程序错误变得更为困难。

正如生活中的每件事一样，中间地带(中庸之道)是个好去处。一眼盯着可被提高的算法，但是也不要过早地在开发过程中混淆代码。经常性地扔给程序一些大数据集。一眼盯着你的应用程序内存使用情况以防它增长地太快太大(多吃多占)。也要确保在用户环境运行程序。如果你正在编写一个桌面app，确保Safari和iTunes处于运行中，因为用户可能正在使用这些app。如果你的应用吃内存很厉害(memory pig)使得iTunes都退出运行了，你肯定会得到一些用户的抱怨。
## 10.4  命令行工具
Mac OS X带来了许多用来追踪特定类型性能问题的命令行工具。命令行工具的美妙之处在于你可以远程登录机器并观察它们。它们不会与你的应用用户界面打交道。
### 10.4.1   命令行工具之time
最简单的工具是**time**。它为命令执行计时；展示给你时钟时间，花费在用户空间的CPU时间以及花在内核的CPU时间。这有一个在**TextEdit**上运行/usr/bin/time的例子。测量的时间是从启动TextEdit到加载/usr/share/dict/words、再到从文本顶部滑至底部的总和。
``` Objective-C
time /Applications/TextEdit.app/Contents/MacOS/TextEdit
```
<img src="/images/posts/2019-03-18/timeForTextEdit.png">
共计10秒，0.5秒在用户空间，以及不到0.2秒用在了内核中。也可以测测其它应用，比如vim，
``` Objective-C
time /usr/bin/vim
```
<img src="/images/posts/2019-03-18/timeForVim.png">
当比较优化结果时，**time**是一件趁手的工具。Run a baseline or two with **time**，做优化，随着再次尝试**time**。如果你正在优化CPU使用量，发现CPU指数在增长，你当重新考虑有针对性的优化。
### 10.4.2  命令行工具之dtruss
### 10.4.3  命令行工具之top
Unix系统是个复杂的beast，它由相互影响的程序构成。性能问题有时表明整个系统比较缓慢；但是孤立地看，每个程序运行状况良好。在观测某个特定程序的系统调用(system call)时，**dtruss**和**sc_usage**是两件非常有用的工具。另一方面，**top**可以用来监测系统之上的所有程序。不带参数运行**top**会显示熟悉的**OS**信息(内存分布、加载平均时间等)。默认情况下，它根据启动顺序对程序排序。如果你正监测一个最近启动的程序，这会是非常有用的特点。-u标志会根据**CPU**使用量排序结果。

**top**也能计算和显示系统级事件。top -e 显示**VM**(virtual memory 虚拟内存)，网络活动，硬盘状态以及消息统计等。
``` Objective-C
top -e
```
<img src="/images/posts/2019-03-18/top.png">
结果中有很多信息：313个进程，1142个线程；SharedLibs占据了297M内存，物理内存用去了7034M；网络及硬件I/O情况。每个进程又有线程数、工作队列、mach端口及内存域等信息。调整终端窗口大小可以看到更多列，比如虚拟内存大小、进程状态、页错误以及BSD系统调用等等。
### 10.4.4  命令行工具之sample
一件非常有用的低技术(low-tech)工具是随机分析(stochastic profiling)；在此过程中，你可将程序运行在调试器中，并偶尔打断它以查看调用栈的内容。如果你在调用栈中一遍遍地看到某函数，你就明白该从哪着手了。在传统性能分析工具无效的平台或情形下，这项技术就变得非常趁手。另外，此技术易用且迅速，尤其是已经在调试器运行程序的情况下。

你可以从命令行做些分析来回答一些“正在发生什么”之类的问题。**sample**程序会以10ms的间隔来采样某进程，随即构建一个程序正在做什么的快照(snapshot)。
``` Objective-C
sample Safari
```
<img src="/images/posts/2019-03-18/introOfSample.png">
<img src="/images/posts/2019-03-18/resultOfSample.png">
从结果中可以看到很多信息：“进程493(Safari)的采样分析结果写到了文件/tmp/Safari_2019-04-03_165334_UBYE.sample.txt内”、“启动路径是什么”、“加载地址”、“分析工具的位置”、“Call graph中方法的启动和调用顺序”等等。
## 10.5  利用mach_absolute_time()精确计时
Command-line tools are a great place to benchmark snippets of code,which is useful for those cases where you can isolate an algorithm or a programming technique out of your full application.一个几十或几百行的命令行工具总比拥有百万行代码的应用好驾驭。并非每个问题都可被放进这么一个小小的检查程序中，但是这并不妨碍它成为一项有用的技术。

命令行程序的妙处在于你可使用**time**命令得到程序运行时间的绝对值，如此一来，对比你对目标程序做出的改变时，将会很容易。

但是，有时**time**命令没有足够的精准度。你可能想要更精确的计时，或者你对测量程序特定部分的耗时感兴趣。你可能不关心加载数据的用时如何；但是如果加载数据耗时是算法运行时间的3倍，你便会想动手测测程序内部的计时了。

Mach(Mac OS X的内核)提供了可用于精确计时的函数。**mach_absolute_time()**读取CPU时间基准寄存器，然后报告结果给用户。此时间基准寄存器也充任系统中其它时间测量的基础。
``` Objective-C
uint64_t mach_absolute_time(void);
```
**mach_absolute_time()**返回基于CPU时间的值，因此它无法直接使用。因为你不清楚计数器每次增长所代表的时间范围是多少。为将**mach_absolute_time()**的结果转换成纳秒，利用**mach_timebase_info()**来获取**mach_absolute_time()**值的缩放比例。
``` Objecitve-C
kern_return_t mach_timebase_info(mach_timebase_info_t info);
```
其中，mach_timebase_info_t是指向mach_timebase_info结构体的指针，
``` Objective-C
struct mach_timebase_info {
	uint32_t numer;
	uint32_t denom;
};
```
**mach_timebase_info()**用分数来填充此结构体，得到的分数去乘以**mach_absolute_time()**的结果以计算实际纳秒值。用(numer/denom)乘以**mach_absolute_time()**的结果。machtime.m文件展示了如何使用这两个函数。代码计算调用**mach_timebase_info()**和**printf()**耗费的时间。当然，对工业应用中的代码，你会放一些需要计时的更有趣的逻辑。

<img src="/images/posts/2019-03-18/codeOfMachtime.png">
``` Objective-C
./machtime
```
<img src="/images/posts/2019-03-18/resultOfMachtime_0.png">
<img src="/images/posts/2019-03-18/resultOfMachtime_1.png">
<img src="/images/posts/2019-03-18/resultOfMachtime_2.png">
<img src="/images/posts/2019-03-18/resultOfMachtime_3.png">
在这个2014 MacBook Pro系统上，这个变换的分子与分母均为1。像TiBook这样的一些老机器，其变换的分子是1000,000,000且分母为24,965,716，导致一个40.05的放大值。所以**mach_absolute_time()**的每个增量代表40纳秒。

我们执行4次程序，其结果并非完全相同。为什么呢？当执行的这么短时间内，任何事都可以叨扰它。可能某些动态库查找被调用了，可能iTunes正在运行且在加载一个新曲目，也可能TimeMachine横插进来了。对一个真实的标准检查程序而言，你将运行一长时间段来隐藏这些昙花一现的干扰。当然了，也应该多运行几次以获得一个好的平均值(信号处理中，屏除噪音的常用手段)！
## 10.7  总结
Performance is a never-ending game.The rules change constantly as new OS revisions and new hardware comes out.We are nearing the end of free performance gains from hardware,at least for applications that can only take advantage of one CPU.Efficiency in coding and in alogorithms,as well as parallel processing,will become more and more important as time goes on.Luckily,we have a number of good tools to profile our code and highlight the areas where we should focus our attention.
