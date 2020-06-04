package com.android.example.myapplication4

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.android.example.myapplication4.database.Transaction
import com.android.example.myapplication4.database.TransactionDatabasedao
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class CentralViewModel (
    val database: TransactionDatabasedao,
    application: Application
) : AndroidViewModel(application) {
    private val _income=MutableLiveData<Int>()                          //Live Data declarations
    val income: LiveData<Int>
    get()=_income

    private val _expense=MutableLiveData<Int>()
    val expense: LiveData<Int>
        get()=_expense

    private val _balance=MutableLiveData<Int>()
    val balance: LiveData<Int>
        get()=_balance

    private var viewModelJob = Job()                                //Starting job
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val transactions = database.getAllTransactions()                       //For getting and printing all transactions
    val transactionsString = Transformations.map(transactions) { transactions ->
        formatNights(transactions, application.resources)
    }

    var current = MutableLiveData<Transaction?>()
    init {
        Log.i("CentralViewModel", current.value?.finalBalance.toString())
        initializeCurrent()

        Log.i("CentralViewModel","Hello")
        Log.i("CentralViewModel", current.value?.finalBalance.toString())
    }

    private fun initializeCurrent() {
        uiScope.launch {
            //val newTransaction = Transaction()
           // Log.i("CentralViewModel", current.value?.finalBalance.toString())
            //insert(newTransaction)
            current.value = getCurrentFromDatabase()
            if(current.value!=null){                                   //To get values of income expense and balance on app start
                _income.value=current.value?.finalIncome
                _expense.value=current.value?.finalExpense
                _balance.value=current.value?.finalBalance
                Log.i("CentralViewModel","inside initializeCurrent")
            }
            Log.i("CentralViewModel", current.value?.finalBalance.toString())
        }
    }
    /*private suspend fun getCurrentFromDatabase2(): Transaction? {
        return withContext(Dispatchers.IO) {
            var current = database.getCurrent2()
            if (current?.amount==0) {
                current = null
            }
            current
        }
    }*/
    private suspend fun getCurrentFromDatabase(): Transaction? {                     //initializeCurrent utility function
        return withContext(Dispatchers.IO) {
            var current = database.getCurrent()
            if (current?.amount==0) {
                current = null
            }
            current
        }
    }

    //var incomeString=current.value?.finalIncome.toString()
    init                                                            //viewModel Init function
    {
        //initializeCurrent()
        if(current.value!=null){
            _income.value=current.value?.finalIncome
            _expense.value=current.value?.finalExpense
            _balance.value=current.value?.finalBalance
            Log.i("CentralViewModel","CreatedViewModel")
        }
        else{
            _income.value=0
            _expense.value=0
            _balance.value=0
            Log.i("CentralViewModel2","CreatedViewModel2")
        }


    }

    fun onClear() {
        uiScope.launch {
            clear()
            if(current.value!=null) {


                //not clearing current to save income expense and balance

                //so that last record can be saved for app restart
                if (current.value?.income!!) {
                    current.value?.amount?.let {
                        current.value?.commentString?.let { it1 ->
                            onDoneTracking(
                                "added", it,
                                it1
                            )
                        }
                    }
                } else {
                    current.value?.amount?.let {
                        current.value?.commentString?.let { it1 ->
                            onDoneTracking(
                                "spent", it,
                                it1
                            )
                        }
                    }
                }
            }
        }
    }
    fun onClearAllData(){
        uiScope.launch{
           /* current.value?.finalBalance=0
            current.value?.finalIncome=0
            current.value?.finalExpense=0
            _income.value=0
            _expense.value=0
            _balance.value=0*/

            clear()
            initializeCurrent()
            _income.value=0
            _expense.value=0
            _balance.value=0
            Log.i("CentralViewModel","${current.value?.finalIncome}")


        }
    }
    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    fun onAddIncome(added:Int)
    {
        _income.value =(income.value)?.plus(added)
        updateBalance()

    }

    fun onAddExpense(added:Int)
    {
        _expense.value =(expense.value)?.plus(added)
        updateBalance()
    }

    fun updateBalance()
    {
        _balance.value= (expense.value)?.let { (income.value)?.minus(it) }

    }

    fun onDoneTracking(action:String,added:Int,comment:String) {                       //For Storing in database, called from fragment
        if(added!=0){
            uiScope.launch {
                val newTransaction = Transaction()                //initializes object
                newTransaction.amount=added                       //adding all current values
                if(action=="added"){
                    newTransaction.income=true
                    newTransaction.expense=false
                }
                else{
                    newTransaction.expense=true
                    newTransaction.income=false
                }
                newTransaction.finalIncome=income.value
                newTransaction.finalExpense=expense.value
                newTransaction.finalBalance=balance.value
                newTransaction.commentString=comment
                val date = getCurrentDateTime()
                val dateInString = date.toString("dd-MMMM-yyyy")
                newTransaction.dateTime=dateInString
                insert(newTransaction)                                    //calls insert ( utility )

                /*current.value.?income=newTransaction.income
                current.value.expense=newTransaction.expense
                current.value.finalBalance=newTransaction.finalBalance
                current*/
                Log.i("CentralViewModel", current.value?.finalBalance.toString())
                current.value = getCurrentFromDatabase()
                Log.i("CentralViewModel", current.value?.finalBalance.toString())
               // current.value.finalBalance+=newTransaction.
            }
        }


    }
    private suspend fun insert(transaction: Transaction) {                //inserts in database
        withContext(Dispatchers.IO) {
            database.insert(transaction)
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()                           //stops all jobs
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

}

