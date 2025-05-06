FROM alpine:3 AS base

# Inspired by https://github.com/saschpe/docker-kotlin/blob/master/Dockerfile

ARG kotlin_version=2.1.20

ENV KOTLIN_ROOT /opt/kotlinc
ENV KOTLIN_VERSION ${kotlin_version}
ENV PATH $PATH:$KOTLIN_ROOT/bin

RUN sed -i 's/^# http/http/' /etc/apk/repositories
RUN apk update
RUN apk add --no-cache --virtual=.build-dependencies openjdk8 bash wget unzip \
	&& wget https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip -O /tmp/kotlin.zip \
	&& mkdir -p /opt \
    && unzip /tmp/kotlin.zip -d /opt \
    && rm -v /tmp/kotlin.zip \
    && java -version \
    && kotlin -version

WORKDIR /lox

COPY ./src/ /lox/
COPY ./bin /usr/local/bin

ENV PATH $PATH:/usr/local/bin

FROM base as generateAST
COPY ./src/ /lox/
RUN kotlinc tool/*.kt -include-runtime -d tool.jar
CMD ["/usr/local/bin/generate-ast"]

FROM base as lox
COPY ./src/ /lox/
RUN kotlinc lox/*.kt -include-runtime -d lox.jar
CMD ["java", "-jar", "lox.jar"]
