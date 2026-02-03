package com.emc.moodmingle.data.remote

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository class for handling Firebase authentication and Firestore user data operations.
 */
class FirebaseRepository @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Registers a new user using email, password, username, and avatar URL.
     */
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        avatarUrl: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("User creation failed"))

            val user = FirebaseUser(uid, username, email, avatarUrl)
            firestore.collection("users").document(uid).set(user).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs in an existing user using email and password.
     */
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("Login failed"))

            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(FirebaseUser::class.java)

            user?.let { Result.success(it) } ?: Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates an existing user's data in Firestore.
     *
     * @param uid The unique identifier of the user to update.
     * @param updatedData A map containing the fields and their new values.
     * @return A Result indicating success or failure.
     */
    suspend fun updateUser(uid: String, updatedData: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("users").document(uid).update(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches a user from Firestore using their UID.
     *
     * @param uid The unique identifier of the user to fetch.
     * @return A Result containing the FirebaseUser if successful, or an error otherwise.
     */
    suspend fun getUserByUid(uid: String): Result<FirebaseUser> {
        return try {
            if (uid.isBlank()) {
                return Result.failure(IllegalArgumentException("Invalid UID: cannot be blank or empty"))
            }

            val document = firestore.collection("users").document(uid).get().await()

            if (!document.exists()) {
                return Result.failure(Exception("User document not found for UID: $uid"))
            }

            val user = document.toObject(FirebaseUser::class.java)
                ?: return Result.failure(Exception("Failed to convert Firestore document to FirebaseUser"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifies if the entered current password matches the authenticated user.
     */
    suspend fun verifyCurrentPassword(currentPassword: String): Result<Boolean> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email ?: return Result.failure(Exception("User not logged in"))

            // reauthenticate user using entered password
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
            val savedCode = doc.getString("code") ?: return Result.failure(Exception("Code not found"))

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

    /**
     * Deletes a user's data from Firestore and optionally their authentication record.
     *
     * @param uid The unique identifier of the user to delete.
     * @param deleteAuth If true, also deletes the Firebase Authentication account.
     * @return A Result indicating success or failure.
     */
    suspend fun deleteUser(uid: String, deleteAuth: Boolean = false): Result<Unit> {
        return try {
            // delete document from firestore
            firestore.collection("users").document(uid).delete().await()

            // optionally delete the user's auth record (must be logged in as that user)
            if (deleteAuth) {
                auth.currentUser?.delete()?.await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Logs out the currently signed-in user.
     */
    fun logout() {
        auth.signOut()
    }
}