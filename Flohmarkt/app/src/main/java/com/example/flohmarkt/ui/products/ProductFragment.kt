package com.example.flohmarkt.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.flohmarkt.MainActivity.Companion.navController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flohmarkt.R
import com.example.flohmarkt.domain.Product
import com.example.flohmarkt.MainActivity.Companion.db
import com.example.flohmarkt.MainActivity.Companion.mainViewModel
import kotlinx.android.synthetic.main.products_detail.view.*

class ProductFragment : Fragment(), ProductListAdapter.OnItemClickListener {

    private lateinit var productViewModel: ProductViewModel
    private lateinit var root: View
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter:ProductListAdapter
    private lateinit var myList:RecyclerView
    private var storeNumber:Int=0
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        productViewModel =
                ViewModelProviders.of(this).get(ProductViewModel::class.java)
        root = inflater.inflate(R.layout.products_detail, container, false)
        layoutManager = GridLayoutManager(root.context, 2)
        adapter = ProductListAdapter(root.context, this)

        myList = root.products_detail
        myList.layoutManager = layoutManager
        myList.adapter = adapter

        storeNumber =  mainViewModel.numberStore.value.toString().toInt()

        db.collection("Products")
            .whereEqualTo("store_number", storeNumber)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    adapter.addProduct(
                        Product(
                            store_number = document.data.getValue("store_number").toString().toInt(),
                            img_url = document.data.getValue("image").toString(),
                            price = document.data.getValue("precio").toString().toLong(),
                            name = document.data.getValue("nombre").toString(),
                            description = document.data.getValue("descripción").toString()
                        )
                    )
                }
            }

        return root
    }

    override fun onItemClicked(product: Product) {
        mainViewModel.nameProduct(product.name)
        navController.navigate(R.id.productDetail)
    }
}
