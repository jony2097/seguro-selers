package com.example.selersmobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home) // Confirmá que este es tu layout principal

        // Vincular las cards
        val cardMoto = findViewById<LinearLayout>(R.id.card_moto)
        val cardBici = findViewById<LinearLayout>(R.id.card_bici)
        val cardMonopatin = findViewById<LinearLayout>(R.id.card_monopatin)

        cardMoto.setOnClickListener {
            startActivity(Intent(this, MotoActivity::class.java))
        }

        cardBici.setOnClickListener {
            startActivity(Intent(this, BiciActivity::class.java))
        }

        cardMonopatin.setOnClickListener {
            startActivity(Intent(this, MonopatinEActivity::class.java))
        }

        // Vincular el BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Listener del menú inferior
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Ya estás en home, opcional: recargar o no hacer nada
                    true
                }
                R.id.nav_request -> {
                    startActivity(Intent(this, SeguimientoActivity::class.java))
                    true
                }
                R.id.nav_incident -> {
                    startActivity(Intent(this, AccidenteActivity::class.java))
                    true
                }
                R.id.nav_insurance -> {
                    startActivity(Intent(this, MiSeguroActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
