package com.lapcevichme.olympiadfinder.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lapcevichme.olympiadfinder.presentation.navigation.AppNavGraph
import com.lapcevichme.olympiadfinder.presentation.navigation.BottomNavigationBar
import com.lapcevichme.olympiadfinder.presentation.viewmodel.MainViewModel
import com.lapcevichme.olympiadfinder.ui.theme.OlympiadFinderTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Основная Activity приложения, точка входа для UI.
 *
 * Аннотация [@AndroidEntryPoint] указывает, что в этой Activity могут быть внедрены зависимости Hilt.
 * Она устанавливает корневой Composable контент и управляет навигацией и темой приложения.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController: NavHostController = rememberNavController()

            val currentTheme by mainViewModel.theme.collectAsState()
            val animateTheme by mainViewModel.animateThemeChanges.collectAsState()
            val currentFont by mainViewModel.appFont.collectAsState()

            /**
             * Компонент [Crossfade] обеспечивает плавный переход между темами приложения.
             * Анимация перехода зависит от значения [animateTheme] из [MainViewModel].
             */
            Crossfade(
                targetState = currentTheme,
                animationSpec = if (animateTheme) {
                    tween(durationMillis = 500)
                } else {
                    snap()
                },
                label = "ThemeCrossfade"
            ) { themeState ->
                OlympiadFinderTheme(
                    dynamicTheme = themeState,
                    appFont = currentFont
                ) {
                    Scaffold(
                        bottomBar = { BottomNavigationBar(navController = navController) }
                    ) { paddingValues ->
                        AppNavGraph(navController = navController, paddingValues = paddingValues)
                    }
                }
            }
        }
    }
}