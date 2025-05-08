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
import androidx.compose.ui.unit.dp
import com.lapcevichme.olympiadfinder.domain.model.Olympiad

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
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Description:", style = MaterialTheme.typography.titleMedium)
        Text(text = olympiad.description ?: "No description available")
        Spacer(modifier = Modifier.height(8.dp))

        olympiad.subjects?.takeIf { it.isNotEmpty() }?.let { subjects ->
            Text("Subjects:", style = MaterialTheme.typography.titleMedium)
            subjects.forEach { subject ->
                Text("- ${subject.name}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        olympiad.stages?.takeIf { it.isNotEmpty() }?.let { stages ->
            Text("Stages:", style = MaterialTheme.typography.titleMedium)
            stages.forEach { stage ->
                Text("- ${stage.name}", style = MaterialTheme.typography.bodyMedium)
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
            Text(it, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }


        olympiad.link?.let {
            Text("Link:", style = MaterialTheme.typography.titleMedium)
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium
            ) // Можно сделать кликабельной ссылкой
            Spacer(modifier = Modifier.height(8.dp))
        }
        olympiad.keywords?.let {
            Text("Keywords:", style = MaterialTheme.typography.titleMedium)
            Text(it, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}