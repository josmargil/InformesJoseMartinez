package com.jose_martinez.ra5generarinformes

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    private lateinit var spinnerCiudad: Spinner
    private lateinit var botonGenerarInforme: Button
    private lateinit var textoCiudadSeleccionada: TextView
    private lateinit var textoNumeroOfertas: TextView
    private lateinit var textoSalarioTotal: TextView
    private lateinit var textoSalarioMedio: TextView
    private lateinit var textoSalarioMaximo: TextView
    private lateinit var textoSalarioMinimo: TextView
    private lateinit var textoMejorOferta: TextView
    private lateinit var contenedorOfertas: LinearLayout
    private lateinit var barraValencia: ProgressBar
    private lateinit var barraMadrid: ProgressBar
    private lateinit var barraAlicante: ProgressBar
    private lateinit var textoCantidadValencia: TextView
    private lateinit var textoCantidadMadrid: TextView
    private lateinit var textoCantidadAlicante: TextView
    private lateinit var botonExportarPdf: Button
    private lateinit var contenedorInforme: LinearLayout


    private val codigoCrearPdf = 100
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
        textoSalarioMinimo = findViewById(R.id.textoSalarioMinimo)
        textoMejorOferta = findViewById(R.id.textoMejorOferta)

        contenedorOfertas = findViewById(R.id.contenedorOfertas)
        barraValencia = findViewById(R.id.barraValencia)
        barraMadrid = findViewById(R.id.barraMadrid)
        barraAlicante = findViewById(R.id.barraAlicante)
        textoCantidadValencia = findViewById(R.id.textoCantidadValencia)
        textoCantidadMadrid = findViewById(R.id.textoCantidadMadrid)
        textoCantidadAlicante = findViewById(R.id.textoCantidadAlicante)
        botonExportarPdf = findViewById(R.id.botonExportarPdf)
        contenedorInforme = findViewById(R.id.contenedorInforme)


        botonGenerarInforme.setOnClickListener {
            val ciudad = spinnerCiudad.selectedItem.toString()
            val ofertasFiltradas = filtrarOfertasPorCiudad(ciudad)

            mostrarResultado(ciudad, ofertasFiltradas)
        }


        botonExportarPdf.setOnClickListener {
            val ciudad = spinnerCiudad.selectedItem.toString()
            val ofertasFiltradas = filtrarOfertasPorCiudad(ciudad)

            mostrarResultado(ciudad, ofertasFiltradas)

            contenedorInforme.post {
                abrirSelectorParaGuardarPdf()
            }
        }
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
        val salarioMinimo = ofertasFiltradas.minOfOrNull { it.salario } ?: 0
        val mejorOferta = ofertasFiltradas.maxByOrNull { it.salario }

        textoCiudadSeleccionada.text = "Ciudad seleccionada: $ciudad"
        textoNumeroOfertas.text = "Número de ofertas: $numeroOfertas"
        textoSalarioTotal.text = "Salario total: $salarioTotal €"
        textoSalarioMedio.text = "Salario medio: $salarioMedio €"
        textoSalarioMaximo.text = "Salario máximo: $salarioMaximo €"
        textoSalarioMinimo.text = "Salario mínimo: $salarioMinimo €"

        if (mejorOferta != null) {
            textoMejorOferta.text = "Mejor oferta: ${mejorOferta.puesto} - ${mejorOferta.empresa} (${mejorOferta.salario} €)"
        } else {
            textoMejorOferta.text = "No hay ofertas disponibles"
        }

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


    private fun abrirSelectorParaGuardarPdf() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_TITLE, "informe_${spinnerCiudad.selectedItem}.pdf")

        startActivityForResult(intent, codigoCrearPdf)
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == codigoCrearPdf && resultCode == RESULT_OK) {
            val uriPdf = data?.data

            if (uriPdf != null) {
                exportarInformePdf(uriPdf)
            }
        }
    }


    private fun exportarInformePdf(uriPdf: Uri) {

        val documentoPdf = PdfDocument()

        val pagina = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val paginaPdf = documentoPdf.startPage(pagina)

        val canvas = paginaPdf.canvas

        val pinturaTitulo = Paint()
        pinturaTitulo.color = Color.BLACK
        pinturaTitulo.textSize = 24f
        pinturaTitulo.isFakeBoldText = true

        canvas.drawColor(Color.WHITE)
        canvas.drawText("Informe de ofertas", 32f, 48f, pinturaTitulo)

        val anchoDisponible = 531f
        val altoDisponible = 760f

        val anchoInforme = contenedorInforme.width
        val altoInforme = contenedorInforme.height

        if (anchoInforme > 0 && altoInforme > 0) {
            val escala = min(
                anchoDisponible / anchoInforme,
                altoDisponible / altoInforme
            )

            canvas.save()
            canvas.translate(32f, 72f)
            canvas.scale(escala, escala)

            contenedorInforme.draw(canvas)

            canvas.restore()
        }

        documentoPdf.finishPage(paginaPdf)

        try {
            val salida = contentResolver.openOutputStream(uriPdf)

            if (salida != null) {
                salida.use {
                    documentoPdf.writeTo(it)
                }

                Toast.makeText(this, "PDF guardado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No se pudo guardar el PDF", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar el PDF", Toast.LENGTH_LONG).show()

        } finally {
            documentoPdf.close()
        }
    }



}