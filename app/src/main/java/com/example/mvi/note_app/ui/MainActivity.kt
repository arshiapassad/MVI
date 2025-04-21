package com.example.mvi.note_app.ui

import academy.nouri.s3_mvi.note_app.data.model.NoteEntity
import academy.nouri.s3_mvi.note_app.ui.NoteAdapter
import academy.nouri.s3_mvi.note_app.utils.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mvi.R
import com.example.mvi.databinding.ActivityMainBinding
import com.example.mvi.note_app.ui.note.NoteFragment
import com.example.mvi.note_app.viewmodel.main.MainIntent
import com.example.mvi.note_app.viewmodel.main.MainState
import com.example.mvi.note_app.viewmodel.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var noteEntity: NoteEntity

    @Inject
    lateinit var noteAdapter : NoteAdapter

    //Other
    private val viewModel : MainViewModel by viewModels()
    private var selectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        //InitViews
        binding?.apply {
            //Toolbar
            setSupportActionBar(notesToolbar)
            //Add
            addNoteBtn.setOnClickListener {
                NoteFragment().show(
                    supportFragmentManager,
                    NoteFragment().tag
                )
            }
            //Call intent
            viewModel.handleIntent(MainIntent.LoadAllNotes)
            //Data
            lifecycleScope.launch {
                //Get
                viewModel.state.collect { state ->
                    when (state) {
                        is MainState.Empty -> {
                            //emptyLay.visibility = View.VISIBLE
                            emptyLay.isVisible = true
                            noteList.isVisible = false
                        }

                        is MainState.LoadNotes -> {
                            emptyLay.isVisible = false
                            noteList.isVisible = true

                            noteAdapter.setData(state.list)
                            noteList.apply {
                                layoutManager = StaggeredGridLayoutManager(
                                    2,
                                    StaggeredGridLayoutManager.VERTICAL
                                )
                                adapter = noteAdapter
                            }

                            noteAdapter.setOnItemClickListener { entity, type ->
                                when (type) {
                                    EDIT -> {
                                        viewModel.handleIntent(MainIntent.ClickToDetail(entity.id))
                                    }

                                    DELETE -> {
                                        noteEntity.id = entity.id
                                        noteEntity.title = entity.title
                                        noteEntity.desc = entity.desc
                                        noteEntity.category = entity.category
                                        noteEntity.priority = entity.priority
                                        viewModel.handleIntent(MainIntent.DeleteNote(noteEntity))
                                    }
                                }
                            }
                        }

                        is MainState.DeleteNote -> {
                            //For example show toast, snack bar and more ...
                        }
                        is MainState.GoToDetail -> {
                            val noteFragment = NoteFragment()
                            val bundle = Bundle()
                            bundle.putInt(BUNDLE_ID, state.id)
                            noteFragment.arguments = bundle
                            noteFragment.show(supportFragmentManager, NoteFragment().tag)
                        }
                    }
                }
            }
            //Filter
            notesToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionFilter -> {
                        priorityFilter()
                        return@setOnMenuItemClickListener true
                    }

                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        val search = menu.findItem(R.id.actionSearch)
        val searchView = search.actionView as SearchView
        searchView.queryHint = getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.handleIntent(MainIntent.SearchNote(newText))
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun priorityFilter() {
        val builder = AlertDialog.Builder(this)

        val priority = arrayOf("All", HIGH, NORMAL, LOW)
        builder.setSingleChoiceItems(priority, selectedItem) { dialog, item ->
            when (item) {
                0 -> {
                    viewModel.handleIntent(MainIntent.LoadAllNotes)
                }
                in 1..3 -> {
                    viewModel.handleIntent(MainIntent.FilterNote(priority[item]))
                }
            }
            selectedItem = item
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}