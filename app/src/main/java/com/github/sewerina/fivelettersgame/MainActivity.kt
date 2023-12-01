package com.github.sewerina.fivelettersgame


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.sewerina.fivelettersgame.LetterState.*
import com.github.sewerina.fivelettersgame.ui.theme.FiveLettersGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FiveLettersGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val vm: MainViewModel = viewModel()
                    when (val gameState = vm.state.value) {
                        is GameState.Active -> {
                            GameScreen(gameState, vm::send)
                        }

                        is GameState.Finished -> {
                            FinishedScreen(gameState, vm::send)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinishedScreen(gameState: GameState.Finished, action: (Event) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(if (gameState.isWin) R.string.text_won else R.string.text_lose),
            fontSize = 20.sp
        )

        Button(onClick = { action(Event.NewGame) }) {
            Text(text = stringResource(R.string.btn_continue))
        }
    }
}

@Composable
fun GameScreen(gameState: GameState.Active, action: (Event) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)

    ) {
        Words(gameState.field)

        Spacer(modifier = Modifier.weight(0.2f))

        Alphabet(gameState, action)

        CheckWordButton(gameState.stateButton, action)
    }
}

@Composable
fun Words(gameField: GameField) {
    Column(
        Modifier.background(color = Color.Yellow)
    ) {
        gameField.words.forEach { word ->
            WordRow(word)
        }
    }
}

@Composable
fun WordRow(word: Word) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Red),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        word.letters.forEach { letter ->
            LetterCell(letter)
        }
    }
}

@Composable
fun LetterCell(letter: Letter) {
    var colorCell: Color =
    when(letter.state) {
        EMPTY -> {
            Color.White
        }
        FILLED -> {
            Color.White
        }
        WRONG -> {
            Color.LightGray
        }
        PART -> {
            Color.Yellow
        }
        FULL -> {
            Color.Green
        }
    }

    Box(
        modifier = Modifier
            .size(72.dp)
            .background(color = colorCell)
            .border(width = 1.dp, color = Color.Blue)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            maxLines = 1,
            text = letter.value.uppercase(),
            style = TextStyle(fontSize = 24.sp, color = Color.DarkGray)
        )
    }
}

val keysTop = arrayOf("Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш", "Щ", "З", "Х", "Ъ")
val keysMiddle = arrayOf("Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д", "Ж", "Э")
val keysBottom = arrayOf("Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "<--")
val keyRows = arrayOf(keysTop, keysMiddle, keysBottom)

@Composable
fun Alphabet(gameState: GameState.Active, action: (Event) -> Unit) {
    Column() {
        keyRows.forEach { keys ->
            Row(modifier = Modifier.fillMaxWidth()) {
                keys.forEach { key ->
                    var stateButton = true
                    var backColor = MaterialTheme.colorScheme.primary
                    if (key in gameState.wrongSet) {
                        backColor = Color.LightGray
                        stateButton = false
                    }
                    if (key in gameState.partSet) {
                        backColor = Color.Yellow
                    }
                    if (key in gameState.fullSet) {
                        backColor = Color.Green
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(24.dp, 32.dp)
                            .heightIn(40.dp, 40.dp)
                            .padding(2.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RectangleShape,
                        onClick = { action(Event.Key(key)) },
                        enabled = stateButton,
                        colors = ButtonDefaults.buttonColors(containerColor = backColor)
                    ) {
                        Text(text = key, maxLines = 1, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckWordButton(stateButton: Boolean, action: (Event) -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        onClick ={ action(Event.Check) },
        enabled = stateButton,
        shape = RectangleShape
    ) {
        Text(text = stringResource(R.string.btn_check_word).uppercase())
    }
}