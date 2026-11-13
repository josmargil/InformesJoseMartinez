package com.jose_martinez.ra5generarinformes

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private lateinit var spinnerCiudad: Spinner
    private lateinit var botonGenerarInforme: Button
    private lateinit var textoCiudadSeleccionada: TextView
    private lateinit var textoNumeroOfertas: TextView
    private lateinit var textoSalarioTotal: TextView
    private lateinit var textoSalarioMedio: TextView
    private lateinit var textoSalarioMaximo: TextView
    private lateinit var contenedorOfertas: LinearLayout
    private lateinit var barraValencia: ProgressBar
    private lateinit var barraMadrid: ProgressBar
    private lateinit var barraAlicante: ProgressBar

    private lateinit var textoCantidadValencia: TextView
    private lateinit var textoCantidadMadrid: TextView
    private lateinit var textoCantidadAlicante: TextView
    private val ofertas = ArrayList<Oferta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        spinnerCiudad = findViewById(R.id.spinnerCiudad)

        configurarSpinner()

        ofertas.addAll(cargarOfertas())

        botonGenerarInforme = findViewById(R.id.botonGenerarInforme)
        textoCiudadSeleccionada = findViewById(R.id.textoCiudadSeleccionada)
        textoNumeroOfertas = findViewById(R.id.textoNumeroOfertas)
        textoSalarioTotal = findViewById(R.id.textoSalarioTotal)
        textoSalarioMedio = findViewById(R.id.textoSalarioMedio)
        textoSalarioMaximo = findViewById(R.id.textoSalarioMaximo)
        contenedorOfertas = findViewById(R.id.contenedorOfertas)
        barraValencia = findViewById(R.id.barraValencia)
        barraMadrid = findViewById(R.id.barraMadrid)
        barraAlicante = findViewById(R.id.barraAlicante)
        textoCantidadValencia = findViewById(R.id.textoCantidadValencia)
        textoCantidadMadrid = findViewById(R.id.textoCantidadMadrid)
        textoCantidadAlicante = findViewById(R.id.textoCantidadAlicante)


        botonGenerarInforme.setOnClickListener {
            val ciudad = spinnerCiudad.selectedItem.toString()
            val ofertasFiltradas = filtrarOfertasPorCiudad(ciudad)

            mostrarResultado(ciudad, ofertasFiltradas)
        }



        Toast.makeText(this, "Ofertas cargadas: ${ofertas.size}", Toast.LENGTH_LONG).show()
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

    private fun cargarOfertas(): ArrayList<Oferta> {
        val listaOfertas = ArrayList<Oferta>()

        val textoJson = assets.open("ofertas.json")
            .bufferedReader()
            .use { it.readText() }

        val arrayJson = JSONArray(textoJson)

        for (i in 0 until arrayJson.length()) {
            val objetoJson = arrayJson.getJSONObject(i)

            val oferta = Oferta(
                objetoJson.getInt("id"),
                objetoJson.getString("puesto"),
                objetoJson.getString("empresa"),
                objetoJson.getString("ciudad"),
                objetoJson.getInt("salario")
            )

            listaOfertas.add(oferta)
        }

        return listaOfertas
    }


    private fun filtrarOfertasPorCiudad(ciudad: String): List<Oferta> {
        return if (ciudad == "Todas") {
            ofertas
        } else {
            ofertas.filter { it.ciudad == ciudad }
        }
    }


    private fun mostrarResultado(ciudad: String, ofertasFiltradas: List<Oferta>) {

        val numeroOfertas = ofertasFiltradas.size
        val salarioTotal = ofertasFiltradas.sumOf { it.salario }

        val salarioMedio = if (ofertasFiltradas.isNotEmpty()) {
            salarioTotal / numeroOfertas
        } else {
            0
        }

        val salarioMaximo = ofertasFiltradas.maxOfOrNull { it.salario } ?: 0

        textoCiudadSeleccionada.text = "Ciudad seleccionada: $ciudad"
        textoNumeroOfertas.text = "Número de ofertas: $numeroOfertas"
        textoSalarioTotal.text = "Salario total: $salarioTotal €"
        textoSalarioMedio.text = "Salario medio: $salarioMedio €"
        textoSalarioMaximo.text = "Salario máximo: $salarioMaximo €"

        mostrarGrafico(ofertasFiltradas)

        contenedorOfertas.removeAllViews()

        for (i in ofertasFiltradas.indices) {
            val oferta = ofertasFiltradas[i]

            val textoOferta = TextView(this)

            textoOferta.text = "${i + 1}. ${oferta.puesto}\n" +
                    "${oferta.empresa} - ${oferta.ciudad} - ${oferta.salario} €"

            textoOferta.textSize = 16f
            textoOferta.setPadding(0, 0, 0, 24)

            contenedorOfertas.addView(textoOferta)
        }
    }


    private fun mostrarGrafico(ofertasFiltradas: List<Oferta>) {

        val ofertasValencia = ofertasFiltradas.count { it.ciudad == "Valencia" }
        val ofertasMadrid = ofertasFiltradas.count { it.ciudad == "Madrid" }
        val ofertasAlicante = ofertasFiltradas.count { it.ciudad == "Alicante" }

        val valorMaximo = maxOf(ofertasValencia, ofertasMadrid, ofertasAlicante, 1)

        barraValencia.max = valorMaximo
        barraMadrid.max = valorMaximo
        barraAlicante.max = valorMaximo

        barraValencia.progress = ofertasValencia
        barraMadrid.progress = ofertasMadrid
        barraAlicante.progress = ofertasAlicante

        textoCantidadValencia.text = ofertasValencia.toString()
        textoCantidadMadrid.text = ofertasMadrid.toString()
        textoCantidadAlicante.text = ofertasAlicante.toString()
    }


}