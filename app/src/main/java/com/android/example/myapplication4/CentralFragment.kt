package com.android.example.myapplication4

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.android.example.myapplication4.database.TransactionDatabase
import com.android.example.myapplication4.databinding.FragmentCentralBinding
import kotlinx.android.synthetic.main.fragment_central.*


/**
 * A simple [Fragment] subclass.
 */
class CentralFragment : Fragment() {
    private lateinit var mLayout:LinearLayout
    private lateinit var mEditText:EditText
    private lateinit var binding : FragmentCentralBinding
    private lateinit var viewModel: CentralViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_central,container,false)
       /* binding.addedValue.setOnClickListener {
            addValue()
        }*/
        binding.doneButton.setOnClickListener {
            showSelectionToast()
        }
        binding.transactionButton.setOnClickListener {                         // Add Transaction ka popup menu
            val popupMenu: PopupMenu = PopupMenu(context,binding.transactionButton)
            popupMenu.menuInflater.inflate(R.menu.menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.add_income ->
                    {
                        Log.i("CentralFragment","Before Incomeaddedvalue")
                        //binding.incomeValue.setText(10.toString())
                        addValue()                                             // To show the empty box and done button
                        Log.i("CentralFragment","After Incomeaddedvalue2")
                        binding.transactionButton.text=getString(R.string.income)
                        binding.doneButton.setOnClickListener{
                            donePressed("Income")                        // To take action on done
                        }

                    }

                    R.id.add_expense ->
                    {
                        //Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                        Log.i("CentralFragment","Before Eaddedvalue")
                        //binding.incomeValue.setText(10.toString())
                        addValue()
                        binding.transactionButton.text=getString(R.string.expense)
                        binding.doneButton.setOnClickListener{
                            donePressed("Expense")                        // To take action on done

                        }
                        Log.i("CentralFragment","Before Eaddedvalue")

                    }

                }
                true
            })
            popupMenu.show()
        }



       // mLayout=binding.linearLayout2
        mEditText=binding.addedValue
        val application = requireNotNull(this.activity).application                           //For creation fo Database
        val dataSource = TransactionDatabase.getInstance(application).transactionDatabaseDao
        val viewModelFactory = CentralViewModelFactory(dataSource, application)
         viewModel= ViewModelProviders.of(                                                  //Creation of ViewModel
            this,viewModelFactory).get(CentralViewModel::class.java)
        binding.centralViewModel=viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        Log.i("CentralFragment",viewModel.income.toString())
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return NavigationUI.onNavDestinationSelected(
//            item,
//            requireView().findNavController())
//                || super.onOptionsItemSelected(item)
//    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.aboutFragment-> {
                NavigationUI.onNavDestinationSelected(
                    item,
                    requireView().findNavController()
                )
                true
            }

            R.id.clearAllDataOption -> {
                onClearAllData()
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }


    fun showSelectionToast()
    {
        Toast.makeText(context,"Please select Transaction Type",Toast.LENGTH_SHORT).show()
    }
    fun onClearAllData(){
        viewModel.onClearAllData()
    }
    /*private fun onClick(action:String)
    {

        mLayout.addView(createNewTextView("You just $action ${mEditText.getText().toString()} "))

    }*/

    /*private fun createNewTextView(text: String): TextView? {
        val lparams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val textView = TextView(context)
        textView.layoutParams = lparams
        textView.freezesText=true
        textView.text = "New transaction: $text"
        return textView
    }*/


    private fun onAddIncome(added:Int)                          //calling ViewModel's add Income
    {
        viewModel.onAddIncome(added)

    }

    private fun onAddExpense(added:Int)                         //calling ViewModel's add Expense
    {
        viewModel.onAddExpense(added)
    }


    // IS UPDATED DIRECTLY BY VIEW MODEL
    /*private fun updateIncomeText()
    {
        binding.incomeValue.text=viewModel.income.value.toString()
    }
    private fun updateExpenseText()
    {
        binding.expenseValue.text=viewModel.expense.value.toString()
    }
    private fun updateBalanceText()
    {
        binding.balanceValue.text=viewModel.balance.value.toString()
    }*/

    private fun addValue()                          //Called by popup menu
    {
        binding.addedValue.visibility=View.VISIBLE
        binding.doneButton.visibility=View.VISIBLE
        binding.addedComment.visibility=View.VISIBLE
    }

    private fun donePressed(type: kotlin.String)        //Called by popup menu
    {
        Log.i("CentralFragment","InsideDone1")

        val number = addedValue.text.toString().toIntOrNull()               //Takes text from editText and checks if it is integer
        val isInteger = number != null
        val addedCommentTemp=binding.addedComment.text.toString()
        if(!isInteger||(TextUtils.isEmpty(addedValue.text.toString())))
        {
            Toast.makeText(context,"Enter a number!",LENGTH_SHORT).show()    //Prompts to add a number

        }
        else {

            val temp = binding.addedValue.text.toString()

            Log.i("CentralFragment", "InsideDone2")
            val finalvalue = Integer.parseInt(temp)
            if (type == "Income") {
                onAddIncome(finalvalue)                                     //for ViewModel addIncome
                viewModel.onDoneTracking("added", finalvalue,addedCommentTemp)        //For updating database with current values
                Log.i("CentralFragment", "InsideDone3")
            } else {
                onAddExpense(finalvalue)
                viewModel.onDoneTracking("spent", finalvalue,addedCommentTemp)
            }
            Log.i("CentralFragment", "InsideDone2")

            binding.addedValue.text = null
            binding.addedComment.text = null
            binding.transactionButton.text=getString(R.string.transaction_button)
            binding.doneButton.setOnClickListener {
                showSelectionToast()
            }
            view?.let { activity?.hideKeyboard(it) }
        }

    }
    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}



