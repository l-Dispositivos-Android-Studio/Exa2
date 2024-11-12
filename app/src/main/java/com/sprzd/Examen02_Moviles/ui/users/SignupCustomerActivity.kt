package com.sprzd.Examen02_Moviles.ui.users

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.sprzd.Examen02_Moviles.R
import com.sprzd.Examen02_Moviles.entities.cls_Customers

class SignupCustomerActivity : AppCompatActivity() {

    // Campos de entrada (EditText)
    private lateinit var edtCustomerID: EditText
    private lateinit var edtContactName: EditText
    private lateinit var edtContactTitle: EditText
    private lateinit var edtShipName: EditText
    private lateinit var edtShipAddress: EditText
    private lateinit var edtShipCity: EditText
    private lateinit var edtShipRegion: EditText
    private lateinit var edtShipPostalCode: EditText
    private lateinit var edtShipCountry: EditText
    private lateinit var btnRegistrarC: Button

    // Instancia de Firebase Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        // Inicialización de los campos de entrada
        edtCustomerID = findViewById(R.id.edtCustomerID)
        edtContactName = findViewById(R.id.edtContactName)
        edtContactTitle = findViewById(R.id.edtContactTitle)
        edtShipName = findViewById(R.id.edtShipName)
        edtShipAddress = findViewById(R.id.edtShipAddress)
        edtShipCity = findViewById(R.id.edtShipCity)
        edtShipRegion = findViewById(R.id.edtShipRegion)
        edtShipPostalCode = findViewById(R.id.edtShipPostalCode)
        edtShipCountry = findViewById(R.id.edtShipCountry)
        btnRegistrarC = findViewById(R.id.btnRegistrarC)

        // Configurar el listener para el botón de guardar
        btnRegistrarC.setOnClickListener {
            saveCustomerToFirebase()
        }
    }

    // Función para guardar el cliente en Firebase Firestore
    private fun saveCustomerToFirebase() {
        // Obtener los valores ingresados por el usuario
        val customerID = edtCustomerID.text.toString().trim()
        val contactName = edtContactName.text.toString().trim()
        val contactTitle = edtContactTitle.text.toString().trim()
        val shipName = edtShipName.text.toString().trim()
        val shipAddress = edtShipAddress.text.toString().trim()
        val shipCity = edtShipCity.text.toString().trim()
        val shipRegion = edtShipRegion.text.toString().trim()
        val shipPostalCode = edtShipPostalCode.text.toString().trim()
        val shipCountry = edtShipCountry.text.toString().trim()

        // Verificar si alguno de los campos está vacío
        if (customerID.isEmpty() || contactName.isEmpty() || contactTitle.isEmpty() || shipName.isEmpty() ||
            shipAddress.isEmpty() || shipCity.isEmpty() || shipRegion.isEmpty() || shipPostalCode.isEmpty() ||
            shipCountry.isEmpty()) {
            // Mostrar un mensaje de error si los campos no están completos
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
        } else {
            // Crear una instancia de cls_Customers con los datos ingresados
            val customer = cls_Customers(
                IDCustomer = customerID,
                contactName = contactName,
                contactTitle = contactTitle,
                shipName = shipName,
                shipAddress = shipAddress,
                shipCity = shipCity,
                shipRegion = shipRegion,
                shipPostalCode = shipPostalCode,
                shipCountry = shipCountry
            )

            // Usar Firestore para agregar el documento a la colección "clients"
            db.collection("clients")  // Nombre de la colección
                .document(customerID)  // Usar el customerID como ID del documento
                .set(customer)  // Usar .set() para asignar el cliente con el customerID como ID
                .addOnSuccessListener {
                    // Mostrar un mensaje si el cliente se guarda correctamente
                    Toast.makeText(this, "Cliente guardado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()  // Cerrar la actividad después de guardar el cliente
                }
                .addOnFailureListener { e ->
                    // Mostrar un mensaje de error si ocurre un problema al guardar el cliente
                    Toast.makeText(this, "Error al guardar el cliente: ${e.message}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()  // Imprimir el error para facilitar la depuración
                }
        }
    }
}
