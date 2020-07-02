---
layout: post
title: FreeBSD是一款令人称道的操作系统
---
{{page.title}}
===========================
这是一篇关于我在FreeBSD上的一些探险经历以及为什么我认为它是一款令人称道之操作系统的小文章。

**更新于2020-01-21:**自从我写了这篇文章，它便在[Hacker News](https://news.ycombinator.com/item?id=22102372)、[Reddit](https://old.reddit.com/r/freebsd/comments/er5wu0/freebsd_is_an_amazing_operating_system/)以及[Lobster](https://lobste.rs/s/jedqwr/freebsd_is_amazing_operating_system)上发表了，一些人给我发来了电子邮件并附上了评论。我则用我认为合适的评论(之内容)更新了这篇文章。作为补充，我想说明我并非FreeBSD开发者，在FreeBSD的世界里可能有一些我完全不知情的事情。我也没有位列在FreeBSD开发者邮件列表上。我不是FreeBSD狂热粉！主要因硬件不兼容(要么缺少驱动程序,要么驱动程序有问题)，在过去的二十年间，我使用GNU/Linux的次数比FreeBSD多得多，我像喜欢FreeBSD一样喜欢着Debian GNU/Linux和Arch Linux。然而，最近GNU/Linux领域的当前发展令我担心。另外，这篇文章也不是关于我试图使任何人从其它系统转向FreeBSD，它只是讲述我因何喜欢FreeBSD，并且如何你喜欢摆弄操作系统，我建议你也尝试一番！

我想那一年是1999年末或2000年年中，有一天我在最喜欢的书店翻看电脑书籍，发现了Greg Lehey于1999年出版的[The Complete FreeBSD](https://openlibrary.org/books/OL8732144M/The_Complete_FreeBSD)第三版。随书还附带着4张FreeBSD 3.3光盘。

我在1998年就已经熟悉了GNU/Linux，并且我正在将家里和公司的所有服务器和桌面操作系统从Microsoft Windows迁移到GNU/Linux；最初是Red Hat Linux，后来是Debian GNU/Linux，它最终成为我多年来最喜欢的GNU/Linux发行版。

当首次看到Greg Lehey所著The Complete FreeBSD一书时，我记得注意到了首页上的文字，上面写着“The Free Version of Berkeley Unix”和“坚如磐石的稳定性”，我立刻就被吸引住了！那是怎么一回事儿呢？一款免费的Unix操作系统！并且如磐石般的稳定性？这一切听起来棒极了！

<img src=“/images/posts/2020-03-10/the-complete-freebsd.jpg”>
我当即买下了这本书，并且于很长一段时间里，它成了我最喜欢的阅读材料(尽管我未做任何与Unix相关的事情)。

我感到惊讶的是，我以前从未听闻过FreeBSD，因为它从1993年就已经存在了，但是因为我对GNU/Linux的经验，至少另一款“类Unix”操作系统于我而言不会陌生如“路人”，而且也确实不陌生！

我在不同的硬件上做了一些测试安装，(结果)我立刻喜欢上了FreeBSD。FreeBSD成为了我第一个在家运行的FTP服务器。

2000年的晚些时候，我受雇于我国最大的ISP(互联网服务提供商)之一，(并且)令我惊讶的是，我发现整个服务器和网络结构都是在FreeBSD上运行的。唯一没有运行FreeBSD的机器是销售人员和秘书工作的办公室内的计算机，它们运行着Microsoft Windows。当我问及对操作系统的选择时，系统管理员说了这样的话“明白自己在做什么的人运行FreeBSD！通信行业的每个人都在运行FreeBSD!”

我最终亲身体验了Grey Lehey书里提到的FreeBSD之“坚如磐石的稳定性”。FreeBSD太棒了！它非常出色，并且极其稳定。每一个在ISP(互联网服务提供商)托管的客户，也即大量客户，都由FreeBSD提供服务；它(FreeBSD)运行在从旧的386电脑到最新的奔腾4机器的所有设备上！FreeBSD在基础系统中升级之时，是它需要重启的唯一时刻。在此处工作的那段时间里，在FreeBSD运行的任何设备处，我都没有遇到过问题！

与FreeBSD相反，GNU/Linux被视作一款“玩具”型操作系统。它只被一些支持人员用于它们的私人设置。

当时我没有意识到的是，FreeBSD被设计成(现在仍然是)一款完整的多用途操作系统，旨在根据特定的用例进行设置和调整。当我偶尔安装FreeBSD时，它并不总是能像默认的Debian GNU/Linux那样完成同样的任务。甚至于我家里的FTP服务器上的FreeBSD也最终被Debian GNU/Linux替代了，因为FreeBSD必须每三天重启一次，否则其性能会下降很多。另一方面，Debian没有任何“乡巴佬”式表演。

**更新于2020-01-21:**人们一直在问当时FreeBSD的具体问题是什么，但是我记不清其具体细节了。我认为问题出在 FreeBSD服务器之内存不足，但我并不确定。我也未尝试解决这个问题，我那时太忙了，只是用Debian替代了FreeBSD。仅此而已。这也是很久以前的事了。当然，这是某种bug，我非常怀疑是我运行的FTP服务器软件所致，甚至与FreeBSD完全无关！

**更新于2020-01-21:**有人在Hacker News上评论道：“依我看，如果需要手动调整，那是一个bug，而非特性。(我以FreeBSD开发者的身份就此发言)”。我全然不同意此观点。有许多选项，你不能简单地自动调整，因为用例是如此不同；你需要能够手动设置你需要的特定选项。在FreeBSD上运行一个像NGINX这样的繁忙静态文件服务器和在FreeBSD上运行一则忙碌的数据库服务器之间有很大的区别；每一项设置都可能需要针对你的用例进行特定的调整，无论是在文件系统中、还是在内核中抑或在其它地方。

在接下来的几年里，GNU/Linux也获得了更好的硬件支持；(但)当我想安装FreeBSD时，一些SB硬件却无法工作。当时，硬件非常昂贵，我并没有选择购买那些我知道在FreeBSD上可以工作的硬件。所有这些问题最终促使我使用GNU/Linux的次数比FreeBSD多得多！现如今，这不再是一个大问题，因为FreeBSD对大多数现代硬件都有强大的支持，但是对GPU的支持还是不如Linux。FreeBSD [wiki](https://wiki.freebsd.org/Graphics)提供了一些相关信息。

我仍旧非常喜欢FreeBSD,并且最终在很长一段时间里，我把FreeBSD作为我的主桌面计算机(同时还在其它计算机上运行着几个GNU/Linux发行版)。

我喜欢FreeBSD的一些地方是：
* FreeBSD是一款完整的操作系统。
* FreeBSD是经过深思熟虑并精心设计的。一旦你理解了FreeBSD是如何设置以及如何工作的，你会惊讶于开发人员考虑了多少细节！
* FreeBSD将内核和基本系统与第三方软件包分开。~~这是FreeBSD独有的，其它BSD都没有此设置。~~ (**更新于2020-01-21:**我写这篇文章时犯了一个错误。其它BSD亦如此。我意思是，此为BSD独有，在GNU/Linux发行版中找不到此优点)。我一直很喜欢FreeBSD的这点，但不幸的是(依我拙见)，自2006年以来这种情况一直在变化。此项工作虽尚未完成，却会悄然而至。我个人希望此特点不必改变。
* 所有的第三方应用程序安装在/usr/local/，所有第三方应用程序之设置安装于/usr/local/etc/内。结合基本系统和第三方应用程序之间的隔离，此举使得管理第三方应用程序变得非常简单；如果需要更改设置，你可以利用pkg delete -a 删除所有已安装的软件包，然后开始安装你想要的那些软件包。
* FreeBSD仅安装了你启用的功能(无论是在安装过程中，亦或手动)，并且没有任何你不了然的功能(正)在运行。FreeBSD也是(可)选择加入的，这意味着你必须启用一些模块才能让他运行和工作。**更新于2020-01-21:**当然，一些非常基础的服务是默认运行的，比如cron，因为它们是基本操作系统维护工具的一部分。如果需要，cron会执行一些基本的日志循环！
* FreeBSD代码得到了精心的维护以及良好的文档记录。**更新于2020-01-21:**有人对此有争议。也许这需要进一步调查研究，但是我通常认为以GNU/Linux作参照时，情形确实如此。
* FreeBSD的基本安装中包含[UFS](https://en.wikipedia.org/wiki/Unix_File_System)和[ZFS](https://en.wikipedia.org/wiki/ZFS)两款文件系统。
* FreeBSD附带富存储系统[GEOM](https://en.wikipedia.org/wiki/GEOM)，它允许你使用两台联网的机器进行高可用存储，使用你选择的RAID级别，也可以添加(数据)压缩或加密等功能。
* FreeBSD还有[geli](https://en.wikipedia.org/wiki/Geli_(software))，这是一个使用GEOM磁盘框架的块设备层磁盘加密系统。
* FreeBSD服务处理非常简单。每个服务，无论是基础系统的一部分，还是从端口安装的，都带有一个负责启动和停止它的脚本(通常还有一些其它选项)。默认脚本驻留在带有默认设置的默认目录中，如/etc/default/rc.conf，但是所有设置都可以通过使用/etc/rc.conf来覆盖。如果你想启用SSHd，只需在/etc/rc.conf内添加sshd_enable="YES"即可，这样系统启动时便启用sshd；或者你可以使用命令service sshd enable，此举虽然更简单，却也能实现所需达成之目的。读取配置文件的FreeBSD rc系统理解各服务之间的依赖关系，它可以自动并行地启动它们；或者等到一项服务完成后，再启动它需要的东西。你可以获得现代配置系统的所有优势，而无需复杂的界面。**更新于2020-01-21:**有人说“其并行部分并不是真的。”我从未使用过这个选项，但是在[FreeBSD Advocacy Project](https://www.freebsd.org/advocacy/whyusefreebsd.html)中确实是这样描述的，它的字面意思是“读取这个文件的rc系统理解服务之间的依赖关系，因此可以并行地启动它们......”。如果这个说法是错误的，那么网站需要一则错误报告。
* FreeBSD同时拥有[ports system](https://en.wikipedia.org/wiki/FreeBSD_Ports)和[pkg](https://en.wikipedia.org/wiki/FreeBSD_Ports#Packages)。
* FreeBSD拥有令人称道的[Jails系统](https://en.wikipedia.org/wiki/FreeBSD_jail)，它允许你在一个sandbox中运行应用程序或整个系统，而这个sandbox并不能访问系统的其它部分。早在Docker出现前，FreeBSD便有了Jails。**更新于2020-01-21:**FreeBSD还具有[Bastille](https://www.freebsdnews.com/2020/01/07/bastille-containers-on-freebsd-bastillebsd-org/)容器管理框架，可从端口和包系统安装。
* FreeBSD具有来自Trusted BSD项目的Mandatory Access Control，它允许你为操作系统的全部资源配置访问控制策略。
* FreeBSD拥有一个允许开发者实现特权分离的Capsicum，减少受损代码的影响。
* FreeBSD还拥有VuXML系统，用于发布端口中的漏洞，该系统集成了pkg等工具，因此你的日常安全电子邮件可以告诉你所移植软件内的任何已知漏洞。
* FreeBSD使用BSM标准进行安全事件审计。

<br/>
如前所述，因为FreeBSD是一款真正的多用途操作系统，有许多不同的用例，所以FreeBSD是非常灵活且可调的。无论想在你的台式机还是你的服务器上运行FreeBSD，它都提供了许多可调整的选项，这可让你使FreeBSD有非常强悍的性能。开箱即用的选项可能并不完全契合你的需求，但是FreeBSD提供了许多关于如何让它按照你之需要进行工作的文档；并且它提供了一个非常有用的社区，其中有许多在处理很多不同情况和问题方面有经验的人员。

我相信，理解FreeBSD并不像GNU/Linux发行版这样一个事实是很重要的。FreeBSD是一款由身兼系统管理员的开发者开发的操作系统。(**更新于2020-01-21:**至少过去是这样，也许在今天是次要的。)这意味着FreeBSD应该由理解操作系统如何工作的系统管理员来运行。你不能粗暴地从像Ubuntu、Fedora或OpenSUSE这样的系统中跳脱出来，然后期望在FreeBSD上获得相同的体验(如果确实如此的话，我和其他很多人会非常难过)。

Linux发行版是由不同群体编写的工具之集合，这些工具通常有着相互冲突的利益和优先级。

Linux发行版需要Linux内核、GNU工具和库文件，可能还需要额外的第三方软件、文档、X视窗系统、窗口管理器以及桌面环境，然后他需要将这些不同的组件组合到最终的发行版中。不同的发行版专注于不同的目标，一些专注于桌面，而另一些则侧重于服务器，还有一些试图提供一个多用途的操作系统，像Ubuntu。

FreeBSD则不是那样的。FreeBSD是一款完整的操作系统，由一个对自己的工作充满热忱的团队开发，项目中没有利益冲突。维护内核的人也是维护C库文件、ls、stat和其它命令以及不同工具的人。FreeBSD还提供了与操作系统相关的所有文档。

**更新于2020-01-21:**如果FreeBSD要用作桌面操作系统，它也需要第三方软件、X视窗系统、窗口管理器以及桌面环境。我的意思是，FreeBSD不是由不同群体编写的工具之集合，通常有着相互冲突的利益和优先级，比如Linux内核、GNU C库等等。

**更新于2020-01-21:**有人指出，在FreeBSD项目中也存在利益冲突，我想到的一个例子可能是Matthew Dillion与其它FreeBSD开发人员之间的冲突；因为他在性能问题上与其它FreeBSD开发者意见不一，最终达成了在FreeBSD 4.8-stable基础上的fork，并将其命名为DragonFly BSD。但是，我最初的声明可能被误解了。我不认为BSD中的此类问题等同于完全不同的项目之间的冲突，比如Linux内核和GNU C库。Linux内的严重冲突可能导致操作系统无法正常运行；因项目的结构之缘故，前者情形在BSD项目是不可能发生的。在FreeBSD上，争议由核心团队通过[投票](https://www.freebsd.org/internal/core-vote.html)来解决，而在OpenBSD问题上，项目负责人Theo de Raadt拥有最终发言权。如果某人不同意，他要么接受此最终裁决，要么像Matthew最终做的那样，或像Theo de Raadt对NetBSD所做的那样，自由地fork项目。这与我想表达的GNU/Linux内发生的事情非常之不同！

无论你是一位在使用已发行了一段时间之GNU/Linux的用户，或是一个已经找到了最喜欢的GNU/Linux发行版之GNU/Linux用户，也或者甚至是一名Microsoft Windows用户，甚至一名MacOS用户，无论如何，我都强烈建议你试试FreeBSD。但是在此之前，花一些时间研究研究FreeBSD文档，如何理解了它是如何工作的，你将从系统获得最大之收益。多接触接触[IRC频道](https://wiki.freebsd.org/IRC/Channels)、[FreeBSD邮件列表](https://www.freebsd.org/community/mailinglists.html)或[FreeBSD论坛](https://forums.freebsd.org)上的人士，你会发现许多(对自己)大有帮助之人。

我强烈推荐Michael W.Lucas的著作[Absolute FreeBSD](https://www.amazon.com/Absolute-FreeBSD-3rd-Complete-Guide/dp/1593278926/ref=nav_signin?dchild=1&keywords=FreeBSD&qid=1593707822&s=books&sr=1-1&)，他是一位网络/安全工程师，有着丰富的在FreeBSD上使用高可用系统之经验。Michael的书详细介绍了FreeBSD操作系统。这本书写得很好，包含众多相关细节。

Michael W.Lucas和[Allan Jude](http://www.allanjude.com/resume/)还为那些对运行于FreeBSD的ZFS文件系统感兴趣的人员写了[FreeBSD Mastery : ZFS](https://www.amazon.com/gp/product/1642350001/ref=ppx_yo_dt_b_asin_title_o06_s00?ie=UTF8&psc=1)和[FreeBSD Mastery : Advanced ZFS](https://www.amazon.com/FreeBSD-Mastery-Advanced-ZFS/dp/164235001X/ref=sr_1_1?dchild=1&keywords=FreeBSD+Mastery&qid=1593708272&s=books&sr=1-1)两本书。第二本是第一本的后续，具有更多高级的用例。这两本书都是非常有价值的学习资料。

FreeBSD是一款了不起的操作系统。如果你有任何意见或修改，请随时发邮件给我。另外，如果你觉得这些内容有用，可以考虑在[Patreon](https://patreon.com/unixsheikh)上支持我。

### 一些相关链接
* [FreeBSD](https://www.freebsd.org)
* [The FreeBSD Journal](https://www.freebsdfoundation.org/journal/browser-based-edition/)
* [The BSD family tree](https://svnweb.freebsd.org/base/head/share/misc/bsd-family-tree?view=co)
* [EuroBSDcon playlists on YouTube](https://www.youtube.com/user/EuroBSDcon/playlists)
