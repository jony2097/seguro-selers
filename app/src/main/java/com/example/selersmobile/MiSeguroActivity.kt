package com.example.selersmobile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MiSeguroActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_miseguro)

        sharedPreferences = getSharedPreferences("InsurancePrefs", MODE_PRIVATE)

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val noInsuranceMessage = findViewById<TextView>(R.id.noInsuranceMessage)
        val insuranceCard = findViewById<CardView>(R.id.insuranceCard)
        val cancelButton = findViewById<Button>(R.id.cancelInsuranceButton)
        val insuranceCategory = findViewById<TextView>(R.id.insuranceCategory)
        val insuranceDetails = findViewById<TextView>(R.id.insuranceDetails)

        // Flecha para volver
        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Verificar si hay un seguro guardado
        val hasInsurance = sharedPreferences.getBoolean("hasInsurance", false)

        // Controla la visibilidad según si hay un seguro
        if (hasInsurance) {
            noInsuranceMessage.visibility = View.GONE
            insuranceCard.visibility = View.VISIBLE

            // Recuperar datos del seguro
            val patent = sharedPreferences.getString("patent", "N/A") ?: "N/A"
            val expiration = sharedPreferences.getString("expiration", "N/A") ?: "N/A"
            val policyNumber = sharedPreferences.getString("policyNumber", "N/A") ?: "N/A"
            val category = sharedPreferences.getString("category", "MOTOCICLETA") ?: "MOTOCICLETA"
            val plan = sharedPreferences.getString("plan", "Plan Parcial") ?: "Plan Parcial"

            // Actualizar los TextViews con los datos
            insuranceCategory.text = category
            insuranceDetails.text = "Plan: $plan\nPatente: $patent\nVigencia hasta $expiration\nNúmero de póliza: $policyNumber"
        } else {
            noInsuranceMessage.visibility = View.VISIBLE
            insuranceCard.visibility = View.GONE
        }

        // Acción del botón "Dar de baja"
        cancelButton.setOnClickListener {
            // Diálogo de confirmación
            android.app.AlertDialog.Builder(this)
                .setTitle("Dar de baja póliza")
                .setMessage("¿Estás seguro de que quieres dar de baja tu póliza?")
                .setPositiveButton("Sí") { _, _ ->
                    // Eliminar los datos del seguro
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("hasInsurance", false)
                    editor.remove("patent")
                    editor.remove("expiration")
                    editor.remove("policyNumber")
                    editor.remove("category")
                    editor.remove("plan")
                    editor.apply()

                    // Actualizar la UI
                    noInsuranceMessage.visibility = View.VISIBLE
                    insuranceCard.visibility = View.GONE
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}