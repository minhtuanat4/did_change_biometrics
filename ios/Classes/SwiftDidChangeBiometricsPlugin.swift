import Flutter
import UIKit
import LocalAuthentication

public class SwiftDidChangeBiometricsPlugin: NSObject, FlutterPlugin {
    
    let preferences = UserDefaults.standard
    
    let currenMyFingerprintKey = "currenMyFingerprintKey"
    
    let reason = "did_change_biometrics"
    
    var currenMyFingerprintData : Data?
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "did_change_biometrics", binaryMessenger: registrar.messenger())
        let instance = SwiftDidChangeBiometricsPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping  FlutterResult) {
        switch call.method {
            case "check":
                self.authenticateBiometrics { data,code in
                    switch code {
                    case 200:
                        result(data)
                    case -7:
                        result(FlutterError(code:"biometric_invalid",message:"Invalid biometric",details: data as Any))
                    case 998:
                        result(FlutterError(code:"biometric_valid",message:data,details: nil))
                    default:
                        result(FlutterError(code:"unknow", message: data, details: nil))
                    }}
            default:
                result(FlutterMethodNotImplemented)
                
                
            }
    }
//result:@escaping FlutterResult, reason : String
    
    private func authenticateBiometrics(complete: @escaping (String?, Int?) -> Void) {

        let policy = LAPolicy.deviceOwnerAuthenticationWithBiometrics
        let context = LAContext()
        var authError : NSError?
        
        guard context.canEvaluatePolicy(policy, error: &authError) else {
            complete(nil, authError?.code)
            return
        }
        
//        context.evaluatePolicy(policy, localizedReason: reason) { (success , error) in
//
//            guard success else {
//                complete(nil, -99)
//                return
//            }
//
            if self.preferences.object(forKey: self.currenMyFingerprintKey) != nil {
                self.currenMyFingerprintData = self.preferences.data(forKey:  self.currenMyFingerprintKey)
            }
            
            let newFingerprintData = context.evaluatedPolicyDomainState
            
            guard self.currenMyFingerprintData == nil || self.currenMyFingerprintData == newFingerprintData else {
                complete("biometric_did_change",200)
                return
            }
            
            if (self.currenMyFingerprintData == nil) {
                self.preferences.set(newFingerprintData, forKey: self.currenMyFingerprintKey)
                complete("FingerprintData firstly is created",998)
            } else {
                complete("FingerprintData was created",998)
            }
//        }
    }
    
}
