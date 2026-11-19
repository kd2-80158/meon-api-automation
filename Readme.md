# DigiLocker Aadhaar E2E

The DigiLocker live test no longer reads config/config.json. It uses the fixed Meon UAT API and OTP relay endpoints, and reads identity/API credentials from secure runtime environment variables. Do not put those values in Java source or commit them.

Required runtime variables:

- AADHAAR_LIVE_ENABLED=true to explicitly opt into the live identity-sharing flow.
- AADHAAR_NUMBER: the authorized 12 digit test Aadhaar number.
- DIGILOCKER_SECURITY_PIN: the six digit DigiLocker PIN.
- AADHAAR_COMPANY_NAME and AADHAAR_SECRET_TOKEN: Meon UAT API credentials.
- AADHAAR_REDIRECT_URL: approved https://live.meon.co.in/ callback.
- DIGILOCKER_OTP_READ_TOKEN: bearer token for reading the OTP relay.

Optional: AADHAAR_OTP_MAX_RESENDS (default 3, allowed 0–6). Each OTP wait is two minutes. The phone app must POST SMS OTPs to https://api.extora.store/webhook/otp; the test reads from that relay using the same endpoint and the read token.

Run only the intended test after providing these variables through a secure runtime:

    mvn -Dtest=AadhaarFetchThroughDigiLockerE2E test

The test requests only the Aadhaar document and validates the exported response, required fields, document URLs, and masked Aadhaar suffix. It suppresses sensitive page and response details.
