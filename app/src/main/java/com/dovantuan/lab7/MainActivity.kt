package com.dovantuan.lab7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.room.Room

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    val db = Room.databaseBuilder(
        context,
        StudentDB::class.java, "student-db"
    )
        .allowMainThreadQueries()
        .build()

    var listStudents by remember { mutableStateOf(db.studentDAO().getAll()) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    var updateHoTen by remember { mutableStateOf("") }
    var updateMSSV by remember { mutableStateOf("") }
    var updateDiemTB by remember { mutableStateOf("") }
    var updateDaTotNghiep by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .safeDrawingPadding()
    ) {
        Text(
            text = "Quản lý sinh viên",
            style = MaterialTheme.typography.titleLarge
        )

        Button(onClick = {
            showCreateDialog = true
        }) {
            Text(text = "Thêm SV")
        }

        LazyColumn {
            itemsIndexed(listStudents) { index, student ->
                ListItem(
                    index = index,
                    item = student,
                    xoa = {
                        db.studentDAO().delete(student)
                        listStudents = db.studentDAO().getAll()
                    },
                    sua = {
                        showUpdateDialog = true
                        updateHoTen = student.hoten.toString()
                        updateMSSV = student.mssv.toString()
                        updateDiemTB = student.diemTB.toString()
                        updateDaTotNghiep = student.daTotNghiep!!
                    }

                )
            }
        }

        if (showCreateDialog) {
            CreateDialog(
                onClick = {
                    showCreateDialog = false
                    listStudents = db.studentDAO().getAll()
                },
                dialogTitle = "Create Student",
                db = db
            )
        }
        if (showUpdateDialog) {
            UpdateDialog(
                onClick = {
                    showUpdateDialog = false
                    listStudents = db.studentDAO().getAll()
                },
                dialogTitle = "Update Student",
                db = db,
                updateHoTen = updateHoTen,
                updateMSSV = updateMSSV,
                updateDiemTB = updateDiemTB,
                updateDaTotNghiep = updateDaTotNghiep
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(
    index: Int,
    item: StudentModel,
    xoa: () -> Unit = {},
    sua: () -> Unit = {},
) {
//    var showUpdateDialog by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = if (index == 0) "ID" else item.uid.toString()
            )
            Text(
                modifier = Modifier.weight(1f),
                text = if (index == 0) "Họ tên" else item.hoten.toString()
            )
            Text(
                modifier = Modifier.weight(1f),
                text = if (index == 0) "MSSV" else item.mssv.toString()
            )
            Text(
                modifier = Modifier.weight(1f),
                text = if (index == 0) "Điểm TB" else item.diemTB.toString()
            )
            Text(
                modifier = Modifier.weight(1f),
                text = if (index == 0)
                    "Tình trạng"
                else if (item.daTotNghiep == true)
                    "Đã tốt nghiệp"
                else "Chưa tốt nghiệp"
            )

            if (index != 0) {
                Card(
                    onClick = xoa,
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text("Xóa")
                    }
                }

                Card(
                    onClick = sua,
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text("Sửa")
                    }
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "CN")
                }
            }
        }
        Divider()
    }
}

@Composable
fun CreateDialog(
    onClick: () -> Unit = {},
    dialogTitle: String = "Create Student",
    db: StudentDB
) {

    var hoten by remember { mutableStateOf("") }

    var mssv by remember { mutableStateOf("") }

    var diemTB by remember { mutableStateOf("") }

    var daTotNghiep by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = {}) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Column {
                    OutlinedTextField(
                        value = hoten,
                        onValueChange = {
                            hoten = it
                        },
                        label = { Text("Họ tên") },
                        placeholder = { Text("Nhập họ tên") }
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    OutlinedTextField(
                        value = mssv,
                        onValueChange = {
                            mssv = it
                        },
                        label = { Text("MSSV") },
                        placeholder = { Text("Nhập MSSV") }
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    OutlinedTextField(
                        value = diemTB,
                        onValueChange = {
                            diemTB = it
                        },
                        label = { Text("Điểm TB") },
                        placeholder = { Text("Nhập điểm TB") }
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = daTotNghiep,
                                onClick = {
                                    daTotNghiep = !daTotNghiep
                                },
                                role = Role.Checkbox
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = daTotNghiep,
                            onCheckedChange = null
                        )

                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(text = "Đã tốt nghiệp")
                    }
                }

                Button(onClick = {
                    db.studentDAO().insert(
                        StudentModel(
                            hoten = hoten,
                            mssv = mssv,
                            diemTB = diemTB.toFloat(),
                            daTotNghiep = daTotNghiep
                        )
                    )
                    onClick()
                }) {
                    Text(text = "Thêm")
                }

            }
        }
    }
}

@Composable
fun UpdateDialog(
    onClick: () -> Unit = {},
    dialogTitle: String = "Cập nhật sinh viên",
    db: StudentDB,
    updateHoTen: String,
    updateMSSV: String,
    updateDiemTB: String,
    updateDaTotNghiep: Boolean
) {

    var hoten by remember { mutableStateOf(updateHoTen) }

    var mssv by remember { mutableStateOf(updateMSSV) }

    var diemTB by remember { mutableStateOf(updateDiemTB) }

    var daTotNghiep by remember { mutableStateOf(updateDaTotNghiep) }

    Dialog(onDismissRequest = {}) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Column {
                    OutlinedTextField(
                        value = hoten,
                        onValueChange = {
                            hoten = it
                        },
                        label = { Text("Họ tên") },
                        placeholder = { Text("Nhập họ tên") }
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    OutlinedTextField(
                        value = mssv,
                        onValueChange = {
                            mssv = it
                        },
                        label = { Text("MSSV") },
                        placeholder = { Text("Nhập MSSV") }
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    OutlinedTextField(
                        value = diemTB,
                        onValueChange = {
                            diemTB = it
                        },
                        label = { Text("Điểm TB") },
                        placeholder = { Text("Nhập điểm TB") }
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = daTotNghiep,
                                onClick = {
                                    daTotNghiep = !daTotNghiep
                                },
                                role = Role.Checkbox
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = daTotNghiep,
                            onCheckedChange = null
                        )

                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(text = "Đã tốt nghiệp")
                    }
                }

                Button(onClick = {

                    onClick()
                }) {
                    Text(text = "Cập nhật")
                }

            }
        }
    }
}