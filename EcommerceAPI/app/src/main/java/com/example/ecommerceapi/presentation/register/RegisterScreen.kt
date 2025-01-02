package com.example.ecommerceapi.presentation.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import java.util.Calendar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import com.example.ecommerceapi.data.firebase.FirebaseHelper

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit  // Navegar para a tela de login após o registro
) {
    // Estados para os campos de entrada
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val firebaseHelper = FirebaseHelper()

    // Configuração do DatePickerDialog para selecionar a data de nascimento
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            birthDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Título da tela
        Text(
            text = "Criar Conta",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Preencha os campos abaixo para criar sua conta",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo Nome
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome Completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Endereço
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Endereço") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Telefone
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefone") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //data de nascimento
        OutlinedTextField(
            value = birthDate,
            onValueChange = { },
            label = { Text("Data de Nascimento") },
            singleLine = true,
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Chama o DatePickerDialog ao clicar no campo
                    datePickerDialog.show()
                },
            trailingIcon = {
                IconButton(onClick = {
                    // Chama o DatePickerDialog ao clicar no ícone
                    datePickerDialog.show()
                }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Abrir calendário"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Email
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

        // Campo Senha
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

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Confirmar Senha
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Senha") },
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

        // Botão de Criar Conta
        Button(
            onClick = {
                when {
                    name.isEmpty() -> {
                        Toast.makeText(context, "Por favor, preencha o campo Nome!", Toast.LENGTH_SHORT).show()
                    }
                    email.isEmpty() -> {
                        Toast.makeText(context, "Por favor, preencha o campo Email!", Toast.LENGTH_SHORT).show()
                    }
                    address.isEmpty() -> {
                        Toast.makeText(context, "Por favor, preencha o campo Endereço!", Toast.LENGTH_SHORT).show()
                    }
                    phone.isEmpty() -> {
                        Toast.makeText(context, "Por favor, preencha o campo Telefone!", Toast.LENGTH_SHORT).show()
                    }
                    birthDate.isEmpty() -> {
                        Toast.makeText(context, "Por favor, preencha a Data de Nascimento!", Toast.LENGTH_SHORT).show()
                    }
                    password.isEmpty() -> {
                        Toast.makeText(context, "Por favor, preencha o campo Senha!", Toast.LENGTH_SHORT).show()
                    }
                    password != confirmPassword -> {
                        Toast.makeText(context, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        isLoading = true
                        val userData = mapOf(
                            "name" to name,
                            "email" to email,
                            "address" to address,
                            "phone" to phone,
                            "birthDate" to birthDate
                        )
                        firebaseHelper.registerUser(
                            email = email,
                            password = password,
                            userData = userData,
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                                onRegisterSuccess() // Navega para a tela de login
                            },
                            onFailure = { exception ->
                                isLoading = false
                                Toast.makeText(context, "Erro: ${exception.message}", Toast.LENGTH_SHORT).show()
                                Log.e("Erro", "Rafa: ${exception.message}")
                            }
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Criar Conta", fontSize = 18.sp)
            }
        }
    }
}