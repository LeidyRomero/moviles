package com.example.flohmarkt.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.flohmarkt.domain.Product
import com.example.flohmarkt.persistence.entity.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * from products_table ORDER BY store_number ASC")
    fun getAlphabetizedStores(): LiveData<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Query("DELETE FROM products_table")
    suspend fun deleteAll()

    @Delete()
    suspend fun delete(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(videos: List<ProductEntity>)

    @Query("SELECT * from products_table WHERE name = :name")
    fun getProduct(name: String): ProductEntity
}