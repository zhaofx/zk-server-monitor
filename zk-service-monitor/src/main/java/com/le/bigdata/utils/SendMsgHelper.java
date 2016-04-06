package com.le.bigdata.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/* ====  发送短信工具类 ，支持中文 ======= */
public class SendMsgHelper {
    String phoneNums = "";
    String msg = "";

    public SendMsgHelper(String phoneNums, String msg) {
        this.phoneNums = phoneNums;//多个号码 以逗号分隔
        this.msg = msg;
    }

    public String sendPhoneMsg(String number, String msg) throws Exception {
        {
            String urll = "http://115.182.51.124:7070/thirdPartner/letvqxtmt?srcAddr=10690228102930&corpID=800035&destAddr=";
            msg = new String(msg.getBytes(), "utf-8");
            msg = java.net.URLEncoder.encode(msg, "gbk");
            String destUrl = urll + number + "&msg=" + msg;
            System.out.println("send message is  = " + msg);
            System.out.println("destUrl  = " + destUrl);

            BufferedWriter output = null;
            InputStream input = null;
            String responseMsg = null;
            try {
                /*发送短信*/
                URL url = new URL(destUrl);
                HttpURLConnection conn;
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("content-type", "text/xml charset=utf-8");
                conn.setRequestProperty("Accept-Charset", "utf-8");
                conn.connect();
                output = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
                output.write(msg);

                /*读取响应*/
                input = conn.getInputStream();
                byte[] response = new byte[input.available()];
                int len = input.read(response);
                responseMsg = new String(response, "utf-8");
                System.out.println("Response:" + responseMsg);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                if(input !=null){
                    input.close();
                }
            }
            return responseMsg;
        }
    }

    public String sendPhoneMsg() throws Exception {
        String urll = "http://115.182.51.124:7070/thirdPartner/letvqxtmt?srcAddr=10690228102930&corpID=800035&destAddr=";
        String backstring = null;
        msg = new String(msg.getBytes(), "utf-8");
        msg = java.net.URLEncoder.encode(msg, "gbk");
        for (String number : phoneNums.split(",")) {
            String destUrl = urll + number + "&msg=" + msg;
            System.out.println("send message is  = " + msg);
            System.out.println("destUrl  = " + destUrl);
            URL url = new URL(destUrl);

            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "text/xml charset=utf-8");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.connect();
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));

            output.write(msg);
            output.flush();
            output.close();

            InputStream input = conn.getInputStream();

            byte[] response = null;

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] ch = new byte[1024];
            int len;

            while ((len = input.read(ch)) >= 0) {
                buf.write(ch, 0, len);
            }

            response = buf.toByteArray();
            buf.flush();
            buf.close();
            backstring = new String(response, "utf-8");
            input.close();
            System.out.println("Response:" + backstring);
        }
        return backstring;
    }

    public static void main(String args[]) {
        String returnMsg = "ok_";
        try {
            final String phoneNums = args[0];
            final String msg = args[1];

            if (phoneNums.equals("") || msg.equals("")) {
                throw new Exception("parameter error:invaild parameters!");
            }
            SendMsgHelper smh = new SendMsgHelper(phoneNums, msg);
            for (String number : phoneNums.split(",")) {
                returnMsg = "ok_" + smh.sendPhoneMsg(number,msg);
                Thread.sleep(6000);
            }
        } catch (Exception e) {
            returnMsg = "error_" + e.getMessage();
        }
        //返回给shell
        System.out.println(returnMsg);
    }
}