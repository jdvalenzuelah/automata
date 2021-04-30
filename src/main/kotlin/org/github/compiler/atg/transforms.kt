package org.github.compiler.atg

import org.github.compiler.regularExpressions.transforms.Transform
import org.github.compiler.regularExpressions.transforms.then
import java.io.File

object ATGTransforms {
    private val filePathToFile = Transform<String, File> { File(it) }
    private val fileToString = Transform<File, String> { it.readText() }
    private val stringToTokens = Transform<String, Collection<Token>> { ATGScanner(it).scanTokens() }
    private val tokensToAtg = Transform<Collection<Token>, ATG> { ATGParser(it).parse() }

    val fileToAtg = fileToString.then(stringToTokens).then(tokensToAtg)
    val fileToAtgByPath = filePathToFile.then(fileToAtg)

 }
