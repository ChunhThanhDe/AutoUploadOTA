package com.vnptt.ota.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.vnptt.ota.common.VnptOtaUtils;
import com.vnptt.ota.getfirmwareinfo.FirmwareInfo;
import com.vnptt.ota.getfirmwareinfo.FirmwareInfoManager;
import com.vnptt.ota.getfirmwareinfo.FirmwareInfoManager.FirmwareInfoListerner;
import com.vnptt.ota.getfirmwareinfo.FirmwareInfoUtils;
import com.vnptt.ota.settings.OtaSettingsHelper;
import com.vnptt.ota.ui.VnptOtaUI;

public class ServiceManagement extends Service{

	private static final String LOG_TAG = "ServiceManagement";

	private IResponseQueryNewVersion iRomInfoCallback;
	private FirmwareInfoManager fmrInfo = null;
	private FirmwareInfoManager fmrInfoAuto = null;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private FirmwareInfoListerner fimwareInfoListener = new FirmwareInfoListerner(){
		@Override
		public void onError(int err) {
			try {
				VnptOtaUtils.LogDebug(LOG_TAG, "onError(): " + err);
				iRomInfoCallback.ErrorVersion(err);
			} catch (RemoteException e) {
			}
		}

		@Override
		public void haveFirmwareUpdate(FirmwareInfo info) {
			try {
				iRomInfoCallback.haveNewVersion(info.getFirmwareVersion(), info.getFirmwareName(), 
						VnptOtaUtils.convetDateToString(info.getFirmwareDate()));
			} catch (RemoteException e) {
			}
			VnptOtaUtils.LogDebug(LOG_TAG, "firmware is available; start activity");
			Intent mIntent = new Intent(getApplicationContext(), VnptOtaUI.class);
			FirmwareInfoUtils.addFirmwareInfoToIntent(mIntent, info);
			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mIntent);		
		}
	};

	private FirmwareInfoListerner fimwareInfoListenerAuto = new FirmwareInfoListerner(){
		@Override
		public void onError(int err) {
			try {
				VnptOtaUtils.LogDebug(LOG_TAG, "onError(): " + err);
				iRomInfoCallback.ErrorVersion(err);
			} catch (RemoteException e) {
			}
		}

		@Override
		public void haveFirmwareUpdate(FirmwareInfo info) {
			try {
				iRomInfoCallback.haveNewVersion(info.getFirmwareVersion(), info.getFirmwareName(), 
						VnptOtaUtils.convetDateToString(info.getFirmwareDate()));
			} catch (RemoteException e) {
			}
		}
	};

	IServiceManagement.Stub mBinder = new IServiceManagement.Stub() {

		@Override
		public void autoQueryNewVersion(IResponseQueryNewVersion responseCb)
				throws RemoteException {
			VnptOtaUtils.LogDebug(LOG_TAG, "autoQueryNewVersion()");
			iRomInfoCallback = responseCb;
			fmrInfoAuto = new FirmwareInfoManager(getApplicationContext(), fimwareInfoListenerAuto);
			fmrInfoAuto.execute();
		}

		@Override
		public void configureOtaSetting(int configure) throws RemoteException {
			VnptOtaUtils.LogDebug(LOG_TAG, "setting change: " + configure);
			OtaSettingsHelper.putInt(ServiceManagement.this.getContentResolver(), 
					OtaSettingsHelper.OTA_SETTINGS, configure);
			if ((configure == VnptOtaUtils.NO_AUTO_QUERY_ALL_CONNECTION) || 
					(configure == VnptOtaUtils.NO_AUTO_QUERY_ONLY_WIFI)) {
				VnptOtaUtils.LogDebug(LOG_TAG, "setting change;cancel auto query alarm");
				VnptOtaUtils.cancelAlarm(ServiceManagement.this, VnptOtaUtils.ACTION_QUERY_FIRMWARE);
			}
		}

		@Override
		public void userQueryNewVersion(IResponseQueryNewVersion responseCb)
				throws RemoteException {
			VnptOtaUtils.LogDebug(LOG_TAG, "userQueryNewVersion()");
			if (VnptOtaUI.isRunning) {
				VnptOtaUtils.LogDebug(LOG_TAG, "resume activity");
				Intent mIntent = new Intent(getApplicationContext(), VnptOtaUI.class);
				//				mIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				//				mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mIntent);
			} else {
				VnptOtaUtils.LogDebug(LOG_TAG, "start checking firmware");
				iRomInfoCallback = responseCb;
				fmrInfo = new FirmwareInfoManager(getApplicationContext(), fimwareInfoListener);
				fmrInfo.execute();
			}
		}

	};

}
