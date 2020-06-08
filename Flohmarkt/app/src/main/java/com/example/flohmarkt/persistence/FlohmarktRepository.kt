package com.example.flohmarkt.persistence

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.flohmarkt.domain.Product
import com.example.flohmarkt.domain.Store
import com.example.flohmarkt.persistence.dao.ProductDao
import com.example.flohmarkt.persistence.dao.StoreDao
import com.example.flohmarkt.persistence.entity.ProductEntity
import com.example.flohmarkt.persistence.entity.StoreEntity
import com.example.flohmarkt.persistence.entity.asDomainModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FlohmarktRepository(private val storeDao: StoreDao, private val productDao: ProductDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val storesLocal: LiveData<List<Store>> = Transformations.map(storeDao.getStores(false)) {
        it.asDomainModel()
    }

    val favoriteStores : LiveData<List<Store>> = Transformations.map(storeDao.getStores(true)) {
        it.asDomainModel()
    }

    val allProducts: LiveData<List<Product>> = Transformations.map(productDao.getAlphabetizedStores()) {
        it.asDomainModel()
    }

    suspend fun insertStore(store: StoreEntity) {
        storeDao.insert(store)
    }

    suspend fun insertProduct(product: ProductEntity) {
        productDao.insert(product)
    }

    suspend fun getStore(store_number: Int): StoreEntity {
        return storeDao.getStore(store_number)
    }

    suspend fun deleteStore(store: StoreEntity) {
        storeDao.delete(store)
    }

    suspend fun getProduct(name: String): ProductEntity {
        return productDao.getProduct(name)
    }
}