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

Explore the API documentation through Swagger at `http://localhost:8080/swagger-ui/index.html`.
  
![Ekran görüntüsü 2024-08-25 221810](https://github.com/user-attachments/assets/ee10a56e-e4be-4883-a9fc-001b1e4071fd)

Swagger UI Screenshot


  This time, I'll keep it brief:

  - **Authentication & Account Management**: Use the `/api/v1/auth` endpoints.
  - **Album Management**: Access the `/api/v1/albums` endpoints for all album-related operations:
    
    **Photo Management Screenshots:**
    
    - **Upload Photo:**
      
      ![Ekran görüntüsü 2024-08-26 001520](https://github.com/user-attachments/assets/b75addca-e599-4bb3-b63a-9a6829654366)
          
    
    - **Download Photo(Also Thumbnail 🙋🏼‍♀️):**
      
      ![Ekran görüntüsü 2024-08-25 231716](https://github.com/user-attachments/assets/0a78f84c-c2ca-4f37-8ef9-30565c545276)

## My Learning and Growth Journey <img src="https://gifdb.com/images/high/peach-workout-3ji7jev6r95djd66.gif" alt="Waving Hand" width="40" height="40">

Throughout the development of this project, I’ve significantly improved my coding practices and expanded my knowledge in several areas:

- **File Handling**: Gained experience with file uploads, downloads, and directory management, including deletions, with a focus on image files.
- **Service Layer Architecture**: Implemented service interfaces to restrict direct access and separated the service layer into interfaces and their implementations for better code organization and maintainability.
- **Entity Management**: I used to handle operations like setting values for entities directly in the controllers. Now, I’ve moved this logic to the service layer, realizing that it leads to cleaner and more maintainable code.
- **API Documentation**: Made a strong effort to document my API responses thoroughly, learning the importance of clear and comprehensive documentation.
- **Code Quality**: Focused on writing more organized and readable code, which has been a significant improvement compared to my previous projects.
  
## Future Enhancements

In the future, I intend to integrate cloud storage solutions, such as AWS S3, for managing photo storage more efficiently.

<img src="https://media.tenor.com/beCXMoxigz4AAAAM/rosycheeks-mochi-peach.gif" alt="Waving Hand" width="40" height="40">Please feel free to explore the code and share your feedback. I am always open to suggestions and collaboration.

- **Email**: [gunsugunay98@gmail.com](mailto:gunsugunay98@gmail.com)
- **LinkedIn**: [linkedin.com/in/gunsugunaydin](https://www.linkedin.com/in/gunsugunaydin/)

