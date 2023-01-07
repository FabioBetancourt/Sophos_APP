package com.lonquers.challengesophosapp.port

import com.lonquers.challengesophosapp.model.SignInResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface APISignIn {
    /*
    Get is for getting the endpoint of "RS_Usuarios" from the API
    suspend fun is for be run inside the coroutine
    Query is the key for obtain the user's information and login with the app by "idUsuario" and
    "clave" and obtain a list of type SignInResponse
     */
    @GET("/RS_Usuarios")
    suspend fun getSignIn(
        @Query("idUsuario") userID: String,
        @Query("clave") password: String
    ): Response<SignInResponse>


}