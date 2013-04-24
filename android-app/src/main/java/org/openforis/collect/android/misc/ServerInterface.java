package org.openforis.collect.android.misc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class ServerInterface {

        public static final String SERVER_URL = "http://ar5.arbonaut.com/webforest/fao-mobile/save-received-data-file";

        public static String sendDataFiles(String xml) {
                return postSyncXML(xml);
        }

        /*private static String executeHttpRequest(String data) {
        		Log.e("executeHttpRequest","=="+data);
                String result = "";
                try {
                        URL url = new URL(SERVER_URL);
                        URLConnection connection = url.openConnection();
  
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setUseCaches(false);
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                        // Send the POST data
                        DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                        dataOut.writeBytes(data);
                        dataOut.flush();
                        dataOut.close();

                        // get the response from the server and store it in result
                        DataInputStream dataIn = new DataInputStream(connection.getInputStream());
                        String inputLine;
                        while ((inputLine = dataIn.readLine()) != null) {
                                result += inputLine;
                        }
                        dataIn.close();
                } catch (IOException e) {
                        e.printStackTrace();
                        result = null;
                }

                return result;
        }*/
        
        private static String postSyncXML(String xml) {
            String url = "http://ar5.arbonaut.com/webforest/fao-mobile/save-received-data-file";
            HttpClient httpclient = new DefaultHttpClient();  
            /* String encode_url=URLEncoder.encode(url,"UTF-8");
         	String decode_url=URLDecoder.decode(encode_url,"UTF-8");*/
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("datafile_xml_string",xml));
            nameValuePairs.add(new BasicNameValuePair("survey_id","99"));
            nameValuePairs.add(new BasicNameValuePair("username","collect"));
            nameValuePairs.add(new BasicNameValuePair("overwrite","true"));

            UrlEncodedFormEntity form;
            try {
                form = new UrlEncodedFormEntity(nameValuePairs);
                        form.setContentEncoding(HTTP.UTF_8);
                HttpPost httppost = new HttpPost(url);

                httppost.setEntity(form);

                HttpResponse response = (HttpResponse) httpclient .execute(httppost);
                HttpEntity resEntity = response.getEntity();  
                String resp = EntityUtils.toString(resEntity);
                /*try
                {
                    final String s = new String(resp.getBytes(), "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    Log.e("utf8", "conversion", e);
                }*/
                return resp;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }        
}
