---
layout: post
title: 为何GNU grep如此之快？
---
{{page.title}}
================================

在浏览FreeBSD之mailing list时，看到GNU grep原作者写的[why GNU grep is fast](https://lists.freebsd.org/pipermail/freebsd-current/2010-August/019310.html)一文，篇幅不长但说清楚了原因。想来，可以翻译一下；不过，如果没有彻底理解其使用的**Boyer-Moore**算法以及编程技巧，单纯地*英译中*是没有意义的！为什么呢？因为这相当于：(你)识得文章中的每一个字，但是对这些字所连接成的段落之意，一无所知；如此一来，高级文盲而已！所以想着彻底理解其算法细节后，再做翻译。

其实，用于pattern matching的优秀软件工具着实不少.比如，Udi Manber和Sun Wu在1994年出了一篇名为A Fast Algorithm for Multi-Pattern Searching的论文，并基于此算法开发了[**agrep**](https://www.tgries.de/agrep/)软件，此软件支持即便拼写错误了也无碍文本搜索的功能。因其开源，也早已被集成到[debian](https://manpages.debian.org/stretch/glimpse/agrep.1.en.html)工具集内。

why GNU grep is fast这篇文章可当作自己深入学习String Matching的引子。这类问题在日常生活中太常见了，比如，在文本编辑器(Emacs,Vim,Word等等)中搜索要寻找的字符串；互联网搜索引擎要查找与查询相关的网页，用户想在网页上定位xxx出现的具体位置；哪怕是在手机通信录上查找xxx的联系方式......已然是构成应用程序功能之基础设施了，而且对其功能运行速度的追求是‘永无止境的’。假设在开发手机通信录app时，就会遇到“如何同时搜索多个字符串”的需求：联系人数较少时，普通算法就可以满足要求；但是，如果联系人数非常多呢(比如1000+)，那么对高级算法的需求就很迫切了。就比如，确实有能够同时搜索多个字符串的方法，什么呢？是Tries！《Algorithms on Strings,Trees and Sequences：Computer Science and Computational Biology》by Dan Gusfield一书便用了足足一章来讲解Multiple String Comparison之原理。希望自己能踏下心来，切实理解，以解决工作中遭遇此需求时之无力感！读经典好书，阅优秀源码。

<img src="/images/posts/2020-07-20/Algorithms_on_Strings_Trees_and_Sequences.jpeg">

附原文:<br/>
Hi Gabor,<br/>

I am the original author of GNU grep.I am also a FreeBSD user,although I live on -stable (and older) and rarely pay attention to -current.<br/>

However, while searching the -current mailing list for an unrelated reason, I stumbled across some flamage regarding BSD grep vs GNU grep performance.  You may have noticed that discussion too...<br/>

Anyway, just FYI, here's a quick summary of where GNU grep gets its speed.  Hopefully you can carry these ideas over to BSD grep.<br/>

1 trick: GNU grep is fast because it AVOIDS LOOKING AT EVERY INPUT BYTE.<br/>
2 trick: GNU grep is fast because it EXECUTES VERY FEW NSTRUCTIONS FOR EACH BYTE that it *does* look at.<br/>

GNU grep uses the well-known **Boyer-Moore** algorithm, which looks first for the final letter of the target string, and uses a lookup table to tell it how far ahead it can skip in the input whenever it finds a non-matching character.<br/>

GNU grep also unrolls the inner loop of Boyer-Moore, and sets up the Boyer-Moore delta table entries in such a way that it doesn't need to do the loop exit test at every unrolled step.  The result of this is that, in the limit, GNU grep averages fewer than 3 x86 instructions executed for each input byte it actually looks at (and it skips many bytes entirely).<br/>


See "Fast String Searching", by Andrew Hume and Daniel Sunday,in the November 1991 issue of Software Practice & Experience, for a good discussion of Boyer-Moore implementation tricks.  It's available as a [free PDF online](http://citeseerx.ist.psu.edu/viewdoc/download?spm=a2c4e.10696291.0.0.7e6619a4UGbS11&doi=10.1.1.13.9460&rep=rep1&type=pdf).<br/>

Once you have fast search, you'll find you also need fast input.<br/>

GNU grep uses raw Unix input system calls and avoids copying data after reading it.<br/>

Moreover, GNU grep AVOIDS BREAKING THE INPUT INTO LINES.  Looking for newlines would slow grep down by a factor of several times,because to find the newlines it would have to look at every byte!<br/>

So instead of using line-oriented input, GNU grep reads raw data into a large buffer, searches the buffer using Boyer-Moore, and only when it finds a match does it go and look for the bounding newlines.(Certain command line options like -n disable this optimization.)<br/>

Finally, when I was last the maintainer of GNU grep (15+ years ago...),GNU grep also tried very hard to set things up so that the **kernel** could ALSO avoid handling every byte of the input, by using **mmap()** instead of **read()** for file input.  At the time, using read() caused most Unix versions to do extra copying.  Since GNU grep passed out of my hands, it appears that use of mmap became non-default, but you can still get it via --mmap.  And at least in cases where the data is already file system buffer caches, mmap is still faster:<br/>
<img src="/images/posts/2020-07-20/why_GNU_grep_is_fast_time.png">

workload was a 648 megabyte MH mail folder containing 41000 messages.So even nowadays, using --mmap can be worth a >20% speedup.<br/>

Summary:
- Use Boyer-Moore (and unroll its inner loop a few times).
- Roll your own unbuffered input using raw system calls.  Avoid copying the input bytes before searching them.  (Do, however, use buffered *output*.  The normal grep scenario is that the amount of output is small compared to the amount of input, so the overhead of output buffer copying is small, while savings due to avoiding many small unbuffered writes can be large.)
- Don't look for newlines in the input until after you've found a match.
- Try to set things up (page-aligned buffers, page-sized read chunks,optionally use mmap) so the kernel can ALSO avoid copying the bytes.

The key to making programs fast is to make them do practically nothing. ;-)<br/>
Regards,<br/>

Mike




