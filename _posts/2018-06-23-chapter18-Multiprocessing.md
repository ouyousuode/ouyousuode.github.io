---
layout: post
title: 第18章  多进程
---
{{page.title}}
====================

所有的现代操作系统都是多进程的，这意味着多个独立的应用可同时运行并且共享系统的资源。遍布于可运行程序间的操作系统时间片，将可用的CPU时间划分给这些可运行程序。
## 18.1  进程调度
调度器是操作系统的一部分，它标出下一步哪个进程应获取使用CPU的权限。它经常参考一些信息来做这个决定，这些信息包括进程优先级、该进程以前获得的CPU时间量以及它是否刚刚完成一个I/O操作。

每个进程都有一个影响优先级的相关美好程度值。此值被指定为-20到20间的一个整数。默认情况下，进程有一个为0的美好程度值。美好程度值越大，进程的优先级越低；越美好的进程越不太需要多少CPU时间。

你可以启动一个利用nice命令指定美好值的进程。可使用renice命令修改一个已运行进程的美好值。你可以给自己的进程降优先级，但若想提升优先级，除非你拥有超级用户权限。

可以将调度器认为是拥有很多带优先级进程的一个列表。这些不被阻塞的进程以优先级的顺序放入一个运行队列(run loop)中。当要运行一个新进程时，调度器从队列头部取出一个进程，使之运行，直到因某些原因被阻塞或者时间片过期了，再将它放回到队列中。

<img src="/images/posts/2018-06-23/scheduler_Small.jpg">
你可利用uptime命令来检查run queue的平均长度。

<img src="/images/posts/2018-06-23/uptime.png">
macOS的load average只是报告run queue的深度。它并不包括阻塞在硬盘I/O上的时间，其它某些Unix类系统(包括Linux)会报告此时间。
## 18.2  一些便利的函数
开启新进程是编程库中一件强劲的工具。Unix附带有很多轻型命令行工具，它们可以执行有用的功能。有时，充分利用这些工具比重新实现它们相同的功能更有意义。举例来说，你可能有一个移除HTML内标签的Perl脚本文件。与利用Objective-C实现正则表达式代码相比较，启动一个Perl脚本再喂给它Html文件，容易得多。

启动其它程序的最简单方式是使用system()函数。它启动程序，等它结束，并随后返回结果代码(result code)。实际上，system()传递控制权给了一个新shell，/bin/sh，因此你可以使用重定向这样的shell功能。
``` Objective-C
int system(const char *string);
```
从system()得到的返回值即从启动的程序返回的结果值。否则，如果启动shell前有错误发生，我们会得到-1并且errno也会被设置。一个127的返回值意味着shell失败了(因某些原因)，可能因为命令字符串中的坏语法。system()以-c参数调用shell。如果你得到了一个错误并且想从Terminal试验你的命令行，可以直接在shell进行操作。如果你确实想向你启动的程序进行读写，那可以用popen()启动程序并打开一个到它的pipe。r然后用pclose关闭此连接：
``` Objective-C
FILE *popen(const char *command,const char *type);
int pclose(FILE *stream);
```
像system()一样，popen()调用一个shell来运行命令。下面是一个popen()开cal程序以获得当前日历的一个小程序。一切为了好玩的是，从cal的输出遭到了来自rev的行反转。

<img src="/images/posts/2018-06-23/pcalCode_Small.png">
./pcal 运行后，

<img src="/images/posts/2018-06-23/resultOfPcal_Small.png">
popen手册指出它用了一个双向pipe，所以你可以指定一个读写模式(r+)以替代仅仅的一个r或者w。不幸的是，在实践中使用返回的stream既读又写可能无效果，因为它需要被popen的进程的合作。Use of a bidirectional stream can easily run afoul of the buffering done under the hood by the stream I/O functions.它也会引起死锁的诡异现象：your process could block writing data to the child process while it blocks wirting already-processed data back.进程既不会得到排干满缓冲器的机会，也不能不阻塞其它操作。如果你需要对子进程既读又写，那么你最好的选择是，要么重定向输出到一个文件，随后处理之；要么自己创建一个子进程，用两个pipe，一个用来读，另一个用来写。
## 18.3  fork
在Unix中创建一个新进程，你必须先制作当前进程的一份拷贝。此拷贝可继续执行原进程的代码，或者可开始执行其它程序。系统调用fork()执行拷贝的过程：
``` Objective-C
pid_t fork(void);
```
fork()制作一份当前运行进程的拷贝，并且它是很少的可返回两次的函数群中的一员。在称为父进程的的原进程中，fork()返回新进程的进程ID。在称为子进程的新进程中，fork()返回0。当发生错误时，没有生成的子进程，fork()在父进程中返回-1并且设置errno。

作为父进程的一份拷贝，子进程继承了父进程的内存、打开的文件、真实有效的用户及组ID、当前的工作目录、信号掩码、文件模式创建掩码、环境、资源限制以及任意附带的共享内存段。此拷贝行为可被展示为：

<img src="/images/posts/2018-06-23/fork_Small.jpg">
拷贝这许多数据听起来较费时，但是macOS采用快捷方式。它在父子进程间共享原始拷贝，延迟进行备份到非复制不可而非立即复制父进程的内存空间。此过程利用的是一种称为copy on write的技术，也叫COW。

<img src="/images/posts/2018-06-23/copyOnWrite.png">
所有与父进程相关的物理页标记为只读。当一个进程试图向“复制的”内存的一个页进行写入时，此页先被拷贝出来以专用，然后每份拷贝都变成分离状态，就拥有了进程的可读写属性。做出这修改的进程依旧做着它想要的改变，而其它进程对此一无所知。这种技术极大地减少了操作系统在fork()上的工作量。

前面提到的system()和open()这样的方便函数最终都要去调用fork(),但是，大多数情况下，与对fork()的恰当调用相比，它们使用更多资源，其原因在于它们需要调用一个shell来运行新进程。我们可以写fork一个子进程的极简程序：

<img src="/images/posts/2018-06-23/fork.png">
运行后，可看到：

<img src="/images/posts/2018-06-23/resultOfFork.png">
像Unix地面儿上的所有情况一样，这也有一些容易忽略的知识点。第一个就涉及到父子进程间的竞争条件：你无法保证谁会先运行。在子进程被调度前，你无法信赖父进程中运行于fork()之后的代码。

其它的知识点与打开的文件如何在两个进程间共享有关。父子进程在内核中共享相同的文件表入口，如下所示的那样。这就意味着打开文件的所有属性都是共享的，比如当前偏移量。这通常是一种被期望的行为——当你想让父子进程打印到相同的标准输出时，每当打印，每个进程会增长在文件的偏移量，因此它们可以避免相互改写。However,it can also be confusing when your file offsets move from underneath you and you were not expecting them to。与文件表事务相关的是the state of the buffers for buffered I/O。The buffered I/O buffers in the parent's 地址空间 get duplicated across the fork。如果fork前缓冲期中有数据，父子进程可以打印两次缓冲期中的数据，这可能不是我们想要的结果。

<img src="/images/posts/2018-06-23/filesAfterFork_Small.jpg">
注意上述极简程序中前有下划线的exit()方法。它行为如exit(),关闭文件描述符，做些一般性的清理工作，但是它不会冲刷文件流缓冲期，因此你不必担心它会为你冲刷带有复制数据的缓冲器。在此程序实例中，printf()中的新行(newline)为我们冲刷缓冲器。

也请注意在退出前父进程小睡了一会儿。当子进程记录信息时，这提高了它仍未子进程的父亲角色的可能性。在子进程写信息前，如果父进程退出了，那此子进程的父亲将为进程1。删除sleep()行，重新编译运行后，你可以观察到这种行为。由于输出依赖竞争条件，所以有可能运行几次后才能看到这种情况。
## 18.4  父子进程的生命周期
由于fork()的天性，每个进程都确有一个父进程，然而一个父进程可以有多个子进程(每个儿子都有父亲，然而父亲却可能有多个儿子)。Unix更是强化了这一规则：尽管任一进程可能有或没有子进程，每个进程都有一个父进程。在子进程退出前，如果其父进程已然退出，那么此子进程就被进程1收养了。在大部分Unix系统下，此为init进程；在macOS下，则是多面手的launchd。

当子进程退出时，这并非是它生命的尽头。在一个进程终止后，操作系统仍将保留与它相关的一些信息，比如退出码以及系统资源使用量统计。只有在这些信息都传递给了此子进程的父亲后，此子进程才被允许死亡。对这些身后信息的索取称为收割子进程。你可利用一个wait()系统调用来收割进程。
``` Objective-C
pid_t wait(int *status);
pid_t waitpid(pid_t wpid,int *status,int options);
pid_t wait3(int *status,int options,struct rusage *rusage);
pid_t wait4(pid_t wpid,int *status,int options,struct rusage *rusage);
```
调用这些wait函数中的任一个均可收集来自子进程的结果码。wait()会阻塞至有子进程等待被收割。如果多个子进程都在等着被收割，wait()会返回一个任意值。如果你想等一个指定的子进程，那就用waitpid()。waitpid()，wait3()和wait4()都用到的options是：
- WNOHANG  不要阻塞着等待子进程。如果没有退出的子进程，立即返回。
- WUNTRACED 报告子进程身上的任务控制动作(像被停止或者后台运行)。

wait3()像wait(),wait4()似waitpid()。前者等待任意的一个子进程，后者等待一个特定的子进程。wait3()和wait4()也会填充一个数据结构，它描述了被子进程耗费的资源。你可以在/usr/include/sys/resource.h中找到完整的struct rusage。这个结构体内比较有趣的元素是：
``` Objective-C
struct rusage {
	struct timeval ru_utime;   // user time used
	struct timeval ru_stime;   // system time used
	long	      ru_maxrss;   // max resident set size
}
```
这些告诉你子进程耗费了多少CPU时间以及内存使用的“水位线”。从子进程得到的返回值被编码在所有wait函数返回的状态结果中。用这些宏可以取出你感兴趣的项目：
- WIFEXITED()	 如果进程正常终止于对exit()的调用，则返回true。
- WEXITSTATUAS() 如果WIFEXITED(status)结果为真，返回子进程传递给exit()的参数中的low-order字节。自main()处的返回值作为exit()的参数。
- WIFSIGNALED()  如果进程由于收到了信号而终止，则返回true。
- WTERMSIG       如果WIFSIGNALED(status)为真，则返回引起进程终止的信号数字。
- WCOREDUMP      如果WIFSIGNALED(status)为真，并且如果进程终止伴随着core dump的产生，则返回true。
- WIFSTOPPED     如果进程未被终止，只是被停止，则返回true，比如在shell中的任务控制(像，Control-Z会挂起进程)。
- WSTOPSIG       如果WIFSTOPPED(status)为真，则返回引发进程停止的信号数字。

接着展示这些运行中的宏：

<img src="/images/posts/2018-06-23/codeOfStatus_0.png">
<img src="/images/posts/2018-06-23/codeOfStatus_1.png">
./status 运行后，结果为：

<img src="/images/posts/2018-06-23/resultOfStatus.png">
在子进程的exit()和父进程的wait()中间，内核需要在某处存储子进程的状态和资源信息。它要立即处理子进程的大部分资源(内存、文件等等)，但是保留它的进程表入口，此包含需要提供给副进程的信息。进程亡了，但它的进程表入口仍继续存在：如此未死透的子进程以僵尸进程著称，不要与Objective-C的NSZombieEnabled内存调试功能混为一谈。Zombies(僵尸)显示在ps命令输出信息的括号内：

<img src="/images/posts/2018-06-23/ps.png">
偶尔的僵尸进程是不足为虑的。当它的父进程退出后，子进程重认进程1为父进程，此进程负责收割它。只有一种糟心的情形下，僵尸变得危险起来：如果一个程序持续地制造僵尸且不退出，那它就会填满整个进程表进而使机器变得无用。更可能的状况是，它会耗尽某个用户的进程限制，使此进程用户的帐户变得无用而并不影响他人。

你怎知何时去调用wait()呢？当子进程退出后，系统发送一个SIGCHLD信号给它的父进程。此信号被默认忽略，但是，你可以设置一个handler并利用它设置一个子进程需要被等的标志。需要注意的是，父进程无法计数收到的每个SIGCHLD信号。因为当进程正在内核的run queue排队时，若干个子进程可能已然退出了。

像在idle进程期间，你可能像继续忽略SIGCHLD信号并周期性地替代wait()。或者，你可用kqueue来监测进程的终止和SIGCHLD信号。通过传给ps的o选项ppid关键字，你可以看到父进程的进程ID：

<img src="/images/posts/2018-06-23/ppid.png">
在这，你能看到一些将/sbin/launched/作为父进程的守护进程。也有一个登录进程(父进程pid 397是Terminal.app)，还有其它像shell和emacs这样的程序。
## 18.5  exec
fork后的大多数情况是，你只是想运行其它程序。exec()函数族用一个新进程替换当前正在运行的进程。你经常听到别人一起说fork()和exec()，由于它们俩很少分开使用。

这里有几个exec()的变种，它们随具体情形而定，例如，你如何指定要运行的文件，如何指定程序参数，以及如何指定新程序的环境变量。

<img src="/images/posts/2018-06-23/execVariants_Small.jpg">
如何解读这些名称:
- p  如果给定文件名包含斜杠，则它被用作路径。否则，此调用使用PATH环境变量来做shell式的程序查找。
- P  像p一样，但是使用提供的"/sbin:/bin:/usr/sbin:/usr/bin"这样的字符串而非PATH环境变量。
- v  程序参数是一个包含字符串的数组。
- l  程序参数是一个list of separate arguments in the exec() command。
- e  环境变量是“VARIABLE=value”形式的字符串数组。
- No e 环境变量通过environ变量继承自父进程。

如果你不用exec(),全局变量environ会被用来构建新进程的环境。execve()函数是其它版本函数基于的实际系统调用，可说是exec的根本系统调用。你被期待着传递可执行文件名字或路径作为第一个参数。参数列表，参数向量以及环境向量都必须以NULL指针作结尾：
``` Objective-C
char *envp[] = {"PATH=/usr/bin","EDITOR=/usr/bin/vim",NULL};
char *argv[] = {"/usr/bin/true",NULL};
```
经过exec()调用，一些属性就被子进程继承下来了，包括：
- 打开的文件
- 进程ID，父进程的进程ID,进程组ID
- 访问的群组，控制终端，资源使用量
- 当前工作目录
- 信号掩码

<br/>
## 18.6  Pipes
打开的文件经exec()被继承下来，除非你明确告诉fd关掉exec！此行为形成了构建程序间管道的基础。在fork()前，进程调用pipe()来创建一个通信管道：
``` Objective-C
int pipe(int fildes[2]);
```
pipe()用两个连接在一起的文件描述符(fd)来填充files数组，写入fildes[1]的数据可从fildes[0]读出来。如果你想fork()及exec()一个命令，并且读取此命令的输出，那可以作以下步骤：
- 创建pipe
- fork()
- 子进程使用dup2()把filedes[1]转向到标准输出
- 子进程exec()一段程序
- 父进程从filedes[0]读取此程序的输出。在子进程退出后，它的文件描述符被关闭，一旦缓存的数据被排干了，从filedes[0]读取就能返回文件的结尾。
- 最后，父进程wait()子进程终止以收割之。

<img src="/images/posts/2018-06-23/pipeAndFork.png">
可使用多个pipe把多个程序的输入和输出链接到一起。根据此原理，我们可以编写一个等同于如下功能的程序：
grep -i mail /usr/share/dict/words | tr '[:lower:]' '[:upper:]'。
什么意思呢？是将从/usr/share/dict/words获得的包含mail的单词转换成大写形式。

<img src="/images/posts/2018-06-23/pipeLine_0.png">
<img src="/images/posts/2018-06-23/pipeLine_1.png">
<img src="/images/posts/2018-06-23/pipeLine_2.png">
运行./pipeline，结果为：

<img src="/images/posts/2018-06-23/resultOfPipeLine.png">
## 18.7  fork() Gotchas
尽管Unix文件描述符可经由fork()操作来继承，大部分Mach端口却并非如此。Cocoa依赖Mach端口来完成它的大部分专有进程间通信，包括与窗口服务器(window server)。如果你尝试在fork()后使用Cocoa，便一语成谶(出现问题)了。

当使用线程时，只有调用fork()的线程才能运行于子进程中。如果你在使用线程(或框架，也或者库文件在以你的名义使用线程)，fork()后唯一可安全调用的函数是exec()函数 和 其它任意异步信号安全的函数(可在signal handler中调用的那些)。在Mac OS X 10.6及以后的版本中，如果你使用run loops，它们会替你启动Grand Cental Dispatch的工作队列线程。Foundation URL加载系统使用线程，很多其它Apple框架本质上也在使用线程。

简而言之：如果你用多进程作为多线程的备选方案以继续目前的程序，你理应深入观察使用多线程或者其它更现代的并发方案。除非你在写一个单线程、BSD(Berkeley Software Distribution)式的程序且从未接触过任意Apple框架或Mach函数，你才fork()后再exec()。

## 18.8  总结
macOS是一个多进程操作系统。独立的进程都被分配CPU时间片，以造成系统上所有程序同在时运行的假象。

有时，你的程序需要创建新进程来完成工作。你可使用像system()或popen()这样方便的函数在shell上运行pipeline。也可以用fork()、exec()直接创建一个子进程，然后在子进程中执行一段新程序。你仍然可以使用pipe()来建立一条关联进程间的通信管道。

最后，摘抄一段王小波于《沉默的大多数》中的一段话：
```
根据我的经验，人在年轻时，最头疼的一件事就是决定自己这一生要做什么。在这方面，
我倒是没有什么具体的建议：干什么都可以，但最好不要写小说，这是和我抢饭碗。
当然，假如你执意要写，我也没有理由反对。总而言之，干什么都是好的；但要干出个
样子来，这才是人的价值和尊严所在。人在工作时，不单用到手、腿、腰，还要用脑子
和自己的心胸。我总觉得国人对这后一方面不够重视，这样就会把工作看成是受罪。
失掉了快乐最主要的源泉，对生活的态度也会因之变得灰暗......
```
