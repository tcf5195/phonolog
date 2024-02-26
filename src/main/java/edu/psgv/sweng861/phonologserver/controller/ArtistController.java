package edu.psgv.sweng861.phonologserver.controller;

import edu.psgv.sweng861.phonologserver.entity.Artist;
import edu.psgv.sweng861.phonologserver.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ArtistController {
    private ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/artists")
    public ResponseEntity<List<Artist>> getArtists(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean exactMatch
    ) {
        List<Artist> artistsFound = null;
        if (!exactMatch)
            artistsFound = artistService.getArtists(name);
        else {
            artistsFound = artistService.getExactMatchArtist(name);
        }

        if (artistsFound == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (artistsFound.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(artistsFound, HttpStatus.OK);
    }
}
