# This should be ran after everything is in git
VERSION=v2.0.0
docker build -t pedroth/public-chat:latest -t pedroth/public-chat:$VERSION .
docker push pedroth/public-chat:latest
docker push pedroth/public-chat:$VERSION

#Arm
#VERSION=linux-arm-v6_v2
#docker build -t pedroth/public-chat:$VERSION .
#docker push pedroth/public-chat:$VERSION