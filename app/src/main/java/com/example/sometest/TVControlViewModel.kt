package com.example.sometest

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectsdk.device.ConnectableDevice
import com.connectsdk.discovery.DiscoveryManager
import com.connectsdk.discovery.DiscoveryManagerListener
import com.connectsdk.service.capability.VolumeControl
import com.connectsdk.service.capability.listeners.ResponseListener
import com.connectsdk.service.command.ServiceCommandError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TVControlViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TVControlState())
    val uiState: StateFlow<TVControlState> = _uiState
    val logText = MutableLiveData("")

    private var discoveryManager: DiscoveryManager? = null
    private var currentDevice: ConnectableDevice? = null

    fun addLog(text: String) {
        logText.value += text + "\n"
    }

    suspend fun startDiscovery(context: Context) = withContext(Dispatchers.IO) {
        DiscoveryManager.init(context)
        discoveryManager = DiscoveryManager.getInstance()
        discoveryManager?.start()
        discoveryManager?.addListener(object : DiscoveryManagerListener {
            override fun onDeviceAdded(manager: DiscoveryManager?, device: ConnectableDevice?) {
                addLog("чето добавляю и проверяю: $device")
                device?.let {
                    viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(
                            devices = _uiState.value.devices + it
                        )
                    }
                }
            }

            override fun onDeviceUpdated(manager: DiscoveryManager?, device: ConnectableDevice?) {

            }

            override fun onDeviceRemoved(manager: DiscoveryManager?, device: ConnectableDevice?) {
                device?.let {
                    viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(
                            devices = _uiState.value.devices - it
                        )
                    }
                }
            }

            override fun onDiscoveryFailed(
                manager: DiscoveryManager?,
                error: ServiceCommandError?
            ) {
                viewModelScope.launch {
                    val errorMessage = "Discovery failed: ${error?.message}, Code: ${error?.code}"
                    _uiState.value = _uiState.value.copy(error = errorMessage)
                    Log.e("MyLog", errorMessage)
                }
            }
        })
    }

    suspend fun connectToDevice(device: ConnectableDevice) = withContext(Dispatchers.IO) {
        currentDevice = device
        device.connect()
        Log.d("MyLog", "${device.capabilities}")
    }

    fun volumeUp() {
        currentDevice?.getCapability(VolumeControl::class.java)?.volumeUp(object : ResponseListener<Any> {
            override fun onError(error: ServiceCommandError?) {
                Log.e("MyLog", error?.message.toString())
            }

            override fun onSuccess(`object`: Any?) {
                Log.d("MyLog", "Volume up success")
            }

        })
    }

    fun volumeDown() {
        currentDevice?.getCapability(VolumeControl::class.java)?.volumeDown(object : ResponseListener<Any> {
            override fun onError(error: ServiceCommandError?) {
                Log.e("MyLog", error?.message.toString())
            }

            override fun onSuccess(`object`: Any?) {
                Log.d("MyLog", "Volume down success")
            }

        })
    }

    fun getCapabilities(): List<String> {
        return currentDevice?.capabilities ?: listOf()
    }

/*    fun switchChannelUp() {
        currentDevice?.getCapability(TVControl::class.java)?.channelUp(object : ResponseListener<Any> {
            override fun onError(error: ServiceCommandError?) {
                Log.e("MyLog", error?.message.toString())
            }

            override fun onSuccess(`object`: Any?) {
                Log.d("MyLog", "work")
            }

        })
    }

    fun switchChannelDown() {
        currentDevice?.getCapability(TVControl::class.java)?.channelDown(object : ResponseListener<Any> {
            override fun onError(error: ServiceCommandError?) {
                Log.e("MyLog", error?.message.toString())
            }

            override fun onSuccess(`object`: Any?) {

            }

        })
    }*/

    override fun onCleared() {
        super.onCleared()
        discoveryManager?.stop()
    }
}

data class TVControlState(
    val devices: List<ConnectableDevice> = emptyList(),
    val error: String? = null
)