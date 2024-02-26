package edu.psgv.sweng861.phonologserver.repository;

import edu.psgv.sweng861.phonologserver.entity.Album;
import edu.psgv.sweng861.phonologserver.entity.Artist;
import edu.psgv.sweng861.phonologserver.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, String> {
    List<Track> findByNameContainingIgnoreCase(String name);
}
