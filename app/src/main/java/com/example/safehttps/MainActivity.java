package com.example.safehttps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "[+]MainActivity";
    public static SSLContext sslContext = null;
    X509TrustManager trustManager = null;

    TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendRequest = (Button) findViewById(R.id.button1);
        responseText = (TextView) findViewById(R.id.request_text);
        sendRequest.setOnClickListener(this);
    }

        @Override
        public void onClick(View view){
            if (view.getId() == R.id.button1){
                sendRequestWithOkHttp();
            }
        }


        private void sendRequestWithOkHttp(){
            new Thread(new Runnable() {
                @Override
                public void run(){

                    try {
                        //利用代码校验证书的公钥hash
//                        String hostname = "www.gohosts.com";
//                        CertificatePinner certificatePinner = new CertificatePinner.Builder()
//                                .add(hostname, "sha256/7VMdvZE3PGbxb0Pgf1PlCp+MI8KZ2ZC5psM8TIylNDA=")
//                                .build();
//                        OkHttpClient client = new OkHttpClient.Builder()
//                                .certificatePinner(certificatePinner)
//                                .hostnameVerifier(new HostnameVerifier() {
//                                    @Override
//                                    public boolean verify(String hostname, SSLSession session) {
//                                        //强行返回true 即验证成功
//                                        return true;
//                                    }
//                                }).build();

                        //正常okhttp https请求
//                        OkHttpClient client = new OkHttpClient().newBuilder().hostnameVerifier(new HostnameVerifier() {
//                            @Override
//                            public boolean verify(String hostname, SSLSession session) {
//                                //强行返回true 即验证成功
//                                return true;
//                            }
//                        }).build();


                        //利用代码校验证书的公钥证书文件
//                        // 获取证书输入流
//                        InputStream openRawResource = getApplicationContext().getResources().openRawResource(R.raw.ttt); //R.raw.bing是bing.com的正确证书，R.raw.bing2_so是hostname=bing.com的so.com的证书，可视为用作测试的虚假bing.com证书
//                        Certificate ca = CertificateFactory.getInstance("X.509").generateCertificate(openRawResource);
//                        // 创建 Keystore 包含我们的证书
//                        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//                        keyStore.load(null, null);
//                        keyStore.setCertificateEntry("ca", ca);
//                        // 创建一个 TrustManager 仅把 Keystore 中的证书 作为信任的锚点
//                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); // 建议不要使用自己实现的X509TrustManager，而是使用默认的X509TrustManager
//                        trustManagerFactory.init(keyStore);
//                        // 用 TrustManager 初始化一个 SSLContext
//                        sslContext = SSLContext.getInstance("TLS");  //定义：public static SSLContext sslContext = null;
//                        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//
//                        OkHttpClient client = new OkHttpClient.Builder()
//                                .sslSocketFactory(sslContext.getSocketFactory(),
//                                        (X509TrustManager) trustManagerFactory.getTrustManagers()[0] )
//                                .hostnameVerifier(new HostnameVerifier() {
//                                    @Override
//                                    public boolean verify(String hostname, SSLSession session) {
//                                        //强行返回true 即验证成功
//                                        return true;
//                                    }
//                                }).build();

                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

                        trustManagerFactory.init((KeyStore) null);
                        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                        }
                        trustManager = (X509TrustManager) trustManagers[0];

                        OkHttpClient client = new OkHttpClient.Builder()
                                .sslSocketFactory(Objects.requireNonNull(ClientSSLSocketFactory.getSocketFactory(getApplicationContext())), Objects.requireNonNull(trustManager))
                                .hostnameVerifier(new HostnameVerifier() {
                                    @Override
                                    public boolean verify(String hostname, SSLSession session) {
                                        //强行返回true 即验证成功
                                        return true;
                                    }
                        }).build();


                        Request request = new Request.Builder()
                                .url("https://ttt.com/")
                                .build();


                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Log.d(TAG, "I am running! ");
                        showResponse(responseData);   //直接显示在主活动页面
 
                         
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }}).start();
        }




    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });
    }



}