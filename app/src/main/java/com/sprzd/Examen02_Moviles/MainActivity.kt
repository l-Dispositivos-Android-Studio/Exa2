package com.sprzd.Examen02_Moviles

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.widget.ListView
import android.util.Log
import android.widget.Button
import com.sprzd.Examen02_Moviles.ui.users.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sprzd.Examen02_Moviles.entities.cls_Category
import com.sprzd.Examen02_Moviles.ui.categories.CategoryAdapter

const val valorIntentLogin = 1

class MainActivity : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    var email: String? = null
    var contra: String? = null
    var db = FirebaseFirestore.getInstance()
    var TAG = "YorkTestingApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Intenta obtener el token del usuario del almacenamiento local; si no, llama a la ventana de registro
        val prefe = getSharedPreferences("appData", Context.MODE_PRIVATE)
        email = prefe.getString("email", "")
        contra = prefe.getString("contra", "")

        // Comprueba los SharedPreferences al iniciar la actividad
        checkSharedPreferences()

        if (email.toString().trim { it <= ' ' }.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, valorIntentLogin)
        } else {
            val uid: String = auth.uid.toString()
            if (uid == "null") {
                auth.signInWithEmailAndPassword(email.toString(), contra.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Autenticación correcta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            obtenerDatos()
        }

        // Captura el botón de cerrar sesión y establece el Listener
        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            auth.signOut() // Cierra la sesión del usuario
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) // Redirige a la actividad de Login
        }


    //----------------------------------------------------------------------------------
        //comprobar que el SharedPreferences(localstorage) esta funcionando
        // Captura el botón de comprobar SharedPreferences y establece el Listener
        val btnComprobar: Button = findViewById(R.id.btnComprobar)
        btnComprobar.setOnClickListener {
            checkSharedPreferences()  // Llamar al método para verificar los datos
        }
    }

    // Método para comprobar los SharedPreferences y mostrar los valores
    private fun checkSharedPreferences() {
        val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)

        //val email = prefe.getString("email", "No email found")
        //val contra = prefe.getString("contra", "No password found")
        val customerId = prefe.getString("CustomerId", "No CustomerId found")
        val shipName = prefe.getString("shipName", "No shipName found")
        val shipAddress = prefe.getString("shipAddress", "No shipAddress found")
        val shipCity = prefe.getString("shipCity", "No shipCity found")
        val shipRegion = prefe.getString("shipRegion", "No shipRegion found")
        val shipPostalCode = prefe.getString("shipPostalCode", "No shipPostalCode found")
        val shipCountry = prefe.getString("shipCountry", "No shipCountry found")

        // Crear un String con los valores
        val message =
                     //"Email: $email\n" +
                    //"Password: $contra\n" +
                    //"CustomerId: $customerId\n" +
                    "Ship Name: $shipName\n" +
                    "Ship Address: $shipAddress\n" +
                    "Ship City: $shipCity\n" +
                    "Ship Region: $shipRegion\n" +
                    "Ship Postal Code: $shipPostalCode\n" +
                    "Ship Country: $shipCountry"

        // Mostrar los datos en un Toast para verificar
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }
    //----------------------------------------------------------------------------------




    // Método para obtener los datos de la base de datos de Firestore
    private fun obtenerDatos() {
        val coleccion: ArrayList<cls_Category?> = ArrayList()
        val listaView: ListView = findViewById(R.id.lstCategories)

        db.collection("Categories").orderBy("CategoryID").get()
            .addOnCompleteListener { docc ->
                if (docc.isSuccessful) {
                    for (document in docc.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                        val datos = cls_Category(
                            document.data["CategoryID"].toString().toInt(),
                            document.data["CategoryName"].toString(),
                            document.data["Description"].toString(),
                            document.data["urlImage"].toString()
                        )
                        coleccion.add(datos)
                    }
                    val adapter = CategoryAdapter(this, coleccion)
                    listaView.adapter = adapter
                } else {
                    Log.w(TAG, "Error getting documents.", docc.exception)
                }
            }
    }
}
