---
layout: post
title: Yahoo!与FreeBSD
---
{{page.title}}
===============================

<img src="/images/posts/2019-09-02/FreeBSD_Logo.png">

雅虎的联合创始人David Filo于1997年写了一篇文章“Yahoo! and FreeBSD”，讲述当年雅虎为何选用FreeBSD作为他们的服务器操作系统。原文不长，4-5分钟即可读完；但是，原文在“墙”外。鉴于自身水准一般，只能将其内容粗略译来，那么语言转换时便难免会丢失信息或曰不能准确表达英文之意。“此皆言其大概，其工巧处、精美处，不能尽述”，尽量去读英文原版！

Yahoo!起家于斯坦福大学，其服务跑在运行OSF的DEC Alpha盒子和运行SunOS的Sparc 20上。它们第一年运行得很好。但是，我们也认识到这两款操作系统并非为处理大量HTTP请求而设计。实际上，我们未发现任何一款商业系统，可以处理我们所面临之扩展性问题。

在离开斯坦福之后，我们也使用过一些平台，包括SGI IRIX、Linux以及BSDI。但是，在性能及稳定性方面，均不出色；所以，我们也一直在寻找其他替代品。随着Yahoo!越来越受欢迎，可扩展性和稳定性对我们的成功变得至关重要！在当时，我们对FreeBSD尚未有什么了解，但是在看过参考资料后，还是决定试一试。

在已经花了很多时间安装其它PC操作系统之后，我对FreeBSD还是持些怀疑态度的。我也本无意花费三天时间来尝试安装另一款OS。但是，令人惊讶的是，进入FreeBSD网站，下载了软盘驱动映像，用创建的软盘启动了PC，回答了一些安装问题，几分钟后，FreeBSD便通过网络进行了安装。真正的惊喜是，当我稍后会到实际使用的完全配置之系统时！如果该安装期间出现任何问题，则其试用期可能已结束。幸运的是，这是我经历过的最简单、最轻松的操作系统安装。

几天后，我们便在Web服务器集群中添加了FreeBSD盒子。它不仅超越了我们的其它机器之性能，而且更加稳定。自那时起，我们便几乎一直将FreeBSD用于生产及开发环境。

我们从运行FreeBSD 2.0.5的单个Pentium 100盒子开始。最终，我们将其余的生产服务器迁移到了FreeBSD。时至今日，我们有超过50台服务器运行2.1 Stable的各种版本。目前在测试2.2 Stable，希望在未来6个月内进行转换。我么使用的机器范围从具有64Mb内存的Pentium 100到具有256Mb内容的PPro 200。当需要额外的I/O性能时，我们使用ccd来剥离多个磁盘；100Mbps的快速以太网用于联网。总体而言，这是一个极具成本效益的解决方案。

对我们而言，FreeBSD非常稳定。我们已经发现，每天要处理超过400万个HTTP请求的计算机有超过180天的正常运行时间。其性能也令人印象深刻。通过使用ccd进行磁盘条带化，我们每天可以在具有128Mb内存的PPro 200机器上处理超过1200万条请求。我们在FreeBSD上发现的唯一负面性是它缺乏第三方软件。幸运的是，这种情况正在改变，但是仍有长的路要走。改变这种境况的唯一方法是Yahoo!与其它组织一起说服软件供应商，让他们认识到其产品有很大的市场！

我们面临的一项重大技术挑战是面对快速增长的服务规模。展望未来，我们对使用SMP来获取更高的性价比非常有兴趣。从性价比角度看，其它平台(比如,Alpha)上的FreeBSD也很有趣。我们也在尝试让FreeBSD提供其它服务，比如，大型可靠的RAID文件服务器。总体而言，我们发现FreeBSD在性能表现、稳定性、技术支持以及价格方面都非常出色。发现FreeBSD两年后，我们还有找到要改用其它产品的原因！(by David Filo,Co-Founder of Yahoo!)

除了雅虎，我们再来看看WhatsApp与FreeBSD的渊源。2018年，Wired杂志采访了WhatsApp联合创始人Brian Acton,期间谈到，为何使用FreeBSD而非Linux？答案是，俩创始人以前就有使用FreeBSD的经验；再者，与Linux相比较，FreeBSD简单、稳定！为何使用Erlang编程语言而非其它？答案是，(它在行业中)基础扎实，为近实时通信设计；虽为通用型语言，但在高并发情况下表现优异！

翻一翻WhatsApp的两位创始人之履历，就会发现，WhatsApp和FreeBSD的结缘更顺理成章。Brian Acton于1996年成为Yahoo Inc的第44号员工；而Jan Koum在1997年更是因FreeBSD被Yahoo!雇佣成为一名infrastructure工程师。2014年，Jan Koum向FreeBSD基金会捐款100万美元；2016年，再捐50万美元。从Jan Koum于Facebook(11/17/2014)发布的捐款声明中，我们更是可以看出Jan Koum对FreeBSD的“感激”之情。

<img src="/images/posts/2019-09-02/Jan_Koum.png">

诚然，FreeBSD和Erlang皆为很棒的工具。但是，这并不意味着鼓励大家都选用它俩作为自己的开发环境，或者非要利用FreeBSD+Erlang来搭建后台服务。因为，即使向普通厨师提供了一流的烹饪工具和上等的食材，他亦无法做出美味佳肴。关键还是看人！

## References
* [Yhaoo! and FreeBSD by David Filo,Co-Founder of Yahoo!](http://zer0.org/daemons/yahoobsd.html)
* [Yahoo! & FreeBSD](https://www.freebsdnews.com/2015/11/21/yahoo-freebsd/)
* [Jan Koum is the Co-Founder and CEO of WhatsApp](https://en.wikipedia.org/wiki/Jan_Koum#References)
* [Brian Acton is an American computer programmer and Internet entreprener](https://en.wikipedia.org/wiki/Brian_Acton)
* [WhatsApp's Co-Founder on How the App Became a Phenomenon](https://www.wired.com/2015/10/whatsapps-co-founder-on-how-the-iconoclastic-app-got-huge/)
* [WhatsApp donates 1 Million Dollars to the FreeBSD Foundation](https://www.freebsdnews.com/2014/11/19/whatsapp-donates-1-million-dollars-freebsd-foundation/)

