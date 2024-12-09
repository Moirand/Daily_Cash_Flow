package com.my.dailycashflow.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.my.dailycashflow.data.CashFlow
import com.my.dailycashflow.data.DataRepository
import com.my.dailycashflow.data.ExpensesSummaryByCategory
import com.my.dailycashflow.data.CashFlowSummaryByCategory

class MainViewModel(repository: DataRepository) : ViewModel() {

    val getCashFlowSummaryByCategory: LiveData<List<CashFlowSummaryByCategory>> = repository.getCashFlowSummary()

    val getRecentCashFlow: LiveData<CashFlow> = repository.getRecentCashFlow()

    val getExpensesSummaryByCategory: LiveData<List<ExpensesSummaryByCategory>> = repository.getExpensesSummaryByCategory()

}