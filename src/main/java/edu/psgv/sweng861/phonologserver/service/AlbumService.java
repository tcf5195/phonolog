package edu.psgv.sweng861.phonologserver.service;

import edu.psgv.sweng861.phonologserver.entity.Album;
import edu.psgv.sweng861.phonologserver.entity.Artist;
import edu.psgv.sweng861.phonologserver.repository.AlbumRepository;
import edu.psgv.sweng861.phonologserver.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AlbumService {
    private SpotifyApiService spotifyApi;
    private AlbumRepository albumsRepo;
    private ArtistRepository artistsRepo;

    @Autowired
    public AlbumService(
            SpotifyApiService spotifyApiService,
            AlbumRepository albumRepository,
            ArtistRepository artistsRepository
    ) {
        this.spotifyApi = spotifyApiService;
        this.albumsRepo = albumRepository;
        this.artistsRepo = artistsRepository;
    }

    private Album getAlbumEntityFromSpotifyResult(AlbumSimplified foundAlbum) {
        Album albumToAdd = new Album();
        ArtistSimplified artist = foundAlbum.getArtists()[0];
        Artist artistToAdd = Artist.builder().id(artist.getId()).name(artist.getName()).build();
        if (artistsRepo.findById(artist.getId()).isEmpty()){
            artistsRepo.save(artistToAdd);
        }

        se.michaelthelin.spotify.model_objects.specification.Album fullAlbum =
                spotifyApi.getFullAlbum(foundAlbum.getId());

        albumToAdd.setId(foundAlbum.getId());
        albumToAdd.setName(foundAlbum.getName());
        if (foundAlbum.getImages().length > 0) {
            albumToAdd.setImageURL(foundAlbum.getImages()[0].getUrl());
        }
        albumToAdd.setArtist(Artist.builder().id(artist.getId()).name(artist.getName()).build());
        albumToAdd.setGenres(Arrays.asList(fullAlbum.getGenres()));
        albumToAdd.setPopularity(fullAlbum.getPopularity());
        albumToAdd.setReleaseDate(fullAlbum.getReleaseDate());
        albumToAdd.setTotalTracks(fullAlbum.getTracks().getItems().length);

        return albumToAdd;
    }

    public List<Album> getAlbums(String name) {
        try {
            List<Album> albumsFound = new ArrayList<>();

            if (name == null)
                albumsFound.addAll(albumsRepo.findAll());
            else
                albumsFound.addAll(albumsRepo.findByNameContainingIgnoreCase(name));

            if (albumsFound.isEmpty()) {
                // Try Spotify Search
                SearchResult searchResult  = spotifyApi.search(name, ModelObjectType.ALBUM, 10);
                Paging<AlbumSimplified> albums = searchResult.getAlbums();
                AlbumSimplified[] foundAlbums = albums.getItems();

                for (AlbumSimplified foundAlbum : foundAlbums) {
                    Album albumToAdd = getAlbumEntityFromSpotifyResult(foundAlbum);
                    albumsFound.add(albumToAdd);
                }
            }

            if (!albumsFound.isEmpty()) {
                albumsRepo.saveAll(albumsFound);
            }
            return albumsFound;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Album> getAlbumsByArtist(String artistName) {
        try {
            List<Album> albumsFound = new ArrayList<>();

            if (artistName == null)
                albumsFound.addAll(albumsRepo.findAll());
            else
                albumsFound.addAll(albumsRepo.findByArtistNameIgnoreCase(artistName));

            if (albumsFound.isEmpty() && artistName != null) {
                // Try Spotify Search
                String searchQuery = "artist:" + URLEncoder.encode(artistName, StandardCharsets.UTF_8);
                SearchResult searchResult  = spotifyApi.search(searchQuery, ModelObjectType.ALBUM, 40);
                Paging<AlbumSimplified> albums = searchResult.getAlbums();
                AlbumSimplified[] foundAlbums = albums.getItems();

                for (AlbumSimplified foundAlbum : foundAlbums) {
                    Album albumToAdd = getAlbumEntityFromSpotifyResult(foundAlbum);
                    albumsFound.add(albumToAdd);
                }
            }

            if (!albumsFound.isEmpty()) {
                albumsRepo.saveAll(albumsFound);

            }
            return albumsFound;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
