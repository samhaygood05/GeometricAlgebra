package geoAlg

import Util.nChoosek
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class TriVector : KVector {

    constructor(dim : Int, data: Array<Double>) : super(dim, 3, data) {
        require(this.data.size == nChoosek[dim][3])
    }
    constructor(dim: Int) : super(dim, 2, Array(nChoosek[dim][3]) { 0.0 })

    override fun toDim(dim: Int): TriVector {
        val temp = TriVector(dim)
        for (i in 0 until min(temp.data.size, data.size)) {
            temp.data[i] = data[i]
        }
        return temp
    }

    override fun sqrMag(): Double = -(this dot this)
    override fun mag(): Double = sqrt(sqrMag())

    infix fun dot(other: Vector) = other dot this
    infix fun dot(other: TriVector) : Double {
        var temp: Double = 0.0
        for (i in 0 until min(this.data.size, other.data.size)) {
            temp -= this.data[i]*other.data[i]
        }
        return temp
    }
}