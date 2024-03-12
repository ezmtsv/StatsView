package ru.netology.statsview

import android.animation.Animator.AnimatorListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import ru.netology.statsview.ui.StatsView
import android.view.animation.AnimationUtils
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<StatsView>(R.id.statsView)
        view.data = listOf(
            42F,
            10F,
            8F,
            15F,
            35F,
        )

        val label = findViewById<TextView>(R.id.label)
        view.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.animation).apply {
                setAnimationListener(object : Animation.AnimationListener{
                    override fun onAnimationStart(animation: Animation?) {
                        label.text = "started"
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        label.text = "ended"
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        label.text = "repeat"
                    }
                })
            }
        )
    }
}