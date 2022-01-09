package fr.lewon.dofus.bot.core.manager

import fr.lewon.dofus.bot.core.manager.d2o.D2OUtil
import fr.lewon.dofus.bot.core.manager.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.maps.DofusArea

object AreaManager : VldbManager {

    private lateinit var subAreaById: Map<Double, DofusArea>

    override fun initManager() {
        subAreaById = D2OUtil.getObjects("Areas").associate {
            val id = it["id"].toString().toDouble()
            val nameId = it["nameId"].toString().toInt()
            val name = I18NUtil.getLabel(nameId)
            val superAreaId = it["superAreaId"].toString().toInt()
            id to DofusArea(id, name, superAreaId)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getArea(areaId: Double): DofusArea {
        return subAreaById[areaId] ?: error("No area for id : $areaId")
    }

}