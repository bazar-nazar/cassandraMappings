package com.bazarnazar.cassandramapings.model.index;

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
@Table(name = "video_by_tag")
public class VideoByTag {

    @PartitionKey
    private String tag;

    @ClusteringColumn(0)
    @Order(order = ClusteringOrder.DESC)
    private Date uploadTimestamp;

    @ClusteringColumn(1)
    private UUID videoId;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
