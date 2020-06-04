FROM java:8
VOLUME /tmp
ADD target/auth-frontier-1.0.0-SNAPSHOT.jar /app/auth-frontier.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/auth-frontier.jar" ]
EXPOSE 30001
