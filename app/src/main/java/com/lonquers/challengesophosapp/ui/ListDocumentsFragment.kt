package com.lonquers.challengesophosapp.ui


import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Base64
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lonquers.challengesophosapp.R
import com.lonquers.challengesophosapp.databinding.FragmentListDocumentsBinding
import com.lonquers.challengesophosapp.ui.adapter.AdapterViewDocument
import com.lonquers.challengesophosapp.viewModel.GetDocuments
import com.lonquers.challengesophosapp.viewModel.GetDocumentsById
import java.util.*


class ListDocumentsFragment : Fragment(R.layout.fragment_list_documents) {

    private val getDocumentsByViewModel: GetDocuments by viewModels()
    private val getDocumentsById: GetDocumentsById by viewModels()

    /*
    The _binding variable is a FragmentListDocumentsBinding object that is used to access the views
    in the layout of the fragment.
     */
    private var _binding: FragmentListDocumentsBinding? = null
    private val binding get() = _binding!!

    /*
    onCreateView is a method that is called when the Fragment is first created. The method inflates
    the layout of the Fragment using a LayoutInflater object and a ViewGroup container, and returns
    the inflated View object.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*
         the method first creates an instance of FragmentListDocumentsBinding by inflating the
         layout of the Fragment. It then enables the options menu for the Fragment by calling
         setHasOptionsMenu(true).
         */
        _binding = FragmentListDocumentsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        /*It then sets the title of the Fragment to the string resource with the
        ID R.string.come_back and enables the home button in the action bar
         */
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.come_back)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //It then checks if the device is in dark or light mode, and sets the home button indicator accordingly.
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_dark_mode)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_light_mode)
            }
        }

        /*
        The method then retrieves the user's email from the Fragment's arguments, and initializes
         the RecyclerView that is used to display a list of documents
         */
        val email = arguments?.getString("user_email")!!
        initRecyclerView()

        /*
        In this case, the code is observing the getDocumentsLiveData property of the
        getDocumentsByViewModel view model, and it is using the observe method to register an
        observer that will be notified whenever the data held by the LiveData object changes.
         */
        getDocumentsByViewModel.getDocumentsLiveData.observe(viewLifecycleOwner) {

            getDocumentsByViewModel.getDocumentsOnList(email)
            initRecyclerView()
        }
        getDocumentsByViewModel.getDocumentsOnList(email)

        getDocumentsById.getDocumentsMutableLiveData.observe(viewLifecycleOwner) {
            val imgBase64 = getDocumentsById.getDocumentsMutableLiveData.value?.get(0)?.Adjunto
            val imgTransformed = decodeString64(imgBase64!!)
            binding.ivListDocuments.setImageBitmap(imgTransformed)
        }
        return binding.root


    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)

        binding.rvListDocuments.layoutManager = linearLayoutManager
        binding.rvListDocuments.adapter =
            AdapterViewDocument(getDocumentsByViewModel.getDocumentsLiveData.value) {
                getDocumentsById.getDocuments(it.IdRegistro)
            }
        binding.rvListDocuments.addItemDecoration(dividerItemDecoration)
    }

    private fun decodeString64(encodedString: String): Bitmap {
        val imageBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
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
                view?.findNavController()
                    ?.navigate(
                        ListDocumentsFragmentDirections.actionListDocumentsFragmentToWelcomeFragment(
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


            R.id.listDocumentMenu -> {
                val onDocuments = getString(R.string.on_documents)
                Toast.makeText(context, onDocuments, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.getOfficesMenu -> {
                view?.findNavController()
                    ?.navigate(
                        ListDocumentsFragmentDirections.actionListDocumentsFragmentToWelcomeFragment(
                            arguments?.getString("user_name"),
                            arguments?.getString("user_email")
                        )
                    )
                view?.findNavController()?.navigate(
                    WelcomeFragmentDirections.actionWelcomeFragmentToGetOfficesFragment(
                        arguments?.getString(
                            "user_email"
                        ),   arguments?.getString(
                            "user_name"
                        )
                    )
                )
                true
            }
            R.id.darkModeMenu -> {
                val prefsDarkLanguage = PreferenceManager.getDefaultSharedPreferences(context)
                val editor = prefsDarkLanguage.edit()

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
                    ?.navigate(R.id.action_listDocumentsFragment_to_signInFragment)
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
                ListDocumentsFragmentDirections.actionListDocumentsFragmentToWelcomeFragment(
                    arguments?.getString("user_name"),
                    arguments?.getString("user_email")
                )
            )

        view?.findNavController()
            ?.navigate(
                WelcomeFragmentDirections.actionWelcomeFragmentToListDocumentFragment(
                    arguments?.getString("user_email"),
                    arguments?.getString("user_name")
                )
            )
    }

}