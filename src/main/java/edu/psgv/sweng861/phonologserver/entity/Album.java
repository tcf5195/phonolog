package edu.psgv.sweng861.phonologserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_album")
public class Album {
    @Id
    private String id;

    @Column
    private String name;

    @Column(name = "total_tracks")
    private int totalTracks;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "image")
    private String imageURL;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Artist artist;

    @Column
    @ElementCollection
    private List<String> genres;

    @Column(name = "popularity_score")
    private int popularity;
}
