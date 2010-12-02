Eight Ball Demo
---------------

The Eight Ball Demo is a lighthearted application based on the Doug Tidwell's Magic 8-Ball demo (http://www.ibm.com/developerworks/webservices/library/ws-eight/) to demonstrate the Tuscany distributed domain support.

There is a eightball.jar SCA contribution which has a Java component that answers yes-no questions, and an eightball-test.jar which is a simple test harness to invoke the EightBall service from the command line. To make the demo more interesting the eightball gives the answers in German.

There is a translator.jar and contribution which has a component that can translate phrases between German and English, and a translator-test.jar for testing that at the command line. Presently the translator is just hardcoded with the phrases the EIghtball uses, later it would be good to enhance the translator to use one of Tuscanys bindings to call one of the remote translaotr services available on the internet.

There is a eightball-process.jar contribution which has a Java component which uses the translator and eightball services to translate phrases from English to German, ask the eightball the question, and then translate the answer from German to English. Ideally this would be rewritten using BPEL. And an associated eightball-process-test.jar to test it at the command line.

And finally there's an eightball-webapp which has a simple webapp to invoke all that from a web gui. Presently this doesn't embed the Tuscany runtime so needs to run on a Tomact with the tuscany.war distribution installed.



When the SCAClient API is updated to work with the distributed domain it would be good to simplify all the *-test.jar contributions to show using the SCAClient APIs.

If you've a recent 2.x full build you can run the contributions using the tuscany.bat script in the 2.x distribution (which you can find in distribution\all\target\apache-tuscany-sca-all-2.0-SNAPSHOT-dir\tuscany-sca-2.0-SNAPSHOT). Its easiest if you add that to your environment path, eg:

   set PATH=\Tuscany\SVN\2.x-trunk\distribution\all\target\apache-tuscany-sca-all-2.0-SNAPSHOT-dir\tuscany-sca-2.0-SNAPSHOT\bin;%PATH%

then at a command prompt:

   tuscany tribes:eightballDomain eightball.jar

and at another command prompt:

   tuscany tribes:eightballDomain eightball-test.jar

That uses multicast, running on separate machines you need point one node at another, so 

   tuscany tribes:eightballDomain eightball.jar

then in the console log look for the IP in the line: 

   INFO: Receiver Server Socket bound to:/9.164.186.49:4000

and start the test node using that ip:port, eg:

   tuscany "tribes:eightballDomain?routes=9.164.186.49:4000" eightball-test.jar
 
(Note that you must have quotes around the config uri)













