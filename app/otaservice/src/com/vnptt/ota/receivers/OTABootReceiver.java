package com.vnptt.ota.receivers;

import com.vnptt.ota.service.VnptOtaService;
import com.vnptt.ota.common.VnptOtaUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OTABootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			VnptOtaUtils.LogError("OTABootReceiver", "Receive BOOT COMPLETE action");
			Intent startServiceIntent = new Intent(context,
					VnptOtaService.class);
			context.startService(startServiceIntent);
		}
	}
}
