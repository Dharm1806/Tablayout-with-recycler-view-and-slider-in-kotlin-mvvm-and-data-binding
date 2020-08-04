package com.tekmindz.covidhealthcare


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import com.tekmindz.covidhealthcare.utills.Utills


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var controller: NavController
    private lateinit var listener: NavController.OnDestinationChangedListener
    private lateinit var tvNotificationItemCount: TextView
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navController: NavController
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupNavigation()
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(
            this
        ) { instanceIdResult: InstanceIdResult ->
            val newToken = instanceIdResult.token
            Log.e("newToken", newToken)

        }
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

            R.id.help -> {
                navController.navigate(R.id.navigateToHelp, null)
            }

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


            R.id.selfAssesment -> {
                val patientId = Utills.getPatientId()
                if (patientId != null && patientId.toInt() != 0) {
                    Log.e("patientId", "$patientId")
                    val bundle = bundleOf("patientId" to patientId.toInt())
                    navController.navigate(R.id.homeToPatientDetails, bundle)
                } else showAlertMessage()

            }

            R.id.base_url -> {
                // val bundle = bundleOf("patientId" to 3)
                navController.navigate(R.id.homeToSetUrl, null)
            }

        }
        return true
    }

    private fun showAlertMessage() {
        Utills.showAlertNoPatient(this)
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val menuItem = menu!!.findItem(R.id.notifications)

        val actionView = menuItem.actionView
        tvNotificationItemCount = actionView.findViewById<View>(R.id.notification_badge) as TextView

        setupBadge()

        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }

        return true
    }

    private fun setupBadge() {
        val mNotificationCount = 0
        if (tvNotificationItemCount != null) {
            if (mNotificationCount == 0) {
                if (tvNotificationItemCount.visibility != View.GONE) {
                    tvNotificationItemCount.visibility = View.GONE
                }
            } else {
                tvNotificationItemCount.text = mNotificationCount.toString()
                if (tvNotificationItemCount.visibility != View.VISIBLE) {
                    tvNotificationItemCount.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notifications -> {
                navController.navigate(R.id.navigateToNotifications, null)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendSos() {
        Utills.callPhoneNumber(this)

    }

    override  fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utills.callPhoneNumber(this)
            }
        }
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

            if (Utills.isPatient(App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))) {
                isDestination =
                    destination.id == R.id.login || destination.id == R.id.search// || destination.id== R.id.patient_details
            } else {
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
        listener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                Utills.destination = destination.label.toString()
            }

        hideSelfAssesment()
        //getCurrentFragment()
    }

    fun hideSelfAssesment() {
        // val userTypes = App.mSharedPrefrenceManager.getValueString(PREF_USER_TYPE)
        val menu = navigationView.menu
        if (Utills.isPatient(App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))) {

            menu.findItem(R.id.selfAssesment).isVisible = false
        } else menu.findItem(R.id.selfAssesment).isVisible = true

    }


    public override fun onResume() {
        super.onResume()
        hideSelfAssesment()
        navController.addOnDestinationChangedListener(listener)
    }

    public override fun onPause() {
        navController.removeOnDestinationChangedListener(listener)
        super.onPause()

    }

}