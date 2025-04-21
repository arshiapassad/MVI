package com.example.mvi.note_app.viewmodel.main

import academy.nouri.s3_mvi.note_app.data.model.NoteEntity

sealed class MainState {
    object Empty : MainState()
    data class LoadNotes(val list :MutableList<NoteEntity>) : MainState()
    data class DeleteNote(val unit: Unit) : MainState()
    data class GoToDetail(val id: Int) : MainState()
}