# NewsAPI Integration with Spring Boot

## Overview

This Spring Boot application demonstrates integration with the NewsAPI to fetch and store news articles. It showcases the use of Spring MVC, Spring Data JPA, and integration with an external RESTful service.

## Features

- Fetch news articles from NewsAPI and store them in a database.
- Retrieve stored news articles with pagination support, sorted by article date in descending order.
- Spring transactions management with rollback support for failed API calls.

## Prerequisites

- Java 11 or newer
- Maven 3.6 or newer
- Your preferred IDE (IntelliJ IDEA, Eclipse, VSCode, etc.)
- An API key from NewsAPI (you can get one by signing up at [https://newsapi.org/](https://newsapi.org/))

## How to Run

1. Clone this repository to your local machine:

    ```bash
    git clone https://github.com/Adamwaa/newsapi.git
    ```

2. Navigate to the project directory:

    ```bash
    cd newsapi
    ```

3. Add your NewsAPI API key to `src/main/resources/application.properties`:

    ```properties
    newsapi.apikey=your_api_key_here
    ```

4. Run the application using Maven:

    ```bash
    mvn spring-boot:run
    ```
## API Endpoints

### Fetch News from NewsAPI

- `GET /api/news` - Fetches the latest news articles from NewsAPI and returns them in a JSON format.

### Fetch Paginated News from Database

- `GET /api/news/paginated?page={page}&size={size}` - Retrieves news articles from the database with pagination support. Allows specifying `page` and `size` to control the pagination. Articles are sorted by publication date in descending order.

### Fetch News with Custom Path

- `GET /api/news/custom?path={path}` - Fetches news articles using a custom path to the NewsAPI. The `path` parameter should be provided to define the endpoint of the NewsAPI to hit (e.g., `top-headlines`, `everything`).

### Fetch News with Custom Path

- `GET /api/news/custom?path={path}` - Fetches news articles using a custom path to the NewsAPI. The `path` parameter allows for flexible API endpoint usage, such as `everything?q=tesla&from=2024-01-05&sortBy=publishedAt`. This parameter should include the middle part of the NewsAPI URL that specifies the endpoint and query parameters (excluding the base URL and API key).

For example, to fetch articles about "Tesla" from a specific date, you would use in Postman:
- `Key: path, Value: everything?q=tesla%26from=2024-01-04%26sortBy=publishedAt.`
- `http://localhost:8080/api/news/custom?path=everything?q=tesla%26from=2024-01-04%26sortBy=publishedAt`

## Responses

The API endpoints return JSON-formatted responses with the following status codes:

- `200 OK` - The request was successful, and the response body contains the requested data.
- `204 No Content` - The request was successful, but no articles were found.


## Swagger UI

Access the API documentation and test the endpoints using Swagger UI:

- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Technologies

- Spring Boot
- Spring MVC
- Spring Data JPA
- H2 Database
- RestTemplate
- Maven

## Testing

The project includes unit tests for the service layer and integration tests for REST controllers. Run tests using the following Maven command:

```bash
mvn test
```


## Example: 

![2024-02-04_21h21_50-ezgif com-crop](https://github.com/Adamwaa/newsapi/assets/97319080/41e7c295-a227-4f73-b24f-3d7cbb8ebd32)

