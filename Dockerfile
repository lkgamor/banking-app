#Download base maven image
FROM maven:3.6-jdk-11 AS maven-dependencies

#Copy project pom file to working directory
COPY ./pom.xml .

#Build and cache all dependencies
RUN mvn dependency:go-offline -B

#Copy all required files and folders to 'build' directory
COPY ./src ./src

#Build application binary
RUN mvn clean package -DskipTests

#Download final package image
FROM openjdk:11-jre-slim

ENV email=lkgamor@gmail.com

#Set Image Author
LABEL maintainer=${email}

#Set project deployment directory
WORKDIR /banking

#Copy all required files and folders to working directory
COPY ./src ./src

#Copy over built artifacts from maven-dependencies
COPY --from=maven-dependencies target/banking.jar ./target/banking.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "./target/banking.jar"]
