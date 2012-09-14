uxbridge-samples
-------------------
This project contains applications which should be used in connection with the
FirstSpirit module "UX-Bridge" which is developed by the e-Spirit AG. 
The head office of the e-Spirit AG is in Dortmund, Germany.

The uxbridge-samples project contains the following subprojects:
- newsDrilldown
  - adapter
    - hibernate
    - mongo
  - grailsapp
- newsWidget
  - adapter
    - hibernate
    - mongo
  - grailsapp
  
The adapters:
-------------
The adapters are listening on a JMS message bus for messages. If they receive a 
message on their listening socket they will proccess it and write the data
into a database.
Furthermore the adapters are able to receive delete messages which will delete specific
data records.
Another implemented command is the cleanup command which deletes all data records which
are older than a given date.

The adapters can be build with the maven command:
mvn package

The war file is placed in the target/ folder of the adapters. Just deploy it to a tomcat and they will
listen on the port you configured in the applicationContext.xml 

The Grails Apps:
----------------
The Grails Apps provide the web applications which will deliver dynamic content.  

The war-files for a tomcat deployment can be build with the command:
grails prod war

NewsDrilldown:
--------------
The newsDrilldown application shows News Articles filtered for their
categories (e.g. Tennis, Soccer, Basketball) and metaCategories (e.g. Sports).
The filtering is realized with help of a drilldown menu on the column side of the website.
The static generated headers and footers are integrated with the help of a http request.

NewsWidget:
----------
The newsWidget application shows a small widget on a page which lists the latest news articles.
If a new article gets written into the database the widget will update the view immediately
to show the newest article. This gets with the help of AJAX requests. 
  

Attention:
----------
Git doesn't push empty folders.
The uxbridge-samples grails applications dont't use the folder grails-app/migrations
so it is empty.
Grails will produce an error when running "grails war" on this application.

In order to prevent this you have to create the folder grails-app/migrations in both 
grails applications in the uxbridge-samples project.

