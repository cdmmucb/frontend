package com.example.diagnos

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.lang.Boolean.FALSE
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

lateinit var listPrimerCarrito: MutableList<Film>
private lateinit var tvTotal: TextView
private lateinit var tvDescuento: TextView
lateinit var auxiPrimerCarritoList: MutableList<Film>

var totalAux: Float = 0.0F
var descuentoAux: Float = 0.0F
var daysBetween = 0


class PrimerCarrito : AppCompatActivity() {
    private lateinit var buttonAtras: Button
    private lateinit var buttonSiguiente: Button
    private lateinit var buttonSesion: Button
    private lateinit var datePicker: DatePicker
    lateinit var adapterPrimerCarrito: CustomAdapter
    lateinit var mensaje: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_primer_carrito)

        mensaje = findViewById(R.id.mensajeFecha)
        datePicker = findViewById(R.id.datePicker)
        tvTotal = findViewById(R.id.tvTotal)
        tvDescuento = findViewById(R.id.tvDescuento)
        buttonAtras = findViewById(R.id.btnAtrasPrimer)
        buttonSesion = findViewById(R.id.btnLoginPrimer)
        buttonSiguiente = findViewById(R.id.btnAlFinal)

        listPrimerCarrito = mutableListOf<Film>()

        val values = getSharedPreferences("values", MODE_PRIVATE)
        if(values.getInt("userId",-1).equals(-1)){
            //DO NOTHING
        }else{
            buttonSesion.text="Cerrar Sesión"
        }
        listPrimerCarrito.clear()
        for (i in 1..4) {
            if (values.getInt("inventoryId" + i, -1).equals(-1)) {
                //DO NOTHING
            } else {
                listPrimerCarrito.add(
                    Film(
                        values.getInt("inventoryId" + i, -1),
                        values.getBoolean("disponible" + i, FALSE),
                        values.getInt("id" + i, -1),
                        values.getString("title" + i, "f").toString(),
                        values.getString("description" + i, "f"),
                    )
                )
            }
        }

        var seleccionado = Calendar.getInstance()
        var calendar = Calendar.getInstance()
        seleccionado.set(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH),0,0)
        calendar.set(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH),0,0)

        datePicker.init(
            calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        ) { datePicker, year, month, dayOfMonth ->
            Log.d(
                "Date",
                "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth
            )
            seleccionado.set(year, month, dayOfMonth, 0, 0);

            var days = daysBetween(calendar, seleccionado)

            if (seleccionado.after(calendar)) {
                buttonSiguiente.visibility = View.VISIBLE
                tvTotal.visibility = View.VISIBLE
                tvDescuento.visibility = View.VISIBLE
                mensaje.visibility = View.GONE

                totalAux = days * listPrimerCarrito.size * 0.99F
                if (totalAux > 20) {
                    descuentoAux = 0.2F
                } else {
                    if (totalAux > 15) {
                        descuentoAux = 0.15F
                    } else {
                        if (totalAux > 10) {
                            descuentoAux = 0.1F
                        } else {
                            descuentoAux = 0.0F
                        }
                    }
                }
                totalAux = totalAux * (1.0F - descuentoAux)
                tvTotal.text = "Total: " + totalAux.toString() + " USD"
                tvDescuento.text = "DSCTO: " + descuentoAux.toString() + " USD"

            } else {
                buttonSiguiente.visibility = View.GONE
                tvTotal.visibility = View.GONE
                tvDescuento.visibility = View.GONE
                mensaje.visibility = View.VISIBLE
            }
        }

        buttonAtras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonSesion.setOnClickListener {
            if (buttonSesion.text.equals("Iniciar Sesión")) {
                showDialogLoginPrimerCarrito(buttonSesion)
            } else {
                val editor = values.edit()
                editor.putInt("userId", -1)
                editor.putString("nombres", "nada")
                editor.putString("apellidos", "nada")
                editor.putString("correo", "nada")
                editor.commit()
                buttonSesion.text = "Iniciar Sesión"
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSiguiente.setOnClickListener {
            if (mensaje.visibility == View.VISIBLE) {
                Toast.makeText(this, "No puede alquilar películas menos de un día", Toast.LENGTH_SHORT).show()
            } else {
                if (listPrimerCarrito.size == 0) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(applicationContext, "Debe agregar al menos una película", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val values = getSharedPreferences("values", MODE_PRIVATE)
                    val pais: String? = values.getString("pais", "default")
                    if (values.getInt("userId",-1).equals(-1)) {
                        showDialogLoginPrimerCarrito(buttonSesion)
                    } else {
                        val date: Date = calendar.getTime()
                        val format1 = SimpleDateFormat("yyyy-MM-dd")
                        var inActiveDate: String? = null
                        try {
                            inActiveDate = format1.format(date)
                            println(inActiveDate)
                        } catch (e1: ParseException) {
                            e1.printStackTrace()
                        }
                        val editor = values.edit()
                        editor.putFloat("total", totalAux)
                        editor.putFloat("descuento", descuentoAux)
                        editor.putString("rentalDate", inActiveDate)
                        editor.commit()
                        val intent = Intent(this, CarritoFinal::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        val RecyclerViewPrimerCarrito: RecyclerView = findViewById(R.id.recyclerViewPrimerCarrito)
        val layoutManagerPrimerCarrito = LinearLayoutManager(this)
        RecyclerViewPrimerCarrito.layoutManager = layoutManagerPrimerCarrito

        var mDividerItemDecorationPrimerCarrito = DividerItemDecoration(
            RecyclerViewPrimerCarrito.getContext(),
            layoutManagerPrimerCarrito.getOrientation()
        )
        RecyclerViewPrimerCarrito.addItemDecoration(mDividerItemDecorationPrimerCarrito)

        adapterPrimerCarrito = CustomAdapter(listPrimerCarrito)
        RecyclerViewPrimerCarrito.adapter = adapterPrimerCarrito
    }

    class CustomAdapter(var dataSet: MutableList<Film>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var titulo: TextView
            var imagen: ImageView
            val quitar: Button
            var ocultoId: TextView
            var ocultoDescripcion: TextView

            init {
                titulo = view.findViewById(R.id.filmTitulo)
                imagen = view.findViewById(R.id.filmImagePrimer)
                quitar = view.findViewById(R.id.buttonQuitarPrimer)
                ocultoId = view.findViewById(R.id.filmId)
                ocultoDescripcion = view.findViewById(R.id.filmDescripcion)
            }

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            var view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_item_primer, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.titulo.text = dataSet[position].title
            viewHolder.ocultoId.setText(dataSet[position].inventoryId.toString())
            viewHolder.ocultoDescripcion.setText(dataSet[position].description)

            viewHolder.ocultoId.visibility = View.GONE

            viewHolder.quitar.setOnClickListener {
                auxiPrimerCarritoList = mutableListOf<Film>()
                auxiPrimerCarritoList.clear()
                listPrimerCarrito.forEachIndexed { index, element ->
                    if (element.inventoryId != Integer.parseInt(viewHolder.ocultoId.text.toString())) {
                        auxiPrimerCarritoList.add(element)
                    }else{
                            var YOLO = viewHolder.itemView.context as PrimerCarrito
                            YOLO.removeFilm(element.inventoryId)
                    }
                }
                listPrimerCarrito.clear()
                listPrimerCarrito.addAll(auxiPrimerCarritoList)
                notifyDataSetChanged()

                totalAux = daysBetween * listPrimerCarrito.size * 0.99F
                if (totalAux > 20) {
                    descuentoAux = 0.2F
                } else {
                    if (totalAux > 15) {
                        descuentoAux = 0.15F
                    } else {
                        if (totalAux > 10) {
                            descuentoAux = 0.1F
                        } else {
                            descuentoAux = 0.0F
                        }
                    }
                }
                totalAux = totalAux * (1.0F - descuentoAux)
                tvTotal.text = "Total: " + totalAux.toString() + " USD"
                tvDescuento.text = "DSCTO: " + descuentoAux.toString() + " USD"
            }
        }

        override fun getItemCount() = dataSet.size
    }

    fun showDialogLoginPrimerCarrito(buttonSesion: Button) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_login)

        var etUser: EditText = dialog.findViewById(R.id.etUserLogin)
        var etPassword: EditText = dialog.findViewById(R.id.etPasswordLogin)
        val btnConfirmLogin: Button = dialog.findViewById(R.id.buttonConfirmLogin)
        val btnCancelLogin: Button = dialog.findViewById(R.id.buttonCancelLogin)

        btnConfirmLogin.setOnClickListener {
            dialog.dismiss()
            val jsonOblectref2logiPrimer: JsonObjectRequest = object : JsonObjectRequest(
                Method.GET, "http://192.168.1.2:8080/users/" + etUser.text.toString(), null,
                Response.Listener { response ->
                    val auxUsuario = response
                    if (auxUsuario.isNull("customerId")) {
                        Toast.makeText(this, "No se pudo iniciar sesión. Intente nuevamente", Toast.LENGTH_SHORT).show()
                    } else {
                        val values = getSharedPreferences("values", MODE_PRIVATE)
                        val editor = values.edit()
                        editor.putInt("userId", auxUsuario.getInt("customerId"))
                        editor.putString("nombres", auxUsuario.getString("firstName"))
                        editor.putString("apellidos", auxUsuario.getString("lastName"))
                        editor.putString("correo", auxUsuario.getString("email"))
                        editor.commit()
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        buttonSesion.text="Cerrar Sesión"
                    }
                },
                Response.ErrorListener {
                    val toast = Toast.makeText(this, "Error", Toast.LENGTH_SHORT)
                    toast.show()
                }) {
                //This is for Headers If You Needed
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/json"
                    return params
                }
            }
            val queue2ref2logiPrimer = Volley.newRequestQueue(it.context)
            queue2ref2logiPrimer.add(jsonOblectref2logiPrimer)
        }

        btnCancelLogin.setOnClickListener {
            dialog.dismiss()
            val toast = Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT)
            toast.show()
        }
        dialog.show()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun removeFilm(inventoryId: Int) {
        val values = getSharedPreferences("values", MODE_PRIVATE)
        val editor = values.edit()
        for (i in 1..4) {
            if (values.getInt("inventoryId" + i, -1).equals(inventoryId)) {
                editor.putInt("inventoryId" + i, -1)
                editor.putBoolean("disponible" + i, FALSE)
                editor.putInt("id" + i, -1)
                editor.putString("title" + i, "f")
                editor.putString("description" + i, "f")
            }
        }
        editor.commit()
    }

    fun daysBetween(startDate: Calendar, endDate: Calendar): Long {
        val newStart = Calendar.getInstance()
        newStart.timeInMillis = startDate.timeInMillis
        newStart[Calendar.HOUR_OF_DAY] = 0
        newStart[Calendar.MINUTE] = 0
        newStart[Calendar.SECOND] = 0
        newStart[Calendar.MILLISECOND] = 0
        val newEnd = Calendar.getInstance()
        newEnd.timeInMillis = endDate.timeInMillis
        newEnd[Calendar.HOUR_OF_DAY] = 0
        newEnd[Calendar.MINUTE] = 0
        newEnd[Calendar.SECOND] = 0
        newEnd[Calendar.MILLISECOND] = 0
        val end = newEnd.timeInMillis
        val start = newStart.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start))
    }
}