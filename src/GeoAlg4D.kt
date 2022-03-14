import GeoAlg4D.W
import GeoAlg4D.X
import GeoAlg4D.Y
import GeoAlg4D.Z
import GeoAlg4D.rotor
import Dimension.*
import kotlin.math.*

object GeoAlg4D {
    @JvmStatic fun rotor(theta: Double, plane: BiVector4D) = arrayOf(cos(theta/2) - (plane.norm * sin(theta/2)), cos(theta/2) + (plane.norm * sin(theta/2)))

    @JvmField val X = Vector4D(x = 1)
    @JvmField val Y = Vector4D(y = 1)
    @JvmField val Z = Vector4D(z = 1)
    @JvmField val W = Vector4D(w = 1)

    @JvmField val XY = BiVector4D(xy = 1)
    @JvmField val YZ = BiVector4D(yz = 1)
    @JvmField val ZX = BiVector4D(zx = 1)
    @JvmField val WX = BiVector4D(wx = 1)
    @JvmField val WY = BiVector4D(wy = 1)
    @JvmField val WZ = BiVector4D(wz = 1)

    @JvmField val XYZ = TriVector4D(xyz = 1)
    @JvmField val XYW = TriVector4D(xyw = 1)
    @JvmField val YZW = TriVector4D(yzw = 1)
    @JvmField val ZXW = TriVector4D(zxw = 1)

    @JvmField val J = QuadVector4D(1)

    @JvmStatic fun exp(vec: Vector4D) = cosh(vec.mag) + sinh(vec.mag) * vec.norm
    @JvmStatic fun exp(bivec: BiVector4D): MultiVector4D {
        return if (bivec.simple) {
            cos(bivec.mag) + sin(bivec.mag) * bivec.norm
        } else {
            var temp = MultiVector4D(1)
            for (bivector in bivec.simpleBivectorDecomposition()) {
                temp *= exp(bivector)
            }
            temp
        }
    }
    @JvmStatic fun exp(trivec: TriVector4D) = cos(trivec.mag) + sin(trivec.mag) * trivec.norm
    @JvmStatic fun exp(quadvec: QuadVector4D) = cosh(quadvec.mag) + sinh(quadvec.mag) * quadvec.norm

    @JvmStatic fun ln(scalar: Double, basis: BiVector4D) = ln(scalar.absoluteValue) + if (scalar.sign == -1.0) PI*basis.norm else BiVector4D()
    @JvmStatic fun ln(scalar: Double, basis: TriVector4D) = ln(scalar.absoluteValue) + if (scalar.sign == -1.0) PI*basis.norm else TriVector4D()

    @JvmStatic fun ln(vec: Vector4D, basis: BiVector4D) = ln(vec.mag) + (3*PI*basis.norm/2) + (PI*basis.norm/2 * vec.norm)
    @JvmStatic fun ln(vec: Vector4D, basis: TriVector4D) = ln(vec.mag) + (3*PI*basis.norm/2) + (PI*basis.norm/2 * vec.norm)

    @JvmStatic fun ln(bivec: BiVector4D) = ln(bivec.mag) + PI*bivec.norm/2
    @JvmStatic fun ln(trivec: TriVector4D) = ln(trivec.mag) + PI*trivec.norm/2
}

class Vector4D : Vector {
    val x: Double
    val y: Double
    val z: Double
    val w: Double

    companion object {
        @JvmField val NaN = Vector4D(Double.NaN, Double.NaN, Double.NaN, Double.NaN)
    }

    override val dimension get() = FOUR

    constructor(x: Number = 0.0, y: Number = 0.0, z: Number = 0.0, w: Number = 0.0) {
        this.x = x.toDouble()
        this.y = y.toDouble()
        this.z = z.toDouble()
        this.w = w.toDouble()
    }
    constructor(vec: Vector2D, z: Number = 0.0, w: Number = 0.0) {
        x = vec.x
        y = vec.y
        this.z = z.toDouble()
        this.w = w.toDouble()
    }
    constructor(vec: Vector3D, w: Number = 0.0) {
        x = vec.x
        y = vec.y
        z = vec.z
        this.w = w.toDouble()
    }

    override operator fun plus(that: Number) = MultiVector4D(vec = this, scalar = that.toDouble())
    operator fun plus(that: Vector4D) = Vector4D(x + that.x, y + that.y, z + that.z, w + that.w)
    operator fun plus(that: BiVector4D) = MultiVector4D(vec = this, bivec = that)
    operator fun plus(that: TriVector4D) = MultiVector4D(vec = this, trivec = that)
    operator fun plus(that: QuadVector4D) = MultiVector4D(vec = this, quadvec = that)
    operator fun plus(that: MultiVector4D) = that + this

    override operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector4D) = this + -that
    operator fun minus(that: BiVector4D) = this + -that
    operator fun minus(that: TriVector4D) = this + -that
    operator fun minus(that: QuadVector4D) = this + -that
    operator fun minus(that: MultiVector4D) = this + -that

    infix fun dot(that: Vector4D) = x * that.x + y * that.y + z * that.z + w * that.w
    infix fun dot(that: BiVector4D) = Vector4D(-y * that.xy + z * that.zx + w * that.wx, x * that.xy - z * that.yz + w * that.wy, -x * that.zx + y * that.yz + w * that.wz, -x * that.wx - y * that.wy - z * that.wz)
    infix fun dot(that: TriVector4D) = BiVector4D(z * that.xyz + w * that.xyw, x * that.xyz + w * that.yzw, y * that.xyz + w * that.zxw, y * that.xyw - z * that.zxw, -x * that.xyw +z * that.yzw, x * that.zxw - y * that.yzw)
    infix fun dot(that: QuadVector4D) = TriVector4D(-w * that.xyzw, z * that.xyzw, x * that.xyzw, y * that.xyzw)

    infix fun wedge(that: Vector4D) = BiVector4D(x * that.y - y * that.x, y * that.z - z * that.y, z * that.x - x * that.z, w * that.x - x * that.w, w * that.y - y * that.w, w * that.z - z * that.w)
    infix fun wedge(that: BiVector4D) = TriVector4D(x * that.yz + y * that.zx + z * that.xy, -x * that.wy + y * that.wx + w * that.xy, -y * that.wz + z * that.wy + w * that.yz, x * that.wz - z * that.wx + w * that.zx)
    infix fun wedge(that: TriVector4D) = QuadVector4D(x * that.yzw + y * that.zxw - z * that.xyw - w * that.xyz)
    infix fun wedge(that: QuadVector4D) = 0.0

    override val sqrMag: Double get() = this dot this
    override val norm: Vector4D get() = if (isZero()) Vector4D() else this / mag

    override operator fun times(that: Number) = that * this
    operator fun times(that: Vector4D) = (this dot that) + (this wedge that)
    operator fun times(that: BiVector4D) = (this dot that) + (this wedge that)
    operator fun times(that: TriVector4D) = (this dot that) + (this wedge that)
    operator fun times(that: QuadVector4D) = this dot that
    operator fun times(that:MultiVector4D) = (this * that.scalar) + (this * that.vec) + (this * that.bivec) + (this * that.trivec) + (this * that.quadvec)

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector4D) = this * (1/that)
    operator fun div(that: BiVector4D) = this * (1/that)
    operator fun div(that: TriVector4D) = this * (1/that)
    operator fun div(that: QuadVector4D) = this * (1/that)

    operator fun unaryMinus() = -1 * this

    fun pow(n: Int): MultiVector4D = when {
        n > 0 && n.mod(2) == 0 -> MultiVector4D(mag.pow(n))
        n > 0 && n.mod(2) != 0 -> MultiVector4D(vec = mag.pow(n) * norm)
        n < 0 && n.mod(2) == 0 -> MultiVector4D(1/mag.pow(n))
        n < 0 && n.mod(2) != 0 -> MultiVector4D(vec = 1/mag.pow(n) * norm)
        else -> MultiVector4D(1.0)
    }

    fun rotate(theta: Double, plane: BiVector4D): Vector4D {
        val rotor = rotor(theta, plane)
        return (rotor[0] * this * rotor[1]).vec
    }

    override fun toString(): String {
        return if(!isZero()){
            val terms = mutableListOf<String>()
            if (x != 0.0) terms.add("${x}x")
            if (y != 0.0) terms.add("${y}y")
            if (z != 0.0) terms.add("${z}z")
            if (w != 0.0) terms.add("${w}w")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }
    override fun isZero(): Boolean = x == 0.0 && y == 0.0 && z == 0.0 && w == 0.0
    override fun equals(other: Any?): Boolean {
        return when (other){
            is Vector4D -> x == other.x && y == other.y && z == other.z && w == other.w
            is Vector3D -> x == other.x && y == other.y && z == other.z && w == 0.0
            is Vector2D -> x == other.x && y == other.y && z == 0.0     && w == 0.0

            is MultiVector4D -> other.scalar == 0.0 && equals(other.vec) && other.bivec.isZero() && other.trivec.isZero() && other.quadvec.isZero()
            is MultiVector3D -> other.scalar == 0.0 && equals(other.vec) && other.bivec.isZero() && other.trivec.isZero()
            is MultiVector2D -> other.scalar == 0.0 && equals(other.vec) && other.bivec.isZero()

            is Number -> isZero() && other.toDouble() == 0.0

            else -> false
        }
    }
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + w.hashCode()
        return result
    }

    fun proj(vec: Vector4D) = (vec.norm dot this) * vec.norm
    fun proj(bivec: BiVector4D) = bivec.norm dot this dot bivec.norm
    fun proj(trivec: TriVector4D) = -trivec.norm dot this dot trivec.norm

    fun reflect(vec: Vector4D): Vector4D = (vec.norm * this * vec.norm).vec
    fun reflect(bivec: BiVector4D): Vector4D = (bivec.norm * this * bivec.norm).vec
    fun reflect(trivec: TriVector4D): Vector4D = (-trivec.norm * this * trivec.norm).vec

    fun linearTrans(xTrans: Vector, yTrans: Vector, zTrans: Vector, wTrans: Vector): Vector = (xTrans * x) + (yTrans * y) + (zTrans * z) + (wTrans * w)
}
class BiVector4D : BiVector {
    val xy: Double
    val yz: Double
    val zx: Double
    val wx: Double
    val wy: Double
    val wz: Double

    companion object {
        @JvmField val NaN = BiVector4D(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN)
    }

    override val dimension get() = FOUR

    constructor(xy: Number = 0.0, yz: Number = 0.0, zx: Number = 0.0, wx: Number = 0.0, wy: Number = 0.0, wz: Number = 0.0) {
        this.xy = xy.toDouble()
        this.yz = yz.toDouble()
        this.zx = zx.toDouble()
        this.wx = wx.toDouble()
        this.wy = wy.toDouble()
        this.wz = wz.toDouble()
    }
    constructor(bivec: BiVector2D, yz: Number = 0.0, zx: Number = 0.0, wx: Number = 0.0, wy: Number = 0.0, wz: Number = 0.0) {
        this.xy = bivec.xy
        this.yz = yz.toDouble()
        this.zx = zx.toDouble()
        this.wx = wx.toDouble()
        this.wy = wy.toDouble()
        this.wz = wz.toDouble()
    }
    constructor(bivec: BiVector3D, wx: Number = 0.0, wy: Number = 0.0, wz: Number = 0.0) {
        this.xy = bivec.xy
        this.yz = bivec.yz
        this.zx = bivec.zx
        this.wx = wx.toDouble()
        this.wy = wy.toDouble()
        this.wz = wz.toDouble()
    }

    operator fun plus(that: Number) = MultiVector4D(bivec = this, scalar = that.toDouble())
    operator fun plus(that: Vector4D) = MultiVector4D(bivec = this, vec = that)
    operator fun plus(that: BiVector4D) = BiVector4D(xy + that.xy, yz + that.yz, zx + that.zx, wx + that.wx, wy + that.wy, wz + that.wz)
    operator fun plus(that: TriVector4D) = MultiVector4D(bivec = this, trivec = that)
    operator fun plus(that: QuadVector4D) = MultiVector4D(bivec = this, quadvec = that)
    operator fun plus(that: MultiVector4D) = that + this

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector4D) = this + -that
    operator fun minus(that: BiVector4D) = this + -that
    operator fun minus(that: TriVector4D) = this + -that
    operator fun minus(that: QuadVector4D) = this + -that
    operator fun minus(that: MultiVector4D) = this + -that

    infix fun dot(that: Vector4D) = -(that dot this)
    infix fun dot(that: BiVector4D) = -(xy * that.xy + yz * that.yz + zx * that.zx + wx * that.wx + wy * that.wy + wz * that.wz)
    infix fun dot(that: TriVector4D) = Vector4D(-yz * that.xyz + wy * that.xyw - wz * that.zxw, -zx * that.xyz - wx * that.xyw + wz * that.yzw, -xy * that.xyz - wx * that.zxw - wy * that.yzw, -xy * that.xyw - yz * that.yzw - zx * that.zxw)
    infix fun dot(that: QuadVector4D) = BiVector4D(wz * that.xyzw, wx * that.xyzw, wy * that.xyzw, yz * that.xyzw, zx * that.xyzw, xy * that.xyzw)

    infix fun cross(that: BiVector4D) = BiVector4D(-yz * that.zx + zx * that.yz - wx * that.wy + wy * that.wx, xy * that.zx - zx * that.xy - wy * that.wz + wz * that.wy, -xy * that.yz + yz * that.xy + wx * that.wz - wz * that.wx, xy * that.wy - wy * that.xy - zx * that.wz + wz * that.zx, -xy * that.wx + wx * that.xy + yz * that.wz - wz * that.yz, -yz * that.wy + wy * that.yz + zx * that.wx - wx * that.zx)
    infix fun cross(that: TriVector4D) = TriVector4D(-wx * that.yzw - wy * that.zxw - wz * that.xyw, wz * that.xyz + zx * that.yzw - yz * that.zxw, xy * that.zxw - zx * that.xyw + wx * that.xyz, -xy * that.yzw + yz * that.xyw + wy * that.xyz)

    infix fun wedge(that: Number) = this * that
    infix fun wedge(that: Vector4D) = that wedge this
    infix fun wedge(that: BiVector4D) = QuadVector4D(-(xy * that.wz + yz * that.wx + zx * that.wy + wx * that.yz + wy * that.zx + wz * that.xy))
    infix fun wedge(that: TriVector4D) = 0.0
    infix fun wedge(that: QuadVector4D) = 0.0

    override val sqrMag: Double get() {
        return if (simple) (this dot this).absoluteValue
        else Double.NaN
    }
    override val norm: BiVector4D get() {
        return when {
            isZero() -> BiVector4D()
            simple -> this / mag
            else -> {
                var bivec1 = BiVector4D()
                for (bivec in simpleBivectorDecomposition()) {
                    bivec1 += bivec.norm
                }
                bivec1
            }
        }
    }

    val simple get() = xy * wz == 0.0 && yz * wx == 0.0 && zx * wy == 0.0

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector4D) = (this dot that) + (this wedge that)
    operator fun times(that: BiVector4D) = (this dot that) + (this cross that) + (this wedge that)
    operator fun times(that: TriVector4D) = (this dot that) + (this cross that)
    operator fun times(that: QuadVector4D) = this dot that
    operator fun times(that:MultiVector4D) = (this * that.scalar) + (this * that.vec) + (this * that.bivec) + (this * that.trivec) + (this * that.quadvec)

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector4D) = this * (1/that)
    operator fun div(that: BiVector4D) = this * (1/that)
    operator fun div(that: TriVector4D) = this * (1/that)
    operator fun div(that: QuadVector4D) = this * (1/that)

    operator fun unaryMinus() = BiVector4D(-xy, -yz, -zx, -wx, -wy, -wz)

    fun pow(n: Int): MultiVector4D = when {
        n > 0 && n.mod(2) == 0 -> MultiVector4D(mag.pow(n) * (-1.0).pow(floor(n/2.0)))
        n > 0 && n.mod(2) != 0 -> MultiVector4D(bivec = mag.pow(n) * norm * (-1.0).pow(floor(n/2.0)))
        n < 0 && n.mod(2) == 0 -> MultiVector4D(1/mag.pow(n) * (-1.0).pow(floor(n/2.0)))
        n < 0 && n.mod(2) != 0 -> MultiVector4D(bivec = 1/mag.pow(n) * norm * (-1.0).pow(floor(n/2.0)))
        else -> MultiVector4D(1.0)
    }

    fun simpleBivectorDecomposition(): Array<BiVector4D> {
        return when {
            simple -> arrayOf(this)
            else -> arrayOf(BiVector4D(xy, yz, zx), BiVector4D(wx = wx, wy = wy, wz = wz))
        }
    }
    fun vectorDecomposition(): Array<Array<Vector4D>> {
        val vectors: MutableList<Array<Vector4D>> = mutableListOf()
        when {
            !simple -> for (bivec in this.simpleBivectorDecomposition()) {
                vectors.add(bivec.vectorDecomposition()[0])
            }
            wx == 0.0 && wy == 0.0 && wz == 0.0 -> if (xy != 0.0) arrayOf(Vector4D(x = xy, z = -yz), Vector4D(y = 1, z = -zx/xy))
                                                   else arrayOf(Vector4D(x = -zx, y = yz), Z)
            xy == 0.0 && yz == 0.0 && zx == 0.0 -> vectors.add(arrayOf(W, Vector4D(wx, wy, wz)))
            xy == 0.0 && wx == 0.0 && wy == 0.0 -> vectors.add(arrayOf(Vector4D(x = -zx, y = yz, w = wz), Z))
            wz == 0.0 && yz == 0.0 && wy == 0.0 -> vectors.add(arrayOf(Vector4D(y = -xy, z = zx, w = wx), X))
            wz == 0.0 && wx == 0.0 && zx == 0.0 -> vectors.add(arrayOf(Vector4D(x = xy, z = -yz, w = wy), Y))
            xy == 0.0 && yz == 0.0 && wy == 0.0 -> if (wx != 0.0) vectors.add(arrayOf(Vector4D(x = -wx, z = -wz), Vector4D(z = zx/wx, w = 1)))
                                                   else vectors.add(arrayOf(Vector4D(x = -zx, w = wz), Z))
            xy == 0.0 && wx == 0.0 && zx == 0.0 -> if (yz != 0.0) vectors.add(arrayOf(Vector4D(y = yz, w = wz), Vector4D(z = 1, w = -wy/yz)))
                                                   else vectors.add(arrayOf(Vector4D(y = -wy, z = -wz), W))
            wz == 0.0 && yz == 0.0 && zx == 0.0 -> if (xy != 0.0) vectors.add(arrayOf(Vector4D(x = xy, w = wy), Vector4D(y = 1, w = -wx/xy)))
                                                   else vectors.add(arrayOf(Vector4D(x = -wx, y = -wy), W))

        }
        return vectors.toTypedArray()
    }

    fun rotate(theta: Double, plane: BiVector4D): BiVector4D {
        val bivectors = vectorDecomposition()
        var bivec = BiVector4D()
        for (vectors in bivectors) {
            bivec += vectors[0].rotate(theta, plane) wedge vectors[1].rotate(theta, plane)
        }
        return bivec
    }

    override fun toString(): String {
        return if (!isZero()){
            val terms = mutableListOf<String>()
            if (xy != 0.0) terms.add("${xy}xy")
            if (yz != 0.0) terms.add("${yz}yz")
            if (zx != 0.0) terms.add("${zx}zx")
            if (wx != 0.0) terms.add("${wx}wx")
            if (wy != 0.0) terms.add("${wy}wy")
            if (wz != 0.0) terms.add("${wz}wz")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero(): Boolean = xy == 0.0 && yz == 0.0 && zx == 0.0 && wx == 0.0 && wy == 0.0 && wz == 0.0
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is BiVector4D -> xy == other.xy && yz == other.yz && zx == other.zx && wx == other.wx && wy == other.wy && wz == other.wz
            is BiVector3D -> xy == other.xy && yz == other.yz && zx == other.zx && wx == 0.0      && wy == 0.0      && wz == 0.0
            is BiVector2D -> xy == other.xy && yz == 0.0      && zx == 0.0      && wx == 0.0      && wy == 0.0      && wz == 0.0

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
        result = 31 * result + wx.hashCode()
        result = 31 * result + wy.hashCode()
        result = 31 * result + wz.hashCode()
        return result
    }
}
class TriVector4D : TriVector {
    val xyz: Double
    val xyw: Double
    val yzw: Double
    val zxw: Double

    companion object {
        @JvmField val NaN = TriVector4D(Double.NaN, Double.NaN, Double.NaN, Double.NaN)
    }

    override val dimension get() = FOUR

    constructor(xyz: Number = 0.0, xyw: Number = 0.0, yzw: Number = 0.0, zxw: Number = 0.0) {
        this.xyz = xyz.toDouble()
        this.xyw = xyw.toDouble()
        this.yzw = yzw.toDouble()
        this.zxw = zxw.toDouble()
    }
    constructor(trivec: TriVector3D, xyw: Number = 0.0, yzw: Number = 0.0, zxw: Number = 0.0) {
        xyz = trivec.xyz
        this.xyw = xyw.toDouble()
        this.yzw = yzw.toDouble()
        this.zxw = zxw.toDouble()
    }

    operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector4D) = MultiVector4D(trivec = this, vec = that)
    operator fun plus(that: BiVector4D) = MultiVector4D(trivec = this, bivec = that)
    operator fun plus(that: TriVector4D) = TriVector4D(xyz + that.xyz, xyw + that.xyw, yzw + that.yzw, zxw + that.zxw)
    operator fun plus(that: QuadVector4D) = MultiVector4D(trivec = this, quadvec = that)
    operator fun plus(that: MultiVector4D) = that + this

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector4D) = this + -that
    operator fun minus(that: BiVector4D) = this + -that
    operator fun minus(that: TriVector4D) = this + -that
    operator fun minus(that: QuadVector4D) = this + -that
    operator fun minus(that: MultiVector4D) = this + -that

    infix fun dot(that: Vector4D) = that dot this
    infix fun dot(that: BiVector4D) = that dot this
    infix fun dot(that: TriVector4D) = -(xyz * that.xyz + xyw * that.xyw + yzw * that.yzw + zxw * that.zxw)
    infix fun dot(that: QuadVector4D) = Vector4D(yzw * that.xyzw, zxw * that.xyzw, xyw * that.xyzw, -xyz * that.xyzw)

    infix fun cross(that: BiVector4D) = -(that cross this)
    infix fun cross(that: TriVector4D) = BiVector4D(zxw * that.yzw - yzw * that.zxw, xyw * that.zxw - zxw * that.xyw, yzw * that.xyw - xyw * yzw, xyz * that.yzw - yzw * that.xyz, xyz * that.zxw - zxw * that.xyz, xyz * that.xyw - xyw * that.xyz)

    override infix fun wedge(that: Number) = this * that
    infix fun wedge(that: Vector4D) = -(that wedge this)
    infix fun wedge(that: BiVector4D) = 0.0
    infix fun wedge(that: TriVector4D) = 0.0
    infix fun wedge(that: QuadVector4D) = 0.0

    override val sqrMag: Double get() = -(this dot this)
    override val mag: Double get() = sqrt(sqrMag)
    override val norm: TriVector4D get() = if (isZero()) TriVector4D() else this / mag

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector4D) = (this dot that) + (this wedge that)
    operator fun times(that: BiVector4D) = (this dot that) + (this cross that)
    operator fun times(that: TriVector4D) = (this dot that) + (this cross that)
    operator fun times(that: QuadVector4D) = this dot that
    operator fun times(that:MultiVector4D) = (this * that.scalar) + (this * that.vec) + (this * that.bivec) + (this * that.trivec) + (this * that.quadvec)

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector4D) = this * (1/that)
    operator fun div(that: BiVector4D) = this * (1/that)
    operator fun div(that: TriVector4D) = this * (1/that)
    operator fun div(that: QuadVector4D) = this * (1/that)

    operator fun unaryMinus() = -1 * this

    fun pow(n: Int): MultiVector4D = when {
        n > 0 && n.mod(2) == 0 -> MultiVector4D(mag.pow(n) * (-1.0).pow(floor(n/2.0)))
        n > 0 && n.mod(2) != 0 -> MultiVector4D(trivec = mag.pow(n) * norm * (-1.0).pow(floor(n/2.0)))
        n < 0 && n.mod(2) == 0 -> MultiVector4D(1/mag.pow(n) * (-1.0).pow(floor(n/2.0)))
        n < 0 && n.mod(2) != 0 -> MultiVector4D(trivec = 1/mag.pow(n) * norm * (-1.0).pow(floor(n/2.0)))
        else -> MultiVector4D(1.0)
    }

    override fun toString(): String {
        return if(!isZero()){
            val terms = mutableListOf<String>()
            if (xyz != 0.0) terms.add("${xyz}xyz")
            if (xyw != 0.0) terms.add("${xyw}xyw")
            if (yzw != 0.0) terms.add("${yzw}yzw")
            if (zxw != 0.0) terms.add("${zxw}zxw")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero(): Boolean = xyz == 0.0 && xyw == 0.0 && yzw == 0.0 && zxw == 0.0

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is TriVector4D -> xyz == other.xyz && xyw == other.xyw && yzw == other.yzw && zxw == other.zxw
            is TriVector3D -> xyz == other.xyz && xyw == 0.0       && yzw == 0.0       && zxw == 0.0

            is MultiVector4D -> other.scalar == 0.0 && other.vec.isZero() && other.bivec.isZero() && equals(other.trivec) && other.quadvec.isZero()
            is MultiVector3D -> other.scalar == 0.0 && other.vec.isZero() && other.bivec.isZero() && equals(other.trivec)

            is Number -> isZero() && other.toDouble() == 0.0

            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = xyz.hashCode()
        result = 31 * result + xyw.hashCode()
        result = 31 * result + yzw.hashCode()
        result = 31 * result + zxw.hashCode()
        return result
    }
}
class QuadVector4D(xyzw: Number = 0.0) : QuadVector {
    val xyzw: Double = xyzw.toDouble()

    companion object {
        @JvmField val NaN = QuadVector4D(Double.NaN)
    }

    override val dimension get() = FOUR

    operator fun plus(that: Number) = MultiVector4D(quadvec = this, scalar = that.toDouble())
    operator fun plus(that: Vector4D) = MultiVector4D(quadvec = this, vec = that)
    operator fun plus(that: BiVector4D) = MultiVector4D(quadvec = this, bivec = that)
    operator fun plus(that: TriVector4D) = MultiVector4D(quadvec = this, trivec = that)
    operator fun plus(that: QuadVector4D) = QuadVector4D(xyzw + that.xyzw)
    operator fun plus(that: MultiVector4D) = that + this

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector4D) = this + -that
    operator fun minus(that: BiVector4D) = this + -that
    operator fun minus(that: TriVector4D) = this + -that
    operator fun minus(that: QuadVector4D) = this + -that
    operator fun minus(that: MultiVector4D) = this + -that

    infix fun dot(that: Vector4D) = -(that dot this)
    infix fun dot(that: BiVector4D) = that dot this
    infix fun dot(that: TriVector4D) = -(that dot this)
    infix fun dot(that: QuadVector4D) = xyzw * that.xyzw

    infix fun wedge(that: Number) = this * that
    infix fun wedge(that: Vector4D) = 0.0
    infix fun wedge(that: BiVector4D) = 0.0
    infix fun wedge(that: TriVector4D) = 0.0
    infix fun wedge(that: QuadVector4D) = 0.0

    override val sqrMag: Double get() = xyzw.pow(2)
    override val mag: Double get() = xyzw.absoluteValue
    override val norm get() = if (isZero()) QuadVector4D() else this / mag

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector4D) = this dot that
    operator fun times(that: BiVector4D) = this dot that
    operator fun times(that: TriVector4D) = this dot that
    operator fun times(that: QuadVector4D) = this dot that
    operator fun times(that:MultiVector4D) = (this * that.scalar) + (this * that.vec) + (this * that.bivec) + (this * that.trivec) + (this * that.quadvec)

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector4D) = this * (1/that)
    operator fun div(that: BiVector4D) = this * (1/that)
    operator fun div(that: TriVector4D) = this * (1/that)
    operator fun div(that: QuadVector4D) = this * (1/that)

    operator fun unaryMinus() = -1 * this

    override fun toString(): String {
        return if(!isZero()){
            val terms = mutableListOf<String>()
            if (xyzw != 0.0) terms.add("${xyzw}xyzw")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero(): Boolean = xyzw == 0.0

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is QuadVector4D -> xyzw == other.xyzw

            is MultiVector4D -> other.scalar == 0.0 && other.vec.isZero() && other.bivec.isZero() && other.trivec.isZero() && equals(other.quadvec)

            is Number -> isZero() && other.toDouble() == 0.0

            else -> false
        }
    }

    override fun hashCode(): Int {
        return xyzw.hashCode()
    }

}

class MultiVector4D(scalar: Number = 0.0, val vec: Vector4D = Vector4D(), val bivec: BiVector4D = BiVector4D(), val trivec: TriVector4D = TriVector4D(), val quadvec: QuadVector4D = QuadVector4D()) : MultiVector {
    val scalar = scalar.toDouble()

    companion object {
        @JvmField val NaN = Double.NaN + Vector4D.NaN + BiVector4D.NaN + TriVector4D.NaN + QuadVector4D.NaN
    }

    override val dimension get() = FOUR

    operator fun plus(that: Number) = that + this
    operator fun plus(that: Vector4D) = this + MultiVector4D(vec = that)
    operator fun plus(that: BiVector4D) = this + MultiVector4D(bivec = that)
    operator fun plus(that: TriVector4D) = this + MultiVector4D(trivec = that)
    operator fun plus(that: QuadVector4D) = this + MultiVector4D(quadvec = that)
    operator fun plus(that: MultiVector4D) = MultiVector4D(scalar + that.scalar, vec + that.vec, bivec + that.bivec, trivec + that.trivec, quadvec + that.quadvec)

    operator fun minus(that: Number) = this + -that.toDouble()
    operator fun minus(that: Vector4D) = this + -that
    operator fun minus(that: BiVector4D) = this + -that
    operator fun minus(that: TriVector4D) = this + -that
    operator fun minus(that: QuadVector4D) = this + -that
    operator fun minus(that: MultiVector4D) = this + -that

    override val norm: MultiVector4D get() = MultiVector4D(scalar.sign, vec.norm, bivec.norm, trivec.norm, quadvec.norm)

    operator fun times(that: Number) = that * this
    operator fun times(that: Vector4D) = (scalar * that) + (vec * that) + (bivec * that) + (trivec * that) + (quadvec * that)
    operator fun times(that: BiVector4D) = (scalar * that) + (vec * that) + (bivec * that) + (trivec * that) + (quadvec * that)
    operator fun times(that: TriVector4D) = (scalar * that) + (vec * that) + (bivec * that) + (trivec * that) + (quadvec * that)
    operator fun times(that: QuadVector4D) = (scalar * that) + (vec * that) + (bivec * that) + (trivec * that) + (quadvec * that)
    operator fun times(that: MultiVector4D) = (scalar * that) + (vec * that) + (bivec * that) + (trivec * that) + (quadvec * that)

    operator fun div(that: Number) = this * (1/that.toDouble())
    operator fun div(that: Vector4D) = this * (1/that)
    operator fun div(that: BiVector4D) = this * (1/that)
    operator fun div(that: TriVector4D) = this * (1/that)
    operator fun div(that: QuadVector4D) = this * (1/that)

    operator fun unaryMinus() = MultiVector4D(-scalar, -vec, -bivec, -trivec, -quadvec)

    override fun toString(): String {
        return if (!isZero()) {
            val terms = mutableListOf<String>()
            if (scalar != 0.0) terms.add(scalar.toString())
            if (!vec.isZero()) terms.add("(${vec})")
            if (!bivec.isZero()) terms.add("(${bivec})")
            if (!trivec.isZero()) terms.add("(${trivec})")
            if (!quadvec.isZero()) terms.add("(${quadvec})")
            Util.concatenate(terms.toTypedArray(), " + ")
        } else "0.0"
    }

    override fun isZero() = scalar == 0.0 && vec.isZero() && bivec.isZero() && trivec.isZero() && quadvec.isZero()
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Vector1D, is Vector2D, is Vector3D, is Vector4D, is BiVector2D, is BiVector3D, is BiVector4D, is TriVector3D, is TriVector4D, is QuadVector4D -> other == this

            is Number -> scalar == other && vec.isZero() && bivec.isZero() && trivec.isZero() && quadvec.isZero()

            is MultiVector4D -> scalar == other.scalar && vec.equals(other.vec) && bivec.equals(other.bivec) && trivec.equals(other.trivec) && quadvec.equals(other.quadvec)
            is MultiVector3D -> scalar == other.scalar && vec.equals(other.vec) && bivec.equals(other.bivec) && trivec.equals(other.trivec) && quadvec.isZero()
            is MultiVector2D -> scalar == other.scalar && vec.equals(other.vec) && bivec.equals(other.bivec) && trivec.isZero()             && quadvec.isZero()
            is MultiVector1D -> scalar == other.scalar && vec.equals(other.vec) && bivec.isZero()            && trivec.isZero()             && quadvec.isZero()

            else -> false
        }
    }
    override fun hashCode(): Int {
        var result = vec.hashCode()
        result = 31 * result + bivec.hashCode()
        result = 31 * result + trivec.hashCode()
        result = 31 * result + quadvec.hashCode()
        result = 31 * result + scalar.hashCode()
        return result
    }

}
