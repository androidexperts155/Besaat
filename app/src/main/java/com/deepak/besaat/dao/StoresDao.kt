package com.deepak.besaat.dao

import androidx.room.*
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails

@Dao
interface StoresDao {
    @Query("SELECT * FROM stores")
    fun getStores(): MutableList<SourceDetails>

    @Query("SELECT * FROM stores WHERE name LIKE :title")
    fun findByName(title: String): SourceDetails

    @Query("SELECT * FROM stores WHERE place_id = :placeID")
    fun findByPlaceID(placeID: String): SourceDetails

    @Query("SELECT * FROM stores WHERE name LIKE :title")
    fun findStoreListByName(title: String): MutableList<SourceDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg store: SourceDetails)

    @Insert
    fun insertAll(store: MutableList<SourceDetails>)

    @Delete
    fun delete(store: SourceDetails)

    @Query("DELETE FROM stores")
    suspend fun deleteAll()

    @Update
    fun updatePlace(vararg store: SourceDetails)
}