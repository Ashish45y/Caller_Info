package dev.ashish.callerinfo.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestOverlayPermissionLauncher: ActivityResultLauncher<Intent>


    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CALL_LOG
    )

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
        initializePermissionLaunchers()
        if (!hasAllPermissions() || !hasOverlayPermission()){
            requestPermissionsOrOverlay()
        }else{
            setupUI()
        }
    }

    private fun setupUI() {
        setUpRecyclerView()
        setUpObserveViewModel()
        appInfoViewModel.fetchApps()
    }
    private fun setUpRecyclerView(){
        appInfoAdapter = AppInfoAdapter{ packageName ->
            openAppInfo(packageName)
        }
        binding.recView.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(15)
        }
        binding.recView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.appInfoAdapter
        }
    }
    private fun setUpObserveViewModel(){
        lifecycleScope.launch {
            appInfoViewModel.uiState.collectLatest {uiState ->
                when(uiState){
                    is AppInfoUiState.Loading ->{

                    }
                    is AppInfoUiState.Success ->{
                        appInfoAdapter.submitData(uiState.apps)
                    }
                    is AppInfoUiState.Error ->{

                    }
                }

            }
        }
    }
    private fun openAppInfo(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    private fun initializePermissionLaunchers() {
        requestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val allGranted = permissions.values.all { it }
                if (allGranted) {
                    if (hasOverlayPermission()) {
                        setupUI()
                    } else {
                        requestOverlayPermission()
                    }
                } else {
                    Toast.makeText(this, "All permissions are required to proceed.", Toast.LENGTH_LONG).show()
                }
            }

        requestOverlayPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (hasOverlayPermission()) {
                    if (hasAllPermissions()) {
                        setupUI()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Overlay permission is required for the app to function.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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

    private fun requestPermissionsOrOverlay() {
        if (!hasAllPermissions()) {
            requestPermissionsLauncher.launch(requiredPermissions)
        }
        if (!hasOverlayPermission()) {
            requestOverlayPermission()
        }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        requestOverlayPermissionLauncher.launch(intent)
    }
}