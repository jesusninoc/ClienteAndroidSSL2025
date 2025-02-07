package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.net.SocketException
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ClientViewModel(this)

        setContent {
            SecureClientApp(viewModel)
        }
    }
}

@Composable
fun SecureClientApp(viewModel: ClientViewModel) {
    var response by remember { mutableStateOf("Esperando respuesta del servidor...") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = response, style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.sendMessage { serverResponse ->
                response = serverResponse
            }
        }) {
            Text("Enviar Mensaje al Servidor")
        }
    }
}

class ClientViewModel(private val context: Context) : ViewModel() {

    fun sendMessage(onResponseReceived: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var cliente: SSLSocket? = null
            var flujoEntrada: DataInputStream? = null
            var flujoSalida: DataOutputStream? = null
            var socketCerrado = false  // Bandera para evitar intentos de cierre duplicados

            try {
                val host = "192.168.0.38"
                val puerto = 5556

                val sslSocketFactory = createSSLSocketFactory(context)
                cliente = sslSocketFactory.createSocket(host, puerto) as SSLSocket

                cliente.soTimeout = 10000

                try {
                    cliente.startHandshake()
                    Log.d("SSLClient", "Handshake SSL completado correctamente")
                } catch (e: Exception) {
                    Log.e("SSLClient", "Error en el handshake SSL/TLS: ${e.message}", e)
                    onResponseReceived("Error en el handshake SSL/TLS: ${e.message}")
                    return@launch
                }

                flujoSalida = DataOutputStream(cliente.getOutputStream())
                flujoEntrada = DataInputStream(cliente.getInputStream())

                flujoSalida.writeUTF("Saludos al SERVIDOR DESDE EL CLIENTE")
                flujoSalida.flush()
                Log.d("SSLClient", "Mensaje enviado al servidor, esperando respuesta...")

                try {
                    val serverResponse =
                        flujoEntrada.readUTF()  // Esperar la respuesta del servidor
                    Log.d("SSLClient", "Respuesta recibida: $serverResponse")

                    // Actualizar la UI con la respuesta
                    onResponseReceived(serverResponse)

                } catch (e: Exception) {
                    Log.e("SSLClient", "Error al leer la respuesta: ${e.message}", e)
                    onResponseReceived("Error al leer la respuesta: ${e.message}")
                } finally {
                    // Cerrar los flujos y el socket solo si aún no están cerrados
                    if (!socketCerrado) {
                        try {
                            flujoEntrada?.close()
                            flujoSalida?.close()

                            if (!cliente.isClosed) {
                                cliente.close()
                                socketCerrado = true  // Marcar el socket como cerrado
                                Log.d("SSLClient", "Socket cerrado correctamente.")
                            }
                        } catch (e: SocketException) {
                            // No mostrar advertencia innecesaria
                        } catch (e: Exception) {
                            Log.e(
                                "SSLClient",
                                "Error inesperado al cerrar el socket: ${e.message}",
                                e
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("SSLClient", "Error en la conexión: ${e.message}", e)
                onResponseReceived("Error en la conexión: ${e.message}")
            }
        }
    }


    private fun createSSLSocketFactory(context: Context): SSLSocketFactory {
        try {
            // Cargar el certificado directamente desde `res/raw/`
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certInputStream: InputStream =
                context.resources.openRawResource(R.raw.servidor_publico)
            val certificado: Certificate = certificateFactory.generateCertificate(certInputStream)
            certInputStream.close()

            // Crear un KeyStore y agregar el certificado del servidor
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null) // Inicializar un KeyStore vacío
            keyStore.setCertificateEntry("servidor", certificado)

            // Crear el TrustManager con el certificado
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)

            // Configurar SSLContext con TLS 1.3
            val sslContext = SSLContext.getInstance("TLSv1.3")
            sslContext.init(null, trustManagerFactory.trustManagers, null)

            return sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException("❌ Error al cargar el certificado en Android: ${e.message}", e)
        }
    }
}
