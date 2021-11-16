package AXIOMA;
import Common.CommonEnv;
import Common.NTCommon;
import MQCommon.MQCommonEnv;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

public class AXIOMA_Confirm_Proc extends Thread{
    private QueueConnection mqConnection = null;
    private String mqQueueURI = NTCommon.AXIOMA_Confirm_Write;
    private String DocID = "";
    private String DocNum= "";
    private String DocDate = "";
    private String ClntName = "";
    private String ClntId = "";
    private String OprId = "";
    private String ReplyTo = NTCommon.AXIOMA_REPLY_Q;

    private String corID = "";
    private String replyID = "";
    private String messageText = "";
    private Queue mqQueue = null;
    private QueueSender mqSender = null;
    private QueueSession mqSession = null;
    private Queue ReplyToQ = null;

    public AXIOMA_Confirm_Proc(String DocumentId, String DocumentNumber,String DocumentDate,String ClientName,String ClientId, String OperationId) {

        DocID = DocumentId;
        DocNum = DocumentNumber;
        DocDate = DocumentDate;
        ClntName = ClientName;
        ClntId = ClientId;
        OprId = OperationId;
        corID = CommonEnv.randomNumeric(25);
        replyID = corID;

    }

    @Override
    public void run() {

        CommonEnv.Delay(CommonEnv.randomTimeout(NTCommon.AXIOMA_delay, NTCommon.AXIOMA_delay_dev));


        send_Answer(Confirmation());


    }



    private String Confirmation() {

        String rsXml;

        ComonEnv.logMessage("AXIOMA_Confirm_Processing", "Sending ApplicationResponse.");

        rsXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
                        "<ConfirmationNotification TradeId=\""+ OprId +"\" DocumentId=\"" + DocID + "\" ApplicationNumber=\"" + DocNum +"\" ApplicationDate=\"" + DocDate +"\">" +
                        "<MessageInfo Source=\"Stub\" SourceVersion=\"172.0.1.16501\" Destination=\"DBO2\" Timestamp=\""+nowAXIOMA()+"\" OperationId=\"" + OprId +"\"/>" +
                        "<Client ClientName=\"" + ClntName + "\">" +
                        "<ClientId ClientId=\"" + ClntId +"\" ClientSource=\"DBO\"/>" +
                        "</Client>" +
                        "<Documents DocumentNumber=\"" + DocNum +"\" DocumentDate=\"" + DocDate +"\">" +
                        "<DocumentInfo DocumentType=\"SigningObject\" FileName=\"Акцепт.zip\">" +
                        "<ContentInfo DocumentType=\"ElectronicDocument\" FileName=\"Digest.txt\"/>" +
                        "<ContentInfo DocumentType=\"Accept\" FileName=\"Акцепт.pdf\"/>" +
                        "</DocumentInfo>" +
                        "<DocumentBody>AAAAZg==</DocumentBody>" +
                        "<Signatures SignDateTime=\"2017-11-30T12:12:12\" EmployeeName=\"Минин Дмитрий Анатольевич\" EmployeePosition=\"Главный менеджер отдела продаж производных инструментов\" EmployeeTelephone=\"8(495)456-54-68\" Login=\"region\\abs001\">" +
                        "<SignData>AAAAZg==</SignData>" +
                        "</Signatures>" +
                        "</Documents>" +
                        "<Documents DocumentNumber=\"" + DocNum +"\" DocumentDate=\"" + DocDate +"\">" +
                        "<DocumentInfo DocumentType=\"Accept\" FileName=\"Акцепт.pdf\"/>" +
                        "<DocumentBody>DDDDZg==</DocumentBody>" +
                        "</Documents>" +
                        "</ConfirmationNotification>";


        return rsXml;

    }

    private String nowAXIOMA() {
        Calendar calendar = Calendar.getInstance();
        //2013-04-02T16:20:21.427+04:00
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.8367277+03:00'");
        return df.format(calendar.getTime());
    }

    private void send_Answer(String rsXml) {
        mqConnection = MQCommonEnv.getConnection1();  //!!!
        try {

            mqSession = mqConnection.createQueueSession(
                    false, Session.AUTO_ACKNOWLEDGE);

            javax.jms.TextMessage message = mqSession.createTextMessage(rsXml);

            //message.setStringProperty("ExtSystemCode", ExtSystemCode);

            CommonEnv.logMessage("AXIOMA_Confirm_Send", "Got message:" + rsXml);

            ReplyToQ = mqSession.createQueue(ReplyTo);

            message.setJMSMessageID(replyID);
            message.setJMSCorrelationID(corID);
            message.setJMSReplyTo(ReplyToQ);

            mqQueue = mqSession.createQueue(mqQueueURI);
            mqSender =  mqSession.createSender(mqQueue);
            mqSender.send(message);


            NTCommon.MessageCount++;

            mqSession.close();

            if (NTCommon.TestLogXML) {
                //            CommonEnv.logMessage("replyId ", replyID);
                CommonEnv.logMessage("AXIOMA_Confirm_Processing", "Response sent: " + rsXml);
            }

        } catch (JMSException e) {
            CommonEnv.logMessage("AXIOMA_Confirm_Processing", "Cant send message: " + e.getMessage());
        }

    }
}
