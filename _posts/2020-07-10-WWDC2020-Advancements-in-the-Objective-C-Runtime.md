---
layout: post
title: WWDC2020之Advancements in the Objective-C Runtime
---
{{page.title}}
======================

<img src="/images/posts/2020-07-10/WWDC_2020_Hello.png">

Dive into the microscopic world of low-level bits and bytes that underlie every Objective-C and Swift class.Find out how recent changes to internal data structures,method lists,and tagged pointers provide better performance and lower memory usage.We'll demonstrate how to recognize and fix crashes in code that depend on internal details,and show you how to keep your code unaffected by changes to the runtime.

<img src="/images/posts/2020-07-10/Ben_Cohen_Languages_and_Runtimes_Engineer.png">

Hi,everyone.I'm going to talk to you about some of the changes we've made this year in the Objective-C Runtime in iOS and macOS that significantly improve memory use.This talk is a little bit different to most : you shouldn't need to change any of your code.I'm not going to talk any new APIs to learn this year,or to deprecation warnings to squash.With any luck,you won't need to do anything,and your apps will just get faster.

So why are we telling you about these improvements ? Well,partly because we think they're cool and interesting.But also because these kind of improvements in the runtime are only possible because our internal data structures are hidden behind APIs.When apps access these data structures directly...things get a little crashy.In this talk,you'll learn a few things to watch out for that might happen when someone else working on your codebase —— not you,obviously —— access things that they shouldn't.We're going to cover three changes in the session.

First,there's a change in the data structures that the Objective-C runtime uses to track classes.Then we'll take a look at changes to Objective-C method lists.Finally,we'll look at a change in how tagged pointers are represented.

<img src="/images/posts/2020-07-10/Class_Data_Structures_Changes.png">
