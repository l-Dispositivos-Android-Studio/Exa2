package com.sprzd.Examen02_Moviles.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sprzd.Examen02_Moviles.R
import com.sprzd.Examen02_Moviles.entities.cls_Product

class CarProductAdapter(val context: Context, val cartItems: ArrayList<cls_Product>) : BaseAdapter() {

    override fun getCount(): Int {
        return cartItems.size
    }

    override fun getItem(position: Int): Any {
        return cartItems[position]
    }

    override fun getItemId(position: Int): Long {
        return cartItems[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_car_product, parent, false)
        }

        val cartItem = getItem(position) as cls_Product

        val txtNombre: TextView = view!!.findViewById(R.id.productName)
        val txtPrecio: TextView = view.findViewById(R.id.productPrice)
        val txtCantidad: TextView = view.findViewById(R.id.productQuantity)

        txtNombre.text = cartItem.nombre
        txtPrecio.text = "Precio: \$${cartItem.precioUnitario}"
        txtCantidad.text = "Cantidad: ${cartItem.cantidad}"

        return view
    }
}
