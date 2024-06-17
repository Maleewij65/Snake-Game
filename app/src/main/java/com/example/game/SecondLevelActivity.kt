package com.example.game

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.game.R.id.*
import java.util.*

class SecondLevelActivity : Activity() {
    private lateinit var board: RelativeLayout
    private lateinit var obstacles: MutableList<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_level)


        board = findViewById(R.id.board)


        obstacles = mutableListOf()


        addObstacles()


        val pauseButton = findViewById<Button>(pause)
        pauseButton.setOnClickListener {

        }
    }

    private fun addObstacles() {
        val obstacleCount = 5
        val random = Random()

        for (i in 0 until obstacleCount) {
            val obstacle = ImageView(this)
            obstacle.setImageResource(R.drawable.obstacles)
            obstacle.layoutParams = RelativeLayout.LayoutParams(100, 100)

            obstacle.x = random.nextInt(board.width - obstacle.width).toFloat()
            obstacle.y = random.nextInt(board.height - obstacle.height).toFloat()
            board.addView(obstacle)
            obstacles.add(obstacle)
        }
    }
}
