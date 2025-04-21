package com.example.mvi.food_app.view.detail


import academy.nouri.s3_mvi.food_app.data.database.FoodEntity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvi.food_app.data.repository.DetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: DetailRepository) : ViewModel() {

    val detailIntent = Channel<DetailIntent>()
    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state: StateFlow<DetailState> get() = _state

    init {
        handleIntents()
    }

    private fun handleIntents() = viewModelScope.launch {
        detailIntent.consumeAsFlow().collect { intent ->
            when (intent) {
                is DetailIntent.FinishPage -> finishingPage()
                is DetailIntent.FoodDetail -> fetchingFoodDetail(intent.id)
                is DetailIntent.SaveFavorite -> saveFavorite(intent.entity)
                is DetailIntent.DeleteFavorite -> deleteFavorite(intent.entity)
                is DetailIntent.ExistsFavorite -> existsFavorite(intent.id)
            }
        }
    }

    private fun existsFavorite(id: Int) = viewModelScope.launch {
        repository.existsFood(id).collect {
            _state.emit(DetailState.ExistsFavorite(it))
        }
    }

    private fun deleteFavorite(entity: FoodEntity) = viewModelScope.launch {
        _state.emit(DetailState.DeleteFavorite(repository.deleteFood(entity)))
    }

    private fun saveFavorite(entity: FoodEntity) = viewModelScope.launch {
        _state.emit(DetailState.SaveFavorite(repository.saveFood(entity)))
    }

    private fun fetchingFoodDetail(id: Int) = viewModelScope.launch {
        val response = repository.detailFood(id)
        _state.emit(DetailState.Loading)
        when (response.code()) {
            in 200..202 -> {
                _state.emit(DetailState.LoadFood(response.body()!!))
            }
            in 400..499 -> {
                _state.emit(DetailState.Error(""))
            }
            in 500..599 -> {
                _state.emit(DetailState.Error(""))
            }
        }
    }

    private fun finishingPage() = viewModelScope.launch {
        _state.emit(DetailState.FinishPage)
    }
}