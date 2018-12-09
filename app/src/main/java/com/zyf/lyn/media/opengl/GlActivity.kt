package com.zyf.lyn.media.opengl

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.zyf.lyn.media.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20.*
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GlActivity : AppCompatActivity() {

    lateinit var glSurfaceView: GLSurfaceView

    var renderSet = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gl)


        var activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        Log.e("GlActivity", "${activityManager.deviceConfigurationInfo.reqGlEsVersion}")
        if (activityManager.deviceConfigurationInfo.reqGlEsVersion > 0x20000) {
            Toast.makeText(this, "support ${activityManager.deviceConfigurationInfo.reqGlEsVersion}", Toast.LENGTH_SHORT).show()
            initGlSurface()
        } else {
            Toast.makeText(this, "not support OpenGl2.0", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initGlSurface() {
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        renderSet = true
    }

    override fun onResume() {
        super.onResume()
        if (renderSet) {
            glSurfaceView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (renderSet) {
            glSurfaceView.onPause()
        }
    }
}

class FirstGLRender : GLSurfaceView.Renderer {

    lateinit var context: Context;

    var POSITION_COMPONENT_COUNT = 2

    var tableVertices = floatArrayOf(0f, 0f, 0f, 14f, 9f, 14f, 9f, 0f)

    var tableVerticesWithTriangles = floatArrayOf(
            // Triangle 1
            0f, 0f,
            9f, 14f,
            0f, 14f,

            // Triangle 2
            0f, 0f,
            9f, 0f,
            9f, 14f
    )

    var vertexData = ByteBuffer
            .allocateDirect(tableVerticesWithTriangles.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(tableVerticesWithTriangles)
            }


    var vertexShaderSource = OpenUtils.readFile(context, R.raw.simple_vertex_shader)

    var fragmentShaderSource = OpenUtils.readFile(context, R.raw.simple_fragment_shader)

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(1.0f, 0f, 0f, 0f)

        val vertexShader = OpenUtils.compileVertexShader(vertexShaderSource)
        val fragmentShader = OpenUtils.compileFragmentShader(fragmentShaderSource)


        OpenUtils.linkProgram(vertexShader, fragmentShader)


        vertexData.position(0)

        glVertexAttribPointer(0, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexData)
    }

}
