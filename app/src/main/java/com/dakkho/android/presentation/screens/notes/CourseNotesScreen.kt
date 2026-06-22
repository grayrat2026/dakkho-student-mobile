package com.dakkho.android.presentation.screens.notes

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.CourseNote
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseNotesScreen(
    courseId: String,
    courseTitle: String,
    videoId: String = "",
    onBackClick: () -> Unit,
    onSeekToPosition: ((Long) -> Unit)? = null,
    viewModel: CourseNotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    androidx.compose.runtime.LaunchedEffect(courseId) {
        viewModel.initialize(courseId, videoId)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Course Notes",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (courseTitle.isNotEmpty()) {
                            Text(
                                text = courseTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral400
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddNoteSheet() },
                containerColor = SkyBlue
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Note",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SkyBlue)
            }
        } else if (uiState.error != null && uiState.notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.error ?: "Something went wrong",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadNotes() }) {
                        Text("Retry")
                    }
                }
            }
        } else if (uiState.notes.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Note,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Neutral400
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No notes yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = Neutral400
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to add your first note",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral400
                    )
                }
            }
        } else {
            // Note editing mode
            if (uiState.isEditing && uiState.selectedNote != null) {
                val selectedNote = uiState.selectedNote!!
                NoteEditorView(
                    note = selectedNote,
                    editContent = uiState.editContent,
                    onContentChange = { viewModel.onEditContentChange(it) },
                    onExit = { viewModel.exitEditMode() },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                // Notes list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    items(
                        items = uiState.notes,
                        key = { it.id }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = { viewModel.onNoteClick(note) },
                            onSeekClick = onSeekToPosition?.let { seek ->
                                { seek(note.positionMs) }
                            },
                            onDeleteClick = { viewModel.showDeleteConfirm(note.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Add note bottom sheet
    if (uiState.showAddNoteSheet) {
        AddNoteBottomSheet(
            currentTimestamp = viewModel.formatTimestamp(uiState.currentVideoPositionMs),
            onDismiss = { viewModel.hideAddNoteSheet() },
            onAddNote = { content ->
                viewModel.addNote(
                    positionMs = uiState.currentVideoPositionMs,
                    content = content
                )
            },
            isSaving = uiState.isSaving
        )
    }

    // Delete confirmation dialog
    if (uiState.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirm() },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        uiState.noteToDelete?.let { viewModel.deleteNote(it) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteConfirm() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun NoteCard(
    note: CourseNote,
    onClick: () -> Unit,
    onSeekClick: (() -> Unit)?,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row: timestamp + actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.timestampLabel != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SkyBlue.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = SkyBlue
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = note.timestampLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SkyBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        if (onSeekClick != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onSeekClick,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Seek to position",
                                    modifier = Modifier.size(16.dp),
                                    tint = Green
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp),
                        tint = Neutral400
                    )
                }
            }

            if (note.videoTitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.videoTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral400
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Note content (show first 3 lines)
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            Text(
                text = formatDate(note.updatedAt),
                style = MaterialTheme.typography.labelSmall,
                color = Neutral400
            )
        }
    }
}

@Composable
private fun NoteEditorView(
    note: CourseNote,
    editContent: String,
    onContentChange: (String) -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = SkyBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editing Note",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            TextButton(onClick = onExit) {
                Text("Done", color = SkyBlue)
            }
        }

        if (note.timestampLabel != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = SkyBlue.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SkyBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = note.timestampLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = SkyBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = editContent,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            placeholder = {
                Text("Write your note...", color = Neutral400)
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddNoteBottomSheet(
    currentTimestamp: String,
    onDismiss: () -> Unit,
    onAddNote: (String) -> Unit,
    isSaving: Boolean
) {
    var noteContent by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Add Note",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Current timestamp badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = SkyBlue.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SkyBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "At $currentTimestamp",
                        style = MaterialTheme.typography.labelMedium,
                        color = SkyBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Write your note...", color = Neutral400) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (noteContent.isNotBlank()) {
                        onAddNote(noteContent)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = noteContent.isNotBlank() && !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = androidx.compose.ui.graphics.Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Save Note",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}
