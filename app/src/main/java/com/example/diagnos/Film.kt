package com.example.diagnos

import java.util.*

class Film (
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
