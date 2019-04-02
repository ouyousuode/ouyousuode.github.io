---
layout: post
title: 性能优化
---
{{page.title}}
=======================
性能问题通常来自以下5个领域的一或多个：算法、内存、CPU、硬盘以及图形(Graphics)。的确，这几个方面是你的程序通常与之打交道的。尽管某个问题可能出现在多个方面，你可利用性能工具孤立地查看某一方面带来的影响。
## 内存
虽然现代计算机有很足量的内存(我更愿称之为主存储器，因它未在CPU内部)，但是RAM仍然是一种很稀缺的资源。一旦你的应用或系统的其它应用填(占)满了内存，Mac OS X将开始发送内存页到硬盘，其结果必然是损毁性能。在iOS设备上，低内存情形下，你的程序可能会被杀掉。

典型地，如果你执行优化以减少内存使用量，你将得到在执行时内存占用量降低的结果，原因在于处理器不会等待自内存到达的额外数据(Typically if you optimize to reduce your memory usage,you will often get reductions in execution time because the processor is not waiting for that extra data to arrive from memory)。另外，因为Mac OS X是个共享式系统，可能有多个用户同时登录，也可能多个用户的很多应用在同时运行，因此有必要保守对待内存使用。当每个进程有它自己用于玩耍的wide-open地址空间，这会是一个严格规定(tough discipline)。
### 内存之Locality of Reference
"Locality of Reference"描述了相互紧挨着的内存访问。从一个4k页中读取一百比特比从散布于地址空间的100个不同页各读取一比特快得多！当你从内存请求数据时，处理器实际上抓取称为cache line的一个比特串序列；它假定你将以共同的方式来访问内存。所以你设置循环来循环地操作内存，你可以看到一个性能提升。locality.m程序创建了一个巨大的二维全局数据，并以两种不同的方式访问它。

<img src="/images/posts/2019-03-18/codeOfLocality.png">
``` Objective-C
./locality 及本机配置
```
<img src="/images/posts/2019-03-18/resultOfLocality.png">
<img src="/images/posts/2019-03-18/computerForLocality.png">
for循环的一个简单颠倒便可导致10倍的性能惩罚！第一个循环遵循了数组内存管理的方式。此循环访问临近的字节，因此随着它走遍数组，它有良好的locality of reference表现。内存页仅被访问一次；在循环体停止操纵一个页上的内存后，此页将不再使用。第二个循环体工作起来像穿越横纹(Bad memory access pattern所示)。每次通过循环，使数组用到的每页都被访问一次。如此每页均处于warm状态(会一直处于内核的数据结构内)，导致内核持续刷新它的least-recently-used页列表，这种情况给虚拟内存系统造成很大压力。第一个循环对内核更友好的原因在于，一旦工作完成便不再接触当前页。一旦页被移除内核的数据结构，它便不再可见。

<img src="/images/posts/2019-03-18/memoryAccessPattern.jpg">
### 内存之缓存
提高性能的一个常用手段是缓存化；此之后，可保持对已加载或计算过的数据访问。如果你不仔细些，这项技术也会有一些缺点，即系统占据过多的虚拟内存及分页化现象。回想：最近未被访问过的内存可被paged out到硬盘，那么RAM中的空间就可被其它进程使用了。iOS设备不会page数据到硬盘，因此dirty页是常驻内存的。

如果你选择缓存信息，最好将缓存数据和描述缓存数据的元数据(metadata)分开。你不会想使用像Bad locality of reference那样的结构，它将缓存数据和元数据混合到了一块。相反，像Good locality of reference那样组织你的数据。保持元数据待在一起，因为当你穿越(walk through)缓存区域找寻期望的对象时，会有良好的locality of reference。你甚至可以做自己的虚拟内存形式：如果一个cache entry已经有段时间未被用到了，或者你得到了一个iOS内存告警，你可将此团数据移除内存；当然，当需要时，再次将其加载进内存。

<img src="/images/posts/2019-03-18/localityOfReference.jpg">
### 内存之Memory is the New I/O
促使程序员缓存读自硬盘数据的动因时访问硬盘I/O极耗时间资源。等待一次硬盘I/O读取可耗费数十万(甚至更多)个CPU循环，而这段时间本可另作其它更好的用途。

伴随着今天的处理器，内存子系统、总线结构以及RAM已然变得像I/O了。有时，与CPU速度像较，访问内存可能是极其缓慢的。比如，根据Apple技术笔记，G5利用从内存加载一个cache line的时间可做16到50次向量相加。而且，在现代处理器上，这种情况正变得越来越糟糕。

与强力计算相比较，“precalculate and store in memory”技术可能已到瓶颈。CPU可促使一些计算比从内存获取快得多，查找表可强制更多重要数据移出CPU缓存。技术笔记继续提到“In one example,vector code that converts unsigned char data to float and then applies a 9th order polynomial to it is still marginally faster than hand turned scalar code that does a lookup into a 256 entry lookup table containing floats.”

<img src="/images/posts/2019-03-18/busStructure.png">
<img src="/images/posts/2019-03-18/memoryHierarchy.png">
距CPU逻辑单元最近到缓存(Level-1 Cache)有一个存放指令的区域，但是每个核仅有32到64KB大小。像会增加代码长度的优化操作可能会被移出此缓存。它成为了一个在代码体积与存取数据间的平衡艺术。有时，trial-and-error是观察何种技术可致最佳性能的一种方式，尤其是如果你有高性能科学建模或需要快速处理大数据(如处理视频)任务时。

C语言的Semantics(语义学、词义学)可达到智能优化的目的，尤其是与内存有关时。如果编译器清楚某块内存是被如何访问的，它便可以将数值缓存在寄存器中，甚至可以避免加载已被加载过的数据。因为C语言有指针，there can be aliasing problem。在进程的某处有个指针，它正指向编译器本可优化访问的一块内存。这也是为什么有些没有指针的语言(像FORTRAN)可以执行更彻底优化的原因。

当在一个循环中由指针或全局变量使用数据结构时，编译器会在每次执行循环体时加载它在内存中的位置。It does this just in case the value was changed by someone else,either in another thread or by a function called inside the loop。创建一个局部变量来持有对应全局变量的值，以便让编译器了解此数据不会改变，最终避免每次循环皆访问内存。
## CPU
当面临优化事务(issue)时，大部分程序员第一个想到的度量标准便是CPU使用量。“我的应用正在拽住CPU,我需要给它加速。” 通常，当CPU使用量成为一个主导性因素时，其根本原因是一个缓慢低效的算法。它可能有一个高复杂性，也或者它只是一个糟糕的实现方式。在几乎所有的情形中，与其它种类代码或系统调整相比，改写你的算法会给你更大程度的加速。一个经典例子是将冒泡排序(N^2复杂度)改为快速排序(nlgn复杂度)。

有时，一个算法的坏实现可导致比较大的破坏。比方说，某个SunOS 4.1.x显示**strstr()**编程错误，竟将O(N)操作变成了O(N^2)操作：
``` Objective-C
	while(c < strlen(string)) {
		// do stuff with string[c]
	}
```
回想，C字符串并不存储它们自身的长度。一个字符串只是一个以0结尾的比特序列。**strlen()**不得不遍访整个字符串方才得以计算全部字符数。虽有很多小手段可用于此过程，但它仍是一个O(N)操作。在这个特定的例子中，字符串的长度不会发生改变，因此没有必要每次循环都去计算其长度。

所幸，通过注意固定在Activity Monitor中的CPU测量仪，或**top**正显示你的应用在耗费99%的可用CPU能量，高CPU使用量会很容易被发现。采样(sampling)和侧写(profiling)工具是追踪此类问题之原因的理想工具。
## 硬盘
硬盘访问是很缓慢的———— 比内存访问慢了几个数量级。一般而言，如果能避免硬盘I/O，则避免。如果你打算从硬盘缓存数据，要记得：虚拟内存系统也使用硬盘。如果你缓存了大量的数据，最后会导致VM系统去做硬盘I/O操作。这是一个很糟糕的情况，因为you have now exchanged one disk read (from disk into memory) into a read and a write to page it out and then another read to page it back in from the disk into memory。

在涉及VM分页化，当优化硬盘访问时，locality of reference扮演着重要角色。伴随着差的locality of reference，你做会接触(touch)很多页。这些页触发其它页page out出VM缓存，以便发送到硬盘上。最终你会再次touch它们以获取这些页上的数据，如此过程便又导致硬盘I/O。

通过不把所有工作都做完，你可以避免一些硬盘I/O耗费。一个常用的技巧是：将窗口们(windows)放入不同的.nib文件内，再按需加载。如果你不需要显示某个窗口，那就没理由把它加载进内存。

类似地，如果你有一个大信息数据库，渐进访问比把全部内容加载进内存获得更高效的加速。使用内存映射(memory-mapped)的文件可避免硬盘活动，因为只有文件被touch的部分才会被载入内存。
## Graphics

