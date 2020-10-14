---
layout: post
title: The Bayesian Trap
---
{{page.title}}
=============================

在YouTube浏览视频时，下意识地搜了Bayesian关键字，结果列表中的The Bayesian Trap观看次数达2百万之多，👍亦颇多，其出品方Veritasium拥766万位订阅者。观看完毕，有所得。其实，贝叶斯定理是很基础的数学知识，大学课程之基础概率论皆有。为何还专门记录呢？其一，温故知新；其二，学学如何讲故事。<br/>

<img src="/images/posts/2020-08-01/The_Bayesian_Trap.png">

试想一下，某天早晨，你起床时感觉到了一些不舒服，也没有什么特别症状，就是感觉不在状态。你去找医生，她也不知道你怎么了，所以她就建议你去做了一系列的检查。<br/>

一周之后结果出来了，结果显示你对于一种十分罕见的疾病之检测结果为阳性！这种病在人群中的发病率只有0.1%，这是一个相当糟糕的结果，你压根不想得这个病。所以，你就问医生“我有多大可能性真的患了这种病？”她说，“这个测试可以99%正确地识别出患这种病的人，只有1%的可能将未病者也识别为患病的人。” 这听起来也太糟糕了。<br/>

我是说，你真的有可能得这种病吗？我想大多数会说99%吧，即这次测试的准确率。但事实上，这是不对的！你需要贝叶斯定理(Bayes' Theorem)以正确地看待这一事件。贝叶斯定理可以告诉你「如果你的检验结果为阳性，那么你得这个病的概率究竟是多少？」<br/>

<img src="/images/posts/2020-08-01/Bayes_Theorem.png"> <br/>
为了计算这个数值P(H|E),你需要使用**假设**(hypothesis)为真时的**先验概率**，也就是在你没有任何检测的情况下，得这种病的概率P(H);**乘以**先验事件为真时该事件发生的概率，也就是得了这种病时检测结果为阳性的概率P(E|H);然后，**除以**该事件发生的全概率，也就是检测结果为阳性的概率,P(E)。其中，P(E)部分包括得这种病时检测结果为阳性的概率，加上你没得这病时检测结果仍为阳性的概率，即P(E) = P(H)•P(E|H) + P(-H)•P(E|-H)。<br/>

<img src="/images/posts/2020-08-01/Bayes_Theorem_total_probability.png"> <br/>
当**假设**(hypothesis)为真时，**先验概率**通常是这个方程式中最难求的。有时去正儿八经地计算它，还不如随便猜一个。但在当前这个案例中，我们的出发点是已知这种病在人群中发生的频率(frequency of the disease),它是0.001，即P(H) = 0.1%。当你补齐剩下的数字计算出结果时，你会发现，<br/>

<img src="/images/posts/2020-08-01/Bayes_Theorem_calculation.png">
<img src="/images/posts/2020-08-01/Bayes_Theorem_result.png">

事实上，在检测结果为阳性时，你只有9% 的概率真得了这种病。想想看，这个概率真的挺低的！这并不是某种疯狂的魔术，它实际上是数学上的常识。<br/>

<img src="/images/posts/2020-08-01/sample_1000_people.png">
试想有样本数量为1000的人群，其中有一人真的患了这种病，检测结果也正确地显示他得了这种病。但是在其他999人中的1% 或者说有10个人也被检测出了患有这种疾病。所以，如果你是其中一个检测结果为阳性的人，并且在这些人里随机选择。实际上，是在一个11人的小组里并且只有一个人得了这种病。所以，你真正患这种病的概率只有1/11，也就是9%。这刚好讲得通！<br/>

<img src="/images/posts/2020-08-01/sample_11_people.png"> <br/>
当贝叶斯第一次提出这个定理时，他并不认为它是革命性的。他甚至认为它不值得出版，他没有提交给他所属的皇家学会，事实上这是在他死后的论文中发现的，他已经放弃了它十多年。他的亲戚请他的朋友Richard Price翻翻他的论文，看看有没有什么值得发表的东西。在那里，Price发现了我们现在所熟知的贝叶斯定理的底稿。<br/>

贝叶斯最初设想了一项思想实验，他背对着一张很平、很方的桌子坐着，然后他让一个助手把一个球扔到桌子上。这个球显然可以落在桌上的任何一处，他想找出这个球在桌上的位置。所以，他就叫助手再丢一个球，然后告诉他这个球落在第一个球的左边、右边还是前面或后面;他把这些记下来，然后再要求助手向桌子上扔更多的球。他意识到，通过这种方法，他可以不断更新他对第一个球位置在何处的想法。当然，他永远也不会完全确定第一个球的位置，但是随着每一件证据的出现，他对这个球的位置之估计也越来越准确，这便是贝叶斯对世界的看法！不是他认为世界没有确定，现实不实际存在，而是我们不能完美地了解它，而我们所能希望做的就是随着越来越多的线索来更新我们的世界观。<br/>

当Richard Price介绍贝叶斯定理时，他打了个比方，一个一辈子都住在山洞里的人走出山洞时，他第一次看见了太阳升起，心想「太阳的升起是不是一次性的？这是不是偶然？还是说它总这样？」而后，在那之后的每一天，当太阳再次升起的时候，他可以变得更自信一点地说「太阳每天都是这样工作的。」<br/>

因此，贝叶斯定理并不仅仅是一则只打算使用一次的公式，在我们获取到新线索来更新我们对某个事物的认识时会用到它很多次。现在我们回到第一个例子，如果你被检测出对某个疾病呈阳性，当你去找另一个医生再做一次相同的检测会发生什么呢？ 或者去另一个实验室，只要保证这些检测是相互独立的，即可！我们假设第二次结果同样也是呈阳性，那么，现在你真正患这种疾病的概率又是多少呢？你可以再次使用贝叶斯方程。不过这次，你「患病的」先验概率<br/>

<img src="/images/posts/2020-08-01/Use_Bayes_Theorem_second.png"> <br/>
变成了我们计算第一次检查为阳性时你患病的概率，也就是计算得到的9%，即图中P(H) = 9%，因为你已经有过一次结果为阳性的检测。如果把这些数值代入公式，<br/>

<img src="/images/posts/2020-08-01/Use_Bayes_Theorem_second_numbers.png">
<img src="/images/posts/2020-08-01/Use_Bayes_Theorem_second_result.png">

那么，基于两次阳性测试，你真正患病的概率将会是91%。你有91%的可能真的患有这种疾病，这是有道理的。两次在不同的实验室阳性的检测貌似不可能是偶然，但是，你也注意到了这个概率仍然没有高于检测报告中的「准确性」。 <br/>

贝叶斯理论已经有很多的实际应用，比如可以过滤垃圾邮件。你知道，传统的垃圾邮件过滤器实际上做得很差，在你的邮箱中有太多被误报的垃圾邮件。但是，有了贝叶斯过滤器之后事情就不一样了，你可以查看各种出现在Email中的单词，然后使用贝叶斯定理来计算「当出现了这些单词时，当前这封邮件为垃圾邮件的概率」。<br/>

<img src="/images/posts/2020-08-01/Spam_Filters.png"> <br/>
贝叶斯定理告诉我们，当有新证据时该怎样修正我们对事物的看法，但是，它却不能告诉我们如何设置我们的**先验**看法。对一些人来说，只有某件事发生的概率为100%，这件事才是真的；而对另一些来说，可能它发生的概率为0，也认为这件事为真！贝叶斯定理在这向我们展示，在这种情况下，没有人能改变他们的看法。因此，Nate Silver在他的《The Signal and The Noise(信号与噪声)》一书中指出「我们也许不应该同那些持肯定态度与否定态度的人进行辩论，因为他们永远不能说服对方什么事」。<br/>

大多数时候，当人们谈论贝叶斯定理时，他们认为这非常反直觉，以及我们为何对它并无内在的感觉。但是，最近我的想法发生了转变，也许我们太擅长内化贝叶斯定理背后的思想。我对此有些担心，因为在生活中我们会渐渐习惯于一些特定的环境，我们会习惯接受一些结果，「也许是被拒绝了或在某件事上失败了，又或者拿着低工资」，我们可以把这种感觉内在化，就好像那个从山洞里走出的人看见太阳每天升起，我们每天都在更新自己对这个世界的认知，几乎可以肯定地说，「我们会渐渐领悟到大自然本该如此，世界本来也应该是这样」，没人能再改变你的想法。<br/>

有一句Nelson Mandela名言，“**任何事情在完成之前都是不可能的**”，我认为这是一种非常贝叶斯的世界观。如果你没有某件事发生的实例，那么你对这件事的预期看法是怎样的。在它真正发生之前，你的先验可能是0，这似乎是完全不可能的(你也许觉得这件事情完全是不可能的，但是，它却发生了)。我们在贝叶斯定理中所忽略的一件事是，我们对某件事结果的认知以及这件事是否会真的发生，**与我们的行动是有关的**！<br/>

但是如果我们先入为主地认为某件事是真的，也许我们100%确定它是真的，而且我们无法改变这个结果，那么，我们将继续做同样的事情，我们将继续得到同样的结果，这是一个自我实现的预言。所以我认为对贝叶斯定理的真正理解意味着，实践是十分必要的！如果你花了很长时间一直在做同一件事情，而且均得到了一个让你不满意的结果，也许，**是时候改变了**！所以，如果你有类似经历，应该好好想想了。<终>

注：视频结尾，作者推荐了 Sharon Bertsch Mcgrayne所著《The Theory That Would Not Die: How Bayes' Rule Cracked the Enigma Code, Hunted Down Russian Submarines, and Emerged Triumphant from Two Centuries of Controversy》一书，检索发现，**The New York Times(纽约时报)**还载有一篇它的书评[The Mathematics of Changing Your Mind](https://www.nytimes.com/2011/08/07/books/review/the-theory-that-would-not-die-by-sharon-bertsch-mcgrayne-book-review.html),且值得耗几分钟时间翻一翻。<br/>


## 深入阅读
* Bayes' theorem - Wikipedia
* 「8.A Plan for Span」in 《Hackers & Painters: Big Ideas from the Computer Age》by Paul Graham <br/>
* 「Chapter 23,Practical: A Spam Filter」in 《Practical Common Lisp》by Peter Seibel <br/>
* 「37 Bayesian Inference and Sampling Theory」in 《Information Theory,Inference,and Learning Algorithms》 by David MacKay <br/>
* 《Bayesian Theory》by José M. Bernardo <br/>
* 《Bayesian Data Analysis》 by Andrew Gelman <br/>
* 《The Theory That Would Not Die: How Bayes' Rule Cracked the Enigma Code, Hunted Down Russian Submarines, and Emerged Triumphant from Two Centuries of Controversy》by Sharon Bertsch Mcgrayne <br/>


<img src="/images/posts/2020-08-01/Scenery.png"> <br/>

附script for「The Bayesian Trap」原文：<br/>

Picture this: You wake up one morning and you feel a little bit sick.No particular symptoms, just not 100%. So you go to the doctor and she also doesn't know what's going on with you, so she suggests they run a battery of tests.<br/>

After a week goes by, the results come back, turns out you tested positive for a very rare disease that affects about 0.1% of the population and it's a nasty disease, horrible consequences, you don't want it.So you ask the doctor "You know, how certain is it that I have this disease?" and she says "Well, the test will correctly identify **99%** of people that have the disease and only incorrectly identify **1%** of people who don't have the disease".<br/>

So that sounds pretty bad.I mean, what are the chances that you actually have this disease? I think most people would say 99%, because that's the accuracy of the test. But that is not actually correct! **You need Bayes' Theorem to get some perspective.** Bayes' Theorem can give you the probability that some **hypothesis**, say that you actually have the disease, is true given an **event**; that you tested positive for the disease.<br/>

<img src="/images/posts/2020-08-01/Bayes_Theorem_en.png">
<img src="/images/posts/2020-08-01/Bayes_Theorem_total_probability_en.png">

To calculate this, you need to take the **prior probability of the hypothesis** was true,<br/>
- that is, how likely you thought it was that you have this disease before you got the test results <br/>
- and multiply it by the probability of the **event** given the **hypothesis** is true <br/>
- that is, the probability that you would test positive if you had the disease  <br/>
- and then divide that by the **total probability of the event** occurring <br/>
- that is testing positive. <br/>

This term is a combination of your probability of having the disease and correctly testing positive plus your probability of not having the disease and being falsely identified.The prior probability that a hypothesis is true is often the hardest part of this equation to figure out and, sometimes, it's no better than a guess. But in this case, a reasonable starting point is the frequency of the disease in the population, so 0.1%. <br/>

And if you plug in the rest of the numbers, you find that you have a **9% chance of actually having the disease after testing positive., which is incredibly low if you think about it**. Now, this isn't some sort of crazy magic. It's actually **common sense** applied to mathematics.<br/>

Just think about a sample size of 1000 people. Now, one person out of that thousand, is likely to actually have the disease. And the test would likely identify them correctly as having the disease. But out of the 999 other people, 1% or 10 people would falsely be identified as having the disease. So, if you're one of those people who has a positive test result and everyone's just selected at random. <br/>

Well, you're actually part of a group of 11 where only one person has the disease. So your chances of actually having it are **1 in 11 (9%). It just makes sense.** <br/>

When Bayes first came up with this theorem he didn't actually think it was revolutionary. He didn't even think it was worthy of publication, he didn't submit it to the **Royal Society** of which he was a member, and in fact it was discovered in his papers after he died and he had abandoned it for more than a decade. <br/>

His relatives asked his friend, **Richard Price**, to dig through his papers and see if there is anything worth publishing in there. And that's where Price discovered what we now know as the origins of Bayes' Theorem. <br/>

Bayes originally considered a thought experiment where he was sitting with his back to a perfectly flat, perfectly square table and then he would ask an assistant to throw a ball onto the table. Now this ball could obviously land and end up anywhere on the table and he wanted to figure out where it was. So what he'd asked his assistant to do was to throw on another ball and then tell him if it landed to the left, or to the right, or in front, behind of the first ball, and he would note that down and then ask for more and more balls to be thrown on the table. What he realized, was that through this method he could keep updating his idea of where the first ball was. Now of course, he would never be completely certain, but with each new piece of evidence, he would get more and more accurate, and that's **how Bayes saw the world**. It wasn't that he thought the world was not determined, that reality didn't quite exist, but it was that we couldn't know it perfectly, and all we could hope to do was update our understanding as more and more evidence became available.<br/>

When Richard Price introduced Bayes Theorem, he made an analogy to a man coming out of a cave, maybe he'd lived his whole life in there and he saw the Sun rise for the first time, and kind of thought to himself: "Is, Is this a one-off, is this a quirk, or does the Sun always do this?" And then, every day after that, as the Sun rose again, he could get a little bit more confident, that, well, that was the way the world works. <br/>

So Bayes' Theorem wasn't really a formula intended to be used just once, it was intended to be used multiple times, each time gaining new evidence and updating your probability that something is true. <br/>

So if we go back to the first example when you tested positive for a disease, what would happen if you went to another doctor, get a second opinion and get that test run again, but let's say by a different lab, just to be sure that those tests are independent, and let's say that test also comes back as positive. <br/>

Now what is the probability that you actually have the disease ? Well, you can use Bayes formula again, except this time for your prior probability that you have the disease, you have to put in the posterior probability, the probability that we worked out before which is 9%, because you've already had one positive test. If you crunch those numbers, the new probability based on two positive tests is 91%. There's a 91% chance that you actually have the disease, which kind of makes sense. 2 positive results by different labs are unlikely to just be chance, but you'll notice that probability is still not as high as the accuracy, the reported accuracy of the test.<br/>

Bayes' Theorem has found a number of practical applications, including notably filtering your spam. You know, traditional spam filters actually do a kind of bad job, there's too many false positives, too much of your email ends up in spam, but using a Bayesian filter, you can look at the various words that appear in e-mails, and use Bayes' Theorem to give a probability that the email is spam, given that those words appear. <br/>

Now Bayes' Theorem tells us how to update our beliefs in light of new evidence, but it can't tell us how to set our prior beliefs, and so it's possible for some people to hold that certain things are true with a 100% certainty, and other people to hold those same things are true with 0% certainty. <br/>

What Bayes' Theorem shows us is that in those cases, there is absolutely no evidence, nothing anyone could do to change their minds, and so as Nate Silver points out in his book, The Signal and The Noise, we should probably not have debates between people with a 100% prior certainty, and 0% prior certainty, because, well really, they'll never convince each other of anything.<br/>

Most of the time when people talk about Bayes' Theorem, they discussed how counterintuitive it is and how we don't really have an inbuilt sense of it, but recently my concern has been the opposite: that maybe we're too good at internalizing the thinking behind Bayes' Theorem, and the reason I'm worried about that is because, I think in life we can get used to particular circumstances, we can get used to results, maybe getting rejected or failing at something or getting paid a low wage and we can internalize that as though we are that man emerging from the cave and we see the Sun rise every day and every day, and we keep updating our beliefs to a point of near certainty that we think that that is basically the way that nature is, it's the way the world is and there's nothing that we can do to change it.<br/>

You know, there's **Nelson Mandela**'s quote that: '**Everything is impossible until it's done**', and I think that is kind of a very Bayesian viewpoint on the world, if you have no instances of something happening, then what is your prior for that event? It will seem completely impossible your prior may be 0 until it actually happens. You know, the thing we forget in Bayes' Theorem is that: our actions play a role in determining outcomes, and determining how true things actually are. <br/>

But if we internalize that something is true and maybe we're a 100% sure that it's true, and there's nothing we can do to change it, well, then we're going to keep on doing the same thing, and we're going to keep on getting the same result, it's a self-fulfilling prophecy, so I think a really good understanding of Bayes' Theorem implies that experimentation is essential. If you've been doing the same thing for a long time and getting the same result that you're not necessarily happy with, maybe it's time to change.<br/>

视频介绍下方，作者又加了一段，I didn't say it explicitly in the video, but in my view the Bayesian trap is interpreting events that happen repeatedly as events that happen inevitably. They may be inevitable OR they may simply be the outcome of a series of steps, which likely depend on our behaviour. Yet our expectation of a certain outcome often leads us to behave just as we always have which only ensures that outcome. To escape the Bayesian trap, we must be willing to experiment.<br/>
