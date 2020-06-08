package com.example.flohmarkt.persistence.viewModel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.flohmarkt.domain.Store
import com.example.flohmarkt.persistence.FlohmarktRepository
import com.example.flohmarkt.persistence.db.FlohmarktRoomDatabase
import com.example.flohmarkt.persistence.entity.StoreEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class StoresViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FlohmarktRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allStores: LiveData<List<Store>>
    val favoriteStores: LiveData<List<Store>>

    init {
        val storesDao = FlohmarktRoomDatabase.getDatabase(application, viewModelScope).storeDao()
        val productsDao = FlohmarktRoomDatabase.getDatabase(application, viewModelScope).productDao()
        repository = FlohmarktRepository(storeDao = storesDao, productDao = productsDao)
        Log.w(ContentValues.TAG, "Data ${repository.storesLocal.value}" )
        allStores = repository.storesLocal
        favoriteStores = repository.favoriteStores
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(store: StoreEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertStore(store)
    }
}