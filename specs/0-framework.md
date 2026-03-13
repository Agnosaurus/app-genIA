# Architecture

 - **Language**: Java 17
 - **Framework**: Spring Boot 3.2.x
 - **Build**: Maven
 - **Database**: Flapdoodle Embedded MongoDB
 - **Package structure**: eu.askadev.magic.{controller,service,repository,model,view}
 - **UI technology**: JavaFX 21


# Specificity
 - **Database should be in memory, and persisted through a file on my computer**
 - **Database should persist between 2 app launches**
 - **Standalone Java application**
 - **Application startup**: Spring Boot standard (SpringApplication.run()), JavaFX initialized via Spring lifecycle hooks
 - **JavaFX integration**: Launched after Spring context is ready using ApplicationRunner or @PostConstruct



# Maven Dependencies

```xml
<!-- Core Spring Boot (no web server) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<!-- Spring Data MongoDB -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<!-- Embedded MongoDB -->
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo.spring30x</artifactId>
    <version>4.9.2</version>
</dependency>

<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>21</version>
</dependency>
```


# Java
 -**All generated class under java-generated folder**