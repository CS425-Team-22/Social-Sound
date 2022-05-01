package com.jotreewater.socialsound;

import com.spotify.protocol.types.ImageUri;

public class Sound extends MainActivity {
    private String trackName;
    private String trackUri;
    private String trackArtist;
    private String trackAlbum;
    private ImageUri trackImage;
    private String username;
    private String profilePicture;
    private String key;

    // Constructor
    public Sound(String trackName, String trackArtist, String trackAlbum, String username, String trackUri, ImageUri trackImage, String profilePicture, String key) {
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.trackAlbum = trackAlbum;
        this.username = username;
        this.trackUri = trackUri;
        this.trackImage = trackImage;
        this.profilePicture = profilePicture;
        this.key = key;
    }

    // Getter and Setter
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackUri() {
        return trackUri;
    }

    public void setTrackUri(String uri) {
        this.trackUri = uri;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }

    public String getTrackAlbum() {
        return trackAlbum;
    }

    public void setTrackAlbum(String trackAlbum) {
        this.trackAlbum = trackAlbum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ImageUri getTrackImage() {
        return trackImage;
    }

    public void setAlbum_art(ImageUri trackImage) {
        this.trackImage = trackImage;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
}
