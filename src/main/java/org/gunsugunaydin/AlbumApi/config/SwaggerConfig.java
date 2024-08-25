package org.gunsugunaydin.AlbumApi.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.*;

@Configuration
@OpenAPIDefinition(
  info =@Info(
    title = "ALBUM API",
    version = "Verions 1.0",
    contact = @Contact(
      name = "Günsu Günaydın", email = "gunsugunay98@gmail.com", url = "https://www.linkedin.com/in/gunsugunaydin/"
    ),
    license = @License(
      name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
    ),
    description = "Photo Album Management API developed with Spring Boot by Günsu."
  )
)
public class SwaggerConfig {

}
