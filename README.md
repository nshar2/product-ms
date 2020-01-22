# product microservice

The service has been written using jdk 13.0.1 and the maven java target version is java 13.
It uses a MongoDB database to persist products

# to build the service

To build(compile, run tests(both unit and integration tests), package) the service from command line: mvn clean install

# service assumptions
The services assumes that there is an instance of MongoDB running locally on port 27017.
If you wish to you a different MongoDB host/port, provide the host/post as following properties/environment variables to the service:

spring.data.mongodb.database=MongoDatabaseName
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017

# to run the service

1) From IDE: Run ProductApplication.java 
2) From command line: mvn spring-boot:run
3) From command line: java -jar target/product-ms-1.0.0-SNAPSHOT.jar

# swagger-ui
localhost:8080/swagger-ui.html

# Improvements possible
1) CreateProduct endpoint overrides any existing product. Ideally if a product by sku already exists, then httpstatus 409(conflict) 
could be sent.
2) Test data creation could have gone into TestDataBuilder classes.
3) More testing around validations would have been nice.


