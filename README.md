# bingo90
Ticket Generator

Prerequisites:<br />
Java 8<br />
maven<br />

Run:<br />
directly from IDE<br />

or

mvn clean install<br />
mvn exec:java<br />

The parameters can be changed from main():<br />
The second parameter represents the number of generated stripes, and the third parameter is a bollean used to print or not print the result.<br />
buildTickets(bingoService, 1, true);<br />

IDE(intellij) performance:<br />
One ticket is generated in ~ 10/12 milliseconds<br />
After printing, total time is ~ 14/15 milliseconds<br />


10.000 tickets will be generated in ~ 600 milliseconds<br />
After printing, total time is ~ 2000 milliseconds (console print took a lot of time)<br />

I noticed mvn run is slower for 1 element<br />

