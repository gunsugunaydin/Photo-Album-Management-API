package org.gunsugunaydin.AlbumApi.repository;

import java.util.List;
import org.gunsugunaydin.AlbumApi.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album,Long> {

    List<Album> findByAccount_id(long id);    
}
