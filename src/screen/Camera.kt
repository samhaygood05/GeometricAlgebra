package screen

import geoAlg.GeoAlg2D.X
import geoAlg.GeoAlg2D.Y
import geoAlg.GeoAlg3D.I
import geoAlg.GeoAlg4D.J
import geoAlg.Vector
import geoAlg.Vector2D
import geoAlg.Vector3D
import geoAlg.Vector4D
import screen.Projection.*

interface Camera {
    val pos: Vector
    val width: Int
    val height: Int
}

class Camera2D(override val pos: Vector2D = Vector2D(),
               override val width: Int, override val height: Int,
               val zoom: Double) : Camera {

    fun transform(vector: Vector2D) = ((vector - pos) * zoom).linearTrans(X, -Y).to2D() + Vector2D(width/2, height/2)
}
class Camera3D(val projection: Projection = PERSPECTIVE,
               override val pos: Vector3D = Vector3D(), val facing: Vector3D, val roll: Double = 0.0,
               override val width: Int, override val height: Int,
               val dist: Double = 1.0, val zoom: Double, val depthScale: Double = 1.0) : Camera {

    fun transform(vector: Vector3D): Vector2D = ((vector - pos).perspecProj((facing*I).norm, dist, when (projection) {
            ORTHOGRAPHIC -> 0.0
            PERSPECTIVE -> 1.0
            OTHER -> depthScale
        }) * zoom).rotate(roll).linearTrans(X, -Y).to2D() + Vector2D(width/2, height/2)
}
class Camera4D(val projection: Projection = PERSPECTIVE,
               override val pos: Vector4D = Vector4D(), val facing: Vector4D, val camera: Camera3D,
               override val width: Int, override val height: Int,
               val dist: Double = 1.0, val depthScale: Double = 1.0) : Camera {
                   fun transform(vector: Vector4D): Vector2D {
                       return camera.transform(
                           (vector - pos).perspecProj(
                               (facing * J).norm, dist, when (projection) {
                                   ORTHOGRAPHIC -> 0.0
                                   PERSPECTIVE -> 1.0
                                   OTHER -> depthScale
                               }
                           )
                       )
                   }
}

enum class Projection {
    PERSPECTIVE, ORTHOGRAPHIC, OTHER
}