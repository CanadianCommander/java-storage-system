FROM openjdk:17.0.1

# setup env
ENV SPRING_PROFILES_ACTIVE=test

# create work dir
RUN mkdir -p /opt/source/
WORKDIR /opt/source/

CMD ./gradlew --stop && ./gradlew clean test