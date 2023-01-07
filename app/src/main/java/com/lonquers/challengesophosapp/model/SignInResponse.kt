package com.lonquers.challengesophosapp.model


data class SignInResponse(
  var id: String,
  var nombre: String,
  var apellido: String,
  var acceso: Boolean,
  var admin: Boolean
)
