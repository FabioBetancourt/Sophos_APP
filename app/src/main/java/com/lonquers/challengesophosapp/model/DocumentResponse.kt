package com.lonquers.challengesophosapp.model

data class DocumentResponse(
    var Items: List<DocumentItems>,
    var Count: Int,
    var ScannedCount: Int
)
