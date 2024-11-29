package dev.ashish.callerinfo.ui.adpater


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.ashish.callerinfo.R
import dev.ashish.callerinfo.databinding.AppDetailsBinding
import dev.ashish.callerinfo.model.AppInfo


/**
 * Adapter class for displaying a list of AppInfo objects in a RecyclerView with pagination.
 * The adapter uses PagingDataAdapter to load data in pages, and handles click events on each app item.
 *
 * @param onAppInfoClick A lambda function that is triggered when an app item is clicked.
 */

class AppInfoAdapter(private val onAppInfoClick: (String) -> Unit) :
    PagingDataAdapter<AppInfo, AppInfoAdapter.AppInfoViewHolder>(AppInfoComparator) {

    /**
     * ViewHolder class that holds and binds the views for each app item.
     * It also handles setting the background color based on whether the app has background access.
     *
     * @property binding The binding object for the app details layout.
     */
    class AppInfoViewHolder(private val binding: AppDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {


        /**
         * Binds the AppInfo data to the views in the ViewHolder.
         * Also handles setting the background color based on whether the app has background access.
         *
         * @param appInfo The AppInfo object that contains the app's data.
         * @param onAppInfoClick A lambda function that is triggered when the app item is clicked.
         */
        fun bind(appInfo: AppInfo, onAppInfoClick: (String) -> Unit) {
            // Set app name and icon
            binding.appname.text = appInfo.name
            binding.appimg.setImageDrawable(appInfo.icon)

            // Set the click listener for the app item
            binding.root.setOnClickListener {
                onAppInfoClick(appInfo.packageName!!)
            }
            // Change the background color based on whether the app has microphone access in background
            if (appInfo.hasBackgroundAccess == true) {
                Log.d("rahul", "bind: 25${appInfo.name}")
                val redColor = ContextCompat.getColor(binding.root.context, R.color.red)
                binding.root.setBackgroundColor(redColor)
            } else {
                val greenColor = ContextCompat.getColor(binding.root.context, R.color.green)
                binding.root.setBackgroundColor(greenColor)
            }
        }
    }


    /**
     * Called when a new ViewHolder is created. This is where the layout for each app item is inflated.
     *
     * @param parent The parent ViewGroup in which the new ViewHolder will be added.
     * @param viewType The view type of the new ViewHolder.
     * @return A new instance of AppInfoViewHolder.
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoViewHolder {
        // Inflate the layout for the app item
        return AppInfoViewHolder(
            AppDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * Object that defines how to compare two AppInfo items to detect changes.
     * Used by DiffUtil to optimize RecyclerView updates.
     */

    object AppInfoComparator : DiffUtil.ItemCallback<AppInfo>() {
        /**
         * Compares whether two AppInfo items are the same based on their package name.
         *
         * @param oldItem The previous AppInfo item.
         * @param newItem The new AppInfo item.
         * @return True if the items represent the same app, false otherwise.
         */
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        /**
         * Compares whether the contents of two AppInfo items are the same.
         *
         * @param oldItem The previous AppInfo item.
         * @param newItem The new AppInfo item.
         * @return True if the contents of the items are identical, false otherwise.
         */

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */

    override fun onBindViewHolder(holder: AppInfoViewHolder, position: Int) {
        val appInfo = getItem(position)
        appInfo?.let {
            holder.bind(it, onAppInfoClick)
        }
    }

}