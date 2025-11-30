package com.example.selersmobile

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ConfirmarAccidenteActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmaraccidente)

        sharedPreferences = getSharedPreferences("AccidentReportPrefs", MODE_PRIVATE)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        val marca = intent.getStringExtra("marca") ?: ""
        val modelo = intent.getStringExtra("modelo") ?: ""
        val anio = intent.getStringExtra("anio") ?: ""
        val patente = intent.getStringExtra("patente") ?: ""
        val fecha = intent.getStringExtra("fecha") ?: ""
        val hora = intent.getStringExtra("hora") ?: ""
        val ubicacion = intent.getStringExtra("ubicacion") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val tipoVehiculo = intent.getStringExtra("vehiculo") ?: ""

        cargarDatoEnCard(R.id.cardTipoVehiculo, "Tipo de Vehículo", tipoVehiculo)
        cargarDatoEnCard(R.id.cardDatosVehiculo, "Datos del Vehículo", "$marca $modelo $anio")
        cargarDatoEnCard(R.id.cardPatente, "Patente", patente)
        cargarDatoEnCard(R.id.cardFecha, "Fecha", fecha)
        cargarDatoEnCard(R.id.cardHora, "Hora", hora)
        cargarDatoEnCard(R.id.cardUbicacion, "Ubicación", ubicacion)
        cargarDatoEnCard(R.id.cardDescripcion, "Descripción", descripcion)

        btnGuardar.setOnClickListener {
            // Generar ID aleatorio
            val idGestion = (100000..999999).random()

            // Guardar los datos del reporte en SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putBoolean("hasReport", true)
            editor.putString("reportId", idGestion.toString())
            editor.putString("vehicleType", tipoVehiculo)
            editor.putString("reportDate", fecha)
            editor.putString("reportStatus", "En curso") // Estado inicial
            editor.apply()


            AlertDialog.Builder(this)
                .setTitle("Gestión exitosa")
                .setMessage("La gestión fue realizada con éxito.\nID de gestión: $idGestion")
                .setPositiveButton("OK") { _, _ ->
                    // Ir a MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish() // Cierra esta pantalla
                }
                .show()
        }

        btnCancelar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cancelar gestión")
                .setMessage("¿Estás seguro de que querés cancelar esta gestión?")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun cargarDatoEnCard(cardId: Int, label: String, valor: String) {
        val cardView = findViewById<View>(cardId)
        val textLabel = cardView.findViewById<TextView>(R.id.fieldLabel)
        val textValue = cardView.findViewById<TextView>(R.id.fieldValue)
        val editIcon = cardView.findViewById<ImageView>(R.id.editIcon)

        textLabel.text = label
        textValue.text = valor

        editIcon.setOnClickListener {
            mostrarDialogoEdicion(textLabel.text.toString(), textValue)
        }
    }

    private fun mostrarDialogoEdicion(titulo: String, textViewParaActualizar: TextView) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_field, null)
        val input = dialogView.findViewById<EditText>(R.id.editFieldInput)
        input.setText(textViewParaActualizar.text.toString())

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar $titulo")
            .setView(dialogView)
            .setPositiveButton("Guardar", null) // manejamos manualmente luego
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val botonGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            botonGuardar.setOnClickListener {
                val nuevoTexto = input.text.toString().trim()

                // Validaciones comunes para campos vacíos
                if (nuevoTexto.isEmpty()) {
                    val mensajeVacio = when(titulo) {
                        "Marca" -> "Seleccioná una marca. Revisá e intentá de nuevo."
                        "Modelo" -> "Seleccioná un modelo. Revisá e intentá de nuevo."
                        "Año" -> "Seleccioná un año. Revisá e intentá de nuevo."
                        "Patente" -> "Complete el campo de patente/identificador. Revisá e intentá de nuevo."
                        "Fecha" -> "Seleccioná una fecha. Revisá e intentá de nuevo."
                        "Hora" -> "Seleccioná una hora. Revisá e intentá de nuevo."
                        "Ubicación" -> "Complete el campo de ubicación. Revisá e intentá de nuevo."
                        "Descripción" -> "Complete el campo de descripción. Revisá e intentá de nuevo."
                        else -> "Complete el campo. Revisá e intentá de nuevo."
                    }
                    input.error = mensajeVacio
                    return@setOnClickListener
                }

                // Validaciones específicas por campo
                when(titulo) {
                    "Tipo de Vehículo" -> {
                        val valoresValidos = listOf("bicicleta", "monopatin", "motocicleta")
                        if (!valoresValidos.contains(nuevoTexto.lowercase())) {
                            input.error = "Solo se permite: bicicleta, monopatin o motocicleta"
                            return@setOnClickListener
                        }
                    }
                    "Marca", "Modelo", "Ubicación", "Descripción" -> {
                        // Ya validado que no está vacío arriba, nada más
                    }
                    "Año" -> {
                        if (!nuevoTexto.matches(Regex("\\d{4}"))) {
                            input.error = "Seleccioná un año. Revisá e intentá de nuevo."
                            return@setOnClickListener
                        } else {
                            val anio = nuevoTexto.toInt()
                            if (anio < 1900 || anio > 2100) {
                                input.error = "Seleccioná un año. Revisá e intentá de nuevo."
                                return@setOnClickListener
                            }
                        }
                    }
                    "Patente" -> {
                        val regexPatente = Regex("^[A-Z]{3}[0-9]{3}\$|^[A-Z]{2}[0-9]{3}[A-Z]{2}\$")
                        val regexIdentificador = Regex("^[A-Za-z0-9]{6,12}\$")
                        if (!regexPatente.matches(nuevoTexto) && !regexIdentificador.matches(nuevoTexto)) {
                            input.error = "Ingresá un formato de patente válido (ej: ABC123 o AB123CD) o un identificador válido (6 a 12 caracteres alfanuméricos). Revisá e intentá de nuevo."
                            return@setOnClickListener
                        }
                    }
                    "Fecha" -> {
                        val fechaRegex = Regex("^\\d{2}/\\d{2}/\\d{4}\$")
                        if (!fechaRegex.matches(nuevoTexto)) {
                            input.error = "Ingresá una fecha válida que no sea futura. Revisá e intentá de nuevo."
                            return@setOnClickListener
                        }
                        // Validar no sea futura
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val fechaSeleccionada = try {
                            sdf.parse(nuevoTexto)
                        } catch (e: Exception) {
                            null
                        }
                        if (fechaSeleccionada == null || fechaSeleccionada.after(Calendar.getInstance().time)) {
                            input.error = "Ingresá una fecha válida que no sea futura. Revisá e intentá de nuevo."
                            return@setOnClickListener
                        }
                    }
                    "Hora" -> {
                        val regexHora = Regex("^(0[1-9]|1[0-2]):[0-5][0-9] (AM|PM)\$")
                        if (!regexHora.matches(nuevoTexto)) {
                            input.error = "Ingresá una hora válida. Revisá e intentá de nuevo."
                            return@setOnClickListener
                        }
                    }
                }

                // Si pasó todas las validaciones
                textViewParaActualizar.text = nuevoTexto
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}
