package com.alexiaherrador.numo.data.local.dao

import androidx.room.*
import com.alexiaherrador.numo.data.local.entity.GastoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {

    @Query("SELECT * FROM gastos ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE fecha BETWEEN :inicio AND :fin ORDER BY fecha DESC")
    fun obtenerPorMes(inicio: Long, fin: Long): Flow<List<GastoEntity>>

    @Query("SELECT SUM(cantidad) FROM gastos WHERE fecha BETWEEN :inicio AND :fin")
    suspend fun totalGastadoEnMes(inicio: Long, fin: Long): Double?

    @Query("SELECT SUM(cantidad) FROM gastos")
    suspend fun totalGastadoHistorico(): Double?

    @Query("SELECT * FROM gastos WHERE fecha >= :inicioDia")
    suspend fun gastosDeHoy(inicioDia: Long): List<GastoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(gasto: GastoEntity)

    @Update
    suspend fun actualizar(gasto: GastoEntity)

    @Delete
    suspend fun eliminar(gasto: GastoEntity)
}