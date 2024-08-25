package org.gunsugunaydin.AlbumApi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Photo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name= "photo_id")
    private Long id;

    @Column(name= "name")
    private String name;

    @Column(name= "description")
    private String description;

    @Column(name= "original_file_name")
    private String originalFileName;

    @Column(name= "file_name")
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "album_id", referencedColumnName= "album_id", nullable = false)
    private Album album;
}
