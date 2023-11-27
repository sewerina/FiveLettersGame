package com.github.sewerina.fivelettersgame


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
            .background(color = Color.Green)
            .padding(horizontal = 8.dp, vertical = 16.dp)

    ) {
        Words()

        Spacer(modifier = Modifier.weight(0.2f))

        Alphabet(action)

        CheckWordButton(action)
    }
}

@Composable
fun Words() {
    Column(
        Modifier.background(color = Color.Yellow)
    ) {
        WordRow()
        WordRow()
        WordRow()
        WordRow()
        WordRow()
    }
}

@Composable
fun WordRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Red),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LetterCell()
        LetterCell()
        LetterCell()
        LetterCell()
        LetterCell()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterCell() {
    TextField(
        modifier = Modifier.size(64.dp),
        textStyle = TextStyle(
            textDecoration = TextDecoration.None,
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        ),
        singleLine = true,
        maxLines = 1,
        value = "X",
        onValueChange = {})
}

val lettersTop = arrayOf("Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш", "Щ", "З", "Х", "Ъ")
val lettersMiddle = arrayOf("Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д", "Ж", "Э")
val lettersBottom = arrayOf("Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "<--")

@Composable
fun Alphabet(action: (Event) -> Unit) {
    Column() {
        Row(modifier = Modifier.fillMaxWidth()) {
            lettersTop.forEach { letter ->
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(24.dp, 32.dp)
                        .heightIn(40.dp, 40.dp)
                        .padding(2.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RectangleShape,
                    onClick = { action(Event.Key(letter)) }) {
                    Text(text = letter, maxLines = 1, textAlign = TextAlign.Center)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            lettersMiddle.forEach { letter ->
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(24.dp, 32.dp)
                        .heightIn(40.dp, 40.dp)
                        .padding(2.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RectangleShape,
                    onClick = { action(Event.Key(letter)) }) {
                    Text(text = letter, maxLines = 1, textAlign = TextAlign.Center)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            lettersBottom.forEach { letter ->
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(24.dp, 32.dp)
                        .heightIn(40.dp, 40.dp)
                        .padding(2.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RectangleShape,
                    onClick = { action(Event.Key(letter)) }
                ) {
                    Text(text = letter, maxLines = 1, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun CheckWordButton(action: (Event) -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { action(Event.Check) },
        shape = RectangleShape
    ) {
        Text(text = stringResource(R.string.btn_check_word).uppercase())
    }
}