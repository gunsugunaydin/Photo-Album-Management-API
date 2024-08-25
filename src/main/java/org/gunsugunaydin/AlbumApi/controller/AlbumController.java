package org.gunsugunaydin.AlbumApi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.lang3.RandomStringUtils;
import org.gunsugunaydin.AlbumApi.model.Account;
import org.gunsugunaydin.AlbumApi.model.Album;
import org.gunsugunaydin.AlbumApi.model.Photo;
import org.gunsugunaydin.AlbumApi.payload.album.AlbumPayloadDTO;
import org.gunsugunaydin.AlbumApi.payload.album.AlbumViewDTO;
import org.gunsugunaydin.AlbumApi.payload.album.PhotoDTO;
import org.gunsugunaydin.AlbumApi.payload.album.PhotoPayloadDTO;
import org.gunsugunaydin.AlbumApi.payload.album.PhotoViewDTO;
import org.gunsugunaydin.AlbumApi.service.interfaces.IAccountService;
import org.gunsugunaydin.AlbumApi.service.interfaces.IAlbumService;
import org.gunsugunaydin.AlbumApi.service.interfaces.IPhotoService;
import org.gunsugunaydin.AlbumApi.util.AppUtils.AppUtil;
import org.gunsugunaydin.AlbumApi.util.constants.AlbumError;
import org.gunsugunaydin.AlbumApi.util.constants.AlbumSuccess;
import org.springframework.core.io.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/albums")
@Tag(name = "Album Controller", description = "Controller for album and photo management")
@Slf4j
public class AlbumController {

    static final String PHOTOS_FOLDER_NAME = "photos";
    static final String THUMBNAIL_FOLDER_NAME = "thumbnails";
    static final int THUMBNAIL_WIDTH = 300;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IAlbumService albumService;

    @Autowired
    private IPhotoService photoService;

    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Album successfully added"),
        @ApiResponse(responseCode = "200", description = "Album successfully added", content = @Content(schema = @Schema(implementation = AlbumViewDTO.class))),
        @ApiResponse(responseCode = "400", description = "Please add a valid name and description"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Please log in to add an album"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error - An error occurred during album addition")
    })
    @Operation(summary = "Add an album")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<?> addAlbum(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO, Authentication authentication) {
        try {
            String email = authentication.getName();
            Account account = accountService.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            Album newAlbum = albumService.addNewAlbum(albumPayloadDTO, account);

            AlbumViewDTO albumViewDTO = new AlbumViewDTO(newAlbum.getId(), newAlbum.getName(), newAlbum.getDescription(), null);
            return ResponseEntity.ok(albumViewDTO);

        } catch (Exception e) {
            log.debug(AlbumError.ADD_ALBUM_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "An error occurred during album addition."));
        }
    }


    @GetMapping(value = "/list", produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of albums", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AlbumViewDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Access denied due to token issues"),
        @ApiResponse(responseCode = "404", description = "Not Found - No albums found for the user"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error - An unexpected error occurred")
    })
    @Operation(summary = "List albums")
    @SecurityRequirement(name = "album-api")
    public List<AlbumViewDTO> albums(Authentication authentication) {       
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        List<AlbumViewDTO> albums = new ArrayList<>();
        for (Album album: albumService.findByAccount_id(account.getId())) {
            List<PhotoDTO> photos = new ArrayList<>();          
            for(Photo photo: photoService.findByAlbum_id(album.getId())) {
                String link = "/albums/"+album.getId()+"/photos/"+photo.getId()+"/download-photo";
                photos.add(new PhotoDTO(photo.getId(), photo.getName(), photo.getDescription(), photo.getFileName(), link));
            }  

            albums.add(new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), photos));
        }
        return albums;
    }


    @GetMapping(value = "/{album_id}/list", produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the album", content = @Content(schema = @Schema(implementation = AlbumViewDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request - No album exists with this ID"), 
        @ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Access denied because the album does not belong to the authenticated user"),
        @ApiResponse(responseCode = "404", description = "Not Found - Album or user not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error - An unexpected error occurred")
    })
    @Operation(summary = "List an album by album ID")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<?> albums_by_id(@PathVariable Long album_id, Authentication authentication) {       
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        
        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if (optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "No album exists with this ID."));
        }
        if (account.getId() != album.getAccount().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", "Oops! Not your album."));
        }

        List<PhotoDTO> photos = new ArrayList<>();
        for (Photo photo : photoService.findByAlbum_id(album.getId())) {
            String link = "/albums/"+album.getId()+"/photos/"+photo.getId()+"/download-photo";
            photos.add(new PhotoDTO(photo.getId(), photo.getName(), photo.getDescription(), photo.getFileName(), link));
        }

        AlbumViewDTO albumViewDTO = new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), photos);

        return ResponseEntity.ok(albumViewDTO);
    }


    @PutMapping(value = "/{album_id}/update", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the album", content = @Content(schema = @Schema(implementation = AlbumViewDTO.class))),
        @ApiResponse(responseCode = "204", description = "No Content - The album was updated, but no content is returned"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Please add valid name and description"), 
        @ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Access denied because the album does not belong to the authenticated user"), 
        @ApiResponse(responseCode = "404", description = "Not Found - No album exists with this ID"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error - An unexpected error occurred")
    })
    @Operation(summary = "Update an album by album ID")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<?> update_Album(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO, 
        @PathVariable Long album_id, Authentication authentication) {

        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();
    
            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("message", "Oops! Not your album."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "No album exists with this ID."));
            }

            Album updatedAlbum = albumService.updateAlbum(albumPayloadDTO, album);
            List<PhotoDTO> photos = new ArrayList<>();
            for(Photo photo: photoService.findByAlbum_id(updatedAlbum.getId())) {
                String link = "/albums/"+updatedAlbum.getId()+"/photos/"+photo.getId()+"/download-photo";
                photos.add(new PhotoDTO(photo.getId(), photo.getName(), photo.getDescription(), 
                photo.getFileName(), link));
            }           

            AlbumViewDTO albumViewDTO = new AlbumViewDTO(updatedAlbum.getId(), updatedAlbum.getName(), updatedAlbum.getDescription(), photos);
            return ResponseEntity.ok(albumViewDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", AlbumError.UPDATE_ALBUM_ERROR.toString()));
        }
    }


    @DeleteMapping(value = "/{album_id}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepted - The album was successfully deleted"),
        @ApiResponse(responseCode = "400", description = "Bad Request - No album exists with this ID or invalid request"), 
        @ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Access denied because the album does not belong to the authenticated user"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error - An unexpected error occurred")
    })
    @Operation(summary = "Delete an album by album ID")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<Map<String, String>> delete_album(@PathVariable Long album_id,Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();
    
            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("message", "Oops! Not your album."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "No album exists with this ID."));
            }
           
            for (Photo photo : photoService.findByAlbum_id(album.getId())) {
                AppUtil.delete_photo_from_path(photo.getFileName(), PHOTOS_FOLDER_NAME, album_id);
                AppUtil.delete_photo_from_path(photo.getFileName(), THUMBNAIL_FOLDER_NAME, album_id);
                photoService.delete(photo);
            }

            albumService.delete(album);
            AppUtil.delete_album_directory(album.getId());  
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Collections.singletonMap("message", AlbumSuccess.ALBUM_SUCCESSFULLY_DELETED.toString()));
           
        } catch (Exception e) {          
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", AlbumError.DELETE_ALBUM_ERROR.toString()));
        }
    }


    @PostMapping(value = "/{album_id}/upload-photos", consumes = {"multipart/form-data"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photos uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Please check the payload or token"), 
        @ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Access denied because the album does not belong to the authenticated user"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error - An unexpected error occurred")
    })
    @Operation(summary = "Upload photos to an album")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<List<HashMap<String, List<?>>>> photos(@RequestPart(required = true) MultipartFile[] files, 
        @PathVariable Long album_id, Authentication authentication) {

        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        
        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if(optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
            if(account.getId() != album.getAccount().getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<PhotoViewDTO> fileNamesWithSuccess = new ArrayList<>();
        List<String> fileNamesWithError = new ArrayList<>();

        Arrays.asList(files).stream().forEach(file -> { 
            String contentType = file.getContentType();
            if(contentType != null && (contentType.equals("image/png")
                                    || contentType.equals("image/jpg")
                                    || contentType.equals("image/jpeg"))) {

                int length = 10;
                boolean useLetters = true;
                boolean useNumbers = true;

                try {
                    String original_file_name = file.getOriginalFilename();
                    String generatedString = RandomStringUtils.random(length, useLetters,useNumbers);
                    String final_photo_name = generatedString + original_file_name;
                    String absolute_fileLocation = AppUtil.get_photo_upload_path(final_photo_name, PHOTOS_FOLDER_NAME, album_id);                   
                    Path path = Paths.get(absolute_fileLocation);//Linux etc. tüm OS için path'in algılanabilmesi amacıyla path nesnesi oluşturdum.
                    
                    //Bu satır, bir dosyanın giriş akışından okunan veriyi belirtilen path konumuna kopyalar. Kopyalama işlemi sırasında, eğer hedef konumda aynı isimde bir dosya zaten mevcutsa, StandardCopyOption.REPLACE_EXISTING seçeneği sayesinde bu dosya üzerine yazılır. 
                    //Eğer dosyayı yalnızca "dizine" kopyalamış olsaydık!!, dosya adı belirtilmeden dizine kopyalama yapılır ve bu işlemde dosya ismi belirtmemiz gerekirdi. 
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    Photo uploadedPhoto = photoService.uploadNewPhoto(original_file_name, final_photo_name, album);   

                    PhotoViewDTO photoViewDTO = new PhotoViewDTO(uploadedPhoto.getId(), uploadedPhoto.getName(), uploadedPhoto.getDescription());
                    fileNamesWithSuccess.add(photoViewDTO);

                    BufferedImage thumbImg = AppUtil.getThumbnail(file, THUMBNAIL_WIDTH);
                    File thumbnail_location = new File(AppUtil.get_photo_upload_path(final_photo_name, THUMBNAIL_FOLDER_NAME, album_id));

                    String formatName = contentType.split("/")[1];
                    ImageIO.write(thumbImg, formatName, thumbnail_location);
                
                } catch (Exception e) {
                    log.debug(AlbumError.UPLOAD_PHOTO_ERROR +":"+ e.getStackTrace());
                    fileNamesWithError.add(file.getOriginalFilename());
                }
                
            } else {
                fileNamesWithError.add(file.getOriginalFilename());
            }
        });
        
        HashMap<String, List<?>> result = new HashMap<>();
        result.put("SUCCESS", fileNamesWithSuccess);
        result.put("ERRORS", fileNamesWithError);
        
        List<HashMap<String, List<?>>> response = new ArrayList<>();
        response.add(result);
        
        return ResponseEntity.ok(response);
    }


    @PutMapping(value = "/{album_id}/photos/{photo_id}/update", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photo information updated successfully", content = @Content(schema = @Schema(implementation = PhotoViewDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Please ensure the provided name and description are valid"), 
        @ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Access denied because the photo does not belong to the album or the album does not belong to the user"), 
        @ApiResponse(responseCode = "500", description = "Internal Server Error - An unexpected error occurred")
    })
    @Operation(summary = "Update a photo info")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<?> update_photo(@Valid @RequestBody PhotoPayloadDTO photoPayloadDTO,
            @PathVariable Long album_id, @PathVariable Long photo_id, Authentication authentication) {
                
        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();
    
            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("message", "Oops! Not your album."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "No album exists with this ID."));
            }
            Optional<Photo> optionalPhoto = photoService.findById(photo_id);
            if(optionalPhoto.isPresent()) {
                Photo photo = optionalPhoto.get();
                if (photo.getAlbum().getId() != album_id) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("message", "The photo does not belong to this album."));
                }
                Photo updatedPhoto = photoService.updatePhotoInfo(photoPayloadDTO, photo);
                
                PhotoViewDTO photoViewDTO = new PhotoViewDTO(updatedPhoto.getId(), updatedPhoto.getName(), updatedPhoto.getDescription());
                return ResponseEntity.ok(photoViewDTO);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "No photo exists with this ID."));
            }

        } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", AlbumError.UPDATE_PHOTO_ERROR.toString()));
        }
    }

    
    @DeleteMapping(value = "/{album_id}/photos/{photo_id}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Photo successfully deleted"),
        @ApiResponse(responseCode = "400", description = "Bad Request - No album or photo exists with the provided IDs"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Access denied because the photo does not belong to the album or the album does not belong to the user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error - An unexpected error occurred")
    })
    @Operation(summary = "Delete a photo from an album")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<Map<String, String>> delete_photo(@PathVariable Long album_id, 
        @PathVariable Long photo_id, Authentication authentication) {

        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();
    
            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("message", "Oops! Not your album."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "No album exists with this ID."));
            }

            Optional<Photo> optionalPhoto = photoService.findById(photo_id);
            if(optionalPhoto.isPresent()) {
                Photo photo = optionalPhoto.get();
                if (photo.getAlbum().getId() != album_id) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("message", "The photo does not belong to this album."));
                }
                
                AppUtil.delete_photo_from_path(photo.getFileName(), PHOTOS_FOLDER_NAME, album_id);
                AppUtil.delete_photo_from_path(photo.getFileName(), THUMBNAIL_FOLDER_NAME, album_id);
                photoService.delete(photo);

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body(Collections.singletonMap("message", AlbumSuccess.PHOTO_SUCCESSFULLY_DELETED.toString()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "No photo exists with this ID."));
            }

        } catch (Exception e) {           
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", AlbumError.DELETE_PHOTO_ERROR.toString()));
        }
    }


    @GetMapping("/{album_id}/photos/{photo_id}/download-photo")
    @Operation(summary = "Download a photo")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<?> downloadPhoto(@PathVariable("album_id") Long album_id, 
        @PathVariable("photo_id") Long photo_id, Authentication authentication) {

        return downloadFile(album_id, photo_id, PHOTOS_FOLDER_NAME, authentication);
    }


    @GetMapping("/{album_id}/photos/{photo_id}/download-thumbnail")
    @Operation(summary = "Download a thumbnail")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<?> downloadThumbnail(@PathVariable("album_id") Long album_id, 
        @PathVariable("photo_id") Long photo_id, Authentication authentication) {

        return downloadFile(album_id, photo_id, THUMBNAIL_FOLDER_NAME, authentication);
    }


    public ResponseEntity<?> downloadFile(Long album_id, Long photo_id, String folder_name, 
        Authentication authentication) {

        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionaAlbum = albumService.findById(album_id);
        Album album;
        if (optionaAlbum.isPresent()) {
            album = optionaAlbum.get();
            if (account.getId() != album.getAccount().getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("message", "Oops! Not your album."));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "No album exists with this ID."));
        }

        Optional<Photo> optionalPhoto = photoService.findById(photo_id);
        if (optionalPhoto.isPresent()) {
            Photo photo = optionalPhoto.get();
            if (photo.getAlbum().getId() != album_id) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("message", "The photo does not belong to this album."));
            }

            Resource resource = null;
            try {
                resource = AppUtil.getFileAsResource(album_id, folder_name, photo.getFileName());
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }

            if (resource == null) {
                return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
            }

            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + photo.getOriginalFileName() + "\"";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "No photo exists with this ID."));
        }
    }
}



