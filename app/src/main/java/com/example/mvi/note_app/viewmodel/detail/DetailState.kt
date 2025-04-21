package com.example.mvi.note_app.viewmodel.detail

import academy.nouri.s3_mvi.note_app.data.model.NoteEntity

sealed class DetailState {
    object Idle : DetailState()
    data class SpinnersData(val categoriesList: MutableList<String>,val prioritiesList: MutableList<String>): DetailState()
    data class Error(val message: String) : DetailState()
    data class SaveNote(val unit: Unit) : DetailState()
    data class UpdateNote(val unit: Unit) : DetailState()
    data class NoteDetail(val entity: NoteEntity) : DetailState()
}