package com.example.flohmarkt.ui.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flohmarkt.R
import com.example.flohmarkt.domain.Store

class StoreListAdapter internal constructor(
    val context: Context,
    private val itemOnClickListener: OnItemClickListener
) : RecyclerView.Adapter<StoreListAdapter.StoreViewHolder>() {

    private lateinit var itemView: View
    private lateinit var current: Store
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var stores: MutableList<Store> = mutableListOf<Store>() // Cached copy of words

    interface OnItemClickListener {
        fun onItemClicked(store: Store)
    }

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeNumber: TextView = itemView.findViewById(R.id.num_tienda)
        val storeOwner: TextView = itemView.findViewById(R.id.duenia)
        val storeCategories: TextView = itemView.findViewById(R.id.categoria)
        val cardId: ConstraintLayout = itemView.findViewById(R.id.card)
        val image: ImageView = itemView.findViewById(R.id.imageStore)

        fun bind(
            item: Store,
            clickListener: OnItemClickListener
        ) {
            storeNumber.text = item.Store_number.toString()
            storeOwner.text = item.Owner_name
            storeCategories.text = item.Categories
            cardId.id = item.Store_number!!
            cardId.setOnClickListener { clickListener.onItemClicked(item) }
            if (item.Image == "store46")
                image.setImageResource(R.drawable.ic_store_black_24dp)
            else if (item.Image?.isNotEmpty()!!) {
                Glide.with(context).load(item.Image)
                    .into(image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        itemView = inflater.inflate(R.layout.card_view_stores, parent, false)
        return StoreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        current = stores[position]
        holder.bind(current!!, itemOnClickListener)
    }

    internal fun setStores(stores: List<Store>) {
        this.stores = stores as MutableList<Store>
        notifyDataSetChanged()
    }

    internal fun addStore(store: Store) {
        this.stores.add(store)
        notifyDataSetChanged()
    }

    override fun getItemCount() = stores.size
}
