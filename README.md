# PublicChat

 * Public chat is a simple chat server that has the option to delete all data.

 ## Dependencies
 * Need Java 8+ [ http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html ]
* Maven [ https://maven.apache.org/install.html ]

 ## Install Public Chat

1) git clone the project
	* git clone https://github.com/pedroth/PublicChat.git

2) run maven clean install
	* mvn clean install

3) run the batch/shell script in target/PublicChat/ folder
	* Click on the run.bat [Windows] or run.sh [Linux]

4) You can also run the jar by yourself, using :
	* java -Xmx1g -jar PublicChat **\<port>**
		* where **\<port>** is a number, that defines the port 
5) Open browser at  **\<IP>**:**\<port>**/PublicChat
	* eg: 192.168.1.6:8080/PublicChat
	* eg: localhost:8000/PublicChat 