---
layout: post
title: 理解Objective-C Runtime
---
{{page.title}}
=================================

<img src="/images/posts/2019-06-01/Understanding_the_Objective-C_Runtime.png">

本文译自Colin Wheeler于01/20/2010写就的[Understanding the Objective-C Runtime](https://cocoasamurai.blogspot.com/2010/01/understanding-objective-c-runtime.html)篇(原文在“墙”外)。

当人们谈到Cocoa/Objective-C时，Objective-C Runtime是Objective-C最常被忽视的功能之一。究其原因，尽管Objective-C仅在几个小时内便可轻松掌握，但学习Cocoa的新手们往往是在Cocoa框架及如何使用它们上下工夫。然而，除了知道像*[target doMethodWith:var];*这样的代码被编译器翻译为*objc_msgSend(target,@selector(doMethodWith:),var1);*之外，Runtime如何工作的一些细节是每一名Objective-C学习者应了解的。了解Objective-C运行时的工作原理将有助于加深理解Objective-C语言本身以及你的应用程序是如何运行的。窃以为，无论您的Mac/iPhone开发经验水平如何，均能从本文中收获一二。

## Objective-C Runtime是开源项目

Objective-C Runtime是开源的，并可随时通过[http://opensource.apple.com](https://opensource.apple.com)获得。事实上，除了阅读Apple公司公布的关于它的文档之外，阅读Runtime源代码是我探究其工作方式的第一种方法。你可以通过[objc4-437.1.tar.gz](http://opensource.apple.com/tarballs/objc4/objc4-437.1.tar.gz)下载适用于Mac OS X 10.6.2的当前Runtime版本(截至本文撰写)。

## 动态语言vs静态语言

Objective-C是一门面向运行时的语言，这意味着应由哪个对象执行哪条消息，是在经编译、链接之后，直至运行时才确定下来。这便为你提供了很大的灵活性，你可以根据需要将消息重定向到适当的对象，甚至可以有意地交换方法实现等等。这需要使用运行时，该运行时可以自省对象以查看它们响应与否，并恰当地分派方法。如果我们把它与C这样的语言对比，在C中，你从*main()*方法开始，然后从此处起，这是一个自上而下的设计，遵循着你已写就的代码之逻辑并执行代码中的函数。C结构不能将执行函数的请求转发给其它目标。比如，你有如下的一段C语言代码，编译器将解析、优化，然后将优化后的代码转换成一段汇编代码，最后将其与库链接在一起并生成一个可执行文件。

<img src="/images/posts/2019-06-01/helloWorld_c.png">
<img src="/images/posts/2019-06-01/helloWorld_s.png">

这与Objective-C的不同之处在于，尽管过程类似，但编译器生成的代码取决于Objective-C运行时库之存在。当我们最初认识Objective-C时，我们被告知(在一个最简单层面上)Ojbective-C之括号代码类似于*[self doSomethingWithVar;var1];*被翻译为*objc_msgSend(self,@selector(doSomethingWithVar:),var1);*但是，除此之外，我们对运行时的工作机制一无所知。

## 何为Objective-C Runtime ？

Objective-C Runtime是一个运行时库，它是一个主要以C和Assembler编写的库，向C添加了面向对象功能(以创建Objective-C)。这意味着它将加载类信息，执行所有的方法分派及转发等等。Objective-C运行时创建了使Objective-C面向对象编程成为可能的所有支持结构！

## Objective-C Runtime相关术语

因此，在我们继续深入之前，让我们共同梳理一下相关术语，以便我们能在同一维度讨论问题！Mac及iPhone开发者关心的两个运行时：现代运行时(Modern Runtime)和传统运行时(Legacy Runtime)。现代运行时涵盖了所有64位Mac OS X及iPhone应用；传统运行时则包括余下之所有32的Mac OS X应用。

有两种基本类型的方法。实例方法，像*-(void)doFoo;*一样以“-”开头，并操作于对象实例；类方法，似*+(id)alloc*这般以“+”开头，当然只能对类自身进行操作。方法看起来和C函数很像，其内部为执行一项小任务的一组代码，像下面这样：

<img src="/images/posts/2019-06-01/movieTitle.png">

Objective-C中的选择器实质上是C数据结构，用作标识你希望对象执行的Objective-C方法之手段。在运行时内，其定义类似于*typedef struct objc_selector *SEL;*；使用时，则*SEL aSel = @selector(movieTitle);*。

像*[target getMovieTitleForObject:obj];*这样，一条Objective-C消息是介于两个方括号“[]”之间的所有内容，由ð接收消息的目标对象、你想执行的方法以及要发送的任何参数组成。事实是，你向某对象发送一条消息，并不意味着次对象会执行它。对象可以检查谁是消息的发送者，并据此来决定是执行一则不同的方法，还是转发此消息给一个不同的目标对象。

<img src="/images/posts/2019-06-01/objc_class.png">

如果你在运行时内查找某个类，会发现此定义！在这，要阐明几件事。这段代码中有Objective-C类及对象的结构体定义。而*objc_object*拥有的所有内容仅是一个定义为*isa*的类指针，这就是术语“isa指针”的意思。这枚isa指针是Objective-C运行时检查对象并查看类为何物、然后开始查看在传递消息时是否响应选择器的全部(指针)。最后，我们看到了*id*指针。默认情况下，*id*指针除了告诉我们“它是Objective-C对象”之外，并未告知关于Objective-C对象的任何信息。当你有一个*id*指针时，可以向该对象询问其所属的类，查看它是否对方法做出响应，等等。然后，当知道所指向的对象是何方神圣后，可更有针对性地进行操作。

<img src="/images/posts/2019-06-01/Block.png">

我们可以在LLVM/Clang文档中，看到关于Block的介绍。Block被设计成与Objective-C运行时兼容，因此，它们被视为对象。所以，它们可以响应*-retain*、*-release*、*-copy*等消息。

*typedef id (*IMP)(id self,SEL _cmd,...);*中的IMP(Method Implementation)是编译器为我们生成的指向方法实现的函数指针。如果你刚接触Objective-C，稍后再作了解即可；不过，我们马上可以看到，Objective-C是如何调用方法的。

<img src="/images/posts/2019-06-01/Objective-C_Class.png">

那么，一个Objective-C类内部都有什么呢？Objective-C中类的基本实现如*MyClass*所示。但是，运行时需要记录更多内容：

<img src="/images/posts/2019-06-01/Objective-C_Class_Runtime.png">

我们可以看到类引用了它的父类、它的名称、实例变量、方法、缓存和它声称要遵守的协议。在响应向类或实例发出的消息时，运行时需要这些信息。

## 类定义对象抑或自身是对象？如何实现？

是的，我之前说过，在Objective-C中，类自身也是对象，并且运行时通过创建元类来处理它。当你发送类似*[NSObject alloc]*的消息时，实际上在向类对象发送消息，并且该类对象为元类(Meta Class)的实例，而元类本身又是根元类的实例。当你说继承自NSObject时，你的类将指向*NSObject*作为其父类。然而，所有元类皆指向根元类作为它们的父类。所有的元类仅具有其响应消息的方法列表之类方法。因此，当你向一个类对象发送*[NSObject alloc]*这样的一条消息时，*objc_msgSend()*实际上会浏览元类以查看其响应，然后，若找到了方法，便对类对象进行操作。

## 为什么我们要继承Apple的类库？

因此，最初开始Cocoa开发时，教程说的都是，继承*NSObject*类，然后开始编写某些代码，你只需要继承Apple类库便可享受很多好处。你甚至都没有意识到的一件事是，此时便设置好了你的对象以使用Objective-C运行时。当我们分配类的一个实例时，是这样做的：*MyObject *object = [[MyObject alloc] init];*。第一条得以执行的消息是*+alloc*。如果你查看[文档](https://developer.apple.com/documentation/objectivec/nsobject?language=objc)，它会说“新实例的isa实例变量便初始化为描述该类的数据结构；所有其它实例变量的内存(内容)设置为0。”因此，通过继承Apple类库，我们不仅继承了一些很棒的属性，而且继承了在内存中轻松分配和创建对象的能力，该对象同运行时期望的结构相匹配(带有指向我们类的isa指针)。

## 何为类缓存(Class Cache)？

当Objective-C运行时通过追踪对象的*isa*指针检查对象时，它可以找到实现许多方法的对象。但是呢，你可能只调用其中的一小部分，并且每次执行查找时并没有必要在类分发表中搜索全部的选择器！故，该类实现了一个缓存；每当你搜索一个类的分配表并找到相应的选择器时，它就会将其放入缓存内。因此，当*objc_msgSend()*在类中查找选择器时，它将首先在类缓存中进行搜索。这是基于这样的理论：如果你在某个类上调用了一条消息，则稍后你可能会再次于此类上调用同一条消息。因此，如果考虑到这一点，它意味着如果我们有一个名为*MyObject*的*NSObject*子类，并运行了以下代码：

<img src="/images/posts/2019-06-01/MyObject_setVarA.png">

接下来发生的是(1)*[MyObject alloc]*首先执行。MyObject类未实现*alloc*，因而我们将无法在该类中找到*+alloc*，只能遵循指向*NSObject*的父类指针。(2)我们询问NSObject是否响应*+alloc*，它确实能响应。*+alloc*检查接收器类，即*MyObject*，并分配一个与类大小相同的内存块，且将*isa*初始化为指向*MyObject*类的指针；我们现在有了一个实例，最后将*+alloc*放进*NSObject*的class cache for the class object。(3)到目前为止，我们一直在发送类消息，但是现在我们发送的实例消息仅调用*-init*或我们指定的初始化消息。当然，我们的类会响应该消息，因此，*-(id)init*被放入缓存。(4)然后，*self = [super init]*被调用。*super*是指向对象父类的神奇关键字，因而，我们转到*NSObject*并调用其它的*init*方法。这样做是为了确保**OOP**继承工作正常，因为所有父类都将正确地初始化其变量，然后(位于子类中)你可以准确地初始化变量，然后在你确实需要时覆盖其父类！就*NSObject*而言，没有什么重要的大事继续发生着，但也并非总是如此。有时，会发生棘手的初始化问题。比如说：

<img src="/images/posts/2019-06-01/MyObject_Class_Cache.png">

现在，如果你是接触Cocoa的新手，我请你猜测打印的内容，你可能会说是前者；但实际打印的内容却是后者！这是因为在Objective-C中，*+alloc*有可能返回一个类的对象，而*-init*有可能返回另一个类的对象！

<img src="/images/posts/2019-06-01/assumed_result_of_MyObject_Class_Cache.png">
<img src="/images/posts/2019-06-01/result_of_MyObject_Class_Cache.png">

## objc_msgSend中会发生什么呢？

实际上，*objc_msgSend()*中发生了很多事！比如，我们有代码：

<img src="/images/posts/2019-06-01/printMessageWithString.png">

从这开始，我们跟随目标对象的*isa*指针来查找，并查看对象(或其它任何父类)是否响应选择器@selector(printMessageWithString:)。假如我们在类分配表或其缓存中找到了选择器，则我们遵从函数指针并执行它！如是，*objc_msgSend()*从不返回，它开始执行，然后跟随一个指向你的方法之指针，然后你的方法返回，因此看起来像*objc_msgSend()*返回了！总结一下，在Objective-C运行时代码中看到的内容...(1)检查Ignored Selector和Short Circut。很明显，如果我们在垃圾回收模式下运行，我们可以忽略对*-retain*、*-release*等的调用。(2)检查nil目标。与其它语言不同的是，在Objective-C中向nil发送消息是完全合法的，并且你也有一些正当的理由。假如我们有一个非nil目标，我们继续...(3)然后，我们需要找到类上的*IMP*，因此我们首先在类缓存中搜索它，如果找到，则跟随指针并跳转到该函数。(4)如果未在缓存中找到*IMP*，则接着搜索类分发表，如果在该表中找到了，则跟随指针并跳转。(5)如果在缓存和类分发表中均未找到*IMP*，则我们转向转发机制(forwarding mechanism)。这意味着，最后编译器将你的代码转换为C函数。所以，你些一个向这样的方法，

<img src="/images/posts/2019-06-01/doComputeWithNum.png">

然后，Objective-C运行时通过调用指向那些方法的函数指针来调用你的方法。现在，我说你不能直接调用这些转换后的方法，但是，Cocoa Framework确实提供了一种获取指针的方法...

<img src="/images/posts/2019-06-01/declare_C_function_pointer.png">

如此，如果你确实需要确保执行特定的方法，则可以直接访问该函数并在运行时直接调用之，甚至可以使用它来规避运行时的动态性。这与Objective-C运行时调用方法的方式相同，但是其使用的是*objc_msgSend()*。

## Objective-C消息转发

在Objective-C，将消息转发到它们不知道如何响应的对象是非常合法的(甚至可能是故意的设计决策)。Apple在其文档内提供此功能的一个原因是模拟Objective-C生来就不支持的多重继承，或者你可能只能抽象化设计并在处理消息的幕后隐藏另一个对象/类。这是运行时非常必要的一方面。它像这样工作(1)运行时在你的类及所有父类的缓存和类分发表内进行搜索，但是未找到指定的方法。(2)Objective-C运行时将于你的类上调用*+(BOOL)resolveInstanceMethod:(SEL)aSEL*方法。这将使你有机会提供一种方法实现，并告诉运行时你已经处理了该方法；如果应该开始做搜索了，它将立即找到该方法。你可以似(如下)这般完成此操作...定义一个函数...

<img src="/images/posts/2019-06-01/fooMethod_resolveInstanceMethod.png">

*class_addMethod()*最后一部分中的“v@”是方法返回的内容及参数。你可以在Runtime Guide之Type Encodings部分看到可以放入的内容。(3)Runtime接着调用*- (id)forwardingTargetForSelector:(SEL)aSelector*。这样做是让你有机会将Objective-C运行时指向另一个理应响应消息的对象。你可以像这样来实现它，

<img src="/images/posts/2019-06-01/forwardingTargetForSelector.png">

很显然，你不会想从此方法中返回*self*，否则可能会导致无限循环。(4)运行时随即最后一次尝试将消息发送到它的预想目标，然后着手调用*-(void)forwardInvocation:(NSInvocation *)anInvocation*。若你从未见过*[NSInvocation](https://developer.apple.com/documentation/foundation/nsinvocation?language=occ)*，简单来讲，它的本质是以对象形式的Objective-C Message。一旦你有了*NSInvocation*，基本上，你可以更改消息的任何内容，包括目标、选择器和参数。所以，你可以这样做，

<img src="/images/posts/2019-06-01/forwardInvocation.png">

默认情况下，如果你继承自NSObject，它的*-(void)forwardInvocation:(NSInvocation *)anInvocation*实现仅调用*-doesNotRecognizeSelector:*；如果你想抓住最后的机会做些什么，可以重写它！

## 健壮的实例变量(Modern运行时)

在Runtime的现代版本中，最大的特点就是健壮的实例变量(Non Fragile ivars)。在编译类时，编译器将创建一个ivar布局，该布局显示了访问类中之ivars的位置。这也是以下操作的底层细节：获取对象的指针，查看ivar相对于对象所指向之字节起始处的偏移量，以及要读取的变量类型大小之字节数。因此，你的ivar布局看起来可能像这样，其中左列中的数字为字节偏移量，

<img src="/images/posts/2019-06-01/MyObject_0.png">

在这里，我们为*NSObject*提供了实例变量(ivar)布局，然后对*NSObject*进行子类化以扩展之并添加自己的ivar。在Apple发布更新或所有新的Mac OS X 10.x发行版之前，此方法正常工作。

<img src="/images/posts/2019-06-01/MyObject_1.png">

你的自定义对象会被清除掉(被划掉了两条线)，因为那块区域与父类重叠了。唯一可以阻止这种情况的方法是，如果Apple坚持以往的布局，但是如果真这样做了，则其Framework将无法向前拓展了，因为其实例变量(ivar)布局被固定死了。在脆弱的实例变量(Fragile ivars)之环境下，你必须重新编译自Apple继承的类以恢复兼容性。那么，在健壮的实例变量(Non Fragile ivars)条件下，会发生什么呢？

<img src="/images/posts/2019-06-01/MyObject_2.png">

在健壮实例变量情形下，编译器将生成与脆弱ivar相同的实例变量布局。但是，当运行时检测到重叠的父类时，它会调整你对类新添加的实例变量之偏移量，从而将你在子类中新添加的成员保留下来。

## Objective-C关联对象

Mac OS X 10.6 Snow Leopard最近引入了一项成为“关联引用”(Associated Reference)的特征。与其它对此有原生支持的语言不同，Objective-C不支持动态地将变量添加给对象。因此，到目前为止，你不得不花很多精力来构建基础结构，以假装正在向类中添加变量。现在，到了Mac OS X 10.6，Objective-C运行时对此有了原生支持。如果我们想为每个已存在的类添加一个变量，比如说*NSView*，我们可以这样做，

<img src="/images/posts/2019-06-01/NSView_Custom_Additions.png">

你可以在[runtime.h](https://opensource.apple.com/source/objc4/objc4-437/runtime/runtime.h.auto.html)中看到，有关传递给*objc_setAssociatedObject()*的值之选项。这些选项与你可以在@property语法中传递的选项相匹配。

<img src="/images/posts/2019-06-01/Associated_Object_support_437.png">

## 混合vTable分发

如果你浏览现代运行时代码([objc-runtime-new.m](https://opensource.apple.com/source/objc4/objc4-437/runtime/objc-runtime-new.m)),将会发现

<img src="/images/posts/2019-06-01/vtable_dispatch.png">

其背后的思想是，运行时试图在此*vtable*中存储最常调用的选择器，从而反过来为你的应用程序加速，因为它使用的指令少于*objc_msgSend()*。这款*vtable*含16件最常用的选择器，它们于全局调用之所有选择器内占绝大多数。实际上，在代码的更下方，你可以看到支持垃圾回收及不支持垃圾回收应用程序之默认选择器...

<img src="/images/posts/2019-06-01/defaultVtable_defaultVtableGC.png">

那么，你如何清楚是否要处理呢？调试时，你会在堆栈调用信息内看到这几种方法中的某一个。你基本上应该将所有这些视为*objc_msgSend()*般以进行调试...在运行时，分配vtable中任一方法时，*objc_msgSend_fixup*便发生了。当你调用理应位于vtable内却不再存于其中的任一方法时，会发生*objc_msgSend_fixedup*。你可能会看到对*objc_msgSend_vtable[0-15]的调用，如*objc_msgSend_vtable5*般，这意味着你正在调用其中的某一个常用方法。运行时可以根据需要以分配或取消分配它们，因此，你不应指望着*objc_msgSend_vtable10在某次运行中与*-length*相对应；这意味着它在今后的任何一次运行中都将存在。

## 总结

我希望你喜欢此篇小文，它基本上构成了我在与[Des Moines Cocoaheads](http://cocoaheads.org/us/DesMoinesIowa/index.html)进行的Objective-C Runtime访谈所涵盖之内容(同我们交谈一样，需要很长时间才能介绍完)。Objective-C运行时是一件非常出色的作品，为我们的Cocoa/Objective-C应用程序提供了强大的功能，并使我们认为理所应当的众多功能成为可能。如果你还没看过Apple提供的这些文档，一定得读一读，这些文档阐释了如何利用Objective-C运行时。感谢[Objective-C Runtime Programming Guide](https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/ObjCRuntimeGuide/Introduction/Introduction.html?language=objc#//apple_ref/doc/uid/TP40008048)和[Objective-C Runtime Reference](https://developer.apple.com/documentation/objectivec/objective-c_runtime?language=objc)。