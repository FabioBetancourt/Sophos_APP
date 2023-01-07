package com.lonquers.challengesophosapp.viewModel

import androidx.lifecycle.*
import com.lonquers.challengesophosapp.port.APISignIn
import com.lonquers.challengesophosapp.model.SignInResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignIn : ViewModel() {

    private val _signInResponse: MutableLiveData<Response<SignInResponse>> = MutableLiveData()
    val signInApiResponse: LiveData<Response<SignInResponse>>
        get() = _signInResponse


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getSignInViewModel(emailWrite: String, passwordWrite: String) {
        viewModelScope.launch {
            _signInResponse.value = getRetrofit().create(APISignIn::class.java)
                .getSignIn(emailWrite, passwordWrite)
        }
    }
}


