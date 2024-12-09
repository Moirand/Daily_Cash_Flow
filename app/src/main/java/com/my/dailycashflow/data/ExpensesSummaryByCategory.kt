package com.my.dailycashflow.data

import androidx.room.Embedded
import java.io.Serializable

data class ExpensesSummaryByCategory(
    @Embedded
    val category: Category,
    val total: Int
): Serializable