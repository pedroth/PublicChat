# PublicChat

Public chat is a simple chat server that has the option to delete all data.

## Features
- Send text messages through web
- Upload files
- Create links from url texts
- Clear all data from server button

## Running PublicChat

The proper way to run public chat, is to use docker:

`docker run -it -p 8080:8080 --name public-chat pedroth/public-chat`

Or just run `dockerRun.sh`:

- `sh dockerRun.sh`

## Installing public chat on your machine

Follow the steps of [Dockerfile](/Dockerfile).

## Demo

A public instance of the public chat is [here](http://pedroth.duckdns.org:8080/PublicChat).

Most of the time is offline...
