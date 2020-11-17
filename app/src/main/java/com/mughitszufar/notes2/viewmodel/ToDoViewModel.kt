package com.mughitszufar.notes2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mughitszufar.notes2.model.TodoData
import com.mughitszufar.notes2.data.TodoDatabase
import com.mughitszufar.notes2.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(application: Application): AndroidViewModel(application) {
    private val toDoDao = TodoDatabase.getDatabase(application).toDodao()
    private val repository: ToDoRepository

    val getAllData: LiveData<List<TodoData>>
    val sortByHighPriority: LiveData<List<TodoData>>
    val sortByLowPriority: LiveData<List<TodoData>>

    init {
        repository = ToDoRepository((toDoDao))
        getAllData = repository.getAllData
        sortByHighPriority = repository.sortByHighPriority
        sortByLowPriority = repository.sortByLowPriority

    }
    fun insertData(todoData: TodoData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(todoData)
        }
    }
    fun updateData(todoData: TodoData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(todoData)
        }
    }
    fun deleteData(todoData: TodoData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(todoData)
        }
    }
    fun deleteAllData(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
    fun searchDatabase(searchQuery: String): LiveData<List<TodoData>>{
        return repository.searchDatabase(searchQuery)
    }

}