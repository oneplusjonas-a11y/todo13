package com.grid.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.grid.app.ui.GridApp
import com.grid.app.ui.GridViewModel
import com.grid.app.ui.theme.GridTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GridViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GridTheme {
                GridApp(viewModel = viewModel)
            }
        }
    }
}
