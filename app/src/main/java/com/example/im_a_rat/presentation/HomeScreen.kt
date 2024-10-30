package com.example.im_a_rat.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.im_a_rat.R
import com.example.im_a_rat.domain.model.TransactionModel
import com.example.im_a_rat.ui.theme.Gray
import com.example.im_a_rat.ui.theme.Purple40
import com.example.im_a_rat.ui.theme.Purple80
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {

    val uiState by homeViewModel.uiState.collectAsState()

    if (uiState.showAddTransactionDialog) {
        AddTransactionDialog(
            onDismiss = {
                homeViewModel.dismissShowAddTransactionDialog()
            },
            onTransactionAdded = { title, amount, date ->
                if (date != null) {
                    homeViewModel.addTransaction(title, amount, date + 86400000L)
                }
            }
        )
    }

    Column {
        Text(
            text = "Hello Rat",
            fontSize = 24.sp,
            color = Gray,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp)
        )
        Balance(uiState.isLoading, uiState.totalAmount) { homeViewModel.onAddTransactionSelected() }
        Text(
            text = "Recent Transaction",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Transactions(uiState.transactions, onItemRemove = { homeViewModel.onItemRemove(it) })
    }
}

@Composable
fun Transactions(transactions: List<TransactionModel>, onItemRemove: (String) -> Unit) {
    LazyColumn {
        items(transactions) { transaction ->
            val swipeLeft = SwipeAction(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                    )
                },
                background = Color.Red,
                isUndo = true,
                onSwipe = {
                    onItemRemove(transaction.id)
                }
            )

            SwipeableActionsBox(endActions = listOf(swipeLeft)) {
                TransactionItem(transaction)
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TransactionItem(transaction: TransactionModel) {
    Row(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_money),
            contentDescription = "",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column() {
            Text(text = transaction.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = transaction.date, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(text = String.format("%.2f$", transaction.amount), color = Color.Red)
    }
}

@Composable
fun Balance(isLoading: Boolean, totalAmount: String, onAddTransactionSelected: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Purple80, Purple40)))
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Owe", color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = totalAmount,
                            fontSize = 28.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onAddTransactionSelected() }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}