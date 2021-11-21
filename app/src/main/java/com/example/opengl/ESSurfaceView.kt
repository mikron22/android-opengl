package com.example.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

/**
 * @author Marek Kulawiak
 */
internal class ESSurfaceView(context: Context) : GLSurfaceView(context) {

  var renderer: GLRenderer

  private val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f
  private var previousX: Float = 0f
  private var previousY: Float = 0f

  init {
    // Stworzenie kontekstu OpenGL ES 2.0.
    setEGLContextClientVersion(2)
    // Przypisanie renderera do widoku.
    renderer = GLRenderer(context)
    setRenderer(renderer)
  }

  override fun performClick(): Boolean {
    return super.performClick()
    // No op
  }

  override fun onTouchEvent(e: MotionEvent): Boolean {
    val x: Float = e.x
    val y: Float = e.y
    when (e.action) {
      MotionEvent.ACTION_MOVE -> {

        renderer.angleX += (x - previousX) * TOUCH_SCALE_FACTOR
        renderer.angleY += (y - previousY) * TOUCH_SCALE_FACTOR
        requestRender()
      }
    }

    performClick()

    previousX = x
    previousY = y
    return true
  }
}