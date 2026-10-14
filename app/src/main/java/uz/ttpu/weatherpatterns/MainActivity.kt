package uz.ttpu.weatherpatterns

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.ttpu.weatherpatterns.di.WeatherModule
import uz.ttpu.weatherpatterns.mvc.MvcScreen
import uz.ttpu.weatherpatterns.mvp.MvpScreen
import uz.ttpu.weatherpatterns.mvi.MviScreen
import uz.ttpu.weatherpatterns.mvi.WeatherMviViewModel
import uz.ttpu.weatherpatterns.mvvm.MvvmScreen
import uz.ttpu.weatherpatterns.mvvm.WeatherViewModel
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

enum class ArchitecturalPattern {
    HOME,
    MVC,
    MVP,
    MVVM,
    MVI
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherPatternsTheme {
                var currentPattern by remember { mutableStateOf(ArchitecturalPattern.HOME) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentPattern) {
                        ArchitecturalPattern.HOME -> LauncherHomeScreen(
                            onSelectPattern = { pattern -> currentPattern = pattern }
                        )
                        ArchitecturalPattern.MVC -> MvcScreen(
                            onBackClicked = { currentPattern = ArchitecturalPattern.HOME }
                        )
                        ArchitecturalPattern.MVP -> MvpScreen(
                            onBackClicked = { currentPattern = ArchitecturalPattern.HOME }
                        )
                        ArchitecturalPattern.MVVM -> {
                            val mvvmViewModel: WeatherViewModel = remember {
                                WeatherModule.provideWeatherViewModelFactory().create(WeatherViewModel::class.java)
                            }
                            MvvmScreen(
                                viewModel = mvvmViewModel,
                                onBackClicked = { currentPattern = ArchitecturalPattern.HOME }
                            )
                        }
                        ArchitecturalPattern.MVI -> {
                            val mviViewModel: WeatherMviViewModel = remember {
                                WeatherMviViewModel(WeatherModule.weatherRepository)
                            }
                            MviScreen(
                                viewModel = mviViewModel,
                                onBackClicked = { currentPattern = ArchitecturalPattern.HOME }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherHomeScreen(
    onSelectPattern: (ArchitecturalPattern) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Architectural Patterns", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Select an Architecture Pattern",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = { onSelectPattern(ArchitecturalPattern.MVC) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text("1. MVC Pattern", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = { onSelectPattern(ArchitecturalPattern.MVP) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text("2. MVP Pattern", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = { onSelectPattern(ArchitecturalPattern.MVVM) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text("3. MVVM Pattern", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = { onSelectPattern(ArchitecturalPattern.MVI) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text("4. MVI Pattern", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
