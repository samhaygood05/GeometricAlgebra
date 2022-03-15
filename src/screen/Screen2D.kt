package screen

import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics

class Screen2D(val camera: Camera2D): Canvas() {

    init {
        background = Color.BLACK
    }

    override fun paint(g: Graphics?) {
        Draw2D.draw(g!!)
    }
}