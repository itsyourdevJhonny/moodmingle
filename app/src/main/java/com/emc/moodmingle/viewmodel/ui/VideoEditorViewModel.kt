package com.emc.moodmingle.viewmodel.ui

import androidx.lifecycle.ViewModel
import com.emc.moodmingle.ui.dailymood.media.video.VideoEditorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class VideoEditorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(VideoEditorState())
    val state = _state.asStateFlow()

    fun update(transform: (VideoEditorState) -> VideoEditorState) {
        _state.update(transform)
    }
}