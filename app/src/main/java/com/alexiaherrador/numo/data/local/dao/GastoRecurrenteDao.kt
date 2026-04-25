package com.alexiaherrador.numo.data.local.dao

import androidx.room.*
import com.alexiaherrador.numo.data.local.entity.GastoRecurrenteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoRecurrenteDao {

    @Query("SELECT * FROM gastos_recurrentes WHERE activo = 1 ORDER BY proximoPago ASC")
    fun obtenerTodos(): Flow<List<GastoRecurrenteEntity>>

    @Query("SELECT * FROM gastos_recurrentes WHERE activo = 1 AND proximoPago <= :hasta ORDER BY proximoPago ASC")
    suspend fun obtenerPendientes(hasta: Long): List<GastoRecurrenteEntity>

    @Query("SELECT * FROM gastos_recurrentes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): GastoRecurrenteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(gasto: GastoRecurrenteEntity)

    @Update
    suspend fun actualizar(gasto: GastoRecurrenteEntity)

    @Delete
    suspend fun eliminar(gasto: GastoRecurrenteEntity)

    @Query("DELETE FROM gastos_recurrentes")
    suspend fun borrarTodos()
}