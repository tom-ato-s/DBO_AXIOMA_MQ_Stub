package AXIOMA;

import Common.CommomEnv;
import Common.NTCommon;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.MessageListener;
import com.mysql.cj.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AXIOMA_Rec implements MessageListener {     // Rec - Resiver
    public AXIOMA_Rec () {

    }

    @Override
    public void onMessage(Message message) { // Выолнение этого класса происходит когда в очередь падает какое-то сообщение
        //здесь происходит обработка сообщения и смотрят что с ним делать

        CommomEnv.logMessage("AXIOMA_Receive", "On message started");
        JMSBytesMessage bMessage = null;
        int MsgType = 0;

        try {
            bMessage = (JMSBytesMessage) message;
        } catch (Exception ex) {
            MsgType = 1;
        }

        String txtMsg = "";
         if(MsgType == 0) {
            try {
                int TEXT_LENGTH = (new Long(bMessage.getBodyLength())).intValue();

                byte[] textBytes = new byte[TEXT_LENGTH];  // сообщение приходит в байтах и мы будет его приобразовывать в STRING

                bMessage.readBytes(textBytes, TEXT_LENGTH);

                txtMsg = new String(textBytes); // здесь лежит все сообщение, вся xml-ка
            } catch (JMSException ex) {
                System.out.println(ex.toString());
            }
         }else {

             try {
                 javax.jms.TextMessage txt  = (javax.jms.TextMessage) message;
                 txtMsg = txt.getText();
             } catch (JMSExeption ex) {
                 System.out.println(ex.toStriog())  ;
             }

         }
        // if (message instanceof JMSBytesMessage) {
        if(NTCommon.TestLogXML) {
            CommomEnv.logMessage("AXIOMA_Receive", "Got message:" + txtMsg);
        }

        String reptyId = "";
        String corID = "";

        try { // у сообщения есть клонверт-протокол. и в нем есть значение полей. в данном случае мы достаем значения reptyId и corID
            reptyId = message.getGMSMessageID();
            corID = message.JMSCorrelationID();
        }catch (JMSException ex) {
            System.out.println(ex.toString());
        }

        String msgType = StringUtils.substringBetween(txtMsg, "standalone=\"yes\"?><", "Request"); // распарсить саму xml-кую Посмотреть какой именно ответ пришел.
        // Здесь должно быть сообщение 2-x типов
        //msgType достаем в середине сообщения
        CommomEnv.logMessage("AXIOMA_Receive", "Got msgType:" + msgType);

//        PrintWriter out = null;
//        try {
//            out = new PrintWriter("C:\\VTB_DBO\\reg.txt");
//        }catch (FileNotFoundException ex) {
//            Logger.getLogger(AXIOMA_REC.class.getName().log()Level.SEVERE, null, ex);
//        }out.println(txtMsg);

        if(Objects.equals(msgType, "ClientInfo") == true) { // важно, что клас формирования ответа будет формироваться в отдельном потоке!!!!!!

            Thread thread1 = new AXIOMA_Proc(reptyId, corID, txtMsg);
            thread1.start();
        }

        if(Objects.equals(msgType, "Application") == true) {

            Thread thread1 = new AXIOMAapp_Proc(reptyId, corID, txtMsg);
            thread1.start();
        }
    }
}
