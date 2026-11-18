# DigiLocker Aadhaar E2E source overlay

This overlay adds a guarded, end-to-end Aadhaar fetch flow to the existing Maven/TestNG project. Copy the `src/test/java` tree into the extracted `meonapiautomation` project, replacing `com/api/filters/LoggingFilter.java` with the safer version included here.

The flow is skipped unless `-Dmeon.aadhaar.live=true` is set. It reads credentials from environment variables and does not log Aadhaar numbers, OTPs, security PINs, OAuth URLs, API response bodies, or request headers.

Before an intentional run, provide these values in the test process environment:

- `AADHAAR_NUMBER`: the 12 digit Aadhaar number for the authorized test account.
- `DIGILOCKER_SECURITY_PIN`: the six digit DigiLocker security PIN.
- `AADHAAR_OTP_MAX_RESENDS`: optional; defaults to `3` resend attempts (each OTP wait is two minutes).

The OTP endpoint is intentionally fixed in the test to `http://10.112.35.159:8080/api/latest-otp`, as requested. Update the constant if the phone's IP address changes again.

Run only the intended test from the extracted project with:

```sh
mvn -Dtest=AadhaarFetchThroughDigiLockerE2E -Dmeon.aadhaar.live=true test
```

It requests only the `aadhaar` document. The final assertions validate the API response structure, required Aadhaar fields, document URLs, and masked Aadhaar suffix without printing the returned identity data. The test has not been run.
