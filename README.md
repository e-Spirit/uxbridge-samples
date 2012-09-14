Git doesn't push empty folders.
The uxbridge-samples grails applications dont't use the folder grails-app/migrations
so it is empty.
Grails will produce an error when running "grails war" on this application.

In order to prevent this you have to create the folder grails-app/migrations in both 
grails applications in the uxbridge-samples project.

