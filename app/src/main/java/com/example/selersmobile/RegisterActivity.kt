package com.example.selersmobile

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var nombreInput: EditText
    private lateinit var apellidoInput: EditText
    private lateinit var correoInput: EditText
    private lateinit var contraseñaInput: EditText
    private lateinit var confirmarContraseñaInput: EditText
    private lateinit var crearCuentaButton: Button
    private lateinit var showPassword: ImageView
    private lateinit var showConfirmPassword: ImageView

    private var passwordVisible = false
    private var confirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nombreInput = findViewById(R.id.nombre_input)
        apellidoInput = findViewById(R.id.apellido_input)
        correoInput = findViewById(R.id.correo_input)
        contraseñaInput = findViewById(R.id.contraseña_input)
        confirmarContraseñaInput = findViewById(R.id.confirmar_contraseña_input)
        crearCuentaButton = findViewById(R.id.crear_cuenta_button)

        showPassword = findViewById(R.id.show_password)
        showConfirmPassword = findViewById(R.id.show_confirmar_password)

        // Iniciar con contraseña oculta y ojo cerrado
        passwordVisible = false
        confirmPasswordVisible = false
        contraseñaInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmarContraseñaInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        showPassword.setImageResource(R.drawable.icerrado)
        showConfirmPassword.setImageResource(R.drawable.icerrado)

        // Toggle mostrar/ocultar contraseña principal
        showPassword.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                contraseñaInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPassword.setImageResource(R.drawable.iconoojo) // ojo abierto
            } else {
                contraseñaInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPassword.setImageResource(R.drawable.icerrado)  // ojo cerrado
            }
            contraseñaInput.setSelection(contraseñaInput.text.length)
        }

        // Toggle mostrar/ocultar confirmar contraseña
        showConfirmPassword.setOnClickListener {
            confirmPasswordVisible = !confirmPasswordVisible
            if (confirmPasswordVisible) {
                confirmarContraseñaInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showConfirmPassword.setImageResource(R.drawable.iconoojo)  // ojo abierto
            } else {
                confirmarContraseñaInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showConfirmPassword.setImageResource(R.drawable.icerrado)  // ojo cerrado
            }
            confirmarContraseñaInput.setSelection(confirmarContraseñaInput.text.length)
        }

        crearCuentaButton.setOnClickListener {
            val nombre = nombreInput.text.toString().trim()
            val apellido = apellidoInput.text.toString().trim()
            val correo = correoInput.text.toString().trim()
            val contraseña = contraseñaInput.text.toString()
            val confirmarContraseña = confirmarContraseñaInput.text.toString()

            if (validarDatos(nombre, apellido, correo, contraseña, confirmarContraseña)) {
                showLongToast("Registro exitoso. Ya podés empezar a usar tu cuenta.")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        val irALoginTextView = findViewById<TextView>(R.id.ir_a_login)
        irALoginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validarDatos(
        nombre: String,
        apellido: String,
        correo: String,
        contraseña: String,
        confirmarContraseña: String
    ): Boolean {

        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty()) {
            showLongToast("Campos incompletos. Revisá e intentá de nuevo.")
            limpiarCampos()
            return false
        }

        val nombreRegex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+\$")
        if (!nombreRegex.matches(nombre) || !nombreRegex.matches(apellido)) {
            showLongToast("Nombre o apellido no válido.\nPor favor, ingresá texto válido.\nNo se permiten números ni caracteres especiales.")
            limpiarCampos()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            showLongToast("El formato del correo electrónico no es válido.\nPor favor, ingresá una dirección válida.")
            limpiarCampos()
            return false
        }

        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}\$")
        if (!passwordRegex.matches(contraseña)) {
            showLongToast("La contraseña ingresada no es válida.\nDebe tener al menos 8 caracteres, incluir una mayúscula, una minúscula y un número.")
            limpiarCampos()
            return false
        }

        if (contraseña != confirmarContraseña) {
            showLongToast("Las contraseñas no coinciden.\nPor favor, verificá que ambas sean iguales.")
            limpiarCampos()
            return false
        }

        return true
    }

    private fun limpiarCampos() {
        nombreInput.text.clear()
        apellidoInput.text.clear()
        correoInput.text.clear()
        contraseñaInput.text.clear()
        confirmarContraseñaInput.text.clear()
    }

    private fun showLongToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_VERTICAL or Gravity.FILL_HORIZONTAL, 0, 100)
        toast.show()
    }
}
