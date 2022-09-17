package fr.lewon.dofus.bot.core.logs

import java.util.concurrent.ArrayBlockingQueue

class VldbLogger(logItemCapacity: Int = DEFAULT_LOG_ITEM_CAPACITY) {

    companion object {
        const val DEFAULT_LOG_ITEM_CAPACITY = 8
    }

    private val logs = ArrayBlockingQueue<LogItem>(logItemCapacity)
    val listeners = ArrayList<VldbLoggerListener>()

    private fun onLogsChange() {
        val logsCopy = getLogs()
        listeners.forEach { it.onLogsChange(this, logsCopy) }
    }

    fun getLogs(): List<LogItem> {
        return logs.toList()
    }

    fun clearLogs() {
        synchronized(logs) {
            logs.clear()
            onLogsChange()
        }
    }

    fun closeLog(message: String, parent: LogItem, clearSubLogs: Boolean = false) {
        synchronized(logs) {
            parent.closeMessage = message
            if (clearSubLogs) {
                parent.subLogs.clear()
            }
            onLogsChange()
        }
    }

    fun appendLog(logItem: LogItem, message: String) {
        synchronized(logs) {
            logItem.message += message
            onLogsChange()
        }
    }

    fun addSubLog(
        message: String, parent: LogItem, subItemCapacity: Int = DEFAULT_LOG_ITEM_CAPACITY
    ): LogItem {
        synchronized(logs) {
            val newItem = LogItem(parent, message, "", subItemCapacity)
            addSubItem(parent, newItem)
            val rootLogItem = parent.getRootLogItem()
            if (!logs.contains(rootLogItem)) {
                addLogItem(rootLogItem)
            }
            onLogsChange()
            return newItem
        }
    }

    private fun addSubItem(logItem: LogItem, subLogItem: LogItem) {
        synchronized(logItem.subLogs) {
            if (!logItem.subLogs.offer(subLogItem)) {
                logItem.subLogs.poll()
                logItem.subLogs.offer(subLogItem)
            }
        }
    }

    fun log(message: String, subItemCapacity: Int = DEFAULT_LOG_ITEM_CAPACITY, description: String = ""): LogItem {
        synchronized(logs) {
            val newItem = LogItem(null, message, description, subItemCapacity)
            addLogItem(newItem)
            onLogsChange()
            return newItem
        }
    }

    private fun addLogItem(logItem: LogItem) {
        if (!logs.offer(logItem)) {
            logs.poll()
            logs.offer(logItem)
        }
    }

}