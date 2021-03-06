package com.cryptlex.lexactivator;

public class LexActivatorException extends Exception {

    int errorCode;

    public LexActivatorException(String message) {
        super(message);
    }

    public LexActivatorException(int errorCode) {
        super(getErrorMessage(errorCode));
        this.errorCode = errorCode;
    }

    public int getCode() {
        return this.errorCode;
    }

    public static String getErrorMessage(int errorCode) {
        String message;
        switch (errorCode) {

        case LA_E_FILE_PATH:
            message = "Invalid file path.";
            break;

        case LA_E_PRODUCT_FILE:
            message = "Invalid or corrupted product file.";
            break;

        case LA_E_PRODUCT_DATA:
            message = "Invalid product data.";
            break;

        case LA_E_PRODUCT_ID:
            message = "The product id is incorrect.";
            break;

        case LA_E_SYSTEM_PERMISSION:
            message = "Insufficent system permissions.";
            break;

        case LA_E_FILE_PERMISSION:
            message = "No permission to write to file.";
            break;

        case LA_E_WMIC:
            message = "Fingerprint couldn't be generated because Windows Management Instrumentation (WMI) service has been disabled.";
            break;

        case LA_E_TIME:
            message = "The difference between the network time and the system time is more than allowed clock offset.";
            break;

        case LA_E_INET:
            message = "Failed to connect to the server due to network error.";
            break;

        case LA_E_NET_PROXY:
            message = "Invalid network proxy.";
            break;

        case LA_E_HOST_URL:
            message = "Invalid Cryptlex host url.";
            break;

        case LA_E_BUFFER_SIZE:
            message = "The buffer size was smaller than required.";
            break;

        case LA_E_APP_VERSION_LENGTH:
            message = "App version length is more than 256 characters.";
            break;

        case LA_E_REVOKED:
            message = "The license has been revoked.";
            break;

        case LA_E_LICENSE_KEY:
            message = "Invalid license key.";
            break;

        case LA_E_LICENSE_TYPE:
            message = "Invalid license type. Make sure floating license is not being used.";
            break;

        case LA_E_OFFLINE_RESPONSE_FILE:
            message = "Invalid offline activation response file.";
            break;

        case LA_E_OFFLINE_RESPONSE_FILE_EXPIRED:
            message = "The offline activation response has expired.";
            break;

        case LA_E_ACTIVATION_LIMIT:
            message = "The license has reached it's allowed activations limit.";
            break;

        case LA_E_ACTIVATION_NOT_FOUND:
            message = "The license activation was deleted on the server.";
            break;

        case LA_E_DEACTIVATION_LIMIT:
            message = "The license has reached it's allowed deactivations limit.";
            break;

        case LA_E_TRIAL_NOT_ALLOWED:
            message = "Trial not allowed for the product.";
            break;

        case LA_E_TRIAL_ACTIVATION_LIMIT:
            message = "Your account has reached it's trial activations limit.";
            break;

        case LA_E_MACHINE_FINGERPRINT:
            message = "Machine fingerprint has changed since activation.";
            break;

        case LA_E_METADATA_KEY_LENGTH:
            message = "Metadata key length is more than 256 characters.";
            break;

        case LA_E_METADATA_VALUE_LENGTH:
            message = "Metadata value length is more than 256 characters.";
            break;

        case LA_E_ACTIVATION_METADATA_LIMIT:
            message = "The license has reached it's metadata fields limit.";
            break;

        case LA_E_TRIAL_ACTIVATION_METADATA_LIMIT:
            message = "The trial has reached it's metadata fields limit.";
            break;

        case LA_E_METADATA_KEY_NOT_FOUND:
            message = "The metadata key does not exist.";
            break;

        case LA_E_TIME_MODIFIED:
            message = "The system time has been tampered (backdated).";
            break;

        case LA_E_RELEASE_VERSION_FORMAT:
            message = "Invalid version format.";
            break;
        
        case LA_E_AUTHENTICATION_FAILED:
            message = "Incorrect email or password.";
            break;
            
        case LA_E_METER_ATTRIBUTE_NOT_FOUND:
            message = "The meter attribute does not exist.";
            break;
            
        case LA_E_METER_ATTRIBUTE_USES_LIMIT_REACHED:
            message = "The meter attribute has reached it's usage limit.";
            break;

        case LA_E_VM:
            message = "Application is being run inside a virtual machine / hypervisor, and activation has been disallowed in the VM.";
            break;

        case LA_E_COUNTRY:
            message = "Country is not allowed.";
            break;

        case LA_E_IP:
            message = "IP address is not allowed.";
            break;

        case LA_E_RATE_LIMIT:
            message = "Rate limit for API has reached, try again later.";
            break;

        case LA_E_SERVER:
            message = "Server error.";
            break;

        case LA_E_CLIENT:
            message = "Client error.";
            break;

        default:
            message = "Unknown error!";

        }
        return message;
    }

    /**
     * * Error Codes **
     */

    /*
     * CODE: LA_E_FILE_PATH
     * 
     * MESSAGE: Invalid file path.
     */
    public static final int LA_E_FILE_PATH = 40;

    /*
     * CODE: LA_E_PRODUCT_FILE
     * 
     * MESSAGE: Invalid or corrupted product file.
     */
    public static final int LA_E_PRODUCT_FILE = 41;

    /*
     * CODE: LA_E_PRODUCT_DATA
     * 
     * MESSAGE: Invalid product data.
     */
    public static final int LA_E_PRODUCT_DATA = 42;

    /*
     * CODE: LA_E_PRODUCT_ID
     * 
     * MESSAGE: The product id is incorrect.
     */
    public static final int LA_E_PRODUCT_ID = 43;

    /*
     * CODE: LA_E_SYSTEM_PERMISSION
     * 
     * MESSAGE: Insufficent system permissions. Occurs when LA_SYSTEM flag is used
     * but application is not run with admin privileges.
     */
    public static final int LA_E_SYSTEM_PERMISSION = 44;

    /*
     * CODE: LA_E_FILE_PERMISSION
     * 
     * MESSAGE: No permission to write to file.
     */
    public static final int LA_E_FILE_PERMISSION = 45;

    /*
     * CODE: LA_E_WMIC
     * 
     * MESSAGE: Fingerprint couldn't be generated because Windows Management
     * Instrumentation (WMI) service has been disabled. This error is specific to
     * Windows only.
     */
    public static final int LA_E_WMIC = 46;

    /*
     * CODE: LA_E_TIME
     * 
     * MESSAGE: The difference between the network time and the system time is more
     * than allowed clock offset.
     */
    public static final int LA_E_TIME = 47;

    /*
     * CODE: LA_E_INET
     * 
     * MESSAGE: Failed to connect to the server due to network error.
     */
    public static final int LA_E_INET = 48;

    /*
     * CODE: LA_E_NET_PROXY
     * 
     * MESSAGE: Invalid network proxy.
     */
    public static final int LA_E_NET_PROXY = 49;

    /*
     * CODE: LA_E_HOST_URL
     * 
     * MESSAGE: Invalid Cryptlex host url.
     */
    public static final int LA_E_HOST_URL = 50;

    /*
     * CODE: LA_E_BUFFER_SIZE
     * 
     * MESSAGE: The buffer size was smaller than required.
     */
    public static final int LA_E_BUFFER_SIZE = 51;

    /*
     * CODE: LA_E_APP_VERSION_LENGTH
     * 
     * MESSAGE: App version length is more than 256 characters.
     */
    public static final int LA_E_APP_VERSION_LENGTH = 52;

    /*
     * CODE: LA_E_REVOKED
     * 
     * MESSAGE: The license has been revoked.
     */
    public static final int LA_E_REVOKED = 53;

    /*
     * CODE: LA_E_LICENSE_KEY
     * 
     * MESSAGE: Invalid license key.
     */
    public static final int LA_E_LICENSE_KEY = 54;

    /*
     * CODE: LA_E_LICENSE_TYPE
     * 
     * MESSAGE: Invalid license type. Make sure floating license is not being used.
     */
    public static final int LA_E_LICENSE_TYPE = 55;

    /*
     * CODE: LA_E_OFFLINE_RESPONSE_FILE
     * 
     * MESSAGE: Invalid offline activation response file.
     */
    public static final int LA_E_OFFLINE_RESPONSE_FILE = 56;

    /*
     * CODE: LA_E_OFFLINE_RESPONSE_FILE_EXPIRED
     * 
     * MESSAGE: The offline activation response has expired.
     */
    public static final int LA_E_OFFLINE_RESPONSE_FILE_EXPIRED = 57;

    /*
     * CODE: LA_E_ACTIVATION_LIMIT
     * 
     * MESSAGE: The license has reached it's allowed activations limit.
     */
    public static final int LA_E_ACTIVATION_LIMIT = 58;

    /*
     * CODE: LA_E_ACTIVATION_NOT_FOUND
     * 
     * MESSAGE: The license activation was deleted on the server.
     */
    public static final int LA_E_ACTIVATION_NOT_FOUND = 59;

    /*
     * CODE: LA_E_DEACTIVATION_LIMIT
     * 
     * MESSAGE: The license has reached it's allowed deactivations limit.
     */
    public static final int LA_E_DEACTIVATION_LIMIT = 60;

    /*
     * CODE: LA_E_TRIAL_NOT_ALLOWED
     * 
     * MESSAGE: Trial not allowed for the product.
     */
    public static final int LA_E_TRIAL_NOT_ALLOWED = 61;

    /*
     * CODE: LA_E_TRIAL_ACTIVATION_LIMIT
     * 
     * MESSAGE: Your account has reached it's trial activations limit.
     */
    public static final int LA_E_TRIAL_ACTIVATION_LIMIT = 62;

    /*
     * CODE: LA_E_MACHINE_FINGERPRINT
     * 
     * MESSAGE: Machine fingerprint has changed since activation.
     */
    public static final int LA_E_MACHINE_FINGERPRINT = 63;

    /*
     * CODE: LA_E_METADATA_KEY_LENGTH
     * 
     * MESSAGE: Metadata key length is more than 256 characters.
     */
    public static final int LA_E_METADATA_KEY_LENGTH = 64;

    /*
     * CODE: LA_E_METADATA_VALUE_LENGTH
     * 
     * MESSAGE: Metadata value length is more than 256 characters.
     */
    public static final int LA_E_METADATA_VALUE_LENGTH = 65;

    /*
     * CODE: LA_E_ACTIVATION_METADATA_LIMIT
     * 
     * MESSAGE: The license has reached it's metadata fields limit.
     */
    public static final int LA_E_ACTIVATION_METADATA_LIMIT = 66;

    /*
     * CODE: LA_E_TRIAL_ACTIVATION_METADATA_LIMIT
     * 
     * MESSAGE: The trial has reached it's metadata fields limit.
     */
    public static final int LA_E_TRIAL_ACTIVATION_METADATA_LIMIT = 67;

    /*
     * CODE: LA_E_METADATA_KEY_NOT_FOUND
     * 
     * MESSAGE: The metadata key does not exist.
     */
    public static final int LA_E_METADATA_KEY_NOT_FOUND = 68;

    /*
     * CODE: LA_E_TIME_MODIFIED
     * 
     * MESSAGE: The system time has been tampered (backdated).
     */
    public static final int LA_E_TIME_MODIFIED = 69;

    /*
     * CODE: LA_E_RELEASE_VERSION_FORMAT
     * 
     * MESSAGE: Invalid version format.
     */
    public static final int LA_E_RELEASE_VERSION_FORMAT = 70;
    
    /*
     * CODE: LA_E_AUTHENTICATION_FAILED
     * 
     * MESSAGE: Incorrect email or password.
     */
    public static final int LA_E_AUTHENTICATION_FAILED = 71;
    
    /*
     * CODE: LA_E_METER_ATTRIBUTE_NOT_FOUND
     * 
     * MESSAGE: The meter attribute does not exist.
     */
    public static final int LA_E_METER_ATTRIBUTE_NOT_FOUND = 72;
    
    /*
     * CODE: LA_E_METER_ATTRIBUTE_USES_LIMIT_REACHED
     * 
     * MESSAGE: The meter attribute has reached it's usage limit.
     */
    public static final int LA_E_METER_ATTRIBUTE_USES_LIMIT_REACHED = 73;

    /*
     * CODE: LA_E_VM
     * 
     * MESSAGE: Application is being run inside a virtual machine / hypervisor, and
     * activation has been disallowed in the VM.
     */
    public static final int LA_E_VM = 80;

    /*
     * CODE: LA_E_COUNTRY
     * 
     * MESSAGE: Country is not allowed.
     */
    public static final int LA_E_COUNTRY = 81;

    /*
     * CODE: LA_E_IP
     * 
     * MESSAGE: IP address is not allowed.
     */
    public static final int LA_E_IP = 82;

    /*
     * CODE: LA_E_RATE_LIMIT
     * 
     * MESSAGE: Rate limit for API has reached; try again later.
     */
    public static final int LA_E_RATE_LIMIT = 90;

    /*
     * CODE: LA_E_SERVER
     * 
     * MESSAGE: Server error.
     */
    public static final int LA_E_SERVER = 91;

    /*
     * CODE: LA_E_CLIENT
     * 
     * MESSAGE: Client error.
     */
    public static final int LA_E_CLIENT = 92;

}
