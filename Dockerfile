FROM openjdk:11
ADD target/docker-spring-boot-backend.jar docker-spring-boot-backend.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar", "docker-spring-boot-backend.jar"]