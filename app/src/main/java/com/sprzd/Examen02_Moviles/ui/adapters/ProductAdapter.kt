package com.sprzd.Examen02_Moviles.ui.products

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.BaseAdapter
import com.sprzd.Examen02_Moviles.R
import com.squareup.picasso.Picasso
import android.widget.Toast
import android.database.Cursor
import android.widget.Button
import com.sprzd.Examen02_Moviles.entities.cls_Product
class ProductAdapter(val context: Context, val cursor: Cursor) : BaseAdapter() {

    private val cartItems = mutableListOf<cls_Product>() // Carrito de compras

    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int): Any {
        cursor.moveToPosition(position)
        return cursor
    }

    override fun getItemId(position: Int): Long {
        cursor.moveToPosition(position)
        return cursor.getLong(cursor.getColumnIndex("id"))
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_product, parent, false)
        }

        cursor.moveToPosition(position)

        val txtNombre: TextView = view!!.findViewById(R.id.productName)
        val txtPrecio: TextView = view.findViewById(R.id.productPrice)
        val txtCantidad: TextView = view.findViewById(R.id.productQuantity)
        val imgProducto: ImageView = view.findViewById(R.id.productImage)

        val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
        val precio = cursor.getDouble(cursor.getColumnIndex("precioUnitario"))
        val cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"))
        val imagenUrl = cursor.getString(cursor.getColumnIndex("imagenUrl"))

        txtNombre.text = nombre
        txtPrecio.text = "Precio: \$${precio}"
        txtCantidad.text = "Cantidad: $cantidad"

        // Cargar la imagen de la URL usando Picasso
        Picasso.get().load(imagenUrl).into(imgProducto)

        // Configurar el botón para agregar al carrito
        val btnAddToCart: Button = view.findViewById(R.id.btn_AgregarCarrito)
        btnAddToCart.setOnClickListener {
            // Agregar el producto al carrito
            val cartItem = cls_Product(
                id = cursor.getLong(cursor.getColumnIndex("id")),
                nombre = nombre,
                precioUnitario = precio,
                cantidad = cantidad, // Aquí puedes permitir que el usuario elija la cantidad
                descuento = 50 // Ejemplo de descuento, lo puedes ajustar
            )
            cartItems.add(cartItem)
            Toast.makeText(context, context.getString(R.string.text_producto_agregado), Toast.LENGTH_SHORT).show()
        }

        return view
    }

    // Método para obtener los productos del carrito
    fun getCartItems(): List<cls_Product> {
        return cartItems
    }
}
