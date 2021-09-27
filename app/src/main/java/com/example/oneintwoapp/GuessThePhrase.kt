package com.example.oneintwoapp

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_guess_the_phrase.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.clRoot

class GuessThePhrase : AppCompatActivity() {

    private lateinit var guesstext: EditText
    private lateinit var guessBtn: Button
    private lateinit var messages: ArrayList<String>
    private lateinit var tvPhrase: TextView
    private lateinit var tvLetters: TextView
    private lateinit var myHighScore: TextView
    private var phrase = "Hello There"
    private var guessChar = ""
    private var count = 0
    private var guessPhrase = true
    private var phraseChar = mutableMapOf<Int, Char>()
    private var youranswer = ""
    private var score = 0
    private var highScore = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess_the_phrase)
        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        highScore = sharedPreferences.getInt("HighScore", 0)

        myHighScore = findViewById(R.id.tvHS)
        myHighScore.text = "High Score: $highScore"


        for (ch in phrase.indices) {
            if (phrase[ch] == ' ') {
                phraseChar[ch] = ' '
                youranswer += ' '
            } else {
                phraseChar[ch] = '*'
                youranswer += '*'
            }
        }

        messages = ArrayList()

        rvMsgs.adapter = MessageAdapter(this, messages)// adap and msg that send
        rvMsgs.layoutManager = LinearLayoutManager(this) //xml

        guesstext = findViewById(R.id.etGuessField) // take user input
        guessBtn = findViewById(R.id.btGuessButton)

        guessBtn.setOnClickListener {
            val msg = guesstext.text.toString()
            if (guessPhrase) {
                if (msg == phrase) {
                    disableEntry()
                    showAlert("You win Do you want to play again:")
                } else {
                    messages.add("Wrong guess: $msg")
                    guessPhrase = false
                    tvPhrase.text = "Phrase:  " + youranswer
                    tvLetters.text = "your guess :  " + guessChar
                    if (guessPhrase) {
                        guesstext.hint = "Guess the full phrase"
                    } else {
                        guesstext.hint = "Guess a letter"
                    }
                }
            } else {
                if (msg.isNotEmpty() && msg.length == 1) {
                    youranswer = ""
                    guessPhrase = true
                    checkLetters(msg[0])
                } else {
                    Snackbar.make(clRoot, "enter a letter", Snackbar.LENGTH_LONG)
                        .show()
                }


            }
        }
        tvPhrase = findViewById(R.id.tvPrompt)
        tvLetters = findViewById(R.id.tvLetters)

        tvPhrase.text = "Phrase:  " + youranswer
        tvLetters.text = "Guessed Letters:  " + guessChar
        if (guessPhrase) {
            guesstext.hint = "Guess the full phrase"
        } else {
            guesstext.hint = "Guess a letter"
        }
        title = "Guess the Phrase"
    }
    override fun recreate() {
        super.recreate()
        phrase = "this is the secret phrase"
        phraseChar.clear()
        youranswer = ""

        for(i in phrase.indices){
            if(phrase[i] == ' '){
                phraseChar[i] = ' '
                youranswer += ' '
            }else{
                phraseChar[i] = '*'
                youranswer += '*'
            }
        }

        guessChar = ""
        count = 0
        guessPhrase = true
        messages.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("phrase", phrase)

        val keys = phraseChar.keys.toIntArray()
        val values = phraseChar.values.toCharArray()
        outState.putIntArray("keys", keys)
        outState.putCharArray("values", values)

        outState.putString("youranswer", youranswer)
        outState.putString("guessChar", guessChar)
        outState.putInt("count", count)
        outState.putBoolean("guessPhrase", guessPhrase)
        outState.putStringArrayList("messages", messages)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        phrase = savedInstanceState.getString("answer", "nothing here")

        val keys = savedInstanceState.getIntArray("keys")
        val values = savedInstanceState.getCharArray("values")
        if(keys != null && values != null){
            if(keys.size == values.size){
                phraseChar = mutableMapOf<Int, Char>().apply {
                    for (i in keys.indices) this [keys[i]] = values[i]
                }
            }
        }

        youranswer = savedInstanceState.getString("youranswer", "")
        guessChar = savedInstanceState.getString("guessChar", "")
        count = savedInstanceState.getInt("count", 0)
        guessPhrase = savedInstanceState.getBoolean("guessPhrase", true)
        messages.addAll(savedInstanceState.getStringArrayList("messages")!!)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_game, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item: MenuItem = menu!!.getItem(1)
        if(item.title == "Other Game"){ item.title = "Numbers Game" }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_new_game -> {
                showAlert("did you want to start new game?")
                return true
            }
            R.id.mi_other_game -> {
                changeScreen(NumbersGame())
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

    private fun disableEntry() {
        guessBtn.isEnabled = false
        guessBtn.isClickable = false
        guesstext.isEnabled = false
        guesstext.isClickable = false
    }

    private fun showAlert(title: String) {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage(title)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                this.recreate()
            })
            // negative button text and action
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Game Over")
        // show alert dialog
        alert.show()
    }

    private fun checkLetters(guessedLetter: Char){
        var found = 0
        for(i in phrase.indices){
            if(phrase[i] == guessedLetter){
                phraseChar[i] = guessedLetter
                found++
            }
        }
        for(i in phraseChar){youranswer += phraseChar[i.key]}
        if(youranswer==phrase){
            disableEntry()
            updateScore()
            showAlert("You win Do you want to play again:")
        }
        if(guessChar.isEmpty()){guessChar+=guessedLetter}else{guessChar+=", "+guessedLetter}
        if(found>0){
            messages.add("Found $found ${guessedLetter.toUpperCase()}(s)")
        }else{
            messages.add("No ${guessedLetter.toUpperCase()}s found")
        }
        count++
        val guessesLeft = 10 - count
        if(count<10){messages.add("$guessesLeft guesses remaining")}
        tvPhrase.text = "Phrase:  " + youranswer.toUpperCase()
        tvLetters.text = "Guessed Letters:  " + guessChar
        if(guessPhrase){
            guesstext.hint = "Guess the full phrase"
        }else{
            guesstext.hint = "Guess a letter"
        }
        rvMsgs.scrollToPosition(messages.size - 1)
    }
    private fun updateScore(){
        score = 10 - count
        if(score >= highScore){
            highScore = score
            with(sharedPreferences.edit()) {
                putInt("HighScore", highScore)
                apply()
            }
            Snackbar.make(clRoot, "NEW HIGH SCORE!", Snackbar.LENGTH_LONG).show()
        }
    }

}
