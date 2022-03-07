fun main() {
    val u = BiVector4D(1, 1, 1, 1, 1, 1)

    println(u.norm)
    println("u = $u")
    println( "1/u = ${(1 / u)}")
    println(u / u)
    println(u / u.norm)
}
