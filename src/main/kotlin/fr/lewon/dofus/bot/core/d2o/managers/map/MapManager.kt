package fr.lewon.dofus.bot.core.d2o.managers.map

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea

object MapManager : VldbManager {

    private lateinit var mapById: Map<Double, DofusMap>

    override fun initManager() {
        val objects = D2OUtil.getObjects("MapPositions")
        mapById = objects.associate {
            val id = getStringByKey(it, "id").toDouble()
            val posX = getStringByKey(it, "posX").toInt()
            val posY = getStringByKey(it, "posY").toInt()
            val subAreaId = getStringByKey(it, "subAreaId").toDouble()
            val subArea = SubAreaManager.getSubArea(subAreaId)
            val worldMapId = getStringByKey(it, "worldMap").toInt()
            val worldMap = WorldMapManager.getWorldMap(worldMapId)
            val isOutdoor = getStringByKey(it, "outdoor").toBoolean()
            val isTransition = getStringByKey(it, "isTransition").toBoolean()
            val hasPriorityOnWorldMap = getStringByKey(it, "hasPriorityOnWorldmap").toBoolean()
            id to DofusMap(subArea, worldMap, id, posX, posY, isOutdoor, isTransition, hasPriorityOnWorldMap)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return listOf(SubAreaManager, WorldMapManager)
    }

    private fun getStringByKey(d2oObject: Map<String, Any>, key: String): String {
        val value = d2oObject[key]
        return value?.toString() ?: error("invalid map value for [$key] : $value")
    }

    fun getAllDofusMaps(): List<DofusMap> {
        return mapById.values.toList()
    }

    fun getDofusMap(id: Double): DofusMap {
        return mapById[id] ?: error("No map found for id [$id]")
    }

    fun getDofusMaps(x: Int, y: Int): List<DofusMap> {
        return mapById.values.filter { it.posX == x && it.posY == y }
    }

    fun getDofusMaps(subArea: DofusSubArea): List<DofusMap> {
        return mapById.values.filter { it.subArea === subArea }
    }

}