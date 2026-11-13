package com.jose_martinez.ra5generarinformes

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    */
    private lateinit var spinnerCiudad: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        spinnerCiudad = findViewById(R.id.spinnerCiudad)

        configurarSpinner()
    }

    private fun configurarSpinner() {
        val ciudades = arrayOf("Todas", "Valencia", "Madrid", "Alicante")

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ciudades
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCiudad.adapter = adaptador
    }
}