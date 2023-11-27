package com.github.sewerina.fivelettersgame

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.sewerina.fivelettersgame.GameState.Active
import com.github.sewerina.fivelettersgame.GameState.Finished

class MainViewModel : ViewModel() {
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
        state.value = Active("")
    }

    private fun check() {
        state.value = Finished(isWin = true)
    }

    private fun input(value: String) {
        TODO("Not yet implemented")
    }


}

sealed interface Event {
    data class Key(val value: String) : Event
    object Check : Event
    object NewGame : Event
}

sealed interface GameState {
    data class Active(val currentWord: String) : GameState
    data class Finished(val isWin: Boolean) : GameState

}