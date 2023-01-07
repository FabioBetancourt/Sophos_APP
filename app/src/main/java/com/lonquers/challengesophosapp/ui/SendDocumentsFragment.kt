package com.lonquers.challengesophosapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.lonquers.challengesophosapp.R
import com.lonquers.challengesophosapp.databinding.FragmentSendDocumentsBinding
import com.lonquers.challengesophosapp.model.DocumentItemsPost
import com.lonquers.challengesophosapp.viewModel.PostDocuments
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class SendDocumentsFragment : Fragment(R.layout.fragment_send_documents),
    AdapterView.OnItemSelectedListener {

    private lateinit var arrayAdapterTypeDocuments: ArrayAdapter<String>
    private var _binding: FragmentSendDocumentsBinding? = null
    private val binding get() = _binding!!
    private val postDocuments: PostDocuments by viewModels()
    private lateinit var arrayAdapterCities: ArrayAdapter<String>


    private val camera: Int = 100
    private val storage: Int = 100


    private val cameraCode: Int = 101
    private val storageCode: Int = 102


    private val calendar: Calendar = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat")
    private val currentDate = SimpleDateFormat("dd/M/yyyy").format(calendar.time)
    private lateinit var citySelected: String
    private var imageBase64 = ""
    private lateinit var typeDocumentSelected: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentSendDocumentsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.come_back)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)


        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_dark_mode)

            }
            Configuration.UI_MODE_NIGHT_NO -> {
                (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_light_mode)
            }
        }

        val documentType = getString(R.string.document_type)

        arrayAdapterTypeDocuments =
            ArrayAdapter<String>(requireContext(), R.layout.spinner_light_mode)
        arrayAdapterCities = ArrayAdapter<String>(requireContext(), R.layout.spinner_light_mode)
        arrayAdapterTypeDocuments.addAll(listOf(documentType, "CC", "TI", "PA", "CE"))

        postDocuments.cityLiveData.observe(viewLifecycleOwner) {
            val city = getString(R.string.city)
            arrayAdapterCities.addAll(
                listOf(
                    city,
                    postDocuments.cityLiveData.value?.get(0).toString(),
                    postDocuments.cityLiveData.value?.get(1).toString(),
                    postDocuments.cityLiveData.value?.get(2).toString(),
                    postDocuments.cityLiveData.value?.get(3).toString(),
                    postDocuments.cityLiveData.value?.get(4).toString(),
                    postDocuments.cityLiveData.value?.get(5).toString(),
                )
            )
        }
        binding.spId.adapter = arrayAdapterTypeDocuments
        binding.spLocationCity.adapter = arrayAdapterCities
        binding.spId.onItemSelectedListener = this
        binding.spLocationCity.onItemSelectedListener = this
        binding.etEmailSendDocuments.setText(arguments?.getString("user_email"))

        binding.ivCamera.setOnClickListener {
            cameraCheckPermission()
        }

        binding.btnUploadDocument.setOnClickListener {
            galleryCheckPermission()
        }

        postDocuments.documentModel.observe(viewLifecycleOwner) {
            postDocuments.sendDocument(infoToSendDocument())
        }

        binding.btnSendDocument.setOnClickListener {
            val documentHasBeenSent = getString(R.string.document_has_been_sent)
            val fieldInFields = getString(R.string.field_in_fields)
            when (dataToSendDocument()) {
                true -> {
                    postDocuments.sendDocument(infoToSendDocument())
                    Toast.makeText(
                        context,
                        documentHasBeenSent,
                        Toast.LENGTH_SHORT
                    ).show()
                    println(infoToSendDocument())
                    view?.findNavController()?.navigate(
                        SendDocumentsFragmentDirections.actionSendDocumentsFragmentSelf(
                            arguments?.getString(
                                "user_email"
                            ),
                            arguments?.getString(
                                "user_name"
                            )
                        )
                    )
                }
                else -> Toast.makeText(
                    context,
                    fieldInFields,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return binding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {

            R.id.spId -> typeDocumentSelected =
                arrayAdapterTypeDocuments.getItem(position).toString()

            R.id.spLocationCity -> citySelected = arrayAdapterCities.getItem(position).toString()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    private fun cameraCheckPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> {
                chooseImageOnCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                val permissionDeniedMessage = getString(R.string.camera_permission)
                Toast.makeText(context, permissionDeniedMessage, Toast.LENGTH_SHORT).show()

            }
            else -> {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), camera)
            }

        }
    }

    private fun galleryCheckPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    == PackageManager.PERMISSION_GRANTED -> {
                uploadImageInCircleView()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {

                val permissionGalleryDenied = getString(R.string.camera_permission)
                Toast.makeText(
                    context,
                    permissionGalleryDenied,
                    Toast.LENGTH_SHORT
                ).show()

            }
            else -> {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), storage
                )
            }

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            camera -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImageOnCamera()
                }
            }

            storage -> {
                if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    uploadImageInCircleView()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }

    }

    private fun chooseImageOnCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, cameraCode)
    }

    private fun uploadImageInCircleView() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val types = arrayOf("images/jpeg", "images/jpg", "images/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, types)
            startActivityForResult(it, storageCode)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            cameraCode -> {
                if (resultCode != Activity.RESULT_OK) {
                    val failedResultPhoto = getString(R.string.failed_result_photo)
                    Toast.makeText(context, failedResultPhoto, Toast.LENGTH_SHORT).show()
                } else {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.ivCamera.setImageBitmap(bitmap)
                    imageBase64 = convertBase64(bitmap)
                }
            }
            this.storageCode -> {
                if ((requestCode == storageCode) && (resultCode == AppCompatActivity.RESULT_OK)) {
                    binding.ivCamera.setImageURI(data?.data)
                    val bitmap = convertUriToBitmap(data?.data)
                    imageBase64 = convertBase64(bitmap)
                } else {
                    val failedResultGallery = getString(R.string.failed_result_gallery)
                    Toast.makeText(context, failedResultGallery, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun convertBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val image = byteArrayOutputStream.toByteArray()
        return android.util.Base64.encodeToString(image, android.util.Base64.DEFAULT)
    }

    private fun infoToSendDocument(): DocumentItemsPost {
        return DocumentItemsPost(
            currentDate,
            typeDocumentSelected,
            binding.etIdSendDocuments.text.toString().trim(),
            binding.etNameSendDocuments.text.toString().trim(),
            binding.etLastNameSendDocuments.text.toString().trim(),
            citySelected,
            binding.etEmailSendDocuments.text.toString().trim(),
            binding.etDocumentType.text.toString().trim(),
            imageBase64

        )
    }

    private fun convertUriToBitmap(uri: Uri?): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri!!)
            ImageDecoder.decodeBitmap(source)
        }
    }

    private fun dataToSendDocument(): Boolean {
        if (infoToSendDocument().TipoId == "Tipo de documento" ||
            infoToSendDocument().Identificacion == "" ||
            infoToSendDocument().Nombre == "" ||
            infoToSendDocument().Apellido == "" ||
            infoToSendDocument().Correo == "" ||
            infoToSendDocument().Ciudad == "Ciudad" ||
            infoToSendDocument().TipoAdjunto == "" ||
            infoToSendDocument().Adjunto == "" ||
            infoToSendDocument().TipoAdjunto == ""
        ) {
            return false
        }
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_option, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val darkModeSetLocate = getString(R.string.dark_mode)
        val lightModeSetLocate = getString(R.string.light_mode)
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                menu.findItem(R.id.darkModeMenu).title = lightModeSetLocate

            }
            Configuration.UI_MODE_NIGHT_NO -> {
                menu.findItem(R.id.darkModeMenu).title = darkModeSetLocate
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sendDocumentsMenu -> {
                val onSendDocument = getString(R.string.on_send_document)
                Toast.makeText(context, onSendDocument, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.listDocumentMenu -> {
                view?.findNavController()
                    ?.navigate(
                        SendDocumentsFragmentDirections.actionSendDocumentsFragmentToWelcomeFragment(
                            arguments?.getString("user_name"),
                            arguments?.getString("user_email")
                        )
                    )
                view?.findNavController()?.navigate(
                    WelcomeFragmentDirections.actionWelcomeFragmentToListDocumentFragment(
                        arguments?.getString(
                            "user_email"
                        ),
                        arguments?.getString(
                            "user_name"
                        )
                    )
                )

                true
            }
            R.id.getOfficesMenu -> {
                view?.findNavController()
                    ?.navigate(
                        SendDocumentsFragmentDirections.actionSendDocumentsFragmentToWelcomeFragment(
                            arguments?.getString("user_name"),
                            arguments?.getString("user_email")
                        )
                    )

                view?.findNavController()?.navigate(
                    WelcomeFragmentDirections.actionWelcomeFragmentToGetOfficesFragment(
                        arguments?.getString(
                            "user_email"
                        ), arguments?.getString(
                            "user_name"
                        )
                    )
                )
                true
            }
            R.id.darkModeMenu -> {
                val sharedPrefsDarkLanguage = PreferenceManager.getDefaultSharedPreferences(context)
                val editor = sharedPrefsDarkLanguage.edit()

                when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        editor.putString("My_Lang", resources.configuration.locale.language)
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        editor.putString("My_Lang", resources.configuration.locale.language)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
                editor.apply()
                true
            }
            R.id.languageMenu -> {
                when (getString(R.string.english_mode)) {
                    "English language" -> languagePreferences("en")
                    "Idioma Español" -> languagePreferences("es")
                }
                navigateFragmentItself()

                true
            }

            R.id.logoutMenu -> {
                view?.findNavController()
                    ?.navigate(R.id.action_sendDocumentsFragment_to_signInFragment)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun languagePreferences(Lang: String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPrefs.edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    private fun navigateFragmentItself() {
        view?.findNavController()
            ?.navigate(
                SendDocumentsFragmentDirections.actionSendDocumentsFragmentToWelcomeFragment(
                    arguments?.getString("user_name"),
                    arguments?.getString("user_email")
                )
            )
        view?.findNavController()
            ?.navigate(
                WelcomeFragmentDirections.actionWelcomeFragmentToSendDocumentsFragment(
                    arguments?.getString("user_email"),
                    arguments?.getString("user_name")
                )
            )
    }
}