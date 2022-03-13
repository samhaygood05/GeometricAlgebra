import kotlin.math.pow

// Addition
operator fun Number.plus(that: Vector1D) = MultiVector1D(this, that)
operator fun Number.plus(that: MultiVector1D) = MultiVector1D(this.toDouble() + that.scalar, that.vec)

operator fun Number.plus(that: Vector2D) = MultiVector2D(this, that)
operator fun Number.plus(that: BiVector2D) = MultiVector2D(this, bivec = that)
operator fun Number.plus(that: MultiVector2D) = MultiVector2D(this) + that

operator fun Number.plus(that: Vector3D) = MultiVector3D(this, that)
operator fun Number.plus(that: BiVector3D) = MultiVector3D(this, bivec = that)
operator fun Number.plus(that: TriVector3D) = MultiVector3D(this, trivec = that)
operator fun Number.plus(that: MultiVector3D) = MultiVector3D(this) + that

operator fun Number.plus(that: Vector4D) = MultiVector4D(this, vec = that)
operator fun Number.plus(that: BiVector4D) = MultiVector4D(this, bivec = that)
operator fun Number.plus(that: TriVector4D) = MultiVector4D(this, trivec = that)
operator fun Number.plus(that: QuadVector4D) = MultiVector4D(this, quadvec = that)
operator fun Number.plus(that: MultiVector4D) = MultiVector4D(this) + that

operator fun Number.plus(that: TypeVector): TypeVector = when (that) {
    is Vector1D -> this + that
    is MultiVector1D -> this + that

    is Vector2D -> this + that
    is BiVector2D -> this + that
    is MultiVector2D -> this + that

    is Vector3D -> this + that
    is BiVector3D -> this + that
    is TriVector3D -> this + that
    is MultiVector3D -> this + that

    is Vector4D -> this + that
    is BiVector4D -> this + that
    is TriVector4D -> this + that
    is QuadVector4D -> this + that
    is MultiVector4D -> this + that

    else -> MultiVector1D()
}

// Subtraction
operator fun Number.minus(that: Vector1D) = this + -that
operator fun Number.minus(that: MultiVector1D) = this + -that

operator fun Number.minus(that: Vector2D) = this + -that
operator fun Number.minus(that: BiVector2D) = this + -that
operator fun Number.minus(that: MultiVector2D) = this + -that

operator fun Number.minus(that: Vector3D) = this + -that
operator fun Number.minus(that: BiVector3D) = this + -that
operator fun Number.minus(that: TriVector3D) = this + -that
operator fun Number.minus(that: MultiVector3D) = this + -that

operator fun Number.minus(that: Vector4D) = this + -that
operator fun Number.minus(that: BiVector4D) = this + -that
operator fun Number.minus(that: TriVector4D) = this + -that
operator fun Number.minus(that: QuadVector4D) = this + -that
operator fun Number.minus(that: MultiVector4D) = this + -that

operator fun Number.minus(that: TypeVector): TypeVector = when (that) {
    is Vector1D -> this - that
    is MultiVector1D -> this - that

    is Vector2D -> this - that
    is BiVector2D -> this - that
    is MultiVector2D -> this - that

    is Vector3D -> this - that
    is BiVector3D -> this - that
    is TriVector3D -> this - that
    is MultiVector3D -> this - that

    is Vector4D -> this - that
    is BiVector4D -> this - that
    is TriVector4D -> this - that
    is QuadVector4D -> this - that
    is MultiVector4D -> this - that

    else -> MultiVector1D()
}

// Products
infix fun Number.dot(that: TypeVector) = 0.0
infix fun Number.wedge(that: TypeVector) = this * that

// Multiplication
operator fun Number.times(that: Vector1D) = Vector1D(this.toDouble() * that.x)
operator fun Number.times(that: MultiVector1D) = MultiVector1D(this.toDouble() * that.scalar, this.toDouble() * that.vec)

operator fun Number.times(that: Vector2D) = Vector2D(this.toDouble() * that.x, this.toDouble() * that.y)
operator fun Number.times(that: BiVector2D) = BiVector2D(this.toDouble() * that.xy)
operator fun Number.times(that: MultiVector2D) = MultiVector2D(this.toDouble() * that.scalar, this * that.vec, this * that.bivec)

operator fun Number.times(that: Vector3D) = Vector3D(this.toDouble() * that.x, this.toDouble() * that.y, this.toDouble() * that.z)
operator fun Number.times(that: BiVector3D) = BiVector3D(this.toDouble() * that.xy, this.toDouble() * that.yz, this.toDouble() * that.zx)
operator fun Number.times(that: TriVector3D) = TriVector3D(this.toDouble() * that.xyz)
operator fun Number.times(that: MultiVector3D) = MultiVector3D(this.toDouble() * that.scalar, this * that.vec, this * that.bivec, this * that.trivec)

operator fun Number.times(that: Vector4D) = Vector4D(this.toDouble() * that.x, this.toDouble() * that.y, this.toDouble() * that.z, this.toDouble() * that.w)
operator fun Number.times(that: BiVector4D) = BiVector4D(this.toDouble() * that.xy, this.toDouble() * that.yz, this.toDouble() * that.zx, this.toDouble() * that.wx, this.toDouble() * that.wy, this.toDouble() * that.wz)
operator fun Number.times(that: TriVector4D) = TriVector4D(this.toDouble() * that.xyz, this.toDouble() * that.xyw, this.toDouble() * that.yzw, this.toDouble() * that.zxw)
operator fun Number.times(that: QuadVector4D) = QuadVector4D(this.toDouble() * that.xyzw)
operator fun Number.times(that: MultiVector4D) = MultiVector4D(this.toDouble() * that.scalar, this * that.vec, this * that.bivec, this * that.trivec, this * that.quadvec)

operator fun Number.times(that: TypeVector): TypeVector = when (that) {
    is Vector1D -> this * that
    is MultiVector1D -> this * that

    is Vector2D -> this * that
    is BiVector2D -> this * that
    is MultiVector2D -> this * that

    is Vector3D -> this * that
    is BiVector3D -> this * that
    is TriVector3D -> this * that
    is MultiVector3D -> this * that

    is Vector4D -> this * that
    is BiVector4D -> this * that
    is TriVector4D -> this * that
    is QuadVector4D -> this * that
    is MultiVector4D -> this * that

    else -> MultiVector1D()
}

// Division
operator fun Number.div(that: Vector1D) = this * (that / that.sqrMag)
operator fun Number.div(that: MultiVector1D) = this * (that.conj / (that.scalar.pow(2) - that.vec.sqrMag))

operator fun Number.div(that: Vector2D) = this * (that / that.sqrMag)
operator fun Number.div(that: BiVector2D) = this * (that / that.sqrMag)
operator fun Number.div(that: MultiVector2D) = this * (that.conj / (that.scalar.pow(2) - that.vec.sqrMag + that.bivec.sqrMag))

operator fun Number.div(that: Vector3D) = this * (that / that.sqrMag)
operator fun Number.div(that: BiVector3D) = this * (that / that.sqrMag)
operator fun Number.div(that: TriVector3D) = this * (that / that.sqrMag)
operator fun Number.div(that: MultiVector3D) = this * (that.conj / ((that.scalar.pow(2) - that.vec.sqrMag + that.bivec.sqrMag - that.trivec.sqrMag).pow(2) + that.trivec.sqrMag))

operator fun Number.div(that: Vector4D) = this * (that / that.sqrMag)
operator fun Number.div(that: BiVector4D): BiVector4D {
    return if (that.simple) this * (that / that.sqrMag)
    else this * ((that * (that dot that) + (that wedge that)) / (that dot that).pow(2) - 4 * (that wedge that).sqrMag).bivec
}
operator fun Number.div(that: TriVector4D) = this * (that / that.sqrMag)
operator fun Number.div(that: QuadVector4D) = this * (that / that.sqrMag)

operator fun Number.div(that: TypeVector): TypeVector = when (that) {
    is Vector1D -> this / that
    is MultiVector1D -> this / that

    is Vector2D -> this / that
    is BiVector2D -> this / that
    is MultiVector2D -> this / that

    is Vector3D -> this / that
    is BiVector3D -> this / that
    is TriVector3D -> this / that
    is MultiVector3D -> this / that

    is Vector4D -> this / that
    is BiVector4D -> this / that
    is TriVector4D -> this / that
    is QuadVector4D -> this / that

    else -> MultiVector1D()
}

val Number.grade: Int get() = 0