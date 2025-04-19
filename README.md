# PublicChat

Public chat is a simple chat server that has the option to delete all data.

![Public chat](/PublicChatv1.gif)

## Features
- Send text messages through web
- Upload files
- Create links from url texts
- Clear all data from server button

## Running PublicChat

The proper way to run public chat, is to use [docker](https://docs.docker.com/get-docker/):

`docker run -it -p 8080:8080 --name public-chat pedroth/public-chat`

Or just run `dockerRun.sh`:

- `sh dockerRun.sh`

## Running PublicChat on RaspberryPi
Since RaspberryPi usually have a `arm` processor, a new version of the docker images needs to be used.

`docker run -it -p 80:8080 --name public-chat pedroth/public-chat:linux-arm-v6`

## Installing public chat on your machine

Follow the steps of [Dockerfile](/Dockerfile).

## Demo

A public instance of the public chat is [here](http://pedroth.duckdns.org).

Most of the time is offline...
