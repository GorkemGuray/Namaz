package co.gorkem.namaz

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import co.gorkem.namaz.ui.navigation.NavGraph
import co.gorkem.namaz.ui.theme.NamazTheme

@Composable
fun NamazApp() {
    NamazTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            NavGraph(navController = navController)
        }
    }
}
