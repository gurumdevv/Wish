package com.gurumlab.wish.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class InternalDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val permissionBooleanKey = booleanPreferencesKey("permission")

    suspend fun getWasPermissionCheck(): Boolean {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[permissionBooleanKey] ?: false
            }.firstOrNull() ?: false
    }

    suspend fun updatePermissionCheck(permission: Boolean) {
        dataStore.edit { preferences ->
            preferences[permissionBooleanKey] = permission
        }
    }
}