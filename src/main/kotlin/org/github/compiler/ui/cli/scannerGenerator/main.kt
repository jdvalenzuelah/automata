package org.github.compiler.ui.cli.scannerGenerator

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import org.github.compiler.atg.ATGTransforms
import org.github.compiler.generate.SpecGenerator
import org.github.compiler.regularExpressions.transforms.then
import java.io.File

class ParserGenerator : CliktCommand() {

    private val atgFile by argument(help = "ATG containing definition of scanner to be generated")
        .file(mustExist = true)

    private val destPath by argument(help = "Destination path where the scanner will be saved")

    private val atgToSpec = ATGTransforms.fileToAtg.then(SpecGenerator)

    override fun run() {

        println("Generating spec file for file ${atgFile.name}")
        val atgSpec = atgToSpec(atgFile)
        atgSpec.writeTo(File(destPath))
        println("Spec and main file generated and saved to $destPath")

    }

}


fun main(args: Array<String>) = ParserGenerator().main(args)
