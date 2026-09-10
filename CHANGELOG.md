# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [0.0.22]

### Added

- Japan and Brazil data centers
- Push validation API
- Device Token registration API
- Voice template create, list, query, and delete APIs
- Push plan statistics and batch message lifecycle APIs
- App VIP status API

### Changed

- Completed typed request and response fields for Push, Schedule, Status, Plan, and Group Push
- Aligned Voice multipart upload and OPPO Image JSON URL requests with the official REST protocol
- Aligned Tag count, quota, status, and Plan Detail parameters and responses with the official REST protocol
- Aligned Jackson components on version 2.18.0 to avoid runtime incompatibility

### Fixed

- Corrected REST paths and parameter serialization that differed from the official API
- Added contract tests for request serialization, response parsing, and error responses
