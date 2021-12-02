package com.example.diagnos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Boolean.FALSE

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val canada: Button = findViewById(R.id.btnCanada)
        val australia: Button = findViewById(R.id.btnAustralia)

        val values = getSharedPreferences("values", MODE_PRIVATE)
        val editor = values.edit()
        editor.putInt("userId",-1)
        editor.putString("nombres", "nada")
        editor.putString("apellidos", "nada")
        editor.putString("correo", "nada")
        for(i in 1..4){
            editor.putInt("inventoryId"+i,-1)
            editor.putBoolean("disponible"+i,FALSE)
            editor.putInt("id"+i,-1)
            editor.putString("title"+i, "f")
            editor.putString("description"+i, "f")
        }
        editor.putFloat("total",0.0F)
        editor.putFloat("descuento",0.0F)
        editor.putString("rentalDate","default")

        canada.setOnClickListener {
            editor.putString("pais", "canada")
            editor.commit()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        australia.setOnClickListener {
            editor.putString("pais", "australia")
            editor.commit()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}