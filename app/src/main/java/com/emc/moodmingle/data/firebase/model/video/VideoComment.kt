package com.emc.moodmingle.data.firebase.model.video

data class VideoComment(
    val id: String = "",
    val videoUrl: String = "",
    val commenterId: String = "",
    val comment: String = "",
    val replies: List<VideoCommentReply> = emptyList(),
    val reactorIds: List<String> = emptyList(),
    val dislikerIds: List<String> = emptyList(),
    val mediaUrls: List<String> = emptyList(),
    val emotion: String = "",
    val anonymous: Boolean = false,
    val supports: List<Support> = emptyList(),
    val triggers: List<Trigger> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class Support(
    val supporterId: String = "",
    val message: String = "",
    val supportType: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Trigger(
    val triggererId: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class VideoCommentReply(
    val id: String = "",
    val videoCommentId: String = "",
    val replierId: String = "",
    val reply: String = "",
    val reactorIds: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)