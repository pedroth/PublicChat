# PublicChat

 * Public chat is a simple chat server that has the option to delete all data. 

 ## Dependencies
* Need Java 8+ [ There are nice tutorials in google for any OS ]
* Apache Maven [ Check google as well ]

 ## Install Public Chat

1) git clone the project
	* git clone https://github.com/pedroth/PublicChat.git

2) Go to PublicChat folder with the **pom.xml** file. Run maven clean install:
	* cd PublicChat/PublicChat/
	* mvn clean install

3) run the batch/shell script in PublicChatServer/ folder
	* Click on the run.bat [Windows] or run *sh run.sh [Linux]* in a terminal

4) You can also run the jar by yourself, using :
	* java -Xmx1g -jar PublicChat **\<port>**
		* where **\<port>** is a number, that defines the port 
		
5) Open browser at  **\<IP>**:**\<port>**/PublicChat
	* eg: 192.168.1.6:8080/PublicChat
	* eg: localhost:8000/PublicChat 
