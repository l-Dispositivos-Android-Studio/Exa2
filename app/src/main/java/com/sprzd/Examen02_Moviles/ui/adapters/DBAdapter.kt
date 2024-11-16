package com.sprzd.Examen02_Moviles.ui.adapters

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBAdapter(context: Context) {

    // Nombre y versión de la base de datos
    private val dbName = "ExamenMoviles"
    private val dbVersion = 1

    // Nombre de la tabla y columnas
    private val tableProduct = "Product"
    private val columnId = "id"
    private val columnName = "nombre"
    private val columnPrice = "precioUnitario"
    private val columnQuantity = "cantidad"
    private val columnWeight = "peso"
    private val columnTotalPrice = "precioTotal"
    private val columnImageUrl = "imagenUrl"

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    private var db: SQLiteDatabase? = null

    // Método para abrir la base de datos
    fun open(): DBAdapter {
        db = dbHelper.writableDatabase
        return this
    }

    // Método para cerrar la base de datos
    fun close() {
        dbHelper.close()
    }

    // Método para insertar un producto
    fun insertProduct(
        nombre: String,
        precioUnitario: Double,
        cantidad: Int,
        peso: Double,
        precioTotal: Double,
        imagenUrl: String
    ): Long {
        val values = ContentValues()
        values.put(columnName, nombre)
        values.put(columnPrice, precioUnitario)
        values.put(columnQuantity, cantidad)
        values.put(columnWeight, peso)
        values.put(columnTotalPrice, precioTotal)
        values.put(columnImageUrl, imagenUrl)

        return db!!.insert(tableProduct, null, values)
    }

    // Método para obtener todos los productos
    val getAllProducts: Cursor
        get() {
            val query = "SELECT * FROM $tableProduct"
            return db!!.rawQuery(query, null)
        }

    // Clase interna para gestionar la creación y actualización de la base de datos
    private inner class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, dbName, null, dbVersion) {

        override fun onCreate(db: SQLiteDatabase) {
            // Crear tabla si no existe
            val createTableQuery = """
                CREATE TABLE $tableProduct (
                    $columnId INTEGER PRIMARY KEY AUTOINCREMENT,
                    $columnName TEXT NOT NULL,
                    $columnPrice REAL NOT NULL,
                    $columnQuantity INTEGER NOT NULL,
                    $columnWeight REAL NOT NULL,
                    $columnTotalPrice REAL NOT NULL,
                    $columnImageUrl TEXT NOT NULL
                )
            """.trimIndent()
            db.execSQL(createTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $tableProduct")
            onCreate(db)
        }
    }
}
