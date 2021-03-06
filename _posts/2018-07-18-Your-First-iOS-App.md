---
layout: post
title: 第一个iOS App
---
{{page.title}}
=================================
<img src="/images/posts/2018-07-18/Your_First_iOS_App.png">
# 0 关于创建第一个iOS App
第一个iOS应用向你介绍iOS app开发的3T:
- **Tools**.如何利用Xcode创建以及管理一个工程。
- **Technologies**.如何创建一个可以响应用户输入动作的应用。
- **Techniques**.如何利用所有iOS应用开发的基础设计模式。

在完成了指导中的所有步骤后；你可以得到这样一个app，它接收你的输入并将其输出到屏幕上:

<img src="/images/posts/2018-07-18/First_iOS_App_3.png">

为从此指南中获益，你必须已熟悉了计算机编程的基本概念，尤其是Objective-C语言。如果你以前没用过Objective-C,在阅读此指南前，读一读*Learning Objective-C:A Primer*。
## 0.1 介绍 
---
跟着此指南走，会帮你踏上开发伟大iOS应用的康庄大道。虽然你在此指南中创建的app非常简单，但是它会帮助你适应iOS开发环境，并且向你介绍一些塑造伟大iOS应用的强大设计模式。
### 熟悉开发工具与设计模式
iOS应用在Xcode中开发，它是Apple的集成开发环境。Xcode包含了多个开发者工具，并且集成了用于编码app的编程框架。随着你开始为app编码，你需要学习成功的app是如何构成的以及一些关键设计模式如何帮你写出更棒的代码。
### 了解视图控制器与视图的作用
在一个iOS应用中，一个视图(view)仅仅是屏幕上的用户可以看见并与之交互的一块区域。一个视图对象可以是任意大小，它可以包含其它视图对象，比如按钮或者图像。视图控制器是控制一个或多个视图的一个对象。在此指南中，你会学到：app中不同对象的角色，如何创建视图控制器与添加到它视图中对象的连接。
### 编写执行特定任务的代码
Xcode与编程框架(programming framework)合作为你提供很多功能，但是仍然有一些你的app必须完成的特定任务。在本指南中，你会学到：如何通过写一个方法来实现某个特定的任务；当一个按钮被点击时，此方法被调用。
### 解决问题并选择下一步的方向
随着你完成本指南中的任务，你可能遇到你不知如何解决的问题。*Your First iOS Application*粗略描述了一些可查找的常见错误，它也包括一个用于对比的代码列表。

在完成本指南后，你应当考虑可提高app质量以及增长知识面的方式。可选择的方向很多，并且Xcode和iOS编程框架可帮你实现你可设计的方方面面。
## 0.2 另请参阅
---
开发iOS应用是一个多步骤的过程，从决定app应做什么到将它提交App Store。*Your First iOS Application*不能帮你决定你的app应当做什么，也未描述如何把他们提交到App Store，但是有很多可帮你实现这些任务的资源。这些资源中的部分列在此处：
- 想学习设计iOS应用的用户界面及体验，可参见 iOS Human Interface Guidelines。
- 想对创建一个全特征iOS应用有综合认识，参见 iOS App Programming Guide。
- 当准备提交app到App Store时，为了解需要执行的全部任务，可参考 Developing for the App Store。

<br/>
# 1 准备开始
为创建本指南中的iOS应用，需要Xcode 4.2(及以上)。Xcode是为iOS及Mac OS X开发的Apple集成开发环境。当在Mac上安装Xcode时，也会得到iOS SDK，它包括iOS平台的编程接口。
## 1.1 创建及测试一个新工程
---
为开启开发应用之路，创建一个新Xcode工程。

1.启动Xcode,

<img src="/images/posts/2018-07-18/Xcode_00.png">
2.在对Xcode的欢迎界面，选择"Create a new Xcode project"。Xcode会打开一个新窗口，并且展示一个对话页面，开发者可从此选择一个模版。Xcode包括几个内置的应用模版，开发者可以利用这些模版开发通常样式的iOS应用。比如，Tabbed App模版创建一个类似于iTunes的应用，而根据Master-Detail App模版创建的app之外观与Mail相差无几。

<img src="/images/posts/2018-07-18/Xcode_01.png">
3.在iOS部分，选择Application条目下的Single View App模版后，点击click。

<img src="/images/posts/2018-07-18/Xcode_02.png">
4.填写Production Name、Organization Name以及Organization Identifier，选择开发使用的编程语言。它们可以使用以下值：
- Language : Objective-C
- Production Name : HelloWorld
- Organization Name及 Identifier : 如有，可如实填写；若无，可以使用edu.self

完成后，选择Next,即得一个新工程。

<img src="/images/posts/2018-07-18/newProjectDone.png">
虽然你连一行代码都没写，但是可以构建应用并在模拟器中运行它。正如它名字所暗示的那样，模拟器(Simulator)允许你得到app如何表现及运行的直观印象，就好像它运行在基于iOS设备上一样。

<img src="/images/posts/2018-07-18/deviceChoice.png">
选择一款称心的模拟设备，然后点按Run。在Xcode完成工程构建后，模拟器会自动启动。因指定了一款iPhone，模拟器会展现一个类似此iPhone的窗口。在模拟的iPhone屏幕上，模拟器打开应用。

<img src="/images/posts/2018-07-18/defaultApp.png">
现在看，这款应用并不有趣：它仅显示一块空白屏幕。为理解此空白屏幕来自何处，需要学习代码中的对象以及它们是如何合作来启动app的。现在，可以退出模拟器了(要确保，不要退出Xcode)。

## 1.2 查明应用如何启动
---
因为基于Xcode模版构建工程，所以当你运行应用时，大部分基本的应用环境已自动建立。比如，Xcode创建一个application对象，它连接窗口服务器(*window server*)，建立*run loop*以及其它的准备工作。此工作的大部分均由*UIApplicationMain*函数完成，它在main.m文件中完成调用。可在工程目录中查看main.m源文件：

<img src="/images/posts/2018-07-18/mainFile.png">
main.m文件内的*main*函数在一个自动释放池内调用*UIApplicationMain*函数。
``` Objective-C
@autoreleasepool {
	return UIApplicationMain(argc,argv,nil,NSStringFromClass([HelloWorldAppDelegate class]));
}
```
此@autoreleasepool声明支持Automatic Reference Counting(ARC)系统。ARC为应用提供自动化的对象生命周期管理。对UIApplicationMain的调用创建一个UIApplication实例以及一个application delegate实例(本例中为HelloWorldAppDelegate)。应用delegate的主要功能是提供绘制应用内容的窗口。

对UIApplicationMain的调用也会扫描应用的Info.plist文件。Info.plist文件是一个属性列表，它包含诸如应用名称、图标等信息。
可在工程中查看此属性列表：

<img src="/images/posts/2018-07-18/Info_plist.png">
因为选择在工程中使用storyboard，Info.plist文件也包含应用对象应当加载的story file的名字。一个*storyboard file*包含一个存档文件，此文件内含对象、过渡以及定义应用用户界面的连接。

在此应用中，storyboard文件被命名为Main.storyboard。当应用启动时，加载此文件，并且实例化其内的initial视图控制器。

为查看storyboard文件，在项目导航器中选择Main.stroyboard；Xcode在编辑域打开故事板(storyboard)文件。打开默认故事板文件，项目窗口应与下图类似：

<img src="/images/posts/2018-07-18/defaultStoryboardFile.png">
故事板文件包含场景(scenes)和segues。一个*scene*代表一个视图控制器，它管理一个内容区域；一个*segue*代表两个场景间的过渡。

因为Single View App模板仅提供一块占据整个屏幕的内容区域，所以app的Main.stroyboard文件仅含有一个场景且不含segue。画布中指向场景左侧的箭头是*initial scene indicator*，它确定app中的初始场景。

因应用中的HelloWorldViewController对象管理此画布中的场景，所以它被命名为Hello World View Controller Scene。Hello World View Controller场景包含显示在Xcode *outline view*的几个条目，即夹在画布与项目导航器中间的这块面板。
- *first responder*是一个动态占位符对象，它代表第一个收到事件的响应者对象，这些事件包括键盘、手势变化以及动作消息等。在本指导中，你不会利用第一响应者做些什么，但是可以在 Event Handling Guide for iOS 中学习更多有关的内容。
- HelloWorldViewController对象由一个内空的黄色球形表示。当故事板文件中的某个场景被加载时，它便创建一个视图控制类的实例。
- 还有一个View。这个view的白色背景即在模拟器运行应用所看到的现象。

在设置过程，应用对象执行以下任务：
- 加载主故事板文件。
- 从应用delegate处获取窗口对象(*window object*),或者创建一个UIWindow实例并把它与应用delegate关联起来。
- 实例化故事板的初始视图控制器，并将其委任为窗口对象的根视图控制器。

当应用对象完成这些任务后，它发送一个*application:didFinishLauchingWithOptions:*消息给它delegate。此消息给此delegate一个执行其它任务的机会，比如应用显示前的额外配置。

在本指导中，应用委托由Single View App模板提供，它的名字是HelloWorldAppDelegate。你不会改变此工程内的默认应用委托(delegate)类，但是你会在随后的步骤中使用委托(delegation)。你可在Delegation Helps You Add Functionality Without Subclassing中学到更多关于委托的内容。

## 1.3 回顾
---
在本章，利用Xcode创建了一个基于Single View模板的新工程。接着，构建且运行了模板定义的默认应用。也查看了工程的一些基本组成，比如main.m源文件、Info.plist文件以及故事板文件；学习了一个应用是如何起动的。下一章，将学习遍及iOS应用开发中的设计模式以及如何将它们适用于自己的app。

# 2 理解基本的设计模式
在开始定制app之前，花些时间学习正在从事的编程环境以及塑造伟大iOS应用的设计模式。即便不读此章节，也可完成本指导；但是如果花时间读一读、想一想它描述的概念，会对iOS应用开发有个更好的理解。
## 2.1 Cocoa Touch提供用于iOS开发的框架
---
Cocoa Touch是所有iOS app的应用环境；Cocoa为Mac OS X apps提供应用环境。随着为iOS开发应用，会依赖Cocoa Touch提供的面向对象框架。尤其是，会用到UIKit框架的应用编程接口(API)，它提供app需要构建、管理用户界面的所有类。

有很多资源可帮助获取对Cocoa Touch(或Cocoa)更深层次理解。一个开始旅程的好地方是Cocoa Fundamentals Guide。
## 2.2 Delegation帮助添加功能而无需子类化
---
Delegation是一种设计模式，在此模式内，一个对象发送消息给另一个对象以获取输入或者通知某事件正在发生，此另一个对象被指定为前者的delegate。此委托对象以一种特定的方式响应这些消息。delegation是Cocoa Touch应用中的一种常见设计模式，因为它允许开发者无需继承或重写某复杂框架对象便提供某些定制行为。

委托方法被聚集进一款协议(*protocol*)。一条协议基本上是一个方法列表。如果某类遵循一条协议，它必须保证实现此协议中的必要(required)方法(协议也可以包含一些可选[optional]方法)。一条委托协议列举了一个对象可能给它的委托发送的全部消息。比如，UIAppliactionDelegate协议列举了UIApplication对象可能发送给委托的所有消息，其中有*application:didFinishLauchingWithoutOptions:*。

本指导中的应用以两种方式使用委托：
- 每个iOS应用必须有一个application委托对象；在此应用，它是由Single View App模板提供的HelloWorldAppDelegate类的一个实例。除了执行定制的配置任务，应用委托可处理application级别的事件，因为它参与响应者链(responder chain)。在本指导中，不必执行特定的配置任务或处理任何app级别的时间，因此无需对HelloWorldAppDelegate源文件做任何修改。
- 当用户结束键入内容时，稍后添加的text field需要告诉它的委托。为帮助实现此目的，需确保视图控制器对象(即HelloWorldViewController)遵循*UITextFieldDelegate*协议。

<br/>
## 2.3 Model-View-Controller使代码更易于编写及重用
---
Model-View-Controller(MVC)设计模式为应用中的对象定义了三种角色。

**Model**对象代表数据，比如游戏中的太空船、日历app中的待办事项或者绘画app中的各种形状。

在这个应用中，model对象非常简单——它只是一个持有用户输入名字的字符串。因为这个字符串仅在一个方法中用到，严格来说，可不把它看作model对象；但是，以model待之帮助开发者适应MVC设计模式。

**View**对象清楚如何显示数据以及允许用户编辑数据。

在这个应用中，需要一个主视图来包含三个其它视图：一个text field从用户处获取输入，一个label显示用户输入的文本，以及一个用户点击的按钮。

**Controller**对象在model与view间居中调度。

在这个应用中，视图控制器对象从输入text field(view)获得数据，将其存储在一个字符串(model)内，随即更新label(另一个view)。此更新开始于按钮发送的一条动作消息。
## 2.4 Target-Action模式支持对用户交互的响应
---
target-action机制允许一个控件对象发送给其它对象一个有意义的消息来响应用户发起的事件，比如一次点击按钮。例如，考虑一个存储用户的联系人app：当用户点按Add Contact按钮，此按钮发送“add contact”消息(*action*)给一个定制的应用对象(*target*)，此对象清楚怎样将联系人添加到联系人列表。

在这个应用中，当按钮被点击，它发送一个动作消息给控制器(*target*)，告诉它根据用户的输入更新其model和view。
## 2.5 其它的设计模式
---
除了委托、MVC和target-action，Cocoa Touch也使用一些其它的设计模式。稍后，当你读完此本指导后，应当学习这些设计模式以便将它们应用到自己的app开发中。可在Cocoa Design Patterns中获得对这些设计模式的综述。

理解Cocoa设计模式使利用Cocoa Touch提供的多种技术变得容易，也可以将习得的技能用于它处。沿用这些模式也意味着你的代码能更好地利用这些对应用框架的增强。

# 3 检查视图控制器及其视图
如前文学到的，一个视图控制器负责管理一个场景，此场景代表一个内容区域。在此区域看到的内容定义在视图控制器的视图(view)中。在本章节，可以凑近看视图控制器以及学习如何调整视图的背景颜色。
## 3.1 利用Inspector检查视图控制器
---
当应用起动时，主故事板文件被加载以及initial视图控制器被实例化。此初始视图控制器管理用户打开应用看到的第一个场景。因为Single View App模版仅提供了一个视图控制器，故而它被自动设置为初始视图控制器。可利用Xcode inspector核实视图控制器的状态。

打开inspector...

1.如有必要，在工程导航器单击Main.stroyboard在画布中显示场景。

2.在大纲视图中选中Hello World View Controller。工程窗口应与下图类似：

<img src="/images/posts/2018-07-18/defaultStoryboardFile.png">
单击工具栏最右端的选项以在窗口右侧显示工具域。在工具域顶部，单击Attributes按钮。此时，工程窗口看起来应类似：

<img src="/images/posts/2018-07-18/defaultStoryboardFile_0.png">
在Attributes inspector，可看到"Is Initial View Controller"选项被选中了。如果取消选定此项，则初始场景提示器从画布消失。在本指导中，确保此"Is Initial View Controller"一直处于选中状态。
## 3.2 更改视图的背景颜色
---
在"2.2 查明应用如何启动"一节，了解到当于模拟器运行app时一个视图提供白色背景。为确保app在正确地工作，可将此视图的背景改为其它颜色而非白色，并且再次于模拟器运行app以验证新颜色是否显示。在改变视图的背景颜色前，确保主故事板文件仍处于打开状态。

为设置视图控制器视图的背景颜色...

1.在大纲视图，选中Hello View Controller下方的View。

2.单击工具域顶部的Attributes按钮来打开Attributes inspector。

3.在Attributes inspector单击Background的白色矩形框来打开Color窗口。

4.在Color窗口，选择一个非白色的颜色。

<img src="/images/posts/2018-07-18/colorBackground.png">
5.关闭Color窗口。
在模拟器测试应用。它应当像这样：

<img src="/images/posts/2018-07-18/redBackgroundApp.png">
## 3.3 回顾
---
在本章，检查了场景，也改变了视图的背景颜色。在接下来的一章，给视图添加控件。

# 4 配置视图
Xcode提供一个对象库，可以把其中的对象添加到故事板文件。有些对象是属于视图的用户界面元素，比如按钮和文本域(text field)；其它的则是更高层级的对象，比如视图控制器和手势识别器。

Hello World View Controller场景已经包含一个视图，现在向它添加一个按钮、一个标签以及一个文本域。接着，建立这些元素和视图控制器类之间的联系以便这些元素能提供咱们想要的行为。
## 4.1 添加用户界面元素
---
从对象库拖拽对象到画布中的视图，即可完成添加UI元素的目的。将它们放置于视图后，可以移动它们或者调整大小。

为向视图添加UI元素：

1.如有必要，在工程导航器选择Main.storyboard，将场景显示在画布中。

2.如有必要，打开对象库。对象库在工具域的底部。

3.在对象库搜索框，选择需要的控件。每次一个，分别将文本域、按钮以及标签拖拽到视图。

4.在视图，合理布局这些控件。在视图内移动控件时，蓝色虚线可帮助排列它们。

5.在视图，选中文本域内到文字，输入Your Name;选中标签，输入result；双击按钮，输入文本Hello。

<img src="/images/posts/2018-07-18/addControlsDone.png">
还需要一些对文本域做些其它改动，以便符合用户期望。首先，因为用户将输入他们的名字，你可以确保iOS建议每个单词大写化。其次，可将与文本域相连的键盘配置为输入名字(而非数字)，且此键盘显示一个Done按钮。

这些改动背后的一个基本原则是：在设计期，你已经清楚文本域需填充的内容，所以加以限制，如此运行期的应用表现更能符合用户的期望。可在Attributes inspector完成这些改动。

为配置文本域：

1.在视图，选中文本域。

2.在Text Field Attributes insepctor，做以下选择：
- 在Capitalization选择Words。
- 确保Keyboard Type设置为Default。
- 在Return Key选择Done。

<img src="/images/posts/2018-07-18/configureTextField.png">
## 4.2 为按钮创建action
---
依照在"2.4 Target-Action模式支持对用户交互的响应"学到的，当用户激活某个UI元素时，此元素可发送动作消息给一个对象，这个对象清楚如何执行对应的动作方法。在本指南中，当用户点击按钮时，你想它发送一条"change the greeting"消息给视图控制器。接着，你想视图控制器执行对应的"change the greeting"方法来响应它，此方法改变显示在标签上的文本。

为按钮添加动作...

1.如有必要，在工程导航器选择Main.storyboard，将场景显示在画布中。

2.在工具栏，单击Utilities按钮来隐藏工具域，单击assistant编辑器按钮来显示Assistant编辑器面板。

3.确保Assistant显示了视图控制器头文件(HelloWorldViewController.h)。

4.在视图选中按钮，按住Control键从它处拖拽到头文件的方法声明区域。当Control-drag时，能看到：

<img src="/images/posts/2018-07-18/createActionForButton_0.png">
释放Control-drag后，Xcode显示一个弹出框，在其中可配置刚做出的动作连接：

<img src="/images/posts/2018-07-18/createActionForButton_1.png">
单击Connect。

如此，便在头文件得到了对此方法对声明：
``` Objective-C
- (IBAction)changeGreeting:(id)sender;
```
以及在实现文件得到一个此方法的存根：
``` Objective-C
- (IBAction)changeGreeting:(id)sender {

}
```
其中，IBAction是一个特殊关键字，用来告诉Xcode将此方法当作对target-action连接的动作。IBAction被定义为void。方法中的sender参数指的是发送动作消息的对象，在本指导中，此sender是按钮。

如此，在按钮与视图控制器间创建了一个连接。建立此连接等同于“在按钮上调用addTarget:action:forControlEvents:”，其中，target是视图控制器，动作为changeGreeting:选择器，以及包含Touch Up Inside的控制器事件。

下一步，在视图控制器与两个剩下的UI元素(即标签和文本域)间建立连接。
## 4.3 为文本域和标签创建outlet
---
**outlet**是指向其它对象的属性。当在Xcode创建一个outlet连接时，此连接会被存档在故事板文件内；当应用运行时，此连接被恢复。被恢复的连接允许两个对象在运行时相互通信。

为添加outlet采取的步骤与添加action很类似。在开始之前，确保：主故事板文件仍在画布中可见，HelloWorldViewController头文件仍在Assistant编辑器处于打开状态。

为文本域添加outlet...

1.在视图选中文本域，按住Control键盘从它处拖拽到头文件的方法声明区域。当Control-drag时，能看到：

<img src="/images/posts/2018-07-18/createOutletForTextField_0.png">
<img src="/images/posts/2018-07-18/createOutletForTextField_1.png">
在弹出框，点击Connect。通过向文本域添加outlet，此完成了两件事：
- 向视图控制器类添加了适当的代码。特别是，向头文件添加了以下声明：
``` Objective-C
@property (weak,nonatomic) IBOutlet UITextField *textField;
```
此处的IBOutlet也是一个特殊关键字，用来告诉Xcode将此对象看作一个outlet。实际上，它被定义为nothing，因此它在编译期没什么影响。
- 建立一条从视图控制器到文本域的连接。建立此连接等同于对视图控制器调用setTextField:，将文本域作为参数。

同理，为标签添加一个outlet并配置之。配置完成后，在Connection inspector，Xcode展示了选中对象的所有连接，如图所示：

<img src="/images/posts/2018-07-18/addActionOutletDone.png">
## 4.4 准备文本域的委托连接
---
在app内，还有一个要完成的连接：需要把文本域和某个对象连接起来，此对象为文本域的委托。在本指导中，利用视图控制器作为此文本域的委托。

需要为此文本域指定一个委托对象，因为当用户点按键盘上的Done按钮时，文本域向它的委托发送一条消息。在随后的步骤中，用与此消息关联的方法来dismiss键盘。

确保在画布的故事板文件仍处于打开状态。如未，在工程导航器打开Main.stroyboard。

为设置文本域的delegate...

1.在视图选中文本域，按住Control键从此处拖拽到视图控制器(内空的黄色球形)。

2.释放Control-drag后，选择Outlets处的delegate。

<img src="/images/posts/2018-07-18/createDelegateForTextField.png">
## 4.5 测试应用
---
单击Run测试app。应发现单击文本域时，键盘出现并可输入文本。然后，没有办法使其消失。为达成此目的，需要实现相关的委托方法。在下一章节完成此部分功能。
## 4.6 回顾
---
当利用合适的连接配置视图时，也应更新实现(implementation)文件以支持outlet和对应的动作。在此刻，HelloWorldViewController.m文件应当显示为：

``` Objective-C
#import "HelloWorldViewController.h"

@interface HelloWorldViewController ()

@end

@implementation HelloWorldViewController

@synthesize textField;
@synthesize label;
@synthesize userName;
@synthesize helloButton;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view,
    // typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)changeGreeting:(id)sender {

} 
  
@end
```
<br/>
# 5 实现视图控制器
这有几个需要实现视图控制器的部分：为用户名添加一个属性，实现changeGreeting:方法，以及确保当用户点击Done时键盘会消失。
## 5.1 为用户名添加属性值
---
需要为持有用户名的字符串声明一个变量，并且需要为它添加一个声明以便代码能时常引用它。添加此二者给视图控制器头文件(HelloWorldViewController.h)。

到此刻为止，已不必对故事板文件再做进一步的改动了。为给写代码腾出更多空间，可以把工具域隐藏起来。

为给用户名添加变量及属性声明...

1.在工程导航器，选择HelloWorldViewController.h。

2.在@interface的大括号内，为持有用户名的字符串添加一个变量声明。注意，可能需要在@interface声明后手动输入一对大括号。添加变量后的形式为：
``` Objective-C
@interface HelloWorldViewController : UIViewController {
	NSString *userName;
}
```
3.在右闭括号之后及@end声明之前，为刚声明的字符串变量添加一个@property声明。
``` Objective-C
@property (nonatomic,copy) NSString *userName;
```
这个userName属性需要一个称为setUserName:的setter方法。在下一步提供此方法。

为完成userName属性的实现，需要告诉编译器合成对应的accessor方法。在视图控制器的实现文件(HelloWorldViewController.m)提供相应的代码。

为给用户名属性合成accessor方法：

1.在工程导航器，选择HelloWorldViewController.m。

2.在@implementation HelloWorldViewController线之后，添加如下代码：
``` Objective-C
@synthesize userName = _userName;
```

当使用@synthesize指令时，你在告诉编译器去为头文件中指定的属性合成accessor方法。在本指导中，userName属性的声明是@property (nonatomic,copy) NSString userName;,因此编译器生成两个accessor方法：
``` Objective-C
- (NSString *)userName;
- (void)setUserName:(NSString *)newUserName;
```
通过在@synthesize代码行给userName添加一个下划线，便告诉编译器使用带下划线的userName作为userName属性的实例变量。

按照惯例，给实例变量加一个下划线前缀是为了提醒开发者不应直接访问一个实例变量。相反，应使用已合成的accessor方法。一个例外之处是，可以在init方法内直接获取或设置实例变量。

从学术角度看，避免对实例变量的直接访问可帮助维持**encapsulation**(封装)，但也是有其它实用价值的：
- 一些Cocoa技术(尤其是key-value coding)依赖对accessor方法的使用，及accessor方法的恰当命名。如不使用accessor方法，你的应用也许不能充分利用标准Cocoa特点的优势。
- 有些属性值是按需创建的。如果你视图直接使用实例变量，可能会得到nil或者一个未初始化的值。比如，视图控制器的视图属性便是按需创建的好例子。

为加深记忆此惯例，修改Xcode为文本域和标签添加的@synthesize代码行，使之变为：
``` Objective-C
@synthesize textField = _textField;
@synthesize label = _label;
```
<br/>
## 5.2 实现changeGreeting:方法 
---
在"To add action for the button"部分，配置了按钮以便“当用户点击它时，它发送一条changeGreeting:消息给视图控制器”。作为响应，你想视图控制器在标签上显示用户在文本域输入的文本。特别地，changeGreeting:应当：
- 从文本域取回字符串，并将视图控制器的userName属性设为此字符串。
- 基于userName属性创建一个新字符串，并将其显示在标签上。

为实现changeGreeting:方法...

1.如有必要，在工程导航器选择HelloWorldViewController.m。

2.通过添加以下代码，完成changeGreeting:方法的存根实现：
``` Objective-C
- (IBAction)changeGreeting:(id)sender {

	self.userName = self.textField.text;
	NSString *nameString = self.userName;
	if([nameString length] == 0) {
		nameString = @"World";
	}

	NSString *greeting = [[NSString alloc] initWithFormat:@"Hello, %@",
	                      nameString];
	
	self.label.text = greet;

}
```
<br/>
## 5.3 配置视图控制器为文本域的委托
---
如果构建运行应用，应当发现当单击按钮时，标签显示"Hello,World"。如果你选择文本域开始键入内容，应当发现仍然没有办法暗示已经完成输入且dismiss键盘。

在iOS应用中，当一个允许文本输入的界面元素变为第一响应者后，键盘会自动显示；当此元素失去第一响应者状态后，键盘又被自动dimiss。虽然没办法直接给键盘发消息，但是可以切换某个文本输入元素的第一响应者状态，此带来的另一个作用便是键盘显示或消失。

UITextFieldDelegate协议包括textFiledShouldReturn:方法；当用户点击Return按钮时，文本域便调用此方法。因为你设置视图控制器为文本域的委托，所以你可实现此方法，通过向它发送一条resignFirstResponder消息来强制其失去第一响应者状态。

为配置HelloWorldViewController为文本域的委托...

1.如必要，在工程导航器选择HelloWorldViewController.m。

2.实现textFieldSholdReturn: method。此方法应告诉文本域放弃第一响应者状态。
``` Objective-C
- (BOOL)textFieldShouldReturn:(UITextField *)textField {

    if(textField == self.textField) {
        [textField resignFirstResponder];
    }
    return YES;
}
```
3.在工程导航器选择HelloWorldViewController.h。

4.在@interface行，恰左开括号前并且UIViewController后，添加<UITextFieldDelegate>。所以接口文件应为：
``` Objective-C
@interface HelloWorldViewController : UIViewController <UITextDelegate> {
	...
}

@end
```
此声明代表HelloWorldViewController类采纳UITextFieldDelegate协议。
## 5.4 测试应用
---
构建且运行应用。这次，它如你期望。在模拟器，输入Your Name后，单击Done以解除键盘；随即单击Hello按钮，以在标签上显示“Hello,Your Name !”。

如果应用的行为未如预期，则需要做些故障排查。
## 5.5 回顾
---
既然已经完成了视图控制器的实现，便完成了第一个iOS应用。恭喜！花点时间仔细想想视图控制器是如何融入整体应用结构的,因为在将来编写的所有iOS应用中都会使用视图控制器。

# 6 接下来
在本指导中，有创造了一个非常简单的iOS应用。虽然Cocoa Touch提供了一个丰富的开发环境，但是本指导只是浮光掠影。随着继续学习iOS应用开发，本章建议了一些你可以采纳的下一步方向。
## 6.1 提高用户界面
---
iOS用户对运行在基于iOS设备的app外观及感觉有很高的期望。随着努力提高app的UI时，可以考虑给UI元素添加图像或颜色，给视图添加一个背景图像。

很多iPhone应用支持多个方向；并且iPad应用应支持所有方向。提告app UI的其它方式可以是确保视图控制器可以处理关于方向的变化。为支持不同方向，首先学习shouldAutorotateToInterfaceOrientation:方法。接着，在故事板文件调整UI以确保：当视图发生旋转时，可以恰当地放置其中的UI元素。

一定读一读iOS Human Interface Guidelines，学习设计app用户界面及使用体验的官方推荐方式。
## 6.2 安装应用到设备上
---
如果你有一个连接到电脑的合适设备并且有一个来自iOS Developer Program的有效证书，可为iPhone设备设置有效的SDK，构建并运行此工程。假如代码可成功编译，Xcode随即自动降app上传到iPhone设备。为了解这部分开发过程，参见Developing for the App Store。
## 6.3 添加更多功能
---
也可以扩展此app的功能。这有可以走的几个方向：
- **Write a custom view.**可以尝试编写一个定制视图，它绘制自身的内容以及响应接触事件。为寻求灵感，可查看样例代码工程，比如MoveMe和Metronome。
- **Use a table view.**虽然使用Xcode来为此应用布局用户界面，但是很多应用利用表格视图来布局界面。应首先调查如何利用表格视图创建一个简单列表。这有很多样例代码工程，比如TableViewSuite；可以将这些样例代码作为某个定制app的基础。
- **Use a navigation controller.**导航控制器和标签栏(tab bar)控制器可以提供组织iOS应用的不同方式。导航控制器经常与表格视图联合使用，但是导航控制器和标签栏控制器都可以与其它控制器合作。可以一窥一些使用导航控制器的实例代码工程(像SimpleDrillDown)，扩展它们来创建自己的applocation。
- **Localize your app.**通过本地化application，可以增长潜在市场的数量。国际化是本地化application的过程。为学习国际化的更多细节，见Internationalization Programming Topics。
- **Optimize your code.**高性能对iOS上的美好用户体验至关重要。应当学习使用Xcode提供的各种性能工具(尤其是，Instruments)来优化application，以便它能最小化资源使用量。

最最重要的事务是尝试新想法以及多做实验。官网有很多可以寻求灵感的代码样例，也有帮助理解概念和编程接口的文档。

## 6.4 添加单元测试
---
当创建本指导中的工程时，并未选择“Include Unit Tests”。当开发真正的应用程序时，包含测试是非常重要的。在某些方面，可将测试当成封装设计原则的映射(counterpart)：测试确保如果一个方法的实现细节变化了，此方法是否仍能像通告般工作。

当向app添加测试时，可创建一个选择了"Include Unit Tests"功能的新版本工程；也可以利用当前工程，选择File->New->Target,然后选择：

<img src="/images/posts/2018-07-18/unitTestingBundle.png">

# 7 代码列表
此附录部分提供HelloWorldViewController类接口与实现文件的列表。在viewDidLoad方法中为按钮设置了圆角、边框颜色及宽度，为标签设置了圆角。
## 7.1 接口文件: HelloWorldViewController.h
<img src="/images/posts/2018-07-18/HelloWorldViewControllerHeader.png">
## 7.2 实现文件: HelloWorldViewController.m 
<img src="/images/posts/2018-07-18/HelloWorldViewControllerImple_0.png">
<img src="/images/posts/2018-07-18/HelloWorldViewControllerImple_1.png">
<img src="/images/posts/2018-07-18/Developer.png">
