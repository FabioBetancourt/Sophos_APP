package com.lonquers.challengesophosapp.port


import com.lonquers.challengesophosapp.model.OfficeResponse
import retrofit2.Response
import retrofit2.http.GET

interface APIGetOffices {
    /*
    Get is for getting the endpoint of "RS_Oficinas" from the API and obtain the cities
    suspend fun is for be run inside the coroutine
     */
    @GET("RS_Oficinas")
    suspend fun getCities(): Response<OfficeResponse>
}