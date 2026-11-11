package com.example.unitconverter.ui.main

import androidx.lifecycle.ViewModel
import com.example.unitconverter.data.DataRepository
import com.example.unitconverter.data.DefaultDataRepository
import com.example.unitconverter.data.UnitCategory
import com.example.unitconverter.data.UnitConverterEngine
import com.example.unitconverter.data.UnitDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UnitConversionItem(
  val unit: UnitDefinition,
  val formattedValue: String,
  val isSelectedTarget: Boolean,
)

data class ConverterUiState(
  val categories: List<UnitCategory> = UnitCategory.entries,
  val selectedCategory: UnitCategory = UnitCategory.LENGTH,
  val availableUnits: List<UnitDefinition> = UnitConverterEngine.getUnits(UnitCategory.LENGTH),
  val fromUnit: UnitDefinition = UnitConverterEngine.getDefaultFromUnit(UnitCategory.LENGTH),
  val toUnit: UnitDefinition = UnitConverterEngine.getDefaultToUnit(UnitCategory.LENGTH),
  val inputText: String = UnitCategory.LENGTH.defaultInput,
  val convertedResult: String = "",
  val formulaSummary: String = "",
  val allConversions: List<UnitConversionItem> = emptyList(),
  val swapCount: Int = 0,
)

class MainScreenViewModel(
  private val dataRepository: DataRepository = DefaultDataRepository(),
) : ViewModel() {

  private val _uiState = MutableStateFlow( createInitialState() )
  val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

  private fun createInitialState(): ConverterUiState {
    val category = UnitCategory.LENGTH
    val units = dataRepository.getUnitsForCategory(category)
    val from = UnitConverterEngine.getDefaultFromUnit(category)
    val to = UnitConverterEngine.getDefaultToUnit(category)
    val input = category.defaultInput
    return recompute(
      ConverterUiState(
        categories = UnitCategory.entries,
        selectedCategory = category,
        availableUnits = units,
        fromUnit = from,
        toUnit = to,
        inputText = input,
      )
    )
  }

  fun selectCategory(category: UnitCategory) {
    _uiState.update { current ->
      if (current.selectedCategory == category) return@update current
      val units = dataRepository.getUnitsForCategory(category)
      val from = UnitConverterEngine.getDefaultFromUnit(category)
      val to = UnitConverterEngine.getDefaultToUnit(category)
      val nextInput = if (current.inputText.toDoubleOrNull() == null) {
        category.defaultInput
      } else {
        current.inputText
      }
      recompute(
        current.copy(
          selectedCategory = category,
          availableUnits = units,
          fromUnit = from,
          toUnit = to,
          inputText = nextInput,
        )
      )
    }
  }

  fun selectFromUnit(unit: UnitDefinition) {
    _uiState.update { current ->
      val newTo = if (unit.id == current.toUnit.id) current.fromUnit else current.toUnit
      recompute(current.copy(fromUnit = unit, toUnit = newTo))
    }
  }

  fun selectToUnit(unit: UnitDefinition) {
    _uiState.update { current ->
      val newFrom = if (unit.id == current.fromUnit.id) current.toUnit else current.fromUnit
      recompute(current.copy(fromUnit = newFrom, toUnit = unit))
    }
  }

  fun swapUnits() {
    _uiState.update { current ->
      recompute(
        current.copy(
          fromUnit = current.toUnit,
          toUnit = current.fromUnit,
          swapCount = current.swapCount + 1,
        )
      )
    }
  }

  fun updateInput(newText: String) {
    val filtered = newText.trim()
    if (filtered.isNotEmpty() && filtered != "-" && filtered != "." && filtered != "-.") {
      // Allow valid partial or complete decimal numbers
      if (filtered.toDoubleOrNull() == null) return
    }
    _uiState.update { current ->
      recompute(current.copy(inputText = filtered))
    }
  }

  private fun recompute(state: ConverterUiState): ConverterUiState {
    val numericValue = state.inputText.toDoubleOrNull()
    val resultString = if (numericValue != null) {
      val raw = UnitConverterEngine.convert(numericValue, state.fromUnit, state.toUnit)
      UnitConverterEngine.formatValue(raw)
    } else {
      "—"
    }

    val formula = UnitConverterEngine.getFormulaSummary(state.fromUnit, state.toUnit)

    val allItems = state.availableUnits.map { unit ->
      val formatted = if (numericValue != null) {
        UnitConverterEngine.formatValue(
          UnitConverterEngine.convert(numericValue, state.fromUnit, unit)
        )
      } else {
        "—"
      }
      UnitConversionItem(
        unit = unit,
        formattedValue = formatted,
        isSelectedTarget = unit.id == state.toUnit.id,
      )
    }

    return state.copy(
      convertedResult = resultString,
      formulaSummary = formula,
      allConversions = allItems,
    )
  }
}
