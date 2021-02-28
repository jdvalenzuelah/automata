package gt.automata.nfa.thompson.configuration

import gt.automata.nfa.thompson.ThompsonTransform
import gt.automata.nfa.thompson.ThomptsonRules
import gt.automata.regex.postfix.RegexToPostfix

object ThomptsonTransformConfig {

    fun getThompsonTransform(): ThompsonTransform {
        return ThompsonTransform(RegexToPostfix, ThomptsonRules())
    }

}
