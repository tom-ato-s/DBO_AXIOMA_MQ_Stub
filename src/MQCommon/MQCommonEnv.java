package MQCommon;

import Common.CommomEnv;
import Common.NTCommon;

import java.util.Queue;

//базовые классы для работы с MQ
public class MQCommonEnv {
    public static com.ibm.mq.jms.MQQueueConnectionFactory mqQCF1 = null;
    private CachingConnectionFactory CCF1 = null;


    public static void CreatConnectionFactory1() throws JMSExepion {    // создание фабрики соединений c MQ
        MQCommonEnv.mqQCF1 = new com.ibm.mq.jms.MQQueueConnectionFactory();

        MQCommonEnv.mqQCF1.setHostNsme(NTCommon.MQ_port1);
        MQCommonEnv.mqQCF1.setPort(NTCommon.MQ_port1);
        MQCommonEnv.mqQCF1.setQueueManager(NTCommon.MQ_QueueManager1);
        // MQCommonEnv.mqQCF1.setChanel(NTCommon.MQ_Channell);
        MQCommonEnv.mqQCF1.setCCSID(437);
        MQCommonEnv.mqQCF1.setTransportType(1);
        MQCommonEnv.mqQCF1.setUseConnectionPooling(false);

        CCF1 = new CachingConnectionFactoty(MQCommonEnv.mqQCF1);
        CCF1.setReconnectOnException(true);
        CCF1.setSessionCacheSize(10);
    }
    public static QueueReceiver createReciever1(String mqQueueName) {  // Создание слушателя на очередь для разбирания входящих сообщений
        String mqQueueURI = "queue:///" + mqQueueName;
        Queue mqQueue = null;
        QueueReceiver mqReceiver = null;
        QueueSession mqSession = null;
        QueueConnection mqConnection = MQCommonEnv.getConnection1();

        try {
            mqSession = mqConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            mqQueue = mqSession.createQueue(mqQueueURI);
            mqReceiver = mqSession.createReceiver(mqQueue);

            System.out.println("Created receiver for " + mqQueueName + " on MQManager " + NTCommon.MQ_QueueManager1 + " onn channel " + NTCommon.MQ_Channell);

            //mqSession.run();
        } catch (JMSException e) {
            CommonEnv.logMessage ("MQListeners", "Creation Receiver for queue \"" + mqQueueName + "\" failure: " + e.getMessage());
            try{
                if(mqSession != null) {
                    mqSession.close();
                }
            }catch (JMSException je) {
                CommomEnv.logMessage("MQListener", "Session closing failure: " + je.getMessage());
            }

        }
        return mqReceiver;
    }

    public static QueueConnection getConnection1() { // создает соединение с менеджером очередей

        QueueConnection qc = null;

        try {
            qc = MQCommonEnv.CCF1.createQueueConnection();

            qc.start();

        }catch (JMSException ex) {
            CommomEnv.logMessage("getConnection1", "Error get connection: " + ex.getMessage());
        }
        return qc;
    }


}
