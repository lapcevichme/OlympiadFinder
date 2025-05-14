package com.lapcevichme.olympiadfinder.presentation.components.olympiad_list.bottom_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.ui.theme.PreviewTheme

@Composable
fun OlympiadDetailsSheetContent(olympiad: Olympiad, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = olympiad.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Description:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = olympiad.description ?: "No description available",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        olympiad.subjects?.takeIf { it.isNotEmpty() }?.let { subjects ->
            Text(
                "Subjects:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            subjects.forEach { subject ->
                Text(
                    "- ${subject.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        olympiad.stages?.takeIf { it.isNotEmpty() }?.let { stages ->
            Text(
                "Stages:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            stages.forEach { stage ->
                Text(
                    "- ${stage.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val gradeText = when {
            olympiad.minGrade != null && olympiad.maxGrade != null -> "Grades: ${olympiad.minGrade} - ${olympiad.maxGrade}"
            olympiad.minGrade != null -> "Min Grade: ${olympiad.minGrade}"
            olympiad.maxGrade != null -> "Max Grade: ${olympiad.maxGrade}"
            else -> null
        }
        gradeText?.let {
            Text(
                it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
        }


        olympiad.link?.let {
            Text(
                "Link:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            ) // Можно сделать кликабельной ссылкой
            Spacer(modifier = Modifier.height(8.dp))
        }
        olympiad.keywords?.let {
            Text(
                "Keywords:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
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
fun PreviewOlympiadDetailsSheetContent_FullData() {
    PreviewTheme { // Используем обертку с параметрами по умолчанию
        val sampleOlympiad = Olympiad(
            id = 1,
            name = "Всероссийская олимпиада школьников по информатике (ВсОШ)",
            description = "Самая главная олимпиада для школьников по программированию, проводится по всей России в несколько этапов.",
            minGrade = 9,
            maxGrade = 11,
            subjects = listOf(
                Subject(name = "Информатика"),
                Subject(name = "Программирование")
            ), // Используем твою модель Subject
            stages = listOf(
                Stage(name = "Школьный", startDate = null, endDate = null),
                Stage(name = "Муниципальный", startDate = null, endDate = null),
                Stage(name = "Региональный", startDate = null, endDate = null),
                Stage(name = "Заключительный", startDate = null, endDate = null)
            ), // Используем твою модель Stage
            link = "[https://vserosolymp.ru/about/inf](https://vserosolymp.ru/about/inf)",
            keywords = "ВсОШ, информатика, программирование, школьники"
        )
        OlympiadDetailsSheetContent(olympiad = sampleOlympiad, onClose = {})
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Theme"
)
@Composable
fun PreviewOlympiadDetailsSheetContent_MinimalData() {
    PreviewTheme { // Используем обертку с параметрами по умолчанию
        val sampleOlympiad = Olympiad(
            id = 2,
            name = "Простая олимпиада",
            description = null,
            minGrade = null,
            maxGrade = null,
            subjects = emptyList(),
            stages = emptyList(),
            link = null,
            keywords = null
        )
        OlympiadDetailsSheetContent(olympiad = sampleOlympiad, onClose = {})
    }
}