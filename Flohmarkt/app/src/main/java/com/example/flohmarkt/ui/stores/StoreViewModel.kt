package com.example.flohmarkt.ui.stores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flohmarkt.domain.Store
import com.example.flohmarkt.persistence.FlohmarktRepository
import com.example.flohmarkt.persistence.db.FlohmarktRoomDatabase
import com.example.flohmarkt.persistence.entity.StoreEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FlohmarktRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    lateinit var actual_store: Store

    init {
        val storesDao = FlohmarktRoomDatabase.getDatabase(application, viewModelScope).storeDao()
        val productsDao = FlohmarktRoomDatabase.getDatabase(application, viewModelScope).productDao()
        repository = FlohmarktRepository(storeDao = storesDao, productDao = productsDao)
        actual_store = Store(0, "", "", 0, "", "", false)
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert() = viewModelScope.launch(Dispatchers.IO) {
        val entity = StoreEntity(
            actual_store.Store_number!!,
            actual_store.Owner_name!!,
            actual_store.Categories!!,
            actual_store.Phone!!,
            actual_store.Store_number!!,
            true,
            actual_store.Email!!,
            "")
        repository.insertStore(entity)
    }

    fun delete()  = viewModelScope.launch(Dispatchers.IO) {
        val entity = StoreEntity(
            actual_store.Store_number!!,
            actual_store.Owner_name!!,
            actual_store.Categories!!,
            actual_store.Phone!!,
            actual_store.Store_number!!,
            true,
            actual_store.Email!!,
            "")
        repository.deleteStore(entity)
    }

    fun setStore(store: Store) {
        actual_store = store
    }

    suspend fun getStore(number: Int) {
        var temp: StoreEntity = repository.getStore(number)
        if (temp != null) {
            actual_store = Store(
                temp.Store_number,
                temp.Owner_name,
                temp.Categories,
                temp.Phone,
                temp.img_url,
                temp.owner_email,
                temp.is_favorite
            )
        }
    }
    suspend fun checkFavorite () {
        var temp: StoreEntity = repository.getStore(actual_store.Store_number!!)
        if (temp != null) {
            if (temp.is_favorite)
            {
                actual_store = Store(
                    actual_store.Store_number!!,
                    actual_store.Owner_name!!,
                    actual_store.Categories!!,
                    actual_store.Phone!!,
                    actual_store.Image!!,
                    actual_store.Email!!,
                    true
                )
            }
        }
    }
}