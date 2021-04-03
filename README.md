# PublicChat

 * Public chat is a simple chat server that has the option to delete all data. 

 ## Dependencies
* Need Java 8+ [ There are nice tutorials in google for any OS ]
* Apache Maven [ Check google as well ]

 ## Install Public Chat

1) git clone the project
	* git clone https://github.com/pedroth/PublicChat.git
	* cd PublicChat

2) Run install/install.bat[sh]:
    * [Windows]: Click on bat file
    * [Linux]: `sh install/install.sh`

   Or go to PublicChat folder with the **pom.xml** file and run maven clean install:
	* `cd PublicChat/PublicChat/`
	* `mvn clean install`

The public chat server is built on folder PublicChatServer.

## Run Public Chat

1) run the batch/shell script in PublicChatServer/ folder
	* [Windows]: Click on the run.bat
	* [Linux]: Type `sh run.sh` in a terminal

2) You can also run the jar by yourself, using:
	* `java -Xmx1g -jar PublicChat <port>`
		* where **\<port>** is a number, that defines the port 
		
3) Open browser at  **\<IP>**:**\<port>**/PublicChat
	* eg: 192.168.1.6:8080/PublicChat
	* eg: localhost:8000/PublicChat 

## Demo

A public instance of the public chat is [here](http://pedroth.duckdns.org:8080/PublicChat)
