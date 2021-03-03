---
layout: post
title: 如何编写一个拼写纠错器
---
{{page.title}}
========================
<img src="/images/posts/2018-06-02/correctorForSpelling.png">
在2007年的某个星期，[我](http://norvig.com/spell-correct.html)的两个朋友(Dean和Bill)分别告诉我，他们俩对Google的拼写纠错功能大为惊奇。在搜索框输入像speling这样的单词，Google会立即返回“您是不是要找:spelling”。我原以为作为高水平的工程师及数学家的他俩对此运行过程有很好的直觉。但是，他们并没有。细细想来，为何他们应该了解他们专业领域之外的事儿呢？<br/>

我认为他俩及其他人可从此过程解释中受益。一个工业级拼写纠错器的全部细节是相当复杂的(可阅读[这](http://static.googleusercontent.com/media/research.google.com/en/us/pubs/archive/36180.pdf)和[此处](http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=52A3B869596656C9DA285DCE83A0339F?doi=10.1.1.146.4390&rep=rep1&type=pdf)以稍做了解)。但是我发现在横贯大陆的飞行旅途中，我能以半页篇幅的代码编写一个迷你(玩具型)的拼写纠错器，它能以每秒至少10个单词的处理速度达到80%到90%的准确率。<br/>

<img src="/images/posts/2018-06-02/Spelling_Corrector_code.png">
函数correction(word)返回一个可能的拼写纠错结果：<br/>
<img src="/images/posts/2018-06-02/correction_speling.png"> <br/>

## 工作原理：一些概率论
---
correction(w)设法选择对w最可能的拼写纠正。没有办法去确切知道(比方说，应将lates纠正为late或latest或者lattes，抑或其他什么)，这就暗示我们须用概率的方式来解决它。给定原始单词w的情况下，我们设法从所有可能的候选者中找到具有最大可能性的c。

<img src="/images/posts/2018-06-02/probabilityTheory.jpg">
此表达式的四个部分为： <br/>
- 选择机制:argmax  我们选择拥有最高联合概率的候选者。
- 候选者模型:c ∈ candidates  这告诉我们可考虑的候选纠正。
- 语言模型:P(c) <br/>
  c作为单词出现在英语文本中的概率。比如，the的出现占据了英语文本的7%，因此我们就应有P(the)=0.07。
- 误差模型:P(w/c) <br/>
  作者想在文本中写c却写成w的概率。比方说,P(teh/the)值相对较高，P(theeyz/the)就比较低。

一个明显的疑问是：为什么将P(c/w)这样简单的表达式替换为一个更复杂的包含两个模型的表达式？答案是P(c/w)已经混合两个因素了，并且将两个元素分开处理更简单些。考虑误拼写的单词w=“thew”且候选纠正c=“the”及c=“thaw”的情况。哪种有更大的P(c/w)值呢？“thaw”似乎挺好，因为唯一的改变是“a”变成了“e”，并且这也是一个小变化。另一方面，“the”看起来也挺好，应为“the”是一个非常常用的单词；尽管增加一个“w”看起来是改变较大、可能性较小的该表，可能是打字员手指滑过了字母“e”以致于附带上了“w”！要点是：为了估计P(c/w)值，我们至少要考虑“c”的概率值以及从“c”改为“w”的概率值两种情况，因此将两因素分开(处理)更清晰。<br/>

## 工作原理：一些Python知识点
---
程序的四个部分为：
### 1.选择机制：
在Python中，带有一个key参数的max函数可实现argmax功能。<br/>
### 2.候选者模型：
首先，一个新概念：对单词的一次简单编辑(a simple edit)是删除(移除一个字母),移位(交换两个临近的字母),替换(将一个字母变为另一个)或者插入(增加一个字母)。函数edits1返回一个利用一次编辑生成的所有字符串的集合： <br/>

<img src="/images/posts/2018-06-02/def_edits1.png">
这会是一个大集合。对一个长度为n的单词而言，将有n种删除，n-1种移位，26n种替换，26(n+1)中插入，总计54n+25种方案(当然，其中会有一些重复值)。比如：<br/>

<img src="/images/posts/2018-06-02/len_edits1_442.png"> <br/>
然而，如果我们将(编辑得到的)单词限定为字典中的已知单词，那集合就小得多了！<br/>
  
<img src="/images/posts/2018-06-02/def_known.png">
<img src="/images/posts/2018-06-02/known_edits1_somthing.png">
我们也会考虑一些需要两次简单编辑的纠正。这产生一个更大的集合，但通常只有其中的一小部分是已知单词：<br/>

<img src="/images/posts/2018-06-02/def_edits2.png">
<img src="/images/posts/2018-06-02/len_set_edits2.png">
我们说edits2(w)结果种的每个单词与w的编辑距离为2。<br/>

### 3.语言模型：
  
我们可以通过计算每个单词在含有百万单词的文本([big.txt](http://norvig.com/big.txt))中出现的次数来测算一个单词的概率，P(word)。它的数据来自[Project Gutenberg](http://www.gutenberg.org/wiki/Main_Page)中的公共域书记以及[Wiktionary](https://en.wiktionary.org/wiki/Wiktionary:Frequency_lists)和[British National Corpus](http://www.kilgarriff.co.uk/bnc-readme.html)中频率最高的单词。words函数将文本拆分成单词，紧接着，变量WORDS持有一个每个单词出现次数的Counter(计数器)，以及据此计数器，P函数统计出每个单词的概率。 <br/>

<img src="/images/posts/2018-06-02/def_words.png">
我们可看到共出现了1,116,119次的32164个不同的单词。其中出现了79809次的单词the，是最常见的单词，其概率约为7%。<br/>

<img src="/images/posts/2018-06-02/len_WORDS.png">

### 4.误差模型：

当我在2007年于机舱内开始编写此程序时，没有拼写错误相关的数据，也没有网络链接(我明白于今天简直不能想象)。没有数据，我便不能构建一个漂亮的拼写误差模型，于是我走了一个捷径。我定义了一个一个粗糙的、有缺陷的误差模型，即：对已知单词对编辑距离为1的概率高于距离为2的编辑，且低于距离为0的编辑。所以，我们可使函数candidate(word)产生一个依优先级顺序的候选者非空列表：<br/>
  
- <1>.原始单词，如果已知；否则<2>
- <2>.编辑距离为1的单词列表中，如果有；否则<3>
- <3>.编辑距离为2的单词列表中，如果有；否则<4>
- <4>.直接返回原始单词，即便它尚未可知。
  
接着，因为选定优先级上的每个候选者拥有相同的概率，所以我们不必乘以P(w/c)因子，得：<br/>
<img src="/images/posts/2018-06-02/def_correction.png">

## 延伸阅读
---
- Roger Mitton有一篇关于拼写检查的[调查文章](http://www.dcs.bbk.ac.uk/~roger/spellchecking.html)。
- Jurafsky和Martin在他们的专著[Speech and Language Processing](http://www.cs.colorado.edu/~martin/slp.html)中很全面地包含了拼写纠错内容。
- Manning和Schutze在他们教科书[Foundations of Statistical Natural Language Processing](https://nlp.stanford.edu/fsnlp/)中包含了统计语言模型，但是好像没有涉及拼写(至少未在index中看到)。
- [aspell](http://aspell.net)项目有很多有趣的材料，包括一些[测试数据](http://aspell.net/test/)(看起来比我用到的数据要好)。
- [LingPipe](http://alias-i.com/lingpipe/)工程有一个[拼写指南](http://alias-i.com/lingpipe/demos/tutorial/querySpellChecker/read-me.html)。

<br/>
## Python版本
---
写完程序后，运行P('the')及P('outrivales')后，结果均为0。随即检查了程序运行时的Python版本;在Python2.X中，整数除以整数，只能得到整数，比如1/2的结果为0。若希望得到小数部分，将其中的一个值改为float型即可。<br/>

<img src="/images/posts/2018-06-02/def_P_float.png">
