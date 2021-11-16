package AXIOMA;
import Common.CommonEnv;
import Common.NTCommon;
import com.ibm.jms.JMSBytesMessage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.JMSException;
import org.apache.commons.lang.StringUtils;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
public class AXIOMA_Rec_Adddoc {


    public AXIOMA_Rec_Adddoc() {
    }

    @Override
    public void onMessage(Message message) {

        CommonEnv.logMessage("AXIOMA_Rec_Addoc_Receive", "On message started");

        JMSBytesMessage bMessage = null;
        int MsgType = 0;

        try {
            bMessage = (JMSBytesMessage) message;
        } catch (Exception ex) {
            MsgType = 1;
        }


        String txtMsg = "";

        if (MsgType == 0) {

            try {
                int TEXT_LENGTH = (new Long(bMessage.getBodyLength())).intValue();

                byte[] textBytes = new byte[TEXT_LENGTH];

                bMessage.readBytes(textBytes, TEXT_LENGTH);

                txtMsg = new String(textBytes);
            } catch (JMSException ex) {
                System.out.println(ex.toString());
            }


        } else {
            try {

                javax.jms.TextMessage txt = (javax.jms.TextMessage) message;
                txtMsg = txt.getText();

            } catch (JMSException ex) {
                System.out.println(ex.toString());
            }
        }
        //if (message instanceof JMSBytesMessage) {
        if (NTCommon.TestLogXML) {
            CommonEnv.logMessage("AXIOMA_Rec_Addoc_Receive", "Got message:" + txtMsg);
        }

//        String replyId = "";
//        String corId = "";
//
//        try {
//            replyId = message.getJMSMessageID();
//            corId = message.getJMSCorrelationID();
//        } catch (JMSException ex) {
//            System.out.println(ex.toString());
//        }
//

//        String msgType = StringUtils.substringBetween(txtMsg, "standalone=\"yes\"?><", "Request");
//
//        CommonEnv.logMessage("AXIOMA_Rec", "Got msgType:" + msgType);
//
//
////        PrintWriter out = null;
////        try {
////            out = new PrintWriter("C:\\VTB_DBO\\req.txt");
////        } catch (FileNotFoundException ex) {
////            Logger.getLogger(AXIOMA_Rec.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        out.println(txtMsg);
//
//
//        if (Objects.equals(msgType, "ClientInfo") == true) {
//                Thread thread1 = new AXIOMA_Proc(replyId, corId, txtMsg);
//                thread1.start();
//        }
//
//        if (Objects.equals(msgType, "Application") == true) {
//                Thread thread1 = new AXIOMAapp_Proc(replyId, corId, txtMsg);
//                thread1.start();
//        }





        //}
    } //onMessage
}
