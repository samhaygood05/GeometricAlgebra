package screen

import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics

class Screen: Canvas() {

    init {
        background = Color.BLACK
    }

    override fun paint(g: Graphics?) {
        Draw3D.draw(g!!)
    }
}