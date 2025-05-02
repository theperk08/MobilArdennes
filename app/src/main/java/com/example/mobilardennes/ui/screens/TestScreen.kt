package com.example.mobilardennes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun TestScreenSimple(
    chaine: String
)
{
    Text(text = chaine)
}

@Composable
fun TestScreen(
    testUiState: TestUiState,
    modifier: Modifier = Modifier
) {
    when (testUiState) {
        is TestUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
        is TestUiState.SuccessTest -> TestCompose(testUiState.resultat)

        // modifier = modifier.fillMaxWidth()
        is TestUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())


    }
}



@Composable
fun TestCompose (
    resultat: String


)

{
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Test Screen example :")
        Text(text = "${resultat} ")
    }
}