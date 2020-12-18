---
layout: post
title: Concurrent Programming in Mac OS X and iOS
---
{{page.title}}
=======================

<img src="/images/posts/2019-01-24/Title.png">
<img src="/images/posts/2019-01-24/Table_of_Contents.png">
随着iPad 2和四核MacBook Pro等多核设备的推出，编写利用设备之多核优势的多线程应用已然成为开发人员最头疼的问题之一。以iPad 2的推出为例。在发布日当天，只有少数应用，还基本上是苹果发布的应用，能够利用其多核之优势。与原始iPad相较，Safari等应用在iPad 2上的性能非常好，然一些第三方浏览器的性能实不如Safari。这背后的原因是Apple在Safari的代码库中利用了Grand Central Dispatch(**GCD**)。GCD是一类低层的C API，允许开发人员在根本不需要管理线程的情况下编写**多线程**程序。开发者要做的就是定义任务，剩下的交给GCD便可。<br/>

行业的趋势是mobility。「无论是像iPhone一样紧凑,还是像MacBook Pro一样强大和成熟」的移动设备，都比像Mac Pro这样的计算机拥有更少的资源，因为所有的硬件都必须放在小型设备的紧凑机身内。正因为如此，编写在iPhone等移动设备上运行流畅的应用程序非常重要。我们离拥有四核或八核智能手机不远了。一旦我们在CPU中有8个内核，一个只在其中一个内核上执行的应用程序将比一个用GCD等技术优化过的应用程序运行得**慢**得多；GCD允许代码在多个内核上得以调度，而无需程序员来管理这种同步。<br/>

Apple正在推动开发人员远离使用thread，并慢慢开始将GCD集成到其各种框架中。例如，在iOS引入GCD之前，操作和操作队列使用线程。随着GCD的引入，Apple彻底改变了操作和操作队列的实现，用GCD代替了线程。<br/>

本书是为那些遵从Apple建议并想做软件开发光明未来之人所写：通过GCD代替线程编程，从线程之繁琐抽身出来，并允许操作系统为你处理线程事务。<br/>

<img src="/images/posts/2019-01-24/Chapter_1.png">

## 1.1 Short Introduction to Block Objects 块对象简介

## 1.2 Constructing Block Objects and Their Syntax 构造块对象及其语法

## 1.3 Variables and Their Scope in Block Objects 块对象中的变量及其范围

## 1.4 Invoking Block Objects 调用块对象

## 1.5 Memory Management for Block Objects 块对象的内存管理


<img src="/images/posts/2019-01-24/Chapter_2.png">

## 2.1 Short Introduction to Grand Central Dispatch

## 2.2 Different Types of Dispatch Queues

## 2.3 Dispatching Tasks to Grand Central Dispatch

## 2.4 Performing UI-Related Tasks

## 2.5 Performing Non-UI-Related Tasks Synchronously

## 2.6 Performing Non-UI-Related Tasks Asynchronously

## 2.7 Performing Tasks After a Delay

## 2.8 Performing a Task at Most Once最多执行一次任务

## 2.9 Running a Group of Tasks Together

## 2.10 Constructing Your Own Dispatch Queues

<img src="/images/posts/2019-01-24/Concurrent_Programming_Mac_OS_X_iOS_Amazon.png">
从图中的🌟可看出「评价不太好」,因为它只是一本小册子。给差评的顾客认为「No publisher should be able to get away with printing a 50 page pamphlet. This is not a book, its a brief pamphlet on **Grand Central Dispatch. I can find more information by spending 30 seconds on Google.** There is much more content available to write a more thorough and useful book, and there is no excuse to stop at 50 pages.」；但是，只以页数作为评判标准，可能也有失偏颇，O'Reilly Media也出版过许多其它的小册子，比如《The Software Paradox : The Rise and Fall of the Commericial Software Market》(Stephen O'Grady)，也不到60页。一本书能展现作者欲表达之意，能给读者「有所得」之感，就可以了。针对本书，从内容组织及阐释要点来看，快速读完，还是有所得！Amazon也有顾客针对其内容做了比较中肯的评价，<br/>

<img src="/images/posts/2019-01-24/Amazon_Customer_reviews.png">
