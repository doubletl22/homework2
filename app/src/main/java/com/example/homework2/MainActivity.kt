package com.example.homework2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu // Sử dụng MenuBook cho DS Sách
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Data Classes (Đã định nghĩa ở trên hoặc có thể để ở đây) ---
data class Book(
    val id: String,
    val name: String,
    var isSelected: Boolean // Trạng thái checkbox trong danh sách của sinh viên
)

data class Student(
    val id: String,
    val name: String,
    val borrowedBooks: MutableList<Book> // Danh sách sách sinh viên này đã mượn
)

// --- Sample Data ---
val sampleStudentA = Student(
    id = "SV001", name = "Nguyen Van A",
    borrowedBooks = mutableListOf(
        Book(id = "B01", name = "Sách 01", isSelected = true),
        Book(id = "B02", name = "Sách 02", isSelected = true)
    )
)

val sampleStudentB = Student(
    id = "SV002", name = "Nguyen Thi B",
    borrowedBooks = mutableListOf(
        Book(id = "B01", name = "Sách 01", isSelected = true)
    )
)

val sampleStudentC = Student(
    id = "SV003", name = "Nguyen Van C",
    borrowedBooks = mutableListOf() // Chưa mượn sách nào
)

class MainActivity : ComponentActivity() { // Tên class này phải khớp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Giao diện Compose của bạn
            LibraryApp()
        }
    }
}
// --- Main Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryManagementScreen(currentStudent: Student, onAddBookToStudent: (Student) -> Unit, onChangeStudent: () -> Unit) {
    var studentNameState by remember(currentStudent) { mutableStateOf(currentStudent.name) }
    val booksForCurrentStudent = remember(currentStudent) { currentStudent.borrowedBooks }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hệ thống\nQuản lý Thư viện",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar() // Giả sử đã có Composable này
        },
        floatingActionButton = {
            Button( // Thay ExtendedFloatingActionButton bằng Button cho giống ảnh hơn
                onClick = { onAddBookToStudent(currentStudent) },
                modifier = Modifier
                    .fillMaxWidth(0.85f) // Điều chỉnh chiều rộng
                    .height(50.dp),    // Điều chỉnh chiều cao
                shape = MaterialTheme.shapes.medium, // Bo góc vừa phải
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Thêm", fontSize = 18.sp)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Sinh viên Section
            Text("Sinh viên", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = studentNameState,
                    onValueChange = { studentNameState = it }, // Cho phép sửa tạm thời, nhưng nên cập nhật student gốc
                    modifier = Modifier.weight(1f),
                    readOnly = true // Tên sinh viên không nên sửa trực tiếp ở đây, mà qua nút "Thay đổi"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onChangeStudent() }, // Xử lý logic thay đổi sinh viên
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Thay đổi")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Danh sách sách Section
            Text("Danh sách sách", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (booksForCurrentStudent.isEmpty()) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 150.dp) // Đảm bảo card có chiều cao tối thiểu
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Bạn chưa mượn quyển sách nào.\nNhấn 'Thêm' để bắt đầu hành trình đọc sách!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        items(booksForCurrentStudent, key = { it.id }) { book ->
                            BookListItem(
                                book = book,
                                onCheckedChange = { isChecked ->
                                    // Tìm sách trong danh sách và cập nhật trạng thái isSelected
                                    // Lưu ý: Điều này thay đổi trạng thái isSelected của đối tượng Book
                                    // trong mutableList của currentStudent.
                                    val bookInList = booksForCurrentStudent.find { it.id == book.id }
                                    bookInList?.isSelected = isChecked
                                    // Để Compose nhận biết sự thay đổi trong list item,
                                    // bạn có thể cần phải tạo một list mới hoặc dùng snapshotFlow
                                    // Tuy nhiên, vì Book.isSelected là var và chúng ta dùng remember(currentStudent),
                                    // Compose nên recompose khi currentStudent (hoặc nội dung của nó) thay đổi.
                                    // Để đảm bảo, bạn có thể cần một cơ chế state management mạnh mẽ hơn (ViewModel).
                                }
                            )
                            if (booksForCurrentStudent.last() != book) {
                                Divider(color = Color.LightGray, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f)) // Đẩy nội dung lên trên nút FAB
        }
    }
}

@Composable
fun BookListItem(book: Book, onCheckedChange: (Boolean) -> Unit) {
    // Sử dụng `key` cho `remember` để đảm bảo state của Checkbox được giữ đúng cho từng item
    // khi danh sách thay đổi hoặc sắp xếp lại.
    var isCheckedState by remember(book.id, book.isSelected) { mutableStateOf(book.isSelected) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Checkbox(
            checked = isCheckedState,
            onCheckedChange = {
                isCheckedState = it
                onCheckedChange(it) // Gọi callback để cập nhật dữ liệu gốc
            },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(book.name, fontSize = 16.sp)
    }
}

// --- Bottom Navigation (Giữ nguyên hoặc tương tự như trước) ---
@Composable
fun BottomNavigationBar() {
    var selectedItem by remember { mutableIntStateOf(0) } // dùng mutableIntStateOf cho hiệu quả hơn
    val items = listOf(
        BottomNavItemData("Quản lý", Icons.Filled.Home),
        BottomNavItemData("DS Sách", Icons.Filled.Menu), // Đã đổi sang MenuBook
        BottomNavItemData("Sinh viên", Icons.Filled.AccountCircle)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

data class BottomNavItemData(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)


// --- ViewModel (Ví dụ đơn giản để quản lý state và logic) ---
// Trong một ứng dụng thực tế, bạn nên sử dụng ViewModel của Android Jetpack.
// class LibraryViewModel : ViewModel() { ... }

// --- Previews để hiển thị các trạng thái khác nhau ---
@Preview(name = "Student A - Has Books", showBackground = true, device = "spec:width=360dp,height=640dp")
@Composable
fun PreviewStudentA() {
    MaterialTheme { // Bạn cần có một MaterialTheme bao bọc
        LibraryManagementScreen(
            currentStudent = sampleStudentA,
            onAddBookToStudent = { student ->
                // Logic thêm sách (ví dụ)
                val newBookId = "B${(student.borrowedBooks.size + 1).toString().padStart(2, '0')}"
                student.borrowedBooks.add(Book(id = newBookId, name = "Sách mới ${student.borrowedBooks.size + 1}", isSelected = true))
                // Cần trigger recomposition nếu student là state
            },
            onChangeStudent = { /* Logic thay đổi sinh viên */ }
        )
    }
}

@Preview(name = "Student B - Fewer Books", showBackground = true, device = "spec:width=360dp,height=640dp")
@Composable
fun PreviewStudentB() {
    MaterialTheme {
        LibraryManagementScreen(
            currentStudent = sampleStudentB,
            onAddBookToStudent = { /* ... */ },
            onChangeStudent = { /* ... */ }
        )
    }
}

@Preview(name = "Student C - No Books", showBackground = true, device = "spec:width=360dp,height=640dp")
@Composable
fun PreviewStudentC() {
    MaterialTheme {
        LibraryManagementScreen(
            currentStudent = sampleStudentC,
            onAddBookToStudent = { student ->
                // Logic thêm sách đầu tiên
                student.borrowedBooks.add(Book(id = "B_NEW_01", name = "Sách Mới Vừa Thêm", isSelected = true))
            },
            onChangeStudent = { /* ... */ }
        )
    }
}

// --- Ví dụ cách sử dụng trong Activity hoặc Composable gốc ---
@Composable
fun LibraryApp() {
    // Quản lý sinh viên hiện tại. Trong ứng dụng thực tế, điều này sẽ đến từ ViewModel.
    var currentDisplayStudent by remember { mutableStateOf(sampleStudentA) }

    val allStudents = listOf(sampleStudentA, sampleStudentB, sampleStudentC)
    var studentIndex by remember { mutableIntStateOf(0) }

    MaterialTheme { // Đảm bảo bạn có MaterialTheme ở cấp cao nhất
        LibraryManagementScreen(
            currentStudent = currentDisplayStudent,
            onAddBookToStudent = { student ->
                // Logic thêm sách cho sinh viên hiện tại
                // Ví dụ: Thêm một cuốn sách mặc định mới
                val newBookNumber = student.borrowedBooks.count { it.name.startsWith("Sách Thêm") } + 1
                val newBook = Book(
                    id = "BT${System.currentTimeMillis()}",
                    name = "Sách Thêm $newBookNumber",
                    isSelected = true
                )
                student.borrowedBooks.add(newBook)

                // Để Compose thấy sự thay đổi trong danh sách, tạo một bản sao của student
                // hoặc đảm bảo currentDisplayStudent được cập nhật đúng cách để trigger recomposition
                currentDisplayStudent = student.copy(borrowedBooks = student.borrowedBooks.toMutableList())

                println("Added book to ${student.name}. Total books: ${student.borrowedBooks.size}")
            },
            onChangeStudent = {
                // Logic thay đổi sinh viên, ví dụ: chuyển sang sinh viên tiếp theo trong danh sách
                studentIndex = (studentIndex + 1) % allStudents.size
                currentDisplayStudent = allStudents[studentIndex]
                println("Changed student to: ${currentDisplayStudent.name}")
            }
        )
    }
}