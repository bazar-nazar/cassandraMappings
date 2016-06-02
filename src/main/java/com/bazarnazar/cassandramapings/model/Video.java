package com.bazarnazar.cassandramapings.model;

import com.bazarnazar.cassandramapings.annotations.DependentTables;
import com.bazarnazar.cassandramapings.annotations.Generated;
import com.bazarnazar.cassandramapings.annotations.GeneratorType;
import com.bazarnazar.cassandramapings.model.index.VideoByTag;
import com.bazarnazar.cassandramapings.model.joins.ActorByVideo;
import com.bazarnazar.cassandramapings.model.joins.VideoByUser;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Computed;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Bazar on 27.05.16.
 */
@Table(name = "video")
@DependentTables(value = {VideoByTag.class, VideoByUser.class, ActorByVideo.class})
public class Video {

    @PartitionKey
    @Generated(GeneratorType.NOW)
    private UUID videoId;

    @ClusteringColumn
    private UUID userId;

    private String title;

    private String description;

    private Integer releaseYear;

    private String tag;

    private String genres;

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(UUID videoId) {
        this.videoId = videoId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Video))
            return false;

        Video video = (Video) o;

        if (description != null ? !description
                .equals(video.description) : video.description != null)
            return false;
        if (genres != null ? !genres.equals(video.genres) : video.genres != null)
            return false;
        if (releaseYear != null ? !releaseYear
                .equals(video.releaseYear) : video.releaseYear != null)
            return false;
        if (tag != null ? !tag.equals(video.tag) : video.tag != null)
            return false;
        if (title != null ? !title.equals(video.title) : video.title != null)
            return false;
        if (userId != null ? !userId.equals(video.userId) : video.userId != null)
            return false;
        if (videoId != null ? !videoId.equals(video.videoId) : video.videoId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = videoId != null ? videoId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (releaseYear != null ? releaseYear.hashCode() : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (genres != null ? genres.hashCode() : 0);
        return result;
    }
}
