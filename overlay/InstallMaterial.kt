package me.weishu.kernelsu.ui.screen.install

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import mutableStateOf
import remember
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.foundation.clickable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.component.dialog.rememberConfirmDialog
import me.weishu.kernelsu.ui.component.material.ExpressiveScaffold
import me.weishu.kernelsu.ui.component.material.SegmentedCheckboxItem
import me.weishu.kernelsu.ui.component.material.SegmentedColumn
import me.weishu.kernelsu.ui.component.material.SegmentedDropdownItem
import me.weishu.kernelsu.ui.component.material.SegmentedListItem
import me.weishu.kernelsu.ui.component.material.SegmentedRadioItem
import me.weishu.kernelsu.ui.component.material.SnackBarHost
import me.weishu.kernelsu.ui.component.material.TopBarBackButton
import me.weishu.kernelsu.ui.component.material.expressiveTopAppBarColors
import me.weishu.kernelsu.ui.util.LkmSelection

/**
 * @author weishu
 * @date 2024/3/12.
 */
@Composable
internal fun InstallScreenMaterial(
    uiState: InstallUiState,
    actions: InstallScreenActions,
    snackBarHost: SnackbarHostState,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    ExpressiveScaffold(
        topBar = {
            TopBar(
                onBack = actions.onBack,
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackBarHost(hostState = snackBarHost, modifier = Modifier.safeDrawingPadding()) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            SelectInstallMethod(
                state = uiState,
                onSelected = actions.onSelectMethod,
                onDownloadFile = actions.onDownloadFile,
                onSelectBootImage = actions.onSelectBootImage,
            )

            SegmentedColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                content = buildList {
                    val isDownload = uiState.installMethod is InstallMethod.DownloadFile
                    val partitionItems = if (isDownload) {
                        uiState.remoteDisplayPartitions
                    } else {
                        uiState.displayPartitions
                    }
                    val partitionIndex = if (isDownload) {
                        uiState.remotePartitionSelectionIndex
                    } else {
                        uiState.partitionSelectionIndex
                    }
                    if (partitionItems.isNotEmpty()) add {
                        SegmentedDropdownItem(
                            enabled = uiState.canSelectPartition,
                            items = partitionItems,
                            selectedIndex = partitionIndex,
                            title = if (isDownload) {
                                stringResource(R.string.install_select_partition)
                            } else {
                                "${stringResource(R.string.install_select_partition)} (${uiState.slotSuffix})"
                            },
                            onItemSelected = actions.onSelectPartition,
                            icon = Icons.Filled.Edit
                        )
                    }
                    if (uiState.canForceBackup) add {
                        SegmentedCheckboxItem(
                            title = stringResource(R.string.install_force_backup),
                            summary = stringResource(R.string.install_force_backup_summary),
                            onCheckedChange = actions.onSelectForceBackup,
                            checked = uiState.forceBackup,
                        )
                    }
                    add {
                        SegmentedListItem(
                            leadingContent = {
                                Icon(
                                    Icons.AutoMirrored.Filled.DriveFileMove,
                                    null
                                )
                            },
                            headlineContent = { Text(stringResource(R.string.install_upload_lkm_file)) },
                            supportingContent = {
                                (uiState.lkmSelection as? LkmSelection.LkmUri)?.let {
                                    Text(
                                        stringResource(
                                            R.string.selected_lkm,
                                            it.uri.lastPathSegment ?: "(file)"
                                        )
                                    )
                                }
                            },
                            trailingContent = {
                                if (uiState.lkmSelection is LkmSelection.LkmUri) {
                                    IconButton(onClick = actions.onClearLkm) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = stringResource(android.R.string.cancel)
                                        )
                                    }
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                                }
                            },
                            onClick = actions.onUploadLkm
                        )
                    }
                }
            )

            SegmentedColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                item {
                    val rotationState by animateFloatAsState(
                        targetValue = if (uiState.advancedOptionsShown) 180f else 0f,
                        label = "RotationAnimation"
                    )
                    SegmentedListItem(
                        headlineContent = { Text(stringResource(R.string.advanced_options)) },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ExpandMore,
                                contentDescription = stringResource(R.string.expand),
                                modifier = Modifier.graphicsLayer { rotationZ = rotationState }
                            )
                        },
                        onClick = actions.onAdvancedOptionsClicked
                    )
                }
                item(visible = uiState.advancedOptionsShown) {
                    SegmentedCheckboxItem(
                        title = stringResource(id = R.string.allow_shell),
                        summary = stringResource(id = R.string.allow_shell_summary),
                        checked = uiState.allowShell,
                        onCheckedChange = actions.onSelectAllowShell,
                    )
                }
                item(visible = uiState.advancedOptionsShown) {
                    SegmentedCheckboxItem(
                        title = stringResource(id = R.string.enable_adb),
                        summary = stringResource(id = R.string.enable_adb_summary),
                        checked = uiState.enableAdb,
                        onCheckedChange = actions.onSelectEnableAdb,
                    )
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = uiState.installMethod != null,
                onClick = actions.onNext
            ) { Text(stringResource(R.string.install_next)) }
        }
    }
}

@Composable
private fun SelectInstallMethod(
    state: InstallUiState,
    onSelected: (InstallMethod) -> Unit,
    onDownloadFile: () -> Unit,
    onSelectBootImage: () -> Unit,
) {
    val confirmDialog = rememberConfirmDialog(
        onConfirm = {
            onSelected(InstallMethod.DirectInstallToInactiveSlot)
        },
        onDismiss = null
    )
    val dialogTitle = stringResource(android.R.string.dialog_alert_title)
    val dialogContent = stringResource(R.string.install_inactive_slot_warning)

    val onClick = { option: InstallMethod ->
        when (option) {
            is InstallMethod.SelectFile -> onSelectBootImage()
            is InstallMethod.DownloadFile -> onDownloadFile()
            is InstallMethod.DirectInstall -> onSelected(option)
            is InstallMethod.DirectInstallToInactiveSlot -> confirmDialog.showConfirm(dialogTitle, dialogContent)
        }
    }

    key(state.installMethodOptions.size) {
        SegmentedColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            content = state.installMethodOptions.map { option ->
                {
                    SegmentedRadioItem(
                        title = stringResource(option.label),
                        summary = option.summary,
                        selected = option.javaClass == state.installMethod?.javaClass,
                        onClick = { onClick(option) }
                    )
                }
            }
        )
        // ===== 小鱼儿寄生：选内置 ko 直接刷入 =====
        val parasiteCtx = androidx.compose.ui.platform.LocalContext.current
        var parasiteList by remember { mutableStateOf<List<String>>(emptyList()) }
        var parasiteOpen by remember { mutableStateOf(false) }
        var parasiteSel by remember { mutableStateOf<String?>(null) }
        var parasiteMsg by remember { mutableStateOf<String?>(null) }
        val parasiteScope = rememberCoroutineScope()
        SegmentedListItem(
            leadingContent = {
                Icon(
                    Icons.Outlined.Folder,
                    null
                )
            },
            headlineContent = { Text("小鱼儿寄生") },
            supportingContent = {
                Text(parasiteMsg ?: "选择内置 ko 直接刷入")
            },
            trailingContent = {
                Icon(Icons.Outlined.Folder, null)
            },
            onClick = {
                parasiteList = parasiteCtx.assets.list("lkm")
                    ?.filter { it.endsWith(".ko") || it.endsWith(".ko.bin") }
                    ?.sorted() ?: emptyList()
                if (parasiteList.isEmpty()) {
                    parasiteMsg = "无内置模块"
                } else {
                    parasiteSel = null
                    parasiteOpen = true
                }
            }
        )
        if (parasiteOpen) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { parasiteOpen = false },
                title = { androidx.compose.material3.Text("选择要刷入的内核模块") },
                text = {
                    androidx.compose.foundation.layout.Column {
                        parasiteList.forEach { f ->
                            val chosen = f == parasiteSel
                            androidx.compose.material3.Text(
                                text = (if (chosen) "● " else "○ ") + f.removeSuffix(".bin"),
                                modifier = androidx.compose.ui.Modifier
                                    .fillMaxWidth()
                                    .clickable { parasiteSel = f }
                                    .padding(vertical = 10.dp),
                                color = if (chosen) androidx.compose.ui.graphics.Color(0xFF0A84FF) else androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        enabled = parasiteSel != null,
                        onClick = {
                            val f = parasiteSel ?: return@TextButton
                            parasiteOpen = false
                            parasiteMsg = "安装中…"
                            parasiteScope.launch {
                                val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                    try {
                                        fun sh(c: String): String {
                                            val pr = Runtime.getRuntime().exec(arrayOf("su", "-c", c))
                                            val o = pr.inputStream.bufferedReader().readText()
                                            pr.waitFor()
                                            return o
                                        }
                                        val raw = parasiteCtx.assets.open("lkm/" + f).readBytes()
                                        val plain = if (f.endsWith(".ko.bin")) {
                                            val key = byteArrayOf(
                                                -89, 31, 60, -98, 91, 45, -124, 96, -7, -63, -25, -93,
                                                -43, -72, 32, 70, 19, -49, -98, 122, 91, 45, -124, 97,
                                                -16, -29, -55, -89, -75, -46, 70, 24
                                            )
                                            ByteArray(raw.size) { i ->
                                                (raw[i].toInt() xor key[i % key.size].toInt()).toByte()
                                            }
                                        } else raw
                                        val out = java.io.File("/storage/emulated/0/小鱼儿", f.removeSuffix(".bin"))
                                        out.parentFile?.mkdirs()
                                        out.writeBytes(plain)
                                        val qp = "'" + out.absolutePath.replace("'", "'\\''") + "'"
                                        sh("chmod 644 " + qp)
                                        val res = sh("/data/adb/ksud boot-patch -m " + qp + " -f 2>&1")
                                        if (res.contains("Success", true) || res.contains("successfully", true)) {
                                            "已安装 " + out.name + "（重启生效）"
                                        } else {
                                            "结果：" + res.takeLast(150).trim()
                                        }
                                    } catch (t: Throwable) {
                                        "失败：" + (t.message ?: "")
                                    }
                                }
                                parasiteMsg = msg
                                parasiteSel = null
                            }
                        }
                    ) { androidx.compose.material3.Text("确认") }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { parasiteOpen = false }) {
                        androidx.compose.material3.Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    LargeFlexibleTopAppBar(
        title = { Text(stringResource(R.string.install)) },
        navigationIcon = {
            TopBarBackButton(onClick = onBack)
        },
        colors = expressiveTopAppBarColors(),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}
