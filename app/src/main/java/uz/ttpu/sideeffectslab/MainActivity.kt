package uz.ttpu.sideeffectslab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ttpu.sideeffectslab.ui.theme.SideEffectsLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SideEffectsLabTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "1. Splash",
        "2. Snackbar",
        "3. Lifecycle",
        "4. SideEffect",
        "5. Scroll",
        "6. ProduceState"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SideEffectsLab — 9-Amaliyot") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> Task1SplashScreen()
                    1 -> Task2SnackbarScreen()
                    2 -> Task3LifecycleLoggerScreen()
                    3 -> Task4SideEffectScreen()
                    4 -> Task5ScrollScreen()
                    5 -> Task6ProduceStateScreen()
                }
            }
        }
    }
}
