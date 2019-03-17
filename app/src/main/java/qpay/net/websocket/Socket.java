package qpay.net.websocket;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.ws.RealWebSocket;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;

/**
 * Websocket class based on OkHttp3 with {event->data} message format to make your life easier.
 *
 * @author Ali Yusuf
 * @since 3/13/17
 */

/*
public class Socket {

    private final static String TAG = Socket.class.getSimpleName();
    private final static String CLOSE_REASON = "End of session";
    private final static int MAX_COLLISION = 7;

    public final static String EVENT_OPEN = "open";
    public final static String EVENT_RECONNECT_ATTEMPT = "reconnecting";
    public final static String EVENT_CLOSED = "closed";

    */
/**
     * Main socket states
     *//*

    public enum State {
        CLOSED, CLOSING, CONNECT_ERROR, RECONNECT_ATTEMPT, RECONNECTING, OPENING, OPEN
    }

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.HEADERS : HttpLoggingInterceptor.Level.NONE);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                    .addInterceptor(logging);

    private static int pingAttempts = 0;
    private static int pingRemainingAttempts = 0;
    private Timer timer = new Timer();
    public static class Builder {

        private Request.Builder request;

        private Builder(Request.Builder request) {
            this.request = request;
        }

        public static Builder with(@NonNull String url) {
            // Silently replace web socket URLs with HTTP URLs.
            if (!url.regionMatches(true, 0, "ws:", 0, 3) && !url.regionMatches(true, 0, "wss:", 0, 4))
                throw new IllegalArgumentException("web socket url must start with ws or wss, passed url is " + url);

            return new Builder(new Request.Builder().url(url));
        }

        public Builder setPingInterval(long interval, @NonNull TimeUnit unit) {
            httpClient.pingInterval(interval, unit);
            return this;
        }

        public Builder addHeader(@NonNull String name, @NonNull String value) {
            request.addHeader(name, value);
            return this;
        }

        public Socket build() {
            return new Socket(request.build());
        }
    }

    */
/**
     * Websocket state
     *//*

    private static State state;
    */
/**
     * Websocket main request
     *//*

    private static Request request;
    */
/**
     * Websocket connection
     *//*

    private static RealWebSocket realWebSocket;
    */
/**
     * Reconnection post delayed handler
     *//*

    private static Handler delayedReconnection;
    */
/**
     * Websocket events listeners
     *//*

    private static Map<String, OnEventListener> eventListener;
    */
/**
     * Websocket events new message listeners
     *//*

    private static Map<String, OnEventResponseListener> eventResponseListener;
    */
/**
     * Message list tobe send onEvent open {@link State#OPEN} connection state
     *//*

    private static Map<String, String> onOpenMessageQueue = new HashMap<>();
    */
/**
     * Websocket state change listener
     *//*

    private static OnStateChangeListener onChangeStateListener;
    */
/**
     * Websocket new message listener
     *//*

    private static OnMessageListener messageListener;
    */
/**
     * Number of reconnection attempts
     *//*

    private static int reconnectionAttempts;
    private static boolean skipOnFailure;

    private Socket(Request request) {
        Socket.request = request;
        state = State.CLOSED;
        eventListener = new HashMap<>();
        eventResponseListener = new HashMap<>();
        delayedReconnection = new Handler(Looper.getMainLooper());
        skipOnFailure = false;
    }

    */
/**
     * Start socket connection if i's not already started
     *//*

    public Socket connect() {
        if (httpClient == null) {
            throw new IllegalStateException("Make sure to use Socket.Builder before using Socket#connect.");
        }
        if (realWebSocket == null) {
            realWebSocket = (RealWebSocket) httpClient.build().newWebSocket(request, webSocketListener);
            changeState(State.OPENING);
        } else if (state == State.CLOSED) {
            realWebSocket.connect(httpClient.build());
            changeState(State.OPENING);
        }
        return this;
    }

    */
/**
     * Set listener which fired every time message received with contained data.
     *
     * @param listener message on arrive listener
     *//*

    public Socket onEvent(@NonNull String event, @NonNull OnEventListener listener) {
        eventListener.put(event, listener);
        return this;
    }

    */
/**
     * Set listener which fired every time message received with contained data.
     *
     * @param listener message on arrive listener
     *//*

    public Socket onEventResponse(@NonNull String event, @NonNull OnEventResponseListener listener) {
        eventResponseListener.put(event, listener);
        return this;
    }

    */
/**
     * Send message in {event->data} format
     *
     * @param event event name that you want sent message to
     * @param data  message data in JSON format
     * @return true if the message send/on socket send quest; false otherwise
     *//*

    public boolean send(@NonNull String event, @NonNull String data) {
        try {
            JSONObject text = new JSONObject();
            JSONObject topic = new JSONObject();
            topic.put("topic", event);
            topic.put("event", "message");
            topic.put("data", new JSONObject(data));
            text.put("t", 7);
            text.put("d", topic);
            Log.v(TAG, "Try to send data " + text.toString());

            return realWebSocket.send(text.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Try to send data with wrong JSON format, data: " + data);
        }
        return false;
    }

    public boolean join(@NonNull String topic) {
        try {
            JSONObject text = new JSONObject();
            JSONObject topics = new JSONObject();
            topics.put("topic", topic);
            text.put("t", 1);
            text.put("d", topics);
            Log.v(TAG, "Try to send data " + text.toString());

            return realWebSocket.send(text.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Try to send data with wrong JSON format, data: " + e);
        }
        return false;
    }

    public void ping(long pingInterval) {
//        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(Socket.pingRemainingAttempts > 0){
                    try {
                        JSONObject text = new JSONObject();
                        text.put("t", 8);
                        Log.v(TAG, "Try to send data " + text.toString());
                        realWebSocket.send(text.toString());
                        Socket.pingRemainingAttempts--;
                    } catch (JSONException e) {
                        Log.e(TAG, "Try to send data with wrong JSON format, data: " + e);
                    }
                }
            }
        }, 0, pingInterval);
    }

    */
/**
     * Set state listener which fired every time {@link Socket#state} changed.
     *
     * @param listener state change listener
     *//*

    public Socket setOnChangeStateListener(@NonNull OnStateChangeListener listener) {
        onChangeStateListener = listener;
        return this;
    }

    */
/**
     * Message listener will be called in any message received even if it's not
     * in a {event -> data} format.
     *
     * @param listener message listener
     *//*

    public Socket setMessageListener(@NonNull OnMessageListener listener) {
        messageListener = listener;
        return this;
    }

    public void removeEventListener(@NonNull String event) {
        eventListener.remove(event);
        onOpenMessageQueue.remove(event);
    }

    */
/**
     * Clear all socket listeners in one line
     *//*

    public void clearListeners() {
        eventListener.clear();
        messageListener = null;
        onChangeStateListener = null;
    }

    */
/**
     * Send normal close request to the host
     *//*

    public void close() {
        if (realWebSocket != null) {
            realWebSocket.close(1000, CLOSE_REASON);
        }
    }

    */
/**
     * Send close request to the host
     *//*

    public void close(int code, @NonNull String reason) {
        if (realWebSocket != null) {
            realWebSocket.close(code, reason);
        }
    }

    */
/**
     * Terminate the socket connection permanently
     *//*

    public void terminate() {
        skipOnFailure = true; // skip onFailure callback
        if (realWebSocket != null) {
            realWebSocket.cancel(); // close connection
            realWebSocket = null; // clear socket object
        }
    }

    */
/**
     * Add message in a queue if the socket not open and send them
     * if the socket opened
     *
     * @param event event name that you want sent message to
     * @param data  message data in JSON format
     *//*

    public void sendOnOpen(@NonNull String event, @NonNull String data) {
        if (state != State.OPEN)
            onOpenMessageQueue.put(event, data);
        else
            send(event, data);
    }

    */
/**
     * Retrieve current socket connection state {@link State}
     *//*

    public State getState() {
        return state;
    }

    */
/**
     * Change current state and call listener method with new state
     * {@link OnStateChangeListener#onChange(Socket, State)}
     *
     * @param newState new state
     *//*

    private void changeState(State newState) {
        state = newState;
        if (onChangeStateListener != null) {
            onChangeStateListener.onChange(Socket.this, state);
        }
    }

    */
/**
     * Try to reconnect to the websocket after delay time using <i>Exponential backoff</i> method.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Exponential_backoff"></a>
     *//*

    private void reconnect() {
        if (state != State.CONNECT_ERROR) // connection not closed !!
            return;

        changeState(State.RECONNECT_ATTEMPT);

        if (realWebSocket != null) {
            // Cancel websocket connection
            realWebSocket.cancel();
            // Clear websocket object
            realWebSocket = null;
        }

        if (eventListener.get(EVENT_RECONNECT_ATTEMPT) != null) {
            eventListener.get(EVENT_RECONNECT_ATTEMPT).onMessage(Socket.this, EVENT_RECONNECT_ATTEMPT);
        }

        // Calculate delay time
        int collision = reconnectionAttempts > MAX_COLLISION ? MAX_COLLISION : reconnectionAttempts;
        long delayTime = Math.round((Math.pow(2, collision) - 1) / 2) * 1000;

        // Remove any pending posts of callbacks
        delayedReconnection.removeCallbacksAndMessages(null);
        // Start new post delay
        delayedReconnection.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeState(State.RECONNECTING);
                reconnectionAttempts++; // Increment connections attempts
                connect(); // Establish new connection
            }
        }, delayTime);
    }

    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.v(TAG, "Socket has been opened successfully.");
            // reset connections attempts counter
            reconnectionAttempts = 0;

            // fire open event listener
            if (eventListener.get(EVENT_OPEN) != null) {
                eventListener.get(EVENT_OPEN).onMessage(Socket.this, EVENT_OPEN);
            }

            // Send data in queue
            for (String event : onOpenMessageQueue.keySet()) {
                send(event, onOpenMessageQueue.get(event));
            }
            // clear queue
            onOpenMessageQueue.clear();

            changeState(State.OPEN);
        }

        */
/**
         * Accept only Json data with format:
         * <b> {"event":"event name","data":{some data ...}} </b>
         *//*

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            // print received message in log
            Log.v(TAG, "New Message received " + text);

            try {
                JSONObject object = new JSONObject(text);
                int type = object.optInt("t");

                switch (type) {
                    case 0: {
                        JSONObject d = object.optJSONObject("d");
                        final long pingInterval = d.optLong("clientInterval");
                        Socket.pingAttempts = d.optInt("clientAttempts");
                        Log.d(TAG, "clientInterval" + pingInterval);
                        ping(pingInterval);
                    }
                    case 9: {
                        Socket.pingRemainingAttempts = Socket.pingAttempts;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // call message listener
            if (messageListener != null)
                messageListener.onMessage(Socket.this, text);

//            try {
//                // Parse message text
//                JSONObject response = new JSONObject(text);
//                String event = response.getString("event");
//                JSONObject data = response.getJSONObject("data");
//
//                // call event listener with received data
//                if (eventResponseListener.get(event) != null) {
//                    eventResponseListener.get(event).onMessage(Socket.this, event, data);
//                }
//                // call event listener
//                if (eventListener.get(event) != null) {
//                    eventListener.get(event).onMessage(Socket.this, event);
//                }
//            } catch (
//                    JSONException e) {
//                // Message text not in JSON format or don't have {event}|{data} object
//                Log.e(TAG, "Unknown message format.");
//            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            // TODO: some action
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.v(TAG, "Close request from server with reason '" + reason + "'");
            changeState(State.CLOSING);
            webSocket.close(1000, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.v(TAG, "Socket connection closed with reason '" + reason + "'");
            changeState(State.CLOSED);
            if (eventListener.get(EVENT_CLOSED) != null) {
                eventListener.get(EVENT_CLOSED).onMessage(Socket.this, EVENT_CLOSED);
            }
        }

        */
/**
         * This method call if:
         * - Fail to verify websocket GET request  => Throwable {@link ProtocolException}
         * - Can't establish websocket connection after upgrade GET request => response null, Throwable {@link Exception}
         * - First GET request had been failed => response null, Throwable {@link java.io.IOException}
         * - Fail to send Ping => response null, Throwable {@link java.io.IOException}
         * - Fail to send data frame => response null, Throwable {@link java.io.IOException}
         * - Fail to read data frame => response null, Throwable {@link java.io.IOException}
         *//*

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            if (!skipOnFailure) {
                skipOnFailure = false; // reset flag
                Log.v(TAG, "Socket connection fail, try to reconnect. (" + reconnectionAttempts + ")");
                changeState(State.CONNECT_ERROR);
                reconnect();
            }
        }
    };

    public abstract static class OnMessageListener {
        public abstract void onMessage(String data);

        */
/**
         * Method called from socket to execute listener implemented in
         * {@link #onMessage(String)} on main thread
         *
         * @param socket Socket that receive the message
         * @param data   Data string received
         *//*

        private void onMessage(Socket socket, final String data) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onMessage(data);
                }
            });
        }
    }

    public abstract static class OnEventListener {
        public abstract void onMessage(String event);

        private void onMessage(Socket socket, final String event) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onMessage(event);
                }
            });
        }
    }

    public abstract static class OnEventResponseListener extends OnEventListener {
        */
/**
         * Method need to override in listener usage
         *//*

        public abstract void onMessage(String event, String data);

        */
/**
         * Just override the inherited method
         *//*

        @Override
        public void onMessage(String event) {
        }

        */
/**
         * Method called from socket to execute listener implemented in
         * {@link #onMessage(String, String)} on main thread
         *
         * @param socket Socket that receive the message
         * @param event  Message received event
         * @param data   Data received in the message
         *//*

        private void onMessage(Socket socket, final String event, final JSONObject data) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onMessage(event, data.toString());
                    onMessage(event);
                }
            });
        }
    }

    public abstract static class OnStateChangeListener {
        */
/**
         * Method need to override in listener usage
         *//*

        public abstract void onChange(State status);

        */
/**
         * Method called from socket to execute listener implemented in
         * {@link #onChange(State)} on main thread
         *
         * @param socket Socket that receive the message
         * @param status new status
         *//*

        private void onChange(Socket socket, final State status) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onChange(status);
                }
            });
        }
    }

}*/
