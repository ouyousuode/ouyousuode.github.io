---
layout: post
title: 从Emacs说起
---
{{page.title}}
===================================
<img src="/images/posts/2019-03-28/I_do_use_Vim_over_Emacs.jpg">
Richard的一句“<font face="Monospace">I mean,why not just use Vim over Emacs</font>(干脆连Emacs也别用，用Vim好了) ?”，直接引发程序员文本编辑器选择之战！坚持自身的选择，是要付出代价的；而且此情境中的代价是惨重的；以至，Richard觉得“两人永远都不会滚床单了！”。唉，真是何必呢？那么，GNU Emacs究竟是何方神器，竟有如此大之吸引力...

<img src="/images/posts/2019-03-28/GNU_Emacs.png">

使用手册道，<font face="Monospace">the advanced,self-documenting,customizable,extensible editor Emacs</font>!我们评价它advanced，因为它能完成的，并非只是针对文本内容的简单插入与删除。它可以控制子进程，自动缩进程序，以及一次显示多个文件等等。Emacs编辑命令根据字符、单词、行、句子、段落和页面以及各编程语言中的表达式和注释进行操作。

self-documenting意味着您可以随时使用特殊命令(称为帮助命令)来查找您的选项，或查找任何命令的功能，或查找与给定主题相关的所有命令。

customizable则意味着您可以通过简单的方式轻松更改Emacs命令的行为。例如，如果您正使用编程语言，其注释以“<-”开头并以“->”结尾，则可以告诉Emacs注释操作命令以使用这些字符串。再举一例，您可以将基本光标移动命令(向上、向下、向左和向右)重新绑定到自己感觉舒服的键盘上的任意键。

extensible意味着可以超越简单的自定义并创建全新的命令。这些新命令只是用Lisp语言编写的程序，此程序由Emacs自己的Lisp解释器运行。甚至可以在编辑会话中重新定义现有命令，而无需重新启动Emacs。Emacs内的绝大多数编辑命令均是用Lisp编写的；少数例外情况，虽可以用Lisp编写，但为效率计，使用了C语言。编写扩展的工作是编程，但是非程序员可以在之后使用它。如果你想学习Emacs Lisp编程，可以参考[<font face="Monospace">An Introduction to Programming in Emacs Lisp</font>](https://www.gnu.org/software/emacs/manual/eintr.html)。

想学Emacs Lisp编程吗？确实想学！试想，在使用Emacs时，想添加一个功能A，自己竟能顺手实现之；或者能在已有别人的(代码)版本基础上改进之。这是一件多么美好的事情！在编写扩展时，可以参考[GNU ELPA Packages](http://elpa.gnu.org/packages/)内比较成熟的代码。关于如何学习Lisp编程，可以参考Richard Stallman给出的[<font face="Monospace">How to Learn Programming</font>](http://www.stallman.org/stallman-computing.html)建议。

能够写出自己想用的扩展只是想学习Emacs Lisp语言原因之其一！那其二、其三...呢？对于如何提高编程的内功修为，programmer群体有一些公认的秘笈书单，比如：
- [Introduction to Algorithms](https://www.amazon.com/Introduction-Algorithms-3rd-MIT-Press/dp/0262033844/ref=sr_1_3?crid=1AE8LJI672T9G&keywords=introduction+to+algorithms&qid=1563115858&s=books&sprefix=introdu%2Cstripbooks%2C1276&sr=1-3)
- [The Algorithm Design Manual](https://www.amazon.com/Algorithm-Design-Manual-Steven-Skiena-dp-1848000693/dp/1848000693/ref=mt_hardcover?_encoding=UTF8&me=&qid=1563113074) by <font face="Monospace">Steven S.Skiena</font>
- [Algorithm Design](https://item.jd.com/12594778.html) by <font face="Monospace">Jon Kleinberg</font>(美国三院院士)
- [Programming Pearls](https://www.amazon.com/Programming-Pearls-2nd-Jon-Bentley/dp/0201657880/ref=sr_1_2?keywords=Programming+Pearls&qid=1563114040&s=books&sr=1-2) by <font face="Monospace">Jon Bentley</font>
- [The Practice of Programming](https://www.amazon.com/Practice-Programming-Addison-Wesley-Professional-Computing/dp/020161586X/ref=sr_1_2?crid=3J2ZIMVLT8TBJ&keywords=the+practice+of+programming&qid=1563114207&s=books&sprefix=The+Practice+of+Prog%2Cstripbooks%2C331&sr=1-2) by <font face="Monospace">Brian W.Kernighan and Rob Pike</font>
- [Design Patterns:Elements of Reusable Object-Oriented Software](https://www.amazon.com/Design-Patterns-Elements-Reusable-Object-Oriented/dp/0201633612/ref=sr_1_8?keywords=Programming+Pearls&qid=1563114308&s=books&sr=1-8)
- [Advanced Programming in the UNIX Environment](https://www.amazon.com/Advanced-Programming-UNIX-Environment-3rd/dp/0321637739/ref=sr_1_1?crid=IVFR9I9OK2WG&keywords=advanced+programming+in+the+unix+environment&qid=1563114529&s=books&sprefix=Advanced+Programming%2Cstripbooks%2C365&sr=1-1)
- [Computer Systems:A Programmer's Perspective](https://www.amazon.com/Computer-Systems-Programmers-Perspective-3/dp/9332573905/ref=sr_1_1?crid=26R37UPMPNMY5&keywords=computer+systems+a+programmers+perspective&qid=1563114627&s=books&sprefix=Computer+Sys%2Cstripbooks%2C336&sr=1-1)
- [Computer Organization and Design:The Hardware/Software Interface](https://www.amazon.com/Computer-Systems-Programmers-Perspective-3/dp/9332573905/ref=sr_1_1?crid=26R37UPMPNMY5&keywords=computer+systems+a+programmers+perspective&qid=1563114627&s=books&sprefix=Computer+Sys%2Cstripbooks%2C336&sr=1-1) 
- [Cracking the Coding Interview:189 Programming Questions and Solutions](https://www.amazon.com/Cracking-Coding-Interview-Programming-Questions/dp/0984782850/ref=sr_1_3?crid=2N0VDCZTXL6ZB&keywords=cracking+the+coding+interview&qid=1563119008&s=gateway&sprefix=Cracking+the+%2Caps%2C322&sr=8-3)
- [Structure and Interpretation of Computer Programs](https://www.amazon.com/Structure-Interpretation-Computer-Programs-Engineering/dp/0262510871/ref=sr_1_1?crid=1G1TMT5CKRSEL&keywords=structure+and+interpretation+of+computer+programs&qid=1563115165&s=books&sprefix=Struc%2Cstripbooks%2C982&sr=1-1)
- ...
<br/>

在以上书单中，<font face="Monospace">Structure and Interpretation of Computer Programs</font>是比较有趣的；因为它诠释内容的示例编程语言是Scheme。而Emacs Lisp、Common Lisp和Scheme都属于Lisp语言的方言。编程语言也是语言，既为语言，便与思维紧密相关。从定义看，思维是人脑借助于语言对客观事物的概括和间接的反应过程！

在学习某门外语时，我们不仅了解此(语言使用)民族的历史传承、文化传统，更是在学习(或言习惯)他们的思维方式，比如时间思维。在德语中，德语名词有单复数、四个格、三个性别，并且德语的冠词及形容词需要随同其修饰的名词之性数格变化。如此这般也还好，痛苦之处在于长句特别多；均从句套从句，据说“有学者在翻译黑格尔的德文原版著作时，翻到第三页居然还没找到一枚句号。”但是，甭管这句子多长、连得多远，这句话在结束时还能拉回来；什么意思呢？无论走多远，都记得初衷，都遵循一个逻辑链条。这样的语言特点就训练此民族的长远(时间)思维。

而编程语言就是程序员的思维方式。使用某些语言时，会强化面向过程的思考模式；使用另一些语言则促使你使用面向对象的方式来思考软件。不止如此，Lisp专家、世界上首个互联网应用程序Viaweb开发者之一、Y Combinator创办者<font face="Monospace">Paul Graham</font>在《On Lisp》中提到“<font face="Monospace">In Lisp, you don't just write your program down toward the language, you also build the language up toward your program.</font>”(在Lisp中，你不仅是根据语言向下构造程序，也可以根据程序向上构造语言。)这便是自顶向下(top-down design)程序设计与自底向上(bottom-up design)设计之区别。

有一条由来已久的编程原则：作为程序的功能性单元不宜过度臃肿。如果程序里某些组件的规模增长超过了它可读的程度，它就会成为一团乱麻，隐匿其中的错误就好像巨型城市里的逃犯那样难以捉拿。这样的软件将难以阅读，难以测试，调试起来也会痛苦不堪。按照这条原则，大型程序必须细分成小块，并且程序之规模越大就应该分得越细。但是，如何划分一个程序呢？传统的观点被称为自顶向下的设计(top-down design)：你说“这个程序的目的是完成这七件事，那么我就把它分成七个主要的子例程。第一个子例程要做这四件事，所以它将进一步细分程它自己的四个子例程”，如此这般。这一过程持续到整个程序被细分到合适的粒度，每一部分都足够大到可以完成一些实际的事情，但也足够小到可以作为一个基本单元来理解。

但是，有经验的Lisp程序员则用另一种不同的方式来细化它们的程序。和自顶向下(top-down)的设计类似，它们遵循一种叫做自底向上(bottom-up)的设计原则，即通过改变语言来适应程序。在编程的时候，你可能会想“Lisp要是有这样或者那样的操作符就好了。”那你就可以直接去实现它。之后，你会意识到使用新的操作符也可以简化程序中另一部分的设计，如此种种。语言和程序一同演进。就像交战两国的边界一样，语言和程序的界限不断地移动，直到最终沿着山脉和河流确定下来，这也就是你要解决的问题本身的自然边界。最后，你的程序看起来就好像，语言是为解决它而设计的。并且，当语言和程序彼此都配合得非常完美时，你得到的将是清晰、简短且高效的代码。

故此，掌握<font face="Monospace">Emacs Lisp</font>语言不仅仅是习得一门为Emacs写扩展的技艺，更多的是认识此语言表现出的世界观、扩展自己的视野范围并丰富自己的思维方式“池”。此为其二吧...

<img src="/images/posts/2019-03-28/emacs@blueberry.png">
