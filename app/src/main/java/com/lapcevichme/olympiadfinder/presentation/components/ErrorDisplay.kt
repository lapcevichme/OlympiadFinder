package com.lapcevichme.olympiadfinder.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lapcevichme.olympiadfinder.presentation.viewmodel.ErrorState // Импортируем состояние ошибки

@Composable
fun ErrorDisplay(
    errorState: ErrorState, // Принимаем состояние ошибки
    onRetryClicked: () -> Unit, // Принимаем колбэк для повтора
    modifier: Modifier = Modifier // Модификатор для настройки контейнера ошибки
) {
    when (errorState) {
        is ErrorState.NoError -> {
            // Если нет ошибки, этот компонент ничего не отображает сам по себе.
        }
        is ErrorState.NetworkError -> {
            Column(
                modifier = modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Нет подключения к интернету.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetryClicked) { // Используем переданный колбэк
                    Text("Повторить")
                }
            }
        }
        is ErrorState.ServerError -> {
            Column(
                modifier = modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Не удалось загрузить олимпиады.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                errorState.message?.let {
                    Text("Детали: $it", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetryClicked) { // Используем переданный колбэк
                    Text("Повторить")
                }
            }
        }
        // TODO: Добавить другие ветки when
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorDisplayNetwork() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ErrorDisplay(errorState = ErrorState.NetworkError, onRetryClicked = {}, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorDisplayServer() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ErrorDisplay(errorState = ErrorState.ServerError("HTTP 500: Internal Server Error"), onRetryClicked = {}, modifier = Modifier.fillMaxWidth())
        }
    }
}