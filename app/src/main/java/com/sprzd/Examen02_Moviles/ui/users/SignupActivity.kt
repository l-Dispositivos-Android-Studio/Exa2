package com.sprzd.Examen02_Moviles.ui.users

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sprzd.Examen02_Moviles.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class SignupActivity : AppCompatActivity() {
    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    private lateinit var txtRNombre: EditText
    private lateinit var txtREmail: EditText
    private lateinit var txtRContra: EditText
    private lateinit var txtRreContra: EditText
    private lateinit var btnRegistrarU: Button
    private lateinit var txtRegisterCustomer: TextView  // Aquí agregamos el TextView para el registro del cliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        txtRNombre = findViewById(R.id.txtRNombre)
        txtREmail = findViewById(R.id.txtREmail)
        txtRContra = findViewById(R.id.txtRContra)
        txtRreContra = findViewById(R.id.txtRreContra)
        btnRegistrarU = findViewById(R.id.btnRegistrarU)
        txtRegisterCustomer = findViewById(R.id.txtRegisterC) // Aquí referenciamos el TextView

        btnRegistrarU.setOnClickListener {
            registrarUsuario()
        }

        // Agregamos el OnClickListener para redirigir a SignupCustomerActivity
        txtRegisterCustomer.setOnClickListener {
            val intent = Intent(this, SignupCustomerActivity::class.java)
            startActivity(intent)  // Redirige a la actividad de registro de cliente
        }
    }

    private fun registrarUsuario() {
        val nombre = txtRNombre.text.toString()
        val email = txtREmail.text.toString()
        val contra = txtRContra.text.toString()
        val reContra = txtRreContra.text.toString()

        if (nombre.isEmpty() || email.isEmpty() || contra.isEmpty() || reContra.isEmpty()) {
            Toast.makeText(this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            if (contra == reContra) {
                auth.createUserWithEmailAndPassword(email, contra)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val dt: Date = Date()
                            val user = hashMapOf(
                                "idemp" to task.result?.user?.uid,
                                "usuario" to nombre,
                                "email" to email,
                                "ultAcceso" to dt.toString(),
                            )
                            db.collection("datosUsuarios")
                                .add(user)
                                .addOnSuccessListener { documentReference ->

                                    // Guardar datos en el almacenamiento local
                                    val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)
                                    val editor = prefe.edit()
                                    editor.putString("email", email)
                                    editor.putString("contra", contra)
                                    editor.commit()

                                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()

                                    // Redirigir a SignupCustomerActivity
                                    val intent = Intent(this, SignupCustomerActivity::class.java)
                                    startActivity(intent)  // Redirige a la actividad de registro de cliente
                                    finish()  // Finaliza la actividad actual
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
