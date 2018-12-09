package com.zyf.lyn.media.opengl

import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

import android.opengl.GLES20.*
import android.util.Log
import android.opengl.GLES20.glDeleteProgram
import android.opengl.GLES20.glGetProgramInfoLog
import android.opengl.GLES20.GL_LINK_STATUS
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glCreateProgram
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glGetShaderInfoLog
import android.opengl.GLES20.GL_COMPILE_STATUS
import android.opengl.GLES20.glCompileShader
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glCreateShader
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_VERTEX_SHADER


object OpenUtils {

    fun readFile(context: Context, resourceId: Int): String {
        var body = StringBuilder()
        var nextLine = ""

        try {
            var inputStream = context.resources.openRawResource(resourceId)

            var inputStreamReader = InputStreamReader(inputStream)

            var bufferedReader = BufferedReader(inputStreamReader)
            nextLine = bufferedReader.readLine()
            while (nextLine != null) {
                body.append(nextLine)
                body.append("\n")
            }

        } catch (e: IOException) {

        } catch (nfe: Resources.NotFoundException) {

        }
        return body.toString()
    }


    fun validateProgram(programObjId: Int): Boolean {
        glValidateProgram(programObjId)

        var validateStatus = intArrayOf(1)
        glGetProgramiv(programObjId, GL_VALIDATE_STATUS, validateStatus, 0)
        Log.v(TAG, "Results of validating program: " + validateStatus[0]
                + "\nLog:" + glGetProgramInfoLog(programObjId));

        return validateStatus[0] != 0;
    }


    /** * Links a vertex shader and a fragment shader together into an OpenGL * program. Returns the OpenGL program object ID, or 0 if linking failed.  */
    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {

        // Create a new program object.
        val programObjectId = glCreateProgram()

        if (programObjectId == 0) {
            Log.w(TAG, "Could not create new program")
            return 0
        }

        // Attach the vertex shader to the program.
        glAttachShader(programObjectId, vertexShaderId)
        // Attach the fragment shader to the program.
        glAttachShader(programObjectId, fragmentShaderId)

        // Link the two shaders together into a program.
        glLinkProgram(programObjectId)

        // Get the link status.
        val linkStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)

        // Print the program info log to the Android log output.
        Log.v(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programObjectId))

        // Verify the link status.
        if (linkStatus[0] == 0) {
            // If it failed, delete the program object.
            glDeleteProgram(programObjectId)
            Log.w(TAG, "Linking of program failed.")
            return 0
        }

        // Return the program object ID.
        return programObjectId
    }


    /** * Compiles a shader, returning the OpenGL object ID.  */
    private fun compileShader(type: Int, shaderCode: String): Int {

        // Create a new shader object.
        val shaderObjectId = glCreateShader(type)

        if (shaderObjectId == 0) {
            Log.w(TAG, "Could not create new shader.")

            return 0
        }

        // Pass in the shader source.
        glShaderSource(shaderObjectId, shaderCode)

        // Compile the shader.
        glCompileShader(shaderObjectId)

        // Get the compilation status.
        val compileStatus = IntArray(1)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)

        // Print the shader info log to the Android log output.
        Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:"
                + glGetShaderInfoLog(shaderObjectId))

        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId)

            Log.w(TAG, "Compilation of shader failed.")

            return 0
        }

        // Return the shader object ID.
        return shaderObjectId
    }


    /** * Loads and compiles a vertex shader, returning the OpenGL object ID.  */
    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GL_VERTEX_SHADER, shaderCode)
    }

    /** * Loads and compiles a fragment shader, returning the OpenGL object ID.  */
    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode)
    }
}

const val TAG = "OpenGlTAG"