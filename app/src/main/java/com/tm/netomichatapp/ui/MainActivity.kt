package com.tm.netomichatapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tm.netomichatapp.ui.screens.ChatScreen
import com.tm.netomichatapp.ui.theme.NetomiChatAppTheme
import com.tm.netomichatapp.ui.viewmodel.ChatViewModel
import com.tm.netomichatapp.ui.viewmodel.ConnectivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetomiChatAppTheme {
                val chatViewModel: ChatViewModel by viewModels()
                val connectivityViewModel: ConnectivityViewModel by viewModels()
                val isConnected =
                    connectivityViewModel.isInternetConnected.collectAsStateWithLifecycle()
                if (isConnected.value) {
                    chatViewModel.connectSocket()
                }
                Scaffold(
                    modifier = Modifier.Companion.fillMaxSize()
                ) { innerPadding ->
                    ChatScreen(modifier = Modifier.Companion.padding(innerPadding), chatViewModel)
                }
            }
        }
    }
}