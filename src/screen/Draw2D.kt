package screen

import geoAlg.GeoAlg2D
import geoAlg.GeoAlg2D.X
import geoAlg.GeoAlg2D.Y
import geoAlg.Vector2D
import screen.Draw2D.Companion.camera
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import javax.swing.JFrame
import kotlin.math.roundToInt

class Draw2D(val camera: Camera2D) :JFrame() {
    private val screen = Screen2D(camera)
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
        val camera = Camera2D(Vector2D(), 1200, 1200 / 16 * 9, 50.0)

        fun drawPoint(g: Graphics, p: Vector2D, weight: Int, color: Color) {
            if (color.alpha != 0) {
                val pNew = camera.transform(p)
                g.color = color
                g.fillOval(pNew.x.roundToInt() - weight, pNew.y.roundToInt() - weight, 2*weight, 2*weight)
            }
        }
        fun drawLine(g: Graphics, p1: Vector2D, p2: Vector2D, color: Color) {
            if (color.alpha != 0) {
                g.color = color
                val p1New = camera.transform(p1)
                val p2New = camera.transform(p2)
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

fun main() {
    val drawScreen = Draw2D(camera)
}