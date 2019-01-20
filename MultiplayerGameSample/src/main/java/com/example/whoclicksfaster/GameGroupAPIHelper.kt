package com.example.whoclicksfaster

import android.util.Log
import network.celer.appsdk.*

object GameGroupAPIHelper {


    private const val TAG = "who clicks faster"
    var groupClient: GroupClient? = null
    var groupResponse: GroupResp? = null

    fun createNewGroupClient(keyStoreString: String, passwordStr: String, callback: GroupCallback): String {
        return try {
            groupClient = Appsdk.newGroupClient("group-test-priv.celer.app:10001", keyStoreString, passwordStr, callback)
            Log.d(TAG, "Connected to Group Server")
            "Connected to Group Server Success"
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            e.toString()
        }
    }


    fun createGame(joinAddr: String): String {
        leave(joinAddr)
        var group = Group()
        group.myId = joinAddr
        group.size = 2
        group.stake = "1000000000000000"
        Log.d(TAG, "Create: " + group.toString())
        try {
            groupClient?.createPrivate(group)
            return "Success"
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            return e.toString()
        }
    }


    fun joinGame(joinAddr: String, code: Long, stake:String) {
        leave(joinAddr)
        var group = Group()
        group.myId = joinAddr
        group.code = code
        group.stake = stake

        try {
            groupClient?.joinPrivate(group)
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
    }


    fun leave(joinAddr: String) {
        groupClient?.let {
            Log.d(TAG, "leave previous group")
            var group = Group()
            group.myId = joinAddr
            it.leave(group)
        }
    }


}