package com.android.example.myapplication4.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.example.myapplication4.CentralViewModel


@Entity(tableName = "transaction_history")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    var transactionId: Long = 0L,
    @ColumnInfo(name = "income")
    var income: Boolean = false,
    @ColumnInfo(name = "expense")
    var expense: Boolean= false,
    @ColumnInfo(name = "amount")
    var amount: Int = 0,
    @ColumnInfo(name="finalincome")
    var finalIncome:Int?= 0,
    @ColumnInfo(name="finalexpense")
    var finalExpense:Int?= 0,
    @ColumnInfo(name="finalbalance")
    var finalBalance:Int?= 0,
    @ColumnInfo(name="COMMENT")
    var commentString:String?="",
    @ColumnInfo(name="dateTime")
    var dateTime:String?=""
//    @ColumnInfo(name="dateTime")
//    var dateTimes:String?=""



)