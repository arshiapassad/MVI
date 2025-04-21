package com.example.mvi.note_app.viewmodel.detail

import academy.nouri.s3_mvi.note_app.data.model.NoteEntity

sealed class DetailIntent {
    object SpinnersList : DetailIntent()
    data class SaveNote(val entity: NoteEntity) : DetailIntent()
    data class UpdateNote(val entity: NoteEntity) : DetailIntent()
    data class NoteDetail(val id: Int) : DetailIntent()
}