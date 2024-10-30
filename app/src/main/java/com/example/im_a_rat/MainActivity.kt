package com.example.im_a_rat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.im_a_rat.presentation.HomeScreen
import com.example.im_a_rat.presentation.HomeViewModel
import com.example.im_a_rat.ui.theme.Im_A_RatTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Im_A_RatTheme {
                val homeViewModel by viewModels<HomeViewModel>()
                HomeScreen(homeViewModel)
            }
        }
    }
}

