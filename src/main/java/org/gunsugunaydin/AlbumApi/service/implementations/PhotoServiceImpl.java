package org.gunsugunaydin.AlbumApi.service.implementations;

import java.util.Optional;
import java.util.List;
import org.gunsugunaydin.AlbumApi.model.Album;
import org.gunsugunaydin.AlbumApi.model.Photo;
import org.gunsugunaydin.AlbumApi.payload.album.PhotoPayloadDTO;
import org.gunsugunaydin.AlbumApi.repository.PhotoRepository;
import org.gunsugunaydin.AlbumApi.service.interfaces.IPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class PhotoServiceImpl implements IPhotoService {
    
    @Autowired
    private PhotoRepository photoRepository;

    @Override
    @Transactional
    public Photo save(Photo photo) {
        return photoRepository.save(photo);
    }

    @Override
    @Transactional
    public Photo uploadNewPhoto(String original_file_name, String generated_photo_name, Album album) {
        Photo photo = new Photo();
        photo.setName(original_file_name);
        photo.setOriginalFileName(original_file_name);
        photo.setFileName(generated_photo_name);
        photo.setAlbum(album);
        return save(photo);
    }

    @Override
    @Transactional
    public Photo updatePhotoInfo(PhotoPayloadDTO photoPayloadDTO, Photo photo) {
        if(photoPayloadDTO.getName() != null && !photoPayloadDTO.getName().equals(photo.getName())) 
        {
            photo.setName(photoPayloadDTO.getName());
        }
        if(photoPayloadDTO.getDescription() != null && !photoPayloadDTO.getDescription().equals(photo.getDescription()))
        {
            photo.setDescription(photoPayloadDTO.getDescription());
        }
        return save(photo);
    }

    @Override
    public Optional<Photo> findById(Long id) {
        return photoRepository.findById(id);
    }

    @Override
    public List<Photo> findByAlbum_id(Long id) {
        return photoRepository.findByAlbum_id(id);
    }

    @Override
    @Transactional
    public void delete(Photo photo) {
        photoRepository.delete(photo);
    }
}
