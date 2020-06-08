package com.example.flohmarkt.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flohmarkt.MainActivity.Companion.db
import com.example.flohmarkt.MainActivity.Companion.navController
import com.example.flohmarkt.MainActivity.Companion.mainViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.flohmarkt.R
import com.example.flohmarkt.domain.Product
import com.example.flohmarkt.domain.Store
import com.example.flohmarkt.persistence.entity.ProductEntity
import com.example.flohmarkt.persistence.entity.StoreEntity
import com.example.flohmarkt.persistence.viewModel.ProductsViewModel
import com.example.flohmarkt.persistence.viewModel.StoresViewModel
import com.example.flohmarkt.ui.products.ProductListAdapter
import com.example.flohmarkt.ui.stores.StoreListAdapter

class HomeFragment : Fragment(), ProductListAdapter.OnItemClickListener, StoreListAdapter.OnItemClickListener {
    private lateinit var productsViewModel: ProductsViewModel
    private lateinit var storesViewModel: StoresViewModel
    private lateinit var root: View
    private lateinit var layoutManager:LinearLayoutManager
    private lateinit var adapter:ProductListAdapter
    private lateinit var adapterStores:StoreListAdapter
    private lateinit var myList:RecyclerView
    private lateinit var stores:RecyclerView
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        productsViewModel =
            ViewModelProviders.of(this).get(ProductsViewModel::class.java)

        storesViewModel =
            ViewModelProviders.of(this).get(StoresViewModel::class.java)

        layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        adapter = ProductListAdapter(root.context, this)

        myList = root.findViewById(R.id.recyclerView_products) as RecyclerView
        myList.layoutManager = layoutManager
        myList.adapter = adapter

        adapterStores = StoreListAdapter(root.context, this)

        stores = root.findViewById(R.id.recyclerView_topStores) as RecyclerView
        stores.layoutManager = LinearLayoutManager(root.context)
        stores.adapter = adapterStores

        mainViewModel.isConnected.observe(viewLifecycleOwner, Observer { isConnected ->
            isConnected?.let {
                if (isConnected) {
                    db.collection("Products")
                        .orderBy("consultas")
                        .limit(5)
                        .get()
                        .addOnSuccessListener { result ->
                            adapter.setProducts(mutableListOf<Product>())
                            for (document in result) {
                                adapter.addProduct(
                                    Product(
                                        store_number = document.data.getValue("store_number")
                                            .toString().toInt(),
                                        img_url = document.data.getValue("image").toString(),
                                        price = document.data.getValue("precio").toString()
                                            .toLong(),
                                        name = document.data.getValue("nombre").toString(),
                                        description = document.data.getValue("descripción")
                                            .toString()
                                    )
                                )

                                if (adapter.itemCount < 6) {
                                    productsViewModel.insert(
                                        ProductEntity(
                                            store_number = document.data.getValue("store_number")
                                                .toString().toInt(),
                                            img_url = "product",
                                            price = document.data.getValue("precio").toString()
                                                .toLong(),
                                            id = adapter.itemCount,
                                            name = document.data.getValue("nombre").toString(),
                                            description = document.data.getValue("descripción")
                                                .toString()
                                        )
                                    )
                                }
                            }
                        }

                    db.collection("Stores")
                        .orderBy("Consultas")
                        .limit(5)
                        .get()
                        .addOnSuccessListener { result ->
                            adapterStores.setStores(mutableListOf<Store>())
                            for (document in result) {
                                adapterStores.addStore(
                                    Store(
                                        document.data.getValue("Store_number")
                                            .toString().toInt(),
                                        document.data.getValue("Categories")
                                            .toString(),
                                        document.data.getValue("Owner_name").toString(),
                                        document.data.getValue("Phone").toString()
                                            .toLong(),
                                        document.data.getValue("Image").toString(),
                                        document.data.getValue("Email").toString(),
                                        false
                                    )
                                )

                                if (adapter.itemCount < 6) {
                                    storesViewModel.insert(
                                        StoreEntity(
                                            id = document.data.getValue("Store_number").toString()
                                                .toInt(),
                                            is_favorite = false,
                                            owner_email = document.data.getValue("Email")
                                                .toString(),
                                            Owner_name = document.data.getValue("Owner_name")
                                                .toString(),
                                            Phone = document.data.getValue("Phone").toString()
                                                .toLong(),
                                            Categories = document.data.getValue("Categories")
                                                .toString(),
                                            img_url = "store46",
                                            Store_number = document.data.getValue("Store_number")
                                                .toString()
                                                .toInt()
                                        )
                                    )
                                }
                            }
                        }
                } else {
                    productsViewModel.favoriteProducts.observe(
                        viewLifecycleOwner,
                        Observer { products ->
                            products?.let { adapter.setProducts(it) }
                        })
                    storesViewModel.allStores.observe(viewLifecycleOwner, Observer { stores ->
                        stores?.let { adapterStores.setStores(it) }
                    })
                }
            }
        })
        return root
    }

    override fun onItemClicked(product: Product) {
        mainViewModel.nameProduct(product.name)
        navController.navigate(R.id.productDetail)
    }

    override fun onItemClicked(store: Store) {
        mainViewModel.numberStore(store.Store_number.toString())
        navController.navigate(R.id.storeDetail)
    }

}
