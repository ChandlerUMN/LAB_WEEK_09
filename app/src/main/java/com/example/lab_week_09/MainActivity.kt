package com.example.lab_week_09

// Android / Compose
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Navigation
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

// Moshi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types

// Project-specific theme / UI elements
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton


data class Student(var name: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController)
                }
            }
        }
    }
}

@Composable
fun App(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(
                navigateFromHomeToResult = { list ->
                    // Convert list to JSON
                    val moshi = Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                    val type = Types.newParameterizedType(List::class.java, Student::class.java)
                    val jsonAdapter = moshi.adapter<List<Student>>(type)
                    val json = jsonAdapter.toJson(list)

                    navController.navigate("resultContent/$json")
                }
            )
        }
        composable(
            "resultContent/{listData}",
            arguments = listOf(navArgument("listData") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("listData").orEmpty()
            // Convert JSON back to list
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val type = Types.newParameterizedType(List::class.java, Student::class.java)
            val jsonAdapter = moshi.adapter<List<Student>>(type)
            val list = jsonAdapter.fromJson(json) ?: emptyList()
            ResultContent(list)
        }
    }
}

@Composable
fun Home(navigateFromHomeToResult: (List<Student>) -> Unit) {
    val listData = remember { mutableStateListOf(Student("Tanu"), Student("Tina"), Student("Tono")) }
    var inputText by remember { mutableStateOf("") }

    HomeContent(
        listData = listData,
        inputText = inputText,
        onInputValueChange = { inputText = it },
        onButtonClick = {
            // Prevent empty submission
            if (inputText.isNotBlank()) {
                listData.add(Student(inputText.trim()))
                inputText = ""
            }
        },
        onNavigate = { navigateFromHomeToResult(listData.toList()) }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputText: String,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    onNavigate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OnBackgroundTitleText("Masukkan Nama:")

        TextField(
            value = inputText,
            onValueChange = { onInputValueChange(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.padding(top = 8.dp)) {
            PrimaryTextButton(
                text = "Tambah",
                onClick = onButtonClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryTextButton(
                text = "Lihat Hasil",
                onClick = onNavigate
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(listData) { student ->
                OnBackgroundItemText(text = student.name)
            }
        }
    }
}

@Composable
fun ResultContent(listData: List<Student>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundTitleText("Hasil Daftar Nama:")

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listData) { student ->
                OnBackgroundItemText(student.name)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    Home { }
}
