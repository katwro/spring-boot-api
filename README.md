# Spring Boot API: Book List Application

This is an example Book List API built using Spring Boot, Hibernate/JPA, and Microsoft SQL Server.

## Prerequisites

Before running the application, ensure you have the following prerequisites installed:

- Java JDK (version 22.0)
- Microsoft SQL Server (Express edition is sufficient)

## Installation

Follow these steps to set up the application:

1. Clone the repository.

2. Set up the database:
   - Run the `BLAPI.sql` script to create the necessary database and tables. 
   - Configure your database connection in the `src/main/resources/application.properties` file by updating the following properties with your SQL Server credentials:
    ```
   spring.datasource.url=jdbc:sqlserver://localhost;databaseName=YourDatabaseName;trustServerCertificate=true;
   spring.datasource.username=yourUsername
   spring.datasource.password=yourPassword
    ```
3. Grant user privileges:
   - Ensure that the database user specified in the `application.properties` file has the necessary privileges to access and modify the database.

## Running the application

To start the application, run the following command:
```
./mvnw spring-boot:run
```
## Running Tests

To run the tests, use the following command:
```
./mvnw test
```
## Usage

Once the application is running, you can explore the API using the Swagger UI:

- Swagger UI: http://localhost:8080/swagger-ui.html
