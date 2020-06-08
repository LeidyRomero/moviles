package com.example.flohmarkt.ui.stores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.flohmarkt.MainActivity.Companion.navController
import com.example.flohmarkt.R
import com.example.flohmarkt.domain.Store
import com.example.flohmarkt.ui.products.ProductViewModel
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.store_detail.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import com.example.flohmarkt.MainActivity.Companion.db
import com.example.flohmarkt.MainActivity.Companion.mainViewModel

class StoreDetail : Fragment(), View.OnClickListener {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var storeViewModel: StoreViewModel
    private lateinit var root: View
    private lateinit var btn_favorite: ImageButton
    private lateinit var btn_products: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root=inflater.inflate(R.layout.store_detail, container, false)
        root.button.setOnClickListener(this)
        root.fav_button.setOnClickListener(this)
        btn_favorite = root.findViewById(R.id.fav_button)
        btn_products = root.findViewById(R.id.button)

        productViewModel = activity?.run {
            ViewModelProviders.of(this)[ProductViewModel::class.java]

        } ?: throw Exception("Invalid Activity")

        storeViewModel = activity?.run {
            ViewModelProviders.of(this)[StoreViewModel::class.java]

        } ?: throw Exception("Invalid Activity")

        mainViewModel.isConnected.observe(viewLifecycleOwner, Observer { isConnected ->
            isConnected?.let {
                if (isConnected) {
                    mainViewModel.numberStore.observe(viewLifecycleOwner, Observer<String> { item ->
                        // Update the UI using new item data

                        db.collection("Stores")
                            .whereEqualTo("Store_number", item.toString().toInt())
                            .get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    storeViewModel.setStore(
                                        Store(
                                            document.data.getValue("Store_number").toString().toInt(),
                                            document.data.getValue("Owner_name").toString(),
                                            document.data.getValue("Categories").toString(),
                                            document.data.getValue("Phone").toString().toLong(),
                                            document.data.getValue("Image").toString(),
                                            document.data.getValue("Email").toString(),
                                            false
                                        )
                                    )

                                    root.Titulo.text = storeViewModel.actual_store.Store_number.toString()
                                    root.nombre_duenio.text = storeViewModel.actual_store.Owner_name
                                    root.telefono.text = storeViewModel.actual_store.Phone.toString()
                                    root.categorias.text = storeViewModel.actual_store.Categories
                                    root.correo.text = storeViewModel.actual_store.Email
                                    btn_products.id = storeViewModel.actual_store.Store_number!!
                                    btn_favorite.id = 0
                                    runBlocking {
                                        withContext(Dispatchers.IO) {
                                            storeViewModel.checkFavorite()
                                        }
                                    }

                                    if (storeViewModel.actual_store.isFavorite!!) {

                                        btn_favorite.setImageResource(R.drawable.corazon)
                                    } else {
                                        btn_favorite.setImageResource(R.drawable.corazon_vacio)
                                    }

                                    Glide.with(requireActivity()).load(storeViewModel.actual_store.Image)
                                        .into(root.imageStore)
                                    db.collection("Stores").document(document.id)
                                        .update("Consultas", FieldValue.increment(1))
                                }
                            }
                    })
                }
                else {
                    mainViewModel.numberStore.observe(viewLifecycleOwner, Observer<String> { item ->
                        item?.let {
                            runBlocking {
                                withContext(Dispatchers.IO) {
                                    storeViewModel.getStore(item.toInt())
                                }
                            }
                            root.Titulo.text = storeViewModel.actual_store.Store_number.toString()
                            root.nombre_duenio.text = storeViewModel.actual_store.Owner_name
                            root.telefono.text = storeViewModel.actual_store.Phone.toString()
                            root.categorias.text = storeViewModel.actual_store.Categories
                            root.correo.text = storeViewModel.actual_store.Email
                            btn_products.id = storeViewModel.actual_store.Store_number!!
                            btn_favorite.id = 0

                            if (storeViewModel.actual_store.isFavorite!!) {

                                btn_favorite.setImageResource(R.drawable.corazon)
                            } else {
                                btn_favorite.setImageResource(R.drawable.corazon_vacio)
                            }
                        }
                    })
                }
            }
        })

        return root
    }
    override fun onClick(v: View?) {
        if (v != null) {
            if(v.id == 0)
            {
                val b = v as ImageButton
                if(!storeViewModel.actual_store.isFavorite!!) {
                    storeViewModel.insert()
                    b.setImageResource(R.drawable.corazon)
                }
                else {
                    storeViewModel.delete()
                    b.setImageResource(R.drawable.corazon_vacio)
                }
            }
            else
            {
                navController.navigate(R.id.productFragment)
            }
        }
    }
}
