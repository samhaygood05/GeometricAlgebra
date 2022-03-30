package geoAlg

import Util.nChoosek
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class BiVector : KVector {

    constructor(dim : Int, data: Array<Double>) : super(dim, 2, data) {
        require(this.data.size == nChoosek[dim][2])
    }
    constructor(dim: Int) : super(dim, 2, Array(nChoosek[dim][2]) { 0.0 })

    override fun toDim(dim: Int): BiVector {
        val temp = BiVector(dim)
        for (i in 0 until min(temp.data.size, data.size)) {
            temp.data[i] = data[i]
        }
        return temp
    }

    override fun sqrMag(): Double? = if (isSimple()) -(this dot this) else null
    override fun mag(): Double? = sqrMag()?.let { sqrt(it) }

    fun isSimple() : Boolean = if (getDimension() < 4) true
    else data[0]*data[5] == 0.0 || data[1]*data[3] == 0.0 || data[2]*data[4] == 0.0

    infix fun dot(other: Vector) = -(other dot this)
    infix fun dot(other: BiVector): Double {
        var temp: Double = 0.0
        for (i in 0 until min(this.data.size, other.data.size)) {
            temp -= this.data[i]*other.data[i]
        }
        return temp
    }
    infix fun dot(other: TriVector): Vector {
        val biggest: Int = max(getDimension(), other.getDimension())
        val temp = Vector(biggest)
        val thisNew = this.toDim(biggest).data
        val otherNew = other.toDim(biggest).data

        temp.data[0] = -thisNew[1]*otherNew[0]
        temp.data[1] = -thisNew[2]*otherNew[0]
        temp.data[2] = -thisNew[0]*otherNew[0]
        if (biggest > 3) {
            temp.data[0] += thisNew[4]*otherNew[1] - thisNew[5]*otherNew[3]
            temp.data[1] += thisNew[5]*otherNew[2] - thisNew[3]*otherNew[1]
            temp.data[2] -= thisNew[3]*otherNew[3] + thisNew[4]*otherNew[2]
            temp.data[3] = -thisNew[0]*otherNew[1] - thisNew[1]*otherNew[2] - thisNew[2]*otherNew[3]
        }
        return temp
    }
    override fun dot(other: KVector): Any {
        TODO("Not yet implemented")
    }

    infix fun wedge(other: Number) = this * other
    override fun wedge(other: KVector): Any {
        TODO("Not yet implemented")
    }

    override fun times(other: Number): BiVector {
        val temp = BiVector(this.getDimension())
        for (i in temp.data.indices) {
            temp.data[i] = this.data[i] * other.toDouble()
        }
        return temp
    }

    override fun div(other: Number) = this * (1/other.toDouble())


}