package com.pavicontech.desktop.agent.domain.usecase.fileSysteme

import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.data.fileSystem.FilesystemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File

class SelectFolderUseCase(
    private val filesystemRepository: FilesystemRepository
) {
    suspend operator fun invoke(): File? = withContext(Dispatchers.IO) {
        when(val result = filesystemRepository.openFolder()){
            is Resource.Success -> result.data
            else -> result.data
        }
    }

}