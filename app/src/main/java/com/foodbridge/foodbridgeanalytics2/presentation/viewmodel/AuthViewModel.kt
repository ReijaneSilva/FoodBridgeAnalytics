package com.foodbridge.foodbridgeanalytics2.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodbridge.foodbridgeanalytics2.data.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    val loginResult = MutableLiveData<Result<String>>()
    val cadastroResult = MutableLiveData<Result<String>>()
    val carregando = MutableLiveData<Boolean>()

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            carregando.value = true
            loginResult.value = repo.login(email, senha)
            carregando.value = false
        }
    }

    fun cadastrar(nome: String, email: String, senha: String, tipo: String) {
        viewModelScope.launch {
            carregando.value = true
            cadastroResult.value = repo.cadastrar(nome, email, senha, tipo)
            carregando.value = false
        }
    }

    fun usuarioLogado() = repo.usuarioLogado()
}