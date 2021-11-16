package AXIOMA;

public class AXIOMAapp_Proc {

    private QueueConnection mqConnection = null;
    private String mqQueueURI = NTCommon.AXIOMA_App_Write;
    private String replyID = "";
    private String corID = "";
    private String messageText = "";
    private Queue mqQueue = null;
    private QueueSender mqSender = null;
    private QueueSession mqSession = null;

    public AXIOMAapp_Proc(String replyId, String corId, String messText) {

        replyID = replyId;
        corID = corId;
        messageText = messText;
    }

    @Override
    public void run() {

        CommonEnv.Delay(CommonEnv.randomTimeout(NTCommon.AXIOMA_delay, NTCommon.AXIOMA_delay_dev));
        String DocumentId = StringUtils.substringBetween(messageText, "DocumentId=\"", "\"");
        String DocumentNumber = StringUtils.substringBetween(messageText, "DocumentNumber=\"", "\"");
        String DocumentDate = StringUtils.substringBetween(messageText, "DocumentDate=\"", "\"");
        String ClientName = StringUtils.substringBetween(messageText, "ClientName=\"", "\"");
        String ClientId = StringUtils.substringBetween(messageText, "ClientId=\"", "\"");
        String OperationId = StringUtils.substringBetween(messageText, "OperationId=\"", "\"");


        send_Answer(ApplicationResponce(OperationId));

        Thread thread1 = new AXIOMA_Confirm_Proc(DocumentId,DocumentNumber,DocumentDate,ClientName,ClientId,OperationId);
        thread1.start();


    }



    private String ApplicationResponce(String OperationId) {

        String rsXml;

        String AccountId = StringUtils.substringBetween(messageText, "Account=\"", "\"");
        String BankCode = StringUtils.substringBetween(messageText, "BankCode=\"", "\"");

        CommonEnv.logMessage("AXIOMAapp_Processing", "Sending ApplicationResponse.");

        rsXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<ApplicationResponse Success=\"true\" TradeId=\"" + OperationId + "\">" +
                        "<MessageInfo Source=\"AXIOMA\" SourceVersion=\"1\" Destination=\"DBO\" Timestamp=\""+nowAXIOMA()+"\" OperationId=\"string\"/>" +
                        "<Account Account=\""+AccountId+"\" BankCode=\""+BankCode+"\"/>" +
                        "</ApplicationResponse>";

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

            CommonEnv.logMessage("AXIOMAapp_Send", "Got message:" + rsXml);

            message.setJMSMessageID(replyID);
            message.setJMSCorrelationID(corID);

            mqQueue = mqSession.createQueue(mqQueueURI);
            mqSender =  mqSession.createSender(mqQueue);
            mqSender.send(message);


            NTCommon.MessageCount++;

            mqSession.close();

            if (NTCommon.TestLogXML) {
                CommonEnv.logMessage("replyId ", replyID);
                CommonEnv.logMessage("AXIOMAapp_Processing", "Response sent: " + rsXml);
            }

        } catch (JMSException e) {
            CommonEnv.logMessage("AXIOMAapp_Processing", "Cant send message: " + e.getMessage());
        }

    }
}
