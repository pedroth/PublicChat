git clone https://github.com/pedroth/PublicChat.git
cd PublicChat/PublicChat/
mvn clean install
mv PublicChatServer ../../PublicChatServer
cd ../../
chmod -R 777 PublicChatServer/
cd PublicChatServer/
sh run.sh
