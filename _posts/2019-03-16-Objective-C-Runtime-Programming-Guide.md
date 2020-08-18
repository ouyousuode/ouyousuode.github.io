---
layout: post
title: Objective-C Runtime编程指南
---
{{page.title}}
=========================
<img src="/images/posts/2019-03-16/Objective-C_Runtime_Programming_Guide.png">
<img src="/images/posts/2019-03-16/Developer.png">

Objective-C语言将尽可能多的决策从编译期及链接期推迟到运行时(runtime)。但凡有机会，它便动态地处理事务。这意味着，此语言需要的不仅仅是一款编译器，还有用于执行已编译代码的运行时系统。其中的运行时系统为Objective-C语言充当操作系统的角色；这便是语言工作的原理。

本文档着眼于NSObject类以及Objective-C程序如何与运行时系统交互。尤其是，它调查了运行时系统动态加载新类、以及向其它对象转发消息之范式。它也提供了关于(当程序运行时)如何找到对象信息的内容。你应当阅读本文档以获得对Objective-C运行时系统如何工作以及如何充分利用它的理解。然后，就一般而言，并不是说非得清楚和理解这些材料才能编写一款Cocoa应用。

Objective-C Runtime Reference描述了Objective-C运行时支持库的数据结构及函数。你的程序可用这些接口同Objective-C运行时系统进行互动。比如，你可以添加类或方法，或者获取已加载类的所有类定义之列表。*Programming with Ojbective-C*则详述了Objective-C语言。

## 1 Runtime Version and Platforms 运行时版本及平台
在不同的平台上，有不同版本的Objective-C运行时。事实上，存在两个Objective-C运行时版本，即“现代的”与“遗留的”。现代版随Objective-C 2.0引入并包括许多新功能。Objective-C 1 Runtime Reference描述了运行时之遗留版本的编程接口；现代版本的编程接口则于Objective-C Runtime Reference内。

最显著的新特征是现代运行时内的实例变量均为健壮的('non-fragile').
- 在遗留运行时，如果你对类中实例变量的布局做修改，你必须重新编译继承自它的所有类。
- 而在现代运行时，做了同样的修改后，你不必重新编译。

另外，现代运行时支持对已声明属性之实例变量合成(synthesis)!

遗留的运行时只用于32位的Mac OS X程序，现代的运行时则用于iOS应用程序(在模拟器和iOS设备上)以及在Mac OS X 10.5或更高版本上运行的64位应用程序(默认)。

## 2 Interacting with the Runtime 与运行时交互
Objective-C程序在三个不同层面上与运行时系统交互：经由Objective-C源代码；通过定义在Foundation框架之NSObject类中的方法；通过对运行时系统之函数的直接调用。
### 2.1 Objective-C Source Code 源代码
在很大程度上，运行时系统是自动并在幕后工作的。你只需编写和编译Objective-C源码，便可以使用此运行时系统。

当你编译包含Objective-C类和方法的代码时，编译器便创建对应的数据结构与函数调用，此二者实现语言的动态特征。数据结构捕获在类、类别定义以及协议声明中发现的信息；它们包括类和协议对象，以及方法选择器、实例变量模板和从源代码内提取的其它信息。主要的运行时函数是那些发送消息的函数。它由源代码消息表达式调用。
### 2.2 NSObject方法
Cocoa内的大部分对象是NSObject的子类，因此，这大部分对象继承NSObject内定义的方法。(显著的例外情况是NSProxy类；更多信息，见于*5 消息转发*一节)因此，它的方法建立了一套每个实例和每个对象固有之行为。然而，少数情况下，NSObject类只定义了一个模板，用于如何完成某件事；它自身并不提供全部的必需代码。

例如，NSObject类定义了一个description实例方法，此方法返回一个描述类内容的字符串。这主要用于调试——GDB print-object命令打印出自此方法返回的字符串。NSObject的此方法实现对类包含的内容并不知情，所以它返回一个包含对象名称与地址的字符串。NSObject子类可实现此方法以返回更多细节。再比如，Foundation类NSArray返回它所包含之对象的描述列表。

一些NSObject方法直接向运行时系统查询信息。这些方法允许对象执行内省(introspection)。这种方法的例子有，class方法，可以要求对象识别所属种类；isKindOfClass:和isMemberOfClass:,检测对象在继承层次中的位置；respondsToSelector:，则检查对象是否可以响应一条特定的消息；conformsToProtocol:，指出对象是否按要求实现了指定协议中定义的方法；以及methodForSelector:,则提供方法实现的地址。像这些方法给了对象内省自身的能力。
### 2.3 Runtime函数
运行时系统是一个动态共享库，具有一个公共接口，该接口由位于目录/usr/include/objc内的头文件中一组函数和数据结构组成。其中，诸多函数允许您使用普通C语言来复制编译器在你编写Objective-C代码时所作的事情。剩余的其它函数则构成了某些功能之基础，这些功能是经由NSObject类之方法导出的。这些函数使开发运行时系统的其它接口、并生产增强开发环境之工具成为可能；但是，以Objective-C进行编码时并不需要它们。然而，不巧的是，当编写Objective-C程序时，可能偶尔会用到其中之少量运行时函数。所有这些函数在Objective-C Runtime Reference均有所描述。

## 3 Messaging消息传递
### 3.1 objc_msgSend函数
<img src="/images/posts/2019-03-16/Figure_3-1_Messaging_Framework.png">

### 3.2 Using Hidden Arguments 使用隐式参数
### 3.3 Getting a Method Address


## 4 Dynamic Method Resolution
### 4.1 Dynamic Method Resolution
### 4.2 Dynamic Loading 动态加载



## 5 消息转发
### 5.1 Forwarding
### 5.2 Forwarding and Multiple Inheritance
<img src="/images/posts/2019-03-16/Figure_5-1_Forwarding.png">

### 5.3 Surrogate Objects
### 5.4 Forwarding and Inheritance


## 6 Type Encodings
<img src="/images/posts/2019-03-16/Table_6-1_0.png">
<img src="/images/posts/2019-03-16/Table_6-1_1.png">
<img src="/images/posts/2019-03-16/Table_6-2_Objective-C_method_0.png">
<img src="/images/posts/2019-03-16/Table_6-2_Objective-C_method_1.png">

## 7 Declared Properties
### 7.1 Property Types and Functions
### 7.2 Property Type String
<img src="/images/posts/2019-03-16/Table_7-1_Declared_property_type_encodings.png">

### 7.3 Property Attribute Description Examples
<img src="/images/posts/2019-03-16/property_getAttributes_0.png">
<img src="/images/posts/2019-03-16/property_getAttributes_1.png">
<img src="/images/posts/2019-03-16/property_getAttributes_2.png">
