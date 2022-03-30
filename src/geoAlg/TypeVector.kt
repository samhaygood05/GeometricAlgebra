package geoAlg

abstract class TypeVector(dim: Int) {
    private val dimension: Int = dim

    init {
        require(dim in 1..4) { "Invalid dim value: $dim" }
    }

    abstract fun <T : TypeVector> norm() : T
    abstract fun isZero(): Boolean
    abstract fun getData() : Array<*>

    infix fun dot(other : Number) = 0.0

    public fun getDimension() = this.dimension

}