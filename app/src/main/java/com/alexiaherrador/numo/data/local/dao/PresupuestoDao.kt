package com.alexiaherrador.numo.data.local.dao

import androidx.room.*
import com.alexiaherrador.numo.data.local.entity.PresupuestoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PresupuestoDao {

    @Query("SELECT * FROM presupuestos")
    fun obtenerTodos(): Flow<List<PresupuestoEntity>>

    @Query("SELECT tope FROM presupuestos WHERE categoria = :categoria")
    suspend fun obtenerTopePorCategoria(categoria: String): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(presupuesto: PresupuestoEntity)

    @Delete
    suspend fun eliminar(presupuesto: PresupuestoEntity)

    @Query("DELETE FROM presupuestos")
    suspend fun borrarTodos()
}