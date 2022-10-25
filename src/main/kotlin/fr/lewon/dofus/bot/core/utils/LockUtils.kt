package fr.lewon.dofus.bot.core.utils

import java.util.concurrent.locks.ReentrantLock

object LockUtils {

    fun <T> ReentrantLock.executeSyncOperation(operation: () -> T): T {
        try {
            lockInterruptibly()
            return operation()
        } finally {
            unlock()
        }
    }

}