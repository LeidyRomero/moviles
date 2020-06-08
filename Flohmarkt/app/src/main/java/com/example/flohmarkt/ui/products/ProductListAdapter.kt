package com.example.flohmarkt.ui.products

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
import com.example.flohmarkt.domain.Product

class ProductListAdapter internal constructor(
    val context: Context,
    private val itemOnClickListener: OnItemClickListener
) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    private lateinit var itemView: View
    private lateinit var current: Product
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var products: MutableList<Product> = mutableListOf<Product>() // Cached copy of words

    interface OnItemClickListener {
        fun onItemClicked(product: Product)
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val price: TextView = itemView.findViewById(R.id.price)
        val cardId: ConstraintLayout = itemView.findViewById(R.id.card)
        val image: ImageView = itemView.findViewById(R.id.img_product)
        val name: TextView = itemView.findViewById(R.id.name_product)

        fun bind(
            item: Product,
            clickListener: OnItemClickListener
        ) {
            price.text = item.price.toString()
            cardId.id = item.store_number
            name.text = item.name
            if (item.img_url == "product")
                image.setImageResource(R.drawable.ic_photo_black_24dp)
            else if (item.img_url.isNotEmpty())
                Glide.with(context).load(item.img_url)
                    .into(image)
            cardId.setOnClickListener { clickListener.onItemClicked(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        itemView = inflater.inflate(R.layout.card_view_products, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        current = products[position]
        holder.bind(current, itemOnClickListener)
    }

    internal fun setProducts(products: List<Product>) {
        this.products = products as MutableList<Product>
        notifyDataSetChanged()
    }

    internal fun addProduct(product: Product) {
        this.products.add(product)
        notifyDataSetChanged()
    }

    override fun getItemCount() = products.size
}