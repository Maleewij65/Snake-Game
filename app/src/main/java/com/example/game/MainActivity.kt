package com.example.game

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : Activity() {

    private var soundPool: SoundPool? = null
    private var eatSoundId: Int = 0
    private var gameOverSoundId: Int = 0

    private var level2Unlocked = false
    private var highestScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        loadHighestScore()


        val highestScoreTextView = findViewById<TextView>(R.id.highest_score)
        highestScoreTextView.text = "Highest Score: $highestScore"
        highestScoreTextView.visibility = View.VISIBLE


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build()
        } else {
            soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
        }

        // sound files
        eatSoundId = soundPool?.load(this, R.raw.eat_sound, 1) ?: 0
        gameOverSoundId = soundPool?.load(this, R.raw.game_over_sound, 1) ?: 0

        val board = findViewById<RelativeLayout>(R.id.board)
        val border = findViewById<RelativeLayout>(R.id.relativeLayout)
        val lilu = findViewById<LinearLayout>(R.id.lilu)
        val upButton = findViewById<Button>(R.id.up)
        val downButton = findViewById<Button>(R.id.down)
        val leftButton = findViewById<Button>(R.id.left)
        val rightButton = findViewById<Button>(R.id.right)
        val pauseButton = findViewById<Button>(R.id.pause)
        val newgame = findViewById<Button>(R.id.new_game)
        val level2 = findViewById<Button>(R.id.level2)

        val resume = findViewById<Button>(R.id.resume)
        val playagain = findViewById<Button>(R.id.playagain)
        val score = findViewById<Button>(R.id.score)
        val score2 = findViewById<Button>(R.id.score2)
        val meat = ImageView(this)
        val snake = ImageView(this)
        val snakeSegments = mutableListOf(snake)
        val handler = Handler()
        var delayMillis = 30L //make snake longerr
        var currentDirection = "right"
        var scorex = 0




        val snakeGameTitle = findViewById<TextView>(R.id.topic)
        snakeGameTitle.visibility = View.VISIBLE

        board.visibility = View.INVISIBLE
        playagain.visibility = View.INVISIBLE
        score.visibility = View.INVISIBLE
        score2.visibility = View.INVISIBLE

        newgame.setOnClickListener {
            board.visibility = View.VISIBLE
            newgame.visibility = View.INVISIBLE
            level2.visibility = if (level2Unlocked) View.VISIBLE else View.INVISIBLE
            resume.visibility = View.INVISIBLE
            score2.visibility = View.VISIBLE
            snakeGameTitle.visibility = View.INVISIBLE

            snake.setImageResource(R.drawable.snake)
            snake.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            board.addView(snake)
            snakeSegments.add(snake)

            var snakeX = snake.x
            var snakeY = snake.y

            meat.setImageResource(R.drawable.meat)
            meat.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            board.addView(meat)

            val random = Random()
            val randomX = random.nextInt(801) - 400
            val randomY = random.nextInt(801) - 400

            meat.x = randomX.toFloat()
            meat.y = randomY.toFloat()

            fun checkFoodCollision() {
                val distanceThreshold = 50

                val distance = sqrt((snake.x - meat.x).pow(2) + (snake.y - meat.y).pow(2))

                if (distance < distanceThreshold) {
                    val newSnake =
                        ImageView(this)
                    newSnake.setImageResource(R.drawable.snake)
                    newSnake.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    board.addView(newSnake)
                    snakeSegments.add(newSnake)

                    val randomX = random.nextInt(801) - -100
                    val randomY = random.nextInt(801) - -100

                    meat.x = randomX.toFloat()
                    meat.y = randomY.toFloat()

                    delayMillis--
                    scorex++
                    score2.text = "score : $scorex"

                    // Play eat sound effect
                    soundPool?.play(eatSoundId, 1.0f, 1.0f, 0, 0, 1.0f)

                    // Unlock level 2 if score reaches 20
                    if (!level2Unlocked && scorex >= 25) {
                        Toast.makeText(this@MainActivity, "Level 2 unlocked!", Toast.LENGTH_SHORT).show()
                        level2Unlocked = true
                    }
                }
            }

            val runnable = object : Runnable {
                @SuppressLint("SetTextI18n")
                override fun run() {
                    for (i in snakeSegments.size - 1 downTo 1) {
                        snakeSegments[i].x = snakeSegments[i - 1].x
                        snakeSegments[i].y = snakeSegments[i - 1].y
                    }

                    when (currentDirection) {
                        "up" -> {
                            snakeY -= 10
                            if (snakeY < -490) {
                                snakeY = -490f
                                border.setBackgroundColor(resources.getColor(R.color.red))
                                playagain.visibility = View.VISIBLE
                                currentDirection = "pause"
                                lilu.visibility = View.INVISIBLE
                                score.text = "your score is  $scorex"
                                score.visibility = View.VISIBLE
                                score2.visibility = View.INVISIBLE


                                soundPool?.play(gameOverSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
                            }
                            snake.translationY = snakeY
                        }
                        "down" -> {
                            snakeY += 10
                            val maxY =
                                board.height / 2 - snake.height + 30
                            if (snakeY > maxY) {
                                snakeY = maxY.toFloat()
                                border.setBackgroundColor(resources.getColor(R.color.red))
                                playagain.visibility = View.VISIBLE
                                currentDirection = "pause"
                                lilu.visibility = View.INVISIBLE
                                score.text = "your score is  $scorex"
                                score.visibility = View.VISIBLE
                                score2.visibility = View.INVISIBLE

                                // Play game over sound effect
                                soundPool?.play(gameOverSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
                            }
                            snake.translationY = snakeY
                        }
                        "left" -> {
                            snakeX -= 10
                            if (snakeX < -490) {
                                snakeX = -490f
                                border.setBackgroundColor(resources.getColor(R.color.red))
                                playagain.visibility = View.VISIBLE
                                currentDirection = "pause"
                                lilu.visibility = View.INVISIBLE
                                score.text = "your score is  $scorex"
                                score.visibility = View.VISIBLE
                                score2.visibility = View.INVISIBLE

                                // Play game over sound effect
                                soundPool?.play(gameOverSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
                            }
                            snake.translationX = snakeX
                        }
                        "right" -> {
                            snakeX += 10
                            val maxX =
                                board.height / 2 - snake.height + 30
                            if (snakeX > maxX) {
                                snakeX = maxX.toFloat()
                                border.setBackgroundColor(resources.getColor(R.color.red))
                                playagain.visibility = View.VISIBLE
                                currentDirection = "pause"
                                lilu.visibility = View.INVISIBLE
                                score.text = "your score is  $scorex"
                                score.visibility = View.VISIBLE
                                score2.visibility = View.INVISIBLE


                                soundPool?.play(gameOverSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
                            }
                            snake.translationX = snakeX
                        }
                        "pause" -> {
                            snakeX += 0
                            snake.translationX = snakeX
                        }
                    }

                    checkFoodCollision()
                    handler.postDelayed(this, delayMillis)
                }
            }

            handler.postDelayed(runnable, delayMillis)

            // Set button onClickListeners to update the currentDirection variable when pressed
            upButton.setOnClickListener {
                currentDirection = "up"
            }
            downButton.setOnClickListener {
                currentDirection = "down"
            }
            leftButton.setOnClickListener {
                currentDirection = "left"
            }
            rightButton.setOnClickListener {
                currentDirection = "right"
            }
            pauseButton.setOnClickListener {
                currentDirection = "pause"
                board.visibility = View.INVISIBLE
                newgame.visibility = View.VISIBLE
                level2.visibility = if (level2Unlocked) View.VISIBLE else View.INVISIBLE
                resume.visibility = View.VISIBLE
                snakeGameTitle.visibility = View.VISIBLE
            }
            resume.setOnClickListener {
                currentDirection = "right"
                board.visibility = View.VISIBLE
                newgame.visibility = View.INVISIBLE
                level2.visibility = View.INVISIBLE
                resume.visibility = View.INVISIBLE
                snakeGameTitle.visibility = View.INVISIBLE
            }
            playagain.setOnClickListener {
                recreate()
            }
        }

        //intent
        level2.setOnClickListener {
             if (level2Unlocked) {
                // Start level 2 activity
                val intent = Intent(this@MainActivity, SecondLevelActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@MainActivity, "Level 2 is locked! Score 25 points in level 1 to unlock!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadHighestScore() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        highestScore = sharedPreferences.getInt("highest_score", 0)
    }

    private fun saveHighestScore(score: Int) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("highest_score", score)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release SoundPool resources
        soundPool?.release()
        soundPool = null
    }


}
