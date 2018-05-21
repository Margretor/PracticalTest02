package practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by student on 21.05.2018.
 */

public class SomeThread implements Runnable {

    String url;
    int port;
    TextView textView;

    public SomeThread(String url, int port, TextView textView) {
        this.url = url;
        this.port = port;
        this.textView = textView;
    }

    @Override
    public void run() {
        Log.v("URL", url);
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
            textView.setText(sb.toString());
            Log.d("Got result", sb.toString());
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
