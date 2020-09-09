package com.dharam.offers


import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.dharam.offers.utills.Utills


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var controller: NavController
    private lateinit var listener: NavController.OnDestinationChangedListener
    private lateinit var tvNotificationItemCount: TextView
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navController: NavController
    lateinit var navigationView: NavigationView
    var mContext: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupNavigation()
        mContext = this


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


    private fun sendSos() {
        Utills.callPhoneNumber(this)

    }

    override fun onRequestPermissionsResult(
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
        setTheme(R.style.AppTheme1)

     //   toolbar = findViewById(R.id.toolbar)
      //  setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        drawerLayout = findViewById(R.id.drawer_layout)
        // navigationView = findViewById(R.id.navigationView)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        // NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        //setupActionBarWithNavController(navController, appBarConfiguration)

        // NavigationUI.setupWithNavController(navigationView, navController)
        //navigationView.setNavigationItemSelectedListener(this)
    }


    public override fun onResume() {
        super.onResume()
       // navController.addOnDestinationChangedListener(listener)
    }

    public override fun onPause() {
      //  navController.removeOnDestinationChangedListener(listener)
        super.onPause()

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }
}