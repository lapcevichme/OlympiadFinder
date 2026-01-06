package com.lapcevichme.olympiadfinder.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class, представляющий элемент Bottom Navigation Bar.
 *
 * @property name Название элемента, отображаемое пользователю.
 * @property route Маршрут графа, к которому относится этот элемент навигации.
 * @property icon Иконка, отображаемая для элемента навигации.
 */
data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)