---
layout: post
title: 关于VMware安装FreeBSD后的若干问题
---
{{page.title}}
=======================

下载VMware Fusion Pro 11.5及FreeBSD 12.1-Release中Virtual Machine
Images类别之
[FreeBSD-12.1-RELEASE-amd64.vmdk.xz](https://download.freebsd.org/ftp/releases/VM-IMAGES/12.1-RELEASE/amd64/Latest/)
，案头备好参考资料：
[FreeBSD Handbook](https://www.freebsd.org/doc/en_US.ISO8859-1/books/handbook/),
本文档一直得以及时的更新，当前的版本已覆盖FreeBSD 12.1-Release和
FreeBSD 11.3-Release；Michael W.Lucas所著[Absolute FreeBSD : The
Complete Guide to FreeBSD](https://www.amazon.com/Absolute-FreeBSD-3rd-Complete-Guide/dp/1593278926/ref=sr_1_1?dchild=1&keywords=Absolute+FreeBSD&qid=1594090340&s=books&sr=1-1)(
于Amazon上购买的第3版尚未到，先拿第二版电子档抵挡一阵子)。

<img src="/images/posts/2020-04-20/FreeBSD_Handbook.png">
<img src="/images/posts/2020-04-20/Absolute_FreeBSD.png">

打开VMware Fusion->新建->创建自定虚拟机->其他(FreeBSD 12 64
位)->指定引导固件：传统BIOS->使用现有虚拟磁盘->选择已下载的vmdk文
件......->进入Welcome to FreeBSD安装界面->安装ing->进入以root身份登陆
的命令行界面

<img src="/images/posts/2020-04-20/FreeBSD_install_Welcome.png">
<img src="/images/posts/2020-04-20/FreeBSD_install_root.png">

界面的提示很清楚，随系统安装的文档在/usr/local/share/doc/freebsd/目录，
或者稍后以pkg install en-freebsd-doc命令安装。/usr/local/用于存放本地
执行文件、库文件等，同时也是FreeBSD ports的默认安装目录，关于目录结构
可见FreeBSD Handbook Chapter 3 FreeBSD Basic之
[Directory Structure](https://www.freebsd.org/doc/en_US.ISO8859-1/books/handbook/dirstructure.html)
，介绍得很详细！

Microsoft Windows和Red Hat Linux这样的操作系统倾向于把你可能需要的一切
都投入到基本安装中，FreeBSD与它们不同，它的基本安装是确确实实地最小化！特别是
Windows系统，你可以在它的主系统目录内看到成千上万个对象，涵盖之广到你
能够想象到的几乎所有共享库。当你启动系统时，Windows便将其中的许多库和
对象加载到主系统内存中。咱们也不清楚，这其中的对象都是用来干嘛的。但是，
对咱们而言，所有这些对象都是要消耗内存的。这就是Microsoft操作系统做事
的方法，给他你拥有的一切，然后你就不用管了。Red Hat Linux也包含类似数
量的东西，但是，至少来说，它不会在系统启动时自动将其加载进内存，用户可
以选择非常轻松地删掉它们。

而一个基本的FreeBSD安装包括刚刚好足够让系统运行的内容、外加一些传统地包含在
Unix系统中的位而已。你可以选择安装附加程序或者源代码。不过，即使是一个
完整的、运行的FreeBSD安装也比Windows或Red Hat Linux安装占用更少的磁盘
空间。这种稀疏性的优点是它只包括必要的系统组件。如此一来，调试变得简单
多了；而且，如果你从未听说过、也没用到过某共享库，便可清晰地确认，它不
会为你的问题负责。当然，这样的安装也有缺点，就是你必须决定你需要什么，
并且安装程序来支持你需要的功能。FreeBSD同时拥有FreeBSD Ports
Collection和package两种安装第三方软件的方式，前者用于从源代码安装，后
者则直接从预编译的二进制版本安装。

据FreeBSD Handbook中21.4. FreeBSD as a Guest on VMware Fusion for Mac
OS®介绍，在将FreeBSD安装到Mac OS®的VMware Fusion之后，还需要进行一系列
的配置，以便为系统的虚拟化操作进行优化。最重要的一步是通过调低kern.hz
变量来降低VMware Fusion环境中的FreeBSD对CPU的使用。此项配置可以通过在
*/boot/loader.conf*中添加*kern.hz=100*来完成。如果不使用这个配置，闲置的
FreeBSD VMware Fusion客户OS会在单核处理器的iMac®上使用大约15%的CPU。不
过，这样修改之后，空闲时的使用量就减少到大约5%了。

既然官方指南了，那就动手吧。*cd /boot/*目录，*vim loader.conf*，提示“vim
command not found”；再试试 *emacs loader.conf*,提示如故！这便是FreeBSD基
本安装的“副作用”了。当然，针对此编辑操作，FreeBSD本身倒是也带着许多有
强大功能的文本编辑器，比如*vi*。但是，还是想从*Vim*和**Emacs**中挑选一款作为工
作中的主力编辑器。思虑再三，选**Emacs**吧!为什么这么选呢？

记得读过The New Yoker上讲Jeff Dean和Sanjay Ghemawat两位Google工程师结
对编程故事的文章，The Friendship That Made Google Huge : Coding
togther at the same computer,Jeff Dean and Sanjay Ghemawat changed the
course of the company - and the Internet。其中一段描述了他俩在咖啡时间
结束后回到自己的电脑前开始工作的情景。After cappuccinos,they walked to
their computers.Jeff rolled a chair from his own desk,which was
messy,to Sanjay's,which was spotless.He rested a foot on a filling
cabinet,leaning back,while Sanjay surveyed the screen in front of
them.There were four windows open: on the left,a Web browser and a
terminal,for running analysis tools;on the right,two documents in the
text editor **Emacs**,one a combination to-do list and notebook,the
other filled with colorful *code*.

当然，这段只是提到两位大神工作中使用Emacs作为编辑器。而*Concurrency in
Go*的作者*Katherine Cox-Buday*在提到使用何种软件时，则详述了选择Emacs的理
由：It wouldn't make sense to start out with anything other than
Emacs.I don't think there has been a piece of software which has had a
larger impact on my life.I began using this about fifteen years
ago,and it has followed me across operating systems,jobs,roles(I used
to manage my teams),languages,and needs.Every time I start something
new,Emacs has been there to make it just a little easier,and the more
I do in it,the easier everything gets.I believe this power comes from
Emacs being the closet thing we have to a working **Lisp Machine**.If
you know a little *Emacs Lisp*,you can begin down the path of creating
your own perfect tool which will grow with you,for life.

Sitting on top of Emacs is *Org Mode*.It is the thing which made
irrelevant my search for the perfect task management software.Like
Emacs,you can mold it into whatever workflow works best for you at the
time.Later,I discovered it is also a wonderful publishing platform.技
术上说，它是一款我们最接近Lisp Machine的软件。情感上讲，它好处多多，可
伴我们成长。因此，*pkg install emacs*，但是问题来了：

<img src="/images/posts/2020-04-20/pkg_install_emacs_fails.png">

问题的提示很直接：write failed,filesystem is
full.pkg : archive_read_extract(extract error) : No space left on
device.既然文件系统已经满了，总得看看详细情形吧。利用*df -h*详细列出文
件系统的可用磁盘空间量之统计信息，

<img src="/images/posts/2020-04-20/pkg_install_emacs_fails_df.png">

rootfs容量为106%，超过100%了，所以问题出现了！不过，好奇怪的百分比呀。
话又说回来，从安装系统到现在，咱一直都规规矩矩的执行基本安装，还是出现
了这莫名其妙的错误；说明，这是系统安装的必经之路。搜索之后，看到
FreeBSD Forums出现过相同的问题，
[My hard disk has 109% of capacity ?](https://forums.freebsd.org/threads/my-hard-disk-has-109-of-capacity.68366/)
。看完解答后，发现是正常情况。而且，答案就在官网提供的文档
[Frequently Asked Questions for FreeBSD 11.x and 12.x](https://www.freebsd.org/doc/en/books/faq/disks.html#idp59477704)
，索性暂不去管它了，

<img src="/images/posts/2020-04-20/capacity_is_more_than_100_percentage.png">

下一个关注点就是磁盘空间使用情况，自FreeBSD官网下载的FreeBSD 12 64位
vmdk文件安装的虚拟机，其硬盘容量默认为4G。现在看，确实有点儿小家子气了。
所以，现在要做的是，关机(虚拟机)，而后更改其硬盘容量，

<img src="/images/posts/2020-04-20/FreeBSD-12.1-amd64_modify_SCSI.png">

将硬盘容量更改为10G之后，利用*gpart show*展示其磁盘空间情形

<img src="/images/posts/2020-04-20/gpart_show.png">

能观察到，有一个区域- free - (6.0G)。当前我有三个分区，其一为
freebsd-boot，其二为freebsd-swap，最后为freebsd-ufs。现在，我想把设置
到free区利用上，怎么办呢？还是利用*gpart*工具，执行*gpart resize -i 3 da0*,

<img src="/images/posts/2020-04-20/gpart_resize.png">

其中，freebsd-ufs分区的容量已增长为(3.0 + 6.0) G。但是，*df -h*后看到
的rootfs之容量并未发生什么变化！为何呀？？？因为前者只是对物理空间进行
了更改，现在还得对逻辑空间做修改，利用*growfs /dev/gpt/rootfs*命令对
root文件系统进行扩容，过程中会提示"make a backup"，输入yes后，工作就完
美地结束了。再次，利用*df -h*查看，

<img src="/images/posts/2020-04-20/growfs_rootfs.png">

结果显而易见，rootfs的容量变为35%，Avail值也变为5.2G了。其实，此扩容过
程可参见FreeBSD Handbook Chapter 17.Storage之17.3. Resizing and Growing
Disks一节，讲解得相当细致！另外，关于文件系统与硬盘的概念，可以通读一
遍Absolute FreeBSD之Chapter 8 Disks and Filesystems一节，计算机类的英
文技术资料本身的文辞比较简单直白，以释意为准，语法翻来覆去也就那几个，
更别提什么艰深的隐喻了。所以，直接读英文原版吧！

<img src="/images/posts/2020-04-20/Resizing_and_Growing_Disks.png">
<img src="/images/posts/2020-04-20/gpart.png">
<img src="/images/posts/2020-04-20/gpart_resize_intro.png">

在使用FreeBSD之前，非常应当花一些时间研究研究FreeBSD官网提供的文
档，了解了它是如何工作的，再上手操作，可收事半功倍之功效！想想，如果有
尝试FreeBSD的冲动，也尝试安装了，却卡在了" :write failed, filesystem
is full "的错误上，相当于"第一次作为全军的统帅，指挥全局，在这么大一张
饼面前，头一口就咬崩了门牙，说不过去呀"！好了，现在，重新执行*pkg
install emacs*命令，可成功安装。执行*emacs -version*查看其软件版本；执
行*whereis emacs*验证其存放目录；执行*emacs*进入GNU Emacs，

<img src="/images/posts/2020-04-20/emacs.png">
<img src="/images/posts/2020-04-20/GNU_Emacs_26.3.png">

可以安心将FreeBSD作为除iOS/macOS App开发之外的主力开发环境了，还需要略
加配置。对Emacs的熟练使用并尝试写扩展，自不用说，这方面可以边阅读*An
Introduction to Programming in Emacs Lisp* by Robert J.Chasell掌握
Emacs Lisp语法，边参考参考*Writing GNU Emacs Extensions* by Bob
Glickstein中的实例，先看懂再修改，假以时日，必有所成；不满意当前的
shell环境的话，可以换[fish shell](https://fishshell.com)耍一耍......<还需继续配置系统的其它方面，
未完待续>

<img src="/images/posts/2020-04-20/fish.png">
<img src="/images/posts/2020-04-20/pkg_install_fish.png">
