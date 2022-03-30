object Util {

    fun concatenate(strings: Array<String>, separator: String): String {
        var out = strings[0]
        for (i in 1 until strings.size) {
            out += "${separator}${strings[i]}"
        }
        return out
    }

    fun isZero(array : Array<Double>) : Boolean {
        var bool: Boolean = true
        for (element : Double in array) {
            bool = bool && element == 0.0
        }
        return bool
    }
}