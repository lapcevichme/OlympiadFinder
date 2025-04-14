package com.lapcevichme.olympiadfinder.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.lapcevichme.olympiadfinder.presentation.navigation.AppNavGraph
import com.lapcevichme.olympiadfinder.presentation.navigation.BottomNavigationBar
import com.lapcevichme.olympiadfinder.presentation.viewmodel.MainViewModel
import com.lapcevichme.olympiadfinder.ui.theme.OlympiadFinderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val currentTheme by mainViewModel.theme.collectAsState()

            OlympiadFinderTheme (
                dynamicTheme = currentTheme
            ){

                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { paddingValues ->

                    /*
                        ---- IMPLEMENTING BOTTOM NAVIGATION ----
                    */

                    AppNavGraph(navController = navController, paddingValues = paddingValues)

                }
            }
        }
    }
}