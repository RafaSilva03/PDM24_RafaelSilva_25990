package com.example.ecommerceapi.presentation.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecommerceapi.data.firebase.FirebaseHelper

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,  // Navegar para a tela principal (dashboard)
    onCreateAccountClick: () -> Unit  // Navegar para a tela de registro
) {
    // Estados para os campos de entrada e carregamento
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val firebaseHelper = FirebaseHelper()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Título da tela
        Text(
            text = "Bem-vindo!",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Faça login para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            trailingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Ícone de Email")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Senha
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botão de Login
        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    firebaseHelper.loginUser(
                        email = email,
                        password = password,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(context, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                            onLoginSuccess() // Navega para a tela principal
                        },
                        onFailure = { exception ->
                            isLoading = false
                            Toast.makeText(context, "Erro: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Entrar", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link para redefinir senha
        TextButton(onClick = {
            if (email.isNotEmpty()) {
                firebaseHelper.resetPassword(
                    email = email,
                    onSuccess = {
                        Toast.makeText(context, "Email de redefinição enviado para $email!", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        Toast.makeText(context, "Erro: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "Por favor, insira seu email!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Esqueceu sua senha?", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Link para criar conta
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Não tem uma conta?")
            TextButton(onClick = onCreateAccountClick) {
                Text(text = "Crie uma agora", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}