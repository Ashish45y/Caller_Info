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

class AppInfoAdapter(private val onAppInfoClick: (String) -> Unit):
    PagingDataAdapter<AppInfo,AppInfoAdapter.AppInfoViewHolder>(AppInfoComparator) {
    class AppInfoViewHolder(private val binding: AppDetailsBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(appInfo: AppInfo, onAppInfoClick: (String) -> Unit){
            binding.appname.text = appInfo.name
            binding.appimg.setImageDrawable(appInfo.icon)
            binding.root.setOnClickListener {
                onAppInfoClick(appInfo.packageName!!)
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoViewHolder {
        return AppInfoViewHolder(AppDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }
    object AppInfoComparator : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: AppInfoViewHolder, position: Int) {
        val appInfo = getItem(position)
        appInfo?.let {
            holder.bind(it, onAppInfoClick)
        }
    }

}