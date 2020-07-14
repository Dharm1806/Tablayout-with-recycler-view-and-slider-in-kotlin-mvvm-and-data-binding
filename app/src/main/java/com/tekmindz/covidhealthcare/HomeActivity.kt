package com.tekmindz.covidhealthcare


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.utills.Utills


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navController: NavController
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
      //  Log.e("format", "${Utills.round("103.232424267")}")
        setupNavigation()
    }


    @Override
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.nav_host_fragment),
            drawerLayout
        )
    }

    @Override
    override fun onBackPressed() = if (drawerLayout.isDrawerOpen(GravityCompat.START))
        drawerLayout.closeDrawer(GravityCompat.START)
    else super.onBackPressed()

    @Override
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        p0.isChecked = true
        drawerLayout.closeDrawers()

        when (p0.itemId) {
            //  R.id.email ->showMessage(getString(R.string.email_slected))
            // *//* if(navController.currentDestination?.id != R.id.commentList){
            // val action =
            //    IssuesListFragmentDirections.issuesListToComment("4996")
            // navController.navigate(action)}*//*

            //    R.id.favourites -> showMessage(getString(R.string.fav_selected))
            R.id.logout -> {
                App.mSharedPrefrenceManager.setIsLogin(PREF_IS_LOGIN, false)
                navController.navigate(
                    R.id.homeTologin, null, NavOptions.Builder()
                        .setPopUpTo(
                            R.id.home,
                            true
                        ).build()
                )
            }


            R.id.selfAssesment -> {/*App.mSharedPrefrenceManager.getValueString(
                Constants.PREF_ACCESS_TOKEN))*/
                val bundle = bundleOf("patientId" to 3)
                navController.navigate(R.id.homeToPatientDetails, bundle)
                /*val intent = Intent(this, MainActivity::class.java)
                   startActivity(intent)*/
            }

            R.id.base_url ->{
               // val bundle = bundleOf("patientId" to 3)
                navController.navigate(R.id.homeToSetUrl, null)
            }

        }
        return true
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val findMenuItems = menuInflater
        findMenuItems.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notifications -> {
                /*val aboutIntent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(aboutIntent)*/
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun setupNavigation() {
        setTheme(R.style.AppTheme)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
       NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        //setupActionBarWithNavController(navController, appBarConfiguration)

        NavigationUI.setupWithNavController(navigationView, navController)
        navigationView.setNavigationItemSelectedListener(this)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            var isDestination = false

            if (Utills.userType().toString() == UserTypes.PATIENT.toString()){
                isDestination = destination.id == R.id.login || destination.id == R.id.search// || destination.id== R.id.patient_details
            }else{
                isDestination = destination.id == R.id.login || destination.id == R.id.search
            }
            if (isDestination) {
                toolbar.visibility = View.GONE
                navigationView.visibility = View.GONE
                drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
            } else {
                toolbar.visibility = View.VISIBLE
                navigationView.visibility = View.VISIBLE
                //navigationView
                drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED)
            }
        }
    }


}