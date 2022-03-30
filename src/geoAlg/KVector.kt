package geoAlg

import kotlin.math.absoluteValue
import kotlin.math.sqrt

abstract class KVector(dim: Int, private val grade: Int, data: Array<Double>) : TypeVector(dim) {
    final override val data : Array<Double>

    init {
        this.data = data
        require(dim >= grade) { "Invalid dim value: $dim" }
    }

    public fun getGrade() = this.grade

    public override fun norm() = if (isZero()) this else this/mag()!!
    public override fun isZero() = Util.isZero(this.data)
//    public override fun getData() : Array<Double> = this.data

    abstract infix fun dot(other : KVector) : Any
    abstract infix fun wedge(other : KVector) : Any

    abstract fun sqrMag() : Double?
    abstract fun mag() : Double?

    abstract fun toDim(dim: Int) : KVector
}