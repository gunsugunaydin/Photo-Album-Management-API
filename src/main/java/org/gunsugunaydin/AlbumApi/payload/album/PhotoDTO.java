package org.gunsugunaydin.AlbumApi.payload.album;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDTO {
 
    @Schema(description = "Unique identifier for the photo")
    private Long id;

    @Schema(description = "Name of the photo")
    private String name;

    @Schema(description = "Description of the photo")
    private String description;

    @Schema(description = "The file name of the photo stored on the server")
    private String fileName;

    @Schema(description = "The URL to download the photo")
    private String downloadLink;
}
