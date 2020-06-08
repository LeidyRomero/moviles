package com.example.flohmarkt.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.flohmarkt.persistence.entity.StoreEntity

@Dao
interface StoreDao {

    @Query("SELECT * from store_table WHERE is_favorite = :favorites ORDER BY store_number ASC")
    fun getStores(favorites: Boolean): LiveData<List<StoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(store: StoreEntity)

    @Query("DELETE FROM store_table WHERE is_favorite = :is_favorite")
    suspend fun deleteByFavorite(is_favorite: Boolean)

    @Query("DELETE FROM store_table")
    suspend fun deleteAll()

    @Delete()
    suspend fun delete(store: StoreEntity)

    @Query("SELECT * FROM store_table WHERE store_number = :number")
    suspend fun getStore(number: Int): StoreEntity
}