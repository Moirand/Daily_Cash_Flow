package com.my.dailycashflow.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.my.dailycashflow.util.CashFlowFilterType
import com.my.dailycashflow.util.FilterUtils
import java.util.concurrent.Executors

class DataRepository(private val cashFlowDao: CashFlowDao) {

    fun getCashFlowSummary(): LiveData<List<CashFlowSummaryByCategory>> {
        return cashFlowDao.getCashFlowSummary()
    }

    fun getRecentCashFlow() = cashFlowDao.getRecentCashFlow()

    fun getExpensesSummaryByCategory(): LiveData<List<ExpensesSummaryByCategory>> {
        return cashFlowDao.getExpensesSummaryByCategory()
    }

    fun getCashFlowsByType(filterType: CashFlowFilterType): LiveData<PagingData<CashFlowWithCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30
            )
        ) {
            val query = FilterUtils.getFilterQuery(filterType)
            cashFlowDao.getAllCashFlowByType(query)
        }.liveData
    }

    fun getExpensesByCategory(categoryId: Int): LiveData<PagingData<CashFlowWithCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30
            )
        ) {
            cashFlowDao.getExpensesByCategory(categoryId)
        }.liveData
    }

    fun getExpenseTypeCategories(): LiveData<List<Category>> {
        return cashFlowDao.getExpenseTypeCategories()
    }

    fun insertCashFlow(data: CashFlow) {
        executeThread {
            cashFlowDao.insertCashFlow(data)
        }
    }

    fun deleteCashFlow(data: CashFlow) {
        executeThread {
            cashFlowDao.deleteCashFlow(data)
        }
    }

    fun getCashFlowSummaryByCategory(categoryId: Int): LiveData<CashFlowSummaryByCategory> {
        return cashFlowDao.getCashFlowSummaryByCategory(categoryId)
    }

    private fun executeThread(f: () -> Unit) {
        Executors.newSingleThreadExecutor().execute(f)
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(context: Context): DataRepository {
            return instance ?: synchronized(DataRepository::class.java) {
                if (instance == null) {
                    val database = CashFlowDatabase.getInstance(context)
                    instance = DataRepository(database.cashFlowDao())

                }
                return instance as DataRepository
            }
        }
    }

}