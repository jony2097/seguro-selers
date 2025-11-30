package com.example.selersmobile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class AccidenteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accidente)

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val cardBicicleta = findViewById<LinearLayout>(R.id.cardBicicleta)
        val cardMoto = findViewById<LinearLayout>(R.id.cardMoto)
        val cardMonopatin = findViewById<LinearLayout>(R.id.cardMonopatin)

        cardBicicleta.setOnClickListener {
            val intent = Intent(this, AccidenteDatosActivity::class.java)
            intent.putExtra("vehiculo", "bicicleta")
            startActivity(intent)
        }

        cardMoto.setOnClickListener {
            val intent = Intent(this, AccidenteDatosActivity::class.java)
            intent.putExtra("vehiculo", "moto")
            startActivity(intent)
        }

        cardMonopatin.setOnClickListener {
            val intent = Intent(this, AccidenteDatosActivity::class.java)
            intent.putExtra("vehiculo", "monopatin")
            startActivity(intent)
        }


    }
}
