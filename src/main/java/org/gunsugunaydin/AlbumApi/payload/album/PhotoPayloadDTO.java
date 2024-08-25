package org.gunsugunaydin.AlbumApi.payload.album;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhotoPayloadDTO {
    
    @NotBlank
    @Schema(description = "Name of the photo", example = "Selfie", requiredMode = RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "Description of the photo", example = "So happy to have me! ", 
    requiredMode = RequiredMode.REQUIRED)
    private String description;
}
