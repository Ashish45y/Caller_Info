package dev.ashish.callerinfo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.ashish.callerinfo.R
import dev.ashish.callerinfo.databinding.ActivityMainBinding
import dev.ashish.callerinfo.model.AppInfoUiState
import dev.ashish.callerinfo.ui.adpater.AppInfoAdapter
import dev.ashish.callerinfo.viewmodel.AppInfoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding

    private  val appInfoViewModel: AppInfoViewModel by viewModels()

   @Inject
   lateinit var appInfoAdapter: AppInfoAdapter

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECORD_AUDIO
    )
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!hasAllPermissions() && !hasOverlayPermission()) {
            requestPermissions(requiredPermissions, PERMISSION_REQUEST_CODE)
            requestPermissionsOrOverlay()
        } else {
            setupUI()
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun setupUI() {
        // Initialize the adapter with an empty list to start with
        appInfoAdapter = AppInfoAdapter(ArrayList()) { packageName ->
            openAppInfo(packageName)
        }

        // Set up the RecyclerView with a LinearLayoutManager and the adapter
        binding.recView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.appInfoAdapter
        }
        lifecycleScope.launch {
            appInfoViewModel.uiState.collectLatest {uiState ->
                when(uiState){
                    is AppInfoUiState.Loading ->{

                    }
                    is AppInfoUiState.Success ->{
                        appInfoAdapter.submitList(uiState.apps)
                    }
                    is AppInfoUiState.Error ->{

                    }
                }

            }
        }
        appInfoViewModel.fetchApps()
    }
    private fun openAppInfo(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }
    @SuppressLint("InlinedApi")
    private fun requestPermissionsOrOverlay() {
        if (!hasAllPermissions()) {
            requestPermissions(requiredPermissions, PERMISSION_REQUEST_CODE)
        }
        if (!hasOverlayPermission()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setupUI()
            } else {
                Toast.makeText(this, "Permissions are required for the app to function.", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (hasOverlayPermission()) {
                setupUI()
            } else {
                Toast.makeText(this, "Overlay permission is required for the app to function.", Toast.LENGTH_LONG).show()
            }
        }
    }
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1002
    }
}