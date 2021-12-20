package fr.lewon.dofus.bot.core.manager

import fr.lewon.dofus.bot.core.manager.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.maps.DofusMap

object WaypointsManager : VldbManager {

    lateinit var waypointsIds: List<Double>

    override fun initManager() {
        val objects = D2OUtil.getObjects("Waypoints")
        waypointsIds = objects.filter { it["activated"].toString().toBoolean() }
            .filter { !isSubAreaConquest(it["subAreaId"].toString().toDouble()) }
            .map { it["mapId"].toString().toDouble() }
    }

    private fun isSubAreaConquest(subAreaId: Double): Boolean {
        val subArea = D2OUtil.getObject("SubAreas", subAreaId)
            ?: error("SubArea not found : $subAreaId")
        return subArea["isConquestVillage"].toString().toBoolean()
    }

    fun getAllZaapMaps(): List<DofusMap> {
        return waypointsIds.map { DofusMapManager.getDofusMap(it) }
    }

}