package DBO_AXIOMA_MQ_Stub;

import AXIOMA.AXIOMA_Rec;
import Common.CommonEnv;
import Common.NTCommon;
import MQCommon.MQCommonEnv;
import javax.jms.JMSException;
import AXIOMA.AXIOMA_Rec_Adddoc;
import AXIOMA.AXIOMA_Rec_Confirm;


//import MQCommon.MQCommonEnv;
//import Common.CommomEnv;
//import javax.*;
//import javax.management.remote.JMXServerErrorException;


public class DBO_AXIOMA_MQ_Stub {
    public static void main(String[] args) {

        try {
            MQCommonEnv.CreatConnectionFactory1();  // создает фабрику соединений с сервером MQ
        } catch (JMXException e) {
            CommomEnv.logMessage("MQInit", "MQ Init error^ " + e.getMessage());
        }

        try {
            MQCommonEnv.createReciever1(NTCommon.AXIOMA_Read).setMessageListener(new AXIOMA_Rec());   // Вешаем ресивер на входящую очередь. НА ресивер вешаем AXIOMA_Read и вешаем оброботчик событий AXIOMA_Rec
//            MQCommonEnv.createReciever1(NTCommon.AXIOMA_App_Read).setMessageListener(new AXIOMA_Rec());
//            MQCommonEnv.createReciever1(NTCommon.AXIOMA_Adddocs_Read).setMessageListener(new AXIOMA_Rec_Adddocs());
//            MQCommonEnv.createReciever1(NTCommon.AXIOMA_REPLY_Q).setMessageListener(new AXIOMA_Rec_Confirm());
        } catch (JMSException ex) {
            CommomEnv.logMessage("ERIBReceiverInit","MQ Init error: " + ex.getMessage());
        }
// дальше логирование - получилось или не  повесить слушателя
        int  i = 0;

        while (i< 10_000) {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                CommomEnv.logMessage("MainThredError", e.getMessage());
                Thread.currentThread().interrupt();
            }
            CommomEnv.logMessage("AXIOMA_Stats", "Message count:"+Integer.toString(NTCommon.MessageCount));
                i = i+1;
        }
    }
}
