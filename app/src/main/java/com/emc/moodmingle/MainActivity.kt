package com.emc.moodmingle

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.emc.moodmingle.navigation.BottomNavItem
import com.emc.moodmingle.navigation.Routes
import com.emc.moodmingle.navigation.bottomNavItems
import com.emc.moodmingle.ui.screens.CreatePostScreen
import com.emc.moodmingle.ui.screens.HomeScreen
import com.emc.moodmingle.ui.screens.InsightsScreen
import com.emc.moodmingle.ui.screens.LoginScreen
import com.emc.moodmingle.ui.screens.ProfileScreen
import com.emc.moodmingle.ui.screens.RegisterScreen
import com.emc.moodmingle.ui.screens.SearchScreen
import com.emc.moodmingle.ui.screens.SettingsScreen
import com.emc.moodmingle.ui.theme.MoodMingleTheme
import com.emc.moodmingle.ui.theme.PrimaryGradient
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMingleTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SplashScreenContent()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SplashScreenContent() {
    var startAnimation by remember { mutableStateOf(false) }
    var showSplash by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200), label = ""
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        showSplash = false
    }

    Crossfade(targetState = showSplash, animationSpec = tween(1000)) { isSplashVisible ->
        if (isSplashVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PurplePrimary, PurpleDark),
                            start = Offset(0f, offset),
                            end = Offset(offset, 0f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(230.dp)
                            .alpha(alphaAnim.value),
                        contentScale = ContentScale.Fit
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "MoodMingle",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.alpha(alphaAnim.value)
                        )

                        Text(
                            text = "Connect. Share. Inspire.",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(alphaAnim.value)
                        )
                    }
                }
            }
        } else {
            Surface(color = MaterialTheme.colorScheme.background) {
                AppNavigation()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut(animationSpec = tween(500))
        }
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                onCreateProfile = { username, password, selectedAvatar, bio ->
                    navController.navigate("home/$username&$password&$selectedAvatar&$bio") {
                        popUpTo(Routes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.Register.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onLoginClick = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Routes.Home.route,
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType },
                navArgument("selectedAvatar") { type = NavType.StringType },
                navArgument("bio") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name").toString()
            val password = backStackEntry.arguments?.getString("password").toString()
            val selectedAvatar = backStackEntry.arguments?.getString("selectedAvatar").toString()
            val bio = backStackEntry.arguments?.getString("bio").toString()

            BottomNavigationContainer(navController, name, password, selectedAvatar, bio)
        }

        composable(Routes.Search.route) {
            SearchScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Routes.CreatePost.route) {
            CreatePostScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Routes.Insights.route) {
            InsightsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun BottomNavigationContainer(
    mainNavController: NavHostController,
    name: String,
    password: String,
    avatar: String,
    bio: String
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Routes.BottomHome.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(500)) + slideInHorizontally(initialOffsetX = { 300 })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500)) + slideOutHorizontally(targetOffsetX = { -300 })
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(500)) + slideInHorizontally(initialOffsetX = { -300 })
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(500)) + slideOutHorizontally(targetOffsetX = { 300 })
            }
        ) {
            composable(Routes.BottomHome.route) {
                HomeScreen(
                    name = name,
                    password = password,
                    selectedAvatar = avatar,
                    bio = bio,
                    onCreateClick = { mainNavController.navigate(Routes.CreatePost.route) },
                    onSearchClick = { mainNavController.navigate(Routes.Search.route) }
                )
            }

            composable(Routes.BottomProfile.route) {
                ProfileScreen(
                    R.drawable.profile,
                    "Jhon Lee Marahay",
                    "Test bio",
                    "Joined 10/8/2025",
                    onCreateClick = { mainNavController.navigate(Routes.CreatePost.route) },
                    onInsightsClick = { mainNavController.navigate(Routes.Insights.route) },
                    onExploreClick = { bottomNavController.navigate(Routes.BottomHome.route) },
                    onLogoutClick = {
                        mainNavController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.BottomSettings.route) {
                SettingsScreen(
                    onBackClick = { bottomNavController.navigate(Routes.BottomHome.route) },
                    onClick = { label ->
                        val route = if (label == "Logout") "login" else label.lowercase()
                        mainNavController.navigate(route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = bottomNavItems

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val gradientBrush = Brush.linearGradient(PrimaryGradient)
    val unselectedBrush = Brush.linearGradient(listOf(Color.White, Color.White))

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = { NavIcon(item, isSelected, gradientBrush, unselectedBrush) },
                label = {
                    Text(
                        text = item.label,
                        modifier = Modifier
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = if (isSelected) gradientBrush else unselectedBrush,
                                        blendMode = BlendMode.SrcAtop
                                    )
                                }
                            }
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}

@Composable
fun NavIcon(
    item: BottomNavItem,
    isSelected: Boolean,
    gradientBrush: Brush,
    unselectedBrush: Brush
) {
    Icon(
        imageVector = item.icon,
        contentDescription = "Gradient Icon",
        modifier = Modifier
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = if (isSelected) unselectedBrush else gradientBrush,
                        blendMode = BlendMode.SrcAtop
                    )
                }
            },
        tint = Color.White
    )
}