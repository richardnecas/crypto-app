package com.example.crypto_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.crypto_app.ui.LandingScreen
import com.example.crypto_app.ui.ResultsScreen
import com.example.crypto_app.ui.theme.CryptoappTheme
import kotlinx.serialization.Serializable

@Serializable
object LandingRoute : NavKey

@Serializable
data class ResultRoute(val algorithmName: String) : NavKey

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoappTheme {
                val backStack = rememberNavBackStack(LandingRoute)
                val viewModel: CryptoViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
                val directive = remember(windowAdaptiveInfo) {
                    calculatePaneScaffoldDirective(windowAdaptiveInfo)
                        .copy(horizontalPartitionSpacerSize = 0.dp)
                }
                val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    sceneStrategies = listOf(listDetailStrategy),
                    entryProvider = entryProvider {
                        entry<LandingRoute>(
                            metadata = ListDetailSceneStrategy.listPane(
                                detailPlaceholder = {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Select an operation to view results")
                                    }
                                }
                            )
                        ) {
                            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                LandingScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onBbsPlusClick = {
                                        viewModel.performBbsPlus()
                                        backStack.add(ResultRoute("BBS+"))
                                    },
                                    onBbsSharpClick = {
                                        viewModel.performBbsSharp("Payload")
                                        backStack.add(ResultRoute("BBS#"))
                                    },
                                    onZkSnarkClick = {
                                        viewModel.performZkSnark("Payload")
                                        backStack.add(ResultRoute("zk-SNARKs"))
                                    },
                                    onZkStarkClick = {
                                        viewModel.performZkStark("Payload")
                                        backStack.add(ResultRoute("zk-STARKs"))
                                    }
                                )
                            }
                        }
                        entry<ResultRoute>(
                            metadata = ListDetailSceneStrategy.detailPane()
                        ) { route ->
                            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                ResultsScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    algorithmName = route.algorithmName,
                                    uiState = uiState
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
