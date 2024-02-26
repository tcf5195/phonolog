package edu.psgv.sweng861.phonologserver.service;

import edu.psgv.sweng861.phonologserver.entity.Artist;
import edu.psgv.sweng861.phonologserver.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ArtistService {
    private SpotifyApiService spotifyApi;
    private ArtistRepository artistsRepo;

    @Autowired
    public ArtistService(SpotifyApiService spotifyApi, ArtistRepository artistsRepo) {
        this.spotifyApi = spotifyApi;
        this.artistsRepo = artistsRepo;
    }

    private Artist getArtistEntityFromSpotifyResult(se.michaelthelin.spotify.model_objects.specification.Artist foundArtist) {
        Artist artistToAdd = new Artist();

        artistToAdd.setId(foundArtist.getId());
        artistToAdd.setName(foundArtist.getName());
        if (foundArtist.getImages().length > 0) {
            artistToAdd.setImageURL(foundArtist.getImages()[0].getUrl());
        }
        artistToAdd.setGenres(Arrays.asList(foundArtist.getGenres()));
        artistToAdd.setPopularity(foundArtist.getPopularity());

        return artistToAdd;
    }

    public List<Artist> getArtists(@RequestParam(required = false) String name) {
        try {
            List<Artist> artistsFound = new ArrayList<>();

            if (name == null)
                artistsFound.addAll(artistsRepo.findAll());
            else
                artistsFound.addAll(artistsRepo.findByNameContainingIgnoreCase(name));

            if (artistsFound.isEmpty() || (artistsFound.size() < 10 && artistsFound.size() > 1)) {
                // Try Spotify Search
                SearchResult searchResult  = spotifyApi.search(name, ModelObjectType.ARTIST, 20);
                Paging<se.michaelthelin.spotify.model_objects.specification.Artist> artists = searchResult.getArtists();
                se.michaelthelin.spotify.model_objects.specification.Artist[] foundArtists = artists.getItems();

                for (se.michaelthelin.spotify.model_objects.specification.Artist foundArtist : foundArtists) {
                    Artist artistToAdd = getArtistEntityFromSpotifyResult(foundArtist);
                    artistsFound.add(artistToAdd);
                }
            }

            if (!artistsFound.isEmpty()) {
                artistsRepo.saveAll(artistsFound);
            }
            return artistsFound;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    public List<Artist> getExactMatchArtist(@RequestParam(required = false) String name) {
        try {
            List<Artist> artistsFound = new ArrayList<>();

            if (name == null)
                artistsFound.addAll(artistsRepo.findAll());
            else
                artistsFound.addAll(artistsRepo.findByNameIsIgnoreCase(name));

            if (artistsFound.isEmpty()) {
                // Try Spotify Search1
                SearchResult searchResult = spotifyApi.search(name, ModelObjectType.ARTIST, 1);
                Paging<se.michaelthelin.spotify.model_objects.specification.Artist> artists = searchResult.getArtists();
                se.michaelthelin.spotify.model_objects.specification.Artist[] foundArtists = artists.getItems();

                for (se.michaelthelin.spotify.model_objects.specification.Artist foundArtist : foundArtists) {
                    if (foundArtist.getName().equalsIgnoreCase(name)) {
                        Artist artistToAdd = getArtistEntityFromSpotifyResult(foundArtist);
                        artistsFound.add(artistToAdd);
                    }
                }
            }
            if (!artistsFound.isEmpty()) {
                artistsRepo.saveAll(artistsFound);
            }
            return artistsFound;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
    }
}
