# DigiLocker Aadhaar E2E

The live TestNG flow uses the project's existing `JSONUtility.getAadhaar()` configuration path. Keep live-only values under `baseURL.aadhaar` in the local `config/config.json`:

- `live_test_enabled`: set to `true` only when intentionally running the live flow.
- `aadhaar_number`: 12 digit authorized test account number.
- `security_pin`: six digit DigiLocker security PIN.
- `otp_endpoint`: local phone OTP endpoint (currently `http://10.112.35.159:8080/api/latest-otp`).
- `otp_max_resends`: optional integer from 0 through 6; defaults to 3.

The config file contains existing API secrets, so it is ignored by Git and should remain local. The E2E class does not contain the Aadhaar number or PIN and does not log OTPs, page content, OAuth URLs, API response bodies, or request headers.

Run only this test from the project root:

```sh
mvn -Dtest=AadhaarFetchThroughDigiLockerE2E test
```

The live test runs only when `baseURL.aadhaar.live_test_enabled` is true. It requests only the Aadhaar document and validates the exported response structure, document URLs, and masked Aadhaar suffix.
