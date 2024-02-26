package edu.psgv.sweng861.phonologserver.service;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistRequest;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;

import java.io.IOException;

@Service
public class SpotifyApiService {
    private final SpotifyApi spotifyApi;

    @Autowired
    public SpotifyApiService(
            SpotifyApi spotifyApi,
            ClientCredentials clientCredentials
    ) {
        this.spotifyApi = spotifyApi;
        this.spotifyApi.setAccessToken(clientCredentials.getAccessToken());
    }

    public SearchResult search(String query, ModelObjectType modelObjectType, Integer limit) {
        String type = modelObjectType.getType();
        int resultLimit = limit == null ? 20 : limit;
        SearchResult res = null;
        try {
            SearchItemRequest searchItemRequest = spotifyApi.searchItem(query, type).limit(resultLimit).build();
            res = searchItemRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return res;
    }

    public Album getFullAlbum(String id) {
        Album res = null;
        GetAlbumRequest getAlbumRequest = spotifyApi.getAlbum(id).build();
        try {
            res = getAlbumRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return res;
    }

    public Artist getFullArtist(String id) {
        Artist res = null;
        GetArtistRequest getArtistRequest = spotifyApi.getArtist(id).build();
        try {
            res = getArtistRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return res;
    }

}
