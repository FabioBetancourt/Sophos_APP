package com.lonquers.challengesophosapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonquers.challengesophosapp.model.DocumentItems
import com.lonquers.challengesophosapp.port.APIGetDocuments
import com.lonquers.challengesophosapp.model.DocumentResponse
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetDocuments : ViewModel(){

    var getDocumentsLiveData = MutableLiveData<List<DocumentItems>>()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getDocumentsOnList (emailSignIn:String) {
        viewModelScope.launch {
            val response : Response<DocumentResponse> = getRetrofit().create(APIGetDocuments::class.java)
                .getDocuments(emailSignIn)
            val documentsInfo = response.body()
            getDocumentsLiveData.postValue(documentsInfo?.Items)

            viewModelScope.cancel()

        }
    }
}