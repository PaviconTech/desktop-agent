package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.creditNote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes.Credit
import com.pavicontech.desktop.agent.domain.usecase.sales.GetAllCreditNotesUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.creditNote.components.CreditNoteBody
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.creditNote.components.CreditNoteUpperSection
import org.koin.compose.koinInject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CreditNoteScreenScreen() {
    val getEtimsSalesUseCase: GetAllCreditNotesUseCase = koinInject()
    val scope = rememberCoroutineScope()

    val crediNotes = remember { mutableStateListOf<Credit>() }
    var refresh by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var isSaleLoading by remember { mutableStateOf(false) }


    LaunchedEffect(refresh) {
        isSaleLoading = true
        crediNotes.clear()
       getEtimsSalesUseCase()?.let {
           crediNotes.addAll(it.credit)
           println(crediNotes)
       }
        isSaleLoading = false
    }

    val formatter = DateTimeFormatter.ISO_DATE_TIME

    val filteredCreditNotes = crediNotes.filter {
        searchQuery.isBlank() ||
                it.orgInvcNo.toString().contains(searchQuery, ignoreCase = true) ||
                it.invcNo.toString().contains(searchQuery, ignoreCase = true)
    }.sortedByDescending {
        runCatching {
            LocalDateTime.parse(
                it.createdAt,
                formatter
            )
        }.getOrElse { LocalDateTime.MIN }
    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        CreditNoteUpperSection(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
            }
        )
        CreditNoteBody(
            creditNotes = filteredCreditNotes,
            isLoading = isSaleLoading,
            onRefresh = { refresh = LocalDateTime.now().format(formatter) },
            onViewClick = {
            },
        )
    }
}

    
    
    
