package geoAlg

import Util.nChoosek

abstract class TypeVector(dim: Int) {
    private val dimension: Int = dim
    abstract val data : Array<*>

    init {
        require(dim in 1..4) { "Invalid dim value: $dim" }
    }

    abstract fun norm() : TypeVector
    abstract fun isZero(): Boolean
//    abstract fun getData() : Array<*>

    infix fun dot(other : Number) = 0.0

    public fun getDimension() = this.dimension

    abstract operator fun times(other: Number) : TypeVector
    abstract operator fun div(other: Number) : TypeVector

}