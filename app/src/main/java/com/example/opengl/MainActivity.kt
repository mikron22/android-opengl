package com.example.opengl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * @author Marek Kulawiak
 */
class MainActivity : AppCompatActivity() {
  private var glSurfaceView: ESSurfaceView? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    glSurfaceView = ESSurfaceView(this)
    setContentView(glSurfaceView)
  }

  override fun onResume() {
    // The activity must call the GL surface view's onResume() on activity onResume().
    super.onResume()
    glSurfaceView?.onResume()
  }

  override fun onPause() {
    // The activity must call the GL surface view's onPause() on activity onPause().
    super.onPause()
    glSurfaceView?.onPause()
  }
}
