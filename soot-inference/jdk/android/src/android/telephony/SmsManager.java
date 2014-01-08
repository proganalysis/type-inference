/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.telephony;

import android.app.ActivityThread;
import android.app.PendingIntent;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;

import com.android.internal.telephony.ISms;
import com.android.internal.telephony.SmsRawData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import checkers.inference.reim.quals.*; 
import checkers.inference.sflow.quals.*; 

/*
 * TODO(code review): Curious question... Why are a lot of these
 * methods not declared as static, since they do not seem to require
 * any local object state?  Presumably this cannot be changed without
 * interfering with the API...
 */

/**
 * Manages SMS operations such as sending data, text, and pdu SMS messages.
 * Get this object by calling the static method SmsManager.getDefault().
 */
public final class SmsManager {
    public static @Tainted ArrayList<SmsMessage> getAllMessagesFromIcc() { throw new RuntimeException("skeleton method"); }
    public static SmsManager getDefault() { throw new RuntimeException("skeleton method"); }
    public boolean copyMessageToIcc(byte[] arg0, byte[] arg1, int arg2) { throw new RuntimeException("skeleton method"); }
    public boolean deleteMessageFromIcc(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean disableCellBroadcast(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean disableCellBroadcastRange(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public ArrayList<String> divideMessage(String arg0) { throw new RuntimeException("skeleton method"); }
    public boolean enableCellBroadcast(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean enableCellBroadcastRange(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void sendDataMessage(String arg0, String arg1, short arg2, @Safe byte[] arg3, PendingIntent arg4, PendingIntent arg5) { throw new RuntimeException("skeleton method"); }
    public void sendMultipartTextMessage(String arg0, String arg1, ArrayList<String> arg2, ArrayList<PendingIntent> arg3, ArrayList<PendingIntent> arg4) { throw new RuntimeException("skeleton method"); }
    public void sendTextMessage(String arg0, String arg1, @Safe String arg2, PendingIntent arg3, PendingIntent arg4) { throw new RuntimeException("skeleton method"); }
    public boolean updateMessageOnIcc(int arg0, int arg1, byte[] arg2) { throw new RuntimeException("skeleton method"); }
}
