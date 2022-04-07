package com.mylittleproject.love42.tools

import android.util.Log
import com.google.firebase.firestore.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map


fun Query.getQuerySnapshotFlow(): Flow<QuerySnapshot?> {
    return callbackFlow {
        val listenerRegistration =
            addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    cancel(
                        message = "error fetching collection data",
                        cause = firebaseFirestoreException
                    )
                    return@addSnapshotListener
                }
                trySend(querySnapshot).isSuccess
            }
        awaitClose {
            Log.d(NAME_TAG, "cancelling my profile listener")
            listenerRegistration.remove()
        }
    }
}

fun <T> Query.getDataFlow(mapper: (QuerySnapshot?) -> T): Flow<T> {
    return getQuerySnapshotFlow()
        .map {
            return@map mapper(it)
        }
}

fun DocumentReference.getDocumentSnapshotFlow(): Flow<DocumentSnapshot?> {
    return callbackFlow {
        val listenerRegistration =
            addSnapshotListener { documentSnapShot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    cancel(
                        message = "error fetching collection data at path - $path",
                        cause = firebaseFirestoreException
                    )
                    return@addSnapshotListener
                }
                trySend(documentSnapShot).isSuccess
            }
        awaitClose {
            Log.d(NAME_TAG, "cancelling my profile listener at path - $path")
            listenerRegistration.remove()
        }
    }
}

fun <T> DocumentReference.getDataFlow(mapper: (DocumentSnapshot?) -> T): Flow<T> {
    return getDocumentSnapshotFlow()
        .map {
            return@map mapper(it)
        }
}