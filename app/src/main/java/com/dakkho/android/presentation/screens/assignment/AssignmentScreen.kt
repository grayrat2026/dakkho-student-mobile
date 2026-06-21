package com.dakkho.android.presentation.screens.assignment

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.assignment.AssignmentEmptyState
import com.dakkho.android.presentation.components.assignment.AssignmentItemCard
import com.dakkho.android.presentation.theme.DesignToken
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    courseId: String,
    onBackClick: () -> Unit,
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Set course ID on first composition
    LaunchedEffect(courseId) {
        viewModel.setCourseId(courseId)
    }

    // Track pending upload assignment ID
    var pendingUploadAssignmentId by remember { mutableStateOf<String?>(null) }

    // SAF file picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        val assignmentId = pendingUploadAssignmentId
        if (uri != null && assignmentId != null) {
            // Copy file to app cache directory for upload
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = getFileName(context, uri)
            val cacheFile = File(context.cacheDir, fileName)
            inputStream?.use { input ->
                cacheFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            viewModel.submitAssignmentWithFile(assignmentId, cacheFile.absolutePath)
        }
        pendingUploadAssignmentId = null
    }

    // Camera launcher for photo upload
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val assignmentId = pendingUploadAssignmentId
        if (success && cameraImageUri != null && assignmentId != null) {
            val filePath = cameraImageUri!!.path
            if (filePath != null) {
                viewModel.submitAssignmentWithFile(assignmentId, filePath)
            }
        }
        pendingUploadAssignmentId = null
    }

    // Show success snackbar
    LaunchedEffect(uiState.uploadSuccess) {
        if (uiState.uploadSuccess) {
            snackbarHostState.showSnackbar("Assignment submitted successfully!")
            viewModel.clearUploadSuccess()
        }
    }

    // Show error snackbar
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error!!)
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Assignments",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && uiState.assignments.isEmpty() -> {
                    AssignmentShimmerContent()
                }
                uiState.assignments.isEmpty() && !uiState.isLoading -> {
                    AssignmentEmptyState(modifier = Modifier.fillMaxSize())
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = DesignToken.Space.dp16),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
                    ) {
                        item { Spacer(modifier = Modifier.height(DesignToken.Space.dp4)) }

                        items(
                            items = uiState.assignments,
                            key = { it.id }
                        ) { assignment ->
                            AssignmentItemCard(
                                item = assignment,
                                isUploading = uiState.isUploading &&
                                    uiState.uploadingAssignmentId == assignment.id,
                                uploadProgress = uiState.uploadProgress,
                                onUploadClick = {
                                    pendingUploadAssignmentId = assignment.id
                                    // Open file picker for PDF/DOC/image files
                                    filePickerLauncher.launch(
                                        arrayOf(
                                            "application/pdf",
                                            "application/msword",
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                            "image/*"
                                        )
                                    )
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(DesignToken.Space.dp16)) }
                    }
                }
            }
        }
    }
}

private fun getFileName(context: android.content.Context, uri: Uri): String {
    var fileName = "upload_${System.currentTimeMillis()}"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) {
                fileName = it.getString(nameIndex)
            }
        }
    }
    return fileName
}

@Composable
private fun AssignmentShimmerContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = DesignToken.Space.dp16),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
    ) {
        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
        repeat(5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerEffect(modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(12.dp)
                    )
                }
            }
        }
    }
}
