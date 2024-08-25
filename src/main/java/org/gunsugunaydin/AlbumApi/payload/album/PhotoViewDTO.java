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
public class PhotoViewDTO {

    @Schema(description = "Unique identifier for the photo")
    private long id;

    @Schema(description = "Name of the photo")
    private String name;

    @Schema(description = "Description of the photo")
    private String desciption;
}
