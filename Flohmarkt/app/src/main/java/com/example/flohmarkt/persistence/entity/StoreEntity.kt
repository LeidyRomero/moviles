package com.example.flohmarkt.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flohmarkt.domain.Store

/**
 * Database entities go in this file. These are responsible for reading and writing
 * from the database.
 */
@Entity(tableName = "store_table")
class StoreEntity(@PrimaryKey(autoGenerate = true) val id: Int,
                  @ColumnInfo(name="owner_name") val Owner_name: String,
                  @ColumnInfo(name="categories") val Categories: String,
                  @ColumnInfo(name="phone") val Phone: Long,
                  @ColumnInfo(name="store_number") val Store_number: Int,
                  @ColumnInfo(name = "is_favorite") val is_favorite: Boolean,
                  @ColumnInfo(name="owner_email") val owner_email: String,
                  @ColumnInfo(name="img_url") val img_url: String)

/**
 * Map StoreEntity to domain entities
 */
fun List<StoreEntity>.asDomainModel(): List<Store> {
    return map {
        Store(
            it.Store_number,
            it.Owner_name,
            it.Categories,
            it.Phone,
            "",
            it.owner_email,
            it.is_favorite
        )
    }
}