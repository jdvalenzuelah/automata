package org.github.compiler.ui.web

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.state.KVar
import kweb.state.render
import org.github.compiler.regularExpressions.automata.toFileFormat
import org.tinylog.kotlin.Logger

class WebApp(
    private val regexHandler: AutomataWebHandler,
    private val port: Int = 7659,
    private val debug: Boolean = false,
) {

    val server: Kweb

    init {
        server = Kweb(port = port, debug = debug, plugins = listOf(fomanticUIPlugin), buildPage = {

            doc.head {
                meta(name = "Description", content = "Simple UI for regex and automatas testing project")
            }

            doc.body {
                mainView("Automatas") {
                    regexProcessing()
                }
            }

        })
    }

    private fun ElementCreator<*>.mainView(pageTile: String, content: ElementCreator<DivElement>.() -> Unit) {
        div(fomantic.ui.main.container) {
            div(fomantic.column) {
                div(fomantic.ui.vertical.segment) {
                    div(fomantic.ui.message) {
                        p().innerHTML(
                            """
                            Simple demo UI for stage 1 of kotlin based compiler project. You can find sources <a href="https://github.com/jdvalenzuelah/automata">here</a> 
                            """
                                .trimIndent()
                        )
                    }
                }

                div(fomantic.ui.vertical.segment) {
                    h1(fomantic.ui.dividing.header).text(pageTile)
                    content(this)
                }
            }
        }
    }

    private fun ElementCreator<*>.gracefullyFail(content: ElementCreator<*>.() -> Unit, onException: ElementCreator<*>.() -> Unit) {
        try {
            content(this)
        } catch (e: Exception) {
            Logger.error("Error ${e.stackTraceToString()}")
            div(fomantic.ui.bottom.attached.error.message) {
                i(fomantic.warning.icon)
                text("An error occurred during executions, please check regex expression and try again!")
            }
            onException(this)
        }
    }

    private fun ElementCreator<*>.regexProcessing() {
        val regex = KVar("")
        val testString =  KVar("")

        form(fomantic.ui.form) {

            val fieldsParent = div(fomantic.ui.two.fields)

            val regexInputDiv = ElementCreator(parent = fieldsParent, position = null).div(fomantic.ui.field)
            val regexInput = ElementCreator(parent = regexInputDiv, position = null)
                .input(type = InputType.text, placeholder = "Regex eg: (a|)b*")

            val testStringDiv = ElementCreator(parent = fieldsParent, position = null).div(fomantic.ui.field)
            val testStringInput = ElementCreator(parent = testStringDiv, position = null)
                .input(type = InputType.text, placeholder = "Test string")

            button(fomantic.ui.button).text("Process").apply {
                on.click { handleProcessTestString(regexInput to regex, testStringInput to testString) }
            }

        }

        render(regex) {

            if(regex.value.isNotEmpty()) {
                div(fomantic.ui.vertical.segment) {
                    val loader = div(fomantic.ui.loader).apply { addClasses("active") }

                    gracefullyFail(
                        content = {
                            val scope = regexHandler.getAutomataScope(regex.value)

                            loader.removeClasses("active")

                            div(fomantic.ui.segment) {
                                h2(fomantic.ui.dividing.header).text("Processed regex: ${regex.value}")

                                renderAutomata("NFA:", scope.nfa.toFileFormat(), scope.nfaGraphBase64) {
                                    render(testString) {
                                        renderSimulationResult(
                                            testString.value,
                                            regexHandler.getSimulationScope(scope.nfa, testString.value).result
                                        )
                                    }
                                }

                                renderAutomata(
                                    "DFA (from nfa):",
                                    scope.dfaByNfa.toFileFormat(),
                                    scope.dfaByNfaGraphBase64
                                ) {
                                    render(testString) {
                                        renderSimulationResult(
                                            testString.value,
                                            regexHandler.getSimulationScope(scope.dfaByNfa, testString.value).result
                                        )
                                    }
                                }

                                renderAutomata(
                                    "DFA (from regex):",
                                    scope.dfaDirect.toFileFormat(),
                                    scope.dfaDirectGraphBase64
                                ) {
                                    render(testString) {
                                        renderSimulationResult(
                                            testString.value,
                                            regexHandler.getSimulationScope(scope.dfaDirect, testString.value).result
                                        )
                                    }
                                }
                            }
                        },
                        onException = { loader.removeClasses("active") }
                    )
                }
            }
        }

    }

    private fun handleProcessTestString(regex: Pair<InputElement, KVar<String>>, testString:  Pair<InputElement, KVar<String>>) {
        GlobalScope.launch { regex.second.value = regex.first.getValue().await() }
        GlobalScope.launch { testString.second.value = testString.first.getValue().await() }
    }

    private fun ElementCreator<*>.renderAutomata(title: String, asText: String, b64Graph: String, content: ElementCreator<DivElement>.() -> Unit) {
        div(fomantic.ui.segment) {
            h3(fomantic.ui.dividing.header).text(title)
            div().text(asText).apply { setAttributeRaw("style", "white-space: pre-wrap;") }
            img(fomantic.ui.image).setAttributeRaw("src", b64Graph)
            content(this)
        }
    }

    private fun ElementCreator<*>.renderSimulationResult(testString: String, result: Boolean) {
        val segmentType = if(result) fomantic.ui.green.segment else fomantic.ui.red.segment
        div(segmentType) {
            p().text("is \"$testString\" accepted? ${result.asText()}")
        }
    }

    private fun Boolean.asText() = if(this) "Yes" else "No"


}
