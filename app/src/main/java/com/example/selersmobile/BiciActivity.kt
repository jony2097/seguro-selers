package com.example.selersmobile

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class BiciActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bicicleta)

        sharedPreferences = getSharedPreferences("InsurancePrefs", MODE_PRIVATE)

        val radioParcial = findViewById<RadioButton>(R.id.radioParcial)
        val radioTotal = findViewById<RadioButton>(R.id.radioTotal)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        // Asegurar que solo uno esté seleccionado
        radioParcial.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) radioTotal.isChecked = false
        }

        radioTotal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) radioParcial.isChecked = false
        }

        // Acción al confirmar
        btnConfirmar.setOnClickListener {
            val patent = generarPatente("BICI")
            val idGenerado = generarNumeroPoliza()
            val vigencia = generarFechaVigencia()

            when {
                radioParcial.isChecked -> {
                    guardarSeguro("BICI", "Plan Parcial", idGenerado, patent, vigencia)
                    mostrarAlertaExito("Plan Parcial", idGenerado)
                }
                radioTotal.isChecked -> {
                    guardarSeguro("BICI", "Plan Total", idGenerado, patent, vigencia)
                    mostrarAlertaExito("Plan Total", idGenerado)
                }
                else -> {
                    Toast.makeText(this, "Por favor, seleccioná un plan antes de continuar.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Flecha para volver
        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Función para generar un número de póliza aleatorio (10 dígitos)
    private fun generarNumeroPoliza(): String {
        val random = Random.nextLong(1_000_000_000L, 9_999_999_999L)
        return random.toString()
    }

    // Función para generar una fecha de vigencia (1 año desde hoy, con variación de 0 a 6 meses)
    private fun generarFechaVigencia(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, 1) // 1 año desde hoy (29/05/2025 -> 29/05/2026)
        calendar.add(Calendar.MONTH, Random.nextInt(0, 7)) // Variación de 0 a 6 meses
        val formatter = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        return formatter.format(calendar.time) // Ejemplo: "05/2026" o "11/2026"
    }

    // Función para generar una patente argentina aleatoria
    private fun generarPatente(category: String): String {
        val letters = "ABCDEFGHJKLMNPQRSTUVWXYZ" // Evita I, Ñ, O para legibilidad
        val firstLetter = letters[Random.nextInt(letters.length)]
        val secondLetter = letters[Random.nextInt(letters.length)]
        val thirdLetter = letters[Random.nextInt(letters.length)]
        val numbers = Random.nextInt(100, 1000).toString().padStart(3, '0')
        return when (category) {
            "MOTOCICLETA" -> "$firstLetter$secondLetter$thirdLetter $numbers"
            "BICI" -> "BIC $numbers"
            "MONOPATIN" -> "MON $numbers"
            else -> "$firstLetter$secondLetter$thirdLetter $numbers"
        }
    }

    // Función para guardar los datos del seguro
    private fun guardarSeguro(category: String, plan: String, policyNumber: String, patent: String, expiration: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("hasInsurance", true)
        editor.putString("category", category)
        editor.putString("plan", plan)
        editor.putString("policyNumber", policyNumber)
        editor.putString("patent", patent)
        editor.putString("expiration", expiration)
        editor.apply()
    }

    // Función para mostrar alerta de éxito
    private fun mostrarAlertaExito(plan: String, id: String) {
        AlertDialog.Builder(this)
            .setTitle("¡Éxito!")
            .setMessage("Pack '$plan' seleccionado correctamente.\nID: $id")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Redirigir a MiSeguroActivity para ver los detalles
                val intent = Intent(this, MiSeguroActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()
    }
}