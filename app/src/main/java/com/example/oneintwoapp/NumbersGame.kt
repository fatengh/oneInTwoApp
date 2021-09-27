package com.example.oneintwoapp

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_guess_the_phrase.*
import kotlin.random.Random

class NumbersGame : AppCompatActivity() {
    private lateinit var clRoot: ConstraintLayout
    private lateinit var guessField: EditText
    private lateinit var guessButton: Button
    private lateinit var messages: ArrayList<String>
    private lateinit var tvPrompt: TextView

    private var answer = 0
    private var numOfGus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numbers_game)
        answer = Random.nextInt(10)

        clRoot = findViewById(R.id.clRoot)
        messages = ArrayList()

        tvPrompt = findViewById(R.id.tvPrompt)

        rvMsgs.adapter = MessageAdapter(this, messages)
        rvMsgs.layoutManager = LinearLayoutManager(this)

        guessField = findViewById(R.id.etGuessField)
        guessButton = findViewById(R.id.btGuessButton)
        var i = 3
        guessButton.setOnClickListener {
            val msg = guessField.text.toString()

            if(msg.isNotEmpty()){
                if(numOfGus<4){

                    if(msg.toInt() == answer){
                        guessButton.isEnabled = false
                        guessButton.isClickable = false
                        guessField.isEnabled = false
                        guessField.isClickable = false
                        var mm = "You win! Play again?"
                        showAlert(mm)
                    }else{
                        numOfGus++
                        messages.add("You guessed $msg")
                        i--
                        messages.add("You have $i guesses left")
                    }
                    if(numOfGus==3){

                        messages.add("You lose - The correct answer was $answer")
                        messages.add("Game Over")
                        var mm ="You lose The correct answer was $answer.you want to Play again?"
                        showAlert(mm)
                        guessButton.isEnabled = false
                        guessButton.isClickable = false
                        guessField.isEnabled = false
                        guessField.isClickable = false
                    }
                }
                guessField.text.clear()
                guessField.clearFocus()
                rvMsgs.adapter?.notifyDataSetChanged()
            }else{
                Snackbar.make(clRoot, "Please enter a number", Snackbar.LENGTH_LONG).show()
            } }

        title = "Numbers Game"
    }

    override fun recreate() {
        super.recreate()
        answer = Random.nextInt(10)
        numOfGus = 0
        messages.clear()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("answer", answer)
        outState.putInt("guesses", numOfGus)
        outState.putStringArrayList("messages", messages)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        answer = savedInstanceState.getInt("answer", 0)
        numOfGus = savedInstanceState.getInt("guesses", 0)
        messages.addAll(savedInstanceState.getStringArrayList("messages")!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_game, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item: MenuItem = menu!!.getItem(1)
        if(item.title == "Other Game"){ item.title = "Guess The Phrase" }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_new_game -> {
                showAlert("did you want to start new game??")
                return true
            }
            R.id.mi_other_game -> {
                changeScreen(GuessThePhrase())
                return true
            }
            R.id.mi_back -> {
                changeScreen(MainActivity())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeScreen(activity: Activity){
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
    }


    private fun showAlert(title: String) {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage(title)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    dialog, id -> this.recreate()
            })
            // negative button text and action
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Game Over")
        // show alert dialog
        alert.show()
    }
}
