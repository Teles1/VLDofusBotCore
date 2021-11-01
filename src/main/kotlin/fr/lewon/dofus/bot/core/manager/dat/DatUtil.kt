package fr.lewon.dofus.bot.core.manager.dat

import com.fasterxml.jackson.databind.ObjectMapper
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.manager.dat.amf3.AMF3Reader
import java.io.File

object DatUtil {

    fun <T> getDatFileContent(fileName: String, parseClass: Class<T>): T {
        var fileNameWithExtension = fileName
        if (!fileNameWithExtension.endsWith(".dat")) {
            fileNameWithExtension += ".dat"
        }
        val file = File(VldbFilesUtil.getDofusRoamingDirectory() + "/$fileNameWithExtension")
        val result = AMF3Reader().read(file.readBytes())
        val objectMapper = ObjectMapper()
        val json = objectMapper.writeValueAsString(result)
        return objectMapper.readValue(json, parseClass)
    }

}