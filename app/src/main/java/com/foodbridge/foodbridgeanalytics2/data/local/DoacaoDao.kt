package com.foodbridge.foodbridgeanalytics2.data.local

import androidx.room.*
import com.foodbridge.foodbridgeanalytics2.data.models.DoacaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DoacaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(doacao: DoacaoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodas(doacoes: List<DoacaoEntity>)

    @Query("SELECT * FROM doacoes_local ORDER BY data DESC")
    fun listarTodas(): Flow<List<DoacaoEntity>>

    @Query("SELECT * FROM doacoes_local WHERE sincronizado = 0")
    suspend fun listarNaoSincronizadas(): List<DoacaoEntity>

    @Query("UPDATE doacoes_local SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizada(id: String)

    @Query("DELETE FROM doacoes_local")
    suspend fun limparTodas()
}