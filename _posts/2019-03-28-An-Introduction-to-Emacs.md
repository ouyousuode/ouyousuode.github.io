---
layout: post
title: 从Emacs说起
---
{{page.title}}
===================================
<img src="/images/posts/2019-03-28/EmacsAndVim.jpg">
“干脆连Emacs也别用，用Vim好了”，此直接引发程序员文本编辑器选择之战！
``` Objective-C
Winnie:I do use Vim over Emacs.

Richard:Oh,God,help us ! Okay,uh...you know what ? I just...I don't think 
this is going to work.I'm sorry.Uh,I mean like,what,we're going to bring 
kids into this world with that over their heads ? That's not really fair 
to them,don't you think ?

Winnie:Kids ? We haven't even slept together.

Richard:And guess what,it's never going to happen now...

Winnie is a Facebook engineer.And when she was at MIT,she did her master's 
thesis on game playing AIs.She did a lot of machine learning stuff! .. 
for example,train a convolutional net to play Atari 2600 games.
```
坚持自身的选择，是要付出代价的；而且此情境中的代价是惨重的；以至，Richard觉得“两人永远都不会滚床单了！”；唉，真是何必呢？那么，GNU Emacs究竟是何方神器，竟有如此大之吸引力...

<img src="/images/posts/2019-03-28/GNU_Emacs.png">
使用手册是这样介绍的，“the advanced,self-documenting,customizable,extensible editor Emacs”。我们说它advanced，因为它能完成的，并非只是针对文本内容的简单插入与删除。它可以控制子进程，自动缩进程序，以及一次显示多个文件等等。Emacs编辑命令根据字符、单词、行、句子、段落和页面以及各编程语言中的表达式和注释进行操作。

self-documenting意味着您可以随时使用特殊命令(称为帮助命令)来查找您的选项，或查找任何命令的功能，或查找与给定主题相关的所有命令。

customizable则意味着您可以通过简单的方式轻松更改Emacs命令的行为。例如，如果您正使用编程语言，其注释以“<-”开头并以“->”结尾，则可以告诉Emacs注释操作命令以使用这些字符串。再举一例，您可以将基本光标移动命令(向上、向下、向左和向右)重新绑定到自己感觉舒服的键盘上的任意键。

extensible意味着可以超越简单的自定义并创建全新的命令。这些新命令只是用Lisp语言编写的程序，此程序由Emacs自己的Lisp解释器运行。甚至可以在编辑会话中重新定义现有命令，而无需重新启动Emacs。Emacs内的绝大多数编辑命令均是用Lisp编写的；少数例外情况，虽可以用Lisp编写，但为效率计，使用了C语言。编写扩展的工作是编程，但是非程序员可以在之后使用它。如果你想学习Emacs Lisp编程，可以参考[An Introduction to Programming in Emacs Lisp](https://www.gnu.org/software/emacs/manual/eintr.html)。

想学Emacs Lisp编程吗？确实想学！试想，在使用Emacs时，想添加一个功能A，自己竟能顺手实现之；或者能在已有别人的(代码)版本基础上改进之。这是一件多么美好的事情！在编写扩展时，可以参考[GNU ELPA Packages](http://elpa.gnu.org/packages/)内比较成熟的代码。关于如何学习Lisp编程，可以参考Richard Stallman给出的[How to Learn Programming](http://www.stallman.org/stallman-computing.html)建议。

能够写出自己想用的扩展只是想学习Emacs Lisp语言原因之其一！那其二、其三...呢？对于如何提高编程的内功修为，programmer群体有一些公认的秘笈书单，比如：
- [Introduction to Algorithms](https://www.amazon.com/Introduction-Algorithms-3rd-MIT-Press/dp/0262033844/ref=sr_1_3?crid=1AE8LJI672T9G&keywords=introduction+to+algorithms&qid=1563115858&s=books&sprefix=introdu%2Cstripbooks%2C1276&sr=1-3)
- [The Algorithm Design Manual](https://www.amazon.com/Algorithm-Design-Manual-Steven-Skiena-dp-1848000693/dp/1848000693/ref=mt_hardcover?_encoding=UTF8&me=&qid=1563113074) by Steven S.Skiena
- [Algorithm Design](https://item.jd.com/12594778.html) by Jon Kleinberg(美国三院院士)
- [Programming Pearls](https://www.amazon.com/Programming-Pearls-2nd-Jon-Bentley/dp/0201657880/ref=sr_1_2?keywords=Programming+Pearls&qid=1563114040&s=books&sr=1-2) by Jon Bentley
- [The Practice of Programming](https://www.amazon.com/Practice-Programming-Addison-Wesley-Professional-Computing/dp/020161586X/ref=sr_1_2?crid=3J2ZIMVLT8TBJ&keywords=the+practice+of+programming&qid=1563114207&s=books&sprefix=The+Practice+of+Prog%2Cstripbooks%2C331&sr=1-2) by Brian W.Kernighan and Rob Pike
- [Design Patterns:Elements of Reusable Object-Oriented Software](https://www.amazon.com/Design-Patterns-Elements-Reusable-Object-Oriented/dp/0201633612/ref=sr_1_8?keywords=Programming+Pearls&qid=1563114308&s=books&sr=1-8)
- [Advanced Programming in the UNIX Environment](https://www.amazon.com/Advanced-Programming-UNIX-Environment-3rd/dp/0321637739/ref=sr_1_1?crid=IVFR9I9OK2WG&keywords=advanced+programming+in+the+unix+environment&qid=1563114529&s=books&sprefix=Advanced+Programming%2Cstripbooks%2C365&sr=1-1)
- [Computer Systems:A Programmer's Perspective](https://www.amazon.com/Computer-Systems-Programmers-Perspective-3/dp/9332573905/ref=sr_1_1?crid=26R37UPMPNMY5&keywords=computer+systems+a+programmers+perspective&qid=1563114627&s=books&sprefix=Computer+Sys%2Cstripbooks%2C336&sr=1-1)
- [Computer Organization and Design:The Hardware/Software Interface](https://www.amazon.com/Computer-Systems-Programmers-Perspective-3/dp/9332573905/ref=sr_1_1?crid=26R37UPMPNMY5&keywords=computer+systems+a+programmers+perspective&qid=1563114627&s=books&sprefix=Computer+Sys%2Cstripbooks%2C336&sr=1-1) 
- [Cracking the Coding Interview:189 Programming Questions and Solutions](https://www.amazon.com/Cracking-Coding-Interview-Programming-Questions/dp/0984782850/ref=sr_1_3?crid=2N0VDCZTXL6ZB&keywords=cracking+the+coding+interview&qid=1563119008&s=gateway&sprefix=Cracking+the+%2Caps%2C322&sr=8-3)
- [Structure and Interpretation of Computer Programs](https://www.amazon.com/Structure-Interpretation-Computer-Programs-Engineering/dp/0262510871/ref=sr_1_1?crid=1G1TMT5CKRSEL&keywords=structure+and+interpretation+of+computer+programs&qid=1563115165&s=books&sprefix=Struc%2Cstripbooks%2C982&sr=1-1)
- ...
<br/>

在以上书单中，Structure and Interpretation of Computer Programs是比较有趣的；因为它诠释内容的示例编程语言是Scheme。Emacs Lisp、Common Lisp和Scheme都属于Lisp语言的方言。

<img src="/images/posts/2019-03-28/Structure_and_Interpretation_of_Computer_Programs.gif">
<img src="/images/posts/2019-03-28/emacs@blueberry.png">


