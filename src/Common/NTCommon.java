package Common;  // набор констант

public class NTCommon {

    public static String MQ_HostName1 = "localhost";
    public static int MQ_port1 = 1416;
    public static String MQ_QueueManager1 = "TEST";
    public static String MQ_Channell = "CHANNELL";
  //  public static String MQ_User1 = "app";
  //  public static String MQ_Password1 = "";

    public static String AXIOMA_Read = "TEST.REQ";  // очередь для запросов
    public static String AXIOMA_Write = "TEST.RESP"; // очередь для ответов

    public static String AXIOMA_App_Read = "MDEPO.IIB.REGISTER_APP.REQ.RQ";
    public static String AXIOMA_App_Write = "IIB.MDEPO.REGISTER_APP.RES.Q";

    public static String AXIOMA_Confirm_Write = "IIB.MDEPO.CONFIRM_APP.VI.O.REQ.RQ";

    public static String AXIOMA_REPLY_Q = "DEV.QUEUE.1";

    public static String AXIOMA_Adddocs_Read = "MDEPO.IIB.REGISTER_ADDDOCS.REQ.RQ";  //просто считать и ничего больше не делать
    public static int MQ_OK = 0;

    public static boolean TestLogXML = false;
    public static int MessageCount = 0;

    public static int AXIOMA_dellay = 0;
    public static int AXIOMA_dellay_dev = 0;

}

