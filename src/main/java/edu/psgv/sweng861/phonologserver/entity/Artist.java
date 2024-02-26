package edu.psgv.sweng861.phonologserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_artist")
public class Artist {
    @Id
    private String id;

    @Column
    private String name;

    @Column
    @ElementCollection
    private List<String> genres;

    @Column(name = "image")
    private String imageURL;

    @Column(name = "popularity_score")
    private int popularity;
}
