package gt.regex

object AugmentRegex {
    operator fun invoke(regex: String) = "($regex)#"
}
