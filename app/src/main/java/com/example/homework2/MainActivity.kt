package com.example.homework2

import android.os.Bundle
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme // Import trực tiếp nếu không dùng theme tùy chỉnh phức tạp
import com.example.homework2.ui.theme.Homework2Theme // Import theme tùy chỉnh của bạn
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.homework2.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Sử dụng theme tùy chỉnh của bạn hoặc MaterialTheme cơ bản
            Homework2Theme { // Hoặc chỉ MaterialTheme { ... } nếu không có theme tùy chỉnh
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}



