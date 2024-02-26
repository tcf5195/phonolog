package edu.psgv.sweng861.phonologserver;

import edu.psgv.sweng861.phonologserver.properties.SpotifyClientProps;
import edu.psgv.sweng861.phonologserver.service.SpotifyApiService;
import org.apache.hc.core5.http.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;

@Configuration
public class Container {

    @Bean
    @Scope("singleton")
    public SpotifyApi spotifyApi(SpotifyClientProps spotifyClientProps) {
        String clientId = spotifyClientProps.getClientId();
        String clientSecret = spotifyClientProps.getClientSecret();
        return new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).build();
    }

    @Bean
    @Scope("singleton")
    public ClientCredentials spotifyClientCredentials(SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException {
        ClientCredentials clientCredentials = null;
        try {
            ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
            clientCredentials = clientCredentialsRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {

        }
        return clientCredentials;
    }

    @Bean
    @Scope("singleton")
    public SpotifyApiService spotifyApiService(SpotifyApi spotifyApi, ClientCredentials clientCredentials) {
        return new SpotifyApiService(spotifyApi, clientCredentials);
    }
}
