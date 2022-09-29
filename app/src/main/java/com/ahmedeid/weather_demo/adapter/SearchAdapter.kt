package com.ahmedeid.weather_demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmedeid.weather_demo.databinding.ItemSearchBinding
import com.ahmedeid.weather_demo.model.search.SearchItem

class SearchAdapter(
    private val list: List<SearchItem>,
    private val listener: SearchAdapterListener
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val myView =
            ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(myView)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item: SearchItem = list[position]

        holder.binding.apply {
            searchTitleTextView.text = "${item.name} -  ${item.country}"
        }

        holder.itemView.setOnClickListener {
            listener.onSelected(item)
        }
    }

    override fun getItemCount() = list.size

    interface SearchAdapterListener {
        fun onSelected(item: SearchItem?)
    }
}