package com.example.unitconverter.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.unitconverter.data.DefaultDataRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.example.unitconverter.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    val viewModel = MainScreenViewModel(DefaultDataRepository())
    composeTestRule.setContent {
      MainScreenContent(
        uiState = viewModel.uiState.value,
        onSelectCategory = viewModel::selectCategory,
        onSelectFromUnit = viewModel::selectFromUnit,
        onSelectToUnit = viewModel::selectToUnit,
        onSwapUnits = viewModel::swapUnits,
        onInputChange = viewModel::updateInput,
      )
    }
  }

  @Test
  fun defaultConversion_isDisplayed() {
    composeTestRule.onNodeWithText("Converty").assertExists()
    composeTestRule.onNodeWithText("3.28084").assertExists()
  }
}
