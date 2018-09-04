import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

public class Service {
    public static void main(String[] args) throws Exception{
        HttpServerProvider provider=HttpServerProvider.provider();
        PrintStream stream=new PrintStream("./log.txt");
        System.setOut(stream);
        try {
            HttpServer httpServer=provider.createHttpServer(new InetSocketAddress(10086),100);
            httpServer.createContext("/",new MyResponseHandler());
            httpServer.setExecutor(null);
            httpServer.start();
            System.out.println("server started");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static class MyResponseHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange httpExchange) {
           String requestMethod=httpExchange.getRequestMethod();
           httpExchange.getRemoteAddress();
           try {
               if (requestMethod.equalsIgnoreCase("POST")){
                   Gson gson=new GsonBuilder()
                           .excludeFieldsWithoutExposeAnnotation()
                           .setPrettyPrinting()
                           .create();
                   InputStream inputStream=httpExchange.getRequestBody();
                   Reader reader=new InputStreamReader(inputStream);
                   JsonReader jsonReader=new JsonReader(reader);
                   jsonReader.setLenient(true);
                   Message message=(Message)gson.fromJson(reader,Message.class);
                   inputStream.close();
                   Message response=new Message();
                   response.setTime(new Message.Time(Integer.parseInt(message.getTimeYear()),Integer.parseInt(message.getTimeMonth()),Integer.parseInt(message.getTimeDay())));
                   if (saveDataToFile(message)){
                       response.setText("saved");
                   }else {
                       response.setText("no_saved");
                   }
                   String r=gson.toJson(response);
                   httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, r.getBytes("UTF-8").length);
                   OutputStream outputStream=httpExchange.getResponseBody();
                   OutputStreamWriter writer=new OutputStreamWriter(outputStream,"UTF-8");
                   writer.write(r);
                   writer.close();
                   outputStream.close();
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }
//        private String getDataFromFile(String fileName){
//            File d=new File("./Data/");
//            if (!d.exists()){
//                d.mkdirs();
//            }
//            DataInputStream dataInputStream=null;
//            File file=new File("./Data/"+fileName);
//            try {
//                dataInputStream=new DataInputStream(new FileInputStream(file));
//                return dataInputStream.readUTF();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }finally {
//                if (dataInputStream!=null) {
//                    try {
//                        dataInputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            return null;
//        }
        private boolean saveDataToFile(Message message){
            String fileName=message.getTimeYear()+message.getTimeMonth()+message.getTimeDay();
            String data=message.getText();
            File d=new File("./Data/");
            if (!d.exists()){
                d.mkdirs();
            }
            File file=new File("./Data/"+fileName);
            try {
                DataOutputStream dataOutputStream=new DataOutputStream(new FileOutputStream(file));
                dataOutputStream.writeUTF(data);
                dataOutputStream.flush();
                dataOutputStream.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
