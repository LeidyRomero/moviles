package com.example.flohmarkt.ui.stores

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flohmarkt.MainActivity.Companion.navController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flohmarkt.R
import com.example.flohmarkt.domain.Store
import com.example.flohmarkt.persistence.viewModel.StoresViewModel
import com.example.flohmarkt.MainActivity.Companion.db
import com.example.flohmarkt.MainActivity.Companion.mainViewModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_stores.view.*

class StoresFragment : Fragment(), StoreListAdapter.OnItemClickListener {

    private lateinit var storesViewModel: StoresViewModel
    private lateinit var root: View
    private lateinit var adapter: StoreListAdapter
    private lateinit var adapterAll: StoreListAdapter
    private lateinit var recyclerViewFavorites:RecyclerView
    private lateinit var recyclerViewAll:RecyclerView
    private lateinit var fireAdapter:  FirestoreRecyclerAdapter<Store, StoreViewHolder>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_stores, container, false)

        recyclerViewFavorites = root.recyclerview_favoritos
        adapter = StoreListAdapter(root.context, this)
        recyclerViewFavorites.adapter = adapter
        recyclerViewFavorites.layoutManager = LinearLayoutManager(root.context)

        recyclerViewAll = root.recyclerview_stores
        adapterAll = StoreListAdapter(root.context, this)
        recyclerViewAll.adapter = adapterAll
        recyclerViewAll.layoutManager = LinearLayoutManager(root.context)

        storesViewModel = ViewModelProvider(this).get(StoresViewModel::class.java)
        storesViewModel.favoriteStores.observe(viewLifecycleOwner, Observer { stores ->
            // Update the cached copy of the words in the adapter.
            stores?.let { adapter.setStores(it) }
        })

        /**fireAdapter = object : FirestoreRecyclerAdapter<Store, StoresFragment.StoreViewHolder>(
            FirestoreRecyclerOptions.Builder<Store>()
                .setQuery(db.collection("Stores"), Store::class.java)
                .build()
        ) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): StoreViewHolder {
                val itemView = inflater.inflate(R.layout.card_view_stores, parent, false)
                return StoreViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: StoreViewHolder, position: Int, model: Store) {
                holder.storeNumber.text = model.Store_number.toString()
                holder.storeOwner.text = model.Owner_name
                holder.storeCategories.text = model.Categories
                holder.cardId.id = model.Store_number!!
                holder.cardId.setOnClickListener { onItemClicked(model) }
                if (model.Image == "store46")
                    holder.image.setImageResource(R.drawable.ic_store_black_24dp)
                else if (model.Image?.isNotEmpty()!!) {
                    Glide.with(root.context).load(model.Image)
                        .into(holder.image)
                }

            }
        }
        fireAdapter!!.notifyDataSetChanged()
        recyclerViewAll.adapter = fireAdapter**/

        mainViewModel.isConnected.observe(viewLifecycleOwner, Observer { isConnected ->
            isConnected?.let {
                if (isConnected) {

                    db.collection("Stores")
                        .get()
                        .addOnSuccessListener { result ->
                            adapterAll.setStores(mutableListOf<Store>())
                            for (document in result) {
                                adapterAll.addStore(
                                    Store(
                                        document.data.getValue("Store_number")
                                            .toString().toInt(),
                                        document.data.getValue("Owner_name").toString(),
                                        document.data.getValue("Categories")
                                            .toString(),
                                        document.data.getValue("Phone").toString()
                                            .toLong(),
                                        document.data.getValue("Image").toString(),
                                        document.data.getValue("Email").toString(),
                                        false
                                    )
                                )
                            }
                        }
                } else {
                    storesViewModel.allStores.observe(viewLifecycleOwner, Observer { stores ->
                        stores?.let { adapterAll.setStores(it) }
                    })
                }
            }
        })
        return root
    }

    override fun onItemClicked(store: Store) {
        mainViewModel.numberStore(store.Store_number.toString())
        navController.navigate(R.id.storeDetail)
    }

    inner class StoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val storeNumber: TextView = itemView.findViewById(R.id.num_tienda)
        val storeOwner: TextView = itemView.findViewById(R.id.duenia)
        val storeCategories: TextView = itemView.findViewById(R.id.categoria)
        val cardId: ConstraintLayout = itemView.findViewById(R.id.card)
        val image: ImageView = itemView.findViewById(R.id.imageStore)
    }

    override fun onStart() {
        super.onStart()
        //fireAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        //fireAdapter!!.stopListening()
    }
}
