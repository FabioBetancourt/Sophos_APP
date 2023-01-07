package com.lonquers.challengesophosapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.lonquers.challengesophosapp.R
import com.lonquers.challengesophosapp.databinding.FragmentSignInBinding
import com.lonquers.challengesophosapp.viewModel.SignIn
import java.util.concurrent.Executor
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL


class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private val signIn: SignIn by viewModels()
    private var userName:String? = null



    private lateinit var executor: Executor
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = " "
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        checkDeviceHasBiometric()

        binding.btnLogin.setOnClickListener {
            val writtenEmail = binding.etEmail.text?.trim().toString()
            val writtenPassword = binding.etPassword.text?.trim().toString()
            validateACorrectEmail(writtenEmail)
            signIn.getSignInViewModel(writtenEmail, writtenPassword)

            signIn.signInApiResponse.observe(viewLifecycleOwner) {
                if (it.body()?.acceso == true) {
                    binding.btnLogin.isEnabled = false
                    fingerprintSave()
                    userName = it.body()!!.nombre
                    navigateToPrincipalPage()
                } else {
                    Toast.makeText(
                        context,
                        R.string.sign_in_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }

        binding.btnFingerprint.setOnClickListener {
            if (!establishPrintSharedPreferences()) {
                establishPrintSharedPreferences()
            }
            fingerprintAuthentication()
            biometricPrompt.authenticate(promptInfo)
        }

        return binding.root
    }

    fun navigateToPrincipalPage() {
        view?.findNavController()
            ?.navigate(
                SignInFragmentDirections.actionSignInFragmentToWelcomeFragment(
                    userName,
                    binding.etEmail.text?.trim().toString()
                )
            )
    }


    private fun fingerprintAuthentication() {
        executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        context,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        context,
                        R.string.authentication_succeeded, Toast.LENGTH_SHORT
                    )
                        .show()
                    loadPreferencesUser()
                    val emailWritten = binding.etEmail.text?.trim().toString()
                    val passwordWritten = binding.etPassword.text?.trim().toString()
                    signIn.getSignInViewModel(emailWritten, passwordWritten)


                    signIn.signInApiResponse.observe(viewLifecycleOwner) {
                        if (it.body()?.acceso == true) {
                            userName = it.body()!!.nombre
                            navigateToPrincipalPage()
                        } else {
                            Toast.makeText(
                                context,
                                R.string.sign_in_error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        context, R.string.authentication_failed,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            })

        val biometricTitle = getString(R.string.biometric_tittle)
        val biometricSubtitle = getString(R.string.biometric_subtitle)
        val biometricNegative = getString(R.string.biometric_negative)

        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle(biometricTitle)
            .setSubtitle(biometricSubtitle)
            .setNegativeButtonText(biometricNegative)
            .build()

    }


    @SuppressLint("SetTextI18n")
    private fun checkDeviceHasBiometric() {

        val biomanagerSuccess = getString(R.string.biomanager_success)
        val biomanagerErrorHardware = getString(R.string.biomanager_error_hardware)
        val biomanagerPermissionUnavailable = getString(R.string.biomanager_permission_unavailable)


        val biometricManager = androidx.biometric.BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("Successful", "App can authenticate using biometrics.")
                binding.tvBiometricPermissions.text = biomanagerSuccess
            }
            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.d("HardwareError", "No biometric features available on this device.")
                binding.tvBiometricPermissions.text = biomanagerErrorHardware
            }
            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.d("PermissionsUnavailable", "Biometric features are currently unavailable.")
                binding.tvBiometricPermissions.text = biomanagerPermissionUnavailable
            }

            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                startActivityForResult(enrollIntent, 100)
            }
        }
    }

    private fun validateACorrectEmail(email: String) {
        val validateEmail = getString(R.string.validate_email)
        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = validateEmail
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun establishPrintSharedPreferences(): Boolean {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val email = sharedPrefs.getString("email", "")
        val password = sharedPrefs.getString("password", "")

        if (email == "" || password == "") {
            fingerprintErrorFirsTime()
            return false
        }
        return true

    }


    private fun fingerprintSave() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val editor = sharedPrefs.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()


    }


    private fun loadPreferencesUser() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val email = sharedPrefs.getString("email", "")
        val password = sharedPrefs.getString("password", "")

        binding.etEmail.setText(email)
        binding.etPassword.setText(password)
    }

    private fun fingerprintErrorFirsTime() {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle(R.string.alert_title)
        builder.setMessage(R.string.alert_message)
        val dialog = builder.create()
        dialog.show()
    }


}