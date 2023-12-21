package com.mobile.communihealthv2.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.HomeBinding
import com.mobile.communihealthv2.databinding.NavHeaderBinding
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.mobile.communihealthv2.ui.auth.Login
import com.mobile.communihealthv2.ui.map.MapsViewModel
import com.mobile.communihealthv2.utils.checkLocationPermissions
import com.mobile.communihealthv2.utils.isPermissionGranted
import timber.log.Timber

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeBinding: HomeBinding
    private lateinit var navHeaderBinding: NavHeaderBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loggedInViewModel: LoggedInViewModel
    private val mapsViewModel : MapsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeBinding = HomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = homeBinding.drawerLayout

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.patientFragment, R.id.patientListFragment, R.id.mapsFragment,R.id.aboutscreenFragment, R.id.calendarFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        if(checkLocationPermissions(this)) {
            mapsViewModel.updateCurrentLocation()
        }

        homeBinding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_sign_out -> {
                    signOut()
                    true
                }
                R.id.patientListFragment -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.patientListFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)
        loggedInViewModel.liveFirebaseUser.observe(this, Observer { firebaseUser ->
            if (firebaseUser != null) {
                updateNavHeader(loggedInViewModel.liveFirebaseUser.value!!)
            }
        })

        loggedInViewModel.loggedOut.observe(this, Observer { loggedOut ->
            if (loggedOut) {
                startActivity(Intent(this, Login::class.java))
            }
        })
    }

    private fun updateNavHeader(currentUser: FirebaseUser) {
        val headerView = homeBinding.navView.getHeaderView(0)
        navHeaderBinding = NavHeaderBinding.bind(headerView)
        navHeaderBinding.navHeaderEmail.text = currentUser.email
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun signOut() {

        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()

        Toast.makeText(this, "You've been signed out.", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isPermissionGranted(requestCode, grantResults))
            mapsViewModel.updateCurrentLocation()
        else {
            // permissions denied, so use a default location
            mapsViewModel.currentLocation.value = Location("Default").apply {
                latitude = 53.220566
                longitude = -6.659308
            }
        }
        Timber.i("LOC : %s", mapsViewModel.currentLocation.value)
    }
}

