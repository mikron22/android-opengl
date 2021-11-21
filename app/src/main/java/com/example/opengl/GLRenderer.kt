package com.example.opengl

import android.graphics.BitmapFactory
import android.content.Context
import android.opengl.*
import android.util.Log
import com.example.opengl.meshes.TexturedCubeMesh
import com.example.opengl.meshes.TexturedPyramidMesh
import com.example.opengl.shaders.ShaderProgram
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig
import kotlin.math.tan


/**
 * @author Marek Kulawiak
 */
class GLRenderer(
  private val appContext: Context
) : GLSurfaceView.Renderer {
  // Macierze modelu, widoku i projekcji.
  private var modelMatrix = FloatArray(16)
  private var pyramidMatrix = FloatArray(16)
  private var viewMatrix = FloatArray(16)
  private var projectionMatrix = FloatArray(16)

  // Iloczyn macierzy modelu, widoku i projekcji.
  private var MVPMatrix = FloatArray(16)
  private var MVMatrix = FloatArray(16)

  // Informacja o tym, z ilu elementów składają się poszczególne atrybuty.
  private val POSITION_DATA_SIZE = 3
  private val COLOUR_DATA_SIZE = 4
  private val NORMAL_DATA_SIZE = 3
  private val TEXCOORD_DATA_SIZE = 2

  // Wartości wykorzystywane przez naszą kamerę. Pierwsze trzy elementy opisują położenie obserwatora,
  // kolejne trzy wskazują na punkt, na który on patrzy, a ostatnie wartości definiują, w którym kierunku
  // jest "góra" (tzw. "up vector").
  private var camera: FloatArray = floatArrayOf(
    0f, 0f, 1.5f,  // pozycja obserwatora
    0f, 0f, 0f,  // punkt na który obserwator patrzy
    0f, 1f, 0f
  )
  private var colShaders: ShaderProgram = ShaderProgram()
  private var texShaders: ShaderProgram = ShaderProgram()

  // Modele obiektów.
  private var texturedCubeMesh: TexturedCubeMesh = TexturedCubeMesh()
  private var texturedPyramidMesh: TexturedPyramidMesh = TexturedPyramidMesh()

  // Adresy tekstur w pamięci modułu graficznego.
  private var crateTextureDataHandle = 0
  private var pyramidTextureDataHandle = 0

  var angleX: Float = 0f
  var angleY: Float = 0f

  // Stworzenie kontekstu graficznego.
  override fun onSurfaceCreated(nieUzywac: GL10, config: EGLConfig) {
    // Kolor tła.
    GLES20.glClearColor(0.05f, 0.05f, 0.1f, 1.0f)

    // Ukrywanie wewnętrznych ścian.
    GLES20.glEnable(GLES20.GL_CULL_FACE)

    // Właczenie sprawdzania głębokości.
    GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    GLES20.glDepthFunc(GLES20.GL_LEQUAL)
    GLES20.glDepthMask(true)

    // Wczytanie tekstur do pamięci.
    crateTextureDataHandle = readTexture(R.drawable.crate_borysses_deviantart_com)

    pyramidTextureDataHandle = readTexture(R.drawable.stone_agf81_deviantart_com)

    // Utworzenie shaderów korzystających z kolorów wierzchołków.
    val colShadersAttributes = arrayOf("vertexPosition", "vertexColour", "vertexNormal")
    colShaders.init(
      R.raw.col_vertex_shader,
      R.raw.col_fragment_shader,
      colShadersAttributes,
      appContext,
      "kolorowanie"
    )

    // Utworzenie shaderów korzystających z tekstur.
    val texShadersAttributes = arrayOf("vertexPosition", "vertexTexCoord", "vertexNormal")
    texShaders.init(
      R.raw.tex_vertex_shader,
      R.raw.tex_fragment_shader,
      texShadersAttributes,
      appContext,
      "teksturowanie"
    )
    Matrix.setIdentityM(modelMatrix, 0) // Zresetowanie pozycji modelu.
    Matrix.translateM(modelMatrix, 0, -2.0f, 0f, -3.0f) // Przesunięcie modelu.

    Matrix.setIdentityM(pyramidMatrix, 0) // Zresetowanie pozycji modelu.
    Matrix.translateM(pyramidMatrix, 0, 2.0f, 0f, -3.0f) // Przesunięcie modelu.
  }

  // Metoda wywoływana przy każdym przeskalowaniu okna.
  override fun onSurfaceChanged(nieUzywac: GL10, width: Int, height: Int) {
    Log.d("KSG", "Rozdzielczość: $width x $height")

    // Rozciągnięcie widoku OpenGL ES do rozmiarów ekranu.
    GLES20.glViewport(0, 0, width, height)

    // Przygotowanie macierzy projekcji perspektywicznej z uwzględnieniem Field of View.
    val ratio = width.toFloat() / height
    val fov = 60f
    val near = 1.0f
    val far = 10000.0f
    val top = (tan(fov * Math.PI / 360.0f) * near).toFloat()
    val bottom = -top
    val left = ratio * bottom
    val right = ratio * top
    Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far)
  }

  // Metoda renderująca aktualną klatkę.
  override fun onDrawFrame(nieUzywac: GL10) {
    // Wyczyszczenie buforów głębi i kolorów.
    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)

    // Ustawienie kamery.
    Matrix.setIdentityM(viewMatrix, 0)
    Matrix.setLookAtM(
      viewMatrix, 0,
      camera[0],
      camera[1],
      camera[2], camera[3], camera[4], camera[5], camera[6], camera[7], camera[8]
    )

    // Transformacja i rysowanie brył.
    GLES20.glUseProgram(texShaders.programHandle) // Użycie shaderów korzystających z teksturowania.
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0) // Wykorzystanie tekstury o indeksie 0.
    GLES20.glBindTexture(
      GLES20.GL_TEXTURE_2D,
      crateTextureDataHandle
    ) // Podpięcie tekstury skrzyni.
    GLES20.glUniform1i(
      texShaders._diffuseTextureHandle,
      0
    ) // Przekazanie shaderom indeksu tekstury (0).
    Matrix.rotateM(modelMatrix, 0, 0.5f, 1f, 1f, 0f)
    Matrix.rotateM(pyramidMatrix, 0, 0.5f, 1f, 1f, 0f)

    Matrix.rotateM(viewMatrix, 0, angleX/2, 0f, -1f, 0f)
    Matrix.rotateM(viewMatrix, 0, angleY/2, -1f, 0f, 0f)

    drawShape(
      texturedCubeMesh.positionBuffer!!,
      null,
      texturedCubeMesh.normalBuffer!!,
      texturedCubeMesh.texCoordsBuffer!!,
      texShaders,
      texturedCubeMesh.numberOfVertices,
      modelMatrix
    )

    GLES20.glBindTexture(
      GLES20.GL_TEXTURE_2D,
      pyramidTextureDataHandle
    )

    drawShape(
      texturedPyramidMesh.positionBuffer!!,
      null,
      texturedPyramidMesh.normalBuffer!!,
      texturedPyramidMesh.texCoordsBuffer!!,
      texShaders,
      texturedPyramidMesh.numberOfVertices,
      pyramidMatrix
    )
  }

  private fun drawShape(
    positionBuffer: FloatBuffer?,
    colourBuffer: FloatBuffer?,
    normalBuffer: FloatBuffer,
    texCoordsBuffer: FloatBuffer?,
    shaderProgram: ShaderProgram,
    numberOfVertices: Int,
    modelMatrix: FloatArray
  ) {
    positionBuffer ?: return

    // Podpięcie bufora pozycji wierzchołków.
    positionBuffer.position(0)
    GLES20.glVertexAttribPointer(
      shaderProgram._vertexPositionHandle,
      POSITION_DATA_SIZE,
      GLES20.GL_FLOAT,
      false,
      0,
      positionBuffer
    )
    GLES20.glEnableVertexAttribArray(shaderProgram._vertexPositionHandle)

    // Podpięcie buforów kolorów lub współrzędnych tekstury (w zależności od wykorzystanych shaderów).
    if (colourBuffer != null && shaderProgram._vertexColourHandle >= 0) {
      colourBuffer.position(0)
      GLES20.glVertexAttribPointer(
        shaderProgram._vertexColourHandle,
        COLOUR_DATA_SIZE,
        GLES20.GL_FLOAT,
        false,
        0,
        colourBuffer
      )
      GLES20.glEnableVertexAttribArray(shaderProgram._vertexColourHandle)
    } else if (texCoordsBuffer != null && shaderProgram._vertexTexCoordHandle >= 0) {
      texCoordsBuffer.position(0)
      GLES20.glVertexAttribPointer(
        shaderProgram._vertexTexCoordHandle,
        TEXCOORD_DATA_SIZE,
        GLES20.GL_FLOAT,
        false,
        0,
        texCoordsBuffer
      )
      GLES20.glEnableVertexAttribArray(shaderProgram._vertexTexCoordHandle)
    }

    // Podpięcie bufora normalnych.
    normalBuffer.position(0)
    GLES20.glVertexAttribPointer(
      shaderProgram._vertexNormalHandle,
      NORMAL_DATA_SIZE,
      GLES20.GL_FLOAT,
      false,
      0,
      normalBuffer
    )
    GLES20.glEnableVertexAttribArray(shaderProgram._vertexNormalHandle)

    // Przemnożenie macierzy modelu, widoku i projekcji.
    Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0)
    Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0)

    // Przekazanie zmiennych uniform.
    GLES20.glUniformMatrix4fv(shaderProgram._MVPMatrixHandle, 1, false, MVPMatrix, 0)
    GLES20.glUniformMatrix4fv(shaderProgram._MVMatrixHandle, 1, false, MVMatrix, 0)

    // Narysowanie obiektu.
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numberOfVertices)
  }

  // Metoda wczytująca teksturę z katalogu drawable.
  private fun readTexture(resourceId: Int): Int {
    Log.d("KSG", "Wczytywanie tekstury.")
    val textureHandle = IntArray(1)
    GLES20.glGenTextures(1, textureHandle, 0) // Wygenerowanie tekstury i pobranie jej adresu.
    if (textureHandle[0] == 0) {
      Log.e("KSG", "Błąd przy wczytywaniu tekstury.")
      return -1
    }
    val options = BitmapFactory.Options()
    options.inScaled = false // Wyłączenie skalowania.

    val bitmap = BitmapFactory.decodeResource(appContext.resources, resourceId, options)
    Log.d("KSG", " bitmap resolution: " + bitmap.width + " x " + bitmap.height)

    // Podpięcie tekstury.
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

    // Ustawienie rodzaju filtrowania tekstury.
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

    // Wczytanie zawartości bitmapy do tekstury.
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

    // Zwolnienie pamięci zajmowanej przez zmienną bitmap.
    bitmap.recycle()
    return textureHandle[0]
  }
}
