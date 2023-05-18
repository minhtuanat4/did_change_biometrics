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
  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {

    activity = binding.getActivity();

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
  Cipher cipher = getCipher();
  SecretKey secretKey = getSecretKey();
  if (secretKey == null) {
    generateSecretKey(new KeyGenParameterSpec.Builder(
        KEY_NAME,
        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setUserAuthenticationRequired(true)
        // Invalidate the keys if the user has registered a new biometric
        // credential, such as a new fingerprint. Can call this method only
        // on Android 7.0 (API level 24) or higher. The variable
        .setInvalidatedByBiometricEnrollment(true)
        .build());
  }
  try {
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    result.success("biometric_valid");
  } catch (KeyPermanentlyInvalidatedException e) {
    System.out.print("key has changed");
    result.success("biometric_did_change");

    generateSecretKey(new KeyGenParameterSpec.Builder(
        KEY_NAME,
        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setUserAuthenticationRequired(true)
        // Invalidate the keys if the user has registered a new biometric
        // credential, such as a new fingerprint. Can call this method only
        // on Android 7.0 (API level 24) or higher. The variable
        .setInvalidatedByBiometricEnrollment(true)
        .build());
  } catch (InvalidKeyException e) {
    e.printStackTrace();
    result.success("biometric_invalid");
  }
}
}
