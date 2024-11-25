package com.dorkytiger.top

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.dorkytiger.top.navigation.SetupNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    val navController = rememberNavController()
    MaterialTheme {
        SetupNavGraph(navController)
    }

}