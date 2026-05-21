package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.JobRepository
import com.example.ui.JobViewModel
import com.example.ui.RozgarApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    // Initialize Room Database, DAO and Repository lazily or in onCreate
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "rozgar_mela_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private val repository by lazy {
        JobRepository(database.jobDao)
    }

    // Standard ViewModel with Factory Delegation
    private val viewModel: JobViewModel by viewModels {
        JobViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RozgarApp(viewModel = viewModel)
                }
            }
        }
    }
}
