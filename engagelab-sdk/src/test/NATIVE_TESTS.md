# Native SDK acceptance tests

Tests contain their own scenarios and assertions. They do not read TAPD, Excel,
`cases.json`, `TEST_CASE_DIR`, another SDK repository, or an external QA runner.
Fixed HTTP responses are local contract fixtures. Integration tests call the
SDK built from this checkout and compare its parsed values with the real wire response.

## Local contract tests

```sh
mvn -f engagelab-sdk/pom.xml test
```

No credentials or network API target are needed. The test process uses local HTTP
listeners. Four shared scenarios cover false/zero push fields, page size 101,
dynamic group AppKeys, and explicit APNs sandbox serialization. Environment checks
verify QA/production origin matching and runtime timeout configuration without
making production requests. Existing SDK tests remain in place.

## Real service tests

Copy `native.env.example` to an ignored local configuration file, fill it, and
export its variables (or inject them using your CI secret manager). This template
is optional: environment variables alone are sufficient.

```sh
# After exporting the required variables:
mvn -f engagelab-sdk/pom.xml -Pintegration test
```

`TEST_ENV=qa` requires a QA origin (`*.qa.jpushoa.com` or `*.txqa.jpushoa.com`).
`TEST_ENV=prod` requires a non-QA HTTPS origin. There is no implicit API target,
application identity, or device fallback. `ENGAGELAB_HTTP_TIMEOUT`,
`ENGAGELAB_WAIT_TIMEOUT`, and `ENGAGELAB_REQUEST_INTERVAL` are seconds.
Missing/invalid required parameters fail explicitly; selected tests do not silently skip.

Use `ENGAGELAB_MUTATION_MODE=isolated_write` to select write scenarios. For a
read-only validation-only selection, use `read_only` and this native filter:

```sh
mvn -f engagelab-sdk/pom.xml -Pintegration -Dtest=NativeIntegrationTest#fieldPassthrough test
```

The same command can target QA or production by changing the injected environment.
Production runs require a dedicated test application and explicit test targets;
production validation has not been performed as part of these changes.
API environment and `ENGAGELAB_APNS_PRODUCTION=true|false` are independent.

| Scenario | Runtime conditions | Assertions and cleanup |
|---|---|---|
| field_passthrough | App credentials and a test Android registration ID | Validate endpoint only; false/zero on wire and parsed message ID; no notification delivery claim |
| pagination_101 | isolated_write | Create 101 uniquely named plans; query 100+1 and 101; exact owned IDs and parsed fields; delete owned plans and verify zero remaining |
| group_push | isolated_write; GroupKey/Secret; at least two comma-separated member AppKeys; explicit target alias | Per-app success IDs and group ID match actual response; API acceptance only, not device delivery |
| apns_token | isolated_write; real 64-hex APNs Token; explicit APNs channel; matching application/Bundle ID | Register/query/re-register; preserve an existing mapping; delete only a mapping created by this test and verify absence |

Group fixture targets must already exist and resolve only to dedicated test devices
in each listed member application. Their setup/teardown is owned by the caller;
this test does not change membership or aliases. Run shared application tests
serially across repositories. Each plan run uses a unique prefix; the test registers
resource ownership before sending creation requests so ambiguous failures can be cleaned.
APNs assertions cover registration and mapping, not APNs delivery.

## Results

The native framework exits nonzero on assertion failure or blocked prerequisites.
Integration tests write per-scenario JSON into `ENGAGELAB_RESULTS_DIR` (default
`.test-results/`, ignored by Git), recording environment, checks and status.
No secret, raw Token, authorization header, or device ID is persisted. Native test
reports remain the authoritative execution result; TAPD publication is optional
post-processing outside this repository. A successful local fixture test is not
a real-service pass. Capture the SDK commit and test diff identity with run artifacts.


## Complete native acceptance suite

The full suite adds 128 local contract scenarios and 62 real-service scenarios.
The previous representative tests remain available as a smaller smoke selection.
Every full scenario has a stable `contract_*` or `qa_*` ID, used as its native test
name and result key. IDs are traceability labels, not external testcase definitions.
The tests contain explicit SDK calls, independent controls and assertions. No
migration generator, TAPD connection, `cases.json`, or another language runtime
is required after checkout. Add new API tests directly in this repository even
when no formal testcase has been authored yet.

```sh
mvn -f engagelab-sdk/pom.xml -Pintegration -Dtest=FullIntegrationTest test
```

`ENGAGELAB_FULL_RESULTS_DIR` selects the full-suite output directory (default
`.test-results/full`). `contract/` records local protocol/serialization checks;
`integration/` records actual service assertions as `pass`, `no_pass`, or `block`.
Both failures and missing prerequisites produce a nonzero framework exit status.
A shared setup can serve several native assertions in one workflow group. Selecting
one test can therefore prepare that group's isolated fixtures and controls.

Full QA groups cover devices/tags/aliases, 101-plan pagination and boundaries,
regid/alias batches, negative/error behavior, VIP/auth, APNs registration, group
push, OPPO image, voice multipart, schedules, message/plan statistics and physical
Android create/validate/withdraw. Production is supported by changing the injected
configuration; backend-only device fixture creation is deliberately QA-only.
Use dedicated production test resources and select the desired native tests.
This change has not been validated against production.

Additional full-suite environment inputs:

| Variable | Purpose |
|---|---|
| `ENGAGELAB_FIXTURE_REGISTRATION_IDS` | Two comma-separated dedicated Android targets with SDK registration/activity/timezone data; otherwise QA backend records are created, which may not satisfy schedules/statistics |
| `ENGAGELAB_GROUP_APP_SECRETS` | JSON mapping of explicitly listed group member AppKeys to secrets, for independent alias readback; inject through a secret manager |
| `ENGAGELAB_OPPO_IMAGE_URL` | Reachable 984x369 PNG below 1MiB |
| `ENGAGELAB_VOICE_FILE` | Complete MP3 voice ZIP; defaults to the repository fixture |
| `ENGAGELAB_VOICE_LANGUAGE` | Unoccupied valid voice language; default `en`; existing templates are preserved |
| `ENGAGELAB_SCHEDULE_DELAY` | Future single-trigger delay in seconds, minimum 45 |
| `ENGAGELAB_SCHEDULE_OBSERVATION` | Observation after trigger, minimum 15 seconds; date strings use UTC+8 |
| `ENGAGELAB_ADB_PATH` / `ENGAGELAB_ADB_SERIAL` | ADB executable and explicitly selected physical Android device |
| `ENGAGELAB_ANDROID_PACKAGE` / `ENGAGELAB_DEVICE_APP_KEY` | QA Demo package and verified AppKey |

Physical tests require an online matching QA Demo and an actual posted notification.
Validation additionally requires a positive log callback control and a bounded
observation beyond TTL. Backend fixtures cannot substitute for physical evidence.
Statistics require nonempty known message/RID/plan links; HMOS field verification
requires nonzero HMOS data. Missing data is blocked, never silently passed.

All newly owned plans, device records, tag/alias bindings, voices and schedules
are cleaned and read back. Existing fixture aliases are restored. `resources/`
is a private recovery journal of owned test resource IDs; keep this ignored output
private. Raw tokens, credentials and authorization headers are never saved.

Validation compares the frozen `PushResult` fields (`request_id`, `msg_id`).
The QA endpoint also returns legacy `sendno`/`all_num`, which are outside that
model. Missing wire `request_id` permits an empty parsed value. Physical absence
of delivery still requires all positive observation controls.
Schedule readback uses the API's flattened `push` representation; tests compare
written business fields. Schedule `MsgIds` may hold JSON result strings, while
`msgIds` holds extracted IDs. The test preserves the former and extracts IDs
for authoritative message linkage checks.

To supply a known lifecycle record, set both `ENGAGELAB_LIFECYCLE_MESSAGE_ID`
and `ENGAGELAB_LIFECYCLE_REGISTRATION_ID`. They must belong to this application
and resolve to a meaningful lifecycle record for that RID.
The test only reads that existing record and never deletes it. If omitted, an
owned message/RID is used. Unknown/no-effect records remain blocked. Optional
`error_code`/`error_message` may be absent on a normal record; the tests verify
valid defaults and exact preservation whenever these fields are returned.

## Formal case mapping

See `traceability/coverage-map.json` beside this guide for the full 571-case mapping.
This repository owns 190 of those scenarios. The file is optional documentation, never a runtime input.
Local default tests run without a QA connection; real-service tests remain opt-in and
need explicit environment/target parameters. No production credentials or execution
results are stored in the mapping. The original `test` branch includes the accepted
SDK fixes; this automation commit changes tests and test configuration only.
