package com.lonquers.challengesophosapp.ui

import android.content.res.Configuration
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.navigation.findNavController
import com.lonquers.challengesophosapp.R
import com.lonquers.challengesophosapp.databinding.FragmentWelcomeBinding
import java.util.*


class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = arguments?.getString("user_name")
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.btnSendDocuments.setOnClickListener {
            view?.findNavController()
                ?.navigate(
                    WelcomeFragmentDirections.actionWelcomeFragmentToSendDocumentsFragment(
                        arguments?.getString(
                            "user_email"
                        ),
                        arguments?.getString(
                            "user_name"
                        )
                    )
                )
        }
        binding.btnListDocuments.setOnClickListener {
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
        }
        binding.btnGetOffices.setOnClickListener {
            view?.findNavController()?.navigate(
                WelcomeFragmentDirections.actionWelcomeFragmentToGetOfficesFragment(
                    arguments?.getString(
                        "user_email"
                    ), arguments?.getString(
                        "user_name"
                    )
                )
            )
        }
        return binding.root
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
                        setDefaultNightMode(MODE_NIGHT_NO)
                        editor.putString("My_Lang", resources.configuration.locale.language)
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        editor.putString("My_Lang", resources.configuration.locale.language)
                        setDefaultNightMode(MODE_NIGHT_YES)
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
                view?.findNavController()?.navigate(R.id.action_welcomeFragment_to_signInFragment)
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
                WelcomeFragmentDirections.actionWelcomeFragmentSelf(
                    arguments?.getString("user_name"),
                    arguments?.getString("user_email")
                )
            )
    }

}
