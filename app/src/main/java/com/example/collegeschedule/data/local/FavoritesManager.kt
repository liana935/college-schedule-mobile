package com.example.collegeschedule.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object FavoritesManager {
    private lateinit var dataStore: DataStore<Preferences>
    private val favoriteGroupsKey = stringSetPreferencesKey("favorite_groups")

    fun init(context: Context) {
        dataStore = context.preferencesDataStore
    }

    suspend fun addFavoriteGroup(groupName: String) {
        dataStore.edit { prefs ->
            val current = prefs[favoriteGroupsKey] ?: emptySet()
            prefs[favoriteGroupsKey] = current + groupName
        }
    }

    suspend fun removeFavoriteGroup(groupName: String) {
        dataStore.edit { prefs ->
            val current = prefs[favoriteGroupsKey] ?: emptySet()
            prefs[favoriteGroupsKey] = current - groupName
        }
    }

    suspend fun toggleFavoriteGroup(groupName: String) {
        dataStore.edit { prefs ->
            val current = prefs[favoriteGroupsKey] ?: emptySet()
            prefs[favoriteGroupsKey] = if (groupName in current) {
                current - groupName
            } else {
                current + groupName
            }
        }
    }

    val favoriteGroups: Flow<Set<String>>
        get() = dataStore.data.map { prefs ->
            prefs[favoriteGroupsKey] ?: emptySet()
        }
}

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")