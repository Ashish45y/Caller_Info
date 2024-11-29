package dev.ashish.callerinfo.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import dev.ashish.callerinfo.utils.logger.Logger
import dev.ashish.callerinfo.viewmodel.AppInfoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * MainActivity that handles the user interface of the app.
 * This includes setting up RecyclerView, managing permissions,
 * and observing the app info from the ViewModel.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // View binding for the main activity layout
    private lateinit var binding: ActivityMainBinding
    // ViewModel for managing app data

    private val appInfoViewModel: AppInfoViewModel by viewModels()

    // Adapter for RecyclerView
    @Inject
    lateinit var appInfoAdapter: AppInfoAdapter

    // Handlers for permissions and RecyclerView setup
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var recyclerViewHandler: RecyclerViewHandler

    // List of required permissions for the app

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CALL_LOG
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inflate and set the content view for the activity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize permission and RecyclerView handlers
        permissionHandler = PermissionHandler(this)
        recyclerViewHandler = RecyclerViewHandler(this, appInfoAdapter)
        // Request permissions or overlay permission if not granted

        if (!permissionHandler.hasAllPermissions() || !permissionHandler.hasOverlayPermission()) {
            permissionHandler.requestPermissionsOrOverlay()
        } else {
            setupUI()
        }
    }

    /**
     * Sets up the UI components such as RecyclerView and observes the ViewModel state.
     */

    private fun setupUI() {
        recyclerViewHandler.setUpRecyclerView()
        recyclerViewHandler.setUpObserveViewModel()
        appInfoViewModel.fetchApps()
    }

    /**
     * A handler class to manage the RecyclerView setup and item click behavior.
     */

    inner class RecyclerViewHandler(
        private val activity: AppCompatActivity,
        private var appInfoAdapter: AppInfoAdapter
    ) {

        /**
         * Sets up the RecyclerView with the appropriate adapter and layout manager.
         */
        fun setUpRecyclerView() {
            appInfoAdapter = AppInfoAdapter { packageName ->
                openAppInfo(packageName)   // Handle item click to open app info
            }
            binding.recView.apply {
                setHasFixedSize(true) // Set fixed size to improve performance
                setItemViewCacheSize(15)   // Set item cache size for faster scrolling
                layoutManager = LinearLayoutManager(activity)
                adapter = appInfoAdapter  // Set the adapter to RecyclerView
            }
        }

        /**
         * Opens the app details settings page when an app is clicked.
         *
         * @param packageName The package name of the app to open.
         */
        private fun openAppInfo(packageName: String) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
            }
            activity.startActivity(intent)
        }

        /**
         * Sets up an observer to collect the latest UI state from the ViewModel.
         */
        fun setUpObserveViewModel() {
            lifecycleScope.launch {
                appInfoViewModel.uiState.collectLatest { uiState ->
                    when (uiState) {
                        is AppInfoUiState.Loading -> {

                        }

                        is AppInfoUiState.Success -> {
                            appInfoAdapter.submitData(uiState.apps)
                        }

                        is AppInfoUiState.Error -> {

                        }
                    }

                }
            }
        }
    }

    /**
     * A handler class for managing permission requests and overlay permission requests.
     */
    inner class PermissionHandler(private val activity: AppCompatActivity) {

        private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
        private lateinit var requestOverlayPermissionLauncher: ActivityResultLauncher<Intent>

        init {
            initializePermissionLaunchers()
        }

        /**
         * Initializes the permission launchers for requesting multiple permissions and overlay permission.
         */
        private fun initializePermissionLaunchers() {
            requestPermissionsLauncher =
                activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    val allGranted = permissions.values.all { it }
                    Log.d("rahul", "initializePermissionLaunchers: $allGranted")
                    if (allGranted) {
                        if (hasOverlayPermission()) {
                            setupUI()
                        } else {
                            requestOverlayPermission()
                        }
                    } else {
                        Toast.makeText(
                            activity,
                            "All permissions are required to proceed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            requestOverlayPermissionLauncher =
                activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (hasOverlayPermission()) {
                        if (hasAllPermissions()) {
                            setupUI()
                        }
                    } else {
                        Toast.makeText(
                            activity,
                            "Overlay permission is required for the app to function.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        /**
         * Checks whether all required permissions are granted.
         */
        fun hasAllPermissions(): Boolean {
            return requiredPermissions.all {
                ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
            }
        }

        /**
         * Checks whether the overlay permission is granted.
         */
        fun hasOverlayPermission(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(activity)
            } else {
                true
            }
        }

        /**
         * Requests the necessary permissions if they are not already granted.
         */
        fun requestPermissionsOrOverlay() {
            if (!hasAllPermissions()) {
                requestPermissionsLauncher.launch(requiredPermissions)
            }
            if (!hasOverlayPermission()) {
                requestOverlayPermission()
            }
        }

        /**
         * Requests the overlay permission from the user.
         */
        private fun requestOverlayPermission() {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            requestOverlayPermissionLauncher.launch(intent)
        }
    }

}