package com.example.flohmarkt.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.flohmarkt.R
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.product_detail.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import com.example.flohmarkt.MainActivity.Companion.db
import com.example.flohmarkt.MainActivity.Companion.mainViewModel

class ProductDetail : Fragment() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.product_detail, container, false)
        productViewModel = activity?.run {
            ViewModelProviders.of(this)[ProductViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        mainViewModel.isConnected.observe(viewLifecycleOwner, Observer { isConnected ->
            isConnected?.let {
                if (isConnected) {
                    mainViewModel.nameProduct.observe(
                        viewLifecycleOwner,
                        Observer<String> { item ->
                            db.collection("Products")
                                .whereEqualTo("nombre", item.toString())
                                .get()
                                .addOnSuccessListener { result ->
                                    for (document in result) {
                                        root.nombre_producto.text =
                                            document.data.getValue("nombre").toString()
                                        root.precio.text =
                                            document.data.getValue("precio").toString()
                                        root.descripcion.text =
                                            document.data.getValue("descripción").toString()
                                        Glide.with(requireActivity()).load(document.data.getValue("image").toString())
                                            .into(root.imageProduct)

                                        db.collection("Products").document(document.id)
                                            .update("consultas", FieldValue.increment(1))
                                    }
                                }
                        })
                } else {
                    mainViewModel.nameProduct.observe(
                        viewLifecycleOwner,
                        Observer<String> { item ->
                            item?.let {
                                runBlocking {
                                    withContext(Dispatchers.IO) {
                                        productViewModel.selectedProduct(
                                            item
                                        )
                                    }
                                }
                                root.nombre_producto.text = productViewModel.product.name
                                root.precio.text = productViewModel.product.price.toString()
                                root.descripcion.text = productViewModel.product.description
                                Glide.with(requireActivity()).load(productViewModel.product.img_url).into(root.imageProduct)
                            }
                        })
                }
            }
        })
        return root
    }
}
