package com.example.unitconverter.data

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs

enum class UnitCategory(
  val displayName: String,
  val shortName: String,
  val badgeSymbol: String,
  val defaultInput: String,
) {
  LENGTH("Length", "Length", "m ⇄ ft", "1"),
  MASS("Weight & Mass", "Mass", "kg ⇄ lb", "1"),
  TEMPERATURE("Temperature", "Temp", "°C ⇄ °F", "20"),
  VOLUME("Volume", "Volume", "L ⇄ gal", "1"),
  AREA("Area", "Area", "m² ⇄ ft²", "1"),
  SPEED("Speed", "Speed", "km/h", "100"),
  TIME("Time", "Time", "hr ⇄ min", "1"),
  DATA("Digital Storage", "Data", "GB ⇄ MB", "1"),
  PRESSURE("Pressure", "Pressure", "bar ⇄ psi", "1"),
  ENERGY("Energy", "Energy", "kcal ⇄ kJ", "1"),
  ANGLE("Plane Angle", "Angle", "deg ⇄ rad", "180"),
  FUEL("Fuel Economy", "Fuel", "mpg ⇄ km/L", "30"),
}

data class UnitDefinition(
  val id: String,
  val name: String,
  val symbol: String,
  val category: UnitCategory,
  val toBase: (Double) -> Double,
  val fromBase: (Double) -> Double,
) {
  constructor(
    id: String,
    name: String,
    symbol: String,
    category: UnitCategory,
    factorToBase: Double,
  ) : this(
    id = id,
    name = name,
    symbol = symbol,
    category = category,
    toBase = { it * factorToBase },
    fromBase = { it / factorToBase },
  )
}

object UnitConverterEngine {

  val unitsByCategory: Map<UnitCategory, List<UnitDefinition>> = mapOf(
    UnitCategory.LENGTH to listOf(
      UnitDefinition("m", "Meter", "m", UnitCategory.LENGTH, 1.0),
      UnitDefinition("km", "Kilometer", "km", UnitCategory.LENGTH, 1000.0),
      UnitDefinition("cm", "Centimeter", "cm", UnitCategory.LENGTH, 0.01),
      UnitDefinition("mm", "Millimeter", "mm", UnitCategory.LENGTH, 0.001),
      UnitDefinition("um", "Micrometer", "µm", UnitCategory.LENGTH, 1e-6),
      UnitDefinition("nm", "Nanometer", "nm", UnitCategory.LENGTH, 1e-9),
      UnitDefinition("mi", "Mile", "mi", UnitCategory.LENGTH, 1609.344),
      UnitDefinition("yd", "Yard", "yd", UnitCategory.LENGTH, 0.9144),
      UnitDefinition("ft", "Foot", "ft", UnitCategory.LENGTH, 0.3048),
      UnitDefinition("in", "Inch", "in", UnitCategory.LENGTH, 0.0254),
      UnitDefinition("nmi", "Nautical Mile", "nmi", UnitCategory.LENGTH, 1852.0),
    ),
    UnitCategory.MASS to listOf(
      UnitDefinition("kg", "Kilogram", "kg", UnitCategory.MASS, 1.0),
      UnitDefinition("g", "Gram", "g", UnitCategory.MASS, 0.001),
      UnitDefinition("mg", "Milligram", "mg", UnitCategory.MASS, 1e-6),
      UnitDefinition("t", "Metric Ton", "t", UnitCategory.MASS, 1000.0),
      UnitDefinition("lb", "Pound", "lb", UnitCategory.MASS, 0.45359237),
      UnitDefinition("oz", "Ounce", "oz", UnitCategory.MASS, 0.028349523125),
      UnitDefinition("st", "Stone", "st", UnitCategory.MASS, 6.35029318),
      UnitDefinition("us_ton", "US Short Ton", "ton (US)", UnitCategory.MASS, 907.18474),
      UnitDefinition("uk_ton", "Imperial Ton", "ton (UK)", UnitCategory.MASS, 1016.0469088),
    ),
    UnitCategory.TEMPERATURE to listOf(
      UnitDefinition(
        id = "c",
        name = "Celsius",
        symbol = "°C",
        category = UnitCategory.TEMPERATURE,
        toBase = { it },
        fromBase = { it },
      ),
      UnitDefinition(
        id = "f",
        name = "Fahrenheit",
        symbol = "°F",
        category = UnitCategory.TEMPERATURE,
        toBase = { (it - 32.0) * 5.0 / 9.0 },
        fromBase = { (it * 9.0 / 5.0) + 32.0 },
      ),
      UnitDefinition(
        id = "k",
        name = "Kelvin",
        symbol = "K",
        category = UnitCategory.TEMPERATURE,
        toBase = { it - 273.15 },
        fromBase = { it + 273.15 },
      ),
    ),
    UnitCategory.VOLUME to listOf(
      UnitDefinition("l", "Liter", "L", UnitCategory.VOLUME, 1.0),
      UnitDefinition("ml", "Milliliter", "mL", UnitCategory.VOLUME, 0.001),
      UnitDefinition("m3", "Cubic Meter", "m³", UnitCategory.VOLUME, 1000.0),
      UnitDefinition("cm3", "Cubic Centimeter", "cm³", UnitCategory.VOLUME, 0.001),
      UnitDefinition("us_gal", "US Gallon", "gal", UnitCategory.VOLUME, 3.785411784),
      UnitDefinition("us_qt", "US Quart", "qt", UnitCategory.VOLUME, 0.946352946),
      UnitDefinition("us_pt", "US Pint", "pt", UnitCategory.VOLUME, 0.473176473),
      UnitDefinition("us_cup", "US Cup", "cup", UnitCategory.VOLUME, 0.2365882365),
      UnitDefinition("us_floz", "US Fluid Ounce", "fl oz", UnitCategory.VOLUME, 0.0295735295625),
      UnitDefinition("us_tbsp", "US Tablespoon", "tbsp", UnitCategory.VOLUME, 0.01478676478125),
      UnitDefinition("us_tsp", "US Teaspoon", "tsp", UnitCategory.VOLUME, 0.00492892159375),
      UnitDefinition("uk_gal", "Imperial Gallon", "imp gal", UnitCategory.VOLUME, 4.54609),
    ),
    UnitCategory.AREA to listOf(
      UnitDefinition("m2", "Square Meter", "m²", UnitCategory.AREA, 1.0),
      UnitDefinition("km2", "Square Kilometer", "km²", UnitCategory.AREA, 1_000_000.0),
      UnitDefinition("cm2", "Square Centimeter", "cm²", UnitCategory.AREA, 0.0001),
      UnitDefinition("mm2", "Square Millimeter", "mm²", UnitCategory.AREA, 1e-6),
      UnitDefinition("ha", "Hectare", "ha", UnitCategory.AREA, 10_000.0),
      UnitDefinition("ft2", "Square Foot", "ft²", UnitCategory.AREA, 0.09290304),
      UnitDefinition("yd2", "Square Yard", "yd²", UnitCategory.AREA, 0.83612736),
      UnitDefinition("in2", "Square Inch", "in²", UnitCategory.AREA, 0.00064516),
      UnitDefinition("ac", "Acre", "ac", UnitCategory.AREA, 4046.8564224),
      UnitDefinition("mi2", "Square Mile", "mi²", UnitCategory.AREA, 2_589_988.110336),
    ),
    UnitCategory.SPEED to listOf(
      UnitDefinition("kmh", "Kilometer per Hour", "km/h", UnitCategory.SPEED, 1.0 / 3.6),
      UnitDefinition("mph", "Mile per Hour", "mph", UnitCategory.SPEED, 0.44704),
      UnitDefinition("ms", "Meter per Second", "m/s", UnitCategory.SPEED, 1.0),
      UnitDefinition("fts", "Foot per Second", "ft/s", UnitCategory.SPEED, 0.3048),
      UnitDefinition("kn", "Knot", "kn", UnitCategory.SPEED, 1852.0 / 3600.0),
    ),
    UnitCategory.TIME to listOf(
      UnitDefinition("hr", "Hour", "hr", UnitCategory.TIME, 3600.0),
      UnitDefinition("min", "Minute", "min", UnitCategory.TIME, 60.0),
      UnitDefinition("s", "Second", "s", UnitCategory.TIME, 1.0),
      UnitDefinition("ms_time", "Millisecond", "ms", UnitCategory.TIME, 0.001),
      UnitDefinition("day", "Day", "day", UnitCategory.TIME, 86400.0),
      UnitDefinition("wk", "Week", "wk", UnitCategory.TIME, 604800.0),
      UnitDefinition("mo", "Month (avg)", "mo", UnitCategory.TIME, 2_629_746.0),
      UnitDefinition("yr", "Year (avg)", "yr", UnitCategory.TIME, 31_556_952.0),
    ),
    UnitCategory.DATA to listOf(
      UnitDefinition("gb", "Gigabyte", "GB", UnitCategory.DATA, 1e9),
      UnitDefinition("mb", "Megabyte", "MB", UnitCategory.DATA, 1e6),
      UnitDefinition("kb", "Kilobyte", "KB", UnitCategory.DATA, 1e3),
      UnitDefinition("tb", "Terabyte", "TB", UnitCategory.DATA, 1e12),
      UnitDefinition("b", "Byte", "B", UnitCategory.DATA, 1.0),
      UnitDefinition("bit", "Bit", "bit", UnitCategory.DATA, 0.125),
      UnitDefinition("kib", "Kibibyte", "KiB", UnitCategory.DATA, 1024.0),
      UnitDefinition("mib", "Mebibyte", "MiB", UnitCategory.DATA, 1024.0 * 1024.0),
      UnitDefinition("gib", "Gibibyte", "GiB", UnitCategory.DATA, 1024.0 * 1024.0 * 1024.0),
      UnitDefinition("tib", "Tebibyte", "TiB", UnitCategory.DATA, 1024.0 * 1024.0 * 1024.0 * 1024.0),
    ),
    UnitCategory.PRESSURE to listOf(
      UnitDefinition("bar", "Bar", "bar", UnitCategory.PRESSURE, 100_000.0),
      UnitDefinition("psi", "Pound per Sq Inch", "psi", UnitCategory.PRESSURE, 6894.757293168),
      UnitDefinition("pa", "Pascal", "Pa", UnitCategory.PRESSURE, 1.0),
      UnitDefinition("kpa", "Kilopascal", "kPa", UnitCategory.PRESSURE, 1000.0),
      UnitDefinition("atm", "Standard Atmosphere", "atm", UnitCategory.PRESSURE, 101_325.0),
      UnitDefinition("mmhg", "Torr / mmHg", "mmHg", UnitCategory.PRESSURE, 101_325.0 / 760.0),
    ),
    UnitCategory.ENERGY to listOf(
      UnitDefinition("kcal", "Kilocalorie (Food)", "kcal", UnitCategory.ENERGY, 4184.0),
      UnitDefinition("kj", "Kilojoule", "kJ", UnitCategory.ENERGY, 1000.0),
      UnitDefinition("j", "Joule", "J", UnitCategory.ENERGY, 1.0),
      UnitDefinition("cal", "Gram Calorie", "cal", UnitCategory.ENERGY, 4.184),
      UnitDefinition("wh", "Watt-hour", "Wh", UnitCategory.ENERGY, 3600.0),
      UnitDefinition("kwh", "Kilowatt-hour", "kWh", UnitCategory.ENERGY, 3_600_000.0),
      UnitDefinition("btu", "British Thermal Unit", "BTU", UnitCategory.ENERGY, 1055.05585262),
    ),
    UnitCategory.ANGLE to listOf(
      UnitDefinition("deg", "Degree", "°", UnitCategory.ANGLE, 1.0),
      UnitDefinition("rad", "Radian", "rad", UnitCategory.ANGLE, 180.0 / PI),
      UnitDefinition("grad", "Gradian", "grad", UnitCategory.ANGLE, 0.9),
      UnitDefinition("arcmin", "Arcminute", "′", UnitCategory.ANGLE, 1.0 / 60.0),
      UnitDefinition("arcsec", "Arcsecond", "″", UnitCategory.ANGLE, 1.0 / 3600.0),
      UnitDefinition("rev", "Revolution / Turn", "rev", UnitCategory.ANGLE, 360.0),
    ),
    UnitCategory.FUEL to listOf(
      // Base unit: km/L
      UnitDefinition("mpg_us", "Miles per Gallon (US)", "mpg (US)", UnitCategory.FUEL, 1.609344 / 3.785411784),
      UnitDefinition("kml", "Kilometers per Liter", "km/L", UnitCategory.FUEL, 1.0),
      UnitDefinition("mpg_uk", "Miles per Gallon (UK)", "mpg (UK)", UnitCategory.FUEL, 1.609344 / 4.54609),
      UnitDefinition(
        id = "l100km",
        name = "Liters per 100 km",
        symbol = "L/100km",
        category = UnitCategory.FUEL,
        toBase = { if (it == 0.0) 0.0 else 100.0 / it },
        fromBase = { if (it == 0.0) 0.0 else 100.0 / it },
      ),
    ),
  )

  fun getUnits(category: UnitCategory): List<UnitDefinition> =
    unitsByCategory[category].orEmpty()

  fun getDefaultFromUnit(category: UnitCategory): UnitDefinition =
    getUnits(category).first()

  fun getDefaultToUnit(category: UnitCategory): UnitDefinition {
    val units = getUnits(category)
    return when (category) {
      UnitCategory.LENGTH -> units.find { it.id == "ft" } ?: units[1]
      UnitCategory.MASS -> units.find { it.id == "lb" } ?: units[1]
      UnitCategory.TEMPERATURE -> units.find { it.id == "f" } ?: units[1]
      UnitCategory.VOLUME -> units.find { it.id == "us_gal" } ?: units[1]
      UnitCategory.AREA -> units.find { it.id == "ft2" } ?: units[1]
      UnitCategory.SPEED -> units.find { it.id == "mph" } ?: units[1]
      UnitCategory.TIME -> units.find { it.id == "min" } ?: units[1]
      UnitCategory.DATA -> units.find { it.id == "mb" } ?: units[1]
      UnitCategory.PRESSURE -> units.find { it.id == "psi" } ?: units[1]
      UnitCategory.ENERGY -> units.find { it.id == "kj" } ?: units[1]
      UnitCategory.ANGLE -> units.find { it.id == "rad" } ?: units[1]
      UnitCategory.FUEL -> units.find { it.id == "kml" } ?: units[1]
    }
  }

  fun convert(value: Double, from: UnitDefinition, to: UnitDefinition): Double {
    if (from.id == to.id) return value
    val baseValue = from.toBase(value)
    return to.fromBase(baseValue)
  }

  private val standardSymbols = DecimalFormatSymbols(Locale.US)
  private val standardFormat = DecimalFormat("#,##0.######", standardSymbols)
  private val scientificFormat = DecimalFormat("0.####E0", standardSymbols)

  fun formatValue(value: Double): String {
    if (value.isNaN() || value.isInfinite()) return "—"
    val normalized = if (abs(value) < 1e-12) 0.0 else value
    if (normalized == 0.0) return "0"
    val absVal = abs(normalized)
    return if (absVal >= 1e9 || absVal < 1e-4) {
      scientificFormat.format(normalized).replace("E", " × 10^")
    } else {
      standardFormat.format(normalized)
    }
  }

  fun getFormulaSummary(from: UnitDefinition, to: UnitDefinition): String {
    if (from.id == to.id) return "1 ${from.symbol} = 1 ${to.symbol}"
    if (from.category == UnitCategory.TEMPERATURE) {
      return when (from.id to to.id) {
        "c" to "f" -> "°F = (°C × 9/5) + 32"
        "f" to "c" -> "°C = (°F − 32) × 5/9"
        "c" to "k" -> "K = °C + 273.15"
        "k" to "c" -> "°C = K − 273.15"
        "f" to "k" -> "K = (°F − 32) × 5/9 + 273.15"
        "k" to "f" -> "°F = (K − 273.15) × 9/5 + 32"
        else -> ""
      }
    }
    if (from.id == "l100km" || to.id == "l100km") {
      val sample = convert(10.0, from, to)
      return "10 ${from.symbol} = ${formatValue(sample)} ${to.symbol}"
    }
    val oneConverted = convert(1.0, from, to)
    return "1 ${from.symbol} = ${formatValue(oneConverted)} ${to.symbol}"
  }
}
