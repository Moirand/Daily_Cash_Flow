package com.my.dailycashflow.ui.expensessummary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.my.dailycashflow.R
import com.my.dailycashflow.data.ExpensesSummaryByCategory

class ExpensesSummaryByCategoryAdapter (
    private val onClick: (ExpensesSummaryByCategory) -> Unit
) : ListAdapter<ExpensesSummaryByCategory, ExpensesSummaryByCategoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesSummaryByCategoryViewHolder {
        return ExpensesSummaryByCategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_expense_category_item, parent, false))
    }

    override fun onBindViewHolder(holder: ExpensesSummaryByCategoryViewHolder, position: Int) {
        holder.bindData(getItem(position) as ExpensesSummaryByCategory)
        holder.itemView.setOnClickListener {
            onClick.invoke(getItem(position) as ExpensesSummaryByCategory)
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ExpensesSummaryByCategory>() {
            override fun areItemsTheSame(oldItem:ExpensesSummaryByCategory, newItem:ExpensesSummaryByCategory): Boolean {
                return oldItem.category.id == newItem.category.id
            }

            override fun areContentsTheSame(oldItem: ExpensesSummaryByCategory, newItem:ExpensesSummaryByCategory): Boolean {
                return oldItem == newItem
            }
        }

    }

}