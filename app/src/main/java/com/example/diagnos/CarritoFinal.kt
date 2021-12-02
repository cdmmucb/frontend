package com.example.diagnos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Boolean

class CarritoFinal : AppCompatActivity() {
    lateinit var listCarritoFinal: MutableList<Film>
    private lateinit var buttonCancelarFinal: Button
    private lateinit var buttonConfirmarFinal: Button
    private lateinit var tvTotalFinal: TextView
    private lateinit var tvDescuentoFinal: TextView
    lateinit var spinnerFinal: Spinner
    lateinit var adapterCarritoFinal: CustomAdapter
    lateinit var textSpinnerFinal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito_final)

        listCarritoFinal = mutableListOf<Film>()
        val values = getSharedPreferences("values", MODE_PRIVATE)
        val paisCarritoFinal: String? = values.getString("pais", "default")
        val totalFin: Float = values.getFloat("total", 0.0F)
        val descuentoFin: Float = values.getFloat("descuento", 0.0F)
        val rentalDate: String? = values.getString("rentalDate","default")
        val userId: Int = values.getInt("userId",-1)
        for (i in 1..4) {
            if (values.getInt("id" + i, -1).equals(-1)) {
                //DO NOTHING
            } else {
                listCarritoFinal.add(
                    Film(
                        values.getInt("inventoryId" + i, -1),
                        values.getBoolean("disponible" + i, Boolean.FALSE),
                        values.getInt("id" + i, -1),
                        values.getString("title" + i, "default").toString(),
                        values.getString("description" + i, "default"),
                    )
                )
            }
        }

        tvTotalFinal = findViewById(R.id.tvTotalFinal)
        tvDescuentoFinal = findViewById(R.id.tvDescuentoFinal)
        tvTotalFinal.text="Total: "+totalFin.toString()+" USD"
        tvDescuentoFinal.text="Descuento: "+descuentoFin.toString()+" USD"

        buttonConfirmarFinal = findViewById(R.id.btnConfirmarFinal)
        buttonCancelarFinal = findViewById(R.id.btnCancelarFinal)

        spinnerFinal = findViewById(R.id.spinnerFinal)
        val arraySpinner = arrayOf(
            "1", "2", "3", "4", "5", "6", "7"
        )
        val adapterSpinnerFinal = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, arraySpinner
        )
        adapterSpinnerFinal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFinal.adapter = adapterSpinnerFinal
        textSpinnerFinal = spinnerFinal.getSelectedItem().toString()

        buttonCancelarFinal.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonConfirmarFinal.setOnClickListener {
            textSpinnerFinal = spinnerFinal.getSelectedItem().toString()
            if (textSpinnerFinal.equals("")) {
                Toast.makeText(this, "Debe seleccionar una dirección de envio", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (listCarritoFinal.size<1){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(applicationContext, "El carrito esta vacio", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }else{

                val rootObjectCarritoFinal = JSONObject()
                rootObjectCarritoFinal.put("rentalDate", rentalDate)
                rootObjectCarritoFinal.put("customerId", userId)
                rootObjectCarritoFinal.put("country", paisCarritoFinal)
                rootObjectCarritoFinal.put("paymentDate", rentalDate)
                rootObjectCarritoFinal.put("amount", totalFin / listCarritoFinal.size)
                if (listCarritoFinal.size > 3) {
                    rootObjectCarritoFinal.put("film4", listCarritoFinal.get(3).filmId)
                } else {
                    rootObjectCarritoFinal.put("film4", -1)
                }
                if (listCarritoFinal.size > 2) {
                    rootObjectCarritoFinal.put("film3", listCarritoFinal.get(2).filmId)
                } else {
                    rootObjectCarritoFinal.put("film3", -1)
                }
                if (listCarritoFinal.size > 1) {
                    rootObjectCarritoFinal.put("film2", listCarritoFinal.get(1).filmId)
                } else {
                    rootObjectCarritoFinal.put("film2", -1)
                }
                if (listCarritoFinal.size > 0) {
                    rootObjectCarritoFinal.put("film1", listCarritoFinal.get(0).filmId)
                } else {
                    rootObjectCarritoFinal.put("film1", -1)
                }

                ///////////////////////////////////////////////////////////////////
                val jsonOblectCarritoFinal: JsonObjectRequest = object : JsonObjectRequest(
                    Method.POST, "http://192.168.1.2:8080/rentals", rootObjectCarritoFinal,
                    Response.Listener { response ->// Get your json response and convert it to whatever you want.
                        Toast.makeText(
                            applicationContext,
                            "Alquiler registrado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                    },
                    Response.ErrorListener {error->
                        Toast.makeText(this, "Error. Intente más tarde", Toast.LENGTH_SHORT).show()
                        Log.d("taggggg",error.toString())
                        //Toast.makeText(this, "Una de sus películas ya no esta disponible. Verifique su carrito si desea alquilar las otras", Toast.LENGTH_LONG).show()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Content-Type"] = "application/json"
                        return params
                    }
                }
                val queue2CarritoFinal = Volley.newRequestQueue(applicationContext)
                queue2CarritoFinal.add(jsonOblectCarritoFinal)
                ///////////////////////////////////////////////////////////////////
            }
        }
        }

        val RecyclerViewCarritoFinal: RecyclerView = findViewById(R.id.recyclerViewFinal)
        val layoutManagerCarritoFinal = LinearLayoutManager(this)
        RecyclerViewCarritoFinal.layoutManager = layoutManagerCarritoFinal

        var mDividerItemDecorationCarritoFinal = DividerItemDecoration(
            RecyclerViewCarritoFinal.getContext(),
            layoutManagerCarritoFinal.getOrientation()
        )
        RecyclerViewCarritoFinal.addItemDecoration(mDividerItemDecorationCarritoFinal)

        adapterCarritoFinal = CustomAdapter(listCarritoFinal)
        RecyclerViewCarritoFinal.adapter = adapterCarritoFinal
    }

    class CustomAdapter(var dataSet: MutableList<Film>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var titulo: TextView
            var imagen: ImageView
            var ocultoId: TextView
            var ocultoDescripcion: TextView

            init {
                // Define click listener for the ViewHolder's View.
                titulo = view.findViewById(R.id.filmTituloFinal)
                imagen = view.findViewById(R.id.filmImageFinal)
                ocultoId = view.findViewById(R.id.filmIdFinal)
                ocultoDescripcion = view.findViewById(R.id.filmDescripcionFinal)
                ocultoId.visibility= View.GONE
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            var view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_item_final, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.titulo.text = dataSet[position].title
            viewHolder.ocultoId.setText(dataSet[position].filmId.toString())
            viewHolder.ocultoDescripcion.setText(dataSet[position].description)
            viewHolder.ocultoId.visibility = View.GONE
        }
        
        override fun getItemCount() = dataSet.size
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}