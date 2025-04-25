FROM alpine/git AS clone
WORKDIR /app
RUN git clone https://github.com/pedroth/PublicChat.git

FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY --from=clone /app/PublicChat/ /app
RUN sh scripts/install.sh

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/PublicChatServer /app
CMD ["sh", "run.sh"]