package com.example.mvi.food_app.ui.favorite

import academy.nouri.s3_mvi.food_app.ui.favorite.FavoriteAdapter
import academy.nouri.s3_mvi.food_app.utils.isVisible
import academy.nouri.s3_mvi.food_app.utils.setupRecyclerView
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvi.R
import com.example.mvi.databinding.FragmentFoodsFavoriteBinding
import com.example.mvi.food_app.ui.list.FoodListFragmentDirections
import com.example.mvi.food_app.view.favorite.FavoriteIntent
import com.example.mvi.food_app.view.favorite.FavoriteState
import com.example.mvi.food_app.view.favorite.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FoodsFavoriteFragment : Fragment() {

    //Binding
    private var _binding: FragmentFoodsFavoriteBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var favoriteAdapter: FavoriteAdapter

    //Other
    private val viewModel: FavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodsFavoriteBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //InitViews
        binding?.apply {
            //Scope
            lifecycleScope.launch {
                //Send
                viewModel.favoriteIntent.send(FavoriteIntent.LoadFavorite)
                //Load data
                viewModel.state.collect { state ->
                    when(state){
                        is FavoriteState.Empty -> {
                            emptyLay.isVisible(true,favoriteList)
                            statusLay.disImg.setImageResource(R.drawable.box)
                            statusLay.disTxt.text = getString(R.string.emptyList)
                        }
                        is FavoriteState.LoadFavorites -> {
                            emptyLay.isVisible(false, favoriteList)
                            favoriteAdapter.setData(state.data)
                            favoriteList.setupRecyclerView(LinearLayoutManager(requireContext()), favoriteAdapter)

                            favoriteAdapter.setOnItemClickListener {
                                val direction = FoodListFragmentDirections.actionListToDetail(it.id)
                                findNavController().navigate(direction)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}