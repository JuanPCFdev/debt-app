package com.example.im_a_rat.data.network

import com.example.im_a_rat.data.dto.TransactionDto
import com.example.im_a_rat.data.network.response.TransactionResponse
import com.example.im_a_rat.domain.model.TransactionModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class DatabaseRepository @Inject constructor(val firestore: FirebaseFirestore) {

    companion object {
        const val USER_COLLECTION = "rat"
        const val FIELD_DATE = "date"
    }

    fun getTransactions(): Flow<List<TransactionModel>> {
        return firestore.collection(USER_COLLECTION).orderBy(FIELD_DATE, Query.Direction.DESCENDING)
            .snapshots().map { qs ->
                qs.toObjects(TransactionResponse::class.java).mapNotNull { transactionResponse ->
                    transactionToDomain(transactionResponse)
                }
            }
    }

    private fun transactionToDomain(tr: TransactionResponse): TransactionModel? {
        if (tr.date == null || tr.amount == null || tr.id == null || tr.title == null) return null
        val date = timeStampToString(tr.date) ?: return null
        return TransactionModel(
            id = tr.id,
            title = tr.title,
            amount = tr.amount,
            date = date
        )
    }

    private fun timeStampToString(timestamp: Timestamp?): String? {
        timestamp ?: return null
        return try {
            val date = timestamp.toDate()
            val sdf = SimpleDateFormat("EEEE dd MMMM", Locale.getDefault())
            sdf.format(date)
        } catch (e: Exception) {
            null
        }
    }

    fun addTransaction(dto: TransactionDto) {
        val customId = getCustomId()
        val model = hashMapOf(
            "id" to customId,
            "title" to dto.title,
            "date" to dto.date,
            "amount" to dto.amount
        )
        firestore.collection(USER_COLLECTION).document(customId).set(model)
    }

    private fun getCustomId(): String {
        return Date().time.toString()
    }

    fun removeTransaction(id: String) {
        firestore.collection(USER_COLLECTION).document(id).delete()
    }

}