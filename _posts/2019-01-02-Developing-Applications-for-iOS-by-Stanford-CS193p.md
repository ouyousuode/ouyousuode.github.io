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

There are other queues,mostly they're created by iOS behind the scenes.Alright,so how do you execute a block on another queues? This is a **C**-level API,it's very low-level API below object,so you're not going to see any object stuff in this lowest level API.There's an **object-oriented** layer on top of it called **NSOperation**,**NSOperationQueue**,but it is kind of a thin object-oriented layer.This is the core layer.<br/>

<img src="/images/posts/2019-01-02/Multithreading_1.png"> <br/>
And this is the fundamental **C** function,`dispatch_async`,and that means asynchronously put this block on this queue.So you see I've declared a queue there,local variable queue,it's a type `dispatch_queue_t`,which is a `typedef`.And then you just say `dispatch_async`,then the queue you want to put the block on and then the block.And the block takes no arguments and it remains no values,it's just a block,and you can put any code you want in there,and that block will take its place in line on that queue,and when that queue gets around it,it will take it off.<br/>

One thing,by the way,about the **main queue**,it never takes anything out of its queue to run until it's quiet(它只在安静时才会执行队列中的内容),meaning whatever current touch events have been processed.It's not going to right in the middle of a touch event,take something off its queue and go do something.So the main queue waits till it's a little quieter,and then it'll take things off the queue and run it,so you can always post things on the main queue and be sure that it's not gonna interrupt the user in anyway.<br/>

What if you wanted to create another queue.Let's say you're gonna do some big math calculation or some big image processing calculation,you don't want to block the **main queue**,the **main thread**,so you can create another queue.Very simple,you just say `dispatch_queue_create`.Notice the name is not an `NSString`,because this is a low-leve API.It's a `const char *`.And the second argument is whether it's a **serial queue** or a **concurrent queue**.So `NULL` means it's a **serial queue**,so one person comes out of the line at a time.<br/>

And there's kind of an easy mode for dispatching back to the **main queue**,which is `performSelectorOnMainThread`,it's an `NSObject` method.You can send it to any `NSObject`.And you can pass a selector and its argument,the `withObject` argument,it could be `nil` and then it has no arguments,that's fine.And the `waitUntilDone` is whether you're gonna wait until this thing gets pulled off the **main queue** and run on the **main queue** and then finishes before this thread,that's calling this,goes or not,usually `waitUntilDone` you would say `no`,we don't need to wait,we're going to put this,call this method on the **main queue**,and whenever it executes is when it executes(轮到它执行时便执行).<br/>

So this `performSelectorOnMainThread` is just like saying `dispatch_async` onto the **main queue**,a block that just calls that method.So let's look at an example that uses this.This example is I want to download the contents of an URL from somewhere on the Internet.<br/>

<img src="/images/posts/2019-01-02/Multithreading_2.png"> <br/>
So I have a URL,`http://`something or other,and I want to download that from the Internet.It could take minutes,well clearly I don't want my user interface,for example,to be blocked waiting for the network to give me that URL back.So I have to call this,do this download,network download in a different queue,in a different thread,not the **main queue**.<br/>

There's an API in iOS for doing exactly this.You give it a URL and it will go do it in a different thread.Now,when it's done though,it needs to call you back,and tell you hey,'I got that URL',and the way it does that is kind of cool,it downloads the URL to a local file,and then it calls you back and gives you a URL to the local file.So now it'a all local,and so now you can open up the file and do all you want and it's not going to be blocking because the network is not a part of it anymore,it's downloaded the contents of the URL.<br/>

First,we create a URL request,which is just a wrapper on URL! A URL request is a little more than URL because you can specify some other things about that request,things you want to do,and most of the time we just create it,and we don't do anything else to the request.So now we have a request,a URL,a URL request.And then we have this configuration thing,then we create what's call a **URLSession**.So **URLSession** is an object that manage a session of time that goes out and talks to the Internet,and gets the answer and all this stuff,so **session** is the main thing that it's doing here.<br/>

And how we create the `NSURLSession` determines where,what thread,which queue,our code is gonna be executed on,and so now we have a session and we can ask the session,'please create us a task which downloads that URL',very simple`downloadTaskWithRequest`,you give it the **URLRequest**,and then you give it a `completionHandler`.<br/>

Now that `completionHandler` very important to understand,you can see that it's a **block**.The arguments to that **block**,the first argument is the most important argument,that's the URL of the local file that it put the URL into for you.So it downloaded this URL off your Internet and it put in a local file and now it's giving you a file URL,not `http://`URL,but a `file:`URL that points to a local file.And then the other arguments are about `error` and `response`,so don't worry about them,the main thing is you got this `localfile`.<br/>

And then inside this block,what if you wanted to do **UI** things here ? Well,if this block is being executed on the **main queue**,you're good to go.But if it's being executed on some other queue,any other queue,you're not good to go,you're gonna have to talk back to the **main queue**.So let's look at those two examples.First of all,let's look at creating the session using this method,`sessionWithConfiguration`,again,don't worry about configuration,`delegate:nil`,`delegateQueue:`somequeue.<br/>

<img src="/images/posts/2019-01-02/Multithreading_3.png"> <br/>
So this `NSURLSession` has a `delegate`.You can set yourself as a delegate to this **URLSession**,and as it's doing its downloading,it'll give you updates,oh,'I loaded it at 5,000 bytes','I just loaded some more bytes','I've got the file now',and 'I'm putting it into a file,a local disc,' it'll tell you all these things.Usually you don't care about any of that.You just want it to tell you when you're done,which is what that `completionHandler` is for.<br/>

So here I'm going to set my delegate to `nil`.I just want my `completionHandler` that you see here,called.But the `delegateQueue` tells you which queue are all your delegate methods going to be called on.And you can specify the **main queue**,as we have here,or you can specify some other queue.You can even pass `nil` here and it'll just make up a queue for you,a random queue(会为你随机分配一个队列).So even though we're not doing delegate methods here,we are having that `completionHandler`,and it calls your `completionHandler` on the same queue as the delegates.So that's why that `delegateQueue` line there is really important.<br/>

What that says is call my `completionHandler`,and all my delegate methods if I had them,on this queue,and I've specified the **main queue**.So,that means the code that's inside my `completionHandler` block down there,the part in yellow that says yes,can do **UI** things directly,that's being executed on the **main queue**(这段代码会在主队列上执行).It gets put in line to run on the **main queue**,and when the main queue is quiet,it'll grab it and execute it.So I can do whatever I want **UI**-wise in here.**I am on the main queue**.<br/>

**Now conversely,I wouldn't want to do anything expensive here,like,look in the URL and parse it and build some huge data structure and all this stuff,something that might block the main queue.**By being,taking a long time.But,mostly,most importantly,I can make UI calls here,I could update,if this was an Image URL,I could update my UI to show the image or whatever I want it to do.Because I have specified that it's gonna run in the **main queue**.<br/>

What if we created `NSURLSession` that doesn't specify the **delegateQueue** ? So I just use `NSURLSession`,`sessionWithConfiguration`,no `delegate`,or `delegateQueue` argument there.This case doesn't use the **main queue**,it uses a different queue.There's no delegate,so there's no need to have the delegate method specified.But the callback,this `completionHandler` gets executed on a different queue,not on the **main queue**.<br/>

<img src="/images/posts/2019-01-02/Multithreading_4.png"> <br/>
Because I haven't specified the **main queue** as the queue I want it to execute on.So in this case,if I want to do **UI** stuff,I have to make another block of stuff and put it onto the **main queue**.And I can do that either with `dispatch_async`,`dispatch_get_main_queue`,a block with this stuff,**UI** stuff I want to do,or I can do`[self performSelectorOnMainThread]` with some method on the **main queue**,or method that I want to execute,and it will be called on the **main queue**.<br/>

So do you see why I had to do that in this case ? Because this `completionHandler` is not called on the **main queue**,it's being called on a different queue.So this is what you need to understand in **iOS** is,when I have a block that's passed to some method,what queue is it going to be called on.(要清楚它会在哪个队列上被调用) <br/>

Now most time when you pass a block to an iOS method,it's going to call it on the same queue that you called that method on.Whatever method you called and you handed it a block,**it'll call you back on the same queue**,most of the time.But if the method,like this one,goes off and does something in another queue,then it's probably gonna call you back in some other queue,unless you specify the **main queue**.<br/>

Is the call `dispatch_async` thread safe ? The answer is **YES**.And so you can use it for thread synchronization,actually,because **it is thread safe.It's an atomic all,it happens atomically.It can't be interrupted** by something else coming in and doing it.<br/>

Notice all,the small thing.You notice I have `[task resume]`.When you create a download task using this,it starts out suspended,in other words,not downloaded.So you have to immediately resume it to start it.Now that **resume** is going to start something happening in a different thread,not on a **main thread**,but sill,**task resume**.Don't forget the `[task resume]` ! <br/>


- [WWDC 2011 308 Blocks and Grand Central Dispatch in Practice](https://developer.apple.com/videos/play/wwdc2011/308/) From processing events and callbacks to keeping your app's user interface running smoothly, block objects and GCD queues are a fundamental part of software design on iOS and Mac OS X. This session provides both an introduction to the technologies and more advanced tips and tricks you need to take advantage of blocks and GCD.
- [WWDC 2015 718 Building Responsive and Efficient Apps with GCD](https://developer.apple.com/videos/play/wwdc2015/718/) watchOS and iOS Multitasking place increased demands on your application's efficiency and responsiveness. With expert guidance from the GCD team, learn about threads, queues, runloops and best practices for their use in a modern app. Take a deep dive into QoS, its propagation and advanced techniques for debugging your GCD-enabled app.
- [WWDC 2017 706 Modernizing Grand Central Dispatch Usage](https://developer.apple.com/videos/play/wwdc2017/706/) macOS 10.13 and iOS 11 have reinvented how Grand Central Dispatch and the Darwin kernel collaborate, enabling your applications to run concurrent workloads more efficiently. Learn how to modernize your code to take advantage of these improvements and make optimal use of hardware resources.


