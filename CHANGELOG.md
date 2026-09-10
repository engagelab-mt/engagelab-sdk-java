# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [0.0.22]

### Added

- `DataCenterHost`: added `JPN` and `BRA` for applications hosted in the Japan and Brazil data centers.
- `PushApi.validate(PushParam)`: added typed push validation with a `PushResult` response.
- `DeviceApi.registerToken(DeviceTokenRegisterParam)`: added vendor-token registration; `DeviceTokenRegisterResult` reports each token's registration ID, creation status, error code, and message.
- `VoiceApi`: added `create(language, file)`, `list()`, `get(language)`, and `delete(language)` for managing voice templates; `VoiceResult` exposes `language` and `fileUrl`.
- `StatusApi`: added `getPlanDetail(PlanDetailGetParam)` and `getBatchMessageLifecycle(List<String>)`.
- `AppApi.getVipStatus()`: added application VIP status and expiry-time lookup through `AppVipStatusResult`.

### Changed

- `PushApi.push`, `PushApi.validate`, Batch Push, Group Push, and scheduled push payloads: added `PushBody.voip`, Android `badgeSetNumber/fold`, Custom Message `testMessage/receiptId`, and Options `autoTruncation`.
- `NotificationMessage`, `NotificationMessage.Android`, and `CustomMessage`: notification content and custom-message content now support both text and structured JSON objects.
- `PushApi.batchPushByRegId` and `PushApi.batchPushByAlias`: `BatchPushResult` now exposes per-target errors and rate-limit information, including partial failures returned with a successful HTTP response.
- `ScheduleApi.schedulePush` and `ScheduleApi.updateSchedulePush`: added intelligent scheduling through `intelligent.backupTime`.
- `DeviceApi.setDevice`: continue using `DeviceSetParam.setTags(Tags)` to add or remove tags; use `DeviceSetParam.clearTags()` to clear all tags.
- `DeviceApi.getTagStatus`: now returns `TagStatusGetResult`, whose `result` field indicates whether the device has the requested tag.
- `DeviceApi.getTagCount` and `DeviceApi.getTagQuota`: now accept `List<String> tags` and one `Platform` value.
- `StatusApi.getMessageStatus`: result models now include plan ID, push content, Live Activity, VoIP, in-app message, HMOS subtotals, and HMOS channel statistics.
- `StatusApi.getMessageLifecycle`: `MessageLifecycleGetResult` now includes `errorCode`, `itime`, and `channel`.
- `PushPlanApi.queryList`: `PushPlanInfo` now exposes `planId` and `entityTag`; create and last-used timestamps remain `Long` millisecond values.
- `ImageApi.uploadOppoImage`: request fields use large-picture or small-picture URLs, and `ImageResult` exposes the corresponding picture IDs; invalid combinations are reported through the existing service error model.
- Dependencies: aligned Jackson components on version 2.18.0 to prevent Feign/Jackson runtime incompatibility.

### Fixed

- `GroupPushApi.push`: corrected requests that previously used an invalid relative path; `GroupPushResult.successes`, `errors`, and `groupMsgId` remain unchanged.
- `DeviceApi.getTagCount` and `DeviceApi.getTagQuota`: corrected multi-tag and platform parameter serialization.
- `StatusApi.getPlanDetail`: corrected the plan IDs and date-range parameters and completed the typed statistics result.
- Tests: added contract coverage for public request models, response parsing, Group Push results, partial Batch failures, and service error responses.
