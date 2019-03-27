---
layout: post
title: C语言的可变参数列表
---
{{page.title}}
=========================

对采用可变参数数目的函数而言，variadic function是一个精巧的名字。他们提供一个灵活且能量大的编程接口。此变长参数函数从属于printf函数族。stdarg帮助文档包含使用可变参数列表的完整细节。

为在你自己的函数中处理可变参数，首先声明一个va_list类型的变量，它行为似指向参数值的一个指针。利用va_start()万层内初始化giving it the name of the last declared function argument。现在，va_list指向追加参数中的第一个。

为得到实际参数值，你可调用va_arg()，当然需随附上期望的参数数据类型。它将返回一个你指定类型的正确数字，并推进va_list指向下一个参数。持续调用va_arg()，直至处理完所有参数。调用va_end()以清理任意的内部状态。下面这例函数，把传给它的所有整数加起来，并用0作为哨兵值以结束处理过程。

<img src="/images/posts/2019-02-23/vararg.png">
<img src="/images/posts/2019-02-23/resultOfVararg.png">
当你调用va_start()时，其内部指针初始化为指向提供参数的调用栈底部。你每次调用va_arg()，它返回构成所提供类型的数据量并且推进指针到此数据的末尾；比如，提供的类型为int，那就返回4个字节的数据量。va_arg()利用提供的数据类型来决定推进指针的距离。

<img src="/images/posts/2019-02-23/addmeUp.jpg">
听起来危险的是：如果实际参数类型大小与期望值不同，如果参数个数比你期望的少，va_arg()如何清楚何时停止呢？简直是束手无策。自动给参数末尾植入一个标志位不算什么特高明的手法。你的代码需要知道何时停下。为实现此目标，可利用某种像传递给printf()的字符串。可使用像zero或NULL这样的一个哨兵值。[NSArray arrayWithObjects:]利用nil(一个零指针值)来示意对象列表的末尾。

尽管这帮助解决了参数个数问题，但对参数类型错误问题无能为力。正如你所想，这是运行时错误的主要根源。如果你向printf()提供一个坏的格式字符串抑或未包含终止哨兵值，处理调用栈的函数可能游走进任意数据区，进而导致应用崩溃或数据污染。

为了更安全地调用变长参数函数，gcc允许你用__attribute__(sentinel())来标记一个函数或方法声明。届时，当你的gcc命令行包含-Wformat时，若你fail to 终止参数列表 with a zero pointer value like NULL or nil，gcc会发出一个警告。此标志也引发gcc对printf()坏调用的警告。Cocoa程序员可以用NS_REQUIRES_NIL_TERMINATION符号而非一些令人迷惑的属性语法。下面这个函数展示了用NULL作为哨兵值的字符串打印功能：

<img src="/images/posts/2019-02-23/sentinel.png">
<img src="/images/posts/2019-02-23/warningOfSentinel.png">
程序在64位Intel机器上可成功打印出第一个字符串，却会崩溃于对第二个printStrings()的调用。当运行于32位机器时，如所示：
``` Objective-C
gcc -arch i386 -g -Wall -o sentinel sentinel.m
./sentinel
```
<img src="/images/posts/2019-02-23/resultOfSentinel.png">

此情况下，第二个printStrings()使用了第一个调用留在栈上的旧有数据。gcc也提供一个可用于函数的__attribute__((format_arg))标记。

一些变长参数函数族会使添加一个额外值变得较容易。你可能想要printf()的另一个版本，它采用一个调试级别参数且仅当此级别超过某个全局设定值，才打印文本。为达成此要求，你可以写一个函数来接受调试级别、格式化字符串以及参数。你可随即查验该调试级别值，如果它在正确范围内，即调用vprintf()。vprintf()是采用va_list作参数的一个printf()版本。一个开头或结尾的**v**经常用于表示将va_list作为参数的函数或方法名，比如vsnprintf()或NSLogv()。下例展现了一种条件输出log的方式：

<img src="/images/posts/2019-02-23/debugLog.png">
<img src="/images/posts/2019-02-23/resultOfDebugLog.png">
像使用C语言那样，可利用Objective-C语言创建变长参数方法，所以没有需要学习的新语法。示例就有一个SomeClass对象，此对象有一个神奇的可打印任意数量对象描述的小方法：

<img src="/images/posts/2019-02-23/describeObjects.png">
<img src="/images/posts/2019-02-23/resultOfDescribeObjects.png">
也有一些其它的Foundation方法，它们也像vprintf()那样接受**va_list**作参数，比方说，**NSString**的**-initWithFormat:arguments:**以及**NSPredicate**的**+predicateWithFormat:arguments:**。

**QuietLog()**是一个像**NSLog()**的函数，除了它不会预置比如进程ID和当前时间的额外信息。其结果极大地减少了输出的数据量。将此实现变得有趣的是我们不能使用vprintf()，因为vprintf()不理解**Cocoa**的%@转换符号(用于打印对象的描述)。像在下例描述的那样，一个临时的**NSString**被创建、被打印。

<img src="/images/posts/2019-02-23/quietLog.png">
<img src="/images/posts/2019-02-23/resultOfQuietLog.png">
