package geoAlg

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Vector : KVector {

    constructor(dim: Int, data: Array<Double>) : super(dim, 1, data) {
        require(data.size == dim)
    }
    constructor(dim: Int) : super(dim, 1, Array(dim) { 0.0 })

    override fun toDim(dim: Int): Vector {
        val temp = Vector(dim)
        for (i in 0 until min(temp.data.size, data.size)) {
            temp.data[i] = data[i]
        }
        return temp
    }

    override fun sqrMag() = this dot this
    override fun mag() = sqrt(sqrMag())

    operator fun plus(other: Vector) : Vector {
        val temp: Vector
        if (other.getDimension() > getDimension()) {
            temp = other
            for (i in data.indices) {
                temp.data[i] += data[i]
            }
        } else {
            temp = this
            for (i in other.data.indices) {
                temp.data[i] += other.data[i]
            }
        }
        return temp
    }

    operator fun minus(other: Vector) = this + -other

    infix fun dot(other: Vector) : Double {
        var temp: Double = 0.0
        for (i in 0 until min(this.getDimension(), other.getDimension())) {
            temp += this.data[i]*other.data[i]
        }
        return temp
    }
    infix fun dot(other: BiVector) : Vector {
        val biggest = max(getDimension(), other.getDimension())
        val temp = Vector(biggest)
        val thisNew = this.toDim(biggest).data
        val otherNew = other.toDim(biggest).data
        temp.data[0] = -thisNew[1]*otherNew[0]
        temp.data[1] = thisNew[0]*otherNew[0]
        if (biggest > 2) {
            temp.data[0] += thisNew[2]*otherNew[2]
            temp.data[1] -= thisNew[2]*otherNew[1]
            temp.data[2] = -thisNew[0]*otherNew[2] + thisNew[1]*otherNew[1]
            if (biggest > 3) {
                temp.data[0] += thisNew[3]*otherNew[3]
                temp.data[1] += thisNew[3]*otherNew[4]
                temp.data[2] += thisNew[3]*otherNew[5]
                temp.data[3] = -thisNew[0]*otherNew[3] - thisNew[1]*otherNew[4] - thisNew[2]*otherNew[5]
            }
        }
        return temp
    }
    infix fun dot(other: TriVector) : BiVector {
        val biggest: Int = max(getDimension(), other.getDimension())
        val temp = BiVector(biggest)
        val thisNew = this.toDim(biggest).data
        val otherNew = other.toDim(biggest).data

        temp.data[0] = thisNew[2]*otherNew[0]
        temp.data[1] = thisNew[0]*otherNew[0]
        temp.data[2] = thisNew[1]*otherNew[0]
        if (biggest > 3) {
            temp.data[0] += thisNew[3]*otherNew[1]
            temp.data[1] += thisNew[3]*otherNew[2]
            temp.data[2] += thisNew[3]*otherNew[3]
            temp.data[3] = thisNew[1]*otherNew[1] - thisNew[2]*otherNew[3]
            temp.data[4] = thisNew[2]*otherNew[2] - thisNew[0]*otherNew[1]
            temp.data[5] = thisNew[0]*otherNew[3] - thisNew[1]*otherNew[2]
        }
        return temp
    }
    override fun dot(other: KVector): Any {
        TODO("Not yet implemented")
    }

    infix fun wedge(other: Number) = this * other
    infix fun wedge(other: Vector) : BiVector {
        val biggest = max(getDimension(), other.getDimension())
        if (biggest > 1) {
            val temp = BiVector(biggest)
            val thisNew = this.toDim(biggest).data
            val otherNew = other.toDim(biggest).data

            temp.data[0] = thisNew[0]*otherNew[1] - thisNew[1]*otherNew[0]
            if (biggest > 2) {
                temp.data[1] = thisNew[1]*otherNew[2] - thisNew[2]*otherNew[1]
                temp.data[2] = thisNew[2]*otherNew[0] - thisNew[0]*otherNew[2]
                if (biggest > 3) {
                    temp.data[3] = thisNew[4]*otherNew[0] - thisNew[0]*otherNew[4]
                    temp.data[4] = thisNew[4]*otherNew[1] - thisNew[1]*otherNew[4]
                    temp.data[5] = thisNew[4]*otherNew[2] - thisNew[2]*otherNew[4]
                }
            }
            return temp
        } else return BiVector(2)
    }
    override fun wedge(other: KVector): Any {
        TODO("Not yet implemented")
    }

    override operator fun times(other: Number) : Vector {
        val temp = Vector(this.getDimension())
        for (i in temp.data.indices) {
            temp.data[i] = this.data[i] * other.toDouble()
        }
        return temp
    }

    override operator fun div(other: Number) = this * (1/other.toDouble())

    operator fun unaryMinus() = this * -1.0
}