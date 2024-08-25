package org.gunsugunaydin.AlbumApi.service.interfaces;

import java.util.Optional;
import org.gunsugunaydin.AlbumApi.model.Album;
import org.gunsugunaydin.AlbumApi.model.Photo;
import org.gunsugunaydin.AlbumApi.payload.album.PhotoPayloadDTO;

import java.util.List;

public interface IPhotoService {
    
    public Photo save(Photo photo);
    public Photo uploadNewPhoto(String original_file_name, String generated_photo_name, Album album);
    public Photo updatePhotoInfo(PhotoPayloadDTO photoPayloadDTO, Photo photo);
    public Optional<Photo> findById(Long id);
    public List<Photo> findByAlbum_id(Long id);
    public void delete(Photo photo);
}
