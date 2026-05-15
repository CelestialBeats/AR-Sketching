package com.fahad.newtruelovebyfahad.ui.fragments.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fahad.newtruelovebyfahad.databinding.ItemHomeCategoryBinding
import com.project.common.viewmodels.HomeAndTemplateViewModel

class HomeForYouAdapter(
    private val onClick: (item: HomeAndTemplateViewModel.DrawableItem, position: Int) -> Unit
) : RecyclerView.Adapter<HomeForYouAdapter.CategoryViewHolder>() {

    private val dataList: MutableList<HomeAndTemplateViewModel.CategoryModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemHomeCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = dataList[position]
        with(holder.binding) {
            tvCategoryTitle.text = category.title

            val childAdapter = HomeForYouChildAdapter(onClick)
            rvCategoryItems.adapter = childAdapter
            childAdapter.submitList(category.items)

            // Setting setting proper animation off to avoid flash during update
            rvCategoryItems.itemAnimator = null
        }
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(items: List<HomeAndTemplateViewModel.CategoryModel>) {
        dataList.clear()
        dataList.addAll(items)
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(val binding: ItemHomeCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
