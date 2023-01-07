package com.lonquers.challengesophosapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonquers.challengesophosapp.model.DocumentItems
import com.lonquers.challengesophosapp.port.APIGetDocumentsById
import com.lonquers.challengesophosapp.model.DocumentResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetDocumentsById: ViewModel() {

    val getDocumentsMutableLiveData = MutableLiveData<List<DocumentItems>>()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getDocuments (idDocument:String) {
        viewModelScope.launch {
            val response : Response<DocumentResponse> = getRetrofit().create(APIGetDocumentsById::class.java)
                .getDocumentsById(idDocument    )
            val docsInfo = response.body()
            getDocumentsMutableLiveData.postValue(docsInfo?.Items)
        }

    }

}