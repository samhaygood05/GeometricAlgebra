import kotlin.math.sqrt

interface MultiVector {

    val dimension: Int
    val norm: MultiVector

    operator fun unaryPlus() = this

    fun isZero(): Boolean

    infix fun dot(that: Number) = 0.0

}

interface KVector : MultiVector {
    val sqrMag: Double
    val mag: Double get() = sqrt(sqrMag)

    val grade: Int
}

interface Vector : KVector {

    override val mag: Double get() = sqrt(sqrMag)
    override val grade: Int get() = 1

    fun to1D(): Vector1D = when (this) {
            is Vector1D -> this
            is Vector2D -> Vector1D(x)
            is Vector3D -> Vector1D(x)
            is Vector4D -> Vector1D(x)
            else -> Vector1D()
        }
    fun to2D(): Vector2D = when (this) {
            is Vector1D -> Vector2D(x, 0)
            is Vector2D -> this
            is Vector3D -> Vector2D(x, y)
            is Vector4D -> Vector2D(x, y)
            else -> Vector2D()
        }
    fun to3D(): Vector3D = when (this) {
            is Vector1D -> Vector3D(x, 0, 0)
            is Vector2D -> Vector3D(this)
            is Vector3D -> this
            is Vector4D -> Vector3D(x, y, z)
            else -> Vector3D()
        }
    fun to4D(): Vector4D = when (this) {
            is Vector1D -> Vector4D(x, 0 ,0, 0)
            is Vector2D -> Vector4D(this)
            is Vector3D -> Vector4D(this)
            is Vector4D -> this
            else -> Vector4D()
        }

    operator fun plus(that: Vector): Vector = when (this) {
        is Vector1D -> when (that) {
            is Vector1D -> this + that
            is Vector2D -> this.to2D() + that
            is Vector3D -> this.to3D() + that
            is Vector4D -> this.to4D() + that
            else -> Vector1D()
        }
        is Vector2D -> when (that) {
            is Vector1D -> this + that.to2D()
            is Vector2D -> this + that
            is Vector3D -> this.to3D() + that
            is Vector4D -> this.to4D() + that
            else -> Vector1D()
        }
        is Vector3D -> when (that) {
            is Vector1D -> this + that.to3D()
            is Vector2D -> this + that.to3D()
            is Vector3D -> this + that
            is Vector4D -> this.to4D() + that
            else -> Vector1D()
        }
        is Vector4D -> when (that) {
            is Vector1D -> this + that.to4D()
            is Vector2D -> this + that.to4D()
            is Vector3D -> this + that.to4D()
            is Vector4D -> this + that
            else -> Vector1D()
        }
        else -> Vector1D()
    }
    operator fun minus(that: Vector): Vector = when (this) {
        is Vector1D -> when (that) {
            is Vector1D -> this - that
            is Vector2D -> this.to2D() - that
            is Vector3D -> this.to3D() - that
            is Vector4D -> this.to4D() - that
            else -> Vector1D()
        }
        is Vector2D -> when (that) {
            is Vector1D -> this - that.to2D()
            is Vector2D -> this - that
            is Vector3D -> this.to3D() - that
            is Vector4D -> this.to4D() - that
            else -> Vector1D()
        }
        is Vector3D -> when (that) {
            is Vector1D -> this - that.to3D()
            is Vector2D -> this - that.to3D()
            is Vector3D -> this - that
            is Vector4D -> this.to4D() - that
            else -> Vector1D()
        }
        is Vector4D -> when (that) {
            is Vector1D -> this - that.to4D()
            is Vector2D -> this - that.to4D()
            is Vector3D -> this - that.to4D()
            is Vector4D -> this - that
            else -> Vector1D()
        }
        else -> Vector1D()
    }

    infix fun dot(that: Vector): Double = when (this) {
        is Vector1D -> when (that) {
            is Vector1D -> this dot that
            is Vector2D -> this.to2D() dot that
            is Vector3D -> this.to3D() dot that
            is Vector4D -> this.to4D() dot that
            else -> Double.NaN
        }
        is Vector2D -> when (that) {
            is Vector1D -> this dot that.to2D()
            is Vector2D -> this dot that
            is Vector3D -> this.to3D() dot that
            is Vector4D -> this.to4D() dot that
            else -> Double.NaN
        }
        is Vector3D -> when (that) {
            is Vector1D -> this dot that.to3D()
            is Vector2D -> this dot that.to3D()
            is Vector3D -> this dot that
            is Vector4D -> this.to4D() dot that
            else -> Double.NaN
        }
        is Vector4D -> when (that) {
            is Vector1D -> this dot that.to4D()
            is Vector2D -> this dot that.to4D()
            is Vector3D -> this dot that.to4D()
            is Vector4D -> this dot that
            else -> Double.NaN
        }
        else -> Double.NaN
    }
    infix fun wedge(that: Vector): BiVector = when (this) {
        is Vector1D -> when (that) {
            is Vector2D -> this.to2D() wedge that
            is Vector3D -> this.to3D() wedge that
            is Vector4D -> this.to4D() wedge that
            else -> BiVector2D()
        }
        is Vector2D -> when (that) {
            is Vector1D -> this wedge that.to2D()
            is Vector2D -> this wedge that
            is Vector3D -> this.to3D() wedge that
            is Vector4D -> this.to4D() wedge that
            else -> BiVector2D()
        }
        is Vector3D -> when (that) {
            is Vector1D -> this wedge that.to3D()
            is Vector2D -> this wedge that.to3D()
            is Vector3D -> this wedge that
            is Vector4D -> this.to4D() wedge that
            else -> BiVector2D()
        }
        is Vector4D -> when (that) {
            is Vector1D -> this wedge that.to4D()
            is Vector2D -> this wedge that.to4D()
            is Vector3D -> this wedge that.to4D()
            is Vector4D -> this wedge that
            else -> BiVector2D()
        }
        else -> BiVector2D()
    }

    operator fun times(that: Number): Vector = when (this) {
        is Vector1D -> this * that
        is Vector2D -> this * that
        is Vector3D -> this * that
        is Vector4D -> this * that
        else -> Vector1D()
    }
    operator fun times(that: Vector): MultiVector = when (this) {
        is Vector1D -> when (that) {
            is Vector1D -> MultiVector1D(this * that)
            is Vector2D -> this.to2D() * that
            is Vector3D -> this.to3D() * that
            is Vector4D -> this.to4D() * that
            else -> MultiVector1D()
        }
        is Vector2D -> when (that) {
            is Vector1D -> this * that.to2D()
            is Vector2D -> this * that
            is Vector3D -> this.to3D() * that
            is Vector4D -> this.to4D() * that
            else -> MultiVector1D()
        }
        is Vector3D -> when (that) {
            is Vector1D -> this * that.to3D()
            is Vector2D -> this * that.to3D()
            is Vector3D -> this * that
            is Vector4D -> this.to4D() * that
            else -> MultiVector1D()
        }
        is Vector4D -> when (that) {
            is Vector1D -> this * that.to4D()
            is Vector2D -> this * that.to4D()
            is Vector3D -> this * that.to4D()
            is Vector4D -> this * that
            else -> MultiVector1D()
        }
        else -> MultiVector1D()
    }
}
interface BiVector: KVector {

    override val grade: Int get() = 2

    fun to2D(): BiVector2D = when (this) {
            is BiVector2D -> this
            is BiVector3D -> BiVector2D(xy)
            is BiVector4D -> BiVector2D(xy)
            else -> BiVector2D()
        }
    fun to3D(): BiVector3D = when (this) {
            is BiVector2D -> BiVector3D(this)
            is BiVector3D -> this
            is BiVector4D -> BiVector3D(xy, yz, zx)
            else -> BiVector3D()
        }
    fun to4D(): BiVector4D = when (this) {
            is BiVector2D -> BiVector4D(this)
            is BiVector3D -> BiVector4D(this)
            is BiVector4D -> this
            else -> BiVector4D()

        }
}
interface TriVector : KVector {

    override val grade: Int get() = 3

    fun to3D(): TriVector3D = when (this) {
            is TriVector3D -> this
            is TriVector4D -> TriVector3D(xyz)
            else -> TriVector3D()
        }
    fun to4D(): TriVector4D = when (this) {
            is TriVector3D -> TriVector4D(this)
            is TriVector4D -> this
            else -> TriVector4D()
        }
}
interface QuadVector : KVector {

    override val grade: Int get() = 4

    fun to4D(): QuadVector4D = when (this) {
            is QuadVector4D -> this
            else -> QuadVector4D()
        }
}
