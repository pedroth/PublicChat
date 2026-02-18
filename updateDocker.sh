# This should be ran after everything is in git
# VERSION=v2.0.1
# docker build -t pedroth/public-chat:latest -t pedroth/public-chat:$VERSION .
# docker push pedroth/public-chat:latest
# docker push pedroth/public-chat:$VERSION

#Arm (cross-build from amd64)
VERSION=linux-arm-v7_v2
docker buildx build --platform linux/arm/v7 -t pedroth/public-chat:$VERSION --push .