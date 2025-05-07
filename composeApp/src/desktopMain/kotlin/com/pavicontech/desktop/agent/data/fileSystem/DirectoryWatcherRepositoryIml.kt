package com.pavicontech.desktop.agent.data.fileSystem

import com.pavicontech.desktop.agent.common.Resource
import io.github.irgaly.kfswatch.KfsDirectoryWatcher
import io.github.irgaly.kfswatch.KfsDirectoryWatcherEvent
import io.github.irgaly.kfswatch.KfsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

class DirectoryWatcherRepositoryIml:DirectoryWatcherRepository {
    override suspend fun watchDirectory(
        path: String,
        onDelete: suspend(Directory) -> Unit,
        onModify:suspend (Directory) -> Unit
    ): Flow<Resource<Directory>> = withContext(Dispatchers.IO){
        channelFlow {
            val watcher = KfsDirectoryWatcher(this)
            watcher.add(path)
            send(Resource.Loading())
            watcher.onEventFlow.collect{event: KfsDirectoryWatcherEvent ->
                when(event.event){
                    KfsEvent.Create -> {
                        send(
                            Resource.Success(
                                data = Directory(
                                    fullDirectory = "$path/${event.path}",
                                    path = event.path,
                                    fileName = event.path
                                ),
                                message = "Success"
                            )
                        )
                    }
                    KfsEvent.Delete -> {
                        onDelete(
                            Directory(
                                fullDirectory = "$path/${event.path}",
                                path = event.path,
                                fileName = event.path
                            )
                        )
                    }
                    KfsEvent.Modify -> {
                        onDelete(
                            Directory(
                                fullDirectory = "$path/${event.path}",
                                path = event.path,
                                fileName = event.path
                            )
                        )
                    }
                }
            }

        }
    }
}