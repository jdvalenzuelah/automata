package org.github.compiler.ui.cli.scannerGenerator

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import org.github.compiler.atg.specification.Spec

class ParserGeneratorMain(
    private val scannerSpecification: Spec
) : CliktCommand() {

    private val fileToScan by argument(help = "File to scan")
        .file(mustExist = true)

    override fun run() {
        echo("Starting file parsing ${fileToScan.name}")
        val tokens = scannerSpecification.getScanner(fileToScan.readText())
        scannerSpecification.parse(tokens)
    }
}
