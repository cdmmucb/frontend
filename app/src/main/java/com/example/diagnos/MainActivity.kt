package com.example.diagnos

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.util.*

lateinit var Estado: TextView
lateinit var carrito: MutableList<Film>
class MainActivity : AppCompatActivity() {

    var radioGroup: RadioGroup? = null
    lateinit var radioButton: RadioButton
    private lateinit var buttonBusqueda: Button
    lateinit var etBusquedaQ: EditText
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    val url = "http://192.168.1.2:8080/rentals"
    var list = mutableListOf<Film>()

    lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var context: Context = this
        Estado = findViewById(R.id.textEstado)
        Estado.visibility=View.GONE
        carrito = mutableListOf()

        val values = getSharedPreferences("values", MODE_PRIVATE)
        val pais: String? = values.getString("pais","default")
        if(values.contains("userid")){
            //do nothing
        }else{
            val editor = values.edit()
            editor.putInt("userid", -1 )
            editor.putString("nombres", "nada" )
            editor.putString("apellidos", "nada" )
            editor.putString("correo", "nada" )
            editor.commit()
        }

        etBusquedaQ = findViewById(R.id.etBusquedaQ)
        radioGroup = findViewById(R.id.radioGroup1)
        buttonBusqueda = findViewById(R.id.submitButton)
        buttonBusqueda.setOnClickListener {
            list.clear()
            val selectedOption: Int = radioGroup!!.checkedRadioButtonId
            radioButton = findViewById(selectedOption)
            Toast.makeText(baseContext, radioButton.text, Toast.LENGTH_SHORT).show()
            var auxiur: String? = null
            if(radioButton.text.toString().equals("Actor")){
                auxiur=url+"/"+pais+"/actor/"+etBusquedaQ.text.toString()
            }else{
                auxiur=url+"/"+pais+"/titulo/"+etBusquedaQ.text.toString()
            }
            val requestInicial: JsonArrayRequest = object : JsonArrayRequest(
                Method.GET, auxiur,null,
                Response.Listener { response: JSONArray ->
                    try {
                        for (i in 0 until response.length()) {
                            val film = response.getJSONObject(i)
                            list.add(Film(
                                film.getInt("inventoryId"),
                                film.getBoolean("disponible"),
                                film.getInt("filmId"),
                                film.getString("title"),
                                film.getString("description")))
                        }
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "dice", Toast.LENGTH_SHORT).show()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this, "exception", Toast.LENGTH_SHORT).show()
                    }
                    if(list.size>0){
                        Estado.visibility=View.GONE
                    }else{
                        Estado.visibility=View.VISIBLE
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                    Log.e("error is ", "" + error) }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/json"
                    return params
                }
            }
            val queueInicial = Volley.newRequestQueue(applicationContext)
            queueInicial.add(requestInicial)
///////////////////////////////////////////////////////////////////////////////////////////////////
        }

        val RecyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        RecyclerView.layoutManager = layoutManager

        var mDividerItemDecoration = DividerItemDecoration(
            RecyclerView.getContext(),
            layoutManager.getOrientation()
        )
        RecyclerView.addItemDecoration(mDividerItemDecoration)

        adapter = CustomAdapter(list)
        RecyclerView.adapter = adapter
        val btnCarrito: Button = findViewById(R.id.btnCarrito)
        btnCarrito.setOnClickListener {
            if(carrito.size==0) {
                Toast.makeText(this, "El carrito no puede estar vacio", Toast.LENGTH_SHORT).show()
            }else{
            val values = getSharedPreferences("values", MODE_PRIVATE)
            val editor = values.edit()
            for (i in 1..4) {
                if (carrito.size>=i) {
                    editor.putInt("inventoryId" + i, carrito.get(i - 1).inventoryId)
                    editor.putBoolean("disponible" + i, carrito.get(i - 1).disponible)
                    editor.putInt("id" + i, carrito.get(i - 1).filmId)
                    editor.putString("title" + i, carrito.get(i - 1).title)
                    editor.putString("description" + i, carrito.get(i - 1).description)
                } else {
                    editor.putInt("inventoryId" + i, -1)
                    editor.putBoolean("disponible" + i, FALSE)
                    editor.putInt("id" + i, -1)
                    editor.putString("title" + i, "f")
                    editor.putString("description" + i, "f")
                }
            }
            editor.commit()
            val intent = Intent(this, PrimerCarrito::class.java)
            startActivity(intent)
            finish()
        }
        }

        val btnLogin: Button = findViewById(R.id.btnLoginMain)
        btnLogin.setOnClickListener {
            showDialogLogin()
        }

        val request: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, url+"/"+pais,null,
            Response.Listener { response: JSONArray ->
                try {
                    for (i in 0 until response.length()) {
                        val film = response.getJSONObject(i)
                        list.add(Film(
                            film.getInt("inventoryId"),
                            film.getBoolean("disponible"),
                            film.getInt("filmId"),
                            film.getString("title"),
                            film.getString("description")))
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if(list.size>0){
                    Estado.visibility=View.GONE
                }else{
                    Estado.visibility=View.VISIBLE
                }
            },
            Response.ErrorListener { error -> Log.e("error is ", "" + error) }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json"
                return params
            }
        }
        val queue = Volley.newRequestQueue(applicationContext)
        request.setRetryPolicy(
            DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        queue.add(request)
///////////////////////////////////////////////////////////////////////////////////////////////////
    }

    fun showDialogLogin() {
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

        ///////////////////////////////////////////////////////////////////
        val jsonOblectref2logi: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "http://192.168.1.2:8080/users/"+etUser.text.toString(), null,
            Response.Listener { response ->
                val auxUsuario = response
                System.out.println(auxUsuario)
                if(auxUsuario.isNull("customerId")){
                    Toast.makeText(
                        this,"No se pudo iniciar sesión. Intente nuevamente", Toast.LENGTH_SHORT).show()
                }else {
                    val values = getSharedPreferences("values", MODE_PRIVATE)
                    val editor = values.edit()
                    editor.putInt("userId", auxUsuario.getInt("customerId"))
                    editor.putString("nombres", auxUsuario.getString("firstName"))
                    editor.putString("apellidos", auxUsuario.getString("lastName"))
                    editor.putString("correo", auxUsuario.getString("email"))
                    editor.commit()
                }
                              },
            Response.ErrorListener { error -> Toast.makeText(this, "Error"+error, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json"
                return params
            }
        }
        val queue2ref2logi = Volley.newRequestQueue(it.context)
        jsonOblectref2logi.setRetryPolicy(
            DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        queue2ref2logi.add(jsonOblectref2logi)
////////////////////////////////////////////////////////////////////////////
    }
    btnCancelLogin.setOnClickListener {
        dialog.dismiss()
        val toast = Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT)
        toast.show()
    }
    dialog.show()
}

    class CustomAdapter(var dataSet: MutableList<Film>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var titulo: TextView
            var disponible: TextView
            var imagen: ImageView
            val agregar: Button
            val agotado: TextView
            val quitar: Button
            var ocultoId: TextView
            var ocultoDescripcion: TextView

            init {
                // Define click listener for the ViewHolder's View.
                titulo = view.findViewById(R.id.itemTitulo)
                disponible = view.findViewById(R.id.textDisponible)
                imagen = view.findViewById(R.id.filmImage)
                agregar = view.findViewById(R.id.buttonAgregar)
                agotado = view.findViewById(R.id.textAgotado)
                quitar = view.findViewById(R.id.buttonQuitar)
                ocultoId = view.findViewById(R.id.ocultoId)
                ocultoDescripcion = view.findViewById(R.id.ocultoDescripcion)
                ocultoId.visibility=View.GONE
                ocultoDescripcion.visibility=View.GONE
                agotado.visibility = View.GONE
                quitar.visibility = View.GONE

                agregar.setOnClickListener {
                    if(carrito.size==4){
                        val toastNo = Toast.makeText(view.context, "Adicion cancelada. No puede alquilar mas de 4 peliculas a la vez", Toast.LENGTH_SHORT)
                        toastNo.show()
                    }else{
                        quitar.visibility = View.VISIBLE
                        agregar.visibility = View.GONE
                        carrito.add(Film(Integer.parseInt(ocultoId.text.toString()),TRUE,Integer.parseInt(ocultoId.text.toString()),titulo.text.toString(),ocultoDescripcion.text.toString()))
                    }
                }

                quitar.setOnClickListener {
                    quitar.visibility = View.GONE
                    agregar.visibility = View.VISIBLE
                    for (i in carrito.indices) {
                        if(carrito[i].inventoryId == Integer.parseInt(ocultoId.text.toString())){
                            carrito.removeAt(i);
                        }
                    }
                }

            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            var view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_item, viewGroup, false)
            return ViewHolder(view)
        }


        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.disponible.text = dataSet[position].disponible.toString()
            viewHolder.titulo.text = dataSet[position].title
            viewHolder.ocultoId.setText(dataSet[position].inventoryId.toString())
            viewHolder.ocultoDescripcion.setText(dataSet[position].description)
            viewHolder.ocultoDescripcion.visibility = View.GONE
            viewHolder.ocultoId.visibility = View.GONE
            viewHolder.quitar.visibility = View.GONE
            viewHolder.disponible.visibility = View.GONE
            if (viewHolder.disponible.text.toString().equals("0")){
                viewHolder.agregar.visibility = View.GONE
                viewHolder.agotado.visibility = View.VISIBLE
            }else{
                viewHolder.agregar.visibility = View.VISIBLE
                viewHolder.agotado.visibility = View.GONE
            }
        }

        override fun getItemCount() = dataSet.size

    }
}