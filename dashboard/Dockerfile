FROM java:8
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=build/libs/dashboard-1.0.0.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]