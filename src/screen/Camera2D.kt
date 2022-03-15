package screen

import geoAlg.GeoAlg2D.X
import geoAlg.GeoAlg2D.Y
import geoAlg.Vector2D

class Camera2D(val pos: Vector2D, val width: Int, val height: Int, val zoom: Double) {

    fun transform(vector: Vector2D) = ((vector - pos) * zoom).linearTrans(X, -Y).to2D() + Vector2D(width/2, height/2)
}