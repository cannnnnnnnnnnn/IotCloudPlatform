package com.example.IotCloudPlatform.tools;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class WebServiceHelper {

    public interface Callback {
        void call(String s);
    }

    public static void SaveInfo(final String msg) {
        new Thread() {
            public void run() {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                SoapObject rpc = new SoapObject("https://localhost:44368/WebService1.asmx", "SaveInfo");
                rpc.addProperty("info", msg);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                HttpTransportSE transportSE = new HttpTransportSE("https://localhost:44368/WebService1.asmx?wsdl");
                try {
                    transportSE.call("https://localhost:44368/WebService1.asmx/GetInfo", envelope);
                    SoapObject soapObject = (SoapObject) envelope.bodyIn;
                    String result = soapObject.getProperty(0).toString();
                    Log.i("result", result);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    public static void GetInfo(final Callback callback) {
        new Thread() {
            @Override
            public void run() {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                SoapObject rpc = new SoapObject("https://localhost:44368/WebService1.asmx", "GetInfo");
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                HttpTransportSE transportSE = new HttpTransportSE("https://localhost:44368/WebService1.asmx?wsdl");
                try {
                    transportSE.call("https://localhost:44368/WebService1.asmx/GetInfo", envelope);
                    SoapObject object = new SoapObject();
                    object = (SoapObject) envelope.bodyIn;
                    String result = object.getProperty(0).toString();
                    Log.i("result", result);
                    callback.call(result);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}