package com.example.flohmarkt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val fragmentQuery = MutableLiveData<String>()
    val numberStore = MutableLiveData<String>()
    val nameProduct = MutableLiveData<String>()
    val nameCategory = MutableLiveData<String>()
    val query = MutableLiveData<String>()
    val userEmail = MutableLiveData<String>()
    val isConnected = MutableLiveData<Boolean>()

    fun numberStore(item: String) {
        numberStore.value = item
    }
    fun updateUserEmail(u: String) {
        userEmail.value = u
    }
    fun fragmentQuery(item: String) {
        fragmentQuery.value = item
    }
    fun query(item: String) {
        query.value = item
    }
    fun nameProduct(item: String) {
        nameProduct.value = item
    }
    fun nameCategory(item: String) {
        nameCategory.value = item
    }

    fun setIsConnected(item: Boolean) {
        isConnected.value = item
    }

}