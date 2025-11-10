package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home()
                }
            }
        }
    }
}

data class Student(
    var name: String
)

@Composable
fun Home() {
    // Mutable list of students
    val listData = remember { mutableStateListOf(
        Student("Tanu"),
        Student("Tina"),
        Student("Tono")
    ) }

    // Input field state
    var inputText by remember { mutableStateOf("") }

    HomeContent(
        listData = listData,
        inputText = inputText,
        onInputValueChange = { inputText = it },
        onButtonClick = {
            if (inputText.isNotBlank()) {
                listData.add(Student(inputText))
                inputText = ""
            }
        }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputText: String,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Input section
        OnBackgroundTitleText(text = "Masukkan Nama:")

        TextField(
            value = inputText,
            onValueChange = { onInputValueChange(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        PrimaryTextButton(
            text = "Tambah",
            onClick = { onButtonClick() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // List section
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(listData) { student ->
                OnBackgroundItemText(text = student.name)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    Home()
}
