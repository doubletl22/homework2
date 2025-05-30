package com.example.homework2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.homework2.ui.screens.ForgetPasswordScreen
import com.example.homework2.ui.screens.VerifyCodeScreen

object AppRoutes {
    const val FORGET_PASSWORD = "forget_password"
    const val VERIFY_CODE = "verify_code/{email}" // {email} là một tham số động

    fun verifyCodeRoute(email: String) = "verify_code/$email"
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.FORGET_PASSWORD) {
        composable(AppRoutes.FORGET_PASSWORD) {
            // Bạn cần đảm bảo ForgetPasswordScreen được định nghĩa và import đúng cách
            ForgetPasswordScreen(
                onNavigateToVerification = { emailFromForgetPassword ->
                    navController.navigate(AppRoutes.verifyCodeRoute(emailFromForgetPassword))
                }
            )
        }

        composable(
            route = AppRoutes.VERIFY_CODE,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            // Bạn cần đảm bảo VerifyCodeScreen được định nghĩa và import đúng cách
            VerifyCodeScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onVerificationSuccess = { otp ->
                    // Xử lý khi xác minh OTP thành công
                    // Ví dụ: Điều hướng đến màn hình đặt lại mật khẩu (chưa được tạo)
                    // navController.navigate("reset_password_screen/$email/$otp") {
                    //     popUpTo(AppRoutes.FORGET_PASSWORD) { inclusive = true }
                    // }
                    // Hoặc quay lại màn hình đăng nhập, hoặc đóng ứng dụng, tùy logic
                    // Tạm thời có thể popBackStack hoặc navigate tới một màn hình placeholder
                    navController.popBackStack(AppRoutes.FORGET_PASSWORD, inclusive = true) // Quay lại trước màn hình quên mật khẩu
                    // Hoặc navController.navigate("login_screen") { popUpTo(navController.graph.startDestinationId) { inclusive = true } }

                    // Để đơn giản, hiện tại chúng ta chỉ quay lại
                    // Hoặc bạn có thể hiển thị một thông báo rồi popBackStack
                    // GlobalScope.launch { snackbarHostState.show(...) } // Nếu có snackbar toàn cục
                }
            )

        }

        // Thêm các màn hình khác (ví dụ: màn hình đặt lại mật khẩu) ở đây
        // composable("reset_password_screen/{email}/{otp}") { ... }
    }
}