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

## 何为Objective-C Runtime ？

## Objective-C Runtime相关术语

## 类定义对象抑或自身是对象？如何实现？

## 为什么我们要继承Apple的类库？

## 何为类缓存(Class Cache)？

## objc_msgSend执行了什么？

## Objective-C消息转发

## 并不脆弱的变量(Modern Runtime)

## Objective-C关联对象

## 混合vTable分发

## 总结
