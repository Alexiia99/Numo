package com.alexiaherrador.numo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alexiaherrador.numo.data.local.dao.GastoDao
import com.alexiaherrador.numo.data.local.dao.GastoRecurrenteDao
import com.alexiaherrador.numo.data.local.dao.PresupuestoDao
import com.alexiaherrador.numo.data.local.entity.GastoEntity
import com.alexiaherrador.numo.data.local.entity.GastoRecurrenteEntity
import com.alexiaherrador.numo.data.local.entity.PresupuestoEntity


@Database(
    entities = [
        GastoEntity::class,
        PresupuestoEntity::class,
        GastoRecurrenteEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gastoDao(): GastoDao
    abstract fun presupuestoDao(): PresupuestoDao
    abstract fun gastoRecurrenteDao(): GastoRecurrenteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS gastos_recurrentes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        cantidad REAL NOT NULL,
                        categoria TEXT NOT NULL,
                        periodicidad TEXT NOT NULL,
                        proximoPago INTEGER NOT NULL,
                        diasAviso INTEGER NOT NULL DEFAULT 1,
                        activo INTEGER NOT NULL DEFAULT 1
                    )
                """)
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Recrea presupuestos sin mes ni anio
                database.execSQL("DROP TABLE IF EXISTS presupuestos")
                database.execSQL(
                    "CREATE TABLE presupuestos (categoria TEXT NOT NULL PRIMARY KEY, tope REAL NOT NULL)"
                )
            }
        }


        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "numo_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}