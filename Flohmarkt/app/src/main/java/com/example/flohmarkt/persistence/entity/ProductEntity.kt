package com.example.flohmarkt.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flohmarkt.domain.Product

/**
 * Database entities go in this file. These are responsible for reading and writing
 * from the database.
 */
@Entity(tableName = "products_table")
class ProductEntity(@PrimaryKey(autoGenerate = true) val id: Int,
                    @ColumnInfo(name="store_number") val store_number: Int,
                    @ColumnInfo(name="img_url") val img_url: String,
                    @ColumnInfo(name="price") val price: Long,
                    @ColumnInfo(name="name") val name: String,
                    @ColumnInfo(name="description") val description: String
)

/**
 * Map ProductEntity to domain entities
 */
fun List<ProductEntity>.asDomainModel(): List<Product> {
    return map {
        Product(
            img_url = it.img_url,
            store_number = it.store_number,
            price = it.price,
            name = it.name,
            description = it.description
        )
    }
}