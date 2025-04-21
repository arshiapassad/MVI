package academy.nouri.s3_mvi.note_app.data.repository

import academy.nouri.s3_mvi.note_app.data.database.NoteDao
import academy.nouri.s3_mvi.note_app.data.model.NoteEntity
import javax.inject.Inject

class MainRepository @Inject constructor(private val dao: NoteDao) {
    fun allNotes() = dao.getAllNotes()
    fun searchNotes(search: String) = dao.searchNote(search)
    fun filterNotes(filter: String) = dao.filetNote(filter)
    suspend fun deleteNote(entity: NoteEntity) = dao.deleteNote(entity)
}