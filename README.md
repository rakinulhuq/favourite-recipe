# ABN AMRO Favourite Recipe BE Assignment

### Architectural Choices
1. Spring Boot Framework is used as we can easily create stand-alone, production-grade Spring based applications with minimal configuration
2. Spring Security is used in combination as it is the de facto standard for securing Spring-based applications
3. Token-Based authentication is used it is stateless, scalable, flexible and provides robust security
4. MongoDB is used as the data for recipes will be unstructured and it will be faster to make complex queries
5. Lombok is used for reducing boiler code
6. Swagger 2.0 is used for documenting the REST APIs

### Pre-requisites
1. Java 17, Maven 3, MongoDB
2. MongoDB configurations and credentials need to be set in the properties files for all profiles

### Guides to run the application
1. Clone the repository
2. Build the application using "mvn clean install" (To build the application without running the unit tests use "mvn clean install -DskipTests")
3. Run the application using "mvn spring-boot:run"
4. Make a GET call to "/management/ping" to confirm if the service is up and running

### Swagger
{BASE_URL}/swagger-ui/index.html <br/>
eg. http://localhost:8080/swagger-ui/index.html

### Notes
1. Please find the postman file "favourite-recipes.postman_collection" that you can import in your Postman to get all the APIs
2. For better security we would rather use a certified identity provider who will provide us with a Public Certificate that we will use to verify the tokens
3. When running the application with an IDE, make sure to follow the same steps mentioned above

