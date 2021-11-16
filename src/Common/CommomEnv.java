package Common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class CommomEnv {

    private static Random RANDOM = new Random();

    public static String now() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(calendar.getTime());
    }

    public static String nowKSH() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000+04:00'");
        return df.format(calendar.getTime());
    }

    public static long nowMs() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static void logMessage(String module, String message) {
        System.out.println(now() + "\t" + module + ":" + message );
    }

    public static void Delay(long Pause) {
        try {
            Thread.sleep(Pause);
        } catch (InterruptedException e) {
            e.getMessage();
            Thread.currentThread().interrupt();// возвращает ссылку на текущий поток и проверяет был ли прерван текущий поток
        }
    }
    public static String randomNumeric(int count) {
        return randomString(count, 0, 0, false, true, null);
    }

    public static String randomAlphanumeric(int count) {
        return randomString(count, 0, 0, true, true, null);
    }

    public static String randomString (int count, int start, int end, boolean leters, boolean numbers, char[] chars) {   // фенкция формирования рендомного стринга
        if(count == 0) {
            return "";
        }else if (count < 0) {
            throw new IllegalArgumentException(
                    "Requested random string length " + count + " is less than 0.");
        }
        if ((start == 0) &&(end == 0)) {
            end = 'z' +1;
            start = ' ';
            if(!leters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;

            }
        }

        char[] buffer = new char[count];   // размер буфера?
        int gap = end- start;

        while (count-- !=0) {
            char ch;
            if(chars == null) {
                ch = (char) (RANDOM.nextInt(gap) +start);
            }else {
                ch = chars[RANDOM.nextInt(gap) +start];
            }
            if(leters && Character.isLetter((ch))
                    || (numbers && Character.isDigit(ch))
                    || (!leters && !numbers)) {
                if(ch >=56320 && ch<= 57343) {
                    if(count == 0) {
                        count++;
                    } else {
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + RANDOM.nextInt(128));
                    }
                }else if(ch > 55296 && ch< 56319) {
                    if (count == 0) {
                        count++;
                    } else {
                        buffer[count] = (char) (RANDOM.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >=56192 && ch<=56319) {
                    count++;
                }else {
                    buffer[count] = ch;
                }
            }else {
                count++;
            }
        }
        return new String(buffer);
    }
    public static long randomTimeout(long delay, long deviation) {
        long timeout = 0;
        long maxDelay, minDelay;
        int range;

        if(delay < deviation) {
            delay = deviation;
        }

        maxDelay = (delay + deviation);
        minDelay = (delay-deviation);
        range = (int) (maxDelay-minDelay);

        timeout = RANDOM.nextInt(range + 1);

        //System.out.println("Final timeout: " + (timeout + minDelay));

     return (long)(timeout + minDelay);
    }

    public static long timewSteps(long delay, long deviation, long timefromstart, long minus) {

        if(delay>0){
            //System.out.println("Delay before:" + delay);
            delay = delay - ((timefromstart/60000)*minus);
            //System.out.println("Delay after:" + delay);
            return  randomTimeout(delay, deviation);
        }
        else {
            return 0;
        }
    };

    public static String mareSoapResponseSimple(String TnnerXml) {
        return "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"+InnerXml+"</soap:Body></soap:Envelope>";
    }


}
