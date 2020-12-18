---
layout: post
title: Mac OS X及iOS中的并发编程
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
块对象是通常以Objective-C中方法的形式出现的代码包。块对象与Grand Central Dispatch(GCD)一起创建了一个和谐的环境；它可以让你可以在iOS和Mac OS X中交付高性能的多线程应用。你可能会问，块对象和GCD有什么特别之处？很简单：不再有线程！你所要做的就是将你的代码放入块对象中，并要求GCD为您处理该代码的执行。<br/>

在本章中，您将学习块对象的基础知识，然后学习一些更高级的主题。在进入Grand Central Dispatch章节之前，你将了解你需要了解的关于块对象的一切。从我的经验来看，学习块对象的最好方法是通过实例，所以你会在本章中看到很多例子。请确保你在Xcode中亲自尝试了这些示例，以真正了解块对象的语法。<br/>

## 1.1 Short Introduction to Block Objects 块对象简介
Objective-C中的Block对象就是编程界所说的一级对象(first-class objects)。这意味着你可以动态地构建代码，将块对象作为参数传递给方法，并能从方法返回块对象。所有这些特性都使得在运行时选择想要做的事情和改变程序的活动变得更加容易。特别是，块对象可由GCD在单独的线程中运行。作为Objective-C对象，你可像处理任何其他对象一样处理块对象：你可以保留(retain)它们，释放它们，等等。块对象也可以称为“闭包”(closure)。<br/>

正如我们将在「Constructing Block Objects and Their Syntax」看到的那样，构造块对象类似于构造传统的C函数。块对象可以有返回值，也可以接受参数。块对象可以内联(inline)定义，也可以作为单独的代码块来处理，类似于C函数。当内联创建时，块对象可访问的变量之范围与块对象作为单独的代码块实现时有很大不同。<br/>

GCD使用块对象。当使用GCD执行任务时，你可以传递一个块对象，该对象的代码可以同步或异步执行，这取决于你在GCD中使用的方法。因此，你可以创建一个块对象，负责下载作为参数传递给它的网址(URL)。该单个块对象可以同步或异步地用于应用程序的不同位置，当然了，具体取决于您希望如何运行它。你不必使块对象本身「同步」或「异步」；你只需用「同步或异步GCD方法」调用它，块对象便可以工作了。<br/>

对于编写iOS和OS X应用程序的程序员来说，块对象是相当新的新鲜事物。事实上，块对象尚不如线程流行，可能是因为它们的语法与纯Objective-C方法有点不同，而且也更复杂些。尽管如此，块对象非常强大,Apple正在大力推动将它们整合到Apple库中。你已经可以在类中看到这些添加，比如**NSMutableArray**，程序员可以使用块对象对数组进行排序。<br/>

本章完全致力于在iOS和Mac OS X应用程序中构建和使用块对象。我想强调的是,习惯块语法的唯一方法是自己动手写几个。看看本章中的示例代码，并尝试实现你自己的块对象。<br/>

## 1.2 Constructing Block Objects and Their Syntax 构造块对象及其语法
块对象可以是内联的，也可以作为独立的代码块进行编码。先说后一种。假设您在Objective-C中有一个方法，它接受两个NSInteger类型的值，通过从另一个值中减去一个值来返回两个值的差，以NSInteger类型返回：<br/>
<img src="/images/posts/2019-01-24/subtract_Objective-C.png">

很简单，不是吗？现在，让我们把这个Objective-C代码翻译成一个纯C函数，此函数提供同样的功能，以让我们离学习块对象的语法更近一步:<br/>
<img src="/images/posts/2019-01-24/subtract_C.png">

你可以看到C函数和它的Objective-C对应物在语法上有很大的不同。现在让我们来看看如何以块对象编写相同功能的函数：<br/>
<img src="/images/posts/2019-01-24/subtract_Block.png">

在我详细介绍块对象的语法之前，让我再给你看几个例子。假设我们在C语言中有一个函数，它接受一个类型为NSUInteger(无符号整数)的参数，并将其作为类型为NSString的字符串返回。下面是我们如何在C语言中实现这一目的：<br>
<img src="/images/posts/2019-01-24/intToString_C.png">

若了解在Objective-C中使用独立于系统的格式说明符格式化字符串，请参考Apple网站之[String Programming Guide, iOS Developer Library](https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/Strings/Articles/formatSpecifiers.html)。Example 1-1中显示了与该C函数等效的块对象。<br/>
<img src="/images/posts/2019-01-24/Example_1-1.png">

独立块对象的最简单形式是返回**void**并且不接受任何参数的块对象:<br/>
<img src="/images/posts/2019-01-24/simpleBlock.png">

块对象的调用方式与C函数完全相同。如果它们有参数，可以像C函数一样将参数传递给它们，并且可以像取回C函数的返回值一样取得任何返回值。这里有一个例子:<br/>
<img src="/images/posts/2019-01-24/intToString_Block.png">

Objective-C方法**callIntToString**通过将值10作为唯一参数传递给该块对象并将该块对象的返回值放在**string**局部变量中来调用**intToString**块对象。既然我们知道了如何将块对象作为独立的代码块来编写，那么让我们来看看如何将块对象作为参数传递给Objective-C方法。为了理解下面示例之目标，我们必须抽象地思考一下。<br/>

假设我们有一个Objective-C方法，它接受一个整数并对它执行某种转换，这可能会根据我们的程序中发生的其他事情而改变。我们知道我们将输入一个整数，输出一枚字符串；但是我们将把精确的转换过程留给一个块对象来处理；而每当我们的方法运行时，该块对象又可能会不同。因此，该方法将接受待变换的整数和将对其进行变换的块作为参数。<br/>

对于我们的块对象，我们将使用与之前在Example 1-1中实现的相同的`intToString`块对象。现在我们需要一个Objective-C方法，它将接受一个无符号整数参数和一个块对象作为它的参数。无符号整数参数很简单，但是如何告诉我们的方法，它必须接受与`intToString`块对象相同类型的块对象呢？首先，我们定义`intToString`块对象的签名，它告诉编译器我们的块对象应该接受哪些参数:<br/>
<img src="/images/posts/2019-01-24/IntToStringConverter_Block.png">
这个`typedef`只是告诉编译器,接受一个整数参数并返回一个字符串的块对象可以简单地用一个名为`IntToStringConverter`的标识符来表示。现在，让我们继续编写我们的Obejctive-C方法，该方法同时接受整数和类型为`IntToStringConverter`的块对象为参数：<br/>
<img src="/images/posts/2019-01-24/convertIntToString_usingBlockObject.png">
我们现在要做的就是用我们选择的块对象调用`convertIntToString:`方法(Example 1-2)。<br/>
<img src="/images/posts/2019-01-24/Example_1-2.png">

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
