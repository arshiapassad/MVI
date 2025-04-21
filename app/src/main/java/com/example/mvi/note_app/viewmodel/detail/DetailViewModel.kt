package com.example.mvi.note_app.viewmodel.detail

import academy.nouri.s3_mvi.note_app.data.model.NoteEntity
import academy.nouri.s3_mvi.note_app.data.repository.DetailRepository
import academy.nouri.s3_mvi.note_app.utils.EDUCATION
import academy.nouri.s3_mvi.note_app.utils.HEALTH
import academy.nouri.s3_mvi.note_app.utils.HIGH
import academy.nouri.s3_mvi.note_app.utils.HOME
import academy.nouri.s3_mvi.note_app.utils.LOW
import academy.nouri.s3_mvi.note_app.utils.NORMAL
import academy.nouri.s3_mvi.note_app.utils.WORK
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: DetailRepository) : ViewModel(){
    val detailIntent = Channel<DetailIntent>(UNLIMITED)
    private val _state = MutableStateFlow<DetailState>(DetailState.Idle)
    val state : StateFlow<DetailState> get() = _state

    init {
        handleIntents()
    }

    private fun handleIntents() = viewModelScope.launch {
        detailIntent.consumeAsFlow().collect{ intent ->
            when (intent){
                is DetailIntent.SpinnersList -> fetchingSpinnersList()
                is DetailIntent.SaveNote -> savingData(intent.entity)
                is DetailIntent.NoteDetail -> fetchingNoteDetail(intent.id)
                is DetailIntent.UpdateNote -> updatingData(intent.entity)
            }
        }
    }

    private fun updatingData(entity: NoteEntity) = viewModelScope.launch {
        _state.value = try {
            DetailState.UpdateNote(repository.updateNote(entity))
        } catch (e: Exception){
            DetailState.Error(e.message.toString())
        }
    }

    private fun fetchingNoteDetail(id: Int) = viewModelScope.launch {
        repository.getNote(id).collect{
            _state.value = DetailState.NoteDetail(it)
        }
    }

    private fun fetchingSpinnersList() = viewModelScope.launch{
        val categoriesList = mutableListOf(WORK, EDUCATION, HOME, HEALTH)
        val prioritiesList = mutableListOf(HIGH, NORMAL, LOW)
        _state.value = DetailState.SpinnersData(categoriesList, prioritiesList)
        //_state.emit(DetailState.SpinnersData(categoriesList, prioritiesList))
    }

    private fun savingData(entity: NoteEntity) = viewModelScope.launch {
        _state.value = try {
           DetailState.SaveNote(repository.saveNote(entity))
        } catch (e: Exception){
            DetailState.Error(e.message.toString())
        }
    }
}