package qpay.net.websocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import np.com.blackspring.adoniswebsocketclient.Socket;


public class MainActivity extends AppCompatActivity {
    private Button start;
    private TextView output;
    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        output = findViewById(R.id.output);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.join("chat");

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", "mobile");
                    jsonObject.put("body", "from mobile");
                    socket.send("chat", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void start() throws JSONException {

        socket = Socket.Builder.with("ws://68.183.232.199/adonis-ws").build();
        socket.connect();
        socket.onEvent(Socket.EVENT_OPEN, new Socket.OnEventListener() {
            @Override
            public void onMessage(String event) {
                output("sonnected");
            }
        });
        socket.onEventResponse("chat", new Socket.OnEventResponseListener() {
            @Override
            public void onMessage(String event, String data) {
                output(data);
            }
        });

        socket.onEvent(Socket.EVENT_RECONNECT_ATTEMPT, new Socket.OnEventListener() {
            @Override
            public void onMessage(String event) {
                output("reconnecting");

            }
        });


//        socket.sendOnOpen("Some event", "{"some data":"in JSON format"}");
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }

}
