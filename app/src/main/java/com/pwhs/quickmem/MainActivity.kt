package com.pwhs.quickmem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pwhs.quickmem.presentation.StandardScaffold
import com.pwhs.quickmem.ui.theme.QuickMemTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var supabaseClient: SupabaseClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickMemTheme {
                val navController = rememberNavController()
                val navHostEngine = rememberNavHostEngine()

                val newBackStackEntry by navController.currentBackStackEntryAsState()
                val route = newBackStackEntry?.destination?.route
                StandardScaffold(
                    navController = navController,
                    showBottomBar = false
                ) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        navController = navController,
                        engine = navHostEngine,
                    )
                }
            }
        }
    }
}

