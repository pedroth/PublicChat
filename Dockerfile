ARG BUILDPLATFORM

FROM --platform=$BUILDPLATFORM alpine/git AS clone
WORKDIR /app
RUN git clone https://github.com/pedroth/PublicChat.git

FROM --platform=$BUILDPLATFORM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY --from=clone /app/PublicChat/ /app
RUN sh scripts/install.sh

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/PublicChatServer /app
CMD ["sh", "run.sh"]