package screen

import geoAlg.*
import geoAlg.GeoAlg2D.X
import geoAlg.GeoAlg2D.Y
import screen.Draw2D.Companion.camera2D
import screen.Draw3D.Companion.camera3D
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import javax.swing.JFrame
import kotlin.math.PI
import kotlin.math.roundToInt
class Draw2D(camera: Camera2D) : JFrame() {
    private val screen = Screen()
    init {
        layout = BorderLayout()
        setSize(camera.width, camera.height)
        title = "Screen Space"
        add("Center", screen)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isVisible = true
    }

    companion object  {
        val camera2D = Camera2D(Vector2D(), 1200, 1200 / 16 * 9, 50.0)

        fun drawPoint(g: Graphics, p: Vector2D, weight: Int, color: Color) {
            if (color.alpha != 0) {
                val pNew = camera2D.transform(p)
                g.color = color
                g.fillOval(pNew.x.roundToInt() - weight, pNew.y.roundToInt() - weight, 2*weight, 2*weight)
            }
        }
        fun drawLine(g: Graphics, p1: Vector2D, p2: Vector2D, color: Color) {
            if (color.alpha != 0) {
                g.color = color
                val p1New = camera2D.transform(p1)
                val p2New = camera2D.transform(p2)
                g.drawLine(p1New.x.roundToInt(), p1New.y.roundToInt(), p2New.x.roundToInt(),p2New.y.roundToInt())
            }
        }

        @JvmStatic fun draw(g: Graphics) {
            drawLine(g, Vector2D(), X, Color.RED)
            drawLine(g, Vector2D(), Y, Color.GREEN)
            drawLine(g, X, Y, Color.WHITE)
        }
    }
}
class Draw3D(camera: Camera3D) : JFrame() {
    private val screen = Screen()
    init {
        layout = BorderLayout()
        setSize(camera.width, camera.height)
        title = "Screen Space 3D"
        add("Center", screen)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isVisible = true
    }

    companion object  {
        val camera3D = Camera3D(pos = Vector3D(4, 0, 2), facing = Vector3D(-1,0,-0.5), roll = PI,
            width = 1200, height = 1200 / 16 * 9, zoom = 200.0, projection = Projection.OTHER, depthScale = 1.0
        )

        fun drawPoint(g: Graphics, p: Vector3D, weight: Int, color: Color) {
            if (color.alpha != 0) {
                val pNew = camera3D.transform(p)
                g.color = color
                g.fillOval(pNew.x.roundToInt() - weight, pNew.y.roundToInt() - weight, 2*weight, 2*weight)
            }
        }
        fun drawPoint(g: Graphics, p: Vector3D, weight: Int) {
            val pNew = camera3D.transform(p)
            g.color = p.color
            g.fillOval(pNew.x.roundToInt() - weight, pNew.y.roundToInt() - weight, 2*weight, 2*weight)
        }
        fun drawLine(g: Graphics, p1: Vector3D, p2: Vector3D, color: Color) {
            if (color.alpha != 0) {
                g.color = color
                val p1New = camera3D.transform(p1)
                val p2New = camera3D.transform(p2)
                g.drawLine(p1New.x.roundToInt(), p1New.y.roundToInt(), p2New.x.roundToInt(),p2New.y.roundToInt())
            }
        }
        fun drawLine(g: Graphics, p1: Vector3D, p2: Vector3D) {
            g.color = ((p1+p2)/2).color
            val p1New = camera3D.transform(p1)
            val p2New = camera3D.transform(p2)
            g.drawLine(p1New.x.roundToInt(), p1New.y.roundToInt(), p2New.x.roundToInt(),p2New.y.roundToInt())
        }
        fun drawLines(g: Graphics, p1: Array<Vector3D>, p2: Array<Vector3D>, color: Color) {
            for (p in p1.indices) drawLine(g, p1[p], p2[p], color)
        }
        fun drawLines(g: Graphics, p1: Array<Vector3D>, p2: Array<Vector3D>) {
            for (p in p1.indices) drawLine(g, p1[p], p2[p])
        }
        fun drawPolygon(g: Graphics, color: Color, vararg points: Vector3D) {
            for (p in points.indices) {
                drawLine(g, points[p], points[(p+1).mod(points.size)], color)
            }
        }
        fun drawPolygon(g: Graphics, vararg points: Vector3D) {
            for (p in points.indices) {
                drawLine(g, points[p], points[(p+1).mod(points.size)])
            }
        }

        @JvmStatic fun draw(g: Graphics) {
            drawPolygon(g, Vector3D(0,0,0), Vector3D(1,0,0), Vector3D(1,1,0), Vector3D(0,1,0))
            drawPolygon(g, Vector3D(0,0,1), Vector3D(1,0,1), Vector3D(1,1,1), Vector3D(0,1,1))
            drawLines(g, arrayOf(Vector3D(0,0,0), Vector3D(1,0,0), Vector3D(1,1,0), Vector3D(0,1,0)),
                arrayOf(Vector3D(0,0,1), Vector3D(1,0,1), Vector3D(1,1,1), Vector3D(0,1,1)))
        }
    }
}
class Draw4D(camera: Camera4D) : JFrame() {
    private val screen = Screen()
    init {
        layout = BorderLayout()
        setSize(camera.width, camera.height)
        title = "Screen Space 4D"
        add("Center", screen)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isVisible = true
    }
    companion object  {
        val camera4D = Camera4D(pos = Vector4D(0, 0, 1, 2), facing = Vector4D(0,0,-0.5, -1), camera = camera3D,
            width = 1200, height = 1200 / 16 * 9, projection = Projection.OTHER, depthScale = 1.0
        )

        fun drawPoint(g: Graphics, p: Vector4D, weight: Int, color: Color) {
            if (color.alpha != 0) {
                val pNew = camera4D.transform(p)
                g.color = color
                g.fillOval(pNew.x.roundToInt() - weight, pNew.y.roundToInt() - weight, 2*weight, 2*weight)
            }
        }
        fun drawPoint(g: Graphics, p: Vector4D, weight: Int) {
            val pNew = camera4D.transform(p)
            g.color = p.color
            g.fillOval(pNew.x.roundToInt() - weight, pNew.y.roundToInt() - weight, 2*weight, 2*weight)
        }
        fun drawLine(g: Graphics, p1: Vector4D, p2: Vector4D, color: Color) {
            if (color.alpha != 0) {
                g.color = color
                val p1New = camera4D.transform(p1)
                val p2New = camera4D.transform(p2)
                g.drawLine(p1New.x.roundToInt(), p1New.y.roundToInt(), p2New.x.roundToInt(),p2New.y.roundToInt())
            }
        }
        fun drawLine(g: Graphics, p1: Vector4D, p2: Vector4D) {
            g.color = ((p1+p2)/2).color
            val p1New = camera4D.transform(p1)
            val p2New = camera4D.transform(p2)
            g.drawLine(p1New.x.roundToInt(), p1New.y.roundToInt(), p2New.x.roundToInt(),p2New.y.roundToInt())
        }
        fun drawLines(g: Graphics, p1: Array<Vector4D>, p2: Array<Vector4D>, color: Color) {
            for (p in p1.indices) drawLine(g, p1[p], p2[p], color)
        }
        fun drawLines(g: Graphics, p1: Array<Vector4D>, p2: Array<Vector4D>) {
            for (p in p1.indices) drawLine(g, p1[p], p2[p])
        }
        fun drawPolygon(g: Graphics, color: Color, vararg points: Vector4D) {
            for (p in points.indices) {
                drawLine(g, points[p], points[(p+1).mod(points.size)], color)
            }
        }
        fun drawPolygon(g: Graphics, vararg points: Vector4D) {
            for (p in points.indices) {
                drawLine(g, points[p], points[(p+1).mod(points.size)])
            }
        }

        @JvmStatic fun draw(g: Graphics) {
            drawPolygon(g, Vector4D(0,0,0), Vector4D(1,0,0), Vector4D(1,1,0), Vector4D(0,1,0))
            drawPolygon(g, Vector4D(0,0,1), Vector4D(1,0,1), Vector4D(1,1,1), Vector4D(0,1,1))
            drawLines(g, arrayOf(Vector4D(0,0,0), Vector4D(1,0,0), Vector4D(1,1,0), Vector4D(0,1,0)),
                arrayOf(Vector4D(0,0,1), Vector4D(1,0,1), Vector4D(1,1,1), Vector4D(0,1,1)))

            drawPolygon(g, Vector4D(0,0,0, 1), Vector4D(1,0,0, 1), Vector4D(1,1,0, 1), Vector4D(0,1,0, 1))
            drawPolygon(g, Vector4D(0,0,1, 1), Vector4D(1,0,1, 1), Vector4D(1,1,1, 1), Vector4D(0,1,1, 1))
            drawLines(g, arrayOf(Vector4D(0,0,0, 1), Vector4D(1,0,0, 1), Vector4D(1,1,0, 1), Vector4D(0,1,0, 1)),
                arrayOf(Vector4D(0,0,1, 1), Vector4D(1,0,1, 1), Vector4D(1,1,1, 1), Vector4D(0,1,1, 1)))
        }
    }
}

fun main() {
    val drawScreen = Draw3D(camera3D)
}