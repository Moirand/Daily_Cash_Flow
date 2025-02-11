package com.my.dailycashflow.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.my.dailycashflow.util.QUERY_EXPENSES_SUMMARY_BY_CATEGORY

@Dao
interface CashFlowDao {

    @Query("SELECT *, sum(amount) total FROM cashflow JOIN category ON cashflow.category_id = category.id GROUP BY category.type")
    fun getCashFlowSummary() : LiveData<List<CashFlowSummaryByCategory>>

    @Transaction
    @Query("SELECT * FROM cashflow ORDER BY dateInMillis DESC, id DESC LIMIT 1")
    fun getRecentCashFlow() : LiveData<CashFlow>

    @Transaction
    @Query(QUERY_EXPENSES_SUMMARY_BY_CATEGORY)
    fun getExpensesSummaryByCategory(): LiveData<List<ExpensesSummaryByCategory>>

    @Transaction
    @RawQuery(observedEntities = [CashFlow::class, Category::class])
    fun getAllCashFlowByType(query: SupportSQLiteQuery): PagingSource<Int, CashFlowWithCategory>

    @Transaction
    @Query("SELECT * FROM cashflow WHERE category_id = :categoryId")
    fun getExpensesByCategory(categoryId:Int) : PagingSource<Int, CashFlowWithCategory>

    @Query("SELECT * FROM category ORDER BY name ASC")
    fun getExpenseTypeCategories() : LiveData<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCashFlow(data:CashFlow) : Long

    @Delete
    fun deleteCashFlow(data: CashFlow)

    @Query("SELECT *,sum(amount) AS total FROM cashflow WHERE category_id = :categoryId")
    fun getCashFlowSummaryByCategory(categoryId: Int):LiveData<CashFlowSummaryByCategory>

}