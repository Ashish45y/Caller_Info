package dev.ashish.callerinfo.ui.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.ashish.callerinfo.databinding.AppDetailsBinding
import dev.ashish.callerinfo.model.AppInfo

class AppInfoAdapter(private val appInfoList: ArrayList<AppInfo>,private val onAppInfoClick: (String) -> Unit):
    RecyclerView.Adapter<AppInfoAdapter.AppInfoViewHolder>() {
    class AppInfoViewHolder(private val binding: AppDetailsBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(appInfo: AppInfo,onAppInfoClick: (String) -> Unit){
            binding.appname.text = appInfo.name
            binding.appimg.setImageDrawable(appInfo.icon)
            binding.root.setOnClickListener {
                onAppInfoClick(appInfo.packageName!!)
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

    override fun getItemCount(): Int = appInfoList.size

    override fun onBindViewHolder(holder: AppInfoViewHolder, position: Int) = holder.bind(appInfoList[position],onAppInfoClick)


    fun submitList(newList: List<AppInfo>) {
        appInfoList.clear()
        appInfoList.addAll(newList)
        notifyDataSetChanged()
    }
}