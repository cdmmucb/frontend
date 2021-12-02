package com.example.diagnos

import java.util.*

class Film(
    var inventoryId: Int,
    var disponible: Boolean,
    var filmId: Int,
    var title: String,
    var description: String? = null,
    var releaseYear: Short? = null,
    var language: String? = null,
    var originalLanguage: String? = null,
    var length: Int? = null,
    var lengthLabel: String? = null,
    var rating: String? = null,
    var specialFeatures: String? = null,
    var actors: List<String>? = null,
    var category: String? = null,
    var lastUpdate: Date? = null
)

/*values
String pais canada,australia,default

Int userId -1
String nombres nada
String apellidos nada
String correo nada

btnLogin.text = "Iniciar Sesión,Cerrar Sesión

Int inventoryIdi -1
Boolean disponiblei FALSE
Int idi -1
String titlei f
String descriptioni f

Float total 0.0F
Float descuento 0.0F
String rentalDate default
*/