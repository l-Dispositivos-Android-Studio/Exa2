package com.sprzd.Examen02_Moviles.ui.users

import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.content.Intent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sprzd.Examen02_Moviles.R
import java.util.Date

class SignupActivity : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()

    private lateinit var txtRNombre: EditText
    private lateinit var txtREmail: EditText
    private lateinit var txtRContra: EditText
    private lateinit var txtRreContra: EditText
    private lateinit var btnRegistrarU: Button
    private lateinit var txtRegisterCustomer: TextView
    private lateinit var spinnerSelectCustomer: Spinner
    private lateinit var txtContactName: EditText
    private lateinit var txtContactTitle: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        txtRNombre = findViewById(R.id.txtRNombre)
        txtREmail = findViewById(R.id.txtREmail)
        txtRContra = findViewById(R.id.txtRContra)
        txtRreContra = findViewById(R.id.txtRreContra)
        btnRegistrarU = findViewById(R.id.btnRegistrarU)
        txtRegisterCustomer = findViewById(R.id.txtRegisterC)
        spinnerSelectCustomer = findViewById(R.id.spinnerSelectCustomer)
        txtContactName = findViewById(R.id.txtContactName)
        txtContactTitle = findViewById(R.id.txtContactTitle)

        // Cargar los clientes en el Spinner
        cargarClientes()

        btnRegistrarU.setOnClickListener {
            registrarUsuario()
        }

        // Redirigir a la actividad de registro de cliente
        txtRegisterCustomer.setOnClickListener {
            val intent = Intent(this, SignupCustomerActivity::class.java)
            startActivity(intent)
        }
    }

    // Cargar los clientes de la colección "clients" en el Spinner
    private fun cargarClientes() {
        db.collection("clients").get()
            .addOnSuccessListener { documents ->
                val customerList = mutableListOf<String>()
                for (document in documents) {
                    val customerId = document.id
                    customerList.add(customerId)
                }

                // Configurar el adaptador del Spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, customerList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSelectCustomer.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar los clientes", Toast.LENGTH_SHORT).show()
            }
    }

    // Registrar el usuario y asociarlo con el cliente seleccionado
    private fun registrarUsuario() {
        val nombre = txtRNombre.text.toString()
        val email = txtREmail.text.toString()
        val contra = txtRContra.text.toString()
        val reContra = txtRreContra.text.toString()
        val selectedCustomerId = spinnerSelectCustomer.selectedItem.toString()
        val contactName = txtContactName.text.toString()
        val contactTitle = txtContactTitle.text.toString()

        if (nombre.isEmpty() || email.isEmpty() || contra.isEmpty() || reContra.isEmpty()) {
            Toast.makeText(this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            if (contra == reContra) {
                auth.createUserWithEmailAndPassword(email, contra)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val dt: Date = Date()

                            // creando un objeto para un nuevo usuario
                            val user = hashMapOf(
                                "idemp" to task.result?.user?.uid,
                                "usuario" to nombre,
                                "email" to email,
                                "ultAcceso" to dt.toString(),
                                "CustomerId" to selectedCustomerId // Asociar el ID del cliente
                            )

                            // registrar el usuario en la coleccion "datosUsuarios"
                            db.collection("datosUsuarios")
                                .add(user)
                                .addOnSuccessListener { documentReference ->
                                    // Actualizar los ptos datos de ContactName y ContactTitle en la colección "clients"
                                    val clientData = hashMapOf<String, String>(
                                        "contactName" to contactName,
                                        "contactTitle" to contactTitle
                                    ) as MutableMap<String, Any>

                                    // actualizar la colección "clients" con el nuevo ContactName y ContactTitle
                                    db.collection("clients").document(selectedCustomerId)
                                        .update(clientData)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()

                                            // save los datos del usuario en SharedPreferences para el pto localstorage
                                            val prefs = getSharedPreferences("appData", Context.MODE_PRIVATE)
                                            val editor = prefs.edit()
                                            editor.putString("email", email)
                                            editor.putString("contra", contra)
                                            editor.apply()

                                            // Redirigir al LoginActivity xd
                                            val intent = Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish() // cerrar la actividad signup
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "Error al actualizar datos del cliente", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
