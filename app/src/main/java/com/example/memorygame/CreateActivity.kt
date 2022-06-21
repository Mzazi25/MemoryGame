package com.example.memorygame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.memorygame.models.BoardSize
import com.example.memorygame.utils.EXTRA_BOARD_SIZE


class CreateActivity : AppCompatActivity() {

    private lateinit var boardSize: BoardSize
    private var numImagesRequired = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        numImagesRequired = boardSize.getNumPairs()
        supportActionBar?.title = "Choose Pics (0 / $numImagesRequired)"

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}