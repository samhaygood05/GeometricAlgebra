import GeoAlg2D.I
import kotlin.math.*

object GeoAlg2D {
    @JvmField val X = Vector2D(x = 1)
    @JvmField val Y = Vector2D(y = 1)

    @JvmField val I = BiVector2D(1)

    @JvmStatic fun exp(vec: Vector2D) = cosh(vec.mag) + sinh(vec.mag) * vec.norm
    @JvmStatic fun exp(bivec: BiVector2D) = cos(bivec.mag) + sin(bivec.mag) * bivec.norm
    @JvmStatic fun exp(multivec: MultiVector2D) = exp(multivec.scalar) * exp(multivec.vec) * exp(multivec.bivec)

    @JvmStatic fun ln(scalar: Double) = kotlin.math.ln(scalar.absoluteValue) + if (scalar.sign == -1.0) PI*I else BiVector2D()
    @JvmStatic fun ln(vec: Vector2D) = ln(vec.mag) + (3*PI*I/2) + (PI*I/2 * vec.norm)
    @JvmStatic fun ln(bivec: BiVector2D) = ln(bivec.mag) + PI*bivec.norm/2

    @JvmStatic fun cosh(vec: Vector2D) = (exp(vec) + exp(-vec))/2
    @JvmStatic fun sinh(vec: Vector2D) = (exp(vec) - exp(-vec))/2
    @JvmStatic fun tanh(vec: Vector2D) = (exp(vec) - exp(-vec))/(exp(vec) + exp(-vec))
    @JvmStatic fun cos(vec: Vector2D) = (exp(-vec * I) + exp(vec * I))/2
    @JvmStatic fun sin(vec: Vector2D) = I * (exp(-vec * I) - exp(vec * I))/2
    @JvmStatic fun tan(vec: Vector2D) = I * (exp(-vec * I) - exp(vec * I))/(exp(-vec * I) + exp(vec * I))

    @JvmStatic fun cosh(bivec: BiVector2D) = (exp(bivec) + exp(-bivec))/2
    @JvmStatic fun sinh(bivec: BiVector2D) = (exp(bivec) - exp(-bivec))/2
    @JvmStatic fun tanh(bivec: BiVector2D) = (exp(bivec) - exp(-bivec))/(exp(bivec) + exp(-bivec))
    @JvmStatic fun cos(bivec: BiVector2D) = (exp(-bivec * I) + exp(bivec * I))/2
    @JvmStatic fun sin(bivec: BiVector2D) = I * (exp(-bivec * I) - exp(bivec * I))/2
    @JvmStatic fun tan(bivec: BiVector2D) = I * (exp(-bivec * I) - exp(bivec * I))/(exp(-bivec * I) + exp(bivec * I))

    @JvmStatic fun cosh(multivec: MultiVector2D) = (exp(multivec) + exp(-multivec))/2
    @JvmStatic fun sinh(multivec: MultiVector2D) = (exp(multivec) - exp(-multivec))/2
    @JvmStatic fun tanh(multivec: MultiVector2D) = (exp(multivec) - exp(-multivec))/(exp(multivec) + exp(-multivec))
    @JvmStatic fun cos(multivec: MultiVector2D) = (exp(-multivec * I) + exp(multivec * I))/2
    @JvmStatic fun sin(multivec: MultiVector2D) = I * (exp(-multivec * I) - exp(multivec * I))/2
    @JvmStatic fun tan(multivec: MultiVector2D) = I * (exp(-multivec * I) - exp(multivec * I))/(exp(-multivec * I) + exp(multivec * I))
}

class Vector2D(x: Number = 0.0, y: Number = 0.0) : Vector {
    val x: Double = x.toDouble()
    val y: Double = y.toDouble()

    override val dimension: Int get() = 2

    operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector2D) = Vector2D(x + that.x, y + that.y)
    operator fun plus(that: BiVector2D) = MultiVector2D(vec = this, bivec = that)
    operator fun plus(that: MultiVector2D) = that + this

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector2D) = this + -that
    operator fun minus(that: BiVector2D) = this + -that
    operator fun minus(that: MultiVector2D) = this + -that

    infix fun dot(that: Vector2D) = x * that.x + y * that.y
    infix fun dot(that: BiVector2D) = Vector2D(-y * that.xy, x * that.xy)

    infix fun wedge(that: Number) = this * that
    infix fun wedge(that: Vector2D) = BiVector2D(x * that.y - y * that.x)
    infix fun wedge(that: BiVector2D) = 0.0

    override val sqrMag: Double get() = this dot this
    override val norm get() = if (isZero()) Vector2D() else this / mag

    override operator fun times(that: Number) = that * this
    operator fun times(that: Vector2D) = (this dot that) + (this wedge that)
    operator fun times(that: BiVector2D) = this dot that
    operator fun times(that: MultiVector2D) = this * that.scalar + this * that.vec + this * that.bivec

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector2D) = this * (1/that)
    operator fun div(that: BiVector2D) = this * (1/that)
    operator fun div(that: MultiVector2D) = this * (1/that)

    fun pow(n: Int): MultiVector2D = when {
        n > 0 && n.mod(2) == 0 -> MultiVector2D(mag.pow(n))
        n > 0 && n.mod(2) != 0 -> MultiVector2D(vec = mag.pow(n) * norm)
        n < 0 && n.mod(2) == 0 -> MultiVector2D(1/mag.pow(n))
        n < 0 && n.mod(2) != 0 -> MultiVector2D(vec = 1/mag.pow(n) * norm)
        else -> MultiVector2D(1.0)
    }
    fun pow(x: Double): MultiVector2D = when {
        x.mod(1.0) == 0.0 -> pow(x.toInt())
        else -> I.pow(x) * mag.pow(x) * GeoAlg2D.exp(x * -PI/2 * norm)
    }

    fun polar(): Array<MultiVector2D> = arrayOf(mag * I + 0.0, -PI*I/2 * norm + 0.0)

    fun rotate(θ: Double) = this * GeoAlg2D.exp(θ*I)

    operator fun unaryMinus() = -1 * this

    override fun toString(): String {
        return if (!isZero()){
            val terms = mutableListOf<String>()
            if (x != 0.0) terms.add("${x}x")
            if (y != 0.0) terms.add("${y}x")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero(): Boolean = x == 0.0 && y == 0.0
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Vector4D -> x == other.x && y == other.y && other.z == 0.0 && other.w == 0.0
            is Vector3D -> x == other.x && y == other.y && other.z == 0.0
            is Vector2D -> x == other.x && y == other.y
            is Vector1D -> x == other.x && y == 0.0

            is MultiVector4D -> other.scalar == 0.0 && equals(other.vec) && other.bivec.isZero() && other.trivec.isZero() && other.quadvec.isZero()
            is MultiVector3D -> other.scalar == 0.0 && equals(other.vec) && other.bivec.isZero() && other.trivec.isZero()
            is MultiVector2D -> other.scalar == 0.0 && equals(other.vec) && other.bivec.isZero()
            is MultiVector1D -> other.scalar == 0.0 && equals(other.vec)

            is Number -> isZero() && other.toDouble() == 0.0

            else -> false
        }
    }
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    fun proj(vec: Vector2D) = (this dot (vec.norm)) * vec.norm

    fun linearTrans(xTrans: Vector, yTrans: Vector): Vector = (xTrans * x) + (yTrans * y)
}
class BiVector2D(xy: Number = 0.0) : BiVector {
    val xy: Double = xy.toDouble()

    override val dimension: Int get() = 2

    operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector2D) = that + this
    operator fun plus(that: BiVector2D) = BiVector2D(xy + that.xy)
    operator fun plus(that: MultiVector2D) = that + this

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector2D) = this + -that
    operator fun minus(that: BiVector2D) = this + -that
    operator fun minus(that: MultiVector2D) = this + -that

    infix fun dot(that: Vector2D) = -(that dot this)
    infix fun dot(that: BiVector2D) = -xy * that.xy

    infix fun wedge(that: Number) = this * that
    infix fun wedge(that: Vector2D) = 0.0
    infix fun wedge(that: BiVector2D) = 0.0

    override val sqrMag: Double get() = xy.pow(2)
    override val mag: Double get() = xy.absoluteValue
    override val norm: BiVector2D get() = BiVector2D(xy.sign)

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector2D) = this dot that
    operator fun times(that: BiVector2D) = this dot that
    operator fun times(that: MultiVector2D) = this * that.scalar + this * that.vec + this * that.bivec

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector2D) = this * (1/that)
    operator fun div(that: BiVector2D) = this * (1/that)
    operator fun div(that: MultiVector2D) = this * (1/that)

    fun pow(n: Int): MultiVector2D = when {
        n > 0 && n.mod(2) == 0 -> MultiVector2D(mag.pow(n) * (-1.0).pow(floor(n/2.0)))
        n > 0 && n.mod(2) != 0 -> MultiVector2D(bivec = mag.pow(n) * norm * (-1.0).pow(floor(n/2.0)))
        n < 0 && n.mod(2) == 0 -> MultiVector2D(1/mag.pow(n) * (-1.0).pow(floor(n/2.0)))
        n < 0 && n.mod(2) != 0 -> MultiVector2D(bivec = 1/mag.pow(n) * norm * (-1.0).pow(floor(n/2.0)))
        else -> MultiVector2D(1.0)
    }
    fun pow(x: Double): MultiVector2D = when {
        x.mod(1.0) == 0.0 -> pow(x.toInt())
        else -> mag.pow(x) * GeoAlg2D.exp(x * PI/2 * norm)
    }

    operator fun unaryMinus() = -1 * this

    override fun toString(): String {
        return if(!isZero()){
            val terms = mutableListOf<String>()
            if (xy != 0.0) terms.add("${xy}xy")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero(): Boolean = xy == 0.0
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is BiVector4D -> xy == other.xy && other.yz == 0.0 && other.zx == 0.0 && other.wx == 0.0 && other.wy == 0.0 && other.wz == 0.0
            is BiVector3D -> xy == other.xy && other.yz == 0.0 && other.zx == 0.0
            is BiVector2D -> xy == other.xy

            is MultiVector4D -> other.scalar == 0.0 && other.vec.isZero() && equals(other.bivec) && other.trivec.isZero() && other.quadvec.isZero()
            is MultiVector3D -> other.scalar == 0.0 && other.vec.isZero() && equals(other.bivec) && other.trivec.isZero()
            is MultiVector2D -> other.scalar == 0.0 && other.vec.isZero() && equals(other.bivec)

            is Number -> isZero() && other.toDouble() == 0.0

            else -> false
        }
    }
    override fun hashCode(): Int {
        return xy.hashCode()
    }

}

class MultiVector2D(scalar: Number = 0.0, val vec: Vector2D = Vector2D(), val bivec: BiVector2D = BiVector2D()) : MultiVector {
    val scalar = scalar.toDouble()

    override val dimension get() = 2

    operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector2D) = this + MultiVector2D(vec = that)
    operator fun plus(that: BiVector2D) = this + MultiVector2D(bivec = that)
    operator fun plus(that: MultiVector2D) = MultiVector2D(scalar + that.scalar, vec + that.vec, bivec + that.bivec)

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector2D) = this + -that
    operator fun minus(that: BiVector2D) = this + -that
    operator fun minus(that: MultiVector2D) = this + -that

    val conj: MultiVector2D get() = MultiVector2D(scalar, -vec, -bivec)
    override val norm: MultiVector2D get() = MultiVector2D(scalar.sign, vec.norm, bivec.norm)

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector2D) = this.scalar * that + this.vec * that + this.bivec * that
    operator fun times(that: BiVector2D) = this.scalar * that + this.vec * that + this.bivec * that
    operator fun times(that: MultiVector2D) = this.scalar * that + this.vec * that + this.bivec * that

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector2D) = this * (1/that)
    operator fun div(that: BiVector2D) = this * (1/that)
    operator fun div(that: MultiVector2D) = this * (1/that)

    fun polar(): Array<MultiVector2D> = when {
        bivec.isZero() && scalar == 0.0 -> vec.polar()
        bivec.isZero() && scalar.absoluteValue > vec.mag -> arrayOf((scalar.pow(2) - vec.sqrMag)/(scalar * sqrt(1-(vec.sqrMag/scalar.pow(2)))) + Vector2D(), atanh(vec.mag/scalar)*vec.norm + 0.0)
        bivec.isZero() && scalar.absoluteValue < vec.mag -> arrayOf((scalar.pow(2) - vec.sqrMag)/(scalar * sqrt(1-(vec.sqrMag/scalar.pow(2)))) * I + 0.0, -atanh(vec.mag/scalar)*I*vec.norm + 0.0)
        bivec.isZero() && scalar.absoluteValue == vec.mag -> arrayOf(MultiVector2D(), MultiVector2D())
        vec.isZero() -> arrayOf(sqrt(scalar.pow(2) + bivec.sqrMag) + Vector2D(), atan2(bivec.mag, scalar)*I + 0.0)
        else -> TODO()
    }

    operator fun unaryMinus() = -1 * this

    override fun toString(): String {
        return if (!isZero()) {
            val terms = mutableListOf<String>()
            if (scalar != 0.0) terms.add(scalar.toString())
            if (!vec.isZero()) terms.add("(${vec})")
            if (!bivec.isZero()) terms.add("(${bivec})")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero() = scalar == 0.0 && vec.isZero() && bivec.isZero()
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Vector1D, is Vector2D, is Vector3D, is Vector4D, is BiVector2D, is BiVector3D, is BiVector4D -> other == this

            is Number -> scalar == other && vec.isZero() && bivec.isZero()

            is MultiVector4D -> scalar == other.scalar && vec.equals(other.vec) && bivec.equals(other.bivec) && other.trivec.isZero() && other.quadvec.isZero()
            is MultiVector3D -> scalar == other.scalar && vec.equals(other.vec) && bivec.equals(other.bivec) && other.trivec.isZero()
            is MultiVector2D -> scalar == other.scalar && vec.equals(other.vec) && bivec.equals(other.bivec)
            is MultiVector1D -> scalar == other.scalar && vec.equals(other.vec) && bivec.isZero()

            else -> false
        }
    }
    override fun hashCode(): Int {
        var result = vec.hashCode()
        result = 31 * result + bivec.hashCode()
        result = 31 * result + scalar.hashCode()
        return result
    }
}

