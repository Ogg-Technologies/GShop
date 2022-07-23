package com.example.gshop.ui.utilities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gshop.model.utilities.monads.*
import com.example.gshop.ui.theme.GShopTheme

val TEXT_SIZE = 16
fun headingSize(level: Int) = (TEXT_SIZE - level) * 2.5

@Composable
fun MarkdownView(markdownString: String, modifier: Modifier = Modifier) {
    val parsedMarkdown = mdDocument(markdownString)
        ?.let { if (it.remaining.isEmpty()) it.value else null }
    if (parsedMarkdown != null) {
        MdDocumentView(parsedMarkdown, modifier)
    } else {
        Column(modifier.padding(16.dp)) {
            Text(markdownString, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun MdDocumentView(mdDocument: MdDocument, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(16.dp)
    ) {
        for (e in mdDocument.elements) {
            when (e) {
                is MdHeading -> Text(text = e.text.string, fontSize = headingSize(e.level).sp)
                is MdParagraph -> Column {
                    for (l in e.lines) {
                        MdLineView(l)
                    }
                }
                is MdList -> MdListView(e)
            }
        }
    }
}

@Composable
private fun MdListView(e: MdList) {
    Column {
        e.items.forEachIndexed { index, item ->
            Row(Modifier.padding(start = 8.dp)) {
                when (e.style) {
                    MdListStyle.Bullet -> Text(text = "•", fontSize = TEXT_SIZE.sp)
                    MdListStyle.Number -> Text(text = "${index + 1}.", fontSize = TEXT_SIZE.sp)
                }
                Spacer(Modifier.width(4.dp))
                MdLineView(item)
            }
        }
    }
}

@Composable
fun MdLineView(mdLine: MdLine) {
    val annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = MaterialTheme.colors.onBackground, fontSize = TEXT_SIZE.sp)) {
            for (part in mdLine.parts) {
                when (part) {
                    is MdText -> {
                        val spanStyle = SpanStyle(
                            fontWeight = if (part.style is MdTextStyle.Bold) FontWeight.ExtraBold else null,
                            fontStyle = if (part.style is MdTextStyle.Italic) FontStyle.Italic else null,
                        )
                        withStyle(spanStyle) {
                            append(part.string)
                        }
                    }
                    is MdHyperLink -> {
                        pushStringAnnotation("hyperlink", part.url)
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colors.secondary,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(part.string)
                        }
                        pop()
                    }
                    is MdLink -> {
                        withStyle(SpanStyle(color = Color.Gray)) {
                            append(part.string)
                        }
                    }
                }
            }
        }
    }
    val uriHandler = LocalUriHandler.current
    ClickableText(text = annotatedString) { offset ->
        annotatedString.getStringAnnotations(tag = "hyperlink", start = offset, end = offset)
            .firstOrNull()?.let {
                val url = it.item
                uriHandler.openUri(url)
            }
    }
}

@Preview(showBackground = true)
@Composable
fun MarkdownViewPreview() {
    val doc = mdDocument(
        """
        # Heading 1
        ###### Heading 6
        Hello *there* **world**!
        ## Heading 2
        [External link](example.com)
        [[Internal link]]
        ### Heading 3
        - Item 1
        - Item 2
        - Item 3
        
        1. Item 1
        2. Item 2
        3. Item 3
    """.trimIndent()
    )
    GShopTheme {
        Surface {
            MdDocumentView(doc!!.value)
            Text(text = doc.remaining)
        }
    }
}