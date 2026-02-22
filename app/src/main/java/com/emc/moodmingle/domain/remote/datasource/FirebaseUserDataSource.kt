package com.emc.moodmingle.domain.remote.datasource

import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseUserDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {

    private val usersRef = firestore.collection("users")

    // register a user in firebase auth + firestore
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        avatarUrl: String,
    ): Result<UserEntityFirebase> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("UID not found")

        val user = UserEntityFirebase(
            uid = uid,
            username = username,
            email = email,
            password = password,
            avatarUrl = avatarUrl
        )
        usersRef.document(uid).set(user).await()
        user
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntityFirebase> =
        runCatching {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Login failed: no UID")
            val snapshot = usersRef.document(uid).get().await()
            snapshot.toObject(UserEntityFirebase::class.java)
                ?: throw Exception("User not found in Firestore")
        }

    fun getCurrentLoggedUser(): Flow<UserEntityFirebase?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = usersRef.document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) close(error)
                else trySend(snapshot?.toObject(UserEntityFirebase::class.java))
            }

        awaitClose { listener.remove() }
    }

    fun getUserByUid(uid: String): Flow<Result<UserEntityFirebase>> = callbackFlow {
        if (uid.isBlank()) {
            trySend(Result.failure(IllegalArgumentException("Invalid UID: cannot be blank or empty")))
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot == null || !snapshot.exists()) {
                    trySend(Result.failure(Exception("User document not found for UID: $uid")))
                    return@addSnapshotListener
                }

                val user = snapshot.toObject(UserEntityFirebase::class.java)
                if (user == null) {
                    trySend(Result.failure(Exception("Failed to convert Firestore document to UserEntityFirebase")))
                } else {
                    trySend(Result.success(user))
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun getUserByUidOnce(uid: String): Result<UserEntityFirebase> {
        return try {
            if (uid.isBlank()) {
                return Result.failure(IllegalArgumentException("Invalid UID: cannot be blank or empty"))
            }

            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("User document not found for UID: $uid"))
            }

            val user = snapshot.toObject(UserEntityFirebase::class.java)
                ?: return Result.failure(Exception("Failed to convert document to UserEntityFirebase"))

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllUsers(): Flow<List<UserEntityFirebase>> = callbackFlow {
        val listener = usersRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
            } else if (snapshot != null) {
                val users =
                    snapshot.documents.mapNotNull { it.toObject(UserEntityFirebase::class.java) }

                val fromCache = snapshot.metadata.isFromCache

                trySend(users)
                if (fromCache) {
                    println("⚠️ Loaded users from local cache (offline mode)")
                } else {
                    println("✅ Loaded users from server (online mode)")
                }
            }
        }
        awaitClose { listener.remove() }
    }

    fun searchUsersByUsername(usernameQuery: String): Flow<List<UserEntityFirebase>> =
        callbackFlow {
            val listener = usersRef
                .orderBy("username")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) close(error)
                    else {
                        val users = snapshot?.documents
                            ?.mapNotNull { it.toObject(UserEntityFirebase::class.java) }
                            ?.filter { it.username.contains(usernameQuery, ignoreCase = true) }
                            ?: emptyList()
                        trySend(users)
                    }
                }
            awaitClose { listener.remove() }
        }

    suspend fun updateUser(user: UserEntityFirebase) {
        usersRef.document(user.uid).set(user)
            .addOnSuccessListener { println("✅ Queued user update successfully") }
            .addOnFailureListener { e -> println("❌ Failed to update: ${e.message}") }
            .await()
    }

    suspend fun verifyCurrentPassword(currentPassword: String): Result<Boolean> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email ?: return Result.failure(Exception("User not logged in"))

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Boolean> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
            user?.updatePassword(newPassword)?.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendVerificationCode(email: String): Result<Boolean> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
            val currentEmail = user?.email ?: return Result.failure(Exception("User not logged in"))

            if (currentEmail == email.trim()) {
                val code = (100000..999999).random().toString()

                firestore.collection("password_reset_codes").document(email)
                    .set(mapOf("code" to code, "timestamp" to System.currentTimeMillis()))
                    .await()

                println("DEBUG: Code sent to $email -> $code")

                Result.success(true)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyCode(email: String, code: String): Result<Boolean> {
        return try {
            val doc = firestore.collection("password_reset_codes").document(email).get().await()
            val savedCode =
                doc.getString("code") ?: return Result.failure(Exception("Code not found"))

            if (savedCode == code) Result.success(true)
            else Result.success(false)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(newPassword: String): Result<Unit> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
                ?: return Result.failure(Exception("User not logged in"))

            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
