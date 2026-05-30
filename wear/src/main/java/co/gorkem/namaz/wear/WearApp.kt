package co.gorkem.namaz.wear

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.AppScaffold
import co.gorkem.namaz.wear.ui.WearViewModel
import co.gorkem.namaz.wear.ui.screens.WearHomeScreen
import co.gorkem.namaz.wear.ui.screens.WearQiblaScreen
import co.gorkem.namaz.wear.ui.theme.WearTheme
import kotlinx.coroutines.launch

@Composable
fun WearApp(
    viewModel: WearViewModel = hiltViewModel()
) {
    val themeId by viewModel.themeId.collectAsState()
    val pagerState = rememberPagerState(initialPage = 1) { 3 }
    val coroutineScope = rememberCoroutineScope()
    
    WearTheme(themeId = themeId) {
        AppScaffold {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> co.gorkem.namaz.wear.ui.screens.WearZikirmatikScreen(
                        onNavigateBack = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                    1 -> WearHomeScreen(
                        viewModel = viewModel,
                        onNavigateToQibla = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        },
                        onNavigateToZikirmatik = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    )
                    2 -> WearQiblaScreen(
                        onNavigateBack = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                }
            }
        }
    }
}
