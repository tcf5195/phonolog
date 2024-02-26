package edu.psgv.sweng861.phonologserver.service;

import edu.psgv.sweng861.phonologserver.entity.Album;
import edu.psgv.sweng861.phonologserver.entity.Artist;
import edu.psgv.sweng861.phonologserver.entity.Track;
import edu.psgv.sweng861.phonologserver.repository.AlbumRepository;
import edu.psgv.sweng861.phonologserver.repository.ArtistRepository;
import edu.psgv.sweng861.phonologserver.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TrackService {
    private SpotifyApiService spotifyApi;
    private AlbumRepository albumsRepo;
    private ArtistRepository artistsRepo;
    private TrackRepository tracksRepo;

    @Autowired
    public TrackService(
            SpotifyApiService spotifyApi,
            ArtistRepository artistRepository,
            AlbumRepository albumRepository,
            TrackRepository trackRepository
            ) {
        this.spotifyApi = spotifyApi;
        this.artistsRepo = artistRepository;
        this.albumsRepo = albumRepository;
        this.tracksRepo = trackRepository;
    }

    private Track getTrackEntityFromSpotifyResult(se.michaelthelin.spotify.model_objects.specification.Track foundTrack) {
        Track trackToAdd = new Track();
        ArtistSimplified artist = foundTrack.getArtists()[0];
        se.michaelthelin.spotify.model_objects.specification.AlbumSimplified album = foundTrack.getAlbum();

        se.michaelthelin.spotify.model_objects.specification.Album fullAlbum =
                spotifyApi.getFullAlbum(album.getId());

        se.michaelthelin.spotify.model_objects.specification.Artist fullArtist =
                spotifyApi.getFullArtist(artist.getId());

        Artist artistToAdd = Artist.builder()
                .id(artist.getId())
                .name(artist.getName())
                .imageURL(fullArtist.getImages().length > 0 ? fullArtist.getImages()[0].getUrl() : null)
                .build();

        Album albumToAdd = Album.builder()
                .id(album.getId())
                .artist(artistToAdd)
                .name(album.getName())
                .genres(Arrays.asList(fullAlbum.getGenres()))
                .imageURL(album.getImages().length > 0 ? album.getImages()[0].getUrl() : null)
                .popularity(fullAlbum.getPopularity())
                .totalTracks(fullAlbum.getTracks().getItems().length)
                .build();

        if (artistsRepo.findById(artist.getId()).isEmpty()) {
            artistsRepo.save(artistToAdd);
        }

        if (albumsRepo.findById(album.getId()).isEmpty()) {
            albumsRepo.save(albumToAdd);
        }

        trackToAdd.setId(foundTrack.getId());
        trackToAdd.setName(foundTrack.getName());
        trackToAdd.setArtist(artistToAdd);
        trackToAdd.setAlbum(albumToAdd);

        return trackToAdd;
    }

    public List<Track> getTracks(String name) {
        try {
            List<Track> tracksFound = new ArrayList<>();

            if (name == null)
                tracksFound.addAll(tracksRepo.findAll());
            else
                tracksFound.addAll(tracksRepo.findByNameContainingIgnoreCase(name));

            if (tracksFound.isEmpty()) {
                SearchResult searchResult  = spotifyApi.search(name, ModelObjectType.TRACK, 40);
                Paging<se.michaelthelin.spotify.model_objects.specification.Track> tracks = searchResult.getTracks();

                se.michaelthelin.spotify.model_objects.specification.Track[] foundTracks = tracks.getItems();

                for (se.michaelthelin.spotify.model_objects.specification.Track foundTrack : foundTracks) {
                    Track trackToAdd = getTrackEntityFromSpotifyResult(foundTrack);
                    tracksFound.add(trackToAdd);
                }
            }

            if (!tracksFound.isEmpty()) {
                tracksRepo.saveAll(tracksFound);
            }
            return tracksFound;
        } catch (Exception e) {
            return null;
        }
    }
}
