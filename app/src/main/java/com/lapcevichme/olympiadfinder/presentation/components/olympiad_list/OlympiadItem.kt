package com.lapcevichme.olympiadfinder.presentation.components.olympiad_list

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import java.time.LocalDate

@Composable
fun OlympiadItem(
    olympiad: Olympiad,
    onClick: () -> Unit,
    animate: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Анимируем scale только если общий флаг animate включен И элемент нажат
    val scale by animateFloatAsState(
        targetValue = if (isPressed && animate) 0.98f else 1.0f,
        label = "OlympiadItemScale",

        animationSpec = if (animate) tween(durationMillis = 100) else snap()
    )

    Card(
        onClick = onClick,
        // Применяем ПЕРЕДАННЫЙ модификатор первым, затем добавляем внутренние
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then( // Добавляем условный graphicsLayer, если animate включен
                if (animate) {
                    Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                } else {
                    Modifier // Если animate выключен, не применяем graphicsLayer
                }
            ),
        interactionSource = interactionSource,
        shape = MaterialTheme.shapes.medium
        // elevation = CardDefaults.cardElevation(...) // Можно добавить elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = olympiad.name, style = MaterialTheme.typography.headlineSmall)

            olympiad.subjects?.takeIf { it.isNotEmpty() }?.let { subjects ->
                Text(
                    text = "Предметы: ${subjects.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            } ?: Text(text = "Предметы: -", style = MaterialTheme.typography.bodyMedium)

            val gradeRange = when {
                olympiad.minGrade != null && olympiad.maxGrade != null -> "Классы: ${olympiad.minGrade} - ${olympiad.maxGrade}"
                olympiad.minGrade != null -> "Класс: от ${olympiad.minGrade}"
                olympiad.maxGrade != null -> "Класс: до ${olympiad.maxGrade}"
                else -> "Классы: -"
            }
            Text(text = gradeRange, style = MaterialTheme.typography.bodyMedium)

            olympiad.stages?.takeIf { it.isNotEmpty() }?.let { stages ->
                Text(
                    text = "Этапы: ${stages.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodySmall
                )
            } ?: Text(text = "Этапы: -", style = MaterialTheme.typography.bodySmall)

            olympiad.description?.takeIf { it.isNotBlank() }?.let { description ->
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            } ?: Text(text = "Описание отсутствует", style = MaterialTheme.typography.bodySmall)
        }
    }
}

/*
    ---- PREVIEWS ----
    (made by gemini <3)
 */

@Preview(showBackground = true)
@Composable
fun OlympiadItemPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 1L,
                name = "Математика Плюс",
                subjects = listOf(Subject("Математика"), Subject("Логика")),
                minGrade = 7,
                maxGrade = 11,
                stages = listOf(
                    Stage("Отборочный", LocalDate.of(2025, 10, 1), LocalDate.of(2025, 11, 15)),
                    Stage("Заключительный", LocalDate.of(2026, 3, 1), null)
                ),
                link = "https://mathplus.ru",
                description = "Олимпиада по математике для школьников 7-11 классов. Включает задания повышенной сложности.",
                keywords = "математика, олимпиада, школьники"
            ),
            onClick = {},
            animate = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OlympiadItemShortPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 2L,
                name = "Русский Медвежонок",
                subjects = listOf(Subject("Русский язык")),
                minGrade = 1,
                maxGrade = 11,
                stages = listOf(
                    Stage("Основной тур", LocalDate.of(2025, 11, 15), null)
                ),
                link = "https://rm.ru",
                description = "Международная олимпиада по русскому языку.",
                keywords = "русский язык, олимпиада, медвежонок"
            ),
            onClick = {},
            animate = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OlympiadItemNoGradesPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 3L,
                name = "Олимпиада без указания классов",
                subjects = listOf(Subject("Разные")),
                minGrade = null,
                maxGrade = null,
                stages = listOf(
                    Stage("Первый этап", LocalDate.now(), LocalDate.now().plusWeeks(2))
                ),
                link = null,
                description = "Описание олимпиады без указания минимального и максимального классов.",
                keywords = "разное, олимпиада"
            ),
            onClick = {},
            animate = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OlympiadItemNullableDataPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 3L,
                name = "Олимпиада с неполными данными",
                subjects = null,
                minGrade = null,
                maxGrade = 9,
                stages = null,
                link = null,
                description = null,
                keywords = null
            ),
            onClick = {},
            animate = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OlympiadItemEmptyListsPreview() {
    MaterialTheme {
        OlympiadItem(
            olympiad = Olympiad(
                id = 4L,
                name = "Олимпиада с пустыми списками",
                subjects = emptyList(),
                minGrade = 5,
                maxGrade = null,
                stages = emptyList(),
                link = "http://example.com",
                description = "",
                keywords = ""
            ),
            onClick = {},
            animate = true
        )
    }
}