package com.erman.pegsolitaire.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(PegSolitaireDatabase.Schema, DATABASE_NAME)
    }
}
