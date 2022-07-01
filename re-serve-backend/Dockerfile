FROM maven:3.8.3-openjdk-17 AS build
RUN mkdir -p "workspace"
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn clean package -DskipTests

FROM build as release
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]