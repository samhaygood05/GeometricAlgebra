import kotlin.math.sqrt
import Dimension.*

enum class Dimension(val dim: Int) {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4)
}

interface TypeVector {

    val dimension: Dimension
    val norm: TypeVector

    operator fun unaryPlus() = this

    fun isZero(): Boolean

    infix fun dot(that: Number) = 0.0

    fun toMultiVec(): MultiVector = when (this) {
        is MultiVector -> this

        is Vector1D -> MultiVector1D(vec = this)
        is Vector2D -> MultiVector2D(vec = this)
        is Vector3D -> MultiVector3D(vec = this)
        is Vector4D -> MultiVector4D(vec = this)

        is BiVector2D -> MultiVector2D(bivec = this)
        is BiVector3D -> MultiVector3D(bivec = this)
        is BiVector4D -> MultiVector4D(bivec = this)

        is TriVector3D -> MultiVector3D(trivec = this)
        is TriVector4D -> MultiVector4D(trivec = this)

        is QuadVector4D -> MultiVector4D(quadvec = this)

        else -> MultiVector4D()
    }
    fun toVec(): Vector = when (this) {
        is Vector -> this

        is MultiVector1D -> vec
        is MultiVector2D -> vec
        is MultiVector3D -> vec
        is MultiVector4D -> vec
        else -> when (dimension) {
            ONE -> Vector1D()
            TWO -> Vector2D()
            THREE -> Vector3D()
            FOUR -> Vector4D()
        }
    }
    fun toBiVec(): BiVector = when (this) {
        is BiVector -> this

        is MultiVector2D -> bivec
        is MultiVector3D -> bivec
        is MultiVector4D -> bivec
        else -> when (dimension) {
            ONE, TWO -> BiVector2D()
            THREE -> BiVector3D()
            FOUR -> BiVector4D()
        }
    }
    fun toTriVec(): TriVector = when (this) {
        is TriVector -> this

        is MultiVector3D -> trivec
        is MultiVector4D -> trivec
        else -> when (dimension) {
            ONE, TWO, THREE -> TriVector3D()
            FOUR -> TriVector4D()
        }
    }
    fun toQuadVec(): QuadVector = when (this) {
        is QuadVector -> this

        is MultiVector4D -> quadvec
        else -> QuadVector4D()
    }

}

interface KVector : TypeVector {
    val sqrMag: Double
    val mag: Double get() = sqrt(sqrMag)

    val grade: Dimension
}

interface Vector : KVector {

    override val mag: Double get() = sqrt(sqrMag)
    override val grade get() = ONE

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
            is Vector2D -> Vector3D(x, y, 0)
            is Vector3D -> this
            is Vector4D -> Vector3D(x, y, z)
            else -> Vector3D()
        }
    fun to4D(): Vector4D = when (this) {
            is Vector1D -> Vector4D(x, 0 ,0, 0)
            is Vector2D -> Vector4D(x, y, 0, 0)
            is Vector3D -> Vector4D(x, y, z, 0)
            is Vector4D -> this
            else -> Vector4D()
        }

    operator fun plus(that: Number): MultiVector = when (this) {
        is Vector1D -> this + that
        is Vector2D -> this + that
        is Vector3D -> this + that
        is Vector4D -> this + that
        else -> MultiVector1D()
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
    operator fun plus(that: BiVector): MultiVector = when (this) {
        is Vector1D, is Vector2D -> when (that) {
            is BiVector2D -> this.to2D() + that
            is BiVector3D -> this.to3D() + that
            is BiVector4D -> this.to4D() + that
            else -> MultiVector2D.NaN
        }
        is Vector3D -> when (that) {
            is BiVector2D, is BiVector3D -> this + that.to3D()
            is BiVector4D -> this.to4D() + that
            else -> MultiVector3D.NaN
        }
        is Vector4D -> this + that.to4D()
        else -> MultiVector4D.NaN
    }
    operator fun plus(that: TriVector): MultiVector = when (this) {
        is Vector1D, is Vector2D, is Vector3D -> when (that) {
            is TriVector3D -> this.to3D() + that
            is TriVector4D -> this.to4D() + that
            else -> MultiVector3D.NaN
        }
        is Vector4D -> this + that.to4D()
        else -> MultiVector4D.NaN
    }
    operator fun plus(that: QuadVector): MultiVector4D = this.to4D() + that.to4D()

    operator fun minus(that: Number): MultiVector = when (this) {
        is Vector1D -> this - that
        is Vector2D -> this - that
        is Vector3D -> this - that
        is Vector4D -> this - that
        else -> MultiVector1D()
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
    operator fun minus(that: BiVector): MultiVector = when (this) {
        is Vector1D, is Vector2D -> when (that) {
            is BiVector2D -> this.to2D() - that
            is BiVector3D -> this.to3D() - that
            is BiVector4D -> this.to4D() - that
            else -> MultiVector2D.NaN
        }
        is Vector3D -> when (that) {
            is BiVector2D, is BiVector3D -> this - that.to3D()
            is BiVector4D -> this.to4D() - that
            else -> MultiVector3D.NaN
        }
        is Vector4D -> this - that.to4D()
        else -> MultiVector4D.NaN
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
    infix fun dot(that: BiVector): Vector = when (this) {
        is Vector1D -> when (that) {
            is BiVector2D -> this.to2D() dot that
            is BiVector3D -> this.to3D() dot that
            is BiVector4D -> this.to4D() dot that
            else -> Vector4D()
        }
        is Vector2D -> when (that) {
            is BiVector2D -> this dot that
            is BiVector3D -> this.to3D() dot that
            is BiVector4D -> this.to4D() dot that
            else -> Vector4D()
        }
        is Vector3D -> when (that) {
            is BiVector2D -> this dot that.to3D()
            is BiVector3D -> this dot that
            is BiVector4D -> this.to4D() dot that
            else -> Vector4D()
        }
        is Vector4D -> when (that) {
            is BiVector2D -> this dot that.to4D()
            is BiVector3D -> this dot that.to4D()
            is BiVector4D -> this dot that
            else -> Vector4D()
        }
        else -> Vector4D()
    }
    infix fun dot(that: TriVector): BiVector = when (this) {
        is Vector1D -> when (that) {
            is TriVector3D -> this.to3D() dot that
            is TriVector4D -> this.to4D() dot that
            else -> BiVector4D()
        }
        is Vector2D -> when (that) {
            is TriVector3D -> this.to3D() dot that
            is TriVector4D -> this.to4D() dot that
            else -> BiVector4D()
        }
        is Vector3D -> when (that) {
            is TriVector3D -> this dot that
            is TriVector4D -> this.to4D() dot that
            else -> BiVector4D()
        }
        is Vector4D -> when (that) {
            is TriVector3D -> this dot that.to4D()
            is TriVector4D -> this dot that
            else -> BiVector4D()
        }
        else -> BiVector4D()
    }
    infix fun dot(that: QuadVector): TriVector4D = this.to4D() dot that.to4D()

    infix fun wedge(that: Number): Vector = this * that
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
    infix fun wedge(that: BiVector): TriVector = when (this) {
        is Vector1D -> when (that) {
            is BiVector2D -> TriVector3D()
            is BiVector3D -> this.to3D() wedge that
            is BiVector4D -> this.to4D() wedge that
            else -> TriVector4D()
        }
        is Vector2D -> when (that) {
            is BiVector2D -> TriVector3D()
            is BiVector3D -> this.to3D() wedge that
            is BiVector4D -> this.to4D() wedge that
            else -> TriVector4D()
        }
        is Vector3D -> when (that) {
            is BiVector2D -> this wedge that.to3D()
            is BiVector3D -> this wedge that
            is BiVector4D -> this.to4D() wedge that
            else -> TriVector4D()
        }
        is Vector4D -> when (that) {
            is BiVector2D -> this wedge that.to4D()
            is BiVector3D -> this wedge that.to4D()
            is BiVector4D -> this wedge that
            else -> TriVector4D()
        }
        else -> TriVector4D()
    }
    infix fun wedge(that: TriVector): QuadVector4D = when (this) {
        is Vector1D -> when (that) {
            is TriVector3D -> QuadVector4D()
            is TriVector4D -> this.to4D() wedge that
            else -> QuadVector4D()
        }
        is Vector2D -> when (that) {
            is TriVector3D -> QuadVector4D()
            is TriVector4D -> this.to4D() wedge that
            else -> QuadVector4D()
        }
        is Vector3D -> when (that) {
            is TriVector3D -> QuadVector4D()
            is TriVector4D -> this.to4D() wedge that
            else -> QuadVector4D()
        }
        is Vector4D -> when (that) {
            is TriVector3D -> this wedge that.to4D()
            is TriVector4D -> this wedge that
            else -> QuadVector4D()
        }
        else -> QuadVector4D()
    }
    infix fun wedge(that: QuadVector) = 0.0

    operator fun times(that: Number): Vector = when (this) {
        is Vector1D -> this * that
        is Vector2D -> this * that
        is Vector3D -> this * that
        is Vector4D -> this * that
        else -> Vector1D()
    }
    operator fun times(that: Vector): TypeVector = when (this) {
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
    operator fun times(that: BiVector): TypeVector = when (this) {
        is Vector1D -> when (that) {
            is BiVector2D -> this.to2D() * that
            is BiVector3D -> this.to3D() * that
            is BiVector4D -> this.to4D() * that
            else -> MultiVector1D()
        }
        is Vector2D -> when (that) {
            is BiVector2D -> this * that
            is BiVector3D -> this.to3D() * that
            is BiVector4D -> this.to4D() * that
            else -> MultiVector1D()
        }
        is Vector3D -> when (that) {
            is BiVector2D -> this * that.to3D()
            is BiVector3D -> this * that
            is BiVector4D -> this.to4D() * that
            else -> MultiVector1D()
        }
        is Vector4D -> when (that) {
            is BiVector2D -> this * that.to4D()
            is BiVector3D -> this * that.to4D()
            is BiVector4D -> this * that
            else -> MultiVector1D()
        }
        else -> MultiVector1D()
    }
    operator fun times(that: TriVector): TypeVector = when (this) {
        is Vector1D -> when (that) {
            is TriVector3D -> this.to3D() * that
            is TriVector4D -> this.to4D() * that
            else -> MultiVector1D()
        }
        is Vector2D -> when (that) {
            is TriVector3D -> this.to3D() * that
            is TriVector4D -> this.to4D() * that
            else -> MultiVector1D()
        }
        is Vector3D -> when (that) {
            is TriVector3D -> this * that
            is TriVector4D -> this.to4D() * that
            else -> MultiVector1D()
        }
        is Vector4D -> when (that) {
            is TriVector3D -> this * that.to4D()
            is TriVector4D -> this * that
            else -> MultiVector1D()
        }
        else -> MultiVector1D()
    }
    operator fun times(that: QuadVector): TriVector4D = this dot that
}
interface BiVector: KVector {

    override val grade get() = TWO

    fun to2D(): BiVector2D = when (this) {
            is BiVector2D -> this
            is BiVector3D -> BiVector2D(xy)
            is BiVector4D -> BiVector2D(xy)
            else -> BiVector2D()
        }
    fun to3D(): BiVector3D = when (this) {
            is BiVector2D -> BiVector3D(xy, 0,0)
            is BiVector3D -> this
            is BiVector4D -> BiVector3D(xy, yz, zx)
            else -> BiVector3D()
        }
    fun to4D(): BiVector4D = when (this) {
            is BiVector2D -> BiVector4D(xy, 0, 0, 0, 0, 0)
            is BiVector3D -> BiVector4D(xy, yz, zx, 0, 0, 0)
            is BiVector4D -> this
            else -> BiVector4D()

        }

    infix fun dot(that: Vector): Vector = when (this) {
        is BiVector2D -> when (that) {
            is Vector1D, is Vector2D -> this dot that.to2D()
            is Vector3D -> this.to3D() dot that
            is Vector4D -> this.to4D() dot that
            else -> Vector4D()
        }
        is BiVector3D -> when (that) {
            is Vector1D, is Vector2D, is Vector3D -> this dot that.to3D()
            is Vector4D -> this.to4D() dot that
            else -> Vector4D()
        }
        is BiVector4D -> this dot that.to4D()
        else -> Vector4D()
    }
    infix fun dot(that: BiVector): Double = when (this) {
        is BiVector2D -> when (that) {
            is BiVector2D -> this dot that
            is BiVector3D -> this.to3D() dot that
            is BiVector4D -> this.to4D() dot that
            else -> Double.NaN
        }
        is BiVector3D -> when (that) {
            is BiVector2D, is BiVector3D -> this dot that.to3D()
            is BiVector4D -> this.to4D() dot that
            else -> Double.NaN
        }
        is BiVector4D -> this dot that.to4D()
        else -> Double.NaN
    }
    infix fun dot(that: TriVector): Vector = when (this) {
        is BiVector2D -> when (that) {
            is TriVector3D -> this.to3D() dot that
            is TriVector4D -> this.to4D() dot that
            else -> Vector2D.NaN
        }
        is BiVector3D -> when (that) {
            is TriVector3D -> this dot that
            is TriVector4D -> this.to4D() dot that
            else -> Vector3D.NaN
        }
        is BiVector4D -> this dot that.to4D()
        else -> Vector4D.NaN
    }
    infix fun dot(that: QuadVector): BiVector4D = this.to4D() dot that.to4D()

    infix fun cross(that: BiVector): BiVector = when (this) {
        is BiVector2D -> when (that) {
            is BiVector2D -> BiVector2D()
            is BiVector3D -> this.to3D() cross that
            else -> BiVector2D.NaN
        }
        is BiVector3D -> when (that) {
            is BiVector2D, is BiVector3D -> this cross that.to3D()
            is BiVector4D -> this.to4D() cross that
            else -> BiVector3D.NaN
        }
        is BiVector4D -> this cross that.to4D()
        else -> BiVector4D.NaN
    }
    infix fun cross(that: TriVector): TriVector = when (this) {
        is BiVector2D -> when (that) {
            is TriVector3D -> TriVector3D.NaN
            is TriVector4D -> this.to4D() cross that
            else -> TriVector3D.NaN
        }
        is BiVector3D -> when (that) {
            is TriVector3D -> TriVector3D.NaN
            is TriVector4D -> this.to4D() cross that
            else -> TriVector3D.NaN
        }
        is BiVector4D -> this cross that.to4D()
        else -> TriVector4D.NaN
    }

    infix fun wedge(that: Vector): TriVector = when (this) {
        is BiVector2D -> when (that) {
            is Vector1D -> TriVector3D()
            is Vector2D -> TriVector3D()
            is Vector3D -> this.to3D() wedge that
            is Vector4D -> this.to4D() wedge that
            else -> TriVector4D()
        }
        is BiVector3D -> when (that) {
            is Vector1D -> this wedge that.to3D()
            is Vector2D -> this wedge that.to3D()
            is Vector3D -> this wedge that
            is Vector4D -> this.to4D() wedge that
            else -> TriVector4D()
        }
        is BiVector4D -> when (that) {
            is Vector1D -> this wedge that.to4D()
            is Vector2D -> this wedge that.to4D()
            is Vector3D -> this wedge that.to4D()
            is Vector4D -> this wedge that
            else -> TriVector4D()
        }
        else -> TriVector4D()
    }
    infix fun wedge(that: BiVector): QuadVector4D = when (this) {
        is BiVector2D -> when (that) {
            is BiVector2D, is BiVector3D -> QuadVector4D()
            is BiVector4D -> this.to4D() wedge that
            else -> QuadVector4D.NaN
        }
        is BiVector3D -> when (that) {
            is BiVector2D, is BiVector3D -> QuadVector4D()
            is BiVector4D -> this.to4D() wedge that
            else -> QuadVector4D.NaN
        }
        is BiVector4D -> this wedge that.to4D()
        else -> QuadVector4D.NaN
    }
    infix fun wedge(that: TriVector) = 0.0
    infix fun wedge(that: QuadVector) = 0.0
}
interface TriVector : KVector {

    override val grade get() = THREE

    fun to3D(): TriVector3D = when (this) {
            is TriVector3D -> this
            is TriVector4D -> TriVector3D(xyz)
            else -> TriVector3D()
        }
    fun to4D(): TriVector4D = when (this) {
            is TriVector3D -> TriVector4D(xyz, 0, 0, 0)
            is TriVector4D -> this
            else -> TriVector4D()
        }

    infix fun dot(that: Vector): BiVector = when (this) {
        is TriVector3D -> when (that) {
            is Vector1D, is Vector2D, is Vector3D -> this dot that.to3D()
            is Vector4D -> this.to4D() dot that
            else -> BiVector4D()
        }
        is TriVector4D -> this dot that.to4D()
        else -> BiVector4D()
    }
    infix fun dot(that: BiVector): Vector = when (this) {
        is TriVector3D -> when (that) {
            is BiVector2D, is BiVector3D -> this dot that.to3D()
            is BiVector4D -> this.to4D() dot that
            else -> Vector4D()
        }
        is TriVector4D -> this dot that.to4D()
        else -> Vector4D()
    }
    infix fun dot(that: TriVector): Double = when (this) {
        is TriVector3D -> when (that) {
            is TriVector3D -> this dot that
            is TriVector4D -> this.to4D() dot that
            else -> 0.0
        }
        is TriVector4D -> this dot that.to4D()
        else -> 0.0
    }
    infix fun dot(that: QuadVector): Vector4D = this.to4D() dot that.to4D()

    infix fun cross(that: BiVector): TriVector = when (this) {
        is TriVector3D -> when (that) {
            is BiVector2D, is BiVector3D -> TriVector3D()
            is BiVector4D -> this.to4D() cross that
            else -> TriVector4D()
        }
        is TriVector4D -> this cross that.to4D()
        else -> TriVector4D()
    }
    infix fun cross(that: TriVector): BiVector = when (this) {
        is TriVector3D -> when (that) {
            is TriVector3D -> BiVector4D()
            is TriVector4D -> this.to4D() cross that
            else -> BiVector4D()
        }
        is TriVector4D -> this cross that.to4D()
        else -> BiVector4D()
    }

    infix fun wedge(that: Number): TriVector = when (this) {
        is TriVector3D -> this * that
        is TriVector4D -> this * that
        else -> TriVector4D()
    }
    infix fun wedge(that: Vector): QuadVector = when (this) {
        is TriVector3D -> when (that) {
            is Vector1D, is Vector2D, is Vector3D -> QuadVector4D()
            is Vector4D -> this.to4D() wedge that
            else -> QuadVector4D()
        }
        is TriVector4D -> this wedge that.to4D()
        else -> QuadVector4D()
    }
    infix fun wedge(that: BiVector) = 0.0
    infix fun wedge(that: TriVector) = 0.0
    infix fun wedge(that: QuadVector) = 0.0
}
interface QuadVector : KVector {

    override val grade get() = FOUR

    fun to4D(): QuadVector4D = when (this) {
            is QuadVector4D -> this
            else -> QuadVector4D()
        }
}
interface MultiVector : TypeVector {

    fun to1D(): MultiVector1D = when (this) {
        is MultiVector1D -> this
        is MultiVector2D -> MultiVector1D(scalar, vec.to1D())
        is MultiVector3D -> MultiVector1D(scalar, vec.to1D())
        is MultiVector4D -> MultiVector1D(scalar, vec.to1D())
        else -> MultiVector1D()
    }
    fun to2D(): MultiVector2D = when (this) {
        is MultiVector1D -> MultiVector2D(scalar, vec.to2D())
        is MultiVector2D -> this
        is MultiVector3D -> MultiVector2D(scalar, vec.to2D(), bivec.to2D())
        is MultiVector4D -> MultiVector2D(scalar, vec.to2D(), bivec.to2D())
        else -> MultiVector2D()
    }
    fun to3D(): MultiVector3D = when (this) {
        is MultiVector1D -> MultiVector3D(scalar, vec.to3D())
        is MultiVector2D -> MultiVector3D(scalar, vec.to3D(), bivec.to3D())
        is MultiVector3D -> this
        is MultiVector4D -> MultiVector3D(scalar, vec.to3D(), bivec.to3D(), trivec.to3D())
        else -> MultiVector3D()
    }
    fun to4D(): MultiVector4D = when (this) {
        is MultiVector1D -> MultiVector4D(scalar, vec.to4D())
        is MultiVector2D -> MultiVector4D(scalar, vec.to4D(), bivec.to4D())
        is MultiVector3D -> MultiVector4D(scalar, vec.to4D(), bivec.to4D(), trivec.to4D())
        is MultiVector4D -> this
        else -> MultiVector4D()
    }
}

fun determinate(vararg trans: Vector): Double = when (trans.size) {
    1 -> trans[0].mag
    2 -> (trans[0] wedge trans[1]).mag
    3 -> (trans[0] wedge trans[1] wedge trans[2]).mag
    4 -> (trans[0] wedge trans[1] wedge trans[2] wedge trans[3]).mag
    else -> 0.0
}
