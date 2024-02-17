# atipera-test-task
## About
This is a project for test task for Junior Java/Kotlin Developer recruitment process.
The task consisted of using GitHub API to fetch data about user repositories and format the response with given criteria.
The project was written in Java 21 using Spring Boot 3 and utilizing TDD approach.

## Run the application
Clone the repository and cd into it, then perform
```
$ mvnw spring-boot:run
```
The application runs on `localhost:8080`
## Specification
### `/api/{username}/repos` endpoint
To retrieve information about all public GitHub repositories for a given user.
#### Response
```
{
  "repositoryName": "string",
  "ownerLogin": "string",
  "branches": [
    {
        "name": string,
        "lastCommitSha": string
    }
  ]
}
```
### Tech Stack
* Java 21
* Spring Boot 3
* Spring Web
* Spring WebFlux
* JUnit 5
* Mockito
* AssertJ
* Lombok
