FROM adoptopenjdk/openjdk14
MAINTAINER lzhang sosilent.lzh@gmail.com
ADD sources.list /etc/apt/
ARG DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
    && apt install -y htop \
    && apt install -y curl \
    && apt install -y vim \
    && apt install -y net-tools \
    && apt install -y iputils-ping

ENV LANG C.UTF-8

# Set the location of the verticles
ENV APP_HOME /app

EXPOSE 5701
EXPOSE 12345

VOLUME $APP_HOME/tmp
VOLUME $APP_HOME/cahce
VOLUME $APP_HOME/logs
VOLUME $APP_HOME/config

COPY thupat-ms-*-fat.jar $APP_HOME/thupat-ms.jar
COPY config.yml $APP_HOME/config.yml

# Launch the app
WORKDIR $APP_HOME

CMD echo "thupat ms is starting"
ENTRYPOINT ["java", "-Duser.timezone=GMT+8", "-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4j2LogDelegateFactory", "-jar", "thupat-ms.jar", "-c", "config.yml"]
