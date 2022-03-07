object Util {

    fun concatenate(strings: Array<String>, separator: String): String {
        var out = strings[0]
        for (i in 1 until strings.size) {
            out += "${separator}${strings[i]}"
        }
        return out
    }
}