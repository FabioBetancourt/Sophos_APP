package com.lonquers.challengesophosapp.port

import com.lonquers.challengesophosapp.model.DocumentItemsPost
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiPostDocument {
    /*
    POST is to sending the documentation to "RS_Documents" to the API
    suspend fun is for be run inside the coroutine
    Body is to specify the type of structure to send to the api in this case of type DocumentItemsPost
     */
    @POST("/RS_Documentos")
    suspend fun postDocument(
        @Body docInfo : DocumentItemsPost
    )
}