FROM maven:3.8.2-jdk-11 as build
WORKDIR /source
COPY . .
RUN mvn clean package

FROM amazoncorretto:11 as salary-mgmt
WORKDIR /app
COPY --from=build /source/target/employee-salary-management-*.jar ./employee-salary-management.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "employee-salary-management.jar"]