package org.gunsugunaydin.AlbumApi.service.implementations;

import java.util.List;
import java.util.Optional;
import org.gunsugunaydin.AlbumApi.model.Account;
import org.gunsugunaydin.AlbumApi.model.Album;
import org.gunsugunaydin.AlbumApi.payload.album.AlbumPayloadDTO;
import org.gunsugunaydin.AlbumApi.repository.AlbumRepository;
import org.gunsugunaydin.AlbumApi.service.interfaces.IAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class AlbumServiceImpl implements IAlbumService {
    
    @Autowired
    private AlbumRepository albumRepository;

    @Override
    @Transactional
    public Album save(Album album) {
        return albumRepository.save(album);
    }

    @Override
    @Transactional
    public Album addNewAlbum(AlbumPayloadDTO albumPayloadDTO, Account account) {
        Album album = new Album();
        album.setName(albumPayloadDTO.getName());
        album.setDescription(albumPayloadDTO.getDescription());
        album.setAccount(account);
        return save(album);        
    }

    @Override
    @Transactional
    public Album updateAlbum(AlbumPayloadDTO albumPayloadDTO, Album album) {
        if(albumPayloadDTO.getName() != null && !albumPayloadDTO.getName().equals(album.getName()))
        {
            album.setName(albumPayloadDTO.getName());
        }
        if(albumPayloadDTO.getDescription() != null && !albumPayloadDTO.getDescription().equals(album.getDescription()))
        {
            album.setDescription(albumPayloadDTO.getDescription());
        }
        return save(album);
    }

    @Override
    public List<Album> findByAccount_id(Long id) {
        return albumRepository.findByAccount_id(id);
    }

    @Override
    public Optional<Album> findById(Long id) {
        return  albumRepository.findById(id);
    }

    @Override
    @Transactional
    public void delete(Album album) {
       albumRepository.delete(album);
    }
}
