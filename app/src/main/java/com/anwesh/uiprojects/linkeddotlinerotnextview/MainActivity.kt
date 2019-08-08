package com.anwesh.uiprojects.linkeddotlinerotnextview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.dotlinerotnextview.DotLineRotNextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DotLineRotNextView.create(this)
    }
}
