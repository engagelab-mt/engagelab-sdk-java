# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [0.0.22]

### Added

- Added `DataCenterHost.JPN` and `DataCenterHost.BRA` for the Japan and Brazil AppPush endpoints.
- Added `PushApi.validate(PushParam)` for `POST /v4/push/validate`, using the same typed request and `PushResult` response as regular push.
- Added `DeviceApi.registerToken(DeviceTokenRegisterParam)` for `POST /v4/devices/token/registration_id`, including `platform`, `tokens`, `apns_production` and per-token `token`, `registration_id`, `is_new`, `code`, and `message` results.
- Added `VoiceApi` create, list, get, and delete operations for `/v4/voices`, including multipart `language` and `file` upload and `file_url` responses.
- Added `StatusApi.getPlanDetail(PlanDetailGetParam)` for plan statistics and `StatusApi.getBatchMessageLifecycle(List<String>)` for batch message lifecycle results.
- Added `AppApi.getVipStatus()` for `/v4/app/vip/status`, mapping `vip_status` and `vip_end_time`.

### Changed

- Completed the Push request model with `body.voip`, Android `badge_set_num` and `is_fold`, Message `test_message` and `receipt_id`, and Options `auto_truncation`.
- Allowed the documented String/JSON Object forms for notification `alert`, Android `alert`, and custom-message `msg_content`; retained dynamic maps for VoIP and vendor-specific payloads.
- Completed Batch Push responses with per-target `error.code/error.message` and top-level `rate_limit_info` fields.
- Added Schedule intelligent triggers through `intelligent.backup_time`; scheduled push payloads now share the completed Push model.
- Added type-safe Device tag updates: `setTags(Tags)` sends `{add,remove}`, while `clearTags()` sends `tags:""`; no public `Object` setter was added.
- Changed tag-device membership to return `TagStatusGetResult.result` without altering the tag-list response model.
- Changed tag count and tag quota methods to accept `List<String> tags` plus one `Platform`, and serialize tags as repeated query parameters.
- Completed message statistics with `plan_id`, `pushContent`, `live_activity`, `voip`, `inapp_message`, `sub_hmos`, and HMOS channel values; completed lifecycle results with `error_code`, `itime`, and `channel`.
- Corrected Push Plan list mapping from `push_id` to `plan_id` and added `entity_tag`, while retaining millisecond timestamps as `Long`.
- Kept OPPO Image as a JSON URL request using `big_picture_url` or `small_picture_url`, with `big_picture_id` and `small_picture_id` responses and server-side business validation.
- Aligned Jackson components on version 2.18.0 to prevent Feign/Jackson runtime incompatibility.

### Fixed

- Corrected Group Push to use the absolute `/v4/grouppush` path; the existing dynamic AppKey success/error response handling and `group_msgid` remain compatible.
- Corrected Tag repeated-key query serialization and the `platform` parameter shape for count and quota requests.
- Corrected Plan Detail query parameters to `plan_ids`, `start_date`, and `end_date` and completed the typed result mapping.
- Added contract coverage for request paths and serialization, union-shaped fields, response parsing, dynamic Group Push results, partial Batch failures, and service error responses.
