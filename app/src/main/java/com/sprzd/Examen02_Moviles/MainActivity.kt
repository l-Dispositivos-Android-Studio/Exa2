package com.sprzd.Examen02_Moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sprzd.Examen02_Moviles.ui.adapters.DBAdapter
import com.sprzd.Examen02_Moviles.ui.cart.CarProductActivity
import com.sprzd.Examen02_Moviles.ui.products.ProductAdapter
import com.sprzd.Examen02_Moviles.ui.users.LoginActivity

const val valorIntentLogin = 1

class MainActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private lateinit var dbAdapter: DBAdapter

    private var email: String? = null
    private var contra: String? = null
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicializar DBAdapter
        dbAdapter = DBAdapter(this)

        // Cargar credenciales desde SharedPreferences
        val prefe = getSharedPreferences("appData", Context.MODE_PRIVATE)
        email = prefe.getString("email", "")
        contra = prefe.getString("contra", "")

        // Verificar si ya está autenticado o necesita iniciar sesión
        verificarAutenticacion()

        // Configurar botón de cerrar sesión
        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            cerrarSesion()
        }
        val btnVerCarrito: Button = findViewById(R.id.btnVerCarrito)
        btnVerCarrito.setOnClickListener {
            // Redirigir al carrito de compras
            val intent = Intent(this, CarProductActivity::class.java)
            startActivity(intent)
        }
    }

    // Verificar si el usuario está autenticado
    private fun verificarAutenticacion() {
        // Si no hay correo o contraseña almacenados en SharedPreferences
        if (email.isNullOrEmpty() || contra.isNullOrEmpty()) {
            // Redirigir al LoginActivity si no hay email en SharedPreferences
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, valorIntentLogin)
        } else {
            // Si ya se tienen las credenciales, intentar autenticar directamente
            val currentUser = auth.currentUser
            if (currentUser == null) {
                // Si no hay usuario autenticado, hacer login con las credenciales almacenadas
                auth.signInWithEmailAndPassword(email!!, contra!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Autenticación exitosa", Toast.LENGTH_SHORT).show()
                            cargarDatosLocales() // Cargar datos locales si la autenticación fue exitosa
                        } else {
                            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivityForResult(intent, valorIntentLogin)
                        }
                    }
            } else {
                // Si ya hay un usuario autenticado, cargar los datos locales
                cargarDatosLocales()
            }
        }
    }

    // Cerrar sesión del usuario
    private fun cerrarSesion() {
        auth.signOut() // Cerrar sesión de Firebase
        val editor = getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
        editor.clear() // Eliminar las credenciales guardadas
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent) // Redirigir al LoginActivity
        finish()
    }

    // Precargar datos iniciales en SQLite
    private fun precargarDatos() {
        dbAdapter.open()
        var id: Long

        id = dbAdapter.insertProduct("sopa azteca", 10.0, 5, 1.5, 50.0, "https://th.bing.com/th/id/OIP.it_6k77yo31VZCTLRWL88AHaHL?rs=1&pid=ImgDetMain")
        Log.v(TAG, "Producto registrado con ID: $id")

        id = dbAdapter.insertProduct("olla de carne", 20.0, 2, 2.0, 40.0, "https://th.bing.com/th/id/OIP.EgTLcaHIMivJxsLXZ6YQHAHaE8?rs=1&pid=ImgDetMain")
        Log.v(TAG, "Producto registrado con ID: $id")

        id = dbAdapter.insertProduct("camarones jumbo", 5.0, 10, 0.5, 50.0, "https://static.vecteezy.com/system/resources/previews/003/152/859/non_2x/grilled-river-prawns-or-shrimps-seafood-style-photo.jpg")
        Log.v(TAG, "Producto registrado con ID: $id")

        id = dbAdapter.insertProduct("torta chilena", 15.0, 3, 2.5, 45.0, "https://th.bing.com/th/id/R.88786c35aa63cd21fb32913a8f741ac1?rik=ggOuozLGokGODg&pid=ImgRaw&r=0")
        Log.v(TAG, "Producto registrado con ID: $id")

        dbAdapter.close()
    }

    // Cargar y mostrar datos desde SQLite
    private fun cargarDatosLocales() {
        dbAdapter.open()
        val cursor = dbAdapter.getAllProducts
        if (cursor.count == 0) {
            precargarDatos() // Si no hay datos, precargar
        } else {
            val listView: ListView = findViewById(R.id.productListView)
            val adapter = ProductAdapter(this, cursor)
            listView.adapter = adapter
        }
        dbAdapter.close()
    }

    // Manejar resultado del LoginActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == valorIntentLogin && resultCode == RESULT_OK) {
            email = data?.getStringExtra("email")
            contra = data?.getStringExtra("contra")

            // Guardar credenciales en SharedPreferences
            val editor = getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
            editor.putString("email", email)
            editor.putString("contra", contra)
            editor.apply()

            verificarAutenticacion()
        }
    }
}
