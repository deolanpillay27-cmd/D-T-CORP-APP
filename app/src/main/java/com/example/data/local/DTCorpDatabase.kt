package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartItemEntity::class,
        WishlistEntity::class,
        OrderEntity::class,
        WarrantyClaimEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DTCorpDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao

    companion object {
        @Volatile
        private var INSTANCE: DTCorpDatabase? = null

        fun getInstance(context: Context): DTCorpDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DTCorpDatabase::class.java,
                    "dt_corp_store.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
