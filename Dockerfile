FROM amazoncorretto:11 as salary-mgmt
WORKDIR /app/zenika/
COPY target/employee-salary-management-*.jar ./employee-salary-management.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "employee-salary-management.jar"]