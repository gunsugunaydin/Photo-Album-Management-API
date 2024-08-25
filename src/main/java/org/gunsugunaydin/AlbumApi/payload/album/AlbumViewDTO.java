package org.gunsugunaydin.AlbumApi.payload.album;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlbumViewDTO {
    
    @Schema(description = "Unique identifier for the album")
    private Long id;

    @NotBlank
    @Schema(description = "Name of the album", example = "Portugal travel", requiredMode = RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "Description of the album", example = "Travel Porto with me!", 
    requiredMode = RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "A list of photos contained in the album")
    private List<PhotoDTO> photos;
}

