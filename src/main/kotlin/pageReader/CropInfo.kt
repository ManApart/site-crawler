package pageReader

internal const val HEADER = "Name, Seed Cost, Initial Growth, Regrowth, Sell Value"

data class CropInfo(val name: String, val seedCost: Int, val initialGrowth: Int, val regrowth: Int, val baseSellValue: Int) {
    fun toRow(): String {
        return listOf(name, seedCost, initialGrowth, regrowth, baseSellValue).joinToString()
    }
}