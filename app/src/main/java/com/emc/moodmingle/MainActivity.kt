package com.emc.moodmingle

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.emc.moodmingle.cloudinary.CloudinaryManager
import com.emc.moodmingle.data.model.UserEntity
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.navigation.BottomNavItem
import com.emc.moodmingle.navigation.Routes
import com.emc.moodmingle.navigation.bottomNavItems
import com.emc.moodmingle.ui.screens.ChatScreen
import com.emc.moodmingle.ui.screens.ConversationScreen
import com.emc.moodmingle.ui.screens.CreatePostScreen
import com.emc.moodmingle.ui.screens.CreateScreen
import com.emc.moodmingle.ui.screens.DailyMoodScreen
import com.emc.moodmingle.ui.screens.DecryptionScreen
import com.emc.moodmingle.ui.screens.EncryptionScreen
import com.emc.moodmingle.ui.screens.FavoritesScreen
import com.emc.moodmingle.ui.screens.HomeScreen
import com.emc.moodmingle.ui.screens.InsightsScreen
import com.emc.moodmingle.ui.screens.LoginScreen
import com.emc.moodmingle.ui.screens.NotificationScreen
import com.emc.moodmingle.ui.screens.PrivacyScreen
import com.emc.moodmingle.ui.screens.ProfileScreen
import com.emc.moodmingle.ui.screens.RegisterScreen
import com.emc.moodmingle.ui.screens.RemixScreen
import com.emc.moodmingle.ui.screens.SavedScreen
import com.emc.moodmingle.ui.screens.SearchMusicScreen
import com.emc.moodmingle.ui.screens.SearchResultsScreen
import com.emc.moodmingle.ui.screens.SearchScreen
import com.emc.moodmingle.ui.screens.SecurityScreen
import com.emc.moodmingle.ui.screens.SettingsScreen
import com.emc.moodmingle.ui.screens.VideoFeedScreen
import com.emc.moodmingle.ui.settings.password.ChangePasswordScreen
import com.emc.moodmingle.ui.settings.password.PasswordScreen
import com.emc.moodmingle.ui.settings.password.VerifyPasswordScreen
import com.emc.moodmingle.ui.settings.password.recover.ForgotPasswordScreen
import com.emc.moodmingle.ui.settings.password.recover.ResetPasswordScreen
import com.emc.moodmingle.ui.settings.password.recover.VerifyCodeScreen
import com.emc.moodmingle.ui.settings.personal.PersonalScreen
import com.emc.moodmingle.ui.theme.MoodMingleTheme
import com.emc.moodmingle.ui.theme.PrimaryGradient
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.SearchViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.notification.NotificationViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()

        CloudinaryManager.init(this)
        FirebaseApp.initializeApp(this)

        setContent {
            MoodMingleTheme {
                Surface(color = Color.Black) {
//                    val networkUtils = NetworkUtils(this)
//                    CheckInternetConnection(networkUtils) {
                    SplashScreenContent()
//                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val channel = NotificationChannel(
            "chat_channel",
            "Chat Messages",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onStop() {
        super.onStop()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SplashScreenContent() {
    val mainNavController = rememberNavController()

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
                            .size(100.dp)
                            .scale(2f)
                            .alpha(alphaAnim.value)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "MoodMingle",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
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
                AppNavigation(mainNavController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavigation(mainNavController: NavHostController) {
    val context = LocalContext.current
    val searchViewModel = hiltViewModel<SearchViewModelFirebase>()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    val userDao = remember { AppDatabase.getDatabase(context).userDao() }
    var loggedUser by remember { mutableStateOf<UserEntity?>(null) }

    var startDestination by remember { mutableStateOf("permission") }

    LaunchedEffect(Unit) {
        loggedUser = userDao.getLoggedUser()
        if (loggedUser != null) {
            startDestination = Routes.Home.route
        }
    }

    loggedUser?.let {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            userViewModel.saveFcmToken(loggedUser!!.uid, token)
        }
    }

    NavHost(
        navController = mainNavController,
        startDestination = startDestination,
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
        composable("permission") {
            PermissionGate {
                mainNavController.navigate(if (loggedUser != null) Routes.Home.route else Routes.Login.route) {
                    popUpTo("permission") { inclusive = true }
                }
            }
        }

        composable(Routes.Login.route) {
            LoginScreen(
                onLogin = { ->
                    mainNavController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    mainNavController.navigate(Routes.Register.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onLoginClick = {
                    mainNavController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegister = { mainNavController.navigate(Routes.Login.route) }
            )
        }

        composable(route = Routes.Home.route) {
            BottomNavigationContainer(mainNavController)
        }

        composable(Routes.Search.route) {
            SearchScreen(
                onBackClick = { mainNavController.popBackStack() },
                onSearchClick = { searchResults ->
                    searchViewModel.setSearchResults(searchResults)
                    mainNavController.navigate(Routes.SearchResult.route)
                },
                onViewClick = { userUid -> mainNavController.navigate("user_profile/$userUid") }
            )
        }

        composable(Routes.SearchResult.route) {
            val results by searchViewModel.searchResults.collectAsState()
            SearchResultsScreen(
                onBackClick = { mainNavController.popBackStack() },
                searchResults = results,
                onViewClick = { userUid -> mainNavController.navigate("user_profile/$userUid") }
            )
        }

        composable(Routes.Create.route) {
            CreateScreen(
                onCreatePost = { mainNavController.navigate(Routes.CreatePost.route) },
                onCreateDailyMood = { mainNavController.navigate(Routes.CreateDailyMood.route) },
                onBack = { mainNavController.popBackStack() }
            )
        }

        composable(Routes.CreatePost.route) {
            CreatePostScreen(onBack = { mainNavController.popBackStack() })
        }

        composable(Routes.CreateDailyMood.route) {
            DailyMoodScreen(onBack = { mainNavController.popBackStack() })
        }

        composable(Routes.Insights.route) {
            InsightsScreen(onBackClick = { mainNavController.popBackStack() })
        }

        composable(
            route = "user_profile/{userUid}",
            arguments = listOf(navArgument("userUid") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userUid").orEmpty()

            ProfileScreen(
                isFromOtherUser = true,
                otherUserId = userId,
                onChatClick = { senderId, receiverId -> mainNavController.navigate("chat/$senderId/$receiverId") },
                onBack = { mainNavController.popBackStack() }
            )
        }

        composable(Routes.Personal.route) {
            PersonalScreen(onBackClick = { mainNavController.popBackStack() })
        }

        composable(Routes.Password.route) {
            PasswordScreen(
                onBack = { mainNavController.popBackStack() },
                onChange = { mainNavController.navigate(Routes.ChangePassword.route) }
            )
        }

        composable(Routes.VerifyPassword.route) {
            VerifyPasswordScreen(
                onBackClick = { mainNavController.popBackStack() },
                onVerified = { mainNavController.navigate(Routes.Password.route) },
                onRecover = { mainNavController.navigate(Routes.ForgotPassword.route) }
            )
        }

        composable(Routes.ChangePassword.route) {
            ChangePasswordScreen(
                onBackClick = { mainNavController.popBackStack() },
                onContinue = { mainNavController.navigate(Routes.Home.route) }
            )
        }

        composable(Routes.ForgotPassword.route) {
            ForgotPasswordScreen(
                onSendCodeSuccess = { email ->
                    mainNavController.navigate(Routes.VerifyCode.route + "/$email")
                }
            )
        }

        composable(Routes.VerifyCode.route + "/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyCodeScreen(
                email = email,
                onVerified = {
                    mainNavController.navigate(Routes.ResetPassword.route) {
                        popUpTo(Routes.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ResetPassword.route) {
            ResetPasswordScreen(
                onContinue = { mainNavController.navigate(Routes.Home.route) },
                onCancel = { mainNavController.navigate(Routes.Home.route) }
            )
        }

        composable(Routes.Save.route) {
            SavedScreen(
                onBack = { mainNavController.popBackStack() }
            )
        }

        composable(Routes.Favorites.route) {
            FavoritesScreen(
                onBackClick = { mainNavController.popBackStack() }
            )
        }

        composable(Routes.Privacy.route) {
            PrivacyScreen(
                onBack = { mainNavController.popBackStack() }
            )
        }

        composable(Routes.Security.route) {
            SecurityScreen(
                onBack = { mainNavController.popBackStack() }
            )
        }

        composable(Routes.Encryption.route) {
            EncryptionScreen(
                onBack = { mainNavController.popBackStack() }
            )
        }

        composable(Routes.Decryption.route) {
            DecryptionScreen(
                onBack = { mainNavController.popBackStack() }
            )
        }

        composable(Routes.Conversation.route) {
            ConversationScreen(
                onBack = { mainNavController.popBackStack() },
                onChatClick = { senderId, receiverId ->
                    mainNavController.navigate("chat/$senderId/$receiverId") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = "chat/{senderId}/{receiverId}") { backStackEntry ->
            val senderId = backStackEntry.arguments?.getString("senderId") ?: ""
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""

            ChatScreen(
                senderId,
                receiverId,
                onBack = { mainNavController.popBackStack() },
                onView = { mainNavController.navigate("user_profile/$receiverId") }
            )
        }

        composable(Routes.BottomVideo.route) {
            VideoFeedScreen(
                onBack = { mainNavController.popBackStack() },
                onUserClick = { userId ->
                    mainNavController.navigate("user_profile/$userId")
                },
                onChatClick = { senderId, receiverId ->
                    mainNavController.navigate("chat/$senderId/$receiverId") {
                        launchSingleTop = true
                    }
                },
                onRemix = { entityId, type ->
                    mainNavController.navigate("remix/$entityId/$type")
                }
            )
        }

        composable(Routes.Remix.route) { backStackEntry ->
            val entityId = backStackEntry.arguments?.getString("entityId") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: ""
            RemixScreen(entityId, type, onBack = { mainNavController.popBackStack() })
        }

        composable(Routes.Music.route) {
            SearchMusicScreen()
        }
    }
}

@Composable
fun PermissionGate(onAllGranted: () -> Unit) {
    val permissions = remember {
        buildList {
            add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
                add(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    var allGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        allGranted = results.values.all { it }
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissions.toTypedArray())
    }

    if (allGranted) {
        onAllGranted()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "This app needs permissions to function properly.",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { launcher.launch(permissions.toTypedArray()) }) {
                Text("Grant Permissions")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun BottomNavigationContainer(mainNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    Scaffold(bottomBar = { BottomBar(bottomNavController, mainNavController) }) { innerPadding ->
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
                    onCreateClick = { mainNavController.navigate(Routes.Create.route) },
                    onSearchClick = { mainNavController.navigate(Routes.Search.route) },
                    onProfileClick = { userUid ->
                        if (currentUser?.uid == userUid) {
                            bottomNavController.navigate(Routes.BottomProfile.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        } else {
                            mainNavController.navigate("user_profile/$userUid")
                        }
                    },
                    onAvatarClick = {
                        bottomNavController.navigate(Routes.BottomProfile.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(bottomNavController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    },
                    onChatClick = { senderId, receiverId ->
                        mainNavController.navigate("chat/$senderId/$receiverId")
                    },
                    onConversationClick = {
                        mainNavController.navigate(Routes.Conversation.route)
                    },
                    onRemix = { entityId, type ->
                        mainNavController.navigate("remix/$entityId/$type")
                    },
                    onCreate = {
                        mainNavController.navigate(Routes.Create.route)
                    }
                )
            }

            composable(Routes.BottomNotification.route) {
                NotificationScreen(
                    onBack = {
                        bottomNavController.popBackStack()
                    }
                )
            }

            composable(Routes.BottomProfile.route) {
                ProfileScreen(
                    onChatClick = { senderId, receiverId ->
                        mainNavController.navigate("chat/$senderId/$receiverId")
                    }
                )
            }

            composable(Routes.BottomSettings.route) {
                SettingsScreen(
                    onBackClick = { bottomNavController.navigate(Routes.BottomHome.route) },
                    onClick = { label ->
                        val route = if (label == "Logout") "login" else label.lowercase()

                        if (route == "password") {
                            mainNavController.navigate(Routes.VerifyPassword.route)
                        } else {
                            mainNavController.navigate(route) {
                                if (route == "login") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                )
            }

            composable(Routes.BottomMusic.route) {
                /*SpotifyScreen(
                    viewModel = hiltViewModel(),
                    onLogin = {
//                        authManager.login(activity)
                    },
                    miniPlayerManager = miniPlayerManager
                )*/
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, mainNavController: NavHostController) {
    val items = bottomNavItems

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val gradientBrush = Brush.linearGradient(PrimaryGradient)
    val unselectedBrush = Brush.linearGradient(listOf(Color.White, Color.White))

    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val notificationViewModel = hiltViewModel<NotificationViewModel>()

    val currentUserId = userViewModelFirebase.loggedUser.value?.uid ?: ""
    val unreadNotifications by remember(currentUserId) {
        notificationViewModel.getUnreadNotificationsByUserId(currentUserId)
    }.collectAsState(initial = emptyList())

    NavigationBar(
        modifier = Modifier.height(94.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    if (item.route == "notification_tab") {
                        Box {
                            NavIcon(item, isSelected, gradientBrush, unselectedBrush)

                            if (unreadNotifications.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 3.dp, y = (-5).dp)
                                        .background(Color.Red, CircleShape)
                                ) {
                                    Text(
                                        text = "${unreadNotifications.size}",
                                        style = Typography.bodyMedium.copy(
                                            color = Color.White,
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        NavIcon(item, isSelected, gradientBrush, unselectedBrush)
                    }
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == "notification_tab" && unreadNotifications.isNotEmpty()) {
                        unreadNotifications.forEach { notification ->
                            notificationViewModel.updateNotification(notification!!.copy(read = true))
                        }
                    }

                    if (item.route != "video_tab") {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    } else {
                        mainNavController.navigate(Routes.BottomVideo.route)
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
    Box(
        modifier = Modifier
            .background(
                if (isSelected) gradientBrush
                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                CircleShape
            )
            .size(if (isSelected) 42.dp else Dp.Unspecified),
        contentAlignment = Alignment.Center
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
}