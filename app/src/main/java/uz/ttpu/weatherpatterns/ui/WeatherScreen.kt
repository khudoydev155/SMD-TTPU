package uz.ttpu.weatherpatterns.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.ttpu.weatherpatterns.data.model.Weather

/**
 * Reusable Weather Screen UI component for all architectural design patterns.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDisplay(
    patternTitle: String,
    cityNameQuery: String,
    onCityNameChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    isLoading: Boolean,
    weather: Weather?,
    errorMessage: String?,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(patternTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBackClicked) {
                        Text("← Back", fontSize = 16.sp)
                    }
                },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = cityNameQuery,
                onValueChange = onCityNameChanged,
                label = { Text("Enter City Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Search Action Button
            Button(
                onClick = onSearchClicked,
                enabled = !isLoading && cityNameQuery.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fetch Weather")
            }

            // Loading State
            AnimatedVisibility(visible = isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            // Error State
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Success Weather Content State
            if (weather != null && !isLoading) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = weather.cityName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${weather.temperatureCelsius}°C",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Condition: ${weather.condition}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Humidity: ${weather.humidity}%")
                            Text(text = "Wind: ${weather.windSpeedKmH} km/h")
                        }
                    }
                }
            }
        }
    }
}
