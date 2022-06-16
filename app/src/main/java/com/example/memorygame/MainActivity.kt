package com.example.memorygame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import com.example.memorygame.models.MemoryCard
import com.example.memorygame.utils.DEFAULT_ICONS

private lateinit var rvBoard: RecyclerView
private lateinit var tvNumPairs : TextView
private lateinit var tvNumMoves : TextView

private var boardSize: BoardSize = BoardSize.Hard
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvBoard = findViewById(R.id.rvBoard)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages =(chosenImages +chosenImages ).shuffled()
        val memoryCards = randomizedImages.map { MemoryCard(it, isFaceUp = false, isMatched = false) }

        rvBoard.adapter = MemoryBoardAdapter(this, boardSize,memoryCards)
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }


}