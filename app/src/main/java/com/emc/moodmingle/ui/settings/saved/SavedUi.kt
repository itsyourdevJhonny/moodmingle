package com.emc.moodmingle.ui.settings.saved

import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase

data class SavedUi(
    val save: SaveEntityFirebase,
    val post: PostEntityFirebase?,
    val user: UserEntityFirebase?
)
