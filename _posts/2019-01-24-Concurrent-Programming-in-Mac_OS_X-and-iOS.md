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
<img src="/images/posts/2019-01-24/convertIntToString_usingBlockObject.png"> <br/>
我们现在要做的就是用我们选择的块对象调用`convertIntToString:`方法(Example 1-2)。<br/>
<img src="/images/posts/2019-01-24/Example_1-2.png">
既然我们已对独立块对象有所了解，那么让我们来看看内联(inline)块对象。在我们刚才看到的`doTheConversion`方法中，我们将`intToString`块对象作为参数传递给了`convertIntToString:usingBlockObject:`方法。如果我们没有准备好传递给这个方法的块对象呢？这也不成问题。如前所述，块对象是一级对象(first-class objects)，可以在运行时构建。让我们来看看`doTheConversion`方法的另一种实现(Example 1-3)。<br/>
<img src="/images/posts/2019-01-24/Example_1-3.png">
将Example1-3与之前的Example1-1比较。我删除了提供块对象签名的初始代码，它由名称和参数组成，`(^intToString)(NSUInteger)`。我保留了块对象的所有剩余部分。现在是匿名对象。但这并不意味着我没有办法引用block对象。我用`=`赋予它一个类型和名称:`IntToStringConverter inlineConverter`。现在，我可以使用数据类型在方法中强制正确使用，并使用名称以实际传递块对象。除了如上所示内联构造块对象之外，我们还可以构造块对象的同时将其作为参数传递:<br/>
<img src="/images/posts/2019-01-24/doTheConversion.png">
将此示例与Example 1-2进行比较。这两种方法都通过使用`usingBlockObject`语法来使用块对象。但是早期版本通过名称(intToString)引用了一个先前声明的块对象，而这个版本只是动态地创建了一个块对象。在这段代码中，我们构造了一个内联块对象，该对象作为第2⃣️个参数传递给了`convertIntToString:usingBlockObject:`方法。<br/>

我相信，至此，你已经对块对象有了足够的了解，以能够进入更有趣的细节；我们将在下一节开始。<br/>

## 1.3 Variables and Their Scope in Block Objects 块对象中的变量及其范围
以下是关于块对象中变量的简要概述:
- 块对象中的局部变量的工作方式与Objective-C方法内完全相同。
- 对于内联块对象，局部变量不仅构成块对象内定义的变量，而且包含实现该块对象之方法中定义的变量。(例子稍后便是)
- 你不能在Objective-C类中实现的独立块对象内引用`self`。如果需要访问`self`，则必须将该对象作为参数传递给块对象。我们很快就会看到这样的实例。
- 对于内联块对象，块对象实现之内部定义的局部变量是可以读取并写入的。换句话说，块对象对块对象体中定义的变量具有读写权限。
- 对内联块对象来说，实现该块对象的Objective-C方法内的局部变量「只能读取，不能写入」。但是有一个例外：如果变量是用`__block`存储类型定义的，那么块对象拥有对此类变量的写入权。我们也将看到一个这样的示例。
- 假设你有一个类型为`NSObject`的对象，并且在该对象的实现中，你将块对象与GCD结合起来使用。那么，在这个块对象中，你可以**读写**该NSObject的声明属性。
- 若想在独立的块对象内访问`NSObject`的声明属性，只能利用这些属性的`setter`和`getter`方法。在独立块对象内部，不能使用点标记法访问对象的声明属性。<br/>

让我们先来看看如何使用两类块对象实现的局部变量。一种是内联块对象，另一则为独立块对象：<br/>
<img src="/images/posts/2019-01-24/independentBlockObject_Block.png"> <br/>
调用此块对象，我们赋予的值被打印到控制台窗口:<br/>
<img src="/images/posts/2019-01-24/result_independentBlockObject_Block.png"> <br/>
目前为止，一切都挺顺。现在，我们来看看内联块对象以及针对其而言的局部变量：<br/>
<img src="/images/posts/2019-01-24/simpleMethod.png"> <br/>
**注**：`NSMutableArray`的`sortUsingComparator:`实例方法试图对可变数组进行排序。此段示例代码的目的只是演示局部变量的使用，所以你也不必知道这个方法实际上究竟做了什么。<br/>

块对象可以读写它自己的`insideVariable`局部变量。然而，默认情况下，块对象对`outsideVariable`变量仅有只读访问权限。为了允许块对象对`outsideVariable`可写，我们必须在`outsideVariable`前加上`__block`存储类型：<br/>
<img src="/images/posts/2019-01-24/simpleMethod_block.png"> <br/>
在内联块对象中访问`self`是可以的，前提为`self`是在创建内联块对象的词法范围中定义的(as long as self is defined in the lexical scope inside which the inline block object is created)。「有点儿绕，用实例说明更直观」例如，在如下示例中，因`simpleMethod`是一个Obejctive-C类的实例方法，故块对象能够访问`self`：<br/>
<img src="/images/posts/2019-01-24/simpleMethod_self.png"> <br/>
在块对象的实现未改变之情况下，便不能访问独立块对象中的`self`。试图编译如下代码将会产生「编译时」错误：<br/>
<img src="/images/posts/2019-01-24/incorrectBlockObject.png"> <br/>
如果你想在独立的块对象内访问`self`，只需要将`self`作为参数传递给块对象：<br/>
<img src="/images/posts/2019-01-24/correctBlockObject.png"> <br/>
**注**：You don’t have to assign the name `self` to this parameter.You can sim- ply call this parameter anything else. However, if you call this parameter `self`,you can simply grab your block object’s code later and place it in an Objective-C method’s implementation without having to change ev- ery instance of your variable’s name to `self` for it to be understood by the compiler.<br/>

让我们看一看声明的属性以及块对象如何访问它们。对内联块对象来说，可以使用点符号对`self`的声明属性进行读写操作！例如，假设我们的类中有一个名为`stringProperty`的`NSString`类型的声明属性：<br/>
<img src="/images/posts/2019-01-24/GCDAppDelegate_h.png"> <br/>
现在，我们可以非常简便地在内联块对象中访问该属性，如下所示:<br/>
<img src="/images/posts/2019-01-24/GCDAppDelegate_m_0.png"> 
<img src="/images/posts/2019-01-24/GCDAppDelegate_m_1.png">
但是，在独立的块对象中，不能使用点符号对「声明的属性」施以读写操作：<br/>
<img src="/images/posts/2019-01-24/correctBlockObject_stringProperty.png"> <br/>
在这种情况下，不使用「点符号」语法，而是使用「合成属性」的`getter`和`setter`方法：<br/>
<img src="/images/posts/2019-01-24/correctBlockObject_setStringProperty.png"> <br/>
当涉及到内联块对象时，有一个你必须记住的非常重要的规则：内联块对象在其词法范围内复制变量值。如果不解其意，也不打紧。让我们看一则实例：<br/>
<img src="/images/posts/2019-01-24/scopeTest.png">
我们声明了一个整数局部变量，并给它赋初值为10.然后，我们实现我们的块对象，但不要调用该块对象。实现块对象后，我们只更改「刚声明的」局部变量的值，⚠️ 块对象稍后被调用时会尝试读取该值。在将局部变量的值更改为20后，我们调用块对象。你可能希望块对象所打印变量的值为20，但它不会。它将打印10，如你所见：<br/>
<img src="/images/posts/2019-01-24/scopeTest_result.png">
此处发生的事情是：块对象在块实现的地方为自身保留了一份`integerValue`变量的只读副本。你可能会想：为什么块对象捕获的只是局部变量`integerValue`的只读副本呢？答案很简单，刚刚的内容已经提到了。除非以存储类型`__block`为前缀，否则，块对象的词法范围内之局部变量只是作为只读变量传递给块对象。因此，要改变这种行为，我们可以对`scopeTest`方法的实现做些更改，即在`integerValue`变量前加上`__block`存储类型，如下所示：<br/>
<img src="/images/posts/2019-01-24/scopeTest_block.png">
现在，如果我们调用`scopeTest`方法会从控制台窗口看到结果，我们将看到：<br/>
<img src="/images/posts/2019-01-24/scopeTest_block_result.png">
关于如何将变量用于块对象，本节已经为你提供了足够的信息。我建议你动手写几个块对象，并在其中「操练」变量一番，包括赋值与读取，以更好地理解块对象如何使用变量。如果你忘了控制块对象中变量访问之规则，请随时返回本节。<br/>

## 1.4 Invoking Block Objects 调用块对象
我们已经在「Constructing Block Objects and Their Syntax」和「Variables and Their Scope in Block Objects」部分见识了调用块对象的实例。本节包含更具体的例子。如果你有一个独立的块对象，你可以像调用C函数一样简便地调用它：<br/>
<img src="/images/posts/2019-01-24/Invoking_Block_Objects_callSimpleBlock.png">
如果要调用另一个独立块对象中的独立块对象，请按照调用新块对象的相同说明即可，就像调用C方法一样：<br/>
<img src="/images/posts/2019-01-24/Invoking_Block_Objects_callTrimBlock.png">
在这个例子中，继续调用`callTrimBlock`Objective-C方法：<br/>
<img src="/images/posts/2019-01-24/self_callTrimBlock.png">
`callTrimBlock`方法会调用`trimWithOtherBlock`块对象，`trim WithOtherBlock`块对象去调用`trimString`块对象以达到修整给定字符串之目的。修整字符串是一件很容易的事，尽可以在一行代码中完成，但是这则示例代码显示了如何在块对象中调用块对象。<br/>

在「Chapter 2」,你将学习如何使用Grand Central Dispatch来同步或异步调用块对象，以释放块对象的真正力量。<br/>

## 1.5 Memory Management for Block Objects 块对象的内存管理
iOS应用程序运行在一个引用计数(reference count)的环境中。这意味着每个对象都有一个保留计数(retain count)以确保Objective-C运行时在它可能被使用的时间段内保留它，并在没人再用它时将其删除。你可以把保留数想象成小动物身上的「皮带数」(或「遛狗绳」)。只要还有一根绳，动物就会呆在原地。如果有两根绳，动物必须被释放两次才能被释放。只要所有的绳子一松开，动物就自由了。在前面的语句中，用object替换所有出现的动物，你就会明白引用计数环境是如何工作的。当我们在iOS中分配一个对象时，该对象的保留计数变为1。每次分配都必须与在对象上调用的「释放调用」配对，以将「释放计数」减1.如果你想在内存中保留对象，你必须确保已经保留了该对象，这样它的保留计数才会随着运行时而增加。<br/>

块对象也是对象，因此它们也可以被复制、保留和释放。编写iOS应用程序时，你可以简便地将块对象视为普通对象，并像处理其他对象一样保留和释放它们:<br/>
<img src="/images/posts/2019-01-24/Memory_Management_callTrimString.png">

在块对象上使用`Block_copy`来声明在你希望使用它的时间段内该块对象的所有权。在保留对块对象之所有权的同时，可以确保iOS不会处置该块对象及其内存。一旦用完此块对象后，必须使用`Block_release`以释放所有权。<br/>

如果你在Mac OS X应用程序中使用块对象，无论是在「垃圾收集」环境还是「引用计数」环境中编写应用程序，均应该遵循相同的规则。下面是来自iOS并且也是为Mac OS X编写的相同示例代码。可在项目启动或未启用「垃圾收集」之情况下编译它：<br/>

<img src="/images/posts/2019-01-24/Memory_Management_applicationDidFinishLaunching.png"> <br/>
在iOS中，您还可以使用「自动释放」块对象，如下所示:<br/>
<img src="/images/posts/2019-01-24/Memory_Management_autoreleaseTrimStringBlockObject.png"> <br/>
你也可以定义保存块对象副本的声明属性。这是我们对象的`.h`文件，它声明了块对象的属性`(nonatomic, copy)`：<br/>
<img src="/images/posts/2019-01-24/Memory_Management_GCDAppDelegate_h.png"> <br/>
**注**：这段代码是在一个简单的通用iOS应用程序的应用程序委托(delegate)中编写的。现在让我们继续实现我们应用程序的委托对象(delegate object):<br/>
<img src="/images/posts/2019-01-24/Memory_Management_GCDAppDelegate_m_0.png">
<img src="/images/posts/2019-01-24/Memory_Management_GCDAppDelegate_m_1.png">
在这个例子中，我们想要实现的是:首先，在我们的应用程序委托中声明`trimString`块对象的所有权;然后，使用该块对象从空白中修整出一个字符串。<br/>

**注**：声明`trimmingBlock`属性为`nonatomic`。此举意味着这个属性的线程安全性必须由我们来管理，并且我们应该确保该属性于同一时间内不会被多个线程同时访问。当然，我们现在也不必关心这个，因为我们没有用线程做任何花哨的事情。这个属性也被定义为`copy`，which tells the runtime to call the `copy` method on any object,including block objects, when we assign those objects to this property, as opposed to retaining those objects by calling the `retain` method on them.<br/>

正如我们之前看到的，`trimString`块对象接受一个字符串作为它的参数，修整这个字符串，并将其返回给调用者。在我们应用程序委托(application delegate)的`application:didFinishLaunch ingWithOptions:`实例方法内，我们只是简单地使用「点符号」语法将`trimString`块对象分配给`trimmingBlock`声明的属性。这意味着运行时将立即调用`trimString`块对象上的`Block_copy`，并将结果值赋值给`trimmingBlock`声明的属性。从此刻开始，直到我们释放块对象，我们在`trimmingBlock`声明属性中拥有它的副本。<br/>

现在我们可以使用`trimmingBlock`声明的属性来调用`trimString`块对象，如下代码所示：<br/>
<img src="/images/posts/2019-01-24/Memory_Management_trimmedString.png"> <br/>
完成后，在我们对象的`dealloc`实例方法中，通过调用其`release`方法来释放`trimmingBlock`声明的属性。<br/>

随着对块对象以及它们如何管理变量和内存之深入了解，终于到了进入「Chapter 2」了解被称为Grand Central Dispatch奇迹的时刻了。我们将在GCD中大量使用块对象，所以在进入下一章之前，请确保你已经真正理解了本章之内容。<br/>

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
