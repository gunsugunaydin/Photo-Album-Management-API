package org.gunsugunaydin.AlbumApi.service.interfaces;

import java.util.List;
import java.util.Optional;
import org.gunsugunaydin.AlbumApi.model.Account;
import org.gunsugunaydin.AlbumApi.model.Album;
import org.gunsugunaydin.AlbumApi.payload.album.AlbumPayloadDTO;

public interface IAlbumService {
    
    public Album save(Album album);
    public Album addNewAlbum(AlbumPayloadDTO albumPayloadDTO, Account account);
    public Album updateAlbum(AlbumPayloadDTO albumPayloadDTO, Album album);
    public List<Album> findByAccount_id(Long id);
    public Optional<Album> findById(Long id);
    public void delete(Album album);
}
