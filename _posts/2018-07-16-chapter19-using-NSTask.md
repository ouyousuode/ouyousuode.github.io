---
layout: post
title: 第19章  使用NSTask
---
{{page.title}}
=====================

在此部分，我们可以了解：
- 如何利用NSTask创建新进程
- 如何利用NSPipe和NSFileHandle发送数据给新进程的输入及取数据自此新进程的输出
- NSProcessInfo如何提供自身信息给程序 

<br/>
## 19.1  NSProcessInfo
你的应用可利用NSProcessInfo对象访问它自己的进程信息。这里是一些通常用到的方法：

``` Objective-C
+ (NSProcessInfo *)processInfo
// 可用这个类方法来得到当前进程的NSProcessInfo的共享实例。

- (NSDictionary *)environment
// 返回一个字典，包含所有环境变量的键值对。

- (NSString *)hostName
// 运行程序的主机名字。

- (NSString *)processName
// 程序的名字，被用于user defaults system。

- (NSString *)globallyUniqueString
// 此方法用主机名、进程ID以及时间戳来创建一个针对网络的独一无二的字符串。
// 它用一个计数器来确保每次调用均能产生一个不同的字符串。
```
<br/>
## 19.2  NSTask
NSTask对象用于创建并控制新进程。当进程结束时，此对象会发出一个NSTaskDidTerminateNotification通知。在创建(或启动)新进程前，你将用一些方法来设置新进程的属性。它们是：
``` Objective-C
- (void)setLaunchPath:(NSString *)path
// 设置当进程创建时要执行的代码路径。
- (void)setArguments:(NSArray *)arguments
// 采用一个包含字符串的数组作为程序的参数。
- (void)setEnvironment:(NSDictionary *)dict
// 可利用此方法设置环境变量。如未设置，则用父进程的。
- (void)setCurrentDirectoryPath:(NSString *)path
// 每个进程都有一个可从中解析所有相关路径的目录。它被称为当前目录。
// 如果未设置，父进程的当前目录就会被共用了。
- (void)setStandardInput:(id)input
//你可以提供一个对象(NSPipe或NSFileHandle)来充当通向新进程标准输入的导流管道。
- (void)setStandardOutput:(id)output
// 你也可以提供一个对象(NSPipe或NSFileHandle)来充当新进程标准输出流的导出管道。
- (void)setStandardError:(id)error
// 你还可以提供一个对象(NSPipe或NSFileHandle)来充当新进程标准错误流的连接管道。
```

一旦进程处于运行中，也有一些会用到的方法。下面几个是最常用的：
``` Objective-C
- (void)launch          // 创建新进程
- (void)terminate       // 通过向新进程发送SIGTERM信号以杀死它
- (int)processIdentifer // 返回新进程的PID
- (BOOL)isRunning       // 如果新进程正在运行，则返回YES
```
<br/>
## 19.3  NSFileHandle
当读取文件时，Cocoa程序员经常会读入一个完整的文件，并且在解析之前将其打包成一个NSData或NSString对象。当写文件时，Cocoa程序员通常创建一个可随后写入文件系统的完整的NSData或NSString对象。有时，你想要更多读写文件过程的控制权。比方说，当你读到想要的内容后随即关闭文件。为了读写过程的更多控制权，你可以利用NSFileHandle对象。

NSFileHandle对象用来读写文件。有些读方法是阻塞的(之所以阻塞，是因为应用要停下来并等待数据变得可用)，当然，其它方法则是非阻塞的。我们在此篇内容内讨论非阻塞方法。这有一些常用的关于读取、写入、查找的方法：
``` Objective-C
- (NSData *)readDataToEndOfFile
- (NSData *)readDataOfLength:(unsigned int)length
// 这两个方法从file handle中读数据

- (void)writeData:(NSData *)data
// 向file handle中写入数据
- (unsigned long long)offsetInFile
- (void)seekToFileOffset:(unsigned long long)offset
// 查找及改变在文件中的位置
- (void)closeFile
// 关闭文件
```
<br/>
## 19.4  NSPipe
类NSPipe拥有两个NSFileHandle实例，一个用于输入，另一个用于输出。

``` Objective-C 
- (NSFileHandle *)fileHandleForReading
- (NSFileHandle *)fileHandleForWriting
```
<br/>
## 19.5  创建一个创建新进程的App
Unix系统有一个称之为sort的应用程序，它读取标准输入的数据，对其排序，然后输出到标准输出。你将编写一个程序，它调用sort作为新进程，写数据到它的标准输入，从它的标准输出读数据。用户会在一个NSTextView内键入内容，点击按钮触发排序，在另一个NSTextView内看到结果。看起来就像下面这样：

<img src="/images/posts/2018-07-16/sortThem.jpg">
为准确起见，我们不会在实际应用中这样做排序。NSArray类有几个优雅的方式做排序。这只是一个应用其它进程的简单示例。以下为此程序nib文件的对象示意图：

<img src="/images/posts/2018-07-16/SortThem_nib_file.jpg">
创建一个Cocoa Application类型的新工程并将之命名为SortThem。编辑工程附带的ViewController.h文件，设置outlets和action。

<img src="/images/posts/2018-07-16/viewControllerHeader.jpg">
编辑Main.storyboard文件。拖拽两个NSTextView对象和一个NSButton到IB窗口。将右边text view的属性设置为non-editable:

<img src="/images/posts/2018-07-16/layingOutTheSortThemWindow.jpg">
在动手写代码前，看一眼如下的对象示意图。

<img src="/images/posts/2018-07-16/SortThem_object_diagram.png">
在Xcode中，在ViewController.m内添加-sort:方法。

<img src="/images/posts/2018-07-16/viewControllerImplementation.jpg">
构建并运行此应用。

## 19.6  Non-blocking读取
如果一个进程花费较长时间来返回输出，当等待此程序的输出时，你自然不想让你的程序停下来。为避免这种情况，你创建一个处理非阻塞读取的file handle。尤其是，你需要设置此file handle以便它能发送一个广播(当有要处理的数据时)。

在这部分，我们创建一个运行traceroute的task。traceroute发送packets来发现你的机器与其它主机间的路由器。来自路由器的响应有时会花一小会儿时间才返回。我们在后台读取此数据并把它附加到text view中。

创建的通知是一个NSFileHandleReadCompleteNotification。为启动等待数据的file handle，需给它发送-readInBackgroundAndNotify消息。每次收到此通知，你便利用NSFileHandle的-availableData方法读取数据。然后，再次调用-readInBackgroundAndNotify以重启对数据的等待。运行中的应用展示如下：

<img src="/images/posts/2018-07-16/traceRouteApplication.png">
### 19.6.1  创建头文件及编辑xib文件
创建类型为Cocoa App的新工程，将之命名为TraceRoute。编辑ViewConroller.h文件：
<img src="/images/posts/2018-07-16/viewControllerHeaderTraceRoute.jpg">
编辑Main.storyboard。拖拽一个NSTextView,一个NSTextField以及一个NSButton到故事板中。把他们与ViewController连接好，当然也要把button的action连接到startStop:。NSTask运行后的移动信息流为：

<img src="/images/posts/2018-07-16/trace_Route.jpg">
编辑实现代码，使之看起来像这样：

<img src="/images/posts/2018-07-16/viewControllerImplementationTraceRoute_00.jpg">
<img src="/images/posts/2018-07-16/viewControllerImplementationTraceRoute_01.jpg">
<img src="/images/posts/2018-07-16/viewControllerImplementationTraceRoute_02.jpg">




















