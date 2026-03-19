package com.erman.pegsolitaire

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.erman.pegsolitaire.engine.BoardType
import com.erman.pegsolitaire.presentation.ChallengeLevelSelectorViewModel
import com.erman.pegsolitaire.presentation.GameViewModel
import com.erman.pegsolitaire.presentation.HomeViewModel
import com.erman.pegsolitaire.ui.screen.ChallengeLevelSelectorScreen
import com.erman.pegsolitaire.ui.screen.GameScreen
import com.erman.pegsolitaire.ui.screen.MenuScreen
import com.erman.pegsolitaire.ui.theme.PegSolitaireTheme
import org.koin.mp.KoinPlatform

private sealed class Screen {
    data object Menu : Screen()
    data object ChallengeLevelSelector : Screen()
    data class ClassicGame(val boardType: BoardType) : Screen()
    data class ChallengeGame(val levelNumber: Int) : Screen()
}

@Composable
fun App() {
    PegSolitaireTheme {
        var currentScreen: Screen by remember { mutableStateOf(Screen.Menu) }

        when (val screen = currentScreen) {
            is Screen.Menu -> MenuScreenRoute(
                onClassicSelected = { currentScreen = Screen.ClassicGame(it) },
                onChallengeSelected = { currentScreen = Screen.ChallengeLevelSelector }
            )
            is Screen.ChallengeLevelSelector -> ChallengeLevelSelectorRoute(
                onLevelSelected = { currentScreen = Screen.ChallengeGame(it) },
                onBack = { currentScreen = Screen.Menu }
            )
            is Screen.ClassicGame -> ClassicGameRoute(
                boardType = screen.boardType,
                onQuit = { currentScreen = Screen.Menu }
            )
            is Screen.ChallengeGame -> ChallengeGameRoute(
                levelNumber = screen.levelNumber,
                onQuit = { currentScreen = Screen.Menu }
            )
        }
    }
}

@Composable
private fun MenuScreenRoute(
    onClassicSelected: (BoardType) -> Unit,
    onChallengeSelected: () -> Unit
) {
    val homeViewModel = remember { KoinPlatform.getKoin().get<HomeViewModel>() }
    MenuScreen(
        homeViewModel = homeViewModel,
        onClassicSelected = onClassicSelected,
        onChallengeSelected = onChallengeSelected
    )
}

@Composable
private fun ChallengeLevelSelectorRoute(
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = remember { KoinPlatform.getKoin().get<ChallengeLevelSelectorViewModel>() }
    ChallengeLevelSelectorScreen(
        viewModel = viewModel,
        onLevelSelected = onLevelSelected,
        onBack = onBack
    )
}

@Composable
private fun ClassicGameRoute(boardType: BoardType, onQuit: () -> Unit) {
    val gameViewModel = remember(boardType) {
        KoinPlatform.getKoin().get<GameViewModel>().also {
            it.startClassicGame(boardType)
        }
    }
    GameScreen(gameViewModel = gameViewModel, onQuit = onQuit)
}

@Composable
private fun ChallengeGameRoute(levelNumber: Int, onQuit: () -> Unit) {
    val gameViewModel = remember(levelNumber) {
        KoinPlatform.getKoin().get<GameViewModel>().also {
            it.startChallengeLevel(levelNumber)
        }
    }
    GameScreen(gameViewModel = gameViewModel, onQuit = onQuit)
}
