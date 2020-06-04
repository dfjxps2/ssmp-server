FROM java:8
VOLUME /tmp
ADD target/auth-service-1.0.0-SNAPSHOT.jar /app/auth-service.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/auth-service.jar"]
EXPOSE 30002