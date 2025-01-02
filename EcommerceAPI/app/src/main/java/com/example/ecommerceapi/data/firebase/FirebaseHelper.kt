package com.example.ecommerceapi.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseHelper {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    fun loginUser(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        onSuccess(userId)
                    } else {
                        onFailure(Exception("ID de usuário não encontrado."))
                    }
                } else {
                    onFailure(task.exception ?: Exception("Erro ao fazer login."))
                }
            }
    }

    fun registerUser(
        email: String,
        password: String,
        userData: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Salvar dados do usuário no Firestore
                        db.collection("User").document(userId)
                            .set(userData)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { exception -> onFailure(exception) }
                    } else {
                        onFailure(Exception("Erro ao obter o ID do usuário."))
                    }
                } else {
                    onFailure(task.exception ?: Exception("Erro ao criar usuário."))
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception ?: Exception("Erro ao enviar email de redefinição de senha."))
                }
            }
    }
}