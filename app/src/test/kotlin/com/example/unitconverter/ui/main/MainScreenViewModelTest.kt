package com.example.unitconverter.ui.main

import com.example.unitconverter.data.DefaultDataRepository
import com.example.unitconverter.data.UnitCategory
import com.example.unitconverter.data.UnitConverterEngine
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {

  @Test
  fun initialState_defaultsToLengthMeterToFoot() = runTest {
    val viewModel = MainScreenViewModel(DefaultDataRepository())
    val state = viewModel.uiState.value
    assertEquals(UnitCategory.LENGTH, state.selectedCategory)
    assertEquals("m", state.fromUnit.id)
    assertEquals("ft", state.toUnit.id)
    assertEquals("1", state.inputText)
    assertEquals("3.28084", state.convertedResult)
  }

  @Test
  fun swapUnits_swapsFromAndToAndRecomputes() = runTest {
    val viewModel = MainScreenViewModel(DefaultDataRepository())
    viewModel.swapUnits()
    val state = viewModel.uiState.value
    assertEquals("ft", state.fromUnit.id)
    assertEquals("m", state.toUnit.id)
    assertEquals("0.3048", state.convertedResult)
  }

  @Test
  fun temperatureConversion_celsiusToFahrenheitAndKelvin() = runTest {
    val viewModel = MainScreenViewModel(DefaultDataRepository())
    viewModel.selectCategory(UnitCategory.TEMPERATURE)
    viewModel.updateInput("100")
    val state = viewModel.uiState.value
    assertEquals("c", state.fromUnit.id)
    assertEquals("f", state.toUnit.id)
    assertEquals("212", state.convertedResult)

    val kelvinItem = state.allConversions.first { it.unit.id == "k" }
    assertEquals("373.15", kelvinItem.formattedValue)
  }

  @Test
  fun fuelEconomyConversion_handlesInverseLitersPer100Km() {
    val fuelUnits = UnitConverterEngine.getUnits(UnitCategory.FUEL)
    val kml = fuelUnits.first { it.id == "kml" }
    val l100km = fuelUnits.first { it.id == "l100km" }

    val result = UnitConverterEngine.convert(10.0, kml, l100km)
    assertEquals(10.0, result, 1e-6)

    val result2 = UnitConverterEngine.convert(20.0, kml, l100km)
    assertEquals(5.0, result2, 1e-6)
  }

  @Test
  fun allCategories_haveValidDefaultUnitsAndConversions() {
    for (category in UnitCategory.entries) {
      val units = UnitConverterEngine.getUnits(category)
      assertTrue("Category $category should have at least 3 units", units.size >= 3)
      val from = UnitConverterEngine.getDefaultFromUnit(category)
      val to = UnitConverterEngine.getDefaultToUnit(category)
      val forward = UnitConverterEngine.convert(10.0, from, to)
      val roundTrip = UnitConverterEngine.convert(forward, to, from)
      assertEquals("Round trip failed for $category", 10.0, roundTrip, 1e-6)
    }
  }
}
