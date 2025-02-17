package com.example.simple_auth_app.dbHandler

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.simple_auth_app.data.Product


object FeedReaderContract {
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "product"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_PRICE = "price"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_IMAGE_URL = "imageUrl"
    }
}

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${FeedReaderContract.FeedEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_NAME} TEXT," +
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_PRICE} DOUBLE," +
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION} TEXT," +
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE_URL} TEXT)"

private const val SQL_DELETE_ENTRIES =
    "DROP TABLE IF EXISTS ${FeedReaderContract.FeedEntry.TABLE_NAME}"

class FeedReaderDbHelper (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
    }

    fun insertData(name: String, price: Double, description: String, imageUrl: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name)
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_PRICE, price)
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, description)
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE_URL, imageUrl)
        }
        return db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
    }

    fun readAllData(): MutableList<Product> {
        val db = readableDatabase
        val productList = mutableListOf<Product>()
        productList.clear()

        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
            FeedReaderContract.FeedEntry.COLUMN_NAME_PRICE,
            FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION,
            FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE_URL
        )
        val cursor = db.query(
            FeedReaderContract.FeedEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val itemName = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME))
                val itemPrice = getDouble(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PRICE))
                val itemDescription = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION))
                val itemImageUrl = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE_URL))

                productList.add(
                    Product(
                        id = itemId,
                        name = itemName,
                        price = itemPrice,
                        description = itemDescription,
                        imageUrl = itemImageUrl
                    )
                )
            }
        }
        cursor.close()

        return productList
    }

    fun readOneById(id: Long): MutableState<Product?> {
        val db = readableDatabase
        val product = mutableStateOf<Product?>(null)
        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
            FeedReaderContract.FeedEntry.COLUMN_NAME_PRICE,
            FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION,
            FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE_URL
        )

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(
            FeedReaderContract.FeedEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val _id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val _name = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME))
            val _price = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PRICE))
            val _description = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION))
            val _imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE_URL))

            product.value = Product(
                id = _id,
                name = _name,
                price = _price,
                description = _description,
                imageUrl = _imageUrl
            )
        }
        cursor.close()

        return product
    }


    fun deleteData(id: Long): Int {
        val db = writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs)
    }

    fun updateData(id: Long, name: String, price: Double, description: String, imageUrl: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name)
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_PRICE, price)
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, description)
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE_URL, imageUrl)
        }
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.update(FeedReaderContract.FeedEntry.TABLE_NAME, values, selection, selectionArgs)
    }
}
