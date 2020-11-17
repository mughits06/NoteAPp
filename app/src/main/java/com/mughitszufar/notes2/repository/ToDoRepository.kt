package com.mughitszufar.notes2.repository

import androidx.lifecycle.LiveData
import com.mughitszufar.notes2.data.TodoDao
import com.mughitszufar.notes2.model.TodoData

class ToDoRepository(private val todoDao: TodoDao) {
    val getAllData: LiveData<List<TodoData>> = todoDao.getAllData()
    val sortByHighPriority: LiveData<List<TodoData>> = todoDao.getAllData()
    val sortByLowPriority: LiveData<List<TodoData>> = todoDao.sortByLowPriority()

    suspend fun insertData (todoData: TodoData){
        todoDao.insertData(todoData)
    }

    suspend fun updateData (todoData: TodoData){
        todoDao.updateData(todoData)
    }

    suspend fun deleteData (todoData: TodoData){
        todoDao.deleteData(todoData)
    }

    suspend fun deleteAll(){
        todoDao.deleteAll()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<TodoData>>{
        return todoDao.searchDatabase(searchQuery)
    }
}