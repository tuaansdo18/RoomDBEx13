package com.dovantuan.lab7

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase

@Database(entities = arrayOf(StudentModel::class), version = 1)
abstract class StudentDB : RoomDatabase() {
    abstract fun studentDAO(): StudentDAO
}

@Dao
interface StudentDAO {
    @Query("SELECT * FROM StudentModel")
    fun getAll(): List<StudentModel>

    @Query("SELECT * FROM StudentModel WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<StudentModel>

    @Insert
    fun insert(vararg users: StudentModel)

    @Delete
    fun delete(user: StudentModel)
}