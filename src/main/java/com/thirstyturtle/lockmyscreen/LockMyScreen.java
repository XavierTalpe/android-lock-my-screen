package com.thirstyturtle.lockmyscreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public final class LockMyScreen extends Activity {

  private static final int REQUEST_CODE_ENABLE_ADMIN = 2;

  @Override
  protected void onCreate( Bundle aSavedInstanceState ) {
    super.onCreate( aSavedInstanceState );

    ComponentName adminComponent = new ComponentName( LockMyScreen.this, PermissionReceiver.class );
    DevicePolicyManager policyManager = ( DevicePolicyManager ) getSystemService( Context.DEVICE_POLICY_SERVICE );

    if ( policyManager.isAdminActive( adminComponent ) ) {
      lockScreen( policyManager );
    }
    else {
      requestPermission( adminComponent );
    }
  }

  @Override
  protected void onActivityResult( int aRequestCode, int aResultCode, Intent aData ) {
    super.onActivityResult( aRequestCode, aResultCode, aData );

    if ( aResultCode == RESULT_OK ) {
      DevicePolicyManager policyManager = ( DevicePolicyManager ) getSystemService( Context.DEVICE_POLICY_SERVICE );
      lockScreen( policyManager );
    }
    else {
      askUserToRetry();
    }
  }

  private void lockScreen( DevicePolicyManager aPolicyManager ) {
    aPolicyManager.lockNow();
    finish();
  }

  private void requestPermission( ComponentName aAdminComponent ) {
    String explanation = getResources().getString( R.string.request_permission_explanation );

    Intent intent = new Intent( DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN );
    intent.putExtra( DevicePolicyManager.EXTRA_DEVICE_ADMIN, aAdminComponent );
    intent.putExtra( DevicePolicyManager.EXTRA_ADD_EXPLANATION, explanation );

    startActivityForResult( intent, REQUEST_CODE_ENABLE_ADMIN );
  }

  private void askUserToRetry() {
    AlertDialog.Builder builder;
    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
      builder = new AlertDialog.Builder( this, AlertDialog.THEME_DEVICE_DEFAULT_DARK );
    }
    else {
      builder = new AlertDialog.Builder( this );
    }

    builder.setTitle( com.thirstyturtle.lockmyscreen.R.string.request_permission_title );
    builder.setMessage( com.thirstyturtle.lockmyscreen.R.string.request_permission_message );
    builder.setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick( DialogInterface aDialog, int aButton ) {
        ComponentName adminComponent = new ComponentName( LockMyScreen.this, PermissionReceiver.class );
        requestPermission( adminComponent );
      }
    } );

    builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
      @Override
      public void onClick( DialogInterface aDialog, int aButton ) {
        aDialog.dismiss();
        finish();
      }
    } );

    builder.create().show();
  }

  public static final class PermissionReceiver extends DeviceAdminReceiver {

    @Override
    public void onDisabled( Context aContext, Intent aIntent ) {
      Toast.makeText( aContext, R.string.on_permission_disabled, Toast.LENGTH_LONG ).show();
    }
  }

}
