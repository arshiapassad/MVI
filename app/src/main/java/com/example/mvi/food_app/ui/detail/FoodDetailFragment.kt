package com.example.mvi.food_app.ui.detail

import academy.nouri.s3_mvi.food_app.data.database.FoodEntity
import academy.nouri.s3_mvi.food_app.utils.isVisible
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.mvi.R
import com.example.mvi.databinding.FragmentFoodDetailBinding
import com.example.mvi.food_app.utils.network.ConnectivityStatus
import com.example.mvi.food_app.utils.network.NetworkConnectivity
import com.example.mvi.food_app.view.detail.DetailIntent
import com.example.mvi.food_app.view.detail.DetailState
import com.example.mvi.food_app.view.detail.DetailViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class FoodDetailFragment : Fragment() {

    //Binding
    private var _binding: FragmentFoodDetailBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var entity: FoodEntity


    @Inject
    lateinit var networkConnectivity: NetworkConnectivity

    //Other
    private val viewModel: DetailViewModel by viewModels()
    private val args: FoodDetailFragmentArgs by navArgs()
    private var foodId = 0
    private var isFavorite = false


    enum class PageState { EMPTY, NETWORK, NONE }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodDetailBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //get id
        foodId = args.foodId
        //InitViews
        binding?.apply {
            //Back
            detailBack.setOnClickListener { lifecycleScope.launch { viewModel.detailIntent.send(DetailIntent.FinishPage) } }
            //Intents
            lifecycleScope.launch {
                //Send on channel
                viewModel.detailIntent.send(DetailIntent.FoodDetail(foodId))
                viewModel.detailIntent.send(DetailIntent.ExistsFavorite(foodId))
                //Load data
                viewModel.state.collect{ state ->
                    when(state){
                        is DetailState.FinishPage -> findNavController().navigateUp()
                        is DetailState.Loading -> detailLoading.isVisible(true,detailContentLay)
                        is DetailState.LoadFood -> {
                            detailLoading.isVisible(false, detailContentLay)
                            //Set data
                            state.data.meals?.get(0)?.let { itMeal ->
                                //Entity
                                entity.id = itMeal.idMeal!!.toInt()
                                entity.title = itMeal.strMeal.toString()
                                entity.img = itMeal.strMealThumb.toString()
                                //Update UI
                                foodCoverImg.load(itMeal.strMealThumb) {
                                    crossfade(true)
                                    crossfade(500)
                                }
                                foodCategoryTxt.text = itMeal.strCategory
                                foodAreaTxt.text = itMeal.strArea
                                foodTitleTxt.text = itMeal.strMeal
                                foodDescTxt.text = itMeal.strInstructions
                                //Play
                                if (itMeal.strYoutube != null) {
                                    foodPlayImg.visibility = View.VISIBLE
                                    /*foodPlayImg.setOnClickListener {
                                        val videoId = itMeal.strYoutube.split("=")[1]
                                        Intent(requireContext(), PlayerActivity::class.java).also {
                                            it.putExtra(VIDEO_ID, videoId)
                                            startActivity(it)
                                        }
                                    }*/
                                } else {
                                    foodPlayImg.visibility = View.GONE
                                }
                                //Source
                                if (itMeal.strSource != null) {
                                    foodSourceImg.visibility = View.VISIBLE
                                    foodSourceImg.setOnClickListener {
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(itMeal.strSource)))
                                    }
                                } else {
                                    foodSourceImg.visibility = View.GONE
                                }
                                //Json Array
                                val jsonData = JSONObject(Gson().toJson(state.data))
                                val meals = jsonData.getJSONArray("meals")
                                val meal = meals.getJSONObject(0)
                                //Ingredient
                                for (i in 1..15) {
                                    val ingredient = meal.getString("strIngredient$i")
                                    if (ingredient.isNullOrEmpty().not()) {
                                        ingredientsTxt.append("$ingredient\n")
                                    }
                                }
                                //Measure
                                for (i in 1..15) {
                                    val measure = meal.getString("strMeasure$i")
                                    if (measure.isNullOrEmpty().not()) {
                                        measureTxt.append("$measure\n")
                                    }
                                }
                            }
                        }

                        is DetailState.SaveFavorite -> {

                        }
                        is DetailState.DeleteFavorite -> {

                        }
                        is DetailState.ExistsFavorite -> {
                            isFavorite = state.exists
                            if (state.exists) {
                                detailFav.setColorFilter(ContextCompat.getColor(requireContext(), R.color.tartOrange))
                            } else {
                                detailFav.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
                            }
                        }
                        is DetailState.Error -> {
                            detailLoading.isVisible(false,detailContentLay)
                            Toast.makeText(requireContext(),state.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            //Save / Delete
            detailFav.setOnClickListener {
                lifecycleScope.launch {
                    if (!isFavorite) {
                        viewModel.detailIntent.send(DetailIntent.SaveFavorite(entity))
                    } else {
                        viewModel.detailIntent.send(DetailIntent.DeleteFavorite(entity))
                    }
                }
            }
            //Check internet
            lifecycleScope.launch {
                networkConnectivity.observe().collect {
                    when (it) {
                        ConnectivityStatus.Status.Available -> {
                            checkConnectionOrEmpty(false, PageState.NONE)
                            //Send on channel
                            viewModel.detailIntent.send(DetailIntent.FoodDetail(foodId))
                            viewModel.detailIntent.send(DetailIntent.ExistsFavorite(foodId))
                        }
                        ConnectivityStatus.Status.Unavailable -> {}
                        ConnectivityStatus.Status.Losing -> {}
                        ConnectivityStatus.Status.Lost -> {
                            checkConnectionOrEmpty(true, PageState.NETWORK)
                        }
                    }
                }
            }
        }
    }

    private fun checkConnectionOrEmpty(isShownError: Boolean, state: PageState) {
        binding?.apply {
            if (isShownError) {
                homeDisLay.isVisible(true, detailContentLay)
                when (state) {
                    PageState.EMPTY -> {
                        statusLay.disImg.setImageResource(R.drawable.box)
                        statusLay.disTxt.text = getString(R.string.emptyList)
                    }
                    PageState.NETWORK -> {
                        statusLay.disImg.setImageResource(R.drawable.disconnect)
                        statusLay.disTxt.text = getString(R.string.checkInternet)
                    }
                    else -> {}
                }
            } else {
                homeDisLay.isVisible(false, detailContentLay)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

