package com.lapcevichme.olympiadfinder.presentation.components

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
import com.lapcevichme.olympiadfinder.domain.model.AppError
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme

/**
 * Компонент для отображения различных состояний ошибок в UI.
 *
 * @param error Состояние ошибки, которое необходимо отобразить. Может быть null, если ошибки нет.
 * @param onRetryClicked Колбэк, вызываемый при нажатии кнопки "Повторить".
 * @param modifier Модификатор для настройки контейнера ошибки.
 */
@Composable
fun ErrorDisplay(
    error: AppError?,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Если ошибки нет, компонент ничего не отображает.
    if (error == null) {
        return
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (error) {
            is AppError.NetworkError -> {
                Text(
                    "Нет подключения к интернету.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            is AppError.ServerError -> {
                Text(
                    "Не удалось загрузить олимпиады.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                error.message?.let {
                    Text(
                        "Детали: $it",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is AppError.DataError -> {
                Text(
                    "Ошибка данных: ${error.message ?: "Некорректные данные."}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            is AppError.NotFoundError -> {
                Text(
                    "Ресурс не найден.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            is AppError.UnknownError -> {
                Text(
                    "Произошла неизвестная ошибка: ${error.message ?: "Пожалуйста, попробуйте снова."}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            // TODO: Добавить другие ветки when для новых типов AppError
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetryClicked) {
            Text("Повторить")
        }
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
                error = AppError.NetworkError,
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
                error = AppError.ServerError("HTTP 500: Internal Server Error"),
                onRetryClicked = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}