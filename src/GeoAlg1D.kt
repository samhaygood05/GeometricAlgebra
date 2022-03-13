import kotlin.math.*
import Dimension.*

object GeoAlg1D {
    @JvmField val X = Vector1D(1)

    @JvmStatic fun exp(vec: Vector1D) = cosh(vec.mag) + sinh(vec.mag) * vec.norm
}

class Vector1D(x: Number = 0.0) : Vector {
    val x: Double = x.toDouble()

    companion object {
        @JvmField val NaN = Vector1D(Double.NaN)
    }

    override val dimension get() = ONE

    override operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector1D) = Vector1D(x + that.x)
    operator fun plus(that: MultiVector1D) = MultiVector1D(that.scalar, this + that.vec)

    override operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector1D) = this + -that
    operator fun minus(that: MultiVector1D) = this + -that

    infix fun dot(that: Vector1D) = x * that.x
    infix fun dot(that: MultiVector1D) = this dot that.vec

    infix fun wedge(that: Vector1D) = 0.0
    infix fun wedge(that: MultiVector1D) = this * that.scalar

    override val sqrMag: Double get() = mag.pow(2)
    override val mag: Double get() = x.absoluteValue
    override val norm: Vector1D get() = if (isZero()) Vector1D() else this / mag

    override operator fun times(that: Number) = that * this
    operator fun times(that: Vector1D) = this dot that
    operator fun times(that: MultiVector1D) = MultiVector1D(this * that.vec, this * that.scalar)

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector1D) = this * (1/that)
    operator fun div(that: MultiVector1D) = this * (1/that)

    operator fun unaryMinus() = -1 * this

    override fun toString(): String = if (!isZero()) "${x}x" else "0.0"

    override fun isZero() = x == 0.0
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Vector4D -> x == other.x && other.y == 0.0 && other.z == 0.0 && other.w == 0.0
            is Vector3D -> x == other.x && other.y == 0.0 && other.z == 0.0
            is Vector2D -> x == other.x && other.y == 0.0
            is Vector1D -> x == other.x

            is MultiVector4D -> other.scalar == 0.0 && equals(other.vec) && other.bivec.isZero() && other.trivec.isZero() && other.quadvec.isZero()
            is MultiVector3D -> other.scalar == 0.0 && equals(other.vec) && other.bivec.isZero() && other.trivec.isZero()
            is MultiVector2D -> other.scalar == 0.0 && equals(other.vec) && other.bivec.isZero()
            is MultiVector1D -> other.scalar == 0.0 && equals(other.vec)

            is Number -> isZero() && other.toDouble() == 0.0

            else -> false
        }
    }
    override fun hashCode(): Int {
        return x.hashCode()
    }

    fun linearTrans(xTrans: Vector): Vector = xTrans * x
}

class MultiVector1D(scalar: Number = 0.0, val vec: Vector1D = Vector1D()) : MultiVector {
    val scalar: Double = scalar.toDouble()

    companion object {
        @JvmField val NaN = Double.NaN + Vector1D.NaN
    }

    override val dimension get() = ONE

    operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector1D) = that + this
    operator fun plus(that: MultiVector1D) = MultiVector1D(this.scalar + that.scalar, this.vec + that.vec)

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector1D) = this + -that
    operator fun minus(that: MultiVector1D) = this + -that

    val conj: MultiVector1D get() = MultiVector1D(scalar, -vec)
    override val norm: MultiVector1D get() = MultiVector1D(scalar.sign, vec.norm)

    infix fun dot(that: Vector1D) = this.vec dot that
    infix fun dot(that: MultiVector1D) = this.vec * that.vec

    infix fun wedge(that: Vector1D) = this.scalar * that
    infix fun wedge(that: MultiVector1D) = this.scalar * that.scalar + this.scalar * that.vec + this.vec * that.scalar

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector1D) = MultiVector1D(this.vec * that, this.scalar * that)
    operator fun times(that: MultiVector1D) = this * that.scalar + this * that.vec

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector1D) = this * (1/that)
    operator fun div(that: MultiVector1D) = this * (1/that)

    operator fun unaryMinus() = -1 * this

    override fun toString(): String {
        return if (!isZero()) {
            val terms = mutableListOf<String>()
            if (scalar != 0.0) terms.add(scalar.toString())
            if (!vec.isZero()) terms.add("(${vec})")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero() = scalar == 0.0 && vec.isZero()
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Vector1D, is Vector2D, is Vector3D, is Vector4D -> other == this

            is Number -> scalar == other && vec.isZero()

            is MultiVector4D -> scalar == other.scalar && vec.equals(other.vec) && other.bivec.isZero() && other.trivec.isZero() && other.quadvec.isZero()
            is MultiVector3D -> scalar == other.scalar && vec.equals(other.vec) && other.bivec.isZero() && other.trivec.isZero()
            is MultiVector2D -> scalar == other.scalar && vec.equals(other.vec) && other.bivec.isZero()
            is MultiVector1D -> scalar == other.scalar && vec.equals(other.vec)

            else -> false
        }
    }
    override fun hashCode(): Int {
        var result = vec.hashCode()
        result = 31 * result + scalar.hashCode()
        return result
    }
}