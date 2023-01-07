package com.lonquers.challengesophosapp.port

import com.lonquers.challengesophosapp.model.DocumentResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIGetDocumentsById {
    /*
    Get is for getting the endpoint of "RS_Documents" from the API
    suspend fun is for be run inside the coroutine
    Query is the key for obtain DocumentResponse by id
     */
    @GET("/RS_Documentos")
    suspend fun getDocumentsById(
        @Query("idRegistro") idRegister:String
    ): Response<DocumentResponse>
}