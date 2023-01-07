package com.lonquers.challengesophosapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.preference.PreferenceManager
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lonquers.challengesophosapp.R
import com.lonquers.challengesophosapp.databinding.FragmentOfficesBinding
import com.lonquers.challengesophosapp.model.OfficeItems
import com.lonquers.challengesophosapp.viewModel.GetOffices
import java.util.*

class OfficesFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentOfficesBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private val REQUEST_CODE_LOCATION = 0
    private val getOfficesViewModel: GetOffices by viewModels()
    private var citiesObserved: MutableList<OfficeItems> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfficesBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
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

        getOfficesViewModel.citiesLiveData.observe(viewLifecycleOwner) {
            getOfficesViewModel.getOffices()

            for (cities in getOfficesViewModel.citiesLiveData.value!!) {
                citiesObserved.add(cities)
            }

            createMarker()

        }
        createFragment()
        return binding.root
    }


    private fun createFragment() {
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
        enableLocation()
    }


    private fun createMarker() {
        for (cities in citiesObserved) {
            val markerOptions = MarkerOptions()
                .position(LatLng(cities.Latitud.toDouble(), cities.Longitud.toDouble()))
                .title(cities.Nombre)
            map.addMarker(markerOptions)
        }
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(6.247642266372111, -75.56574513949772), 12f),
            4000,
            null
        )
    }

    private fun locationPermissions() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (locationPermissions()) {
            if (context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED && context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }

    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(context, "Set up location permissions in settings", Toast.LENGTH_SHORT)
                .show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (context?.let {
                        ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } != PackageManager.PERMISSION_GRANTED && context?.let {
                        ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    } != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                map.isMyLocationEnabled = true
            } else {
                val locationPermission = getString(R.string.location_permission)
                Toast.makeText(
                    context,
                    locationPermission,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_option, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sendDocumentsMenu -> {
                view?.findNavController()
                    ?.navigate(
                        OfficesFragmentDirections.actionGetOfficesFragmentToWelcomeFragment(
                            arguments?.getString("user_name"),
                            arguments?.getString("user_email")
                        )
                    )
                view?.findNavController()?.navigate(
                    WelcomeFragmentDirections.actionWelcomeFragmentToSendDocumentsFragment(
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
            R.id.listDocumentMenu -> {
                view?.findNavController()
                    ?.navigate(
                        OfficesFragmentDirections.actionGetOfficesFragmentToWelcomeFragment(
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
                val youOnOffices = getString(R.string.on_offices)
                Toast.makeText(context, youOnOffices, Toast.LENGTH_SHORT)
                    .show()
                true
            }
            R.id.logoutMenu -> {
                view?.findNavController()
                    ?.navigate(R.id.action_getOfficesFragment_to_signInFragment)
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
                OfficesFragmentDirections.actionGetOfficesFragmentToWelcomeFragment(
                    arguments?.getString("user_name"),
                    arguments?.getString("user_email")
                )
            )
        view?.findNavController()
            ?.navigate(
                WelcomeFragmentDirections.actionWelcomeFragmentToGetOfficesFragment(
                    arguments?.getString("user_email"),
                    arguments?.getString("user_name")
                )
            )
    }

}