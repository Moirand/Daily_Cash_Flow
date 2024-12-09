package com.my.dailycashflow.ui.expensessummary

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.my.dailycashflow.R
import com.my.dailycashflow.data.ExpensesSummaryByCategory
import com.my.dailycashflow.util.formatToIDR

class ExpensesSummaryByCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindData(expenses: ExpensesSummaryByCategory) {
        val tvCategory: TextView = itemView.findViewById(R.id.text_category)
        val tvTotalExpense: TextView = itemView.findViewById(R.id.text_total_expenses)
        val tvLimit: TextView = itemView.findViewById(R.id.text_limited)

        tvCategory.text = expenses.category.name
        tvTotalExpense.text = expenses.total.formatToIDR()
        tvLimit.text = expenses.category.limit.formatToIDR()

        if (expenses.total >= expenses.category.limit){
            tvTotalExpense.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
        } else {
            tvTotalExpense.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))

        }
    }

}