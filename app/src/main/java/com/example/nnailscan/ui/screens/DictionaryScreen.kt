package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.ui.components.DictionaryTermCard
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.Typography

@Composable
fun DictionaryScreen(
    onTermClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground),
    ) {
        Text(
            text = stringResource(R.string.dictionary_title),
            modifier = Modifier
                .fillMaxWidth()
                .background(NailScanSurface)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            style = Typography.titleMedium.copy(
                color = NailScanTextPrimary,
                fontWeight = FontWeight.Bold,
            ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = DictionaryContent.terms,
                key = { it.title },
            ) { term ->
                DictionaryTermCard(
                    term = term,
                    onClick = { onTermClick(term.id) },
                )
            }
        }
    }
}
