# Photo Album Management API

This project is a Photo Album Management API developed using Spring Boot. The API provides a set of endpoints for managing user accounts, albums, and photos. Users can create, update, delete, and retrieve albums and photos. The API also supports uploading photos and generating thumbnails automatically.

## Features

- **User Authentication**: Secure authentication using Spring Security.
- **Album Management**: Users can create, update, delete, and list their photo albums.
- **Photo Management**: Users can upload, download, update, delete, and list photos within their albums.
- **Thumbnail Generation**: Automatically generates thumbnails for uploaded photos.
- **API Documentation**: Integrated with Swagger for easy API documentation and testing.

## Technologies

- **Spring Boot**: For building the RESTful API.
- **Spring Security**: For managing user authentication and authorization.
- **Swagger**: For API documentation and testing.
- **Lombok**: To reduce boilerplate code.
- **Maven**: For project management and dependency management.

## Project Structure

- **Maven Project**: Managed using Maven, with dependencies for Spring Boot, JPA, validation, OAuth2 resource server, and more.
- **Java Version**: Built with Java 21 to leverage the latest features and improvements in the language.
- **Dependencies**: Includes essential libraries like `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, and `springdoc-openapi` for API documentation.
- **Image Processing**: Utilizes the `imgscalr-lib` for efficient image resizing and processing.
- **Database**: Integrated with H2 for a lightweight, in-memory database solution.

## Setup and Installation

To get started with the project, clone the repository and build it using Maven. The API is ready to run with a simple `mvn spring-boot:run`.

## Usage and API Documentation

- Explore the API documentation through Swagger at `http://localhost:8080/swagger-ui/index.html`.
  
![Ekran görüntüsü 2024-08-25 221810](https://github.com/user-attachments/assets/ee10a56e-e4be-4883-a9fc-001b1e4071fd)

Swagger UI Screenshot

  This time, I'll keep it brief:

  - **Authentication & Account Management**: Use the `/api/v1/auth` endpoints.
  - **Album Management**: Access the `/api/v1/albums` endpoints for all album-related operations.


## My Learning and Growth Journey

Throughout the development of this project, I’ve significantly improved my coding practices and expanded my knowledge in several areas:

- **File Handling**: Gained experience with file uploads, downloads, and directory management, including deletions, with a focus on image files.
- **Service Layer Architecture**: Implemented service interfaces to restrict direct access and separated the service layer into interfaces and their implementations for better code organization and maintainability.
- **Entity Management**: I used to handle operations like setting values for entities directly in the controllers. Now, I’ve moved this logic to the service layer, realizing that it leads to cleaner and more maintainable code.
- **API Documentation**: Made a strong effort to document my API responses thoroughly, learning the importance of clear and comprehensive documentation.
- **Code Quality**: Focused on writing more organized and readable code, which has been a significant improvement compared to my previous projects.

## Future Enhancements

- **Cloud Storage Integration**: Moving forward, I aim to integrate cloud storage solutions like AWS S3 for managing photo storage.

Feel free to explore the code and provide feedback. I’m always open to suggestions and collaboration!

- **Email**: gunsugunay98@gmail.com
- **LinkedIn**: https://www.linkedin.com/in/gunsugunaydin/
