package com.example.did_change_biometrics;

import androidx.annotation.NonNull;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import android.app.Activity;

import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import java.security.NoSuchAlgorithmException;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;

import android.security.keystore.KeyProperties;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;



/** DidChangeBiometricsPlugin */
public class DidChangeBiometricsPlugin
    implements FlutterPlugin, MethodCallHandler, ActivityAware{
  /// The MethodChannel that will the communication between Flutter and native
  /// Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine
  /// and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity activity;
  private String KEY_NAME = "did_change_biometrics";
  private KeyStore keyStore;
  BiometricManager biometricManager;


  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    biometricManager = BiometricManager.from(activity);
  }

  @Override
  public void onDetachedFromActivity() {
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }



  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("check")) {
      authenticateFingerPrint(result);
    } else if (call.method.equals("registerSecretKey")) {
        int typeBiometric =  biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        if (typeBiometric
              == BiometricManager.BIOMETRIC_SUCCESS) {
         generateSecretKey(new KeyGenParameterSpec.Builder(
        KEY_NAME,
        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setUserAuthenticationRequired(true)
        .setInvalidatedByBiometricEnrollment(true)
        .build());
         System.out.println(" Valid registerSecretKey");    
        } else {
          removeSecretKey();
          System.out.println(" biometric_disenable: Error registerSecretKey"); 
          result.success("biometric_disenable");
        }
    
    } else {
      result.notImplemented();
    }
  }

  
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "did_change_biometrics");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    channel = null;
    activity = null;
  }
  private void generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) {
    KeyGenerator keyGenerator;
  try {
      keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
      keyGenerator.init(keyGenParameterSpec);
      keyGenerator.generateKey();
  } catch ( Exception e) {
    e.printStackTrace();
  }
}

private SecretKey getSecretKey() {
     try {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return ((SecretKey)keyStore.getKey(KEY_NAME, null));
  } catch ( Exception e) {
    // e.printStackTrace();
    throw new RuntimeException("Failed to get an instance of Cipher", e);
  }
}

private boolean removeSecretKey() {
  try {
      keyStore = KeyStore.getInstance("AndroidKeyStore");
      keyStore.load(null);
      keyStore.deleteEntry(KEY_NAME);
      return true;
      } catch (Exception e) {
          e.printStackTrace();
      }
  return false;
}

private Cipher getCipher() {
  try {//ww  w .  j ava2  s  .c o m
    return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
      + KeyProperties.BLOCK_MODE_CBC + "/"
      + KeyProperties.ENCRYPTION_PADDING_PKCS7);
} catch (Exception e) {
    throw new RuntimeException("Failed to get an instance of Cipher", e);
}
}

private void authenticateFingerPrint(Result result) {
  int typeBiometric = 990;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        typeBiometric =  biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
      } else {
        typeBiometric = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
      }
    System.out.println("--------------- " +   String.valueOf(typeBiometric));

    if (typeBiometric
          == BiometricManager.BIOMETRIC_SUCCESS) {
    Cipher cipher = getCipher();
    SecretKey secretKey = getSecretKey();
    if (secretKey == null) {
      generateSecretKey(new KeyGenParameterSpec.Builder(
          KEY_NAME,
          KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
          .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
          .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
          .setUserAuthenticationRequired(true)
          .setInvalidatedByBiometricEnrollment(true)
          .build());
    }
    try {
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      result.success("biometric_valid");
    } catch (KeyPermanentlyInvalidatedException e) {
      System.out.println("key has changed");
      result.success("biometric_did_change");
    } catch (InvalidKeyException e) {
      e.printStackTrace();
      if (secretKey == null){
        result.success("biometric_disenable");
      }else {
        result.success("biometric_invalid");
      }
    }
    } else if (typeBiometric
          == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED){
      result.success("biometric_disenable");
    } else {
      result.success("biometric_disenable");
    }
  }
}
