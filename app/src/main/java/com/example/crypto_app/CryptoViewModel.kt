package com.example.crypto_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto_app.crypto.CryptoNative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uniffi.crypto_ffi.*

sealed class CryptoUiState {
    data object Idle : CryptoUiState()
    data object Loading : CryptoUiState()
    data class Result(val text: String) : CryptoUiState()
    data class Error(val message: String) : CryptoUiState()
}

class CryptoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CryptoUiState>(CryptoUiState.Idle)
    val uiState: StateFlow<CryptoUiState> = _uiState.asStateFlow()

    fun performBbsPlus() {
        _uiState.value = CryptoUiState.Loading
        viewModelScope.launch {
            try {
                //background thread init
                val result = withContext(Dispatchers.Default) {
                    val claims = listOf("given_name: Jan", "family_name: Novak", "birth_date: 1999-05-12",
                        "nationality: CZ", "age_over_18: true")

                    val setup = ffiBbsSetup(claims.size.toUInt())
                    val sig   = ffiBbsSign(claims, setup.secretKeyBytes, setup.paramsBytes)
                    val proof = ffiBbsProve(claims, listOf(0u, 4u), sig.signatureBytes, setup.paramsBytes)
                    val res   = ffiBbsVerify(
                        proof.proofBytes,
                        listOf(RevealedAttribute(0u, "given_name: Jan"), RevealedAttribute(4u, "age_over_18: true")),
                        setup.publicKeyBytes, setup.paramsBytes,
                    )
                    //Log.d("BBS", "prove ${proof.executionTimeMs} ms, ${proof.proofSizeBytes} B, valid=${res.isValid}")
                    "prove ${proof.executionTimeMs} ms, ${proof.proofSizeBytes} B, valid=${res.isValid}"
                }

                //ui update
                _uiState.value = CryptoUiState.Result(result)

            } catch (e: Exception) {
                _uiState.value = CryptoUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    //TODO change to direct API call without wrappers
    fun performBbsSharp(payload: String) {
        executeCryptoOperation("BBS#") { CryptoNative.generateBbsSharpSignature(payload) }
    }

    fun performZkSnark(payload: String) {
        executeCryptoOperation("zk-SNARK") { CryptoNative.generateZkSnarkProof(payload) }
    }

    fun performZkStark(payload: String) {
        executeCryptoOperation("zk-STARK") { CryptoNative.generateZkStarkProof(payload) }
    }

    private fun executeCryptoOperation(operationName: String, operation: () -> String) {
        viewModelScope.launch {
            _uiState.value = CryptoUiState.Loading
            try {
                // Execute the JNI operation on the Default dispatcher since it can be CPU intensive
                val result = withContext(Dispatchers.Default) {
                    operation()
                }
                _uiState.value = CryptoUiState.Result(result)
            } catch (e: UnsatisfiedLinkError) {
                // Fallback mock behavior if the Rust native library is not yet linked or compiled
                _uiState.value = CryptoUiState.Result(
                    "Mock $operationName Result: Native library not yet connected."
                )
            } catch (e: Exception) {
                _uiState.value = CryptoUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
