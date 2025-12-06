package com.example.asteroidsredux

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        config.useImmersiveMode = true
        config.useWakelock = true // Keep screen on
        initialize(AsteroidsGame(), config)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Let LibGDX handle back button through Input.Keys.BACK
        // Don't call super.onBackPressed() which would exit the app
    }
}
