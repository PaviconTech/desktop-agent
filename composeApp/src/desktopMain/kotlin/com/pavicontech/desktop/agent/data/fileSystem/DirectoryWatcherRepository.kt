package com.pavicontech.desktop.agent.data.fileSystem

import com.pavicontech.desktop.agent.common.Resource
import kotlinx.coroutines.flow.Flow


interface DirectoryWatcherRepository {
    suspend fun watchDirectory(
        path:String,
        onDelete: suspend (Directory) -> Unit,
        onModify: suspend(Directory) -> Unit
    ): Flow<Resource<Directory>>
}