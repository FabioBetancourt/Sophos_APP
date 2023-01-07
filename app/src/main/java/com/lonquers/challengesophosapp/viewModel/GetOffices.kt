package com.lonquers.challengesophosapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonquers.challengesophosapp.model.OfficeItems
import com.lonquers.challengesophosapp.port.APIGetOffices
import com.lonquers.challengesophosapp.model.OfficeResponse
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetOffices : ViewModel() {

    init {
        getOffices()
    }

    val citiesLiveData = MutableLiveData<List<OfficeItems>>()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getOffices (){
        viewModelScope.launch {
            val response: Response<OfficeResponse> = getRetrofit().create(APIGetOffices::class.java)
                .getCities()
            val citiesInfo = response.body()
            citiesLiveData.postValue(citiesInfo?.Items)
            viewModelScope.cancel()

        }
    }
}