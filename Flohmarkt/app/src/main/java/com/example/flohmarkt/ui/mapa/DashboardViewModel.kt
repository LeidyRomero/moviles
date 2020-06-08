package com.example.flohmarkt.ui.mapa

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    val nearStore = MutableLiveData<Int>()
    val bNearStore = MutableLiveData<Int>()

    val restZone = MutableLiveData<Double>()
    val biciZone = MutableLiveData<Double>()

    val stores = MutableLiveData<List<String>>()
    val bStores = MutableLiveData<List<String>>()

    init {
        stores.value = emptyList()
        bStores.value = emptyList()
    }

    fun setNeartest(near: String) {
        if (near.isNotEmpty()) {
            val num = near.substring(11).toInt()
            if (nearStore.value != num) {
                bNearStore.value = nearStore.value
                nearStore.value = near.substring(11).toInt()
            }
        }
    }

    fun setResultStores(result: List<String>) {
        if (!result.isNullOrEmpty())
        {
            bStores.value = stores.value!!.toSet().minus(result.toSet()).toList()
            stores.value = result
        }
    }
    fun setRest(distance: Double) {
        restZone.value = distance
    }

    fun setBici(distance: Double) {
        biciZone.value = distance
    }

    fun restartResultStores() {
        stores.value = emptyList()
        bStores.value = emptyList()
    }
}