---
layout: post
title: 理解Objective-C Runtime
---
{{page.title}}
=================================

<img src="/images/posts/2019-06-01/Understanding_the_Objective-C_Runtime.png">

本文译自**Colin Wheeler**的[Understanding the Objective-C Runtime](https://cocoasamurai.blogspot.com/2010/01/understanding-objective-c-runtime.html)篇(需能访问Google)。

当人们谈到Cocoa/Objective-C时，Objective-C Runtime是Objective-C最常被忽视的功能之一。究其原因，尽管Objective-C很容易在几个小时内学会，但是学习Cocoa的新手们往往是在Cocoa框架及如何使用它们方面埋头探究。然而，除了知道像[target domethodWith:var1];这样的代码被编译器翻译为objc_msgSend(target,@selector(doMethodWith:),var1);之外，Runtime如何工作的一些细节是每一名学习Objective-C者应当了解的。了解Objective-C运行时的工作原理将有助于加深理解Objective-C语言本身以及你的应用程序是如何运行的。窃以为，无论您的经验水平如何，Mac/iPhone开发者均能从本文收获一二。
## Objective-C Runtime是开源项目
Objective-C Runtime是一项开源项目，并可随时从[http://opensource.apple.com](https://opensource.apple.com)。事实上，除了阅读苹果公司公布的关于它的文档之外，阅读其Runtime源代码是探究其工作机制的方法之一。你可以通过以下链接下载针对Mac OS X 10.6.2版本的Runtime源代码[objc4-437.1.tar.gz](https://opensource.apple.com/source/objc4/objc4-437.1/)。
## 动态语言vs静态语言
Objective-C是一门面向运行时的语言，这意味着应由哪个对象执行消息是在经编译、链接之后，直至运行时才能确定下来。这便为你提供了很大的灵活性，因为可根据需要将消息重定向到适当的对象，甚至可以交换方法实现，等等。这需要一个运行时，它可以内省对象来查看它们响应与否并恰当地分派方法。如果我们把它与**C**这样的语言对比，在**C**中，你从**main()**方法开始，然后从此处起，它几乎是一项自上而下的设计，遵循着你已写就的代码之逻辑并执行代码中的函数。**C**结构不能将执行函数的请求转发到其它目标。例如，如下所示的一段**C**语言代码：

<img src="/images/posts/2019-06-01/helloWorld_c.png">
在经过编译器解析、优化之后，变成如下的一段汇编代码：

<img src="/images/posts/2019-06-01/helloWorld_s.png">
然后，把它与库文件链接到一起，最终会生成一个可执行文件。此过程与Objective-C依赖Objective-C Runtime库编译、链接程序的过程相似。在我们最初认识Objective-C时，在一个最浅显的层面上，我们被告知发生于Objective-C括号代码类似于
``` Objective-C
[self doSomethingWithVar;var1];
```
被翻译为
``` Objective-C
objc_msgSend(self,@selector(doSomethingWithVar:),var1);
```
但是，除此之外，我们对Objective-C Runtime的工作机制一无所知。
## 何为Objective-C Runtime ？
Objective-C Runtime是一个运行时库，这是一个主要以**C**和汇编语言编写的库，使**C**具有了面向对象能力(以创建Objective-C)。这意味着它可以加载类信息，也可实现方法分发及转发等等...Objective-C Runtime创建了使Objective-C面向对象编程成为可能的所有支持结构！
## Objective-C Runtime相关术语
所以，在我们继续深入之前，让我们共同梳理一下相关术语，以便我们能在同一层面讨论问题。
### 术语之Runtime
Mac及iPhone开发者关心的有两个运行时：现代运行时(Modern Runtime)和传统运行时(Legacy Runtime)。现代运行时涵盖了所有64位Mac OS X应用及iPhone应用；而传统运行时则包括余下的所有32位的Mac OS X应用。
### 术语之Method(方法)
此包含两类方法：实例方法，像**-(void)doFoo;**一样以“-”开头，并操作于对象实例；类方法，似**+(id)alloc**这般以“+”开头，当然只能对类自身操作。方法看起来和**C**语言的函数很像，其内部是为执行一项小任务的一组代码，像下面这样：
``` Objective-C
- (NSString *)movieTitle {
	return @"Futurama: Into the Wild Green Yonder";
}
```

### 术语之Selector(选择器)
Objective-C中的选择器本质上是一个**C**语言数据结构，它充任识别你想某对象执行之方法的一种手段！在运行时内的定义类似于这样：
``` Objective-C
typedef struct objc_selector *SEL;
```
使用时，则这样：
``` Objective-C
SEL aSel = @selector(movieTitle);
```

### 术语之Message(消息)

``` Objective-C
[target getMovieTitleForObject:obj];
```
一条Objective-C消息是“[]”内所有的内容，它由接收消息的目标对象、你想执行的方法以及发送给方法的任意参数三部分组成。Objective-C消息尽管类似于**C**函数，但终究不同。事实是，你向某对象发送一条消息，并不意味着此对象会执行它。对象可以检查谁是消息的发送者，并据此以决定是执行一不同的方法，还是转发此消息给一个不同的目标对象。
### 术语之Class(类)
如果你在Runtimen内查找类，会发现如下定义：

<img src="/images/posts/2019-06-01/Class.png">
这有几件事要阐明。这段代码中有Objective-C类及对象的结构体定义。而objc_object拥有的所有内容仅是定义为isa的一个类指针，就是我们常说的“isa指针”。这个isa指针是所有Objective-C Runtime均需要的，用以检查对象并查看它是什么类，然后开始看当你是消息对象时能否响应选择器。最后，我们看到了**id**指针。默认情况下，id指针除了告诉我们它们时Objective-C对象之外，并未告知关于Objective-C对象的任何信息。当你有一个id指针时，你可以向对象询问它的类，看看它是否能响应一个方法，等等。然后当你知道你所指向的对象是何方神圣后，可更有针对性地进行操作。
### 术语之Block(块)
在**LLVM/Clang**文档中，我们可以看到关于Block的介绍：

<img src="/images/posts/2019-06-01/Block.png">
Block被设计为与Objective-C运行时兼容，因此可视其为对象。所以，它们可以响应像-retail、-release及-copy等等之类的消息。
### 术语之IMP(方法实现)

``` Objective-C
typedef id (*IMP)(id self,SEL _cmd,...);
```
IMP是编译器为我们生成的指向方法实现的函数指针。如果你刚接触Objective-C，稍后再了解它即可；不过，我们马上就可以看到，Objective-C运行时是如何调用方法的。
### 术语之Objective-C Class
那么，一个Objective-C类中都有什么呢？Objective-C中类的基本实现如下：

<img src="/images/posts/2019-06-01/Objective-C_Class.png">
但是，运行时需要记录更多内容：

<img src="/images/posts/2019-06-01/Objective-C_Class_Runtime.png">
我们可以看到，一个类有到它的父类、名字、实例变量、方法、缓存以及声称遵守的协议之引用！运行时在响应你的类或实例的消息时需要这些信息。
## 类定义对象抑或自身是对象？如何实现？
是的，我之前说过，在Objective-C中，类本身也是对象，运行时通过创建元类来处理此类问题。当你发送像[NSObject alloc]这样的一条消息时，你实际上在向类对象发送了一条消息，并且该类对象需是元类(MetaClass)的实例，而元类自身又是根元类的实例。假若你的类继承自NSObject，则此类指向NSObject作为其父类。然而，所有的元类皆指向根元类作为它们的父类。所有的元类都只是简单地拥有它们可以响应消息的方法列表之类方法。因此，当你向一个类对象发送[NSObject alloc]这样的一条消息时，接着objc_msgSend()实际查看元类，看它响应了什么，如果它找到了方法，便对类对象进行操作。
## 为什么我们要继承Apple的类库？
所以，最初当你开始**Cocoa**开发时，教程说的都是，继承**NSObject**类，然后开始编写一些内容，你可以简单地通过继承Apple类库来享受很多好处。你甚至都没有意识到的一件事是，此时便设置好了你的对象以使用Objective-C运行时。当我们给自定义的类创建一个实例对象时，是这样做的：
``` Objective-C
MyObject *object = [[MyObject alloc] init];
```
第一条得以执行的消息是+alloc。如果你查看[此文档](https://developer.apple.com/documentation/objectivec/nsobject?language=objc)，会发现它提到“新建实例的isa实例变量初始化为描述类的一个数据结构；所有其它实例变量的内存(内容)均设置为0”。因此，通过继承Apple类库，我们不仅继承了一些伟大的属性，而且也继承了上述于内存内轻松创建对象并初始化的过程，经此操作创建的对象匹配Runtime期望的数据结构(对象的isa指针指向自定义的类)。
## 何为类缓存(Class Cache)？

## objc_msgSend执行了什么？

## Objective-C消息转发

## 并不脆弱的变量(Modern Runtime)

## Objective-C关联对象

## 混合vTable分发

## 总结
