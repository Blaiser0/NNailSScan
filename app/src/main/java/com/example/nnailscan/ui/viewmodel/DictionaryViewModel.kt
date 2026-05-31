package com.example.nnailscan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.data.model.DictionaryTerm
import com.example.nnailscan.data.model.DictionaryTermDetail
import com.example.nnailscan.firebase.DictionaryRepository
import com.example.nnailscan.firebase.DictionarySeedRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DictionaryUiState(
    val terms: List<DictionaryTerm> = emptyList(),
    val isLoading: Boolean = true,
)

class DictionaryViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val dictionaryRepository = DictionaryRepository()
    private val dictionarySeedRepository = DictionarySeedRepository(application)

    val uiState: StateFlow<DictionaryUiState> = dictionaryRepository.observeTerms()
        .map { terms ->
            DictionaryUiState(
                terms = terms,
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DictionaryUiState(
                terms = DictionaryContent.terms.map { term ->
                    term.copy(imageUrl = DictionaryContent.assetImagePath(term.id))
                },
                isLoading = true,
            ),
        )

    private val detailStates = mutableMapOf<String, StateFlow<DictionaryTermDetail?>>()

    init {
        viewModelScope.launch {
            dictionarySeedRepository.seedIfNeeded()
        }
    }

    fun detailUiState(termId: String): StateFlow<DictionaryTermDetail?> =
        detailStates.getOrPut(termId) {
            dictionaryRepository.observeTermDetail(termId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = DictionaryContent.detailById(termId)?.copy(
                        imageUrl = DictionaryContent.assetImagePath(termId),
                    ),
                )
        }
}
