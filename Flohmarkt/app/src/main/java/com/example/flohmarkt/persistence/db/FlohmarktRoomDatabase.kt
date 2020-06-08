package com.example.flohmarkt.persistence.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.flohmarkt.persistence.dao.ProductDao
import com.example.flohmarkt.persistence.dao.StoreDao
import com.example.flohmarkt.persistence.entity.ProductEntity
import com.example.flohmarkt.persistence.entity.StoreEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [StoreEntity::class, ProductEntity::class], version = 1, exportSchema = false)
public abstract class FlohmarktRoomDatabase : RoomDatabase() {

    abstract fun storeDao(): StoreDao
    abstract fun productDao(): ProductDao

    private class FlohmarktDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var storeDao = database.storeDao()
                    var productDao = database.productDao()

                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: FlohmarktRoomDatabase? = null

        fun getDatabase(context: Context,
           scope: CoroutineScope
        ): FlohmarktRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        FlohmarktRoomDatabase::class.java,
                        "store_database"
                    )
                    .addCallback(FlohmarktDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}