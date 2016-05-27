package com.bazarnazar.cassandramapings.model.joins;

import com.bazarnazar.cassandramapings.annotations.DependentTables;
import com.bazarnazar.cassandramapings.annotations.Order;
import com.datastax.driver.core.ClusteringOrder;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Bazar on 27.05.16.
 */
@Table(name = "video_by_actor")
@DependentTables({ActorByVideo.class})
public class VideoByActor {

    @PartitionKey(0)
    protected UUID actorId;

    @ClusteringColumn(0)
    @Order(order = ClusteringOrder.DESC)
    protected Date releaseDate;

    @ClusteringColumn(1)
    @Order(order = ClusteringOrder.ASC)
    private String title;

    @ClusteringColumn(2)
    @Order(order = ClusteringOrder.DESC)
    private UUID videoId;

    @ClusteringColumn(3)
    private String characterName;

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
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

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(UUID videoId) {
        this.videoId = videoId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}
