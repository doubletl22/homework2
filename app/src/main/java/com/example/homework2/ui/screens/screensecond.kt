package com.example.homework2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homework2.R
import kotlinx.coroutines.launch

private const val OTP_LENGTH = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeScreen(
    email: String, // Email nhận được từ màn hình trước
    onNavigateBack: () -> Unit,
    onVerificationSuccess: (otp: String) -> Unit // Callback khi xác minh thành công
) {
    var otpValues by remember { mutableStateOf(List(OTP_LENGTH) { "" }) }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusRequesters = remember { List(OTP_LENGTH) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    fun getFullOtp(): String = otpValues.joinToString("")

    fun handleOtpChange(index: Int, newValue: String) {
        val newOtpValues = otpValues.toMutableList()
        val char = newValue.takeLast(1) // Chỉ lấy ký tự cuối cùng

        if (char.all { it.isDigit() }) {
            newOtpValues[index] = char
            otpValues = newOtpValues

            if (char.isNotEmpty() && index < OTP_LENGTH - 1) {
                focusRequesters[index + 1].requestFocus()
            } else if (char.isNotEmpty() && index == OTP_LENGTH - 1) {
                // Tất cả các ô đã được điền, có thể tự động submit hoặc đợi nhấn Next
                focusManager.clearFocus()
            }
        } else if (newValue.isEmpty() && index > 0) { // Xử lý backspace
            newOtpValues[index] = ""
            otpValues = newOtpValues
            focusRequesters[index -1].requestFocus()
        } else if (newValue.isEmpty() && index == 0) {
            newOtpValues[index] = ""
            otpValues = newOtpValues
        }
    }

    fun handleNextClick() {
        focusManager.clearFocus()
        val otp = getFullOtp()
        if (otp.length == OTP_LENGTH && otp.all { it.isDigit() }) {
            isLoading = true
            coroutineScope.launch {
                kotlinx.coroutines.delay(2000) // Giả lập gọi API
                isLoading = false
                // Trong ứng dụng thực tế, bạn sẽ gọi ViewModel để xác minh OTP
                // ví dụ: viewModel.verifyOtp(email, otp)
                if (otp == "12345") { // Giả sử mã đúng là "12345"
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.info_verification_successful),
                        duration = SnackbarDuration.Short
                    )
                    onVerificationSuccess(otp) // Điều hướng đến màn hình tiếp theo
                } else {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error_invalid_otp),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.error_otp_incomplete),
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Có thể để trống hoặc thêm tiêu đề nếu muốn */ },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface, // Hoặc màu bạn muốn
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp), // Padding tổng thể
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Image - Thay thế R.drawable.uth_logo bằng resource logo thực tế của bạn
            Image(
                painter = painterResource(id = R.drawable.uth2), // Placeholder
                // painter = painterResource(id = R.drawable.uth_logo), // Logo thực tế
                contentDescription = "Logo UTH",
                modifier = Modifier
                    .height(60.dp) // Kích thước nhỏ hơn một chút so với màn hình trước
                    .padding(top = 16.dp, bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "SmartTasks",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = stringResource(id = R.string.title_verify_code),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.description_verify_code, email),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Các ô nhập OTP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Hoặc Arrangement.SpaceBetween
            ) {
                for (i in 0 until OTP_LENGTH) {
                    OtpInputBox(
                        value = otpValues[i],
                        onValueChange = { newValue -> handleOtpChange(i, newValue) },
                        focusRequester = focusRequesters[i],
                        isLastBox = i == OTP_LENGTH -1,
                        onBackspaceOnEmpty = {
                            if (i > 0) {
                                focusRequesters[i-1].requestFocus()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { handleNextClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.button_next),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f)) // Đẩy nội dung lên
        }
    }
}

@Composable
fun OtpInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    isLastBox: Boolean,
    onBackspaceOnEmpty: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = TextFieldValue(text = value, selection = TextRange(value.length)), // Luôn đặt con trỏ ở cuối
        onValueChange = { textFieldValue ->
            onValueChange(textFieldValue.text)
        },
        modifier = Modifier
            .size(50.dp) // Kích thước của mỗi ô OTP
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(0.dp) // Loại bỏ padding mặc định của BasicTextField nếu có
            .focusRequester(focusRequester)
            .onKeyEvent { event -> // Xử lý sự kiện bàn phím
                if (event.type == KeyEventType.KeyUp && event.key == Key.Backspace && value.isEmpty()) {
                    onBackspaceOnEmpty()
                    return@onKeyEvent true
                }
                false
            },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword, // Bàn phím số
            imeAction = if (isLastBox) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() },
            onNext = { /* Mặc định sẽ chuyển focus, không cần xử lý đặc biệt ở đây */ }
        ),
        singleLine = true,
        decorationBox = { innerTextField -> // Để căn giữa text bên trong box
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        }
    )
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerifyCodeScreenPreview() {
    MaterialTheme {
        VerifyCodeScreen(
            email = "example@example.com",
            onNavigateBack = {},
            onVerificationSuccess = {}
        )
    }
}