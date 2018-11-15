sudo rtcwake -m mem -s 25200
sudo nmcli radio wifi off
sudo sleep 30
sudo nmcli radio wifi on
sudo sleep 30
sudo nmcli -terse
#sudo rtcwake -m mem -s 25200
