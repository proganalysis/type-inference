package android.location;
import checkers.inference.reim.quals.*; 
import checkers.inference.sflow.quals.*; 
public class Location implements android.os.Parcelable {
    public static int FORMAT_DEGREES;
    public static int FORMAT_MINUTES;
    public static int FORMAT_SECONDS;
    public static java.lang.String EXTRA_COARSE_LOCATION;
    public static java.lang.String EXTRA_NO_GPS_LOCATION;
    private java.lang.String mProvider;
    private long mTime;
    private long mElapsedRealtimeNanos;
    private double mLatitude;
    private double mLongitude;
    private boolean mHasAltitude;
    private double mAltitude;
    private boolean mHasSpeed;
    private float mSpeed;
    private boolean mHasBearing;
    private float mBearing;
    private boolean mHasAccuracy;
    private float mAccuracy;
    private android.os.Bundle mExtras;
    private boolean mIsFromMockProvider;
    private double mLat1;
    private double mLon1;
    private double mLat2;
    private double mLon2;
    private float mDistance;
    private float mInitialBearing;
    private float[] mResults;
    public static android.os.Parcelable.Creator<android.location.Location> CREATOR;
    public Location(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public Location(android.location.Location arg0) { throw new RuntimeException("skeleton method"); }
    public void set(android.location.Location arg0) { throw new RuntimeException("skeleton method"); }
    public void reset() { throw new RuntimeException("skeleton method"); }
    public static java.lang.String convert(double arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public static double convert(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    private static void computeDistanceAndBearing(double arg0, double arg1, double arg2, double arg3, float[] arg4) { throw new RuntimeException("skeleton method"); }
    public static void distanceBetween(double arg0, double arg1, double arg2, double arg3, float[] arg4) { throw new RuntimeException("skeleton method"); }
    public float distanceTo(android.location.Location arg0) { throw new RuntimeException("skeleton method"); }
    public float bearingTo(android.location.Location arg0) { throw new RuntimeException("skeleton method"); }
    public java.lang.String getProvider() { throw new RuntimeException("skeleton method"); }
    public void setProvider(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public long getTime() { throw new RuntimeException("skeleton method"); }
    public void setTime(long arg0) { throw new RuntimeException("skeleton method"); }
    public long getElapsedRealtimeNanos() { throw new RuntimeException("skeleton method"); }
    public void setElapsedRealtimeNanos(long arg0) { throw new RuntimeException("skeleton method"); }
    public double getLatitude() { throw new RuntimeException("skeleton method"); }
    public void setLatitude(double arg0) { throw new RuntimeException("skeleton method"); }
    public double getLongitude() { throw new RuntimeException("skeleton method"); }
    public void setLongitude(double arg0) { throw new RuntimeException("skeleton method"); }
    public boolean hasAltitude() { throw new RuntimeException("skeleton method"); }
    public double getAltitude() { throw new RuntimeException("skeleton method"); }
    public void setAltitude(double arg0) { throw new RuntimeException("skeleton method"); }
    public void removeAltitude() { throw new RuntimeException("skeleton method"); }
    public boolean hasSpeed() { throw new RuntimeException("skeleton method"); }
    public float getSpeed() { throw new RuntimeException("skeleton method"); }
    public void setSpeed(float arg0) { throw new RuntimeException("skeleton method"); }
    public void removeSpeed() { throw new RuntimeException("skeleton method"); }
    public boolean hasBearing() { throw new RuntimeException("skeleton method"); }
    public float getBearing() { throw new RuntimeException("skeleton method"); }
    public void setBearing(float arg0) { throw new RuntimeException("skeleton method"); }
    public void removeBearing() { throw new RuntimeException("skeleton method"); }
    public boolean hasAccuracy() { throw new RuntimeException("skeleton method"); }
    public float getAccuracy() { throw new RuntimeException("skeleton method"); }
    public void setAccuracy(float arg0) { throw new RuntimeException("skeleton method"); }
    public void removeAccuracy() { throw new RuntimeException("skeleton method"); }
    public boolean isComplete() { throw new RuntimeException("skeleton method"); }
    public void makeComplete() { throw new RuntimeException("skeleton method"); }
    public android.os.Bundle getExtras() { throw new RuntimeException("skeleton method"); }
    public void setExtras(android.os.Bundle arg0) { throw new RuntimeException("skeleton method"); }
    public java.lang.String toString() { throw new RuntimeException("skeleton method"); }
    public void dump(android.util.Printer arg0, java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public int describeContents() { throw new RuntimeException("skeleton method"); }
    public void writeToParcel(android.os.Parcel arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public android.location.Location getExtraLocation(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public void setExtraLocation(java.lang.String arg0, android.location.Location arg1) { throw new RuntimeException("skeleton method"); }
    public boolean isFromMockProvider() { throw new RuntimeException("skeleton method"); }
    public void setIsFromMockProvider(boolean arg0) { throw new RuntimeException("skeleton method"); }
}
