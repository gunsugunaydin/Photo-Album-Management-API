package org.gunsugunaydin.AlbumApi.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProfileDTO {

    @Schema(description = "Unique identifier for the profile")
    private Long id;

    @Schema(description = "Email address associated with the profile")
    private String email;

    @Schema(description = "Authorities granted to the profile")
    private String authorities;
}
