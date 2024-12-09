package com.my.dailycashflow.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.my.dailycashflow.R
import com.my.dailycashflow.data.CashFlow
import com.my.dailycashflow.data.CashFlowSummaryByCategory
import com.my.dailycashflow.data.ExpensesSummaryByCategory
import com.my.dailycashflow.ui.allcashflows.AllCashFlowsActivity
import com.my.dailycashflow.ui.customview.AllSummarySection
import com.my.dailycashflow.ui.expensessummary.ExpensesSummaryByCategoryAdapter
import com.my.dailycashflow.ui.listexpenses.ListExpensesByCategoryActivity
import com.my.dailycashflow.ui.setting.SettingActivity
import com.my.dailycashflow.util.CATEGORY_ID
import com.my.dailycashflow.util.DataViewModelFactory
import com.my.dailycashflow.util.convertLongToTime
import com.my.dailycashflow.util.formatToIDR

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapterExpensesSummaryByCategory: ExpensesSummaryByCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory = DataViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        findViewById<Button>(R.id.btn_all_cashflows).setOnClickListener {
            startActivity(Intent(this@MainActivity, AllCashFlowsActivity::class.java))
        }

        findViewById<ImageView>(R.id.btn_setting).setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingActivity::class.java))
        }

        viewModel.getCashFlowSummaryByCategory.observe(this) {
            showCashFlowSummary(it)
        }

        viewModel.getRecentCashFlow.observe(this) {
            recentCashFlow(it)
        }

        adapterExpensesSummaryByCategory = ExpensesSummaryByCategoryAdapter (
            onClick = { expensesSummaryByCategory ->
                val intent = Intent(this@MainActivity, ListExpensesByCategoryActivity::class.java)
                intent.putExtra(CATEGORY_ID, expensesSummaryByCategory.category.id)
                startActivity(intent)
            }
        )

        with(findViewById<RecyclerView>(R.id.recyclerview_expenses_by_category)) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = adapterExpensesSummaryByCategory
        }

        viewModel.getExpensesSummaryByCategory.observe(this) { list ->
            adapterExpensesSummaryByCategory.submitList(list) {
                adapterExpensesSummaryByCategory.notifyDataSetChanged()
            }
        }
    }

    private fun recentCashFlow(data: CashFlow) {
        val viewGroup = findViewById<CardView>(R.id.vg_recentCashFlow)

        val textDate = viewGroup.findViewById<TextView>(R.id.text_date)
        val textInformation = viewGroup.findViewById<TextView>(R.id.text_information)
        val textPrice = viewGroup.findViewById<TextView>(R.id.text_price)

        textDate.text = data.dateInMillis.convertLongToTime()
        textInformation.text = data.information
        textPrice.text = data.amount.formatToIDR()
    }

    private fun showCashFlowSummary(data: List<CashFlowSummaryByCategory>) {
        val expenses = data[0].total
        val income = data[1].total
        findViewById<AllSummarySection>(R.id.all_summary_section).setSummaryData(
            income = income.formatToIDR(),
            expenses = expenses.formatToIDR(),
            balance = (income - expenses).formatToIDR()
        )
    }
}