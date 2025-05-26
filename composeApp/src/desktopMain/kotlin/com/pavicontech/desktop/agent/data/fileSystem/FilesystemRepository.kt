package com.pavicontech.desktop.agent.data.fileSystem

import com.pavicontech.desktop.agent.common.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File


interface FilesystemRepository {
    suspend fun watchDirectory(
        path:String,
        onDelete: suspend (Directory) -> Unit,
        onModify: suspend(Directory) -> Unit
    ): Flow<Resource<Directory>>

    suspend fun openFolder():Resource<File>
    suspend fun selectFile():Resource<File>


}