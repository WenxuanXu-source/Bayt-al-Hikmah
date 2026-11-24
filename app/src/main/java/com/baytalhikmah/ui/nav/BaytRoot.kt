package com.baytalhikmah.ui.nav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.home.HomeScreen
import com.baytalhikmah.ui.learn.LearnScreen
import com.baytalhikmah.ui.profile.ProfileScreen
import com.baytalhikmah.ui.reference.ReferenceScreen
import com.baytalhikmah.ui.saved.SavedWordsScreen
import com.baytalhikmah.ui.settings.AboutScreen
import com.baytalhikmah.ui.settings.DictionaryInfoScreen
import com.baytalhikmah.ui.settings.SettingsScreen
import com.baytalhikmah.ui.settings.TutorialScreen
import com.baytalhikmah.ui.test.TestSessionScreen
import com.baytalhikmah.ui.test.TestTabScreen
import com.baytalhikmah.ui.word.WordDetailScreen

object Routes {
    const val HOME = "home"
    const val LEARN = "learn?date={date}"
    const val TEST = "test"
    const val REFERENCE = "reference"
    const val PROFILE = "profile"
    const val SESSION = "session/{sessionId}"
    const val WORD = "word/{vocabularyId}"
    const val SAVED = "saved"
    const val SETTINGS = "settings"
    const val DICTIONARY_INFO = "dictionary_info"
    const val ABOUT = "about"
    const val TUTORIAL = "tutorial"
}

@Composable
fun BaytRoot(container: AppContainer) {
    val navController = rememberNavController()
    var checkedLaunchGuide by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!checkedLaunchGuide) {
            checkedLaunchGuide = true
            if (container.settingsRepository.current().showTutorialOnLaunch) {
                navController.navigate(Routes.TUTORIAL)
            }
        }
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in setOf(
        Routes.HOME, Routes.LEARN, Routes.TEST, Routes.REFERENCE, Routes.PROFILE
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BaytBottomBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    container = container,
                    onOpenLearn = { date ->
                        navController.navigate(if (date == null) "learn" else "learn?date=$date")
                    },
                    onOpenSession = { id -> navController.navigate("session/$id") },
                    onOpenTestTab = { navController.navigateMainTab(Routes.TEST) },
                    onOpenSaved = { navController.navigate(Routes.SAVED) }
                )
            }
            composable(
                route = Routes.LEARN,
                arguments = listOf(
                    navArgument("date") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entry ->
                LearnScreen(
                    container = container,
                    date = entry.arguments?.getString("date"),
                    onOpenSession = { id -> navController.navigate("session/$id") },
                    onBack = {
                        if (!navController.popBackStack()) {
                            navController.navigateMainTab(Routes.HOME)
                        }
                    }
                )
            }
            composable(Routes.TEST) {
                TestTabScreen(
                    container = container,
                    onOpenSession = { id -> navController.navigate("session/$id") }
                )
            }
            composable(Routes.REFERENCE) {
                ReferenceScreen(
                    container = container,
                    onOpenWord = { id -> navController.navigate("word/$id") }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    container = container,
                    onOpenSaved = { navController.navigate(Routes.SAVED) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenDictionaryInfo = { navController.navigate(Routes.DICTIONARY_INFO) },
                    onOpenAbout = { navController.navigate(Routes.ABOUT) },
                    onOpenTutorial = { navController.navigate(Routes.TUTORIAL) }
                )
            }
            composable(Routes.SESSION) { entry ->
                TestSessionScreen(
                    container = container,
                    sessionId = entry.arguments?.getString("sessionId").orEmpty(),
                    onClose = { navController.popBackStack() },
                    onOpenSession = { id ->
                        navController.navigate("session/$id") {
                            popUpTo(entry.destination.id) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.WORD) { entry ->
                WordDetailScreen(
                    container = container,
                    vocabularyId = entry.arguments?.getString("vocabularyId").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SAVED) {
                SavedWordsScreen(
                    container = container,
                    onBack = { navController.popBackStack() },
                    onOpenWord = { id -> navController.navigate("word/$id") },
                    onOpenSession = { id -> navController.navigate("session/$id") }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    container = container,
                    onBack = { navController.popBackStack() },
                    onOpenDictionaryInfo = { navController.navigate(Routes.DICTIONARY_INFO) }
                )
            }
            composable(Routes.DICTIONARY_INFO) {
                DictionaryInfoScreen(
                    container = container,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.TUTORIAL) {
                TutorialScreen(
                    container = container,
                    fromSettings = true,
                    onBack = {
                        if (!navController.popBackStack()) {
                            navController.navigateMainTab(Routes.HOME)
                        }
                    }
                )
            }
        }
    }
}

private fun NavHostController.navigateMainTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val matches: (String?) -> Boolean
)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Home", Icons.Outlined.Home) { it == Routes.HOME },
    BottomItem("learn", "Learn", Icons.Outlined.AutoStories) { it?.startsWith("learn") == true },
    BottomItem(Routes.TEST, "Test", Icons.Outlined.FactCheck) { it == Routes.TEST },
    BottomItem(Routes.REFERENCE, "Reference", Icons.Outlined.LibraryBooks) { it == Routes.REFERENCE },
    BottomItem(Routes.PROFILE, "Profile", Icons.Outlined.Person) { it == Routes.PROFILE }
)

@Composable
private fun BaytBottomBar(navController: NavHostController, currentRoute: String?) {
    Column {
        Hairline()
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            bottomItems.forEach { item ->
                val selected = item.matches(currentRoute)
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) navController.navigateMainTab(item.route)
                    },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
