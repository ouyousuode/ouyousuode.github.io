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

本文档包含以下章节：
- Getting Started with Blocks(块入门)提供了一个快速、实用的块简介。
- Conceptual Overview(整体概念)提供了块的概念性介绍。
- Declaring and Creating Blocks(声明及创建块)展示了如何声明块变量以及如何实现块。
- Blocks and Variables(块和变量)描述了块和变量之间的交互，并定义`__block`存储类型修饰符。
- Using Blocks(使用块)演示了各种使用模式。

<br/>
## 一、Getting Started with Blocks
下面章节利用实际用例帮你入门block(块)。<br/>

### 1.1 声明和使用块
你可以使用^运算符来声明一个block变量，并指示一个block字面量的开始。block本身的主体包含在`{}`中，正如本例所示(形如C语言，`;`标示着语句的结束)：<br/>
<img src="/images/posts/2021-05-17/1_1.png">

如图解释了该示例:<br/>
<img src="/images/posts/2021-05-17/blocks.jpg">

注意，block可以使用自定义它的相同范围内的变量。如果将block声明为变量，则可以像使用函数一样使用它：<br/>
<img src="/images/posts/2021-05-17/1_2.png">

### 1.2 直接使用块
在很多情况下，你无需声明block变量；相反，在需要将其作为参数的地方内联编写一个block字面量，即可。以下示例使用了`qsort_b`函数。`qsort_b`类似于标准的`qsort_r`函数，但以一个block作为其最后一个参数。<br/>
<img src="/images/posts/2021-05-17/1_3.png">

### 1.3 Cocoa中的块
Cocoa frameworks中的一些方法以block为参数，通常对对象集合执行操作，或者在操作完成后用于「回调」。下面示例展示了如何将block与NSArray方法[sortedArrayUsingComparator:](https://developer.apple.com/documentation/foundation/nsarray/1411195-sortedarrayusingcomparator?language=objc)一起使用。该方法只有一个block参数。举例来讲，此场景下，block被定义一个`NSComparator`类型的局部变量：<br/>
<img src="/images/posts/2021-05-17/1_4.png">

### 1.4 `__block`变量
block的一个强大特性是它们可以修改相同词法作用域内的变量。给变量加上`__block`存储类型符即意味着你可以在block内修改此变量。利用「1.3 Cocoa中的块」之示例，你可以使用block变量来计算如下示例中🈶️多少字符串是相等的。举例来讲，此场景下，直接使用block并在block内使用了`currentLocale`作为只读变量：<br/>
<img src="/images/posts/2021-05-17/1_5_0.png">
<img src="/images/posts/2021-05-17/1_5_1.png">

## 二、Conceptual Overview
block对象为你提供了一种在C语言及其派生语言(如Objective-C和C++)中创建特殊函数体作为表达式的方法。在其它编程语言和环境中，block对象有时也被称为「闭包」(closure)。在这里，除非和标准C语言术语的一段(block)代码混淆之情况下，一般通俗地称其为「块」(block)。<br/>

### 2.1 块功能
block就是一段匿名的内联代码段：
- 像函数一样有一个类型化的参数列表。
- 有可推断或直接声明的返回值类型。
- 可以从定义状态的词法范围中捕获状态。
- 可以选择性地修改词法作用域的状态。
- 可以和相同词法范围内定义的其它块共享修改状态。
- 在词法作用域(「栈帧」stack frame)被破坏后，可以继续共享并修改词法作用域内定义之状态。

你可以复制一个block，甚至可以将其传递给其它线程以供延迟执行(也或者，在其自己线程内，传递给一个runloop)。编译器和運行時会安排引自该block的所有变量在该block的所有副本之生命周期内得以保留(换言之，保留至声明周期后)。虽然block对纯C和C++语言是可用的，但是block也始终是一个Objective-C对象。<br/>

### 2.2 用法
block一般是小段的、自成体系的代码段。因此，它特别适用于作为封装可能并发执行之工作单元的一种方式，或者作为集合中的项，也或者作为另一个操作完成时的「回调」(callback)。<br/>

block作为传统回调函数的实用替代，主要有两大原因：
- 它们允许你把具体实现之代码写在调用处。因此，block通常是框架(framework)方法的参数。
- 它们允许访问局部变量。回调函数需要包含执行操作所需的所有上下文信息之数据结构，而你可以不需使用回调函数，而是直接访问局部变量。

<br/>
## 三、Declaring and Creating Blocks
### 3.1 声明块引用
block变量包含对块的引用。你可以使用类似于声明函数指针的语法来声明它们，区别在于使用^而非`*`。block类型同C语言类型系统的其余部分完全相通。以下是所有有效的block变量声明：<br/>
<img src="/images/posts/2021-05-17/3_1.png">

block也支持可变参数(...)。不带参数的block必须在参数列表中制定`void`。通过给编译器一整套元数据以验证block的使用、传递给block之参数以及返回值的赋值這三者之有效性，block得以被设计为完全类型安全的。你可以将block引用强制转换为任意类型的指针，反之亦然。但是呢，你不能像对指针以`*`來取消引用操作那樣對block進行解引用(dereference)操作，因此不能在編譯時計算block的大小。你可以为block创建类型————当你在多个位置使用具有给定签名的block时，此做法通常被认为是最佳实践：<br/>
<img src="/images/posts/2021-05-17/3_2.png">

### 3.2 创建块
你可以使用^运算符来指示block字面表达式的开始。它后面可能跟着一个包含在`( )`中的参数列表。block的主体包含在`{ }`中。下面的示例定义了一个简单的block，并将其赋值给之前定义的变量(oneFrom)————同以C语言中结束语句的常规`;`来结束。<br/>
<img src="/images/posts/2021-05-17/3_3.png">

若你没有明确声明block表达式的返回值，它可从block的内容中自动推断出来。如果返回值类型是推断的，并且参数列表为`void`，则你也可以省略参数列表。当有多个返回语句时，它们必须是类型一致的(必要时，进行强制转换)。<br/>

### 3.3 全局块
在「文件」层面上，你可以将block用作全局字面量：<br/>
<img src="/images/posts/2021-05-17/3_4.png">


## 文档修订历史
<img src="/images/posts/2021-05-17/Document_Revision_History.png">
