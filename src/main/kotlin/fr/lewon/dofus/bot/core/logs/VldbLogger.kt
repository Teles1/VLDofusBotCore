package fr.lewon.dofus.bot.core.logs

import fr.lewon.dofus.bot.core.utils.LockUtils
import java.util.concurrent.locks.ReentrantLock

class VldbLogger {

    companion object {
        const val DEFAULT_LOG_ITEM_CAPACITY = 8
    }

    val listeners = HashSet<VldbLoggerListener>()
    private val lock = ReentrantLock()

    private fun getRootLogItem(logItem: LogItem): LogItem {
        return logItem.parent?.let { getRootLogItem(it) }
            ?: return logItem
    }

    fun closeLog(message: String, parent: LogItem, clearSubLogs: Boolean = false) {
        return LockUtils.executeSyncOperation(lock) {
            parent.closeMessage = message
            if (clearSubLogs) {
                parent.subLogs.clear()
            }
            onLogUpdated(getRootLogItem(parent))
        }
    }

    fun addSubLog(
        message: String, parent: LogItem, subItemCapacity: Int = DEFAULT_LOG_ITEM_CAPACITY
    ): LogItem {
        return LockUtils.executeSyncOperation(lock) {
            LogItem(parent, message, "", subItemCapacity).also {
                addSubItem(parent, it)
                onLogUpdated(getRootLogItem(it))
            }
        }
    }

    private fun addSubItem(logItem: LogItem, subLogItem: LogItem) {
        LockUtils.executeSyncOperation(lock) {
            if (!logItem.subLogs.offer(subLogItem)) {
                logItem.subLogs.poll()
                logItem.subLogs.offer(subLogItem)
            }
        }
    }

    fun log(message: String, subItemCapacity: Int = DEFAULT_LOG_ITEM_CAPACITY, description: String = ""): LogItem {
        return LockUtils.executeSyncOperation(lock) {
            LogItem(null, message, description, subItemCapacity).also { onLogUpdated(it) }
        }
    }

    private fun onLogUpdated(logItem: LogItem) {
        listeners.forEach { it.onLogUpdated(this, logItem) }
    }

}