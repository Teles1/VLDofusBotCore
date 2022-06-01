package fr.lewon.dofus.bot.core.ui.managers

import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.core.ui.dat.DatUtil
import fr.lewon.dofus.bot.core.ui.xml.containers.Container

enum class DofusUIElement(
    private val xmlFileName: String,
    private val overrideType: OverrideType = OverrideType.NO_OVERRIDE,
    private val key: String = "NO_OVERRIDE",
    private val ctr: String = "NO_OVERRIDE",
) {

    INVENTORY("equipmentUi.xml", OverrideType.REPLACE, "storage", "equipmentUi"),
    ZAAP_SELECTION("zaapiSelection.xml", OverrideType.ADD_OVERRIDE, "zaapSelection", "window316"),
    BANNER("banner.xml", OverrideType.REPLACE, "banner", "mainCtr"),
    TREASURE_HUNT("treasureHunt.xml", OverrideType.REPLACE, "treasureHunt", "ctr_hunt"),
    ARENA("pvpArena.xml", OverrideType.REPLACE, "pvpArena", "window830"),
    MOUNT_PADDOCK("mountPaddock.xml"),
    STORAGE("storage.xml");

    companion object {
        private const val CONTEXT_DEFAULT = "default"
        private const val CONTEXT_FIGHT = "fight"

        private fun getUIPoints(): DofusUIPointByKey {
            return DatUtil.getDatFileContent("Berilia_ui_positions", DofusUIPointByKey::class.java)
                ?: error("Couldn't get UI positions")
        }

        fun shouldInitializeXml(xmlFileName: String): Boolean {
            return values().any { it.xmlFileName == xmlFileName }
        }

        private class DofusUIPointByKey : HashMap<String, UIPoint?>()
    }

    fun getPosition(fightContext: Boolean = false): UIPoint {
        return getContainer(fightContext).bounds.position
    }

    fun getSize(fightContext: Boolean = false): UIPoint {
        return getContainer(fightContext).bounds.size
    }

    fun getContainer(fightContext: Boolean = false): Container {
        val uiDefinition = XmlUiUtil.getUIDefinition(xmlFileName)
        val container = uiDefinition.children.firstOrNull { it.name == ctr }
            ?: uiDefinition.children[0]
        container.defaultSize = getUIPoints()[buildSizeKey(fightContext)]
        XmlContainerInitializer.initAll(container)
        val overriddenPosition = getUIPoints()[buildPosKey(fightContext)]
            ?: return container
        val positionDelta = overrideType.getResultPosition(container.bounds.position, overriddenPosition)
            .transpose(container.bounds.position.invert())
        updatePosition(container, positionDelta)
        return container
    }

    private fun updatePosition(container: Container, positionDelta: UIPoint) {
        container.bounds.position = container.bounds.position.transpose(positionDelta)
        container.children.forEach {
            updatePosition(it, positionDelta)
        }
    }

    private fun buildPosKey(fightContext: Boolean): String {
        return buildKey(fightContext, "pos")
    }

    private fun buildSizeKey(fightContext: Boolean): String {
        return buildKey(fightContext, "size")
    }

    private fun buildKey(fightContext: Boolean, infoType: String): String {
        val context = if (fightContext) CONTEXT_FIGHT else CONTEXT_DEFAULT
        return "$key##$infoType##$ctr##$context"
    }

    private enum class OverrideType(private val resultPositionCalculator: (UIPoint, UIPoint) -> UIPoint) {

        ADD_OVERRIDE({ basePosition, overriddenPosition -> basePosition.transpose(overriddenPosition) }),
        REPLACE({ _, overriddenPosition -> overriddenPosition }),
        NO_OVERRIDE({ basePosition, _ -> basePosition })
        ;

        fun getResultPosition(basePosition: UIPoint, overriddenPosition: UIPoint): UIPoint {
            return resultPositionCalculator(basePosition, overriddenPosition)
        }
    }

}