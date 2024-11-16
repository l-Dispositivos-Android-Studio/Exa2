package com.sprzd.Examen02_Moviles.ui.cart

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sprzd.Examen02_Moviles.R
import com.sprzd.Examen02_Moviles.entities.cls_Product
import com.sprzd.Examen02_Moviles.ui.adapters.CarProductAdapter

class CarProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_product)

        // Obtener los productos del carrito pasados en el Intent
        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<cls_Product>

        // Verificar que el carrito no esté vacío
        if (cartItems.isNullOrEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        } else {
            // Usar el adapter para llenar el ListView con los productos del carrito
            val listView: ListView = findViewById(R.id.listViewCarProduct)
            val adapter = CarProductAdapter(this, cartItems)
            listView.adapter = adapter
        }
    }
}
