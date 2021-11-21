package com.example.opengl.meshes

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author Marek Kulawiak
 */
// Klasa reprezentująca pojedynczy model.
abstract class BaseMesh {
  // Rozmiar typu float w bajtach.
  protected val BYTES_PER_FLOAT = 4
  var positionBuffer: FloatBuffer? = null
  var colourBuffer: FloatBuffer? = null
  var normalBuffer: FloatBuffer? = null
  var texCoordsBuffer: FloatBuffer? = null
  var numberOfVertices = 0
    protected set

  fun createBuffer(data: FloatArray): FloatBuffer {
    val buffer: FloatBuffer = ByteBuffer.allocateDirect(data.size * BYTES_PER_FLOAT)
      .order(ByteOrder.nativeOrder()).asFloatBuffer()
    buffer.put(data).position(0)
    return buffer
  }
}
