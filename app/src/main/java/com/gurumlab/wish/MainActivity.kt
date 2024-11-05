package com.gurumlab.wish

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.gurumlab.wish.navigation.WishNavHost
import com.gurumlab.wish.navigation.WishScreen
import com.gurumlab.wish.ui.theme.WishTheme
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLEncoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        getFCMToken()

        intent.getStringExtra(Constants.CHAT_ROOM_ID)?.let { viewModel.loadChatRoomInfo(it) }

        val splashScreen = installSplashScreen()

        lifecycleScope.launch {
            viewModel.uid.collect {
                splashScreen.setKeepOnScreenCondition { it == null }
            }
        }

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false

        setContent {
            val navController = rememberNavController()

            WishTheme {
                viewModel.uid.collectAsStateWithLifecycle().value?.let { uid ->
                    val startDestination =
                        if (uid.isBlank()) WishScreen.LOGIN.name else WishScreen.HOME.name
                    WishNavHost(navController = navController, startDestination = startDestination)
                }
            }

            viewModel.chatRoomInfo.collectAsStateWithLifecycle().value?.let {
                navController.navigate(
                    WishScreen.CHAT_ROOM.name
                            + "/${Gson().toJson(it.chatRoom)}"
                            + "/${it.othersInfo.name}"
                            + "/" + URLEncoder.encode(it.othersInfo.profileImageUrl, "UTF-8")
                            + "/${it.othersInfo.fcmToken}"
                )
            }
        }
    }

    private fun createNotificationChannel() {
        val channelId = getString(R.string.notification_channel_id)
        val channelName = getString(R.string.channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = getString(R.string.chat_notification_description)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Messaging Service", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            Log.d("Messaging Service", "token: $token")
        })
    }
}