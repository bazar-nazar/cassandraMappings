package com.bazarnazar.cassandramapings.model.joins;

import com.bazarnazar.cassandramapings.annotations.DependentTables;
import com.bazarnazar.cassandramapings.annotations.Order;
import com.bazarnazar.cassandramapings.model.Video;
import com.datastax.driver.core.ClusteringOrder;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Bazar on 27.05.16.
 */
@Table(name = "video_by_user")
@DependentTables({Video.class})
public class VideoByUser {

    @PartitionKey
    private UUID userId;

    @ClusteringColumn
    @Order(order = ClusteringOrder.DESC)
    private Date uploadTimestamp;

    private UUID videoId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Date getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(Date uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(UUID videoId) {
        this.videoId = videoId;
    }
}
