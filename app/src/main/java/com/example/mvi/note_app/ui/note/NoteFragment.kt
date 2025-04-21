package com.example.mvi.note_app.ui.note

import academy.nouri.s3_mvi.note_app.data.model.NoteEntity
import academy.nouri.s3_mvi.note_app.utils.BUNDLE_ID
import academy.nouri.s3_mvi.note_app.utils.*
import academy.nouri.s3_mvi.note_app.utils.setupListWithAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mvi.databinding.FragmentNoteBinding
import com.example.mvi.note_app.viewmodel.detail.DetailIntent
import com.example.mvi.note_app.viewmodel.detail.DetailState
import com.example.mvi.note_app.viewmodel.detail.DetailViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment : BottomSheetDialogFragment() {

    //Binding
    private var _binding : FragmentNoteBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var entity: NoteEntity

    //Other
    private val viewModel: DetailViewModel by viewModels()
    private var category = ""
    private var priority = ""
    private var noteId = 0
    private var type = ""
    private val categoriesList: MutableList<String> = mutableListOf()
    private val prioritiesList: MutableList<String> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNoteBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Bundle
        noteId = arguments?.getInt(BUNDLE_ID) ?: 0
        //Type
        type = if (noteId > 0) EDIT else NEW
        //InitViews
        binding?.apply {
            //Close
            closeImg.setOnClickListener { dismiss() }
            //Data
            lifecycleScope.launch {
                //Send
                viewModel.detailIntent.send(DetailIntent.SpinnersList)
                //Note detail
                if (type == EDIT){
                    viewModel.detailIntent.send(DetailIntent.NoteDetail(noteId))
                }
                //Get data
                viewModel.state.collect { state ->
                    when (state){
                        is DetailState.Idle -> {}
                        is DetailState.SpinnersData -> {
                            //Category
                            categoriesList.addAll(state.categoriesList)
                            categoriesSpinner.setupListWithAdapter(state.categoriesList){
                                category = it
                            }
                            //Priority
                            prioritiesList.addAll(state.prioritiesList)
                            prioritySpinner.setupListWithAdapter(state.prioritiesList){
                                priority = it
                            }
                        }
                        is DetailState.SaveNote -> {
                            dismiss()
                        }
                        is DetailState.Error -> {
                            Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()
                        }
                        is DetailState.NoteDetail -> {
                            titleEdt.setText(state.entity.title)
                            descEdt.setText(state.entity.desc)
                            categoriesSpinner.setSelection(categoriesList.getIndexFromList(state.entity.category))
                            prioritySpinner.setSelection(prioritiesList.getIndexFromList(state.entity.priority))
                        }
                        is DetailState.UpdateNote -> {
                            dismiss()
                        }
                    }
                }
            }
            //Save
            saveNote.setOnClickListener {
                val title = titleEdt.text.toString()
                val desc = descEdt.text.toString()
                entity.id = noteId
                entity.title = title
                entity.desc = desc
                entity.category = category
                entity.priority = priority

                lifecycleScope.launch {
                    if (type == NEW) {
                        viewModel.detailIntent.send(DetailIntent.SaveNote(entity))
                    } else {
                        viewModel.detailIntent.send(DetailIntent.UpdateNote(entity))
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        _binding = null
    }
}