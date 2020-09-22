---
layout: post
title: Developing Applications for iOS 之UITableView,iPad
---
{{page.title}}
=========================
<img src="/images/posts/2019-01-04/Stanford_CS193p.png"> <br/>
Let's dive into UITableView.This is a very important class,because a lot of times the data you need to display in your applications comes in the form of a list.And TableView is basically a list view.It's a list of terms.

<img src="/images/posts/2019-01-04/UITableView_intro.png"> <br/>
<img src="/images/posts/2019-01-04/UITableView_Dynamic_Static.png"> <br/>
<img src="/images/posts/2019-01-04/UITableView_Sections_Or_Not.png"> <br/>
<img src="/images/posts/2019-01-04/UITableView_Cell_Type.png"> <br/>
<img src="/images/posts/2019-01-04/UITableView_Protocols_intro.png"> <br/>
<img src="/images/posts/2019-01-04/UITableViewDataSource_intro.png"> <br/>
<img src="/images/posts/2019-01-04/UITableViewDataSource_get_UITableViewCell.png"> <br/>
In a **static** table,you do not need to implement this method (though you can if you want to ignore what's in the storyboard).`NSIndexPath` is just an object with two important properties for use with `UITableView` row and section.The purpose of this method is get a cell to use (intance of `UITableViewCell`) and set @propertys on the cell to prepare it to display. <br/>

**@“Flickr Photo Cell”** MUST match what is in your storyboard if you want to use the prototype you defined there ! `dequeueReusableCellWithIdentifier` The cells in the table are actually reused.When one goes off-screen, it gets put into a “reuse pool.” The next time a cell is needed, one is grabbed from the reuse pool if available.If none is available,one will be put into the reuse pool if there’s a prototype in the storyboard.Otherwise this dequeue method will return nil.And `cell.textLabel.text = ` are obviously other things you can do in the cell besides setting its text (detail text, image, checkmark, etc.).<br/> 

<img src="/images/posts/2019-01-04/UITableViewDataSource_count_of_rows_sections.png"> <br/>
<img src="/images/posts/2019-01-04/UITableViewDataSource_other_methods.png"> <br/>
<img src="/images/posts/2019-01-04/UITableViewDelegate_intro.png"> <br/>
<img src="/images/posts/2019-01-04/UITableView_Target_Action.png"> <br/>
<img src="/images/posts/2019-01-04/UITableView_Detail_Disclosure.png"> <br/>
<img src="/images/posts/2019-01-04/UITableViewDelegate_other_methods.png"> <br/>
<img src="/images/posts/2019-01-04/UITableView_Segue.png"> <br/>
<img src="/images/posts/2019-01-04/UITableView_Spinner.png"> <br/>
<img src="/images/posts/2019-01-04/UITableView_Model_changes.png"> <br/>
