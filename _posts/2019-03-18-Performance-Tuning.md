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
