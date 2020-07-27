package org.ukey.android.manager;

import android.content.Context;

public abstract class IUKeyManager {

    public abstract int getSDKVersion();

    public abstract int getUKeyVersion();

    public abstract int getUKeyStatus(String packageName);

    public abstract void requestUKeyPermission(Context context, int requestCode);

    public abstract int createUKey(String spID, String ssdAid, String sign, String timeStamp);

    public abstract int deleteUKey(String spID, String ssdAid, String sign, String timeStamp);

    public abstract String getUKeyID();

    public abstract int syncUKey(String spID, String sign, String timeStamp);
}
