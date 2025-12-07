package com.example.asteroidsredux.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException

class ShaderManager : Disposable {
    private val shaders = mutableMapOf<String, ShaderProgram>()

    fun load() {
        loadShader("cold", "shaders/default.vert", "shaders/cold.frag")
    }

    private fun loadShader(name: String, vertPath: String, fragPath: String) {
        val vert = Gdx.files.internal(vertPath)
        val frag = Gdx.files.internal(fragPath)
        
        val program = ShaderProgram(vert, frag)
        if (!program.isCompiled) {
            throw GdxRuntimeException("Error compiling shader $name: ${program.log}")
        }
        
        if (program.log.isNotEmpty()) {
            Gdx.app.log("ShaderManager", "Shader $name log: ${program.log}")
        }
        
        shaders[name] = program
    }

    fun get(name: String): ShaderProgram {
        return shaders[name] ?: throw GdxRuntimeException("Shader $name not found")
    }

    override fun dispose() {
        shaders.values.forEach { it.dispose() }
        shaders.clear()
    }
}
