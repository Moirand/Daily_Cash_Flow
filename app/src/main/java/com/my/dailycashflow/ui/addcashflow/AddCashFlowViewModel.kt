package com.my.dailycashflow.ui.addcashflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.my.dailycashflow.data.Category
import com.my.dailycashflow.data.CashFlowSummaryByCategory
import com.my.dailycashflow.data.DataRepository
import com.my.dailycashflow.data.CashFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCashFlowViewModel(private val repository: DataRepository) : ViewModel() {

    val expenseCategories: LiveData<List<Category>> = repository.getExpenseTypeCategories()

    private val _categoryId: MutableLiveData<Int> = MutableLiveData(1)

    val cashFlowSummaryByCategory: LiveData<CashFlowSummaryByCategory> = _categoryId.switchMap {
        repository.getCashFlowSummaryByCategory(it)
    }

    fun insertCashFlow(data: CashFlow) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCashFlow(data)
            withContext(Dispatchers.Main) {
                _categoryId.value = data.categoryId
            }
        }
    }

}