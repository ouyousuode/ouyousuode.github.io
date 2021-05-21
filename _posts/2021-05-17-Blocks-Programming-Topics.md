---
layout: post
title: 「Apple文档」之Blocks Programming Topics
---
{{page.title}}
===================================

block对象是C语言层级的语法，是运行时特性。它们类似于标准的C函数，但除了可执行代码之外，它们可能还包含与自动(栈)或托管(堆)内存相绑定的变量。因此，block可以维护一组状态(数据)，用于在执行时影响代码之行为。<br/>

你可以使用block来组成函数表达式；可将这些表达式作为参数传递给API，或者存储下来用于多线程！作为「回调」(callback)，block特别有用，因为它不仅携带着「回调」时要执行的代码，还包含了执行过程中所需的数据！<br/>

block在GCC和Clang中都是随OS X v10.6 Xcode开发者工具一起提供的。你可以在OS X v10.6及iOS 4.0之后的版本中使用block对象。block運行時是开源的，见于[LLVM’s compiler-rt subproject repository](https://llvm.org/svn/llvm-project/compiler-rt/trunk/)。block也已經作為[N1370: Apple’s Extensions to C](http://www.open-std.org/jtc1/sc22/wg14/www/docs/n1370.pdf)提交給了C語言標準工作組。由于Objective-C与C++均派生自C語言，所以block也被设计成可以在此三种语言(以及Objective-C++)中使用。其语法也反映了这个目标。<br/>

你应当阅读此文档以了解什么是block对象、以及如何从C、C++或Objective-C角度来使用它们。<br/>

本文档包含以下章节：<br/>
- Getting Started with Blocks(块入门)提供了一个快速、实用的块简介。
- Conceptual Overview(整体概念)提供了块的概念性介绍。
- Declaring and Creating Blocks(声明及创建块)展示了如何声明块变量以及如何实现块。
- Blocks and Variables(块和变量)描述了块和变量之间的交互，并定义`__block`存储类型修饰符。
- Using Blocks(使用块)演示了各种使用模式。


## 一、Getting Started with Blocks
下面章节利用实际用例帮你入门block(块)。<br/>

### 1.1 声明和使用块
你可以使用^运算符来声明一个block变量，并指示一个block字面量的开始。block本身的主体包含在`{}`中，正如本例所示(形如C语言，`;`标示着语句的结束)：<br/>
<img src="/images/posts/2021-05-17/1_1.png">

如图解释了该示例:<br/>
<img src="/images/posts/2021-05-17/blocks.jpg">

注意，block可以使用自定义它的相同范围内的变量。如果将block声明为变量，则可以像使用函数一样使用它：<br/>
<img src="/images/posts/2021-05-17/1_2.png">
