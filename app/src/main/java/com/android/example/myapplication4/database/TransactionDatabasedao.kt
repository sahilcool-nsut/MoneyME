package com.android.example.myapplication4.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDatabasedao
{
    @Insert
    fun insert(transaction: Transaction)
    @Update
    fun update(transaction: Transaction)
    @Query(("SELECT * from transaction_history WHERE transactionId = :key"))
    fun getCurrentByKey(key: Long): Transaction?
    @Query("SELECT * FROM transaction_history ORDER BY transactionId DESC LIMIT 1")
    fun getCurrent(): Transaction?

    /*@Query("SELECT * FROM transaction_history WHERE amount != 0 ORDER BY transactionId DESC LIMIT 1")
    fun getCurrent2(): Transaction?*/

    @Query("SELECT * FROM transaction_history ORDER BY transactionId DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("DELETE FROM transaction_history")
    fun clear()
}
