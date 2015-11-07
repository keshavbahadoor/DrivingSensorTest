package keshav.com.restservice;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Keshav on 10/31/2015.
 */
public class SSLSocketFactoryService {
    private static final String TAG = "SSL_FACTORY_SERVICE";

    /**
     * Performs SSL Handshake process using Keystore and TrustManager.
     * Read: http://developer.android.com/training/articles/security-ssl.html#HttpsExample
     */
    public static SSLSocketFactory createSSLSocketFactory(Context context) {

        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            //InputStream caInput = new BufferedInputStream(new FileInputStream("assets/cert/apache.crt"));
            //caInput = this.context.getAssets().open("certs/apache.crt");

            InputStream caInput = context.getResources().getAssets().open("certs/apache.crt");
            //InputStream caInput = new BufferedInputStream(is);

            Certificate ca;
            ca = cf.generateCertificate(caInput);
            caInput.close();

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance( keyStoreType );
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslCcontext = SSLContext.getInstance("TLS");
            sslCcontext.init(null, tmf.getTrustManagers(), null);
            return sslCcontext.getSocketFactory();

        }
        catch (Exception ex) {
            Log.d(TAG, "Error occurred during SSL Handshake ");
            Log.d(TAG, "Error: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Creates an SSL Socket Factory that trusts all connections.
     * Note that this is un-secure and should be used only when debugging or testing.
     * @return
     */
    public static SSLSocketFactory createSSLSocketFactoryTrustAll(){
        try {
            TrustManager[] byPassTrustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance( "TLS" );
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            return sslContext.getSocketFactory();
        }
        catch (Exception ex) {
            Log.d(TAG, "Error occured when creating a Trust All SSL Socket Factory");
            Log.d( TAG, "Error: " + ex.getMessage() );
            return null;
        }
    }
}
