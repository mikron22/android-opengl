package com.example.opengl.shaders

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder


/**
 * @author Marek Kulawiak
 */
class ShaderProgram {
  var programHandle = 0

  // Adresy zmiennych uniform w shaderach.
  var _MVPMatrixHandle = -1
  var _MVMatrixHandle = -1
  var _diffuseTextureHandle = -1

  // Adresy atrybutów w vertex shaderze.
  var _vertexPositionHandle = -1
  var _vertexColourHandle = -1
  var _vertexNormalHandle = -1
  var _vertexTexCoordHandle = -1
  fun init(
    vertexShaderId: Int,
    fragmentShaderId: Int,
    attributeBindings: Array<String>,
    appContext: Context,
    debugName: String
  ) {
    programHandle =
      createShaders(vertexShaderId, fragmentShaderId, attributeBindings, appContext, debugName)
    variableHandles
  }

  val variableHandles: Unit
    get() {
      Log.d("KSG", "Pobranie adresów zmiennych w shaderach.")
      _MVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "MVPMatrix")
      _MVMatrixHandle = GLES20.glGetUniformLocation(programHandle, "MVMatrix")
      _vertexPositionHandle = GLES20.glGetAttribLocation(programHandle, "vertexPosition")
      _vertexColourHandle = GLES20.glGetAttribLocation(programHandle, "vertexColour")
      _vertexNormalHandle = GLES20.glGetAttribLocation(programHandle, "vertexNormal")
      _vertexTexCoordHandle = GLES20.glGetAttribLocation(programHandle, "vertexTexCoord")
      _diffuseTextureHandle = GLES20.glGetUniformLocation(programHandle, "diffuseTexture")
    }

  protected fun createShaders(
    vertexShaderId: Int,
    fragmentShaderId: Int,
    attributeBindings: Array<String>,
    appContext: Context,
    debugName: String
  ): Int {
    Log.d("KSG", "Przygotowanie shaderów ($debugName).")
    Log.d("KSG", " Wczytywanie vertex shadera.")
    val vertexShader = readShaderFile(vertexShaderId, appContext)
    if (vertexShader == null) {
      Log.e("KSG", "Plik vertex shadera nie został wczytany.")
    }
    Log.d("KSG", " Wczytywanie fragment shadera.")
    val fragmentShader = readShaderFile(fragmentShaderId, appContext)
    if (fragmentShader == null) {
      Log.e("KSG", "Plik fragment shadera nie został wczytany.")
    }
    Log.d("KSG", " Kompilacja vertex shadera.")
    var vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
    if (vertexShaderHandle == 0) {
      Log.e("KSG", "Nie można utworzyć vertex shadera.")
      return -1
    } else {
      GLES20.glShaderSource(vertexShaderHandle, vertexShader)
      GLES20.glCompileShader(vertexShaderHandle)
      val compileStatus = IntArray(1)
      GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
      if (compileStatus[0] == 0) {
        Log.e("KSG", "Błąd kompilacji vertex shadera.")
        Log.e("KSG", GLES20.glGetShaderInfoLog(vertexShaderHandle))
        GLES20.glDeleteShader(vertexShaderHandle)
        vertexShaderHandle = 0
        return -1
      }
    }
    Log.d("KSG", " Kompilacja fragment shadera.")
    var fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
    if (fragmentShaderHandle == 0) {
      Log.e("KSG", "Nie można utworzyć fragment shadera.")
      return -1
    } else {
      GLES20.glShaderSource(fragmentShaderHandle, fragmentShader)
      GLES20.glCompileShader(fragmentShaderHandle)
      val compileStatus = IntArray(1)
      GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
      if (compileStatus[0] == 0) {
        Log.e("KSG", "Błąd kompilacji fragment shadera.")
        Log.e("KSG", GLES20.glGetShaderInfoLog(fragmentShaderHandle))
        GLES20.glDeleteShader(fragmentShaderHandle)
        fragmentShaderHandle = 0
        return -1
      }
    }
    Log.d("KSG", " Linkowanie shaderów.")
    var programHandle = GLES20.glCreateProgram()
    if (programHandle == 0) {
      Log.e("KSG", "Nie można podlinkować shaderów.")
      return -1
    } else {
      GLES20.glAttachShader(programHandle, vertexShaderHandle)
      GLES20.glAttachShader(programHandle, fragmentShaderHandle)
      var tempAttributes = ""
      for (i in attributeBindings.indices) {
        GLES20.glBindAttribLocation(programHandle, i, attributeBindings[i])
        tempAttributes += " " + attributeBindings[i]
      }
      Log.d("KSG", " shader attributes:$tempAttributes")
      //GLES20.glBindAttribLocation(programHandle, 0, "vertexPosition");
      //GLES20.glBindAttribLocation(programHandle, 1, "vertexColour");
      //GLES20.glBindAttribLocation(programHandle, 2, "vertexNormal");
      GLES20.glLinkProgram(programHandle)
      val linkStatus = IntArray(1)
      GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)
      if (linkStatus[0] == 0) {
        Log.e("KSG", "Błąd linkowania shaderów.")
        GLES20.glDeleteProgram(programHandle)
        programHandle = 0
        return -1
      }
    }

    // Wykorzystanie utworzonych shaderów podczas rysowania.
    //GLES20.glUseProgram(programHandle);
    return programHandle
  }

  // Metoda wczytująca kod shadera z katalogu raw.
  fun readShaderFile(resourceId: Int, appContext: Context): String? {
    if (appContext == null) {
      Log.e("KSG", "readShaderFile: appContext == null")
      return null
    }
    val inputStream: InputStream = appContext.getResources().openRawResource(resourceId)
    val inputStreamReader = InputStreamReader(inputStream)
    val bufferedReader = BufferedReader(inputStreamReader)
    var line: String? = ""
    val sb = StringBuilder()
    try {
      while (bufferedReader.readLine().also { line = it } != null) {
        sb.append(line)
        sb.append('\n')
        //Log.d("KSG", line);
      }
    } catch (e: Exception) {
      Log.e("KSG", "Błąd przy wczytywaniu pliku: " + e.message)
      return null
    }
    return sb.toString()
  }
}
