FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace
ARG MAVEN_CLI_OPTS="--batch-mode --errors --fail-at-end -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.timeout=120000"
COPY pom.xml /workspace/pom.xml
COPY customer-service/pom.xml /workspace/customer-service/pom.xml
# Parent POM declares both modules, so both child POMs must exist in the build context.
COPY import-service/pom.xml /workspace/import-service/pom.xml
RUN mvn ${MAVEN_CLI_OPTS} -f /workspace/pom.xml -pl customer-service -am dependency:go-offline -DexcludeScope=test
COPY customer-service/src /workspace/customer-service/src
RUN mvn ${MAVEN_CLI_OPTS} -f /workspace/pom.xml -pl customer-service -am clean package -Dmaven.test.skip=true

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /workspace/customer-service/target/customer-service-1.0.0.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]


