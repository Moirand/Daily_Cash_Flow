package com.my.dailycashflow.ui.addcashflow

import android.app.DatePickerDialog
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.textfield.TextInputEditText
import com.my.dailycashflow.R
import com.my.dailycashflow.data.CashFlow
import com.my.dailycashflow.data.Category
import com.my.dailycashflow.data.CashFlowSummaryByCategory
import com.my.dailycashflow.notification.NotificationWorker
import com.my.dailycashflow.util.*
import java.text.SimpleDateFormat
import java.util.*

class AddCashFlowActivity : AppCompatActivity() {

    private lateinit var viewModel: AddCashFlowViewModel

    private lateinit var spinnerType: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var adapterCategory: ArrayAdapter<Category>
    private var categorySelected: Category? = null

    private lateinit var edAmount: TextInputEditText
    private lateinit var edDate: TextInputEditText
    private lateinit var edInformation: TextInputEditText

    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cashflow)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.add_cashFlow)

        val factory = DataViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory).get(AddCashFlowViewModel::class.java)

        viewModel.cashFlowSummaryByCategory.observe(this, Observer(this::setUpWorkManager))

        spinnerCategory = findViewById(R.id.spinner_category)
        edAmount = findViewById(R.id.ed_amount)
        edInformation = findViewById(R.id.ed_information)
        edDate = findViewById(R.id.ed_date)
        edDate.setOnClickListener {
            showDatePicker()
        }

        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.list_type,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType = findViewById(R.id.spinner_type)
        spinnerType.adapter = spinnerAdapter

        spinnerType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 1) {
                        spinnerCategory.visibility = View.GONE
                        categorySelected = Category(1, "Income", 0, INCOME)
                    } else {
                        spinnerCategory.visibility = View.VISIBLE
                        viewModel.expenseCategories.observe(this@AddCashFlowActivity) {
                            setExpenseCategories(it)
                        }
                    }
                }
            }

        btnSave = findViewById(R.id.btn_save)
        btnSave.setOnClickListener {
            if (isFieldInputFilled()) {
                viewModel.insertCashFlow(
                    CashFlow(
                        amount = edAmount.text.toString().toInt(),
                        information = edInformation.text.toString(),
                        dateInMillis = edDate.text.toString().convertDateToMillis(),
                        categoryId = categorySelected?.id!!
                    )
                )
            }
        }
    }

    private fun isFieldInputFilled(): Boolean {
        val checkAmountInput = edAmount.text?.isBlank() == false
        val checkDateInput = edDate.text?.isBlank() == false
        val checkInformationInput = edInformation.text?.isBlank() == false

        return checkAmountInput && checkDateInput && checkInformationInput
    }

    private fun setExpenseCategories(expenseCategories: List<Category>) {
        adapterCategory = ArrayAdapter(this, android.R.layout.simple_list_item_1, expenseCategories)
        spinnerCategory.apply {
            adapter = adapterCategory
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    categorySelected = expenseCategories[position]
                }
            }
        }
    }

    private fun setUpWorkManager(cashFlowSummaryByCategory: CashFlowSummaryByCategory) {
        categorySelected?.let { category ->
            if (cashFlowSummaryByCategory.total >= category.limit
                && cashFlowSummaryByCategory.category.type == EXPENSE
            ) {
                val data = Data.Builder()
                    .putInt(CATEGORY_ID, category.id)
                    .putString(CATEGORY_NAME, category.name)
                    .putInt(CATEGORY_LIMIT, category.limit)
                    .putInt(CATEGORY_TOTAL, cashFlowSummaryByCategory.total)
                    .build()
                val workManager = WorkManager.getInstance(this)
                val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setInputData(data)
                    .build()

                workManager.enqueueUniqueWork(
                    NOTIF_UNIQUE_WORK,
                    ExistingWorkPolicy.APPEND,
                    workRequest
                )
            }
            finish()
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val yearInit = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val format = "dd-MMM-yyyy"
                val sdf = SimpleDateFormat(format, Locale.US)
                edDate.setText(sdf.format(cal.timeInMillis))
            },
            yearInit,
            month,
            day
        )
        dpd.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}