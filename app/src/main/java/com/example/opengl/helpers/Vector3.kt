package com.example.opengl.helpers

/**
 * @author Marek Kulawiak
 */
// Klasa kontenerowa do przechowywania trójwymiarowych współrzędnych.
class Vector3 @JvmOverloads constructor(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
  constructor(pos: Vector3) : this(pos.x, pos.y, pos.z) {}

  fun add(pos: Vector3) {
    x += pos.x
    y += pos.y
    z += pos.z
  }

  fun multiply(value: Float): Vector3 {
    x *= value
    y *= value
    z *= value
    return Vector3(this)
  }

  fun toFloatArray(): FloatArray {
    val vals = FloatArray(4)
    vals[0] = x
    vals[1] = y
    vals[2] = z
    vals[3] = 0f
    return vals
  }
}
