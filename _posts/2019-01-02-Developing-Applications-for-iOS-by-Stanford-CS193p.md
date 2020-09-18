---
layout: post
title: Developing Applications for iOS
---
{{page.title}}
==========================
<img src="/images/posts/2019-01-02/Stanford_CS193p_2013.png">
<img src="/images/posts/2019-01-02/MVC.png">

The idea with **multithreading** is that you want to divide up the execution paths of your program into different and distinct paths that are possibly running at the same time.Now I say possibly,from your standpoint as a programmer,they look like they're all running at the same time.But of course,if you have a computer or a phone that only has one processor,there's no way for them to run at the same time ! But the **OS** makes it appear that they are by basically **time slicing**,giving each one of them a little bit of time to make it seem like they're all running at the same time.<br/>

And so,if you did have a multiprocessor,maybe they would actually running at the same time,or maybe not,but you don't care and you don't know.It's totally not for you to know.Why do we want this kind of behaviour where we have these multiple threads of execution ? Well,a couple of reasons.<br/>

One,we've got one thread of execution which is the **main thread** of execution where the user is interacting,doing touch events,we want thing to be very responsive.We want that to always be listening,we never want that to not be listening.<br/>

The other thing is we have other threads of execution that actually might block.Why would they block ? Why would they stop ? Well,let's say that do a network call and they're waiting for something to come back over the network.Well,they have to wait for that thing to come back.So they are blocked,they are stopped ! We would never want that **main execution** that's listening for touch events to be stopped,or blocked.But these other ones,it's okay if they're blocked.If they're waiting for something,they're waiting for something.So to understand how we do multithreading in **iOS**,there is one thing you have to understand and that's queues !<br/>

A **queue**,just like a queue in the real world,means like a line,like you go to the movie theater and there's a line of people,that's called a queue.And so you got that queue,and the same thing is happening here with **iOS Multithreading**.<br/>

<img src="/images/posts/2019-01-02/Multithreading_0.png"><br/>
You have these queues,but in these queues,instead of people waiting for the theater,there are **block**s.So you got this queue,in line,you have these blocks,and they're all waiting in line to be executed.And depending on which queue they're in,when it's their turn,when they get to the front of the line,they get taken off the queue and they get to run.Possibly in a separate thread.Usually there might be multiple threads assigned to one queue,or single thread(一个队列会分配单个也或多个线程),again,you don't know what's going on.All you know is you're putting these blocks in a queue,and they are being taken off and allowed to run.<br/>

And people can be allowed to come off the queue one at a time,one person gets to go and watch the whole movie,and when they're done,the next person goes in.That's called a **serial queue**,it's a very simple queue.There is also concurrent queues,however,where a whole bunch of people get to go into a theater and they're all get to be doing stuff simultaneously.It's a little more complicated,because you got this queue,you're pulling off these blocks and they're all running together,if they ever want to share resources or something,they need a little more advanced multithread programming.That's how this multithreading works!<br/>

Now there's a very important queue,which is the **main queue**,that's the queue on which multi-touch is happening and all the UI stuff is happening,and this is special for two reasons.One is that we never want to block it.So we never want to do anything that's gonna take very long on the main queue.<br/>

And the second thing is we use it for synchronization for everything that's UI related.So all the methods,not all,but most of the methods in **UIKit**,you want to call them only on the main queue,and in fact,if you call them on some other,some block that came off some other queue probably wouldn't work.Now,there's a few,like UIImage,UIFont,UIColor,a couple of those things,they'll work off the main queue,but anything that's going to cause the screen to have to change or synchronize or anything like that,or that might cause that,that all needs to happen on the main queue,so we use that main queue,both to have something that's constantly responsive to the user and for synchronization,to keep anything in **sync** of what's going on in the UI side.<br/>

Everything else we could do in other queues,and actually,amazingly,iOS is doing things like actually drawing in another queue.If it's drawing something very graphic-intensive and the user multi-touches in the main queue,you want to switch back to that main queue and give it the priority,and the drawing can wait a little bit while that multi-touch gets done.<br/>

So this main queue doesn't want to be blocked,it's where we do the synchronization.If you got a block that's on another queue,non-main thread queue,then,you want to do some UI,you got to somehow talk to that **main queue**,you got a block basically on that main queue.<br/>

There are other queues,mostly they're created by iOS behind the scenes.Alright,so how do you execute a block on another queues? This is a **C**-level API,it's very low-level API below object,so you're not going to see any object stuff in this lowest level API.There's an object-oriented layer on top of it called **NSOperation**,**NSOperationQueue**,but it is kind of a thin object-oriented layer.This is the core layer.<br/>

<img src="/images/posts/2019-01-02/Multithreading_1.png"> <br/>
And this is the fundamental **C** function,`dispatch_async`,and that means synchronously put this block on this queue.So you see I've declared a queue there,local variable queue,it's a type `dispatch_queue_t`,which is a `typedef`.And then you just say `dispatch_async`,then the queue you want to put the block on and then the block.And the block takes no arguments and it remains no values,it's just a block,and you can put any code you want in there,and that block will take its place in line on that queue,and when that queue gets around it,it will take it off.<br/>

One thing,by the way,about the **main queue**,it never takes anything out of its queue to run until it's quiet(它只在安静时才会执行队列中的内容),meaning whatever current touch events have been processed.It's not going to right in the middle of a touch event,take something off its queue and go do something.So the main queue waits till it's a little quieter,and then it'll take things off the queue and run it,so you can always post things on the main queue and be sure that it's not gonna interrupt the user in anyway.<br/>

<img src="/images/posts/2019-01-02/Multithreading_2.png">
<img src="/images/posts/2019-01-02/Multithreading_3.png">
<img src="/images/posts/2019-01-02/Multithreading_4.png">

- [WWDC 2011 308 Blocks and Grand Central Dispatch in Practice](https://developer.apple.com/videos/play/wwdc2011/308/) From processing events and callbacks to keeping your app's user interface running smoothly, block objects and GCD queues are a fundamental part of software design on iOS and Mac OS X. This session provides both an introduction to the technologies and more advanced tips and tricks you need to take advantage of blocks and GCD.
- [WWDC 2015 718 Building Responsive and Efficient Apps with GCD](https://developer.apple.com/videos/play/wwdc2015/718/) watchOS and iOS Multitasking place increased demands on your application's efficiency and responsiveness. With expert guidance from the GCD team, learn about threads, queues, runloops and best practices for their use in a modern app. Take a deep dive into QoS, its propagation and advanced techniques for debugging your GCD-enabled app.
- [WWDC 2017 706 Modernizing Grand Central Dispatch Usage](https://developer.apple.com/videos/play/wwdc2017/706/) macOS 10.13 and iOS 11 have reinvented how Grand Central Dispatch and the Darwin kernel collaborate, enabling your applications to run concurrent workloads more efficiently. Learn how to modernize your code to take advantage of these improvements and make optimal use of hardware resources.


