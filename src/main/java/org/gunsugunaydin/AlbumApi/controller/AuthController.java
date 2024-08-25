package org.gunsugunaydin.AlbumApi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.gunsugunaydin.AlbumApi.model.Account;
import org.gunsugunaydin.AlbumApi.model.Album;
import org.gunsugunaydin.AlbumApi.model.Photo;
import org.gunsugunaydin.AlbumApi.payload.auth.AccountDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.AccountViewDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.AuthoritiesDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.PasswordDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.ProfileDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.TokenDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.UserLoginDTO;
import org.gunsugunaydin.AlbumApi.service.interfaces.IAccountService;
import org.gunsugunaydin.AlbumApi.service.interfaces.IAlbumService;
import org.gunsugunaydin.AlbumApi.service.interfaces.IPhotoService;
import org.gunsugunaydin.AlbumApi.service.interfaces.ITokenService;
import org.gunsugunaydin.AlbumApi.util.AppUtils.AppUtil;
import org.gunsugunaydin.AlbumApi.util.constants.AccountError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Controller", description = "Controller for account management" )
@Slf4j
public class AuthController {

    //These two variables are used for deleting profile-related data
    static final String PHOTOS_FOLDER_NAME = "photos";
    static final String THUMBNAIL_FOLDER_NAME = "thumbnails";

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private ITokenService tokenService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IAlbumService albumService;

    @Autowired
    private IPhotoService photoService;
   

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
            return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
        } catch (Exception e) {
            log.debug(AccountError.TOKEN_GENERATION_ERROR.toString() + ": " + e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(value = "/users/add", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account successfully added"),
        @ApiResponse(responseCode = "400", description = "Invalid email or password length. Ensure email format is correct and password length is between 6 to 20 characters."),
        @ApiResponse(responseCode = "500", description = "Internal server error during account addition")
    }) 
    @Operation(summary = "Add a new user")
    public ResponseEntity<Map<String, String>> addUser(@Valid @RequestBody AccountDTO accountDTO) {
        try {

            accountService.addNewUser(accountDTO);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "ACCOUNT_ADDED");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.debug(AccountError.ADD_ACCOUNT_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "An error occurred during account addition."));
        }
    }


    @GetMapping(value = "/users/list", produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of users"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access. Token is missing."),
        @ApiResponse(responseCode = "403", description = "Forbidden. Token is invalid or you are not authorized to access this resource.")
    })
    @Operation(summary = "List users")
    @SecurityRequirement(name = "album-api")
    public List<AccountViewDTO> Users() {
        List<AccountViewDTO> accounts = new ArrayList<>();
        for (Account account : accountService.findAll()) {
            accounts.add(new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities()));
        }
        return accounts;
    }


    @PutMapping(value = "/users/{user_id}/update-authorities", produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated authorities"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access. Token is missing."),
        @ApiResponse(responseCode = "400", description = "Invalid user ID. User with the given ID does not exist."),
        @ApiResponse(responseCode = "403", description = "Forbidden. You are not authorized to update authorities for this user.")
    })
    @Operation(summary = "Update authorities")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<AccountViewDTO> update_auth(@Valid @RequestBody AuthoritiesDTO authoritiesDTO,
            @PathVariable long user_id) {

        Optional<Account> optionalAccount = accountService.findById(user_id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            Account configuredAccount = accountService.configureAuthorities(authoritiesDTO, account);

            AccountViewDTO accountViewDTO = new AccountViewDTO(configuredAccount.getId(), configuredAccount.getEmail(),
                    configuredAccount.getAuthorities());
            return ResponseEntity.ok(accountViewDTO);
        }
        return new ResponseEntity<AccountViewDTO>(new AccountViewDTO(), HttpStatus.BAD_REQUEST);
    }


    @GetMapping(value = "/profile", produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access. Token is missing."),
        @ApiResponse(responseCode = "403", description = "Forbidden access. Token Error: You are not authorized to access this profile.")
    })
    @Operation(summary = "View profile")
    @SecurityRequirement(name = "album-api")
    public ProfileDTO profile(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        ProfileDTO profileDTO = new ProfileDTO(account.getId(), account.getEmail(), account.getAuthorities());
        return profileDTO;
    }


    @PutMapping(value = "/profile/update-password", produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access. Token is missing."),
        @ApiResponse(responseCode = "403", description = "Forbidden access. Token Error: You are not authorized to update this profile."),
        @ApiResponse(responseCode = "400", description = "Bad request. Invalid password.")
    })
    @Operation(summary = "Update profile password")
    @SecurityRequirement(name = "album-api")
    public AccountViewDTO update_password(@Valid @RequestBody PasswordDTO passwordDTO, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        Account accountWithNewPassword = accountService.updatePassword(passwordDTO, account);

        AccountViewDTO accountViewDTO = new AccountViewDTO(accountWithNewPassword.getId(), accountWithNewPassword.getEmail(),
                accountWithNewPassword.getAuthorities());
        return accountViewDTO;
    }


    @DeleteMapping(value = "/profile/delete")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Profile deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access. Token is missing."),
        @ApiResponse(responseCode = "403", description = "Forbidden access. You are not authorized to delete this profile."),
        @ApiResponse(responseCode = "400", description = "Bad request. User profile not found.")
    })
    @Operation(summary = "Delete profile")
    @SecurityRequirement(name = "album-api")
    public ResponseEntity<String> delete_profile(Authentication authentication) {
        String email = authentication.getName();
        try {
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            if (optionalAccount.isPresent()) {
                Long accountId = optionalAccount.get().getId();
                List<Album> albums = albumService.findByAccount_id(accountId);
                
                for (Album album : albums) {
                    List<Photo> photos = photoService.findByAlbum_id(album.getId());
                    for (Photo photo : photos) {
                        photoService.delete(photo);
                        AppUtil.delete_photo_from_path(photo.getFileName(), PHOTOS_FOLDER_NAME, album.getId());
                        AppUtil.delete_photo_from_path(photo.getFileName(), THUMBNAIL_FOLDER_NAME, album.getId());
                    }
                    albumService.delete(album);
                    AppUtil.delete_album_directory(album.getId()); 
                }
                
                accountService.deleteById(accountId);
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.badRequest().body("User profile not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the profile");
        }
    }
}    

