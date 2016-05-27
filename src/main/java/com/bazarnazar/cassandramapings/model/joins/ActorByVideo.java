package com.bazarnazar.cassandramapings.model.joins;

import com.bazarnazar.cassandramapings.annotations.DependentTables;
import com.bazarnazar.cassandramapings.annotations.Static;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Bazar on 27.05.16.
 */
@Table(name = "actor_by_video")
@DependentTables({VideoByActor.class, VideosByActorGenre.class})
public class ActorByVideo {

    @PartitionKey
    private UUID videoId;

    @ClusteringColumn(0)
    private UUID actorId;

    @ClusteringColumn(1)
    private String characterName;

    @Static
    private Date releaseDate;

    @Static
    private String title;

    @Static
    private String genre;

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(UUID videoId) {
        this.videoId = videoId;
    }

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
