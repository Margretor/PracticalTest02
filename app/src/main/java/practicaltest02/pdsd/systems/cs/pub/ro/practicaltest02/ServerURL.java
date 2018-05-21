package practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02;

import android.provider.SyncStateContract;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by student on 21.05.2018.
 */

public class ServerURL implements Runnable {

    private int port;
    private ServerSocket serverSocket;
    private HashMap<String, String> proxy = new HashMap<>();

    public ServerURL(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            Log.d("Server creation", serverSocket.toString());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
        while (!Thread.currentThread().isInterrupted()) {
                Log.i("Server", "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i("Server", "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                InputStream socketInputStream = socket.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(socketInputStream));
                String urlRequest = in.readLine();
                String result = null;
                Log.d("Server", urlRequest);
                if (proxy.containsKey(urlRequest)){
                    result = proxy.get(urlRequest);
                } else {
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(urlRequest);
                        HttpResponse httpGetResponse = httpClient.execute(httpGet);
                        HttpEntity httpGetEntity = httpGetResponse.getEntity();
                        if (httpGetEntity != null) {
                            result = EntityUtils.toString(httpGetEntity);
                            Log.i("Server URL", result);
                        }
                    } catch (Exception exception) {
                        Log.e("Server URL", exception.getMessage());
                        exception.printStackTrace();

                    }
                }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            PrintWriter printWriter = new PrintWriter(bufferedOutputStream, false);
            printWriter.write(result);
            printWriter.flush();
            }
        } catch (Exception e) {
            Log.e("Server", "[SERVER THREAD] An exception has occurred: " + e.getMessage());
        }
    }

    public ServerSocket getServerSocket(){
        return this.serverSocket;
    }
}
