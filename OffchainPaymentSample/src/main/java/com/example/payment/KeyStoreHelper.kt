package com.example.payment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import network.celer.geth.Account
import network.celer.geth.Geth
import network.celer.geth.KeyStore
import java.io.File
import android.R.id.edit
import android.content.Context.MODE_MULTI_PROCESS
import android.content.Context.MODE_PRIVATE


object KeyStoreHelper {
    private val password = "CelerNetwork"
    private var gethKeyStore: KeyStore? = null
    private var account: Account? = null
    private var keyStoreString: String = ""

    /*fun Activity.SharedPreferencesSave2String(key: String, info: String): Unit {
        // 1.获得SharedPreferences对象
        var sp: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE or MODE_MULTI_PROCESS)
        // 2.获得Editor对象
        var et: SharedPreferences.Editor = sp.edit()
        // 3.存储数据
        et.putString(key, info)
        // 4.提交
        et.commit()
    }

    //取key对应的数据
    fun Activity.SharedPreferencesGet2String(key: String): String {
        // 1.获得SharedPreferences对象
        var sp: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE or MODE_MULTI_PROCESS)
        // 2.取数据
        var result: String = sp.getString(key, "")
        if (!"".equals(result)) {
            return result
        } else {
            return ""
        }
    }*/

    fun getAddress(): String {
        return account!!.address.hex
    }

    fun getPassword(): String {
        return password
    }

    fun setKeyStoreString(str: String) {
        keyStoreString = str
    }

    fun getKeyStoreString(): String {

        return keyStoreString
    }

    fun generateFilePath(context: Context): String {
        val file = File(context.filesDir.path, "celer")
        if (!file.exists()) {
            file.mkdir()
        }
        return file.path
    }

    fun generateAccount(context: Context) {
        if (gethKeyStore == null) {
            val filePath = generateFilePath(context)
            gethKeyStore = KeyStore(filePath, Geth.LightScryptN, Geth.LightScryptP)
        }

        gethKeyStore?.let { gethKeyStore ->
            account = gethKeyStore.newAccount(password)
            account?.let { account ->
                keyStoreString = String(gethKeyStore.exportKey(account, password, password), Charsets.UTF_8)
                PreferencesUtil.saveValue("keyStoreString",keyStoreString);

            }
        }
    }



}