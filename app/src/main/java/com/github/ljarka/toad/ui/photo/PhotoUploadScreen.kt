package com.github.ljarka.toad.ui.photo

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.ljarka.toad.data.species.SpeciesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoUploadScreen(
    onBack: () -> Unit,
    viewModel: PhotoUploadViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.uploadSuccess) {
        if (state.uploadSuccess) {
            onBack()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.onErrorShown()
        }
    }

    var showSourceSheet by remember { mutableStateOf(false) }
    var showSpeciesSheet by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            val uri = createTempPhotoUri(context)
            tempCameraUri = uri
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            tempCameraUri?.let { viewModel.onImageSelected(it) }
        }
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    if (showSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSourceSheet = false },
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Dodaj zdjęcie", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        showSourceSheet = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("📷 Zrób zdjęcie")
                }
                TextButton(
                    onClick = {
                        showSourceSheet = false
                        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("🖼 Wybierz z galerii")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showSpeciesSheet) {
        SpeciesPickerSheet(
            query = state.speciesSearchQuery,
            results = state.speciesSearchResults,
            onQueryChange = viewModel::onSpeciesSearchQueryChange,
            onSpeciesSelected = { species ->
                viewModel.onSpeciesSelected(species)
                showSpeciesSheet = false
            },
            onDismiss = { showSpeciesSheet = false },
        )
    }

    LaunchedEffect(tempCameraUri) {
        tempCameraUri?.let { uri ->
            takePictureLauncher.launch(uri)
            tempCameraUri = null
        }
    }

    var previewBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(state.imageUri) {
        previewBitmap = state.imageUri?.let { uri ->
            withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }.getOrNull()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nowe zdjęcie") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                onClick = { showSourceSheet = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (previewBitmap != null) {
                    Image(
                        bitmap = previewBitmap!!.asImageBitmap(),
                        contentDescription = "Podgląd zdjęcia",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🍄", style = MaterialTheme.typography.displayLarge)
                            Text(
                                "Dotknij, aby wybrać zdjęcie",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            val locationText = when {
                state.lat != null && state.lon != null ->
                    "📍 %.2f°N %.2f°E".format(state.lat, state.lon)
                else -> "📍 Lokalizacja niedostępna"
            }
            Text(
                locationText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedTextField(
                value = state.speciesSearchQuery,
                onValueChange = viewModel::onSpeciesSearchQueryChange,
                label = { Text("Gatunek (opcjonalnie)") },
                placeholder = { Text("Szukaj gatunku…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (state.selectedSpecies != null) {
                        TextButton(onClick = { viewModel.onSpeciesSelected(null) }) {
                            Text("Usuń")
                        }
                    }
                },
                readOnly = state.selectedSpecies != null,
            )
            if (state.speciesSearchResults.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        state.speciesSearchResults.take(5).forEach { species ->
                            TextButton(
                                onClick = { viewModel.onSpeciesSelected(species) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(species.namePl)
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = state.caption,
                onValueChange = viewModel::onCaptionChange,
                label = { Text("Opis (opcjonalnie)") },
                placeholder = { Text("Max 280 znaków…") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                supportingText = { Text("${state.caption.length}/280") },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Publiczne zdjęcie", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = state.isPublic,
                    onCheckedChange = viewModel::onIsPublicChange,
                )
            }

            Button(
                onClick = viewModel::upload,
                enabled = state.imageUri != null && !state.isUploading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp,
                    )
                }
                Text(if (state.isUploading) "Przesyłanie…" else "Opublikuj")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpeciesPickerSheet(
    query: String,
    results: List<SpeciesEntity>,
    onQueryChange: (String) -> Unit,
    onSpeciesSelected: (SpeciesEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Wybierz gatunek", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Szukaj…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))
            results.take(10).forEach { species ->
                TextButton(
                    onClick = { onSpeciesSelected(species) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(species.namePl)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun createTempPhotoUri(context: Context): Uri {
    val imageDir = File(context.cacheDir, "images").also { it.mkdirs() }
    val file = File(imageDir, "${UUID.randomUUID()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
