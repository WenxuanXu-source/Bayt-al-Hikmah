package com.baytalhikmah.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.domain.Morphology
import com.baytalhikmah.domain.VocabBundle
import com.baytalhikmah.ui.theme.BaytType
import com.baytalhikmah.ui.theme.LocalBaytPalette

@Composable
fun HighlightedText(
    text: String,
    target: String?,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val annotated: AnnotatedString = remember(text, target, color) {
        buildAnnotatedString {
            if (target.isNullOrBlank()) {
                append(text)
            } else {
                val index = text.indexOf(target)
                if (index < 0) {
                    append(text)
                } else {
                    append(text.substring(0, index))
                    pushStyle(
                        SpanStyle(
                            color = color,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                    append(target)
                    pop()
                    append(text.substring(index + target.length))
                }
            }
        }
    }
    Text(annotated, style = style, modifier = modifier)
}

@Composable
fun VocabularyCard(
    bundle: VocabBundle,
    language: LanguageEntity,
    saved: Boolean,
    difficult: Boolean,
    modifier: Modifier = Modifier,
    onToggleSaved: (() -> Unit)? = null,
    onToggleDifficult: (() -> Unit)? = null,
    startExpanded: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val vocabulary = bundle.vocabulary
    val palette = LocalBaytPalette.current
    val morphology = remember(language.morphology) { Morphology(language.morphology) }
    var showForms by remember { mutableStateOf(false) }
    var showEtymology by remember { mutableStateOf(false) }
    val formsWithLabels = remember(bundle) {
        bundle.forms.mapNotNull { form ->
            morphology.labelFor(vocabulary.pos, form.dimensions)?.let { form to it }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = vocabulary.headword,
                    style = if (startExpanded) BaytType.headword else BaytType.headwordCompact,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildString {
                        append(vocabulary.pos.label)
                        vocabulary.gender?.let { append(" · ").append(it.label) }
                        if (vocabulary.inflectionType.isNotBlank()) append(" · ").append(vocabulary.inflectionType)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onToggleSaved != null) {
                IconButton(onClick = onToggleSaved) {
                    Icon(
                        imageVector = if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (saved) "Remove from saved words" else "Save word",
                        tint = if (saved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (onToggleDifficult != null) {
                IconButton(onClick = onToggleDifficult) {
                    Icon(
                        imageVector = if (difficult) Icons.Filled.Flag else Icons.Outlined.Flag,
                        contentDescription = if (difficult) "Unmark difficult" else "Mark as difficult",
                        tint = if (difficult) palette.warning else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        vocabulary.definitions.forEach { definition ->
            Row(Modifier.padding(vertical = 2.dp)) {
                Text("—", style = BaytType.definition, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Text(
                    definition,
                    style = BaytType.definition,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (vocabulary.grammaticalClass.isNotBlank() || vocabulary.principalParts.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Hairline()
            Spacer(Modifier.height(12.dp))
            SectionLabel("Grammatical information")
            Spacer(Modifier.height(6.dp))
            if (vocabulary.grammaticalClass.isNotBlank()) {
                Text(
                    vocabulary.grammaticalClass,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (vocabulary.principalParts.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Principal parts: " + vocabulary.principalParts.joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (vocabulary.irregularForms.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Irregular forms: " + vocabulary.irregularForms.joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.warning
                )
            }
        }

        if (formsWithLabels.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            ExpandRow(
                label = "Forms (${formsWithLabels.size})",
                expanded = showForms,
                onToggle = { showForms = !showForms }
            )
            if (showForms) {
                Spacer(Modifier.height(6.dp))
                formsWithLabels.forEach { (form, label) ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(form.writtenForm, style = BaytType.tableCell, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                }
            }
        }

        if (!vocabulary.etymology.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            ExpandRow(
                label = "Etymology",
                expanded = showEtymology,
                onToggle = { showEtymology = !showEtymology }
            )
            if (showEtymology) {
                Spacer(Modifier.height(6.dp))
                Text(
                    vocabulary.etymology!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                vocabulary.etymologyNote?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        val examples = bundle.examples.sortedBy { if (it.isAuthentic) 1 else 0 }
        if (examples.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Hairline()
            Spacer(Modifier.height(12.dp))
            SectionLabel(if (examples.size > 1) "Examples" else "Example")
            examples.forEachIndexed { index, example ->
                if (index > 0) {
                    Spacer(Modifier.height(12.dp))
                }
                Spacer(Modifier.height(6.dp))
                HighlightedText(
                    text = example.originalText,
                    target = example.targetForm,
                    style = BaytType.example,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    example.translation,
                    style = BaytType.translation,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                val attribution = if (example.isAuthentic) {
                    listOfNotNull(example.author, example.work, example.passageReference)
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                } else {
                    "Constructed pedagogical example"
                }
                if (attribution.isNotBlank()) {
                    Text(
                        attribution,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!vocabulary.sourceReference.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                "Source: ${vocabulary.sourceReference}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExpandRow(label: String, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
    }
}
