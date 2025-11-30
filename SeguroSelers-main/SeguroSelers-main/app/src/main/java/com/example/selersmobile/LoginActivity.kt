package com.example.selersmobile

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.selersmobile.MainActivity
import com.example.selersmobile.R
import com.example.selersmobile.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var showPassword: ImageView
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView

    private var passwordVisible = false

    // Simulamos un usuario registrado (en la vida real esto vendría de la base de datos)
    private val dummyEmail = "usuario@correo.com"
    private val dummyPassword = "Hola123456"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        showPassword = findViewById(R.id.show_password)
        loginButton = findViewById(R.id.login_button)
        registerLink = findViewById(R.id.register_link)

        // Inicializamos con contraseña oculta y ojo cerrado
        passwordVisible = false
        passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        showPassword.setImageResource(R.drawable.icerrado)

        // Toggle mostrar/ocultar contraseña
        showPassword.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPassword.setImageResource(R.drawable.iconoojo) // ojo abierto
            } else {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPassword.setImageResource(R.drawable.icerrado)  // ojo cerrado
            }
            passwordInput.setSelection(passwordInput.text.length) // Mantener cursor al final
        }

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (!isValidLogin(email, password)) return@setOnClickListener

            if (email == dummyEmail && password == dummyPassword) {
                Toast.makeText(this, "Bienvenido $email", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Email o contraseña incorrectos. Por favor, revisá e intentá de nuevo.", Toast.LENGTH_LONG).show()
                limpiarCampos()
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun isValidLogin(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Campos incompletos. Revisá e intentá de nuevo.", Toast.LENGTH_SHORT).show()
            limpiarCampos()
            emailInput.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El formato del correo electrónico no es válido. Por favor, ingresá una dirección válida.", Toast.LENGTH_LONG).show()
            limpiarCampos()
            emailInput.requestFocus()
            return false
        }

        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
        if (!passwordPattern.matches(password)) {
            Toast.makeText(this, "La contraseña ingresada no es válida. Debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula y un número.", Toast.LENGTH_LONG).show()
            limpiarCampos()
            emailInput.requestFocus()
            return false
        }

        return true
    }

    private fun limpiarCampos() {
        emailInput.text.clear()
        passwordInput.text.clear()
    }
}
