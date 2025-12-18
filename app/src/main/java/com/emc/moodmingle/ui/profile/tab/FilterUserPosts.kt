package com.emc.moodmingle.ui.profile.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.model.post.user.CombinedPost
import com.emc.moodmingle.ui.profile.UserPostContent
import com.emc.moodmingle.ui.settings.saved.utils.NoResult
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase

@Composable
fun FilterUserPosts(
    filterType: String,
    posts: List<CombinedPost>,
    onChatClick: (String, String) -> Unit
) {
    val postViewModel = hiltViewModel<PostViewModelFirebase>()
    var filteredPosts by remember { mutableStateOf(emptyList<CombinedPost>()) }

    filteredPosts = if (filterType == "TEXT") {
        filterByText(posts)
    } else {
        filterByMedia(posts)
    }

    if (filteredPosts.isEmpty()) {
        NoResult(R.drawable.empty, "No ${filterType.lowercase()} posts.")
    } else {
        filteredPosts.forEach { post ->
            UserPostContent(
                post,
                postViewModel,
                onChatClick
            )
        }
    }
}

@Composable
private fun filterByText(posts: List<CombinedPost>): List<CombinedPost> {
    return posts.filter { post -> post.postEntity?.urls?.isEmpty() == true }
}

@Composable
private fun filterByMedia(posts: List<CombinedPost>): List<CombinedPost> {
    return posts.filter { post -> post.postEntity?.urls?.isNotEmpty() == true }
}