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


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val canada: Button = findViewById(R.id.btnCanada)
        val australia: Button = findViewById(R.id.btnAustralia)
        canada.setOnClickListener {
                    val values = getSharedPreferences("values", MODE_PRIVATE)
                    val editor = values.edit()
                    editor.putString("pais", "canada")
                    editor.commit()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
            }

        australia.setOnClickListener {
            val values = getSharedPreferences("values", MODE_PRIVATE)
            val editor = values.edit()
            editor.putString("pais", "australia")
            editor.commit()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        }

    }