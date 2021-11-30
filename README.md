# bingo90
Ticket Generator

Prerequisites:
Java 8
maven

Run:
directly from IDE

or

mvn clean install
mvn exec:java

The parameters can be changed from main() :
The second parameter represents the number of generated stripes, and the third parameter is a bollean used to print or not print the result.
buildTickets(bingoService, 1, true);

IDE(intellij) perfrmance
one ticket is generated in ~ 10/12 milliseconds 
after print total time is - 14/15 millisecond

10.000 tickets will be generated in ~ 600 milliseconds
after print total time is - 2000 milliseconds (console print took a lot of time)

I noticed mvn run is slower for 1 element
