package com.example.flohmarkt.persistence.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.flohmarkt.domain.Product
import com.example.flohmarkt.persistence.FlohmarktRepository
import com.example.flohmarkt.persistence.db.FlohmarktRoomDatabase
import com.example.flohmarkt.persistence.entity.ProductEntity
import com.example.flohmarkt.persistence.entity.StoreEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FlohmarktRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val favoriteProducts: LiveData<List<Product>>

    init {
        val storesDao = FlohmarktRoomDatabase.getDatabase(application, viewModelScope).storeDao()
        val productsDao = FlohmarktRoomDatabase.getDatabase(application, viewModelScope).productDao()
        repository = FlohmarktRepository(storeDao = storesDao, productDao = productsDao)
        favoriteProducts = repository.allProducts
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(product: ProductEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertProduct(product)
    }

}