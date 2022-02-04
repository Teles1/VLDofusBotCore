package fr.lewon.dofus.bot.core.d2o.managers.spell

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.spell.*

object SpellLevelManager : VldbManager {

    private lateinit var spellLevelById: Map<Int, DofusSpellLevel>

    override fun initManager() {
        spellLevelById = D2OUtil.getObjects("SpellLevels").associate {
            val id = it["id"].toString().toInt()
            val criticalHitProbability = it["criticalHitProbability"].toString().toInt()
            val needFreeCell = it["needFreeCell"].toString().toBoolean()
            val needTakenCell = it["needTakenCell"].toString().toBoolean()
            val maxRange = it["range"].toString().toInt()
            val minRange = it["minRange"].toString().toInt()
            val castInLine = it["castInLine"].toString().toBoolean()
            val rangeCanBeBoosted = it["rangeCanBeBoosted"].toString().toBoolean()
            val apCost = it["apCost"].toString().toInt()
            val castInDiagonal = it["castInDiagonal"].toString().toBoolean()
            val initialCooldown = it["initialCooldown"].toString().toInt()
            val castTestLos = it["castTestLos"].toString().toBoolean()
            val minCastInterval = it["minCastInterval"].toString().toInt()
            val maxStack = it["maxStack"].toString().toInt()
            val grade = it["grade"].toString().toInt()
            val minPlayerLevel = it["minPlayerLevel"].toString().toInt()
            val maxCastPerTarget = it["maxCastPerTarget"].toString().toInt()
            val maxCastPerTurn = it["maxCastPerTurn"].toString().toInt()
            val forClientOnly = it["forClientOnly"].toString().toBoolean()
            val effects = parseEffects(it["effects"] as List<Map<String, Any>>?)
            val criticalEffects = parseEffects(it["criticalEffect"] as List<Map<String, Any>>?)
            id to DofusSpellLevel(
                id, criticalHitProbability, needFreeCell, needTakenCell, maxRange, minRange, castInLine,
                rangeCanBeBoosted, apCost, castInDiagonal, initialCooldown, castTestLos, minCastInterval,
                maxStack, grade, minPlayerLevel, maxCastPerTarget, maxCastPerTurn, forClientOnly,
                effects, criticalEffects
            )
        }
    }

    private fun parseEffects(effectsMaps: List<Map<String, Any>>?): List<DofusSpellEffect> {
        return effectsMaps?.mapNotNull { parseEffect(it) } ?: emptyList()
    }

    private fun parseEffect(effectMap: Map<String, Any>): DofusSpellEffect? {
        val effectId = effectMap["effectId"].toString().toInt()
        val spellEffectType = DofusSpellEffectType.fromEffectId(effectId) ?: return null
        val rawZone = parseEffectZone(effectMap["rawZone"].toString()) ?: return null
        val target = DofusSpellTarget.fromString(effectMap["targetMask"].toString()) ?: return null
        val min = effectMap["diceNum"].toString().toInt()
        val max = effectMap["diceSide"].toString().toInt()
        return DofusSpellEffect(min, max, rawZone, spellEffectType, target)
    }

    private fun parseEffectZone(effectZoneStr: String): DofusEffectZone? {
        val zoneTypeKey = effectZoneStr.firstOrNull() ?: return null
        val effectZoneType = DofusEffectZoneType.fromKey(zoneTypeKey) ?: return null
        val size = if (effectZoneStr.length > 1) {
            effectZoneStr.substring(1).toIntOrNull() ?: return null
        } else 1
        return DofusEffectZone(effectZoneType, size)
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getSpellLevel(id: Int): DofusSpellLevel {
        return spellLevelById[id] ?: error("No spell for id : $id")
    }

}