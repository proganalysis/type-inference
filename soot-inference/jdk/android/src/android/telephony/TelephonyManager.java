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

import android.annotation.SdkConstant;
import android.annotation.SdkConstant.SdkConstantType;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;

import com.android.internal.telephony.IPhoneSubInfo;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.RILConstants;
import com.android.internal.telephony.TelephonyProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import checkers.inference.reim.quals.*; 
import checkers.inference.sflow.quals.*; 

public class TelephonyManager {
    public TelephonyManager(Context arg0) { throw new RuntimeException("skeleton method"); }
    public static TelephonyManager from(Context arg0) { throw new RuntimeException("skeleton method"); }
    public static TelephonyManager getDefault() { throw new RuntimeException("skeleton method"); }
    public int getLteOnCdmaModeStatic() { throw new RuntimeException("skeleton method"); }
    public int getNetworkClass(int arg0) { throw new RuntimeException("skeleton method"); }
    public String getNetworkTypeName(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getPhoneType(int arg0) { throw new RuntimeException("skeleton method"); }
    public void disableLocationUpdates() { throw new RuntimeException("skeleton method"); }
    public void enableLocationUpdates() { throw new RuntimeException("skeleton method"); }
    public List<CellInfo> getAllCellInfo() { throw new RuntimeException("skeleton method"); }
    public int getCallState() { throw new RuntimeException("skeleton method"); }
    public int getCdmaEriIconIndex() { throw new RuntimeException("skeleton method"); }
    public int getCdmaEriIconMode() { throw new RuntimeException("skeleton method"); }
    public String getCdmaEriText() { throw new RuntimeException("skeleton method"); }
    public CellLocation getCellLocation() { throw new RuntimeException("skeleton method"); }
    public String getCompleteVoiceMailNumber() { throw new RuntimeException("skeleton method"); }
    public int getCurrentPhoneType() { throw new RuntimeException("skeleton method"); }
    public int getDataActivity() { throw new RuntimeException("skeleton method"); }
    public int getDataState() { throw new RuntimeException("skeleton method"); }
    public @Tainted String getDeviceId() { throw new RuntimeException("skeleton method"); }
    public String getDeviceSoftwareVersion() { throw new RuntimeException("skeleton method"); }
    public String getIsimDomain() { throw new RuntimeException("skeleton method"); }
    public String getIsimImpi() { throw new RuntimeException("skeleton method"); }
    public String[] getIsimImpu() { throw new RuntimeException("skeleton method"); }
    public String getLine1AlphaTag() { throw new RuntimeException("skeleton method"); }
    public String getLine1Number() { throw new RuntimeException("skeleton method"); }
    public int getLteOnCdmaMode() { throw new RuntimeException("skeleton method"); }
    public String getMsisdn() { throw new RuntimeException("skeleton method"); }
    public List<NeighboringCellInfo> getNeighboringCellInfo() { throw new RuntimeException("skeleton method"); }
    public String getNetworkCountryIso() { throw new RuntimeException("skeleton method"); }
    public String getNetworkOperator() { throw new RuntimeException("skeleton method"); }
    public String getNetworkOperatorName() { throw new RuntimeException("skeleton method"); }
    public int getNetworkType() { throw new RuntimeException("skeleton method"); }
    public String getNetworkTypeName() { throw new RuntimeException("skeleton method"); }
    public int getPhoneType() { throw new RuntimeException("skeleton method"); }
    public String getSimCountryIso() { throw new RuntimeException("skeleton method"); }
    public String getSimOperator() { throw new RuntimeException("skeleton method"); }
    public String getSimOperatorName() { throw new RuntimeException("skeleton method"); }
    public @Tainted String getSimSerialNumber() { throw new RuntimeException("skeleton method"); }
    public int getSimState() { throw new RuntimeException("skeleton method"); }
    public @Tainted String getSubscriberId() { throw new RuntimeException("skeleton method"); }
    public String getVoiceMailAlphaTag() { throw new RuntimeException("skeleton method"); }
    public String getVoiceMailNumber() { throw new RuntimeException("skeleton method"); }
    public int getVoiceMessageCount() { throw new RuntimeException("skeleton method"); }
    public boolean hasIccCard() { throw new RuntimeException("skeleton method"); }
    public boolean isNetworkRoaming() { throw new RuntimeException("skeleton method"); }
    public boolean isSmsCapable() { throw new RuntimeException("skeleton method"); }
    public boolean isVoiceCapable() { throw new RuntimeException("skeleton method"); }
    public void listen(PhoneStateListener arg0, int arg1) { throw new RuntimeException("skeleton method"); }
}
