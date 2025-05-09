package com.lapcevichme.olympiadfinder.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lapcevichme.olympiadfinder.presentation.viewmodel.ErrorState
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme

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
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Цвет текста ошибки уже был MaterialTheme.colorScheme.error
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
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Не удалось загрузить олимпиады.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                errorState.message?.let {
                    Text(
                        "Детали: $it",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

/*
    ---- PREVIEWS ----
 */

@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewErrorDisplayNetwork() {
    PreviewTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ErrorDisplay(
                errorState = ErrorState.NetworkError,
                onRetryClicked = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewErrorDisplayServer() {
    PreviewTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ErrorDisplay(
                errorState = ErrorState.ServerError("HTTP 500: Internal Server Error"),
                onRetryClicked = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}