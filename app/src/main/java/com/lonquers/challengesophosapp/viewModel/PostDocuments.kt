package com.lonquers.challengesophosapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonquers.challengesophosapp.port.ApiPostDocument
import com.lonquers.challengesophosapp.model.*
import com.lonquers.challengesophosapp.port.APIGetOffices
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostDocuments : ViewModel() {

    init {
        getCities()
    }
    var cityLiveData = MutableLiveData<MutableList<String>>()
    var documentModel = MutableLiveData<DocumentItems>()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun sendDocument(DocumentInput: DocumentItemsPost) {
        viewModelScope.launch {
            val response = getRetrofit().create(ApiPostDocument::class.java)
                .postDocument(DocumentInput)
            println("Server response $response")
        }
    }

    private fun getCities() {
        viewModelScope.launch {
            val response = getRetrofit().create(APIGetOffices::class.java)
                .getCities()

            val cities = response.body()?.Items
            if (cities != null) {
                val citySet = mutableSetOf<String>()
                for (city in cities.indices) {
                    citySet.add(cities[city].Ciudad)
                }
                cityLiveData.postValue(citySet.toMutableList())
            }
        }
    }
}