package com.theblissprogrammer.amazon.sdk.preferences

import com.theblissprogrammer.amazon.sdk.enums.DefaultsKey

/**
 * Created by ahmedsaad on 2017-11-03.
 * Copyright © 2017. All rights reserved.
 */

class PreferencesWorker(val store: PreferencesStore) : PreferencesWorkerType() {
    /// Retrieves the value from user defaults that corresponds to the given key.
    ///
    /// - Parameter key: The key that is used to read the user defaults item.
    override fun <T> get(key: DefaultsKey<T?>): T? {
        return store.get(key)
    }

    /// Stores the value in the user defaults item under the given key.
    ///
    /// - Parameters:
    ///   - value: Value to be written to the user defaults.
    ///   - key: Key under which the value is stored in the user defaults.
    override fun <T> set(value: T?, key: DefaultsKey<T?>) {
        store.set(value, key = key)
    }

    /// Deletes the single user defaults item specified by the key.
    ///
    /// - Parameter key: The key that is used to delete the keychain item.
    /// - Returns: True if the item was successfully deleted.
    override fun <T> remove(key: DefaultsKey<T?>) {
        store.remove(key)
    }

    /// Removes all the user defaults items.
    override fun clear() {
        store.clear()
    }
}