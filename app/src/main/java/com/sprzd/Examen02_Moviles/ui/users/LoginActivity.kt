package com.sprzd.Examen02_Moviles.ui.users

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sprzd.Examen02_Moviles.R
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

const val valorIntentSignup = 1

class LoginActivity : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    private lateinit var btnAutenticar: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        btnAutenticar = findViewById(R.id.btnAutenticar)
        txtEmail = findViewById(R.id.txtEmail)
        txtContra = findViewById(R.id.txtContra)
        txtRegister = findViewById(R.id.txtRegister)

        txtRegister.setOnClickListener {
            goToSignup()
        }

        btnAutenticar.setOnClickListener {
            if (txtEmail.text.isNotEmpty() && txtContra.text.isNotEmpty()) {
                auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtContra.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val dt: Date = Date()
                        val user = hashMapOf("ultAcceso" to dt.toString())

                        // Buscar el documento del usuario en "datosUsuarios"
                        db.collection("datosUsuarios").whereEqualTo("idemp", it.result?.user?.uid.toString()).get()
                            .addOnSuccessListener { documentReference ->
                                documentReference.forEach { document ->
                                    // Actualizar la fecha de acceso
                                    db.collection("datosUsuarios").document(document.id).update(user as Map<String, Any>)

                                    // Obtener el ID del cliente asociado
                                    val customerId = document.getString("CustomerId")

                                    // Recuperar los datos del cliente de la colección "clients"
                                    if (customerId != null) {
                                        db.collection("clients").document(customerId).get()
                                            .addOnSuccessListener { clientDoc ->
                                                if (clientDoc.exists()) {
                                                    // Recuperar los detalles del cliente
                                                    val shipName = clientDoc.getString("shipName")
                                                    val shipAddress = clientDoc.getString("shipAddress")
                                                    val shipCity = clientDoc.getString("shipCity")
                                                    val shipRegion = clientDoc.getString("shipRegion")
                                                    val shipPostalCode = clientDoc.getString("shipPostalCode")
                                                    val shipCountry = clientDoc.getString("shipCountry")

                                                    // Guardar los datos del cliente y del usuario en SharedPreferences
                                                    val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)
                                                    val editor = prefe.edit()
                                                    editor.putString("email", txtEmail.text.toString())
                                                    editor.putString("contra", txtContra.text.toString())
                                                    editor.putString("CustomerId", customerId)
                                                    editor.putString("shipName", shipName)
                                                    editor.putString("shipAddress", shipAddress)
                                                    editor.putString("shipCity", shipCity)
                                                    editor.putString("shipRegion", shipRegion)
                                                    editor.putString("shipPostalCode", shipPostalCode)
                                                    editor.putString("shipCountry", shipCountry)
                                                    editor.apply()

                                                    // Redirigir a la actividad principal
                                                    Intent().let {
                                                        setResult(Activity.RESULT_OK)
                                                        finish()
                                                    }
                                                } else {
                                                    showAlert("Error", "Cliente no encontrado")
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(this, "Error al obtener los datos del cliente", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al actualizar los datos del usuario", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        showAlert("Error", "Al autenticar el usuario")
                    }
                }
            } else {
                showAlert("Error", "El correo electrónico y contraseña no pueden estar vacíos")
            }
        }
    }

    private fun goToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivityForResult(intent, valorIntentSignup)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            Intent().let {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun showAlert(titu: String, mssg: String) {
        val diagMessage = AlertDialog.Builder(this)
        diagMessage.setTitle(titu)
        diagMessage.setMessage(mssg)
        diagMessage.setPositiveButton("Aceptar", null)

        val diagVentana: AlertDialog = diagMessage.create()
        diagVentana.show()
    }
}
