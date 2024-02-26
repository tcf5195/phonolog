package edu.psgv.sweng861.phonologserver.repository;

import edu.psgv.sweng861.phonologserver.entity.Album;
import edu.psgv.sweng861.phonologserver.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String> {
    List<Artist> findByNameContainingIgnoreCase(String name);
    List<Artist> findByNameIsIgnoreCase(String name);
}
