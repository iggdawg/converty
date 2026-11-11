package com.example.unitconverter.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface DataRepository {
  val categories: Flow<List<UnitCategory>>
  fun getUnitsForCategory(category: UnitCategory): List<UnitDefinition>
}

class DefaultDataRepository : DataRepository {
  override val categories: Flow<List<UnitCategory>> =
    flowOf(UnitCategory.entries)

  override fun getUnitsForCategory(category: UnitCategory): List<UnitDefinition> =
    UnitConverterEngine.getUnits(category)
}
