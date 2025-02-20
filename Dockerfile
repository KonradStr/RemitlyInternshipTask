FROM openjdk:22

COPY target/SwiftCodeApi*.jar /app/app.jar
COPY src/main/resources/Interns_2025_SWIFT_CODES.xlsx /app/resources/Interns_2025_SWIFT_CODES.xlsx
COPY src/main/resources/db /app/db

WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]