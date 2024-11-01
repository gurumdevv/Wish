package com.gurumlab.wish.util

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.gurumlab.wish.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class MessagingUtil(private val context: Context) {
    /**
     * Retrieve a valid access token that can be use to authorize requests to the FCM REST API.
     *
     * @return Access token.
     */
    private suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        context.assets.open("service-account.json").use { serviceAccount ->
            val googleCredentials = GoogleCredentials
                .fromStream(serviceAccount)
                .createScoped(SCOPES)
            googleCredentials.refreshAccessToken().tokenValue
        }
    }

    /**
     * Create HttpURLConnection that can be used for both retrieving and publishing.
     *
     * @return Base HttpURLConnection.
     * @throws IOException
     */
    private suspend fun getConnection(): HttpURLConnection =
        withContext(Dispatchers.IO) {
            val url = URL(BASE_URL + FCM_SEND_ENDPOINT)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setRequestProperty(
                "Authorization",
                "Bearer ${getAccessToken()}"
            )
            httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8")
            httpURLConnection
        }

    /**
     * Send request to FCM message using HTTP.
     * Encoded with UTF-8 and support special characters.
     *
     * @param fcmMessage Body of the HTTP request.
     * @throws IOException
     */
    private suspend fun sendMessage(fcmMessage: JsonObject) =
        withContext(Dispatchers.IO) {
            val connection = getConnection()
            connection.doOutput = true
            connection.outputStream.use { outputStream ->
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    writer.write(fcmMessage.toString())
                    writer.flush()
                }
            }

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("Message sent to Firebase for delivery, response:")
                println(response)
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() }
                println("Unable to send message to Firebase:")
                println(errorResponse)
            }
        }

    /**
     * Send notification message to FCM for delivery to registered devices.
     *
     * @throws IOException
     */
    suspend fun sendCommonMessage(
        title: String,
        body: String,
        othersFcmToken: String
    ) =
        withContext(Dispatchers.IO) {
            val notificationMessage = buildNotificationMessage(title, body, othersFcmToken)
            println("FCM request body for message using common notification object:")
            prettyPrint(notificationMessage)
            sendMessage(notificationMessage)
        }

    /**
     * Construct the body of a notification message request.
     *
     * @return JSON of notification message.
     */
    private fun buildNotificationMessage(
        title: String,
        body: String,
        othersFcmToken: String
    ): JsonObject {
        val jNotification = JsonObject().apply {
            addProperty("title", title)
            addProperty("body", body)
        }

        val jMessage = JsonObject().apply {
            add("notification", jNotification)
            addProperty("token", othersFcmToken)
        }

        return JsonObject().apply {
            add(MESSAGE_KEY, jMessage)
        }
    }

    /**
     * Pretty print a JsonObject.
     *
     * @param jsonObject JsonObject to pretty print.
     */
    private fun prettyPrint(jsonObject: JsonObject) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        println(gson.toJson(jsonObject) + "\n")
    }

    companion object {
        private const val PROJECT_ID = BuildConfig.PROJECT_ID
        private const val BASE_URL = "https://fcm.googleapis.com"
        private const val FCM_SEND_ENDPOINT = "/v1/projects/$PROJECT_ID/messages:send"
        private const val MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
        private val SCOPES = listOf(MESSAGING_SCOPE)
        private const val MESSAGE_KEY = "message"
    }
}