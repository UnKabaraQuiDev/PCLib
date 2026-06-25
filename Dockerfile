FROM catthehacker/ubuntu:act-latest

RUN apt-get update && \
    apt-get install -y maven openjdk-17-jdk && \
    apt-get clean

RUN node --version
RUN mvn --version
