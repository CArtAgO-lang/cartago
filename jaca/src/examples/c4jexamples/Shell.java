package c4jexamples;

import cartago.*;
import java.io.*;

public class Shell extends Artifact {

  @OPERATION void whoami(OpFeedbackParam<String> res){
    try {
      Process proc = Runtime.getRuntime().exec("whoamI");
      BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      StringBuffer buffer = new StringBuffer("");
      String st = reader.readLine();
      while (st != null){
        buffer.append(st+"\n");
        st = reader.readLine();
      }
      res.set(buffer.toString());
    } catch (Exception ex){
      failed("cmd failed");
    }
  }
  
  @OPERATION void traceroute(String address){
    try {
      Process proc = Runtime.getRuntime().exec(new String[]{"traceroute", address});
      InputStream is = proc.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String st = reader.readLine();
      while (st != null){
        String tokens[] = st.trim().split(" ");
          if (tokens[0].equals("*")){
            
          } else {
            try {
              int num = Integer.parseInt(tokens[0]);
              String logicalAddress = tokens[2];
              String ipAddress = tokens[3].substring(1,tokens[3].length()-1);
              double delay1 = Double.parseDouble(tokens[5]);
              double delay2 = Double.parseDouble(tokens[8]);
              double delay3 = Double.parseDouble(tokens[11]);
              signal("hop",num,logicalAddress,ipAddress,delay1,delay2,delay3);
            } catch (Exception ex){
              // ex.printStackTrace();
              // not hop info, ignore
            }
          }
        st = reader.readLine();
      }
    } catch (Exception ex){
      ex.printStackTrace();
      failed("cmd failed");
    }
  } 
}
