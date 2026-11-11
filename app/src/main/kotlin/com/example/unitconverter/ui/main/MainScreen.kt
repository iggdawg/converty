package com.example.unitconverter.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.unitconverter.R
import com.example.unitconverter.data.DefaultDataRepository
import com.example.unitconverter.data.UnitCategory
import com.example.unitconverter.data.UnitDefinition
import com.example.unitconverter.ui.theme.UnitConverterTheme

@Composable
fun MainScreen(
  onItemClick: (NavKey) -> Unit = {},
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository()) },
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  MainScreenContent(
    uiState = uiState,
    onSelectCategory = viewModel::selectCategory,
    onSelectFromUnit = viewModel::selectFromUnit,
    onSelectToUnit = viewModel::selectToUnit,
    onSwapUnits = viewModel::swapUnits,
    onInputChange = viewModel::updateInput,
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
  uiState: ConverterUiState,
  onSelectCategory: (UnitCategory) -> Unit,
  onSelectFromUnit: (UnitDefinition) -> Unit,
  onSelectToUnit: (UnitDefinition) -> Unit,
  onSwapUnits: () -> Unit,
  onInputChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            UnitConverterBrandIcon(size = 30.dp)
            Text(
              text = stringResource(R.string.app_name),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.SemiBold,
            )
          }
        },
        actions = {
          TextButton(
            onClick = { onInputChange(uiState.selectedCategory.defaultInput) }
          ) {
            Text("Reset")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
      )
    },
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Category Filter Chips
      item {
        CategorySelectorRow(
          categories = uiState.categories,
          selectedCategory = uiState.selectedCategory,
          onSelectCategory = onSelectCategory,
        )
      }

      // Primary Conversion Card
      item {
        PrimaryConversionCard(
          uiState = uiState,
          onSelectFromUnit = onSelectFromUnit,
          onSelectToUnit = onSelectToUnit,
          onSwapUnits = onSwapUnits,
          onInputChange = onInputChange,
          modifier = Modifier.padding(horizontal = 16.dp),
        )
      }

      // Section Header for All Units in Category
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "All ${uiState.selectedCategory.displayName} Conversions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = "Tap to select target",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // List of all units in the selected category
      items(
        items = uiState.allConversions,
        key = { it.unit.id },
      ) { item ->
        AllUnitsConversionRow(
          item = item,
          isFromUnit = item.unit.id == uiState.fromUnit.id,
          onClick = { onSelectToUnit(item.unit) },
          modifier = Modifier.padding(horizontal = 16.dp),
        )
      }
    }
  }
}

@Composable
private fun CategorySelectorRow(
  categories: List<UnitCategory>,
  selectedCategory: UnitCategory,
  onSelectCategory: (UnitCategory) -> Unit,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(categories, key = { it.name }) { category ->
      val selected = category == selectedCategory
      FilterChip(
        selected = selected,
        onClick = { onSelectCategory(category) },
        label = {
          Column(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
              text = category.shortName,
              style = MaterialTheme.typography.labelLarge,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            )
            Text(
              text = category.badgeSymbol,
              style = MaterialTheme.typography.labelSmall,
              color = if (selected) {
                MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
            )
          }
        },
        shape = RoundedCornerShape(14.dp),
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
          selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
      )
    }
  }
}

@Composable
private fun PrimaryConversionCard(
  uiState: ConverterUiState,
  onSelectFromUnit: (UnitDefinition) -> Unit,
  onSelectToUnit: (UnitDefinition) -> Unit,
  onSwapUnits: () -> Unit,
  onInputChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val quickPresets = remember(uiState.selectedCategory) {
    when (uiState.selectedCategory) {
      UnitCategory.TEMPERATURE -> listOf("-40", "0", "20", "37", "100")
      UnitCategory.ANGLE -> listOf("45", "90", "180", "360")
      UnitCategory.FUEL -> listOf("10", "25", "35", "50")
      else -> listOf("1", "10", "100", "1000")
    }
  }

  ElevatedCard(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      // FROM section
      Text(
        text = "FROM",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
      )

      UnitDropdownSelector(
        selectedUnit = uiState.fromUnit,
        availableUnits = uiState.availableUnits,
        onSelectUnit = onSelectFromUnit,
        label = "From unit",
      )

      OutlinedTextField(
        value = uiState.inputText,
        onValueChange = onInputChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Enter value in ${uiState.fromUnit.name}") },
        suffix = {
          Text(
            text = uiState.fromUnit.symbol,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
          )
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(16.dp),
      )

      // Quick preset chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Quick:",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        quickPresets.forEach { preset ->
          SuggestionChip(
            onClick = { onInputChange(preset) },
            label = {
              Text(
                text = "$preset ${uiState.fromUnit.symbol}",
                style = MaterialTheme.typography.labelSmall,
              )
            },
          )
        }
      }

      // Swap Row + Formula Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Surface(
          shape = RoundedCornerShape(50),
          color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
          Text(
            text = uiState.formulaSummary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        FilledTonalIconButton(
          onClick = onSwapUnits,
          modifier = Modifier.semantics {
            contentDescription = "Swap From and To units"
          },
        ) {
          Text(
            text = "⇅",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
          )
        }
      }

      // TO section
      Text(
        text = "TO",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
      )

      UnitDropdownSelector(
        selectedUnit = uiState.toUnit,
        availableUnits = uiState.availableUnits,
        onSelectUnit = onSelectToUnit,
        label = "To unit",
      )

      // Result Display Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            text = "${uiState.inputText.ifEmpty { "0" }} ${uiState.fromUnit.symbol} =",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              text = uiState.convertedResult,
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = uiState.toUnit.symbol,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.SemiBold,
            )
          }
          Text(
            text = uiState.toUnit.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
          )
        }
      }
    }
  }
}

@Composable
private fun UnitDropdownSelector(
  selectedUnit: UnitDefinition,
  availableUnits: List<UnitDefinition>,
  onSelectUnit: (UnitDefinition) -> Unit,
  label: String,
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxWidth()) {
    OutlinedCard(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .clickable { expanded = true }
        .semantics { contentDescription = label },
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.outlinedCardColors(
        containerColor = MaterialTheme.colorScheme.surface,
      ),
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
          ) {
            Text(
              text = selectedUnit.symbol,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
          }
          Text(
            text = selectedUnit.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
        Text(
          text = "▼",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      availableUnits.forEach { unit ->
        DropdownMenuItem(
          text = {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = unit.name,
                fontWeight = if (unit.id == selectedUnit.id) FontWeight.Bold else FontWeight.Normal,
              )
              Spacer(modifier = Modifier.width(16.dp))
              Text(
                text = unit.symbol,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
              )
            }
          },
          onClick = {
            onSelectUnit(unit)
            expanded = false
          },
        )
      }
    }
  }
}

@Composable
private fun AllUnitsConversionRow(
  item: UnitConversionItem,
  isFromUnit: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val containerColor = when {
    item.isSelectedTarget -> MaterialTheme.colorScheme.secondaryContainer
    isFromUnit -> MaterialTheme.colorScheme.surfaceContainerHigh
    else -> MaterialTheme.colorScheme.surfaceContainerLow
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = item.unit.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (item.isSelectedTarget || isFromUnit) FontWeight.SemiBold else FontWeight.Normal,
          )
          if (isFromUnit) {
            Text(
              text = "(From)",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
            )
          } else if (item.isSelectedTarget) {
            Text(
              text = "(To)",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }
        Text(
          text = item.unit.symbol,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Text(
        text = "${item.formattedValue} ${item.unit.symbol}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = if (item.isSelectedTarget) {
          MaterialTheme.colorScheme.onSecondaryContainer
        } else {
          MaterialTheme.colorScheme.onSurface
        },
      )
    }
  }
}

/**
 * Simple, clean vector icon drawn via Compose Canvas matching the adaptive launcher icon.
 */
@Composable
fun UnitConverterBrandIcon(
  size: Dp = 72.dp,
  modifier: Modifier = Modifier,
) {
  Canvas(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
  ) {
    val w = this.size.width
    val h = this.size.height

    // Deep teal gradient background
    drawRect(
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFF00506E), Color(0xFF197297)),
        start = Offset.Zero,
        end = Offset(w, h),
      )
    )

    // Subtle inner circle
    drawCircle(
      color = Color.White.copy(alpha = 0.10f),
      radius = w * 0.38f,
      center = Offset(w * 0.5f, h * 0.5f),
    )

    val strokeWidth = w * 0.075f
    val stroke = Stroke(
      width = strokeWidth,
      cap = StrokeCap.Round,
      join = StrokeJoin.Round,
    )

    // Top right-pointing arrow (White)
    val topPath = Path().apply {
      moveTo(w * 0.26f, h * 0.36f)
      lineTo(w * 0.72f, h * 0.36f)
      moveTo(w * 0.59f, h * 0.24f)
      lineTo(w * 0.73f, h * 0.36f)
      lineTo(w * 0.59f, h * 0.48f)
    }
    drawPath(path = topPath, color = Color.White, style = stroke)

    // Bottom left-pointing arrow (Light Sky Blue)
    val bottomPath = Path().apply {
      moveTo(w * 0.74f, h * 0.64f)
      lineTo(w * 0.28f, h * 0.64f)
      moveTo(w * 0.41f, h * 0.52f)
      lineTo(w * 0.27f, h * 0.64f)
      lineTo(w * 0.41f, h * 0.76f)
    }
    drawPath(path = bottomPath, color = Color(0xFF87CFF9), style = stroke)
  }
}

/**
 * Branded Splash Screen composable matching the Android 12+ SplashScreen theme.
 */
@Composable
fun UnitConverterSplashScreen(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(Color(0xFF00506E), Color(0xFF00384E))
        )
      ),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Surface(
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.12f),
        modifier = Modifier.padding(8.dp),
      ) {
        UnitConverterBrandIcon(
          size = 104.dp,
          modifier = Modifier.padding(14.dp),
        )
      }
      Text(
        text = "Converty",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White,
      )
      Text(
        text = "Simple • Accurate • Material Design",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF87CFF9),
      )
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
  val viewModel = MainScreenViewModel(DefaultDataRepository())
  UnitConverterTheme(darkTheme = false) {
    MainScreenContent(
      uiState = viewModel.uiState.value,
      onSelectCategory = {},
      onSelectFromUnit = {},
      onSelectToUnit = {},
      onSwapUnits = {},
      onInputChange = {},
    )
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
  UnitConverterTheme {
    UnitConverterSplashScreen()
  }
}
