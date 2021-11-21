package com.example.opengl.meshes

class TexturedPyramidMesh : BaseMesh() {
  private val positionData = floatArrayOf( // Tył
    -1f, -1f, -1f,
    0f, 1f, 0f,
    1f, -1f, -1f, // Prawa
    1f, -1f, -1f,
    0f, 1f, 0f,
    1f, -1f, 1f, // Przód
    1f, -1f, 1f,
    0f, 1f, 0f,
    -1f, -1f, 1f, // Lewa
    -1f, -1f, 1f,
    0f, 1f, 0f,
    -1f, -1f, -1f, // Dół
    -1f, -1f, -1f,
    1f, -1f, -1f,
    1f, -1f, 1f,
    1f, -1f, 1f,
    -1f, -1f, 1f,
    -1f, -1f, -1f
  )

  // Współrzędne tekstury są takie same dla wszystkich ścian.
  private val texCoordData = floatArrayOf(
    0.0f, 0.0f,
    0.5f, 1.0f,
    1.0f, 0.0f,
    0.0f, 0.0f,
    0.5f, 1.0f,
    1.0f, 0.0f,
    0.0f, 0.0f,
    0.5f, 1.0f,
    1.0f, 0.0f,
    0.0f, 0.0f,
    0.5f, 1.0f,
    1.0f, 0.0f,

    0.0f, 1.0f,
    1.0f, 1.0f,
    1.0f, 0.0f,

    1.0f, 0.0f,
    0.0f, 0.0f,
    0.0f, 1.0f,
  )
  private val normalData = floatArrayOf(
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f
  )

  init {
    numberOfVertices = 18
    positionBuffer = createBuffer(positionData)
    texCoordsBuffer = createBuffer(texCoordData)
    normalBuffer = createBuffer(normalData)
  }
}
