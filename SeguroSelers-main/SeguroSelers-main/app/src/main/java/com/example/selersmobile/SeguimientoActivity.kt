package com.example.selersmobile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SeguimientoActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento)

        sharedPreferences = getSharedPreferences("AccidentReportPrefs", MODE_PRIVATE)

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val noReportMessage = findViewById<TextView>(R.id.noReportMessage)
        val reportDetailsCard = findViewById<View>(R.id.reportDetailsCard)
        val reportDetails = findViewById<TextView>(R.id.reportDetails)

        // Flecha para volver
        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Verificar si hay un reporte guardado
        val hasReport = sharedPreferences.getBoolean("hasReport", false)

        if (hasReport) {
            noReportMessage.visibility = View.GONE
            reportDetailsCard.visibility = View.VISIBLE

            // Recuperar datos del reporte
            val reportId = sharedPreferences.getString("reportId", "N/A") ?: "N/A"
            val vehicleType = sharedPreferences.getString("vehicleType", "N/A")?.capitalize() ?: "N/A"
            val reportDate = sharedPreferences.getString("reportDate", "N/A") ?: "N/A"
            val reportStatus = sharedPreferences.getString("reportStatus", "En curso") ?: "En curso"

            // Nombre del usuario (fijo por ahora, puedes ajustarlo)
            val userName = "Wilson Mejia"

            // Mostrar los datos
            reportDetails.text = "Usuario: $userName\nID de reporte: $reportId\nFecha: $reportDate\nVehículo: $vehicleType\nEstado: $reportStatus"
        } else {
            noReportMessage.visibility = View.VISIBLE
            reportDetailsCard.visibility = View.GONE
        }
    }
}