package com.example.ecommerceapi.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.ecommerceapi.Model.Product
import com.example.ecommerceapi.Model.CartProduct

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

    fun getProductsByCategory(category: String, onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Products")
            .whereEqualTo("Category", category)
            .get()
            .addOnSuccessListener { result ->
                val products = result.mapNotNull { document ->
                    try {
                        Product(
                            id =  document.getLong("ProductID")!!.toInt(),
                            name = document.getString("Name") ?: "",
                            price = document.getDouble("Price") ?: 0.0,
                            imageUrl = document.getString("ImageUrl") ?: "",
                            category = document.getString("Category") ?: "",
                            brand = document.getString("Brand") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("FirebaseHelper", "Erro ao mapear produto: ${e.message}")
                        null
                    }
                }
                Log.d("FirebaseHelper", "Produtos carregados: $products")
                onSuccess(products)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseHelper", "Erro ao buscar produtos: ${exception.message}")
                onFailure(exception)
            }
    }

    fun getProductQuantities(
        productId: Int,
        onSuccess: (List<Pair<Int, Int>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("Tamanho", "Buscando tamanhos para ProductID: $productId")
        db.collection("ProductQuantity")
            .whereEqualTo("ProductID", productId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val quantities = querySnapshot.documents.mapNotNull { document ->
                    val size = document.getLong("Size")?.toInt()
                    val quantity = document.getLong("Quantity")?.toInt()
                    if (size != null && quantity != null) {
                        size to quantity
                    } else null
                }.distinct()
                Log.d("Tamanho", "Tamanhos carregados: $quantities")
                onSuccess(quantities)
            }
            .addOnFailureListener { exception ->
                Log.d("Tamanho", "Tamanhos carregados: $exception")
                onFailure(exception)
            }
    }

    fun getProductImage(
        productId: Int,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("Products")
            .whereEqualTo("ProductID", productId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val imageUrl = querySnapshot.documents.firstOrNull()?.getString("ImageUrl")
                if (imageUrl != null) {
                    onSuccess(imageUrl)
                } else {
                    onFailure(Exception("Imagem não encontrada para o produto $productId"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun createCartForUser(
        userEmail: String,
        onSuccess: (Int) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val cartCollection = db.collection("Cart")

        // Verifica o maior CartId atual e incrementa
        cartCollection
            .orderBy("CartId", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lastCartId = querySnapshot.documents.firstOrNull()?.getLong("CartId")?.toInt() ?: 0
                val newCartId = lastCartId + 1

                // Cria o novo carrinho com o CartId como inteiro
                val cart = hashMapOf(
                    "CartId" to newCartId,
                    "UserEmail" to userEmail
                )

                cartCollection.add(cart)
                    .addOnSuccessListener {
                        onSuccess(newCartId) // Retorna o CartId criado
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception) // Retorna falha ao criar
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Retorna falha ao buscar último CartId
            }
    }

    fun addProductToCart(cartId: Int, productId: Int, size: Int, quantity: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val product = hashMapOf(
            "CartId" to cartId,
            "ProductId" to productId,
            "Size" to size,
            "Quantity" to quantity
        )
        FirebaseFirestore.getInstance().collection("CartProducts")
            .add(product)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getCartProducts(
        cartId: Int,
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("CartProducts")
            .whereEqualTo("CartId", cartId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val cartProducts = mutableListOf<CartProduct>()

                val productIds = querySnapshot.documents.mapNotNull {
                    it.getLong("ProductId")?.toInt() ?: it.getString("ProductId")?.toIntOrNull()
                }

                if (productIds.isNotEmpty()) {
                    db.collection("Products")
                        .whereIn("ProductID", productIds)
                        .get()
                        .addOnSuccessListener { productsSnapshot ->
                            val productsMap = productsSnapshot.documents.associateBy(
                                { it.getLong("ProductID")?.toInt() ?: 0 },
                                { document ->
                                    Product(
                                        id = document.getLong("ProductID")?.toInt() ?: 0,
                                        name = document.getString("Name") ?: "",
                                        price = document.getDouble("Price") ?: 0.0, // Certifique-se de que o campo Price está correto
                                        imageUrl = document.getString("ImageUrl") ?: "",
                                        category = document.getString("Category") ?: "",
                                        brand = document.getString("Brand") ?: ""
                                    )
                                }
                            )

                            querySnapshot.documents.forEach { document ->
                                val productId = document.getLong("ProductId")?.toInt()
                                    ?: document.getString("ProductId")?.toIntOrNull()
                                val product = productsMap[productId]
                                if (product != null) {
                                    cartProducts.add(
                                        CartProduct(
                                            product = product,
                                            size = document.getLong("Size")?.toInt() ?: 0,
                                            quantity = document.getLong("Quantity")?.toInt() ?: 0
                                        )
                                    )
                                }
                            }
                            onSuccess(cartProducts)
                        }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    Log.w("FirebaseHelper", "Nenhum produto no carrinho.")
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getUserCartId(
        userEmail: String,
        onSuccess: (Int?) -> Unit, // Retorna o CartId como Int ou null se não existir
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Cart")
            .whereEqualTo("UserEmail", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Se houver carrinho, retorna o CartId
                    val cartId = querySnapshot.documents.first().getLong("CartId")?.toInt()
                    onSuccess(cartId)
                } else {
                    // Se não houver carrinho, retorna null
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun importSharedCart(
        sharedCartId: String,
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("CartProducts")
            .whereEqualTo("CartId", sharedCartId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val cartProducts = mutableListOf<CartProduct>()
                val productIds = querySnapshot.documents.mapNotNull { it.getLong("ProductId")?.toInt() }

                if (productIds.isNotEmpty()) {
                    db.collection("Products")
                        .whereIn("ProductID", productIds)
                        .get()
                        .addOnSuccessListener { productsSnapshot ->
                            val productsMap = productsSnapshot.documents.associateBy(
                                { it.getLong("ProductID")!!.toInt() },
                                { document ->
                                    Product(
                                        id = document.getLong("ProductID")!!.toInt(),
                                        name = document.getString("Name") ?: "",
                                        imageUrl = document.getString("ImageUrl") ?: "",
                                        category = document.getString("Category") ?: "",
                                        brand = document.getString("Brand") ?: ""
                                    )
                                }
                            )

                            querySnapshot.documents.forEach { document ->
                                val productId = document.getLong("ProductId")!!.toInt()
                                val product = productsMap[productId]
                                if (product != null) {
                                    cartProducts.add(
                                        CartProduct(
                                            product = product,
                                            size = document.getLong("Size")?.toInt() ?: 0,
                                            quantity = document.getLong("Quantity")?.toInt() ?: 0
                                        )
                                    )
                                }
                            }
                            onSuccess(cartProducts)
                        }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    Log.w("FirebaseHelper", "Nenhum produto no carrinho compartilhado.")
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener { onFailure(it) }
    }
}