package com.pavicontech.desktop.agent.domain.usecase

import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.data.fileSystem.DirectoryWatcherRepository


class FilePathWatcherUseCase(
    private val directoryWatcherRepository: DirectoryWatcherRepository,
) {

    suspend operator fun invoke() {
        directoryWatcherRepository.watchDirectory(
            path = "/home/dev-pasaka/Desktop",
            onDelete = { dir ->

            },
            onModify = { dir ->

            }

        ).collect { event ->
            when (event) {
                is Resource.Loading -> {
                    println("Event: ${event.message}  ${event.data} \n")
                }

                is Resource.Success -> {
                    println("File: ${event.data?.fileName}  ${event.data?.fullDirectory} \n")
                }

                is Resource.Error -> {
                    println("Event: ${event.message}  ${event.data} \n")
                }
            }
        }
    }
}