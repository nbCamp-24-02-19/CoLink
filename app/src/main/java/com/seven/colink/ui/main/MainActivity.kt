package com.seven.colink.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.seven.colink.R
import com.seven.colink.databinding.ActivityMainBinding
import com.seven.colink.ui.main.viewmodel.MainViewModel
import com.seven.colink.ui.mypageedit.MyPageEditDetailActivity
import com.seven.colink.ui.notify.NotificationActivity
import com.seven.colink.util.Constants
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
    }

    private val navController by lazy {
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }


    private fun initView() {
        initBottomNav()
        initPermission()
        onMyProfileEdit()
        onNotify()
        viewModel
    }

    private fun onNotify() = with(binding) {
        btnNotify.setOnClickListener {
            startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
        }
    }

    private fun initBottomNav() = with(binding) {
        navView.setupWithNavController(navController)
    }

    private fun onMyProfileEdit() = with(binding){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            btnMypageEdit.isVisible = destination.id == R.id.navigation_my_page
            btnNotify.isVisible = destination.id != R.id.navigation_dashboard
        }

        btnMypageEdit.setOnClickListener {
            startActivity(Intent(this@MainActivity ,MyPageEditDetailActivity::class.java))
        }
    }

    // permission
    private fun initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(Constants.IMAGE_PERMISSION, 200)
        } else {
            requestPermission(Constants.MEDIA_PERMISSION, 100)
        }
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        if (isPermissionGranted(permission)) {
            // TODO: 권한 부여시 작업 수행
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100, 200 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: 권한 부여시 작업 수행
                } else {
                    finish()
                }
            }
        }
    }
}