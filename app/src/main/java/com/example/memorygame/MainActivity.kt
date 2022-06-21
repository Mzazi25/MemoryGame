package com.example.memorygame

import android.animation.ArgbEvaluator
import android.app.Instrumentation
import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayoutStates
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import com.example.memorygame.models.MemoryGame
import com.example.memorygame.utils.EXTRA_BOARD_SIZE
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
//        private const val CREATE_REQUEST_CODE = 248
    }

    private lateinit var memoryGame: MemoryGame
    private lateinit var rvBoard: RecyclerView
    private lateinit var adapter : MemoryBoardAdapter
    private lateinit var tvNumPairs : TextView
    private lateinit var tvNumMoves : TextView
    private lateinit var clRoot: ConstraintLayout

    private var boardSize: BoardSize = BoardSize.Easy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvBoard = findViewById(R.id.rvBoard)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        clRoot = findViewById(R.id.clRoot)

        setupBoard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_refresh ->{
                if(memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Quit your current Game?", null,View.OnClickListener {
                        setupBoard()
                    })
                }else{
                    setupBoard()
                }
                //Setup the game again
                return true
            }
            R.id.mi_new_size ->{
                showNewSizeDialog()
                return true

            }
            R.id.mi_custom ->{
                showCreationDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCreationDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        showAlertDialog("Create your own Memory Board",boardSizeView, View.OnClickListener {
            //Set new value for the board size
            val desiredBoardSize = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy ->{
                    BoardSize.Easy
                }
                R.id.rbMedium ->{
                    BoardSize.Medium
                }

                else ->BoardSize.Hard
            }

            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra(EXTRA_BOARD_SIZE,desiredBoardSize)
            startActivity(intent)


        })


    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when(boardSize){
            BoardSize.Easy -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.Medium -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.Hard -> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose New Size",boardSizeView, View.OnClickListener {
            //Set new value for the board size
            boardSize = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy ->{
                    BoardSize.Easy
                }
                R.id.rbMedium ->{
                    BoardSize.Medium
                }

                else ->BoardSize.Hard
            }
            setupBoard()
        })
    }

    private fun showAlertDialog(title: String, view: View?,positiveClickListener:View.OnClickListener) {
        AlertDialog.Builder(this)
            .setView(view)
            .setTitle(title)
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Ok"){_,_->
                positiveClickListener.onClick(null)

            }.show()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun setupBoard() {
        when(boardSize){
            BoardSize.Easy -> {
                tvNumMoves.text = "Easy: 4 *2"
                tvNumPairs.text = "Pairs:0/4"
            }
            BoardSize.Medium -> {
                tvNumMoves.text = "Medium: 6 *3"
                tvNumPairs.text = "Pairs:0/9"
            }
            BoardSize.Hard -> {
                tvNumMoves.text = "Hard: 6 *4"
                tvNumPairs.text = "Pairs:0/12"
            }

        }
        tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize)
        adapter = MemoryBoardAdapter(this, boardSize,memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    private fun updateGameWithFlip(position: Int) {
        //Error Checking
        if(memoryGame.haveWonGame()){
            //Alert user of an invalid move
            Snackbar.make(clRoot, "You already won!",Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)){
            //Alert User of an invalid move
            Snackbar.make(clRoot, "Invalid Move!",Snackbar.LENGTH_SHORT).show()
            return
        }
        if(memoryGame.flipCard(position)){
            Log.i(TAG, "Found a match NumPairs found ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat()/boardSize.getNumPairs(),
                ContextCompat.getColor(this,R.color.color_progress_none),
                ContextCompat.getColor(this,R.color.color_progress_full)

            )as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs:${memoryGame.numPairsFound}/${boardSize.getNumPairs()}"
            if(memoryGame.haveWonGame()){
                Snackbar.make(clRoot,"You won, Congratulations!" , Snackbar.LENGTH_LONG).show()
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }


}