package com.example.flohmarkt.ui.products

import android.app.Application
import androidx.lifecycle.*
import com.example.flohmarkt.persistence.FlohmarktRepository
import com.example.flohmarkt.persistence.db.FlohmarktRoomDatabase
import com.example.flohmarkt.persistence.entity.ProductEntity

class ProductViewModel(application: Application) : AndroidViewModel(application){

    private val repository: FlohmarktRepository

    lateinit var product: ProductEntity


    init {
        val storesDao = FlohmarktRoomDatabase.getDatabase(application, viewModelScope).storeDao()
        val productsDao = FlohmarktRoomDatabase.getDatabase(application, viewModelScope).productDao()
        repository = FlohmarktRepository(storeDao = storesDao, productDao = productsDao)
    }

    suspend fun selectedProduct(name: String) {
        product = repository.getProduct(name)
    }
}