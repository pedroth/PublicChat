sudo sleep 30
sudo nmcli radio wifi off
sudo sleep 30
sudo nmcli radio wifi on
sudo sleep 30
sudo nmcli -terse
echo "starting server on port 8080"
cd /home/pedroth/Desktop/PublicChatServer/
sh run.sh
#sudo java -Xmx1g -jar /home/pedroth/Desktop/PublicChatServer/PublicChat.jar 8080
#sudo rtcwake -m mem -s 25200
