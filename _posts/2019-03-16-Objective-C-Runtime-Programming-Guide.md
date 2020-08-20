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

例如，NSObject类定义了一个`description`实例方法，此方法返回一个描述类内容的字符串。这主要用于调试——GDB print-object命令打印出自此方法返回的字符串。NSObject的此方法实现对类包含的内容并不知情，所以它返回一个包含对象名称与地址的字符串。NSObject子类可实现此方法以返回更多细节。再比如，Foundation类NSArray返回它所包含之对象的描述列表。

一些NSObject方法直接向运行时系统查询信息。这些方法允许对象执行内省(introspection)。这种方法的例子有，`class`方法，可以要求对象识别所属种类;`isKindOfClass:`和`isMemberOfClass:`,检测对象在继承层次中的位置;`respondsToSelector:`,则检查对象是否可以响应一条特定的消息;`conformsToProtocol:`,指出对象是否按要求实现了指定协议中定义的方法;以及`methodForSelector:`,则提供方法实现的地址。像这些方法给了对象内省自身的能力。<br/>
### 2.3 Runtime函数
运行时系统是一个动态共享库，具有一个公共接口，该接口由位于目录`/usr/include/objc`内的头文件中一组函数和数据结构组成。其中，诸多函数允许您使用普通C语言来复制编译器在你编写Objective-C代码时所作的事情。剩余的其它函数则构成了某些功能之基础，这些功能是经由NSObject类之方法导出的。这些函数使开发运行时系统的其它接口、并生产增强开发环境之工具成为可能；但是，以Objective-C进行编码时并不需要它们。然而，不巧的是，当编写Objective-C程序时，可能偶尔会用到其中之少量运行时函数。所有这些函数在Objective-C Runtime Reference均有所描述。<br/>

## 3 Messaging消息传递
本章描述消息表达式如何转换成`objc_msgSend`函数调用，以及如何依名称调取对应之方法。随即解释你如何利用`objc_msgSend`,并且(如果你有需要)如何避免动态绑定。

### 3.1 objc_msgSend函数
在Objective-C，消息直到运行时才绑定到对应的方法实现。编译器将消息表达式`[receiver message]`转换为对消息传递函数`objc_msgSend`的调用。此函数将`receiver`和`message`中提到的方法名称，即方法选择器，作为它的两个主要参数:`objc_msgSend(receiver,selector);`。当然了，在`message`中传递的任意参数也交由`objc_msgSend`处置。<br/>
`objc_msgSend(receiver,selector,arg1,arg2,...)`消息传递函数为动态绑定做了全部必要的工作：
- 它首先找到选择器(selector)所提及的程序(方法实现)。由于相同的方法可以通过不同的类加以实现，所以它找到的确切程序依赖receiver所属的class。
- 然后，它调用该程序，将接收对象(指向其数据的指针)以及为该方法指定的任何参数传递给它。
- 最后，它传递程序的返回值作为它自己的返回值。

消息传递的关键在于编译器为每个类及对象构造的‘结构’。每个class结构均包含这两项基本要素：
- 一个指向父类(superclass)的指针。
- 一张类调度(dispatch)表格。此表中的条目将方法选择器与它们所标识的方法之特定于类的地址关联在一起。`setOrigin:`方法的选择器与`setOrigin:`的地址(实现过程/具体程序)相关联，display方法的选择器同display之地址相关联，依次类推。

当一个新对象被创建时，针对它的内存(memroy)也会被分配好，并且其实例变量也被初始化。首先，最重要的变量是一个指向它class结构的‘指针’！这个名为`isa`的指针赋予对象对其类的访问权限，并通过类访问它所继承的所有类。类及对象结构之元素诠释于Figure 3-1中：<br/>
<img src="/images/posts/2019-03-16/Figure_3-1_Messaging_Framework.png">
当消息发送到对象时，消息函数跟随对象的`isa`指针到class结构，(于此)在dispatch表中查找对应的方法选择器。若在class结构中未发现选择器，`objc_msgSend`跟随指向父类(superclass)的指针，尝试在其父类之dispatch表内查找对应之选择器。持续的失败会导致`objc_msgSend`爬上类层次直至到达`NSObject`处。一旦找到选择器，函数便调用表中输入的方法，并将接收对象之数据结构传递给它。<br/>

这便是在运行时选择方法实现的方式，或者，以面向对象编程之术语而言，这些方法被动态绑定到消息上！<br/>

为加速消息传递过程计，运行时系统在使用方法时缓存其选择器和地址。每个类都有一处单独的缓存，它可包含所继承方法的选择器以及类中定义之方法的选择器。在搜索调度(dispatch)表之前，消息程序首先检查接收对象类的缓存(依据曾使用过的方法可能会再次使用的理论)。如果方法选择器在缓存中，消息传递则只比函数调用稍慢一点。一旦一个程序已运行了足够长的时间来‘预热’其缓存，它发送的几乎全部消息都会找到一个被缓存的方法。当程序运行时，缓存会动态增长以容纳新消息。<br/>

### 3.2 Using Hidden Arguments使用隐式参数
当`objc_msgSend`找到实现方法的procedure时，它调用该procedure并将消息中的所有参数传递给它。它还传递给procedure两个隐式参数：
- 接收对象。
- 方法的选择器。

这些参数为每个方法实现提供了关于调用它的消息表达式之两部分的明确信息。说它们是‘隐式的’，因为未在定义方法的源代码中声明。在编译代码时，它们被插入到实现中。<br/>

尽管这些参数没有明确声明，源码仍然可以引用它们(就像它可以引用接收对象的实例变量一样)。方法将接收对象引为`self`，将它自己的选择器称为`_cmd`。在下面的示例中，`_cmd`指的是strange方法的选择器，而`self`则指代接收strange消息的对象。<br/>
<img src="/images/posts/2019-03-16/strange_0.png">
<img src="/images/posts/2019-03-16/strange_1.png">
这两个参数中，`self`更有用。事实上，这是接收对象之实例变量对方法可用的方式。<br/>

### 3.3 Getting a Method Address获取方法地址
规避动态绑定的唯一方法是获取方法的地址，并像调用函数一样直接调用它。这可能适用于特定方法将被连续执行多次的罕见情况，并且希望避免每次执行该方法时的消息传递之开销。<br/>

利用在`NSObject`类中定义的方法，`methodForSelector:`，你可以请求实现方法的procedure之指针，然后使用此指针调用该procedure。`methodForSelector:`返回的指针必须仔细强制转换为正确的函数类型。强制转换中应包含返回值及参数类型。下面的示例显示了如何调用实现`setFilled:`方法之过程。<br/>
<img src="/images/posts/2019-03-16/Getting_a_Method_Address.png">
传递过程的前两个参数是接收对象(`self`)和方法选择器(`_cmd`)。这些参数隐藏在方法语法中，但当方法作为函数调用时必须显式显示！

使用`methodForSelector:`避开动态绑定节省了消息传递所需的大部分时间。然而，只有当一个特定的消息被重复多次时，这种节省(效果)才是显著的，如上面所示的for循环。请注意，`methodForSelector:`由**Cocoa**运行时系统提供；这并非Objective-C语言本身的特征。

## 4 Dynamic Method Resolution动态方法解析
本章描述如何动态地提供方法的实现。
### 4.1 Dynamic Method Resolution动态方法解析
在某些情况下，你可能动态地提供一个方法的实现。比如，Objective-C声明的属性特征包括@dynamic指令：`@dynamic propertyName;`告诉编译器将动态地提供与属性关联的方法。你可以实现`resolveInstanceMethod:`和`resolveClassMethod:`方法分别为实例和类方法的给定选择器动态地提供实现！

Objective-C方法只是一个至少接受两个参数(`self`与`_cmd`)的**C**语言函数。你可以用函数`class_addMethod`将函数作为方法添加到类中。因此，给定以下功能：
```
void dynamicMethodIMP(id self, SEL _cmd) {
	// implementation ....
}
```
你可以利用`resolveInstanceMethod:`将它作为方法(称为resolveThisMethodDynamically)动态地添加到类中。<br/>
<img src="/images/posts/2019-03-16/MyClass_0.png">
<img src="/images/posts/2019-03-16/MyClass_1.png">
转发方法和动态方法解析，两者在很大程度上是正交的。类有机会在转发机制启动之前动态解析一则方法。如果调用了`respondsToSelector:`或`instancesRespondToSelector:`，则动态方法解析器有机会首先为选择器提供一个**IMP**。如果你实现了`resolveInstanceMethod:`但是希望某特定的选择器实际上能通过转发机制得以转发，那么，对于此类选择器，返回NO即可！

### 4.2 Dynamic Loading 动态加载
一个Objective-C程序可以在运行时(期间)加载并链接新的类及类别(category)。新代码被合并进程序，并与开始时加载的类和类别享受同等地位。<br/>

动态加载可以用来做很多不同的事情。比如，**System Preferences**应用程序中的各种模块便是动态加载的。<br/>

在**Cocoa**环境中，动态加载技术通常用于允许定制应用程序。其他人可以编写你的应用程序在运行时加载的模块——就像**Interface Builder**加载自定义调色板和**OS X System Preferences**应用程序加载自定义首选项模块一样。可加载模块扩展了应用程序的功能。它们以你允许却无法预料或定义之方式对此加以贡献。你提供框架，但其他人提供具体的实现代码。

虽然有运行时函数可以在**Mach-O**文件内执行Objective-C模块的动态加载(`objc_loadModules`定义在`objc/objc-load.h`中)，但是，Cocoa的**NSBundle**类为动态加载提供了一个更为方便的接口——一个面向对象并集成了相关服务的接口。有关**NSBundle**类及其使用的信息，见**Foundation**框架参考中NSBundle类规范。关于Mach-O文件的信息，则参阅OS X ABI Mach-O File Format Reference。<br/>

## 5 消息转发
向不处理消息的对象发送对应的消息是错误的。然而，在宣布错误之前，运行时系统给接收对象第二次机会以处理该消息。
### 5.1 Forwarding转发
如果你向一个不处理该消息的对象发送了此消息，在宣布错误之前，运行时系统会接收对象发送一条`forwardInvocation:`消息，以**NSInvocation**对象作为其唯一的参数——NSInvocation对象封装了原始消息和随其传递的参数。<br/>

你可以实现`forwardInvocation:`方法来给消息一个默认响应，或者以其它方式避免此错误。顾名思义，`forwardInvocation:`通常用于转发消息到另一个对象。<br/>

要了解转发的范围和意图，请想象一下场景：首先，假设你正在设计一个可响应一条名为`negotiate`消息的对象，并且希望它的响应中包含另一种对象的响应。你可以通过将`negotiate`消息传递给(你)实现的`negotiate`方法主体中的其它对象来轻松实现这一点。<br/>

更进一步，假设你希望你的对象对`negotiate`消息的响应恰好是在另一个类中实现的响应。实现这一点的一种思路是让你的类从另一个类继承该方法。然而，如此安排事情许是不可能的。你的类和实现`negotiate`方法的类位于继承层次结构的不同分支中。很好的理由！<br/>

即使你的类不能继承此`negotiate`方法，你仍然可以通过实现简单地将消息传递给另一个类之实例的方法这样的版本来‘借用’它：<br/>
<img src="/images/posts/2019-03-16/negotiate.png">
如此行事可能会有点麻烦，尤其是如果有很多条消息，你都想让你的对象传递给另一对象。你必须实现一则方法来覆盖(你)想从另一个类中借用的每一个方法。此外，在你编写代码时，不可能兼顾到你不知道的所有消息。这样的消息集合可能依赖于运行时的事件，并且它可能随着将来新方法和类的实现而改变。<br/>

`forwardInvocation:`消息提供的第二次机会，为解决此问题提供了一种不那么特别的解决方案，并且是动态而非静态的！它是这样工作的：当一个对象不能响应一条消息，因它没有与消息中的选择器匹配之方法时，运行时系统通过发送一条`forwardInvocation:`消息来通知这个对象。每一个对象都从NSObject类那里继承了一个`forwardInvocation:`方法。但是，NSObject版本的方法仅仅是调用`doesNotRecognizeSelector:`。通过覆盖NSObject版本并实现自己的版本，可以利用`forwardInvocation:`提供的机会将消息转发给其它对象。<br/>

要转发一条消息，`forwardInvocation:`要做的就是：<br/>
- 确定消息应该发往何处，以及
- 将其与原始参数一起发往到目的地

可以使用`invokeWithTarget:`方法发送消息：<br/>
<img src="/images/posts/2019-03-16/forwardInvocation.png">
所转发消息之返回值将返回给原始发送者。所有类型的返回值都可以传递给发送者，包括ids、结构体和双精度浮点数等。<br/>

`forwardInvocation:`方法可充任未识别消息的分发中心，将它们分发给不同的接收者。或者它可以是一座中转站，将所有的消息发往同一目的地。它可以将一条消息翻译为另一条，或者简单地‘吞下’一些信息(‘留中不发’)，如此也不会有回应和错误。`forwardInvocation:`方法还可以将几条消息合并成一声响应。`forwardInvocation:`之所作所为完全取决于实现者。然而，它提供了在转发链中链接对象的机会，为程序设计打开了可能性之门。<br/>

注意：只有当消息未在名义接收者处调用现有方法时，`forwardInvocation:`才能加以处理消息。举例来说，如果你希望你的对象将`negotiate`消息转发给另一个对象，它自己就不能有`negotiate`方法。如果它自身拥有，则消息将永远不会到达`forwardInvocation:`处。有关转发和调用的更多信息，参见Fondation框架参考中的NSInvocation类规范。<br/>

### 5.2 转发及多重继承
转发模仿了继承，并且可以用来把多重继承的一些效果借给Objective-C程序。如图5-1所示，通过转发消息以实现响应的对象似乎‘借用’或‘继承’了在另一个类中定义的方法实现。<br/>

<img src="/images/posts/2019-03-16/Figure_5-1_Forwarding.png">
在此示意图中，Warrior类的一个实例将negotiate消息转发给Diplomat类的一个实例。Warrior会像Diplomat一样谈判(negotiate)。它似乎对`negotiate`消息做出了应有的回应，实际上它也确实做出了响应(尽管实际上是一名Diplomat在做这项工作)。<br/>

因此，转发消息的对象从继承层次结构的两个分支处‘继承’方法——它自己的分支和响应消息的对象所属之分支。在上面的例子中，Warrior类似乎继承了Diplomat以及它自己的父类。<br/>

转发提供了你通常希望从多重继承中获得的大多数功能。然而，二者之间有一个重要区别：多重继承在一个对象中结合了不同的功能。它倾向于大型的、多面的对象。另一方面，转发给不同的对象赋予不同的职责。它将问题分解成更小的对象，但是以对消息发送者透明的方式关联这些对象！<br/>

### 5.3 Surrogate Objects代理对象
转发不仅模仿了多重继承，还使得开发代表或‘覆盖’更多实体对象的轻量级对象成为可能。代理代表另一个对象，并向他传递消息。<br/>

在The Objective-C Programming Language的‘Remote Messaging’中讨论的代理(proxy)就是这样一种代理(surrogate)。代理(proxy)负责将消息转发给远程接收者的管理细节，确保通过连接以复制并检索参数值，等等。但是，它并没有尝试做其它事情；它没有复制远程对象的功能，只是给远程对象一个本地地址，一块它可以在另一个应用程序中接收消息的地方。<br/>

其它种类的代理对象(surrogate object)也是可能的。例如，假设你有一个处理大量数据的对象，它可能会创建一张复杂的图像或读取磁盘上文件的内容。设置这个对象可能很耗时，所以你偏好在真正需要或系统资源暂时空闲时以惰性设置！同时，为了让应用程序中的其它对象正常工作，你至少需要一个(对象的)占位符。<br/>

在这种情况下，你可以先创建一个轻量级的代理，而非完整的对象。这个对象可以自己完成一些事情，比如回答关于数据的问题，但是大多数情况下，它只是为更大的对象保留一处位置而已，时辰一到，便可向其转发消息。当代理(surrogate)的`forwardInvocation:`方法第一次接收到一条去往另一对象的消息时，它将确保该对象存在，并且在不存在之时创建它。较大对象的所有消息都是通过代理，因此，就程序的其它部分而言，代理和较大对象是相同的！<br/>
### 5.4 Forwarding and Inheritance转发和继承
虽然转发模拟了继承，但是NSObject类从未混淆这两者。像`respondsToSelector:`和`isKindOfClass:`这样的方法只关注继承层次，而不关心转发链。例如，如果一个Warrior对象被询问是否响应negotiate消息，
```
if ([aWarrior respondsToSelector:@selector(negotiate)]) 
	......
```
答案是NO! 虽然它可以无误地接收negotiate消息并在某种意义上将它们转发给Diplomat做出响应。<br/>

在许多情况下，NO是正确答案。但它可能也不是。如果你用转发来设置代理对象或扩展类的功能，那么转发机制应该像继承一样透明。如果你希望(你的)对象表现得好像它们真正继承了它们转发消息到的对象之行为，那么你需要重新实现`respondsToSelector:`和`isKindOfClass:`方法来包含你的转发算法：<br/>
<img src="/images/posts/2019-03-16/respondsToSelector.png">
除了`respondsToSelector:`和`isKindOfClass:`，`instancesRespondToSelector:`方法也应当反映其转发算法。若使用协议，同样应该将`conformsToProtocol:`方法添加到表中。类似地，如果一个对象转发它收到的任何远程消息，它应该有一个版本的`methodSignatureForSelector:`，它可以返回对最终响应已转发之消息的方法的准确描述；例如，如果一个对象能够将消息转发给它的代理，那么你将实现如下所示的`methodSignatureForSelector:`，<br/>
<img src="/images/posts/2019-03-16/methodSignatureForSelector.png">
你可以考虑将转发algorithm放在私有代码中的某个地方，并调用所有这些方法，当然也包括`forwardInvocation:`。<br/>

注意：这是一种高级技术，仅适用于没有其它解决方案的情况。它不是用来代替继承的。如果你非得使用这项技术，须确保你已完全理解了进行转发的类和你要转发到的(目的地)类之行为。<br/>

本节中提到的方法在Foundation框架参考中的NSObject类规范中进行了描述。有关`invokeWithTarget:`的信息，可参阅Foundation framework参考中的NSInvocation类规范。<br/>

## 6 Type Encodings类型编码
为助运行时系统一臂之力，编译器将每个方法的返回和参数类型编码成字符串，并把字符串同方法选择器相关联。它使用的编码方案在其它环境中也很有用，因此可以通过`@encode()`编译器指令公开使用。当给定一个类型规范时，`@encode()`返回一个针对该类型进行编码的字符串。该类型可以是基本类型，如`int`,指针,标记结构体或`union`,或者类名——事实上，可以用于**C**`sizeof()`运算符之参数的任何类型。<br/>
```
char *buf1 = @encode(int **);
char *buf2 = @encode(struct key);
char *buf3 = @encode(Rectangle);
```
下表列出了种种之类型代码。请注意，其中许多代码与你出于存档(archiving)或分发(distribution)目的对象进行编码时使用的代码重叠。但是，这里列出的一些代码是你在编写编码器(coder)时不能使用的，还有一些代码是你在编写并非由`@encode()`生成之编码器时，想要使用的。(有关为存档或分发而编码对象之更多信息，参见Foundation Framework参考中的NSCoder类规范)<br/>
<img src="/images/posts/2019-03-16/Table_6-1_0.png">
<img src="/images/posts/2019-03-16/Table_6-1_1.png">
数组的类型代码用方括号括起来；数组内的元素数是紧接在开括号之后，数组类型之前指定的。例如，指向浮点数的12个指针的数组将被编码为`[12^f]`。<br/>

结构体在大括号中指定，联合体(union)在括号内指定。首先列出结构题标签，然后依次列出等号和结构体字段之代码。例如，结构体<br/>
<img src="/images/posts/2019-03-16/typedef_struct_example.png">
的编码，如`{example=@*i}`。无论是定义的类型名称(Example)还是结构体标记(example)，只要传递给`@encode()`，均产生相同的编码结果。结构体指针的编码与结构体字段的编码携带相同数量的信息，`^{example=@*i}`。然而，另一个间接层次则删除了内部类型的规范:`^^{example}`。

对象被视为结构体。例如，将NSObject类名传递给`@encode()`会产生编码：`{NSObject=#}`。NSObject类只声明一个实例变量，其类型为Class的`isa`。

注意，虽然`@encode()`指令未返回它们，但是当它们用于声明协议中的方法时，运行时系统使用Table 6-2中列出的附加编码作为类型限定符。<br/>
<img src="/images/posts/2019-03-16/Table_6-2_Objective-C_method_0.png">
<img src="/images/posts/2019-03-16/Table_6-2_Objective-C_method_1.png">

## 7 Declared Properties属性声明
当编译器遇到属性声明时，它会生成与封闭类、类别(category)或协议相关联的描述性元数据。你可以用函数来访问这些元数据，这些函数支持在类或协议上按名称查找属性，以`@encode`字符串的形式获取属性的类型，并以**C**字符串数组的形式复制属性的attributes列表。每个类和协议都有一个声明属性的列表。<br/>
### 7.1 Property Types and Functions属性类型和函数
Property结构体定义了属性描述符的不透明句柄,`typedef struct objc_property *Property;`。你可以用`class_copyPropertyList`和`protocol_copyPropertyList`分别检索与类(包括加载的类别/category)和协议相关联的属性数组：<br/>
```
objc_property_t *class_copyPropertyList(Class cls, unsigned int *outCount)
objc_property_t *protocol_copyPropertyList(Protocol *proto, unsigned int *outCount)
```
又比如，给定以下类声明：<br/>
```
@interface Lender : NSObject {
    float alone;
}

@property float alone;

@end
```
你可以使用以下命令以获取属性列表：<br/>
```
id LenderClass = objc_getClass("Lender");
unsigned int outCount;
objc_property_t *properties = class_copyPropertyList(LenderClass, &outCount);
```

你也可以用`property_getName`函数来发现某属性的名称,可以使用`property_getAttributes`函数来发现属性的名称和`@encode`类型字符串, 还可以利用函数`class_getProperty`和`protocol_getProperty`分别在类和协议中获取对具有给定名称的属性之引用。<br/>
```
const char *property_getName(objc_property_t property)
const char *property_getAttributes(objc_property_t property)
objc_property_t class_getProperty(Class cls, const char *name)
objc_property_t protocol_getProperty(Protocol *proto, const char *name, BOOL
isRequiredProperty, BOOL isInstanceProperty)
```
将这些放在一起，您可以使用以下代码打印与类相关联的所有属性的列表:<br/>
<img src="/images/posts/2019-03-16/LenderClass.png">

### 7.2 Property Type String属性类型字符串
您可以使用`property_getAttributes`函数来发现属性的名称、`@encode`类型字符串以及其他属性。该字符串以T开头，后跟`@encode`类型和逗号，以V结尾，后跟支持实例变量的名称。在这两者之间，属性由以下描述符指定，用逗号分隔:<br/>
<img src="/images/posts/2019-03-16/Table_7-1_Declared_property_type_encodings.png">

### 7.3 Property Attribute Description Examples
根据这些定义：
```
enum FooManChu { FOO, MAN, CHU };
struct YorkshireTeaStruct { int pot; char lady; };
typedef struct YorkshireTeaStruct YorkshireTeaStructType;
union MoneyUnion { float alone; double down; };
```
下表显示了示例属性声明和由`property_getAttributes`返回的相应字符串:<br/>
<img src="/images/posts/2019-03-16/property_getAttributes_0.png">
<img src="/images/posts/2019-03-16/property_getAttributes_1.png">
<img src="/images/posts/2019-03-16/property_getAttributes_2.png">

<结束！>
