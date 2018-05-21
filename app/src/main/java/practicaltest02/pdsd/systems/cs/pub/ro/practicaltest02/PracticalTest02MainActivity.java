package practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.CharBuffer;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private Button mButtonStart;
    private EditText mEditPort;
    private Button mButtonSend;
    private EditText mEditURL;
    private ServerURL serverThread;
    private TextView textView;
    private int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        mButtonStart = findViewById(R.id.start);
        mEditPort   = findViewById(R.id.port);
        textView = findViewById(R.id.result_text);
        mButtonSend = findViewById(R.id.send);
        mEditURL = findViewById(R.id.url);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        mButtonStart.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String serverPort = mEditPort.getText().toString();
                        if (serverPort == null || serverPort.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        port = Integer.parseInt(serverPort);
                        serverThread = new ServerURL(port);


                        AsyncTask.execute(serverThread);

                        if (serverThread.getServerSocket() == null) {
                            Log.e("MAIN ACTIVITY", "[MAIN ACTIVITY] Could not create server thread!");
                            return;
                        }
                    }
                });

        mButtonSend.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String url = mEditURL.getText().toString();
//                        SomeThread st = new SomeThread(url, port, textView);
//                        AsyncTask.execute(st);
                        try {
                            Socket socket = new Socket("127.0.0.1", port);
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                            PrintWriter printWriter = new PrintWriter(bufferedOutputStream, true);
                            printWriter.write(url+"\n");
                            printWriter.flush();
                            InputStream socketInputStream = socket.getInputStream();
                            BufferedReader in = new BufferedReader(new InputStreamReader(socketInputStream));
                            String line;
                            line = in.readLine();
                            StringBuilder sb = new StringBuilder();
                            sb.append(line);
                            while ((line = in.readLine()) != null) {
                                sb.append(line);
                            }
//                            char[] cbuff ;
//                            in.read(cbuff)
                            textView.setText(sb.toString());
                            Log.d("Got result", sb.toString());
                            socket.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            serverThread.getServerSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


