package com.github.sewerina.fivelettersgame

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sewerina.fivelettersgame.GameState.Active
import com.github.sewerina.fivelettersgame.api.ApiRepository
import kotlinx.coroutines.launch
import java.lang.Integer.min

class MainViewModel(private val apiRepo: ApiRepository) : ViewModel() {
    val state: MutableState<GameState> = mutableStateOf(Active(""))

    fun send(event: Event) {
        when (event) {
            is Event.Key -> {
                input(event.value)
            }

            is Event.Check -> {
                check()
            }

            is Event.NewGame -> {
                newGame()
            }
        }
    }

    private fun newGame() {
        viewModelScope.launch {
            val word = apiRepo.getWord().word
            state.value = Active(word.uppercase())
        }
    }

    private fun check() {
        val s = state.value as Active
        val wrongSet = s.wrongSet
        val partSet = s.partSet
        val fullSet = s.fullSet
        val field = s.field.copy(words = s.field.words.copyOf())
        val word =
            field.words[s.curWordIndex].copy(letters = field.words[s.curWordIndex].letters.copyOf())

        // Цикл для проверки соответствия букв в введенном слове (letter = word.letters[i]) и букв в загаданном слове (s.word)
        for (i in 0..4) {
            val letter = word.letters[i]
            var letterState: LetterState
            if (s.word.contains(letter.value)) {
                if (s.word[i].toString() == letter.value) {
                    letterState = LetterState.FULL
                    fullSet.add(letter.value)
                } else {
                    letterState = LetterState.PART
                    partSet.add(letter.value)
                }
            } else {
                letterState = LetterState.WRONG
                wrongSet.add(letter.value)
            }
            word.letters[i] = letter.copy(state = letterState)
        }
        field.words[s.curWordIndex] = word

        if (word.letters.all { it.state == LetterState.FULL }) {
            state.value = GameState.Finished(isWin = true)
            return
        }

        if (s.curWordIndex < 5) {
            state.value = s.copy(field = field).also {
                it.curWordIndex = s.curWordIndex + 1
                it.curLetterIndex = 0
                it.wrongSet = wrongSet
                it.partSet = partSet
                it.fullSet = fullSet
            }
            return
        }

        state.value = GameState.Finished(isWin = false)
    }

    private fun input(key: String) {
        val s = state.value as Active

        val field = s.field.copy(words = s.field.words.copyOf())
        val word =
            field.words[s.curWordIndex].copy(letters = field.words[s.curWordIndex].letters.copyOf())

        var curLetterIndex = s.curLetterIndex
        if (key == "<--") {
            curLetterIndex = word.letters.indexOfLast { it.state == LetterState.FILLED }
            val letter = word.letters[curLetterIndex]
            word.letters[curLetterIndex] = letter.copy(value = "", state = LetterState.EMPTY)
        } else {
            val letter = word.letters[curLetterIndex]
            word.letters[curLetterIndex] = letter.copy(value = key, state = LetterState.FILLED)
            curLetterIndex = min((curLetterIndex + 1), 4)
        }

        field.words[s.curWordIndex] = word
        state.value = s.copy(field = field).also {
            it.curWordIndex = s.curWordIndex
            it.curLetterIndex = curLetterIndex

            if (it.field.words[it.curWordIndex].letters[4].state == LetterState.FILLED) {
                it.stateButton = true
            }

            it.wrongSet = s.wrongSet
            it.partSet = s.partSet
            it.fullSet = s.fullSet
        }
    }
}

sealed interface Event {
    data class Key(val value: String) : Event
    object Check : Event
    object NewGame : Event
}

sealed interface GameState {
    data class Active(val word: String, val field: GameField = GameField()) : GameState {
        var curWordIndex: Int = 0
        var curLetterIndex: Int = 0
        var stateButton = false
        var wrongSet = mutableSetOf<String>()
        var partSet = mutableSetOf<String>()
        var fullSet = mutableSetOf<String>()
    }

    data class Finished(val isWin: Boolean) : GameState
}

enum class LetterState {
    EMPTY, FILLED, WRONG, PART, FULL
}

data class Letter(val value: String = "", val state: LetterState = LetterState.EMPTY)
data class Word(val letters: Array<Letter> = Array(5) { Letter() }) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Word

        if (!letters.contentEquals(other.letters)) return false

        return true
    }

    override fun hashCode(): Int {
        return letters.contentHashCode()
    }
}

data class GameField(val words: Array<Word> = Array(6) { Word() }) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameField

        if (!words.contentEquals(other.words)) return false

        return true
    }

    override fun hashCode(): Int {
        return words.contentHashCode()
    }
}