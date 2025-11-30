package com.example.selersmobile

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import java.text.SimpleDateFormat
import java.util.Locale

class AccidenteDatosActivity : AppCompatActivity() {
    private lateinit var spinnerMarca: Spinner
    private lateinit var spinnerModelo: Spinner
    private lateinit var spinnerAnio: Spinner
    private lateinit var editPatente: EditText
    private lateinit var editFecha: EditText
    private lateinit var editHora: EditText
    private lateinit var editUbicacion: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var btnSiguiente: Button
    private lateinit var btnSubirFoto: Button
    private lateinit var btnTomarFoto: Button
    private lateinit var imagePreview: ImageView

    private var imagenSeleccionadaUri: Uri? = null
    private var imagenTomada: Bitmap? = null

    companion object {
        private const val REQUEST_GALERIA = 1001
        private const val REQUEST_CAMARA = 1002
        private const val PERMISO_CAMARA = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datosaccidente)

        spinnerMarca = findViewById(R.id.spinnerMarca)
        spinnerModelo = findViewById(R.id.spinnerModelo)
        spinnerAnio = findViewById(R.id.spinnerAnio)
        editPatente = findViewById(R.id.editPatente)
        editFecha = findViewById(R.id.editFecha)
        editHora = findViewById(R.id.editHora)
        editUbicacion = findViewById(R.id.editUbicacion)
        editTextDescription = findViewById(R.id.editTextDescription)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        btnSubirFoto = findViewById(R.id.btnSubirFoto)
        btnTomarFoto = findViewById(R.id.btnTomarFoto)
        imagePreview = findViewById(R.id.imagePreview)

        imagePreview.setImageURI(imagenSeleccionadaUri)
        imagePreview.visibility = View.VISIBLE

        cargarDatosEnSpinners()

        editFecha.setOnClickListener { mostrarDatePicker() }
        editHora.setOnClickListener { mostrarTimePicker() }

        // Evitar edición manual de hora, solo seleccionar con picker
        editHora.isFocusable = false
        editHora.isClickable = true

        btnSubirFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_GALERIA)
        }

        btnTomarFoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISO_CAMARA)
            } else {
                abrirCamara()
            }
        }

        btnSiguiente.setOnClickListener {
            validarYEnviar()
        }

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()
        }

        val tipoVehiculo = intent.getStringExtra("vehiculo")
        Toast.makeText(this, "Vehículo seleccionado: $tipoVehiculo", Toast.LENGTH_SHORT).show()

        val title = findViewById<TextView>(R.id.title)
        title.text = "Datos del accidente con $tipoVehiculo"
    }

    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            editFecha.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
        }, year, month, day).show()
    }

    private fun mostrarTimePicker() {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, h, m ->
            val amPm = if (h >= 12) "PM" else "AM"
            val h12 = if (h > 12) h - 12 else if (h == 0) 12 else h
            editHora.setText(String.format("%02d:%02d %s", h12, m, amPm))
        }, hora, minuto, false).show()
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMARA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISO_CAMARA && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirCamara()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_GALERIA -> {
                    imagenSeleccionadaUri = data.data
                    imagePreview.setImageURI(imagenSeleccionadaUri)
                }
                REQUEST_CAMARA -> {
                    imagenTomada = data.extras?.getParcelable("data")
                    imagePreview.setImageBitmap(imagenTomada)
                }
            }
        }
    }

    private fun cargarDatosEnSpinners() {
        val marcas = listOf("Seleccione una marca", "Toyota", "Ford", "Chevrolet", "Renault", "Volkswagen", "Fiat", "Honda")
        val modelos = listOf("Seleccione un modelo", "Corolla", "Focus", "Onix", "Clio", "Gol", "Cronos", "Civic")
        val anios = listOf("Seleccione un año") + (2019..2025).map { it.toString() }

        spinnerMarca.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, marcas)
        spinnerModelo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, modelos)
        spinnerAnio.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, anios)
    }

    private fun validarYEnviar() {
        val patente = editPatente.text.toString().trim()
        val fecha = editFecha.text.toString().trim()
        val hora = editHora.text.toString().trim()
        val ubicacion = editUbicacion.text.toString().trim()
        val descripcion = editTextDescription.text.toString().trim()
        val marcaValida = spinnerMarca.selectedItemPosition != 0
        val modeloValido = spinnerModelo.selectedItemPosition != 0
        val anioValido = spinnerAnio.selectedItemPosition != 0
        val hayFoto = imagenSeleccionadaUri != null || imagenTomada != null

        // Validación general de campos vacíos
        if (patente.isEmpty() || fecha.isEmpty() || hora.isEmpty() || ubicacion.isEmpty() || descripcion.isEmpty()
            || !marcaValida || !modeloValido || !anioValido || !hayFoto) {
            Toast.makeText(this, "Campos incompletos. Revisá e intentá de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        // Marca
        if (!marcaValida) {
            Toast.makeText(this, "Seleccioná una marca. Revisá e intentá de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        // Modelo
        if (!modeloValido) {
            Toast.makeText(this, "Seleccioná un modelo. Revisá e intentá de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        // Año
        if (!anioValido) {
            Toast.makeText(this, "Seleccioná un año. Revisá e intentá de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        // Patente / Identificador
        val regexPatente = Regex("^[A-Z]{3}[0-9]{3}\$|^[A-Z]{2}[0-9]{3}[A-Z]{2}\$")
        val regexIdentificador = Regex("^[A-Za-z0-9]{6,12}\$")

        if (!regexPatente.matches(patente) && !regexIdentificador.matches(patente)) {
            editPatente.error = "Ingresá un formato de patente válido (ej: ABC123 o AB123CD).\nO un identificador válido (6 a 12 caracteres alfanuméricos). Revisá e intentá de nuevo."
            editPatente.requestFocus()
            return
        }

        // Fecha
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaSeleccionada = try {
            sdf.parse(fecha)
        } catch (e: Exception) {
            null
        }

        if (fechaSeleccionada == null || fechaSeleccionada.after(Calendar.getInstance().time)) {
            editFecha.error = "Ingresá una fecha válida que no sea futura. Revisá e intentá de nuevo."
            editFecha.requestFocus()
            return
        }

        // Hora
        val regexHora = Regex("^(0[1-9]|1[0-2]):[0-5][0-9] (AM|PM)\$")

        if (!regexHora.matches(hora)) {
            editHora.error = "Ingresá una hora válida. Revisá e intentá de nuevo."
            editHora.requestFocus()
            return
        }

        // Ubicación
        if (ubicacion.isEmpty()) {
            editUbicacion.error = "Complete el campo de ubicación. Revisá e intentá de nuevo."
            editUbicacion.requestFocus()
            return
        }

        // Descripción
        if (descripcion.isEmpty()) {
            editTextDescription.error = "Complete el campo de descripción. Revisá e intentá de nuevo."
            editTextDescription.requestFocus()
            return
        }

        // Imagen
        if (!hayFoto) {
            Toast.makeText(this, "Complete el campo de foto. Revisá e intentá de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        irASiguientePantalla()
    }

    private fun irASiguientePantalla() {
        val tipoVehiculo = intent.getStringExtra("vehiculo")
        val intent = Intent(this, ConfirmarAccidenteActivity::class.java).apply {
            putExtra("vehiculo", tipoVehiculo)
            putExtra("marca", spinnerMarca.selectedItem.toString())
            putExtra("modelo", spinnerModelo.selectedItem.toString())
            putExtra("anio", spinnerAnio.selectedItem.toString())
            putExtra("patente", editPatente.text.toString())
            putExtra("fecha", editFecha.text.toString())
            putExtra("hora", editHora.text.toString())
            putExtra("ubicacion", editUbicacion.text.toString())
            putExtra("descripcion", editTextDescription.text.toString())
            imagenSeleccionadaUri?.let {
                putExtra("imagenUri", it.toString())
            }
        }
        startActivity(intent)
    }
}
