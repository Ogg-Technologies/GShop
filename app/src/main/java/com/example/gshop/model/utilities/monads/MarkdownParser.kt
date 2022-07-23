package com.example.gshop.model.utilities.monads


data class MdText(val string: String, val style: MdTextStyle = MdTextStyle.Plain) : MdLineElement
sealed interface MdTextStyle {
    object Plain : MdTextStyle
    object Bold : MdTextStyle
    object Italic : MdTextStyle
}

data class MdLink(val string: String) : MdLineElement

data class MdHyperLink(val string: String, val url: String) : MdLineElement

sealed interface MdLineElement
data class MdLine(val parts: List<MdLineElement>)

data class MdParagraph(val lines: List<MdLine>) : MdDocumentElement

data class MdHeading(val level: Int, val text: MdText) : MdDocumentElement

data class MdList(val items: List<MdLine>, val style: MdListStyle) : MdDocumentElement
sealed interface MdListStyle {
    object Bullet : MdListStyle
    object Number : MdListStyle
}

data class MdDocument(val elements: List<MdDocumentElement>)
sealed interface MdDocumentElement


private fun Char.isLineBreak(): Boolean = this == '\n' || this == '\r'

private fun stringWhere(charPredicate: (Char) -> Boolean): Parser<String> =
    oneOrMore(charSatisfying { charPredicate(it) }).map { it.joinToString("") }

private val nonLineBreakChar = charSatisfying { !it.isLineBreak() }

private fun singleLineStringBoundedBy(bounds: Pair<Parser<Any>, Parser<Any>>): Parser<String> =
    nonLineBreakChar.repeatedUntil(bounds.second).surroundedBy(bounds).map { it.joinToString("") }

private val linkParen = string("[[") to string("]]")
private val hyperLinkTextParen = char('[') to char(']')
private val hyperLinkUrlParen = char('(') to char(')')

val mdLink: Parser<MdLink> = singleLineStringBoundedBy(linkParen).map { MdLink(it) }

val mdHyperLink: Parser<MdHyperLink> = parser {
    val linkText = singleLineStringBoundedBy(hyperLinkTextParen).bind()
    val url = singleLineStringBoundedBy(hyperLinkUrlParen).bind()
    pure(MdHyperLink(linkText, url))
}

private val plainString: Parser<String> =
    stringWhere { it != '*' && it != '[' && !it.isLineBreak() }
private val italicMdText: Parser<MdText> =
    (plainString surroundedBy char('*')).map { MdText(it, MdTextStyle.Italic) }
private val boldMdText: Parser<MdText> =
    (plainString surroundedBy string("**")).map { MdText(it, MdTextStyle.Bold) }
private val mdTextWithStar: Parser<MdText> = stringWhere { !it.isLineBreak() }.map { MdText(it) }
val mdText: Parser<MdText> =
    boldMdText or italicMdText or plainString.map { MdText(it) } or mdTextWithStar

val mdLineElement = mdLink or mdHyperLink or mdText

val mdLine: Parser<MdLine> = oneOrMore(mdLineElement).map { MdLine(it) }

private val lineBreak: Parser<Unit> = char('\n').map { }

val mdHeading: Parser<MdHeading> = parser {
    val level = char('#').repeatedBetween(1, 7).map { it.size }.bind()
    char(' ').bind()
    val text = mdText.bind()
    pure(MdHeading(level, text))
}

private fun listParser(linePrefix: Parser<Any>, mdListStyle: MdListStyle): Parser<MdList> =
    chain(linePrefix right mdLine, lineBreak).map { MdList(it, mdListStyle) }

private val bulletMdList: Parser<MdList> = listParser(string("- "), MdListStyle.Bullet)
private val numberMdList: Parser<MdList> = listParser(digit then string(". "), MdListStyle.Number)
val mdList = bulletMdList or numberMdList

private val consumeLineBreaks: Parser<Unit> = zeroOrMore(lineBreak).map { }

val mdParagraph: Parser<MdParagraph> =
    chain(mdLine ifNot (mdHeading or mdList), lineBreak).map { MdParagraph(it) }

val mdDocument: Parser<MdDocument> =
    chain(mdHeading or mdList or mdParagraph, consumeLineBreaks).surroundedBy(consumeLineBreaks)
        .map { MdDocument(it) }
