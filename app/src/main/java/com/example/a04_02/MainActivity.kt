package com.example.a04_02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a04_02.ui.theme._04_02Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CounterViewModel: ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count

    private val _autoMode = MutableStateFlow(false)
    val autoMode: StateFlow<Boolean> = _autoMode

    private val _interval = MutableStateFlow(3000L)
    val interval: StateFlow<Long> = _interval

    // basic increment/decrement
    fun increment() { _count.value += 1}
    fun decrement() { _count.value -= 1}
    fun reset() { _count.value = 0}
    fun toggleAuto() { _autoMode.value = !_autoMode.value}


    //interval for auto mode
    fun setInt(newInt: Long) {
        if (newInt > 0) {
            _interval.value = newInt
        }
    }

    init {
        viewModelScope.launch {
            while (true) {
                delay(_interval.value)
                if (_autoMode.value) increment()
            }
        }
    }
}
class MainActivity : ComponentActivity() {
    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _04_02Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CounterScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CounterScreen(viewModel: CounterViewModel, modifier: Modifier = Modifier) {
    val count by viewModel.count.collectAsState()
    val autoMode by viewModel.autoMode.collectAsState()
    val currentInterval by viewModel.interval.collectAsState()


    var intervalInput by remember { mutableStateOf((currentInterval).toString()) }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        // Text at the Beginning
        Text("Count: $count", style = MaterialTheme.typography.headlineMedium)
        Text("Auto mode: ${if(autoMode) "ON" else "OFF"}", style = MaterialTheme.typography.bodyLarge)
        Text("Interval: $currentInterval ms", style = MaterialTheme.typography.bodyMedium)

        // Buttons
        Column(
            modifier = Modifier.padding(vertical = 24.dp), // Add vertical padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp) // Space between rows
        ) {
            // Inc + dec
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { viewModel.increment() }) { Text("+") }
                Button(onClick = { viewModel.decrement() }) { Text("-") }
            }
            // restart and auto count
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { viewModel.reset() }) { Text("Reset") }
                //Launch a coroutine that increments the counter every 3 seconds when “Auto” mode is toggled on.
                Button(onClick = { viewModel.toggleAuto() }) { Text("Auto") }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // settings screen to configure the auto-increment interval.
        Text("Set Interval (ms)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = intervalInput,
            onValueChange = { intervalInput = it },
            label = { Text("Interval in milliseconds") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = {
                intervalInput.toLongOrNull()?.let {
                    viewModel.setInt(it)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Apply Interval")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _04_02Theme {
        CounterScreen(viewModel = CounterViewModel())
    }
}
