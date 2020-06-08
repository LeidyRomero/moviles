package com.example.flohmarkt.domain

/**
 * Store representation
 */
class Store {
    var Store_number: Int? = null
    var Owner_name: String? = null
    var Categories: String? = null
    var Phone: Long? = null
    var Image: String? = null
    var Email: String? = null
    var isFavorite: Boolean? = null

    constructor(){}

    constructor(storeNumber: Int, owner: String, categories: String, ownerPhone: Long, imgUrl: String, ownerEmail: String, isFavorite: Boolean) {
        this.Store_number = storeNumber
        this.Owner_name = owner
        this.Categories = categories
        this.Phone = ownerPhone
        this.Image = imgUrl
        this.Email = ownerEmail
        this.isFavorite = isFavorite
    }

    constructor(storeNumber: Int, owner: String, categories: String, ownerPhone: Long, imgUrl: String, ownerEmail: String) {
        this.Store_number = storeNumber
        this.Owner_name = owner
        this.Categories = categories
        this.Phone = ownerPhone
        this.Image = imgUrl
        this.Email = ownerEmail
        this.isFavorite = false
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result["Store_number"] = Store_number!!
        result["Owner_name"] = Owner_name!!
        result["Categories"] = Categories!!
        result["Phone"] = Phone!!
        result["Image"] = Image!!
        result["Email"] = Email!!
        result["isFavorite"] = isFavorite!!

        return result
    }
}

/**
 * Product representation
 */
data class Product (val img_url: String,
                    val price: Long,
                    val store_number: Int,
                    val name: String,
                    val description: String
) {

}