# Based on https://dzone.com/articles/a-dockerfile-for-maven-based-github-projects

FROM alpine/git as clone
WORKDIR /app
RUN git clone https://github.com/pedroth/PublicChat.git

FROM maven:3.5-jdk-8-alpine as build 
WORKDIR /app
COPY --from=clone /app/PublicChat/ /app
RUN sh scripts/install.sh

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=build /app/PublicChatServer /app
CMD ["sh" , "run.sh"]