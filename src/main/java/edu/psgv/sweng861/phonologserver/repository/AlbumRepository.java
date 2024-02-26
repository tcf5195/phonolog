package edu.psgv.sweng861.phonologserver.repository;

import edu.psgv.sweng861.phonologserver.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String> {
    List<Album> findByNameContainingIgnoreCase(String name);
    List<Album> findByArtistNameIgnoreCase(String artistName);
}
