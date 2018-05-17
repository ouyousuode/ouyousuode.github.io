## 从学习一段代码说起
[浏览网页](https://www.raywenderlich.com/902/sqlite-tutorial-for-ios-creating-and-scripting)时发现一份来自2000年10月1日以来破产银行的名单，就以csv格式将其下载到本地，打开文件发现数据包含成行的由逗号隔开的内容，格式如Desert Hills Bank,Phonenix,A2,57060,26-Mar-10,26-Mar-10。得到数据后，我们想将其保存在SQLit e数据库中，以便其他应用访问。
```
create table failed_banks(name text,city text,stcode text,zip integer,close_date,update_date text)
```

然后将此csv文件导入，但不幸的是，通观整份名单，我们发现有些行的格式为"La Jolla Bank,FSB",La Jolla,CA,32423,19-Feb-10,24-Feb-10。"La Jolla Bank,FSB"的双引号意味着引号内的全部字符构成银行的名字。

可以用Python编码向数据库插入数据，Python有一个很简洁的称为string.split()的方法，可在split()中指定一个分界符，比如
```
a = "Unity National Bank,Cartersville,GA,34678,26-Mar-10,26-Mar-10"
a.split(",")
#结果为
['Unity Nation Bank','Cartersville','GA','34678','26-Mar-10','26-Mar-10']
```
然后将数组中的每项放进数据库表中对应的列内。但是，问题来了，如何处理"La Jolla Bank,FSB",La Jolla,CA,32423,19-Feb-10,24-Feb-10这种情况呢？特殊之处在于""内有逗号。正如前文所述，此双引号内的内容为一整体，不可分拆。文中的处理方式为

```
def mySplit(string):
    quote = False
    returnValue = [] # 数组
    current = "" # 临时存储空间
    for char in string :
        if char == '"' :
            quote = not quote
        elif char == ',' and not quote :
            # 检测到逗号，并且引号已处理，
            # 则把得到的部分加入到数组的某单元中；
            # 将临时存储空间清零
            returnValue.append(current)
            current = ""
        else :
            current += char
    #最后一部分肯定不带逗号，所以在此添加，随后返回即可
    returnValue.append(current)
    return returnValue
```
其代码逻辑是什么呢？
* 首先，双引号不作为提取的部分
* 其次，检测到左引号后，就要想着还有右引号，此双引号间的内容是不可分割的整体
* 最后，没遇到双引号或者双引号已处理的情况下，遇到逗号意味着此时得到的内容为一完整的单元

这段代码是为解决实际问题而写，日后碰到此类问题复用即可，节省时间。从实现过程看，编程是一件特别考验逻辑思维的脑力劳动，不仅要求编程人员有从大处着眼的大局观，而且要求有从小处入手的实现力。碰到此类未处理过的问题时，由于大脑数据库中没有现成的解决方案，慌乱之心自然而生，情况属于失控状态。所以，才有“事缓则圆”之说。

先让自己慢下来，把问题写清楚；当然要想写清楚，首先要想清楚。想清楚（1）当前问题是什么（2）限制条件有哪些（3）此问题的难点在哪（4）可能的解决方案有哪些......如此可将思维从一团乱麻中解脱出来。

以买卖房子为例，当前以100万购入房子一套，首付三成，以基准利率纯商业贷款25年，选择等额本息（支付利息逐年递减）的月供方式，2年后以130万卖出，盈利是30万吗？或者说期望利用此房两年内盈利30万，卖出价格应为多少？主要需考虑两方面的因素：
* 若不买此房，利用30万投资理财（保守如余额宝），年化利率以百分之4计，两年的收益约为2.4万。此为少挣的。
* 若买了此房，需支付利息，2年的利息约为6.7万。此为多花的。

所以，若想利用此房两年内获利30万，须以不低于139万的价格卖出。需多加应用此思维方式于决策场景中，若知而不行，岂不浪费此篇小文之精力。

知行合一，聊以自勉吧～