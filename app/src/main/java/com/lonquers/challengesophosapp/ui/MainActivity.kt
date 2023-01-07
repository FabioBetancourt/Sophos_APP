package com.lonquers.challengesophosapp.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import com.lonquers.challengesophosapp.R
import com.lonquers.challengesophosapp.databinding.ActivityMainBinding
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setSupportActionBar(activityMainBinding.toolbarActivity)


        val navigationHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
        navController = navigationHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        when(this.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)){
            Configuration.UI_MODE_NIGHT_YES -> {
                activityMainBinding.toolbarActivity.setTitleTextColor(resources.getColor(R.color.white))
                activityMainBinding.toolbarActivity.overflowIcon = getDrawable(R.drawable.ic_menu_hamburger_dark_mode)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                activityMainBinding.toolbarActivity.setTitleTextColor(resources.getColor(R.color.light_color))
                activityMainBinding.toolbarActivity.overflowIcon = getDrawable(R.drawable.ic_menu_hamburger_light_mode)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}

        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_light_mode)

    }


    override fun onSupportNavigateUp(): Boolean {
        val navigationController = findNavController(R.id.navigation_host_fragment)
        return navigationController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}


