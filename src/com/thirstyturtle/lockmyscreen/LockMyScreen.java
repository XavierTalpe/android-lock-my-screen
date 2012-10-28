package com.thirstyturtle.lockmyscreen;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public final class LockMyScreen extends Activity {

  private static final int REQUEST_CODE_ENABLE_ADMIN = 2;

  private ComponentName fAdminComponent;
  private DevicePolicyManager fPolicyManager;

  @Override
  public void onCreate( Bundle aSavedInstanceState ) {
    super.onCreate( aSavedInstanceState );

    fAdminComponent = new ComponentName( LockMyScreen.this, PermissionReceiver.class );
    fPolicyManager = ( DevicePolicyManager ) getSystemService( Context.DEVICE_POLICY_SERVICE );

    if ( fPolicyManager.isAdminActive( fAdminComponent ) ) {
      lockScreen();
    }
    else {
      requestPermission();
    }
  }

  private void lockScreen() {
    fPolicyManager.lockNow();
    finish();
  }

  private void requestPermission() {
    Intent intent = new Intent( DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN );
    intent.putExtra( DevicePolicyManager.EXTRA_DEVICE_ADMIN, fAdminComponent );

    startActivityForResult( intent, REQUEST_CODE_ENABLE_ADMIN );
  }

  @Override
  protected void onActivityResult( int aRequestCode, int aResultCode, Intent aData ) {
    super.onActivityResult( aRequestCode, aResultCode, aData );
    // TODO: There's no guarantee this result is correct. Instead we use the
    // admin receiver object to keep track of whether a user accepted
    // or rejected the request.

    if ( aResultCode == RESULT_OK ) {
      lockScreen();
    }
    else {
      showExplainingDialogAndTryAgain();
    }
  }

  private void showExplainingDialogAndTryAgain() {
    AlertDialog.Builder builder = new AlertDialog.Builder( this, AlertDialog.THEME_DEVICE_DEFAULT_DARK );
    builder.setTitle( com.thirstyturtle.lockmyscreen.R.string.request_permission_title );
    builder.setMessage( com.thirstyturtle.lockmyscreen.R.string.request_permission_message );
    builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick( DialogInterface aDialog, int aButton ) {
        requestPermission();
      }
    } );

    builder.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
      @Override
      public void onClick( DialogInterface aDialog, int aButton ) {
        aDialog.dismiss();
        finish();
      }
    } );


    builder.create().show();
  }

  public static final class PermissionReceiver extends DeviceAdminReceiver {

  }

}
