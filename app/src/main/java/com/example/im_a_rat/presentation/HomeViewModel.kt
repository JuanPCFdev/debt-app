package com.example.im_a_rat.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.im_a_rat.data.dto.TransactionDto
import com.example.im_a_rat.data.network.DatabaseRepository
import com.example.im_a_rat.domain.model.TransactionModel
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("DefaultLocale")
@HiltViewModel
class HomeViewModel @Inject constructor(val databaseRepository: DatabaseRepository) : ViewModel() {

    var _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            databaseRepository.getTransactions().collect { transactions ->
                _uiState.update { uiState ->
                    uiState.copy(
                        transactions = transactions,
                        totalAmount = String.format("%.2f$", transactions.sumOf { it.amount })
                    )
                }
            }
        }
    }

    fun onAddTransactionSelected() {
        _uiState.update { it.copy(showAddTransactionDialog = true) }
    }

    fun dismissShowAddTransactionDialog() {
        _uiState.update { it.copy(showAddTransactionDialog = false) }
    }

    fun addTransaction(title: String, amount: String, date: Long?) {
        val dto = prepareDTO(title, amount, date)
        if (dto != null) {
            viewModelScope.launch {
                databaseRepository.addTransaction(dto)
            }
        }
        dismissShowAddTransactionDialog()
    }

    private fun prepareDTO(title: String, amount: String, date: Long?): TransactionDto? {
        if (title.isBlank() || amount.isBlank()) return null
        val timestamp = if (date != null) {
            val seconds = date / 1000
            val nanoseconds = ((date % 1000) * 1000000).toInt()
            Timestamp(seconds, nanoseconds)
        } else {
            Timestamp.now()
        }

        return try {
            TransactionDto(title, amount.toDouble(), timestamp)
        } catch (e: Exception) {
            null
        }

    }

    fun onItemRemove(id: String) {
        databaseRepository.removeTransaction(id)
    }

}

data class HomeUiState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionModel> = emptyList(),
    val totalAmount: String = "",
    val showAddTransactionDialog: Boolean = false
)