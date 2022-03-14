import GeoAlg3D.I
import GeoAlg3D.rotor
import Dimension.*
import GeoAlg3D.X
import GeoAlg3D.XY
import GeoAlg3D.Y
import GeoAlg3D.YZ
import GeoAlg3D.Z
import GeoAlg3D.ZX
import kotlin.math.*

object GeoAlg3D {
    @JvmStatic fun rotor(theta: Double, plane: BiVector3D) = arrayOf(exp(-theta/2 * plane.norm), exp(theta/2 * plane.norm))

    @JvmField val X = Vector3D(x = 1)
    @JvmField val Y = Vector3D(y = 1)
    @JvmField val Z = Vector3D(z = 1)

    @JvmField val XY = BiVector3D(xy = 1)
    @JvmField val YZ = BiVector3D(yz = 1)
    @JvmField val ZX = BiVector3D(zx = 1)

    @JvmField val I = TriVector3D(1)

    @JvmStatic fun exp(vec: Vector3D) = cosh(vec.mag) + sinh(vec.mag) * vec.norm
    @JvmStatic fun exp(bivec: BiVector3D) = cos(bivec.mag) + sin(bivec.mag) * bivec.norm
    @JvmStatic fun exp(trivec: TriVector3D) = cos(trivec.mag) + sin(trivec.mag) * trivec.norm
    @JvmStatic fun exp(multivec: MultiVector3D) = exp(multivec.scalar) * exp(multivec.vec) * exp(multivec.bivec) * exp(multivec.trivec)

    @JvmStatic fun ln(scalar: Double) = kotlin.math.ln(scalar.absoluteValue) + if (scalar.sign == -1.0) PI*I else TriVector3D()
    @JvmStatic fun ln(scalar: Double, basis: BiVector3D) = kotlin.math.ln(scalar.absoluteValue) + if (scalar.sign == -1.0) PI*basis.norm else BiVector3D()

    @JvmStatic fun ln(vec: Vector3D) = ln(vec.mag) + (3*PI*I/2) + (PI*I/2 * vec.norm)
    @JvmStatic fun ln(vec: Vector3D, basis: BiVector3D) = ln(vec.mag) + (3*PI*basis.norm/2) + (PI*basis.norm/2 * vec.norm)

    @JvmStatic fun ln(bivec: BiVector3D) = ln(bivec.mag) + PI*bivec.norm/2
    @JvmStatic fun ln(trivec: TriVector3D) = ln(trivec.mag) + PI*trivec.norm/2
}

class Vector3D : Vector {
    val x: Double
    val y: Double
    val z: Double

    companion object {
        @JvmField val NaN = Vector3D(Double.NaN, Double.NaN, Double.NaN)
    }

    override val dimension get() = THREE

    constructor(x: Number = 0.0, y: Number = 0.0, z: Number = 0.0) {
        this.x = x.toDouble()
        this.y = y.toDouble()
        this.z = z.toDouble()
    }
    constructor(vec: Vector2D, z: Number = 0.0) {
        x = vec.x
        y = vec.y
        this.z = z.toDouble()
    }

    override operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector3D) = Vector3D(x + that.x, y + that.y, z + that.z)
    operator fun plus(that: BiVector3D) = MultiVector3D(vec = this, bivec = that)
    operator fun plus(that: TriVector3D) = MultiVector3D(vec = this, trivec = that)
    operator fun plus(that: MultiVector3D) = that + this

    override operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector3D) = this + -that
    operator fun minus(that: BiVector3D) = this + -that
    operator fun minus(that: MultiVector3D) = this + -that

    infix fun dot(that: Vector3D) = x * that.x + y * that.y + z * that.z
    infix fun dot(that: BiVector3D) = Vector3D(z * that.zx - y * that.xy, x * that.xy - z * that.yz, y * that.yz - x * that.zx)
    infix fun dot(that: TriVector3D) = BiVector3D(z * that.xyz, x * that.xyz, y * that.xyz)

    infix fun wedge(that: Vector3D) = BiVector3D(x * that.y - y * that.x, y * that.z - z * that.y, z * that.x - x * that.z)
    infix fun wedge(that: BiVector3D) = TriVector3D(x * that.yz + y * that.zx + z * that.xy)
    infix fun wedge(that: TriVector3D) = 0.0

    infix fun vecCross(that: Vector3D) = -I * (this wedge that)

    override val sqrMag: Double get() = this dot this
    override val norm: Vector3D get() = if (isZero()) Vector3D() else this / mag

    override operator fun times(that: Number) = that * this
    operator fun times(that: Vector3D) = (this dot that) + (this wedge that)
    operator fun times(that: BiVector3D) = (this dot that) + (this wedge that)
    operator fun times(that: TriVector3D) = this dot that
    operator fun times(that: MultiVector3D) = this * that.scalar + this * that.vec + this * that.bivec + this * that.trivec

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector3D) = this * (1/that)
    operator fun div(that: BiVector3D) = this * (1/that)
    operator fun div(that: TriVector3D) = this * (1/that)
    operator fun div(that: MultiVector3D) = this * (1/that)

    operator fun unaryMinus() = -1 * this

    fun rotate(theta: Double, plane: BiVector3D): Vector3D {
        val rotor = rotor(theta, plane)
        return (rotor[0] * this * rotor[1]).vec
    }
    fun rotate(plane: BiVector3D): Vector3D {
        val rotor = rotor(plane.mag, plane.norm)
        return (rotor[0] * this * rotor[1]).vec
    }

    override fun toString(): String {
        return if (!isZero()){
            val terms = mutableListOf<String>()
            if (x != 0.0) terms.add("${x}x")
            if (y != 0.0) terms.add("${y}y")
            if (z != 0.0) terms.add("${z}z")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero(): Boolean = x == 0.0 && y == 0.0 &&z == 0.0
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Vector4D -> x == other.x && y == other.y && z == other.z && other.w == 0.0
            is Vector3D -> x == other.x && y == other.y && z == other.z
            is Vector2D -> x == other.x && y == other.y && z == 0.0
            is Vector1D -> x == other.x && y == 0.0     && z == 0.0

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
        result = 31 * result + z.hashCode()
        return result
    }

    fun proj(vec: Vector3D): Vector3D = (vec.norm dot this) * vec.norm
    fun proj(bivec: BiVector3D): Vector3D = bivec.norm dot this dot bivec.norm

    fun reflect(vec: Vector3D): Vector3D = (vec.norm * this * vec.norm).vec
    fun reflect(bivec: BiVector3D): Vector3D = (bivec.norm * this * bivec.norm).vec

    fun linearTrans(xTrans: Vector, yTrans: Vector, zTrans: Vector): Vector = (xTrans * x) + (yTrans * y) + (zTrans * z)

    fun orthoProj(bivec: BiVector3D): Vector2D {

        val xPrime = X.proj(bivec)
        val yPrime = Y.proj(bivec)
        val zPrime = Z.proj(bivec)

        return linearTrans(xPrime, yPrime, zPrime).to3D().rotateFromTo(bivec, XY).to2D()
    }
    fun orthoProj(vec: Vector3D): Vector1D {

        val xPrime = X.proj(vec)
        val yPrime = Y.proj(vec)
        val zPrime = Z.proj(vec)

        return linearTrans(xPrime, yPrime, zPrime).to3D().rotateFromTo(vec, X).to1D()
    }

    fun perspecProj(bivec: BiVector3D, dist: Double): Vector2D {

        val xPrime = X.proj(bivec)
        val yPrime = Y.proj(bivec)
        val zPrime = Z.proj(bivec)
        val projDist = dist - proj(-bivec*I).mag

        return linearTrans(xPrime/projDist, yPrime/projDist, zPrime/projDist).to3D().rotateFromTo(bivec, XY).to2D()
    }
    fun perspecProj(vec: Vector3D, dist: Double): Vector1D {

        val xPrime = X.proj(vec)
        val yPrime = Y.proj(vec)
        val zPrime = Z.proj(vec)
        val projDist = dist - proj(-vec*I).mag

        return linearTrans(xPrime/projDist, yPrime/projDist, zPrime/projDist).to3D().rotateFromTo(vec, X).to1D()
    }

    fun rotateFromTo(from: Vector3D, to: Vector3D): Vector3D = rotate(acos((from.norm) dot (to.norm)), from.norm wedge to.norm)
    fun rotateFromTo(from: BiVector3D, to: BiVector3D): Vector3D = rotate(acos((from.norm*I) dot (to.norm*I)), (from*I wedge to*I).norm)
}
class BiVector3D : BiVector {
    val xy: Double
    val yz: Double
    val zx: Double

    companion object {
        @JvmField val NaN = BiVector3D(Double.NaN, Double.NaN, Double.NaN)
    }

    override val dimension get() = THREE

    constructor(xy: Number = 0.0, yz: Number = 0.0, zx: Number = 0.0) {
        this.xy = xy.toDouble()
        this.yz = yz.toDouble()
        this.zx = zx.toDouble()
    }
    constructor(bivec: BiVector2D, yz: Number = 0.0, zx: Number = 0.0) {
        xy = bivec.xy
        this.yz = yz.toDouble()
        this.zx = zx.toDouble()
    }

    operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector3D) = that + this
    operator fun plus(that: BiVector3D) = BiVector3D(xy + that.xy, yz + that.yz, zx + that.zx)
    operator fun plus(that: TriVector3D) = that + this
    operator fun plus(that: MultiVector3D) = that + this

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector3D) = this + -that
    operator fun minus(that: BiVector3D) = this + -that
    operator fun minus(that: TriVector3D) = this + -that
    operator fun minus(that: MultiVector3D) = this + -that

    infix fun dot(that: Vector3D) = -(that dot this)
    infix fun dot(that: BiVector3D) = -(xy * that.xy + yz * that.yz + zx * that.zx)
    infix fun dot(that: TriVector3D) = Vector3D(-yz * that.xyz, -zx * that.xyz, -xy * that.xyz)

    infix fun cross(that: BiVector3D) = BiVector3D(zx * that.yz - yz * that.zx, xy * that.zx - zx * that.xy, yz * that.xy - xy * that.yz)

    infix fun wedge(that: Number) = this * that
    infix fun wedge(that: Vector3D) = that wedge this
    infix fun wedge(that: BiVector3D) = 0.0
    infix fun wedge(that: TriVector3D) = 0.0

    override val sqrMag: Double get() = -(this dot this)
    override val mag: Double get() = sqrt(sqrMag)
    override val norm: BiVector3D get() = if (isZero()) BiVector3D() else this / mag

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector3D) = (this dot that) + (this wedge that)
    operator fun times(that: BiVector3D) = (this dot that) + (this cross that)
    operator fun times(that: TriVector3D) = this dot that
    operator fun times(that: MultiVector3D) = this * that.scalar + this * that.vec + this * that.bivec + this * that.trivec

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector3D) = this * (1/that)
    operator fun div(that: BiVector3D) = this * (1/that)
    operator fun div(that: TriVector3D) = this * (1/that)
    operator fun div(that: MultiVector3D) = this * (1/that)

    operator fun unaryMinus() = -1 * this

    fun vectorDecomposition(): Array<Vector3D> {
        return if (xy != 0.0) arrayOf(Vector3D(xy,0.0,-yz), Vector3D(0.0, 1.0, -zx/xy))
        else arrayOf(Vector3D(-zx, yz, 0.0), Vector3D(0, 0 ,1))
    }

    fun rotate(theta: Double, plane: BiVector3D): BiVector3D {
        val vectors = vectorDecomposition()
        return vectors[0].rotate(theta, plane) wedge vectors[1].rotate(theta, plane)
    }

    override fun toString(): String {
        return if (!isZero()){
            val terms = mutableListOf<String>()
            if (xy != 0.0) terms.add("${xy}xy")
            if (yz != 0.0) terms.add("${yz}yz")
            if (zx != 0.0) terms.add("${zx}zx")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero(): Boolean = xy == 0.0 && yz == 0.0 && zx == 0.0
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is BiVector4D -> xy == other.xy && yz == other.yz && zx == other.zx && other.wx == 0.0 && other.wy == 0.0 && other.wz == 0.0
            is BiVector3D -> xy == other.xy && yz == other.yz && zx == other.zx
            is BiVector2D -> xy == other.xy && yz == 0.0      && zx == 0.0

            is MultiVector4D -> other.scalar == 0.0 && other.vec.isZero() && equals(other.bivec) && other.trivec.isZero() && other.quadvec.isZero()
            is MultiVector3D -> other.scalar == 0.0 && other.vec.isZero() && equals(other.bivec) && other.trivec.isZero()
            is MultiVector2D -> other.scalar == 0.0 && other.vec.isZero() && equals(other.bivec)

            is Number -> isZero() && other.toDouble() == 0.0

            else -> false
        }
    }
    override fun hashCode(): Int {
        var result = xy.hashCode()
        result = 31 * result + yz.hashCode()
        result = 31 * result + zx.hashCode()
        return result
    }

    fun proj(bivec: BiVector3D): BiVector3D = (-bivec.norm dot this) * bivec.norm

}
class TriVector3D(xyz: Number = 0.0) : TriVector {
    val xyz: Double = xyz.toDouble()

    companion object {
        @JvmField val NaN = TriVector3D(Double.NaN)
    }

    override val dimension get() = THREE

    operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector3D) = MultiVector3D(vec = that, trivec = this)
    operator fun plus(that: BiVector3D) = MultiVector3D(bivec = that, trivec = this)
    operator fun plus(that: TriVector3D) = TriVector3D(xyz + that.xyz)
    operator fun plus(that: MultiVector3D) = that + this

    infix fun dot(that: Vector3D) = that dot this
    infix fun dot(that: BiVector3D) = that dot this
    infix fun dot(that: TriVector3D) = -xyz * that.xyz

    override infix fun wedge(that: Number) = this * that
    infix fun wedge(that: Vector3D) = 0.0
    infix fun wedge(that: BiVector3D) = 0.0
    infix fun wedge(that: TriVector3D) = 0.0

    override val sqrMag: Double get() = xyz.pow(2)
    override val mag: Double get() = xyz.absoluteValue
    override val norm get() = TriVector3D(xyz.sign)

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector3D) = this dot that
    operator fun times(that: BiVector3D) = this dot that
    operator fun times(that: TriVector3D) = this dot that
    operator fun times(that: MultiVector3D) = this * that.scalar + this * that.vec + this * that.bivec + this * that.trivec

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector3D) = this * (1/that)
    operator fun div(that: BiVector3D) = this * (1/that)
    operator fun div(that: TriVector3D) = this * (1/that)
    operator fun div(that: MultiVector3D) = this * (1/that)

    operator fun unaryMinus() = -1 * this

    override fun toString(): String {
        return if(!isZero()){
            val terms = mutableListOf<String>()
            if (xyz != 0.0) terms.add("${xyz}xyz")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero(): Boolean = xyz == 0.0
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is TriVector4D -> xyz == other.xyz && other.xyw == 0.0 && other.yzw == 0.0 && other.zxw == 0.0
            is TriVector3D -> xyz == other.xyz

            is MultiVector4D -> other.scalar == 0.0 && other.vec.isZero() && other.bivec.isZero() && equals(other.trivec) && other.quadvec.isZero()
            is MultiVector3D -> other.scalar == 0.0 && other.vec.isZero() && other.bivec.isZero() && equals(other.trivec)

            is Number -> isZero() && other.toDouble() == 0.0

            else -> false
        }
    }
    override fun hashCode(): Int {
        return xyz.hashCode()
    }
}

class MultiVector3D(scalar: Number = 0.0, val vec: Vector3D = Vector3D(), val bivec: BiVector3D = BiVector3D(), val trivec: TriVector3D = TriVector3D()) : MultiVector {
    val scalar = scalar.toDouble()

    companion object {
        @JvmField val NaN = Double.NaN + Vector3D.NaN + BiVector3D.NaN + TriVector3D.NaN
    }

    override val dimension get() = THREE

    operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector3D) = this + MultiVector3D(vec = that)
    operator fun plus(that: BiVector3D) = this + MultiVector3D(bivec = that)
    operator fun plus(that: TriVector3D) = this + MultiVector3D(trivec = that)
    operator fun plus(that: MultiVector3D) = MultiVector3D(scalar + that.scalar, vec + that.vec, bivec + that.bivec, trivec + that.trivec)

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector3D) = this + -that
    operator fun minus(that: BiVector3D) = this + -that
    operator fun minus(that: TriVector3D) = this + -that
    operator fun minus(that: MultiVector3D) = this + -that

    val conj: MultiVector3D get() = MultiVector3D(scalar, -vec, -bivec, trivec) * ((scalar.pow(2) - vec.sqrMag + bivec.sqrMag - trivec.sqrMag) - 2 * scalar * trivec)
    override val norm: MultiVector3D get() = MultiVector3D(scalar.sign, vec.norm, bivec.norm, trivec.norm)

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector3D) = this.scalar * that + this.vec * that + this.bivec * that + this.trivec * that
    operator fun times(that: BiVector3D) = this.scalar * that + this.vec * that + this.bivec * that + this.trivec * that
    operator fun times(that: TriVector3D) = this.scalar * that + this.vec * that + this.bivec * that + this.trivec * that
    operator fun times(that: MultiVector3D) = this.scalar * that + this.vec * that + this.bivec * that + this.trivec * that

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector3D) = this * (1/that)
    operator fun div(that: BiVector3D) = this * (1/that)
    operator fun div(that: TriVector3D) = this * (1/that)
    operator fun div(that: MultiVector3D) = this * (1/that)

    operator fun unaryMinus() = -1 * this

    override fun toString(): String {
        return if (!isZero()) {
            val terms = mutableListOf<String>()
            if (scalar != 0.0) terms.add(scalar.toString())
            if (!vec.isZero()) terms.add("(${vec})")
            if (!bivec.isZero()) terms.add("(${bivec})")
            if (!trivec.isZero()) terms.add("(${trivec})")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero() = scalar == 0.0 && vec.isZero() && bivec.isZero() && trivec.isZero()

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Vector1D, is Vector2D, is Vector3D, is Vector4D, is BiVector2D, is BiVector3D, is BiVector4D, is TriVector3D, is TriVector4D -> other == this

            is Number -> scalar == other && vec.isZero() && bivec.isZero() && trivec.isZero()

            is MultiVector4D -> scalar == other.scalar && vec.equals(other.vec) && bivec.equals(other.bivec) && trivec.equals(other.trivec) && other.quadvec.isZero()
            is MultiVector3D -> scalar == other.scalar && vec.equals(other.vec) && bivec.equals(other.bivec) && trivec.equals(other.trivec)
            is MultiVector2D -> scalar == other.scalar && vec.equals(other.vec) && bivec.equals(other.bivec) && trivec.isZero()
            is MultiVector1D -> scalar == other.scalar && vec.equals(other.vec) && bivec.isZero()            && trivec.isZero()

            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = vec.hashCode()
        result = 31 * result + bivec.hashCode()
        result = 31 * result + trivec.hashCode()
        result = 31 * result + scalar.hashCode()
        return result
    }
}

