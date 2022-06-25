package fr.lewon.dofus.bot.core.logs

interface VldbLoggerListener {

    fun onLogsChange(logger: VldbLogger, logs: List<LogItem>)

}