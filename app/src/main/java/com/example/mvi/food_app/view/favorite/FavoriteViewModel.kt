package com.example.mvi.food_app.view.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvi.food_app.data.repository.FavoriteRepository
import com.example.mvi.food_app.view.detail.DetailIntent
import com.example.mvi.food_app.view.detail.DetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val repository : FavoriteRepository) : ViewModel(){

    val favoriteIntent = Channel<FavoriteIntent>()
    private val _state = MutableStateFlow<FavoriteState>(FavoriteState.Empty)
    val state: StateFlow<FavoriteState> get() = _state

    init {
        handleIntents()
    }

    private fun handleIntents() = viewModelScope.launch {
        favoriteIntent.consumeAsFlow().collect { intent ->
            when (intent) {
                is FavoriteIntent.LoadFavorite -> fetchingLoadFavorites()
            }
        }
    }

    private fun fetchingLoadFavorites() = viewModelScope.launch{
        repository.foodsList().collect{
            _state.value = if (it.isEmpty()){
                FavoriteState.Empty
            }else{
                FavoriteState.LoadFavorites(it)
            }
        }
    }
}