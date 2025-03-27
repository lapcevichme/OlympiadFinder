package com.lapcevichme.olympiadfinder.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.lapcevichme.olympiadfinder.presentation.navigation.AppNavGraph
import com.lapcevichme.olympiadfinder.presentation.navigation.BottomNavigationBar
import com.lapcevichme.olympiadfinder.ui.theme.OlympiadFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OlympiadFinderTheme {

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