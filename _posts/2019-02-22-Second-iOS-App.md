---
layout: post
title: 第二个iOS App
---
{{page.title}}
==========================

<video width="480" height="360" controls="" preload="none"><source src="/images/posts/2019-02-22/iOS_App.mp4" type="video/mp4"></video>
平日闲暇时分，总爱翻几页闲书，以致闲书越来越多，所以便想着写这么一个简单的app作整理之用。我们了解，app运行时数据存储在内存，重启后，数据便丢失了，所以需基于(数据库)文件进行增、删、改、查等操作。由此，我选取熟悉的SQLite。

<img src="/images/posts/2019-02-22/iOS_App_00.png">
<img src="/images/posts/2019-02-22/iOS_App_01.png">
就目前而言，关于一本书，我考虑：(2)书名、(3)作者、(4)我给它的评分、(5)作者简介、(6)书本的内容简介以及(7)看完之后给它的小结。以上7项在数据库文件内即一条记录而已，既是记录，就需分“第几条”，所以再加(1)bookInfoID项。由此，创建一个数据库文件booksListDb.db并新建一张表bookInfo，完成后，将其添加到工程内。

<img src="/images/posts/2019-02-22/SQLiteForBookInfo.png">
利用工程中的DBManager.h及.m文件完成对数据库部分的封装，对外部提供的(接口/可见)功能有：(1)对数据库文件的初始化，包括文件名及所在目录；(2)针对app首页，需从数据库获取全部数据以显示于首页；(3)针对添加页及在首页点击各项，需提供增加、修改的功能。对外部提供的属性可以是：(1)数据表中各列名称；(2)被操作的行记录；(3)最后插入的行记录。

<img src="/images/posts/2019-02-22/DBManagerHeader.png">
关于初始化：首先，我想保存它的文档路径及数据库文件名称；其次，根据App内部结构，数据库文件应存于文档目录内，所以应执行一次拷贝操作。DBManager头文件中的loadDataFromDB:与executeQuery:方法之区别在于是否返回数据，所以(1)我要保存此返回的数据用于显示主页内容(2)将此两个方法并为一个，根据参数判断执行对应功能即可。由此，便得到了3个私有属性以及2个私有方法。

<img src="/images/posts/2019-02-22/DBManagerImple_00.png">
<img src="/images/posts/2019-02-22/DBManagerImple_01.png">
<img src="/images/posts/2019-02-22/DBManagerImple_02.png">
<img src="/images/posts/2019-02-22/DBManagerImple_03.png">
创建Xcode工程时，我选择的"Single View Application"模板，但是我想此应用为基于导航的app，所以选中Main.storyboard的Personal Library Scene后执行**Editor > Embed In > Navigation Controller**。随即一个Navigation Controller被添加到场景的左侧，并且一个箭头从此Navigation Controller指向Personal Library Controller。

向Personal Library添加子视图：从Objects库拖拽一个UITableView到场景；拖拽一个UITableViewCell到table view并将之设为prototype，**Style**选Subtitle,**Identifier**设为idCellRecord,**Selection**定为None以及将**Accessory**确定为 Detail Disclosure，即每条右端的">"效果；拖拽一个UIBarButtonItem到导航条的右侧，将**Identifier**选为Add,即有右上角的"+"符号。除此之外，设置Navigation Item的属性：

<img src="/images/posts/2019-02-22/iOS_App_02.png">
接着，从Objects库拖拽一个UIViewController对象到canvas。针对此控制器，我需要创建一个新类。所以，将目光转到Xcode的工具条，执行**File > New > File ...**。创建一个名为**EditBookInfoViewController**的**UIViewController**子类。现在，将刚拖拽到画布的UIViewController对象的类属设定为EditBookInfoViewController,

<img src="/images/posts/2019-02-22/iOS_App_03.png">
我需要EditBookInfoVC充当(1)展示每本书具体内容及(2)添加一本新书的角色，所以需要建立从Personal Library Controller到Edit Info Controller的连接，利用鼠标选中起点，按住control拖拽到目的地即可，选择连接二者的**Storyboard Segue**类型为Show(e.g.Push)。

<img src="/images/posts/2019-02-22/iOS_App_04.png">
接下来，为EditBookInfoVC添加需要的子视图以及拖拽一个UIBarButtonItem到导航条的右侧，将**Identifier**设为Save。为需要获取(显示)的内容设置对应的属性，为Save设置IBAction方法，并进行关联。在Xcode进入Assistant editor后，左侧显示画布，右侧显示程序文本；左侧选中控件，利用鼠标拖拽到右侧的文本，即可完成连接！

<img src="/images/posts/2019-02-22/EditBookInfoHeader.png">
在此头文件中，不仅有控件对应的属性及IBAction，还有声明的一条protocol及采用此协议的delegate，以及一个recordIDToEdit(int型)变量。此三者意欲何为？还得细说从头！

利用BookList页显示全部的书目列表，此时需要应对的动作有：(1)点击+，到添加新书的页面；(2)点击每项右侧的">"以显示当前所选项目的全部信息；(3)删除某条目；(4)在列表页移动条目的位置(将某作者的全部作品放在一起、将某一类书目置于一起等)。针对第(3)、(4)项，在**viewDidLoad**方法内将self设为tableView的delegate，随即实现**UITableViewDelegate**内对应的方法即可。针对(1)、(2)项，此二者情境应用同一个ViewController，所以启动EditBookInfoVC时，我附加一个参数recordIDToEdit以区分两种情境。

据前文，每本书都有一个bookInfoID。如此，执行(2)时，将bookInfoID赋予recordIDToEdit；执行(1)时，因是新建，所以将recordIDToEdit设为-1。EditBookInfoVC得到的recordIDToEdit非-1时，则据此值查询数据库；若为-1，则准备接收用户的输入。

那“声明的一条protocol及采用此协议的delegate”又是怎么一回事？在新书内容编写完毕，点按Save后回到首页，却看不到**刚刚编辑并已保存的**内容，但是它确实已被存入数据库；而且在重启app后，可以看到**刚刚编辑并已保存的**内容条目。这就牵涉到了UITableView显示dataSource的机制，当重新加载时，调用UITableView的reload方法即可。所以，在saveInfo：方法内，退回到首页前，向自己的delegate发送一条"已编辑完成"的消息。此时的delegate自然就是BookListViewController了。其实，就**委托**而言，我定义一个采用某**协议**的委托，而后向此委托发送包含于**协议**的消息即可；我不关心**委托**是什么类，在整个系统充任什么角色。所以，这也是**协议**的美妙之处，只涉及功能而不涉及类。就语言特性而言，**Objective-C**协议等价于**Java**接口；事实上，**Java**的设计者借用了**Objective-C**中的概念。

<img src="/images/posts/2019-02-22/EditBookInfoImple_00.png">
<img src="/images/posts/2019-02-22/EditBookInfoImple_01.png">
前文提到EditBookInfoVC有一个delegate变量，又说BookListViewController就是这个delegate。那两者如何关联起来呢？**为之奈何**的对策：BookListViewController采用EditBookInfoViewControllerDelegate协议，并在进入EditBookInfoVC之前将自身(**self**)设定为目的VC的delegate(值)即可，参见代码**prepareForSegue**部分。

<img src="/images/posts/2019-02-22/BookListHeader.png">
<img src="/images/posts/2019-02-22/BookListImple_00.png">
<img src="/images/posts/2019-02-22/BookListImple_01.png">
可看到BookListViewController头文件内容比较简单，仅有属性及IBAction方法各一个。(IBAction)addNewBook:对应“(1)点击+..."，而“(2)点击每项右侧的>”及(3)(4)项都得对属性**tableBook**做文章了。

“(2)点击每项右侧的>以显示当前所选项目的全部信息”对应UITableView委托的**- (void)tableView:accessoryButtonTappedForRowWithIndexPath:**方法；“(3)删除某条目”对应委托的**- (void)tableView:commitEditingStyle:forRowAtIndexPath:**方法。

<img src="/images/posts/2019-02-22/BookListImple_02.png">

此外，还需确定：TableView有几类section；总共有多少行；以及每行对应的cell内容是什么。此俱为**UITableViewDataSource**方法所涉及，所以，采用此协议，实现方法:

<img src="/images/posts/2019-02-22/BookListImple_03.png">
此外，在编辑页面的“内容简介”及“一言以蔽之”部分，控件比较靠下，当输入文本时，键盘会遮挡它们。所以，需要实现一个简单的功能：键盘出现时，页面View整体上移；输入完毕后，页面View整体下移至原状。

<img src="/images/posts/2019-02-22/EditBookInfoImple_02.png">





