package com.example.mvi.food_app.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvi.food_app.data.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ListViewModel @Inject constructor(private val repository: ListRepository) : ViewModel(){

    val intentChannel = Channel<ListIntent>()
    private val _state = MutableStateFlow<ListState>(ListState.Idle)
    val state: StateFlow<ListState> get() = _state

    init {
        handelIntents()
    }

    private fun handelIntents() = viewModelScope.launch {
        intentChannel.consumeAsFlow().collect{ intent ->
            when(intent){
                is ListIntent.LoadFiltersLetters -> fetchingFiltersList()
                is ListIntent.LoadRandom -> fetchingRandomFood()
                is ListIntent.LoadCategoriesList -> fetchingCategoriesList()
                is ListIntent.LoadFoods -> fetchingFoodsList(intent.letter)
                is ListIntent.LoadSearchFoods -> fetchingSearchFood(intent.search)
                is ListIntent.LoadFoodsByCategory -> fetchingFoodsByCategory(intent.category)
            }
        }
    }

    private fun fetchingFoodsByCategory(category: String) = viewModelScope.launch {
        val response = repository.foodsByCategory(category)
        _state.emit(ListState.LoadingFoods)
        when(response.code()){
            in 200..202 -> {
                _state.value = if (response.body()!!.meals != null){
                    ListState.FoodsList(response.body()!!.meals!!)
                }else{
                    ListState.Empty
                }
            }
            in 400..499 -> {
                _state.emit(ListState.Error(""))
            }
            in 500..599 -> {
                _state.emit(ListState.Error(""))
            }
        }
    }

    private fun fetchingSearchFood(search: String) = viewModelScope.launch {
        val response = repository.searchFood(search)
        _state.emit(ListState.LoadingFoods)
        when(response.code()){
            in 200..202 -> {
                _state.value = if (response.body()!!.meals != null){
                    ListState.FoodsList(response.body()!!.meals!!)
                }else{
                    ListState.Empty
                }
            }
            in 400..499 -> {
                _state.emit(ListState.Error(""))
            }
            in 500..599 -> {
                _state.emit(ListState.Error(""))
            }
        }
    }

    private fun fetchingFoodsList(letter: String) = viewModelScope.launch {
        val response = repository.foodsList(letter)
        _state.emit(ListState.LoadingFoods)
        when(response.code()){
            in 200..202 -> {
                _state.value = if (response.body()!!.meals != null){
                    ListState.FoodsList(response.body()!!.meals!!)
                }else{
                    ListState.Empty
                }
            }
            in 400..499 -> {
                _state.emit(ListState.Error(""))
            }
            in 500..599 -> {
                _state.emit(ListState.Error(""))
            }
        }
    }

    private fun fetchingCategoriesList() = viewModelScope.launch {
        val response = repository.categoriesList()
        _state.emit(ListState.LoadingCategory)
        when(response.code()){
            in 200..202 -> {
                _state.emit(ListState.CategoriesList(response.body()!!.categories))
            }
            in 400..499 -> {
                _state.emit(ListState.Error(""))
            }
            in 500..599 -> {
                _state.emit(ListState.Error(""))
            }
        }
    }

    private suspend fun fetchingRandomFood() {
        val response = repository.randomFood()
        when (response.code()) {
            in 200..202 -> {
                _state.emit(ListState.RandomFood(response.body()?.meals?.get(0)))
            }
            in 400..499 -> {
                _state.emit(ListState.Error(""))
            }
            in 500..599 -> {
                _state.emit(ListState.Error(""))
            }
        }
    }

    private suspend fun fetchingFiltersList() {
        val list = listOf('A'..'Z').flatten().toMutableList()
        _state.emit(ListState.FilterLetters(list))
    }
}