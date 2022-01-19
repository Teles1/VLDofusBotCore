package fr.lewon.dofus.bot.core.manager.d2o.managers

import fr.lewon.dofus.bot.core.manager.VldbManager
import fr.lewon.dofus.bot.core.manager.d2o.D2OUtil
import fr.lewon.dofus.bot.core.manager.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.charac.DofusBreed

object BreedManager : VldbManager {

    private lateinit var breedById: Map<Int, DofusBreed>

    override fun initManager() {
        breedById = D2OUtil.getObjects("Breeds").associate {
            val id = it["id"].toString().toInt()
            val nameId = it["shortNameId"].toString().toInt()
            id to DofusBreed(id, I18NUtil.getLabel(nameId))
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getBreed(id: Int): DofusBreed {
        return breedById[id] ?: error("No breed for id : $id")
    }

    fun getAllBreeds(): List<DofusBreed> {
        return breedById.values.toList()
    }

}
