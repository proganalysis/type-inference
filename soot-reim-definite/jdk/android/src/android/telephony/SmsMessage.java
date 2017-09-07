package android.telephony;
import checkers.inference.reim.quals.*; 
import checkers.inference.sflow.quals.*; 

public class SmsMessage {
    public static class SubmitPdu {
        public byte[] encodedScAddress;
        public byte[] encodedMessage;
        public java.lang.String toString() { throw new RuntimeException("skeleton method"); }
        protected SubmitPdu(com.android.internal.telephony.SmsMessageBase.SubmitPduBase arg0) { throw new RuntimeException("skeleton method"); }
    }
    public static final class MessageClass {
        public static android.telephony.SmsMessage.MessageClass UNKNOWN;
        public static android.telephony.SmsMessage.MessageClass CLASS_0;
        public static android.telephony.SmsMessage.MessageClass CLASS_1;
        public static android.telephony.SmsMessage.MessageClass CLASS_2;
        public static android.telephony.SmsMessage.MessageClass CLASS_3;
        public static android.telephony.SmsMessage.MessageClass[] values() { throw new RuntimeException("skeleton method"); }
        public static android.telephony.SmsMessage.MessageClass valueOf(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
        private MessageClass() { throw new RuntimeException("skeleton method"); }
    }
    private static java.lang.String LOG_TAG;
    public static int ENCODING_UNKNOWN;
    public static int ENCODING_7BIT;
    public static int ENCODING_8BIT;
    public static int ENCODING_16BIT;
    public static int ENCODING_KSC5601;
    public static int MAX_USER_DATA_BYTES;
    public static int MAX_USER_DATA_BYTES_WITH_HEADER;
    public static int MAX_USER_DATA_SEPTETS;
    public static int MAX_USER_DATA_SEPTETS_WITH_HEADER;
    public static java.lang.String FORMAT_3GPP;
    public static java.lang.String FORMAT_3GPP2;
    public com.android.internal.telephony.SmsMessageBase mWrappedSmsMessage;
    private SmsMessage(com.android.internal.telephony.SmsMessageBase arg0) { throw new RuntimeException("skeleton method"); }
    public static @Tainted android.telephony.SmsMessage createFromPdu(@Tainted byte[] arg0) { throw new RuntimeException("skeleton method"); }
    public static @Tainted android.telephony.SmsMessage createFromPdu(byte[] arg0, java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public static @Tainted android.telephony.SmsMessage newFromCMT(java.lang.String[] arg0) { throw new RuntimeException("skeleton method"); }
    public static @Tainted android.telephony.SmsMessage newFromParcel(android.os.Parcel arg0) { throw new RuntimeException("skeleton method"); }
    public static @Tainted android.telephony.SmsMessage createFromEfRecord(int arg0, byte[] arg1) { throw new RuntimeException("skeleton method"); }
    public static int getTPLayerLengthForPDU(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public static int[] calculateLength(java.lang.CharSequence arg0, boolean arg1) { throw new RuntimeException("skeleton method"); }
    public static java.util.ArrayList<java.lang.String> fragmentText(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public static int[] calculateLength(java.lang.String arg0, boolean arg1) { throw new RuntimeException("skeleton method"); }
    public static android.telephony.SmsMessage.SubmitPdu getSubmitPdu(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, boolean arg3) { throw new RuntimeException("skeleton method"); }
    public static android.telephony.SmsMessage.SubmitPdu getSubmitPdu(java.lang.String arg0, java.lang.String arg1, short arg2, byte[] arg3, boolean arg4) { throw new RuntimeException("skeleton method"); }
    public java.lang.String getServiceCenterAddress() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getOriginatingAddress() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getDisplayOriginatingAddress() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getMessageBody() { throw new RuntimeException("skeleton method"); }
    public android.telephony.SmsMessage.MessageClass getMessageClass() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getDisplayMessageBody() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getPseudoSubject() { throw new RuntimeException("skeleton method"); }
    public long getTimestampMillis() { throw new RuntimeException("skeleton method"); }
    public boolean isEmail() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getEmailBody() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getEmailFrom() { throw new RuntimeException("skeleton method"); }
    public int getProtocolIdentifier() { throw new RuntimeException("skeleton method"); }
    public boolean isReplace() { throw new RuntimeException("skeleton method"); }
    public boolean isCphsMwiMessage() { throw new RuntimeException("skeleton method"); }
    public boolean isMWIClearMessage() { throw new RuntimeException("skeleton method"); }
    public boolean isMWISetMessage() { throw new RuntimeException("skeleton method"); }
    public boolean isMwiDontStore() { throw new RuntimeException("skeleton method"); }
    public byte[] getUserData() { throw new RuntimeException("skeleton method"); }
    public byte[] getPdu() { throw new RuntimeException("skeleton method"); }
    public int getStatusOnSim() { throw new RuntimeException("skeleton method"); }
    public int getStatusOnIcc() { throw new RuntimeException("skeleton method"); }
    public int getIndexOnSim() { throw new RuntimeException("skeleton method"); }
    public int getIndexOnIcc() { throw new RuntimeException("skeleton method"); }
    public int getStatus() { throw new RuntimeException("skeleton method"); }
    public boolean isStatusReportMessage() { throw new RuntimeException("skeleton method"); }
    public boolean isReplyPathPresent() { throw new RuntimeException("skeleton method"); }
}
