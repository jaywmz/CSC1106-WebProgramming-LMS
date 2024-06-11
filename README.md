# CSC1106 Web Programming Project

## Group 7
- Felix Chang (2301105)
- Jiang Weimin (2301083)
- Leo Oh Kang Weng (2301071)
- Elroy Lee (2300950)
- Lim Jing Chuan Jonathan (2300923)
- Tee Yu Cheng (2300884)
- Joween Ang (2301093)
- Chew Liang Zhi (2300948)

## Description
LearnZenith is a Learning Management System (LMS) built using Java and the Spring Boot Framework. It utilises Thymeleaf as the HTML Template Engine. It is connected to an Azure MySQL server for databases services and Azure Blob for file storage.

## Requirements

 - Java 17+
 - Maven 3.6.3+

## How to run the application

1.	Ensure that maven has been properly installed
	-	Run `mvn -version` in a Terminal to verify your maven installation
2. Open a Terminal
3. 	Navigate to the unzipped folder
	- 	Your working directory should be in the same folder as `pom.xml`
5. Run `mvn install` to install and build our application dependencies
	-	You can also do `mvn clean install` to build from a clean slate
6.  Run `mvn spring-boot:run` to start running the application
	-	Alternatively, you can run `java -jar target/csc1106-0.0.1-SNAPSHOT.jar` to run the application using the jar file generated in the previous step
7. Once there is the “Application Started” message, open your web browser and go to `localhost:8080`
