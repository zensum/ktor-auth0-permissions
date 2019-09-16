### No need to have this if it's a jib build
FROM gradle:5.3.1-jdk11

USER root
RUN mkdir -p /usr/src/app && chown gradle:gradle /usr/src/app
USER gradle

WORKDIR /usr/src/app
ADD build.gradle.kts /usr/src/app/
ADD settings.gradle.kts /usr/src/app/

ARG JITPACK_TOKEN=${JITPACK_TOKEN}
RUN gradle -q --no-daemon dependencies

ADD . /usr/src/app
USER root
RUN chown -R gradle:gradle /usr/src/app

RUN gradle -q shadowJar --no-daemon

WORKDIR /usr/src/app

CMD java $JAVA_OPTS -jar build/libs/shadow.jar
