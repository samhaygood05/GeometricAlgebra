fun main() {
    val u = Vector3D(1 ,1, 1)
    val b = GeoAlg3D.ZX

    println(u)
    println(b)
    println(u.proj(b))
    println(u.orthoProj(b))
}
