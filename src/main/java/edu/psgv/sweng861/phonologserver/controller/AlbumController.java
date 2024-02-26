package edu.psgv.sweng861.phonologserver.controller;

import edu.psgv.sweng861.phonologserver.entity.Album;
import edu.psgv.sweng861.phonologserver.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AlbumController {
    private AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/albums")
    public ResponseEntity<List<Album>> getAlbums(@RequestParam(required = false) String name) {
        List<Album> albumsFound = albumService.getAlbums(name);

        if (albumsFound == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (albumsFound.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(albumsFound, HttpStatus.OK);

    }

    @GetMapping("/albumsByArtist")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@RequestParam(required = false) String artistName) {
        List<Album> albumsFound = albumService.getAlbumsByArtist(artistName);

        if (albumsFound == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (albumsFound.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(albumsFound, HttpStatus.OK);
    }
}
