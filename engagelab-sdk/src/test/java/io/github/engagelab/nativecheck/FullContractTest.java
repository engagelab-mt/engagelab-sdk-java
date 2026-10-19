package io.github.engagelab.nativecheck;

import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;

class FullContractTest {
  static final Map<String, String[]> METHOD_PATHS = new HashMap<>();

  static {
    METHOD_PATHS.put("push", new String[] {"POST", "/v4/push"});
    METHOD_PATHS.put("validate", new String[] {"POST", "/v4/push/validate"});
    METHOD_PATHS.put("group", new String[] {"POST", "/v4/grouppush"});
    METHOD_PATHS.put("batch_regid", new String[] {"POST", "/v4/batch/push/regid"});
    METHOD_PATHS.put("batch_alias", new String[] {"POST", "/v4/batch/push/alias"});
    METHOD_PATHS.put("withdraw", new String[] {"DELETE", "/v4/push/withdraw/{msg_id}"});
    METHOD_PATHS.put("device_get", new String[] {"GET", "/v4/devices/{registration_id}"});
    METHOD_PATHS.put("device_set", new String[] {"POST", "/v4/devices/{registration_id}"});
    METHOD_PATHS.put("device_delete", new String[] {"DELETE", "/v4/devices/{registration_id}"});
    METHOD_PATHS.put("device_status", new String[] {"POST", "/v4/devices/status/"});
    METHOD_PATHS.put("token", new String[] {"POST", "/v4/devices/token/registration_id"});
    METHOD_PATHS.put("tags", new String[] {"GET", "/v4/tags/"});
    METHOD_PATHS.put("tag_set", new String[] {"POST", "/v4/tags/{tag}"});
    METHOD_PATHS.put("tag_delete", new String[] {"DELETE", "/v4/tags/{tag}"});
    METHOD_PATHS.put(
        "tag_status", new String[] {"GET", "/v4/tags/{tag}/registration_ids/{registration_id}"});
    METHOD_PATHS.put("tag_count", new String[] {"GET", "/v4/tags_count"});
    METHOD_PATHS.put("tag_quota", new String[] {"GET", "/v4/tags/quota-info"});
    METHOD_PATHS.put("alias_get", new String[] {"GET", "/v4/aliases/{alias}"});
    METHOD_PATHS.put("alias_delete", new String[] {"DELETE", "/v4/aliases/{alias}"});
    METHOD_PATHS.put("schedule_create", new String[] {"POST", "/v4/schedules"});
    METHOD_PATHS.put("schedule_update", new String[] {"PUT", "/v4/schedules/{schedule_id}"});
    METHOD_PATHS.put("schedule_get", new String[] {"GET", "/v4/schedules/{schedule_id}"});
    METHOD_PATHS.put("schedule_delete", new String[] {"DELETE", "/v4/schedules/{schedule_id}"});
    METHOD_PATHS.put("schedule_list", new String[] {"GET", "/v4/schedules"});
    METHOD_PATHS.put("schedule_msgs", new String[] {"GET", "/v4/schedules/{schedule_id}/msg-ids"});
    METHOD_PATHS.put("status", new String[] {"GET", "/v4/status/detail"});
    METHOD_PATHS.put("lifecycle", new String[] {"GET", "/v4/status/message"});
    METHOD_PATHS.put("batch_lifecycle", new String[] {"GET", "/v4/status/batch/message"});
    METHOD_PATHS.put("users", new String[] {"GET", "/v4/status/users"});
    METHOD_PATHS.put("plan_status", new String[] {"GET", "/v4/status/plan/detail"});
    METHOD_PATHS.put("plan_create", new String[] {"POST", "/v4/push_plan"});
    METHOD_PATHS.put("plan_list", new String[] {"GET", "/v4/push_plan/list"});
    METHOD_PATHS.put("plan_msgs", new String[] {"GET", "/v4/status/plan/msg/"});
    METHOD_PATHS.put("plan_delete", new String[] {"DELETE", "/v4/push_plan/{plan_id}"});
    METHOD_PATHS.put(
        "plan_batch_delete", new String[] {"DELETE", "/v4/push_plan/batch/{plan_ids}"});
    METHOD_PATHS.put("voice_create", new String[] {"POST", "/v4/voices"});
    METHOD_PATHS.put("voices", new String[] {"GET", "/v4/voices"});
    METHOD_PATHS.put("voice_get", new String[] {"GET", "/v4/voices/{language}"});
    METHOD_PATHS.put("voice_delete", new String[] {"DELETE", "/v4/voices/{language}"});
    METHOD_PATHS.put("image", new String[] {"POST", "/v4/image/oppo"});
    METHOD_PATHS.put("vip", new String[] {"GET", "/v4/app/vip/status"});
    METHOD_PATHS.put("push_raw", new String[] {"POST", "/v4/push"});
    METHOD_PATHS.put("schedule_raw", new String[] {"POST", "/v4/schedules"});
    METHOD_PATHS.put("legacy_pushbody", new String[] {"POST", "/v4/push/validate"});
  }

  @TestFactory
  Stream<DynamicTest> fullContract() {
    return Arrays.asList(
        DynamicTest.dynamicTest(
            "contract_9cdebf8d4bd9 删除别名结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_9cdebf8d4bd9",
                        "删除别名结构化错误解码",
                        "alias_delete",
                        "[\"alias\",[\"android\"]]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_f0a82aa8d813 查询别名结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_f0a82aa8d813",
                        "查询别名结构化错误解码",
                        "alias_get",
                        "[\"alias\",[\"android\"]]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_5155df98938b 批量无限流false",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_5155df98938b",
                        "批量无限流false",
                        "batch_regid",
                        "[{\"requests\":[]}]",
                        200,
                        "{\"results\":{\"ridA\":{\"target\":\"ridA\",\"success\":true,\"msg_id\":123},\"ridB\":{\"target\":\"ridB\",\"success\":false,\"error\":{\"code\":21003,\"message\":\"bad-target\"}}},\"rate_limit_info\":{\"message\":\"limited\",\"rate_limit_occurred\":false}}",
                        "null",
                        "{\"results\":{\"ridA\":{\"target\":\"ridA\",\"success\":true,\"msg_id\":123},\"ridB\":{\"target\":\"ridB\",\"success\":false,\"error\":{\"code\":21003,\"message\":\"bad-target\"}}},\"rate_limit_info\":{\"message\":\"limited\",\"rate_limit_occurred\":false}}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_da7062b4b0b0 批量部分失败及顶层限流",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_da7062b4b0b0",
                        "批量部分失败及顶层限流",
                        "batch_regid",
                        "[{\"requests\":[]}]",
                        200,
                        "{\"results\":{\"ridA\":{\"target\":\"ridA\",\"success\":true,\"msg_id\":123},\"ridB\":{\"target\":\"ridB\",\"success\":false,\"error\":{\"code\":21003,\"message\":\"bad-target\"}}},\"rate_limit_info\":{\"message\":\"limited\",\"rate_limit_occurred\":true}}",
                        "null",
                        "{\"results\":{\"ridA\":{\"target\":\"ridA\",\"success\":true,\"msg_id\":123},\"ridB\":{\"target\":\"ridB\",\"success\":false,\"error\":{\"code\":21003,\"message\":\"bad-target\"}}},\"rate_limit_info\":{\"message\":\"limited\",\"rate_limit_occurred\":true}}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_ac3d49e7bc4c 按别名批量单推结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_ac3d49e7bc4c",
                        "按别名批量单推结构化错误解码",
                        "batch_alias",
                        "[{\"requests\":[]}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_259297710fd6 按注册ID批量单推结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_259297710fd6",
                        "按注册ID批量单推结构化错误解码",
                        "batch_regid",
                        "[{\"requests\":[{\"target\":\"rid\",\"platform\":\"android\",\"notification\":{\"alert\":\"demo\"}}]}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_caddd5567f23 Jackson运行时依赖回归",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_caddd5567f23",
                        "Jackson运行时依赖回归",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_9dbfdd925496 批量新旧msgId访问器",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_9dbfdd925496",
                        "批量新旧msgId访问器",
                        "accessors",
                        "[]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        "{\"new\":123,\"old\":456}",
                        "\"accessors\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_2c0a8a27ee39 既有pushObject入口",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_2c0a8a27ee39",
                        "既有pushObject入口",
                        "push_raw",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}},\"request_id\":\"compat-1\",\"custom_args\":{\"zero\":0,\"false\":false,\"none\":null,\"unicode\":\"兼容性\",\"nested\":[{\"n\":0,\"v\":null}]}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}},\"request_id\":\"compat-1\",\"custom_args\":{\"zero\":0,\"false\":false,\"unicode\":\"兼容性\",\"nested\":[{\"n\":0}]}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_93d3132972ad 既有scheduleObject入口",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_93d3132972ad",
                        "既有scheduleObject入口",
                        "schedule_raw",
                        "[{\"name\":\"compat-periodical\",\"enabled\":false,\"trigger\":{\"periodical\":{\"start\":\"2030-01-01"
                            + " 00:00:00\",\"end\":\"2030-01-03"
                            + " 00:00:00\",\"time\":\"12:00:00\",\"frequency\":1,\"time_unit\":\"day\",\"point\":[\"1\"],\"zone_type\":1}},\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}},\"request_id\":\"compat-1\",\"custom_args\":{\"zero\":0,\"false\":false,\"none\":null,\"unicode\":\"兼容性\",\"nested\":[{\"n\":0,\"v\":null}]}}}]",
                        200,
                        "{\"schedule_id\":\"s1\",\"name\":\"compat-periodical\"}",
                        "{\"name\":\"compat-periodical\",\"enabled\":false,\"trigger\":{\"periodical\":{\"start\":\"2030-01-01"
                            + " 00:00:00\",\"end\":\"2030-01-03"
                            + " 00:00:00\",\"time\":\"12:00:00\",\"frequency\":1,\"time_unit\":\"day\",\"point\":[\"1\"],\"zone_type\":1}},\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}},\"request_id\":\"compat-1\",\"custom_args\":{\"zero\":0,\"false\":false,\"none\":null,\"unicode\":\"兼容性\",\"nested\":[{\"n\":0,\"v\":null}]}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_dfa2046dabe5 仅修改别名不修改标签",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_dfa2046dabe5",
                        "仅修改别名不修改标签",
                        "device_set",
                        "[\"rid\",{\"alias\":\"alias-new\"}]",
                        200,
                        "\"success\"",
                        "{\"alias\":\"alias-new\"}",
                        "null",
                        "\"no-tags\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_dfb4e0c04755 删除设备结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_dfb4e0c04755",
                        "删除设备结构化错误解码",
                        "device_delete",
                        "[\"rid\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_72a44c83c3bf 批量查询设备状态结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_72a44c83c3bf",
                        "批量查询设备状态结构化错误解码",
                        "device_status",
                        "[{\"registration_ids\":[\"rid\"]}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_0ecfd4e41741 查询设备结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_0ecfd4e41741",
                        "查询设备结构化错误解码",
                        "device_get",
                        "[\"rid\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_d9c339d62287 清空标签",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_d9c339d62287",
                        "清空标签",
                        "device_set",
                        "[\"rid\",{\"tags\":\"\"}]",
                        200,
                        "\"success\"",
                        "{\"tags\":\"\"}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_79427730138c 设置设备结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_79427730138c",
                        "设置设备结构化错误解码",
                        "device_set",
                        "[\"rid\",{\"alias\":\"demo\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_ebdb2613f795 分组推送成功结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_ebdb2613f795",
                        "分组推送成功结构化错误解码",
                        "group",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_2dfd142e4cb3 动态AppKey混合结果",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_2dfd142e4cb3",
                        "动态AppKey混合结果",
                        "group",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"appA\":{\"request_id\":\"r1\",\"msg_id\":\"123\"},\"appB\":{\"error\":{\"code\":21003,\"message\":\"bad\"}},\"group_msgid\":\"g1\"}",
                        "null",
                        "{\"group_msgid\":\"g1\",\"successes\":{\"appA\":{\"msg_id\":\"123\"}},\"errors\":{\"appB\":{\"code\":21003,\"message\":\"bad\"}}}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_687d54787e11 独立Group认证",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_687d54787e11",
                        "独立Group认证",
                        "group",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"group_msgid\":\"g1\"}",
                        "null",
                        "null",
                        "\"group-auth\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_bf68e6a9401c 上传OPPO大图URL结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_bf68e6a9401c",
                        "上传OPPO大图URL结构化错误解码",
                        "image",
                        "[{\"big_picture_url\":\"https://example.test/a.png\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_5eb9bfe9737c 双URL错误透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_5eb9bfe9737c",
                        "双URL错误透传",
                        "image",
                        "[{\"big_picture_url\":\"https://example.test/big.png\",\"small_picture_url\":\"https://example.test/small.png\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"two urls\"}}",
                        "{\"big_picture_url\":\"https://example.test/big.png\",\"small_picture_url\":\"https://example.test/small.png\"}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_4c035e91ff51 大图URL和大图ID",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_4c035e91ff51",
                        "大图URL和大图ID",
                        "image",
                        "[{\"big_picture_url\":\"https://example.test/big.png\"}]",
                        200,
                        "{\"big_picture_id\":\"big-1\"}",
                        "{\"big_picture_url\":\"https://example.test/big.png\"}",
                        "{\"big_picture_id\":\"big-1\"}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_f8d7833139db 小图URL和小图ID",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_f8d7833139db",
                        "小图URL和小图ID",
                        "image",
                        "[{\"small_picture_url\":\"https://example.test/small.png\"}]",
                        200,
                        "{\"small_picture_id\":\"small-1\"}",
                        "{\"small_picture_url\":\"https://example.test/small.png\"}",
                        "{\"small_picture_id\":\"small-1\"}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_875843e4ebe6 创建推送计划结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_875843e4ebe6",
                        "创建推送计划结构化错误解码",
                        "plan_create",
                        "[{\"plan_description\":\"sdk-demo\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_11805d2b6e51 删除推送计划结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_11805d2b6e51",
                        "删除推送计划结构化错误解码",
                        "plan_delete",
                        "[{\"planId\":\"p1\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_3a07b7732ac3 批量删除推送计划结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_3a07b7732ac3",
                        "批量删除推送计划结构化错误解码",
                        "plan_batch_delete",
                        "[[\"p1\",\"p2\"]]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_1c0f0c91c0ce 查询推送计划列表结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_1c0f0c91c0ce",
                        "查询推送计划列表结构化错误解码",
                        "plan_list",
                        "[{\"pageIndex\":1,\"pageSize\":20,\"sendSource\":null,\"searchDescription\":\"\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_231ace1b89ea 查询计划关联消息结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_231ace1b89ea",
                        "查询计划关联消息结构化错误解码",
                        "plan_msgs",
                        "[{\"planIds\":\"p1\",\"startDate\":\"2026-10-01\",\"endDate\":\"2026-10-02\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_9edb7086dd58 计划毫秒时间戳",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_9edb7086dd58",
                        "计划毫秒时间戳",
                        "plan_list",
                        "[{\"pageIndex\":1,\"pageSize\":20,\"sendSource\":null,\"searchDescription\":\"\"}]",
                        200,
                        "{\"push_plan_info\":[{\"plan_id\":\"p1\",\"entity_tag\":\"e1\",\"create_time\":1750000000123,\"last_used_time\":1750000000999}],\"total\":1}",
                        "null",
                        "{\"push_plan_info\":[{\"plan_id\":\"p1\",\"entity_tag\":\"e1\",\"create_time\":1750000000123,\"last_used_time\":1750000000999}]}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_e8824fdee833 Android alert对象",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_e8824fdee833",
                        "Android alert对象",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"alert\":{\"title\":\"标题\",\"body\":\"正文\"}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"alert\":{\"title\":\"标题\",\"body\":\"正文\"}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_be95bbb49b74 Android徽标显式零值",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_be95bbb49b74",
                        "Android徽标显式零值",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"badge_set_num\":0}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"badge_set_num\":0}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_33c08308bc82 Android徽标正值",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_33c08308bc82",
                        "Android徽标正值",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"badge_set_num\":9}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"badge_set_num\":9}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_a1836b411d36 Android折叠true",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_a1836b411d36",
                        "Android折叠true",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"is_fold\":true}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"is_fold\":true}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_1678c0d073d9 Android折叠显式false",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_1678c0d073d9",
                        "Android折叠显式false",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"is_fold\":false}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"is_fold\":false}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_59cc473e4f61 HMOS通知字段",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_59cc473e4f61",
                        "HMOS通知字段",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"hmos\":{\"alert\":\"demo\",\"title\":\"title\",\"category\":\"category\",\"large_icon\":\"https://example.test/icon\",\"intent\":{\"url\":\"demo://open\"},\"badge_add_num\":1,\"badge_set_num\":0,\"test_message\":false,\"receipt_id\":\"receipt\",\"extras\":{\"n\":0},\"style\":1,\"inbox_content\":[\"a\",\"b\"],\"push_type\":0,\"extra_data\":\"data\",\"display_foreground\":\"1\"}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"hmos\":{\"alert\":\"demo\",\"title\":\"title\",\"category\":\"category\",\"large_icon\":\"https://example.test/icon\",\"intent\":{\"url\":\"demo://open\"},\"badge_add_num\":1,\"badge_set_num\":0,\"test_message\":false,\"receipt_id\":\"receipt\",\"extras\":{\"n\":0},\"style\":1,\"inbox_content\":[\"a\",\"b\"],\"push_type\":0,\"extra_data\":\"data\",\"display_foreground\":\"1\"}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_4d70e82c7224 LiveActivity连字符和零值",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_4d70e82c7224",
                        "LiveActivity连字符和零值",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"live_activity\":{\"ios\":{\"attributes-type\":\"Demo\",\"content-state\":{\"n\":0},\"dismissal-date\":0,\"stale-date\":1750000000,\"relevance-score\":0,\"apns-priority\":10}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"live_activity\":{\"ios\":{\"attributes-type\":\"Demo\",\"content-state\":{\"n\":0},\"dismissal-date\":0,\"stale-date\":1750000000,\"relevance-score\":0,\"apns-priority\":10}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_be624be5b5d1 VoIP任意JSON对象",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_be624be5b5d1",
                        "VoIP任意JSON对象",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"voip\":{\"call_id\":\"c1\",\"nested\":{\"video\":true}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"voip\":{\"call_id\":\"c1\",\"nested\":{\"video\":true}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_3e0616162c43 alias目标形态",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_3e0616162c43",
                        "alias目标形态",
                        "validate",
                        "[{\"to\":{\"alias\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"alias\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_89d8dd7a2551 all目标形态",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_89d8dd7a2551",
                        "all目标形态",
                        "validate",
                        "[{\"to\":\"all\",\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":\"all\",\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_044219309d70 fcm厂商配置嵌套透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_044219309d70",
                        "fcm厂商配置嵌套透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"fcm\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"fcm\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_395fa770d3d7 hmos厂商配置嵌套透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_395fa770d3d7",
                        "hmos厂商配置嵌套透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"hmos\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"hmos\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_febbdf980a2e honor厂商配置嵌套透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_febbdf980a2e",
                        "honor厂商配置嵌套透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"honor\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"honor\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_31617ee7a925 huawei厂商配置嵌套透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_31617ee7a925",
                        "huawei厂商配置嵌套透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"huawei\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"huawei\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_6ce4022da7de iOS连字符字段",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_6ce4022da7de",
                        "iOS连字符字段",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"ios\":{\"content-available\":true,\"mutable-content\":true,\"thread-id\":\"t\",\"interruption-level\":\"active\"}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"ios\":{\"content-available\":true,\"mutable-content\":true,\"thread-id\":\"t\",\"interruption-level\":\"active\"}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_d0836a5e1fa7 live_activity_id目标形态",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_d0836a5e1fa7",
                        "live_activity_id目标形态",
                        "validate",
                        "[{\"to\":{\"live_activity_id\":\"live1\"},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"live_activity_id\":\"live1\"},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_dcf476eb4731 meizu厂商配置嵌套透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_dcf476eb4731",
                        "meizu厂商配置嵌套透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"meizu\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"meizu\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_2fa3c3093257 oppo厂商配置嵌套透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_2fa3c3093257",
                        "oppo厂商配置嵌套透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"oppo\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"oppo\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_863a2c664451 registration_id目标形态",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_863a2c664451",
                        "registration_id目标形态",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_0e68ba4d8b3c seg目标形态",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_0e68ba4d8b3c",
                        "seg目标形态",
                        "validate",
                        "[{\"to\":{\"seg\":{\"id\":\"seg1\"}},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"seg\":{\"id\":\"seg1\"}},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_8a0c098f4fbb tag_and目标形态",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_8a0c098f4fbb",
                        "tag_and目标形态",
                        "validate",
                        "[{\"to\":{\"tag_and\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"tag_and\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_aafabb8f0d5f tag_not目标形态",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_aafabb8f0d5f",
                        "tag_not目标形态",
                        "validate",
                        "[{\"to\":{\"tag_not\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"tag_not\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_79b6e40c5a99 tag目标形态",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_79b6e40c5a99",
                        "tag目标形态",
                        "validate",
                        "[{\"to\":{\"tag\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"tag\":[\"target1\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_b835b5944185 vivo厂商配置嵌套透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_b835b5944185",
                        "vivo厂商配置嵌套透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"vivo\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"vivo\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_a11a03885d19 xiaomi厂商配置嵌套透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_a11a03885d19",
                        "xiaomi厂商配置嵌套透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"xiaomi\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"third_party_channel\":{\"xiaomi\":{\"distribution_new\":\"secondary_push\",\"data\":{\"n\":0,\"flag\":false}}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_e656c8187753 创建通知推送结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_e656c8187753",
                        "创建通知推送结构化错误解码",
                        "push",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_c903a69359a8 可选新增字段未设置",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_c903a69359a8",
                        "可选新增字段未设置",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_7ac4ecc638d4 回执ID透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_7ac4ecc638d4",
                        "回执ID透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"receipt_id\":\"receipt-contract\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"receipt_id\":\"receipt-contract\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_6e726e41edc4 多语言嵌套透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_6e726e41edc4",
                        "多语言嵌套透传",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"multi_language\":{\"zh\":{\"content\":\"你好\",\"title\":\"通知\",\"subtitle\":\"副标题\"},\"en\":{\"content\":\"hello\"}}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"multi_language\":{\"zh\":{\"content\":\"你好\",\"title\":\"通知\",\"subtitle\":\"副标题\"},\"en\":{\"content\":\"hello\"}}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_26a01b29a803 撤回推送结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_26a01b29a803",
                        "撤回推送结构化错误解码",
                        "withdraw",
                        "[\"m1\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_f60a65daa94a 校验推送结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_f60a65daa94a",
                        "校验推送结构化错误解码",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_938d0727c229 根对象动态参数",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_938d0727c229",
                        "根对象动态参数",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}},\"from\":\"source\",\"request_id\":\"r1\",\"custom_args\":{\"trace\":\"t1\",\"n\":0}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}},\"from\":\"source\",\"request_id\":\"r1\",\"custom_args\":{\"trace\":\"t1\",\"n\":0}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_04b1a35b7394 测试消息false",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_04b1a35b7394",
                        "测试消息false",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"test_message\":false}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"test_message\":false}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_25935b05f837 测试消息true",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_25935b05f837",
                        "测试消息true",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"test_message\":true}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"test_message\":true}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_3d1e6a7fa88c 自动截断false",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_3d1e6a7fa88c",
                        "自动截断false",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"auto_truncation\":false}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"auto_truncation\":false}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_7ecd20648db7 自动截断true",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_7ecd20648db7",
                        "自动截断true",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"auto_truncation\":true}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"options\":{\"auto_truncation\":true}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_aeab5f3b04f3 自定义消息字符串",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_aeab5f3b04f3",
                        "自定义消息字符串",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"msg_content\":\"订单通知\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"msg_content\":\"订单通知\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_e2cc1dc0f016 自定义消息对象",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_e2cc1dc0f016",
                        "自定义消息对象",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"msg_content\":{\"order\":123,\"items\":[\"a\"]}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"},\"message\":{\"msg_content\":{\"order\":123,\"items\":[\"a\"]}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_5c3fb99e56a3 通知alert对象",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_5c3fb99e56a3",
                        "通知alert对象",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":{\"title\":\"标题\",\"body\":\"正文\"}}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":{\"title\":\"标题\",\"body\":\"正文\"}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_34753ccf040e 分页查询定时任务结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_34753ccf040e",
                        "分页查询定时任务结构化错误解码",
                        "schedule_list",
                        "[{\"page\":1}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_a006064310d2 创建定时任务结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_a006064310d2",
                        "创建定时任务结构化错误解码",
                        "schedule_create",
                        "[{\"name\":\"sdk-demo\",\"enabled\":true,\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}},\"trigger\":{\"single\":{\"time\":\"2030-01-02"
                            + " 12:00:00\",\"zone_type\":1}}}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_5e6a52b60549 删除定时任务结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_5e6a52b60549",
                        "删除定时任务结构化错误解码",
                        "schedule_delete",
                        "[\"s1\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_f5c3f9d7b04b 周期定时原调用",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_f5c3f9d7b04b",
                        "周期定时原调用",
                        "schedule_create",
                        "[{\"name\":\"compat-periodical\",\"enabled\":false,\"trigger\":{\"periodical\":{\"start\":\"2030-01-01"
                            + " 00:00:00\",\"end\":\"2030-01-03"
                            + " 00:00:00\",\"time\":\"12:00:00\",\"frequency\":1,\"time_unit\":\"DAY\",\"point\":[\"1\"],\"zone_type\":1}},\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}}]",
                        200,
                        "{\"schedule_id\":\"s1\",\"name\":\"compat-periodical\"}",
                        "{\"name\":\"compat-periodical\",\"enabled\":false,\"trigger\":{\"periodical\":{\"start\":\"2030-01-01"
                            + " 00:00:00\",\"end\":\"2030-01-03"
                            + " 00:00:00\",\"time\":\"12:00:00\",\"frequency\":1,\"time_unit\":\"DAY\",\"point\":[\"1\"],\"zone_type\":1}},\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_a24cb7dc7fc4 嵌套推送新增字段",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_a24cb7dc7fc4",
                        "嵌套推送新增字段",
                        "schedule_create",
                        "[{\"name\":\"sdk-demo\",\"enabled\":true,\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"is_fold\":false,\"badge_set_num\":0}},\"options\":{\"auto_truncation\":false}}},\"trigger\":{\"single\":{\"time\":\"2030-01-02"
                            + " 12:00:00\",\"zone_type\":1}}}]",
                        200,
                        "{}",
                        "{\"name\":\"sdk-demo\",\"enabled\":true,\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\",\"android\":{\"is_fold\":false,\"badge_set_num\":0}},\"options\":{\"auto_truncation\":false}}},\"trigger\":{\"single\":{\"time\":\"2030-01-02"
                            + " 12:00:00\",\"zone_type\":1}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_43e45479ade7 智能定时创建备用时间",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_43e45479ade7",
                        "智能定时创建备用时间",
                        "schedule_create",
                        "[{\"name\":\"sdk-demo\",\"enabled\":true,\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}},\"trigger\":{\"intelligent\":{\"backup_time\":\"2030-01-02"
                            + " 12:00:00\"}}}]",
                        200,
                        "{}",
                        "{\"name\":\"sdk-demo\",\"enabled\":true,\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}},\"trigger\":{\"intelligent\":{\"backup_time\":\"2030-01-02"
                            + " 12:00:00\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_2c968bdd4735 智能定时更新备用时间",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_2c968bdd4735",
                        "智能定时更新备用时间",
                        "schedule_update",
                        "[{\"name\":\"sdk-demo\",\"enabled\":true,\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}},\"trigger\":{\"intelligent\":{\"backup_time\":\"2030-01-02"
                            + " 12:00:00\"}},\"scheduleId\":\"s1\"}]",
                        200,
                        "{}",
                        "{\"name\":\"sdk-demo\",\"enabled\":true,\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}},\"trigger\":{\"intelligent\":{\"backup_time\":\"2030-01-02"
                            + " 12:00:00\"}}}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_630b72f4c210 更新定时任务结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_630b72f4c210",
                        "更新定时任务结构化错误解码",
                        "schedule_update",
                        "[{\"name\":\"sdk-demo\",\"enabled\":true,\"push\":{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}},\"trigger\":{\"single\":{\"time\":\"2030-01-02"
                            + " 12:00:00\",\"zone_type\":1}},\"scheduleId\":\"s1\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_41157efae155 查询定时任务详情结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_41157efae155",
                        "查询定时任务详情结构化错误解码",
                        "schedule_get",
                        "[\"s1\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_f69ad479ad5f 查询定时消息ID结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_f69ad479ad5f",
                        "查询定时消息ID结构化错误解码",
                        "schedule_msgs",
                        "[{\"scheduleId\":\"s1\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_d63416f0611c 批量查询生命周期结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_d63416f0611c",
                        "批量查询生命周期结构化错误解码",
                        "batch_lifecycle",
                        "[[\"m1\",\"m2\"]]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_962a5def6de2 批量生命周期数组",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_962a5def6de2",
                        "批量生命周期数组",
                        "batch_lifecycle",
                        "[[\"m1\",\"m2\"]]",
                        200,
                        "[{\"message_id\":\"m1\",\"registration_id\":\"ridA\",\"error_code\":1,\"itime\":1750000000,\"channel\":\"engagelab\",\"status\":\"sent\"},{\"message_id\":\"m2\",\"registration_id\":\"ridB\",\"error_code\":21003,\"itime\":1750000001,\"channel\":\"hmos\",\"status\":\"failed\"}]",
                        "null",
                        "[{\"message_id\":\"m1\",\"registration_id\":\"ridA\",\"error_code\":1,\"itime\":1750000000,\"channel\":\"engagelab\",\"status\":\"sent\"},{\"message_id\":\"m2\",\"registration_id\":\"ridB\",\"error_code\":21003,\"itime\":1750000001,\"channel\":\"hmos\",\"status\":\"failed\"}]",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_383f9f5e575f 查询消息生命周期结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_383f9f5e575f",
                        "查询消息生命周期结构化错误解码",
                        "lifecycle",
                        "[{\"messageId\":\"m1\",\"registrationIds\":[\"ridA\",\"ridB\"]}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_479f6def19ca 查询消息统计结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_479f6def19ca",
                        "查询消息统计结构化错误解码",
                        "status",
                        "[{\"messageIds\":[\"m1\"]}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_213b7affc995 查询用户统计结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_213b7affc995",
                        "查询用户统计结构化错误解码",
                        "users",
                        "[{\"timeUnit\":\"DAY\",\"startTime\":\"2026-10-01\",\"duration\":1}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_b55f50ee284c 查询计划统计结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_b55f50ee284c",
                        "查询计划统计结构化错误解码",
                        "plan_status",
                        "[{\"planIds\":[\"p1\"],\"startDate\":\"2026-10-01\",\"endDate\":\"2026-10-02\"}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_f622e4328613 生命周期单条动态RID",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_f622e4328613",
                        "生命周期单条动态RID",
                        "lifecycle",
                        "[{\"messageId\":\"m1\",\"registrationIds\":[\"ridA\",\"ridB\"]}]",
                        200,
                        "{\"ridA\":{\"message_id\":\"m1\",\"registration_id\":\"ridA\",\"error_code\":1,\"itime\":1750000000,\"channel\":\"engagelab\",\"status\":\"sent\"},\"ridB\":{\"message_id\":\"m2\",\"registration_id\":\"ridB\",\"error_code\":21003,\"itime\":1750000001,\"channel\":\"hmos\",\"status\":\"failed\"}}",
                        "null",
                        "{\"ridA\":{\"message_id\":\"m1\",\"registration_id\":\"ridA\",\"error_code\":1,\"itime\":1750000000,\"channel\":\"engagelab\",\"status\":\"sent\"},\"ridB\":{\"message_id\":\"m2\",\"registration_id\":\"ridB\",\"error_code\":21003,\"itime\":1750000001,\"channel\":\"hmos\",\"status\":\"failed\"}}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_1f3dc13c7176 用户TimeUnit与HMOS",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_1f3dc13c7176",
                        "用户TimeUnit与HMOS",
                        "users",
                        "[{\"timeUnit\":\"DAY\",\"startTime\":\"2026-10-01\",\"duration\":1}]",
                        200,
                        "{\"TimeUnit\":\"DAY\",\"items\":[{\"time\":\"2026-10-01\",\"hmos\":{\"new\":7,\"active\":7,\"online\":7}}]}",
                        "null",
                        "{\"TimeUnit\":\"DAY\",\"items\":[{\"time\":\"2026-10-01\",\"hmos\":{\"new\":7,\"active\":7,\"online\":7}}]}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_b41ccaa62ad8 统计嵌套消息类型",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_b41ccaa62ad8",
                        "统计嵌套消息类型",
                        "status",
                        "[{\"messageIds\":[\"m1\"]}]",
                        200,
                        "{\"m1\":{\"plan_id\":\"p1\",\"pushContent\":{\"body\":{\"message\":{\"msg_content\":\"demo\"}}},\"sub\":{\"live_activity\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}},\"voip\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}},\"inapp_message\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}}}}}",
                        "null",
                        "{\"m1\":{\"plan_id\":\"p1\",\"pushContent\":{\"body\":{\"message\":{\"msg_content\":\"demo\"}}},\"sub\":{\"live_activity\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}},\"voip\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}},\"inapp_message\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}}}}}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_666ca276f3cf 统计未知字段容忍",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_666ca276f3cf",
                        "统计未知字段容忍",
                        "status",
                        "[{\"messageIds\":[\"m1\"]}]",
                        200,
                        "{\"m1\":{\"plan_id\":\"p1\",\"pushContent\":{\"body\":{\"message\":{\"msg_content\":\"demo\"}}},\"sub\":{\"live_activity\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}},\"voip\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}},\"inapp_message\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}}},\"target_converted\":2}}",
                        "null",
                        "{\"m1\":{\"plan_id\":\"p1\",\"pushContent\":{\"body\":{\"message\":{\"msg_content\":\"demo\"}}},\"sub\":{\"live_activity\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}},\"voip\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}},\"inapp_message\":{\"target\":7,\"sent\":6,\"delivered\":5,\"impressions\":4,\"click\":3,\"sub_hmos\":{\"engageLab_hmos\":{\"targets\":7,\"sent\":6},\"harmonyos\":{\"targets\":7,\"sent\":6}}}}}}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_9844d9143c0b 删除标签结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_9844d9143c0b",
                        "删除标签结构化错误解码",
                        "tag_delete",
                        "[\"tag\",[\"android\"]]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_740419533058 未绑定布尔false",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_740419533058",
                        "未绑定布尔false",
                        "tag_status",
                        "[\"tag\",\"rid\"]",
                        200,
                        "{\"result\":false}",
                        "null",
                        "{\"result\":false}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_0e57515cd8f7 查询全部标签结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_0e57515cd8f7",
                        "查询全部标签结构化错误解码",
                        "tags",
                        "[]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_21077ba993f8 查询标签设备数结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_21077ba993f8",
                        "查询标签设备数结构化错误解码",
                        "tag_count",
                        "[[\"tagA\",\"tagB\"],\"android\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_6268f5855c7b 查询标签配额结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_6268f5855c7b",
                        "查询标签配额结构化错误解码",
                        "tag_quota",
                        "[[\"tagA\",\"tagB\"],\"android\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_31f93b123ee7 查询设备标签关系结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_31f93b123ee7",
                        "查询设备标签关系结构化错误解码",
                        "tag_status",
                        "[\"tag\",\"rid\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_7aef776a16b5 标签重复query编码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_7aef776a16b5",
                        "标签重复query编码",
                        "tag_count",
                        "[[\"tagA\",\"标签 空格+&\"],\"android\"]",
                        200,
                        "{}",
                        "null",
                        "null",
                        "[\"query\",{\"tags\":[\"tagA\",\"标签 空格+&\"],\"platform\":[\"android\"]}]",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_08e95382e910 设置标签绑定结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_08e95382e910",
                        "设置标签绑定结构化错误解码",
                        "tag_set",
                        "[\"tag\",{\"registration_ids\":{\"add\":[\"rid\"]}}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_9d690bd51b56 配额重复query编码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_9d690bd51b56",
                        "配额重复query编码",
                        "tag_quota",
                        "[[\"tagA\",\"tagB\"],\"android\"]",
                        200,
                        "{}",
                        "null",
                        "null",
                        "[\"query\",{\"tags\":[\"tagA\",\"tagB\"],\"platform\":[\"android\"]}]",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_d52a61f55719 APNS条件字段缺失透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_d52a61f55719",
                        "APNS条件字段缺失透传",
                        "token",
                        "[{\"platform\":\"ios\",\"tokens\":[\"token0\"]}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "{\"platform\":\"ios\",\"tokens\":[\"token0\"]}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_83da2e7f9f33 Token数量0透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_83da2e7f9f33",
                        "Token数量0透传",
                        "token",
                        "[{\"platform\":\"android\",\"tokens\":[]}]",
                        200,
                        "{\"results\":[]}",
                        "{\"platform\":\"android\",\"tokens\":[]}",
                        "{\"results\":[]}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_701ddbc642e6 Token数量1透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_701ddbc642e6",
                        "Token数量1透传",
                        "token",
                        "[{\"platform\":\"android\",\"tokens\":[\"token0\"]}]",
                        200,
                        "{\"results\":[]}",
                        "{\"platform\":\"android\",\"tokens\":[\"token0\"]}",
                        "{\"results\":[]}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_afced1ccc342 Token数量500透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_afced1ccc342",
                        "Token数量500透传",
                        "token",
                        "[{\"platform\":\"android\",\"tokens\":[\"token0\",\"token1\",\"token2\",\"token3\",\"token4\",\"token5\",\"token6\",\"token7\",\"token8\",\"token9\",\"token10\",\"token11\",\"token12\",\"token13\",\"token14\",\"token15\",\"token16\",\"token17\",\"token18\",\"token19\",\"token20\",\"token21\",\"token22\",\"token23\",\"token24\",\"token25\",\"token26\",\"token27\",\"token28\",\"token29\",\"token30\",\"token31\",\"token32\",\"token33\",\"token34\",\"token35\",\"token36\",\"token37\",\"token38\",\"token39\",\"token40\",\"token41\",\"token42\",\"token43\",\"token44\",\"token45\",\"token46\",\"token47\",\"token48\",\"token49\",\"token50\",\"token51\",\"token52\",\"token53\",\"token54\",\"token55\",\"token56\",\"token57\",\"token58\",\"token59\",\"token60\",\"token61\",\"token62\",\"token63\",\"token64\",\"token65\",\"token66\",\"token67\",\"token68\",\"token69\",\"token70\",\"token71\",\"token72\",\"token73\",\"token74\",\"token75\",\"token76\",\"token77\",\"token78\",\"token79\",\"token80\",\"token81\",\"token82\",\"token83\",\"token84\",\"token85\",\"token86\",\"token87\",\"token88\",\"token89\",\"token90\",\"token91\",\"token92\",\"token93\",\"token94\",\"token95\",\"token96\",\"token97\",\"token98\",\"token99\",\"token100\",\"token101\",\"token102\",\"token103\",\"token104\",\"token105\",\"token106\",\"token107\",\"token108\",\"token109\",\"token110\",\"token111\",\"token112\",\"token113\",\"token114\",\"token115\",\"token116\",\"token117\",\"token118\",\"token119\",\"token120\",\"token121\",\"token122\",\"token123\",\"token124\",\"token125\",\"token126\",\"token127\",\"token128\",\"token129\",\"token130\",\"token131\",\"token132\",\"token133\",\"token134\",\"token135\",\"token136\",\"token137\",\"token138\",\"token139\",\"token140\",\"token141\",\"token142\",\"token143\",\"token144\",\"token145\",\"token146\",\"token147\",\"token148\",\"token149\",\"token150\",\"token151\",\"token152\",\"token153\",\"token154\",\"token155\",\"token156\",\"token157\",\"token158\",\"token159\",\"token160\",\"token161\",\"token162\",\"token163\",\"token164\",\"token165\",\"token166\",\"token167\",\"token168\",\"token169\",\"token170\",\"token171\",\"token172\",\"token173\",\"token174\",\"token175\",\"token176\",\"token177\",\"token178\",\"token179\",\"token180\",\"token181\",\"token182\",\"token183\",\"token184\",\"token185\",\"token186\",\"token187\",\"token188\",\"token189\",\"token190\",\"token191\",\"token192\",\"token193\",\"token194\",\"token195\",\"token196\",\"token197\",\"token198\",\"token199\",\"token200\",\"token201\",\"token202\",\"token203\",\"token204\",\"token205\",\"token206\",\"token207\",\"token208\",\"token209\",\"token210\",\"token211\",\"token212\",\"token213\",\"token214\",\"token215\",\"token216\",\"token217\",\"token218\",\"token219\",\"token220\",\"token221\",\"token222\",\"token223\",\"token224\",\"token225\",\"token226\",\"token227\",\"token228\",\"token229\",\"token230\",\"token231\",\"token232\",\"token233\",\"token234\",\"token235\",\"token236\",\"token237\",\"token238\",\"token239\",\"token240\",\"token241\",\"token242\",\"token243\",\"token244\",\"token245\",\"token246\",\"token247\",\"token248\",\"token249\",\"token250\",\"token251\",\"token252\",\"token253\",\"token254\",\"token255\",\"token256\",\"token257\",\"token258\",\"token259\",\"token260\",\"token261\",\"token262\",\"token263\",\"token264\",\"token265\",\"token266\",\"token267\",\"token268\",\"token269\",\"token270\",\"token271\",\"token272\",\"token273\",\"token274\",\"token275\",\"token276\",\"token277\",\"token278\",\"token279\",\"token280\",\"token281\",\"token282\",\"token283\",\"token284\",\"token285\",\"token286\",\"token287\",\"token288\",\"token289\",\"token290\",\"token291\",\"token292\",\"token293\",\"token294\",\"token295\",\"token296\",\"token297\",\"token298\",\"token299\",\"token300\",\"token301\",\"token302\",\"token303\",\"token304\",\"token305\",\"token306\",\"token307\",\"token308\",\"token309\",\"token310\",\"token311\",\"token312\",\"token313\",\"token314\",\"token315\",\"token316\",\"token317\",\"token318\",\"token319\",\"token320\",\"token321\",\"token322\",\"token323\",\"token324\",\"token325\",\"token326\",\"token327\",\"token328\",\"token329\",\"token330\",\"token331\",\"token332\",\"token333\",\"token334\",\"token335\",\"token336\",\"token337\",\"token338\",\"token339\",\"token340\",\"token341\",\"token342\",\"token343\",\"token344\",\"token345\",\"token346\",\"token347\",\"token348\",\"token349\",\"token350\",\"token351\",\"token352\",\"token353\",\"token354\",\"token355\",\"token356\",\"token357\",\"token358\",\"token359\",\"token360\",\"token361\",\"token362\",\"token363\",\"token364\",\"token365\",\"token366\",\"token367\",\"token368\",\"token369\",\"token370\",\"token371\",\"token372\",\"token373\",\"token374\",\"token375\",\"token376\",\"token377\",\"token378\",\"token379\",\"token380\",\"token381\",\"token382\",\"token383\",\"token384\",\"token385\",\"token386\",\"token387\",\"token388\",\"token389\",\"token390\",\"token391\",\"token392\",\"token393\",\"token394\",\"token395\",\"token396\",\"token397\",\"token398\",\"token399\",\"token400\",\"token401\",\"token402\",\"token403\",\"token404\",\"token405\",\"token406\",\"token407\",\"token408\",\"token409\",\"token410\",\"token411\",\"token412\",\"token413\",\"token414\",\"token415\",\"token416\",\"token417\",\"token418\",\"token419\",\"token420\",\"token421\",\"token422\",\"token423\",\"token424\",\"token425\",\"token426\",\"token427\",\"token428\",\"token429\",\"token430\",\"token431\",\"token432\",\"token433\",\"token434\",\"token435\",\"token436\",\"token437\",\"token438\",\"token439\",\"token440\",\"token441\",\"token442\",\"token443\",\"token444\",\"token445\",\"token446\",\"token447\",\"token448\",\"token449\",\"token450\",\"token451\",\"token452\",\"token453\",\"token454\",\"token455\",\"token456\",\"token457\",\"token458\",\"token459\",\"token460\",\"token461\",\"token462\",\"token463\",\"token464\",\"token465\",\"token466\",\"token467\",\"token468\",\"token469\",\"token470\",\"token471\",\"token472\",\"token473\",\"token474\",\"token475\",\"token476\",\"token477\",\"token478\",\"token479\",\"token480\",\"token481\",\"token482\",\"token483\",\"token484\",\"token485\",\"token486\",\"token487\",\"token488\",\"token489\",\"token490\",\"token491\",\"token492\",\"token493\",\"token494\",\"token495\",\"token496\",\"token497\",\"token498\",\"token499\"]}]",
                        200,
                        "{\"results\":[]}",
                        "{\"platform\":\"android\",\"tokens\":[\"token0\",\"token1\",\"token2\",\"token3\",\"token4\",\"token5\",\"token6\",\"token7\",\"token8\",\"token9\",\"token10\",\"token11\",\"token12\",\"token13\",\"token14\",\"token15\",\"token16\",\"token17\",\"token18\",\"token19\",\"token20\",\"token21\",\"token22\",\"token23\",\"token24\",\"token25\",\"token26\",\"token27\",\"token28\",\"token29\",\"token30\",\"token31\",\"token32\",\"token33\",\"token34\",\"token35\",\"token36\",\"token37\",\"token38\",\"token39\",\"token40\",\"token41\",\"token42\",\"token43\",\"token44\",\"token45\",\"token46\",\"token47\",\"token48\",\"token49\",\"token50\",\"token51\",\"token52\",\"token53\",\"token54\",\"token55\",\"token56\",\"token57\",\"token58\",\"token59\",\"token60\",\"token61\",\"token62\",\"token63\",\"token64\",\"token65\",\"token66\",\"token67\",\"token68\",\"token69\",\"token70\",\"token71\",\"token72\",\"token73\",\"token74\",\"token75\",\"token76\",\"token77\",\"token78\",\"token79\",\"token80\",\"token81\",\"token82\",\"token83\",\"token84\",\"token85\",\"token86\",\"token87\",\"token88\",\"token89\",\"token90\",\"token91\",\"token92\",\"token93\",\"token94\",\"token95\",\"token96\",\"token97\",\"token98\",\"token99\",\"token100\",\"token101\",\"token102\",\"token103\",\"token104\",\"token105\",\"token106\",\"token107\",\"token108\",\"token109\",\"token110\",\"token111\",\"token112\",\"token113\",\"token114\",\"token115\",\"token116\",\"token117\",\"token118\",\"token119\",\"token120\",\"token121\",\"token122\",\"token123\",\"token124\",\"token125\",\"token126\",\"token127\",\"token128\",\"token129\",\"token130\",\"token131\",\"token132\",\"token133\",\"token134\",\"token135\",\"token136\",\"token137\",\"token138\",\"token139\",\"token140\",\"token141\",\"token142\",\"token143\",\"token144\",\"token145\",\"token146\",\"token147\",\"token148\",\"token149\",\"token150\",\"token151\",\"token152\",\"token153\",\"token154\",\"token155\",\"token156\",\"token157\",\"token158\",\"token159\",\"token160\",\"token161\",\"token162\",\"token163\",\"token164\",\"token165\",\"token166\",\"token167\",\"token168\",\"token169\",\"token170\",\"token171\",\"token172\",\"token173\",\"token174\",\"token175\",\"token176\",\"token177\",\"token178\",\"token179\",\"token180\",\"token181\",\"token182\",\"token183\",\"token184\",\"token185\",\"token186\",\"token187\",\"token188\",\"token189\",\"token190\",\"token191\",\"token192\",\"token193\",\"token194\",\"token195\",\"token196\",\"token197\",\"token198\",\"token199\",\"token200\",\"token201\",\"token202\",\"token203\",\"token204\",\"token205\",\"token206\",\"token207\",\"token208\",\"token209\",\"token210\",\"token211\",\"token212\",\"token213\",\"token214\",\"token215\",\"token216\",\"token217\",\"token218\",\"token219\",\"token220\",\"token221\",\"token222\",\"token223\",\"token224\",\"token225\",\"token226\",\"token227\",\"token228\",\"token229\",\"token230\",\"token231\",\"token232\",\"token233\",\"token234\",\"token235\",\"token236\",\"token237\",\"token238\",\"token239\",\"token240\",\"token241\",\"token242\",\"token243\",\"token244\",\"token245\",\"token246\",\"token247\",\"token248\",\"token249\",\"token250\",\"token251\",\"token252\",\"token253\",\"token254\",\"token255\",\"token256\",\"token257\",\"token258\",\"token259\",\"token260\",\"token261\",\"token262\",\"token263\",\"token264\",\"token265\",\"token266\",\"token267\",\"token268\",\"token269\",\"token270\",\"token271\",\"token272\",\"token273\",\"token274\",\"token275\",\"token276\",\"token277\",\"token278\",\"token279\",\"token280\",\"token281\",\"token282\",\"token283\",\"token284\",\"token285\",\"token286\",\"token287\",\"token288\",\"token289\",\"token290\",\"token291\",\"token292\",\"token293\",\"token294\",\"token295\",\"token296\",\"token297\",\"token298\",\"token299\",\"token300\",\"token301\",\"token302\",\"token303\",\"token304\",\"token305\",\"token306\",\"token307\",\"token308\",\"token309\",\"token310\",\"token311\",\"token312\",\"token313\",\"token314\",\"token315\",\"token316\",\"token317\",\"token318\",\"token319\",\"token320\",\"token321\",\"token322\",\"token323\",\"token324\",\"token325\",\"token326\",\"token327\",\"token328\",\"token329\",\"token330\",\"token331\",\"token332\",\"token333\",\"token334\",\"token335\",\"token336\",\"token337\",\"token338\",\"token339\",\"token340\",\"token341\",\"token342\",\"token343\",\"token344\",\"token345\",\"token346\",\"token347\",\"token348\",\"token349\",\"token350\",\"token351\",\"token352\",\"token353\",\"token354\",\"token355\",\"token356\",\"token357\",\"token358\",\"token359\",\"token360\",\"token361\",\"token362\",\"token363\",\"token364\",\"token365\",\"token366\",\"token367\",\"token368\",\"token369\",\"token370\",\"token371\",\"token372\",\"token373\",\"token374\",\"token375\",\"token376\",\"token377\",\"token378\",\"token379\",\"token380\",\"token381\",\"token382\",\"token383\",\"token384\",\"token385\",\"token386\",\"token387\",\"token388\",\"token389\",\"token390\",\"token391\",\"token392\",\"token393\",\"token394\",\"token395\",\"token396\",\"token397\",\"token398\",\"token399\",\"token400\",\"token401\",\"token402\",\"token403\",\"token404\",\"token405\",\"token406\",\"token407\",\"token408\",\"token409\",\"token410\",\"token411\",\"token412\",\"token413\",\"token414\",\"token415\",\"token416\",\"token417\",\"token418\",\"token419\",\"token420\",\"token421\",\"token422\",\"token423\",\"token424\",\"token425\",\"token426\",\"token427\",\"token428\",\"token429\",\"token430\",\"token431\",\"token432\",\"token433\",\"token434\",\"token435\",\"token436\",\"token437\",\"token438\",\"token439\",\"token440\",\"token441\",\"token442\",\"token443\",\"token444\",\"token445\",\"token446\",\"token447\",\"token448\",\"token449\",\"token450\",\"token451\",\"token452\",\"token453\",\"token454\",\"token455\",\"token456\",\"token457\",\"token458\",\"token459\",\"token460\",\"token461\",\"token462\",\"token463\",\"token464\",\"token465\",\"token466\",\"token467\",\"token468\",\"token469\",\"token470\",\"token471\",\"token472\",\"token473\",\"token474\",\"token475\",\"token476\",\"token477\",\"token478\",\"token479\",\"token480\",\"token481\",\"token482\",\"token483\",\"token484\",\"token485\",\"token486\",\"token487\",\"token488\",\"token489\",\"token490\",\"token491\",\"token492\",\"token493\",\"token494\",\"token495\",\"token496\",\"token497\",\"token498\",\"token499\"]}",
                        "{\"results\":[]}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_81265228b94b Token数量501透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_81265228b94b",
                        "Token数量501透传",
                        "token",
                        "[{\"platform\":\"android\",\"tokens\":[\"token0\",\"token1\",\"token2\",\"token3\",\"token4\",\"token5\",\"token6\",\"token7\",\"token8\",\"token9\",\"token10\",\"token11\",\"token12\",\"token13\",\"token14\",\"token15\",\"token16\",\"token17\",\"token18\",\"token19\",\"token20\",\"token21\",\"token22\",\"token23\",\"token24\",\"token25\",\"token26\",\"token27\",\"token28\",\"token29\",\"token30\",\"token31\",\"token32\",\"token33\",\"token34\",\"token35\",\"token36\",\"token37\",\"token38\",\"token39\",\"token40\",\"token41\",\"token42\",\"token43\",\"token44\",\"token45\",\"token46\",\"token47\",\"token48\",\"token49\",\"token50\",\"token51\",\"token52\",\"token53\",\"token54\",\"token55\",\"token56\",\"token57\",\"token58\",\"token59\",\"token60\",\"token61\",\"token62\",\"token63\",\"token64\",\"token65\",\"token66\",\"token67\",\"token68\",\"token69\",\"token70\",\"token71\",\"token72\",\"token73\",\"token74\",\"token75\",\"token76\",\"token77\",\"token78\",\"token79\",\"token80\",\"token81\",\"token82\",\"token83\",\"token84\",\"token85\",\"token86\",\"token87\",\"token88\",\"token89\",\"token90\",\"token91\",\"token92\",\"token93\",\"token94\",\"token95\",\"token96\",\"token97\",\"token98\",\"token99\",\"token100\",\"token101\",\"token102\",\"token103\",\"token104\",\"token105\",\"token106\",\"token107\",\"token108\",\"token109\",\"token110\",\"token111\",\"token112\",\"token113\",\"token114\",\"token115\",\"token116\",\"token117\",\"token118\",\"token119\",\"token120\",\"token121\",\"token122\",\"token123\",\"token124\",\"token125\",\"token126\",\"token127\",\"token128\",\"token129\",\"token130\",\"token131\",\"token132\",\"token133\",\"token134\",\"token135\",\"token136\",\"token137\",\"token138\",\"token139\",\"token140\",\"token141\",\"token142\",\"token143\",\"token144\",\"token145\",\"token146\",\"token147\",\"token148\",\"token149\",\"token150\",\"token151\",\"token152\",\"token153\",\"token154\",\"token155\",\"token156\",\"token157\",\"token158\",\"token159\",\"token160\",\"token161\",\"token162\",\"token163\",\"token164\",\"token165\",\"token166\",\"token167\",\"token168\",\"token169\",\"token170\",\"token171\",\"token172\",\"token173\",\"token174\",\"token175\",\"token176\",\"token177\",\"token178\",\"token179\",\"token180\",\"token181\",\"token182\",\"token183\",\"token184\",\"token185\",\"token186\",\"token187\",\"token188\",\"token189\",\"token190\",\"token191\",\"token192\",\"token193\",\"token194\",\"token195\",\"token196\",\"token197\",\"token198\",\"token199\",\"token200\",\"token201\",\"token202\",\"token203\",\"token204\",\"token205\",\"token206\",\"token207\",\"token208\",\"token209\",\"token210\",\"token211\",\"token212\",\"token213\",\"token214\",\"token215\",\"token216\",\"token217\",\"token218\",\"token219\",\"token220\",\"token221\",\"token222\",\"token223\",\"token224\",\"token225\",\"token226\",\"token227\",\"token228\",\"token229\",\"token230\",\"token231\",\"token232\",\"token233\",\"token234\",\"token235\",\"token236\",\"token237\",\"token238\",\"token239\",\"token240\",\"token241\",\"token242\",\"token243\",\"token244\",\"token245\",\"token246\",\"token247\",\"token248\",\"token249\",\"token250\",\"token251\",\"token252\",\"token253\",\"token254\",\"token255\",\"token256\",\"token257\",\"token258\",\"token259\",\"token260\",\"token261\",\"token262\",\"token263\",\"token264\",\"token265\",\"token266\",\"token267\",\"token268\",\"token269\",\"token270\",\"token271\",\"token272\",\"token273\",\"token274\",\"token275\",\"token276\",\"token277\",\"token278\",\"token279\",\"token280\",\"token281\",\"token282\",\"token283\",\"token284\",\"token285\",\"token286\",\"token287\",\"token288\",\"token289\",\"token290\",\"token291\",\"token292\",\"token293\",\"token294\",\"token295\",\"token296\",\"token297\",\"token298\",\"token299\",\"token300\",\"token301\",\"token302\",\"token303\",\"token304\",\"token305\",\"token306\",\"token307\",\"token308\",\"token309\",\"token310\",\"token311\",\"token312\",\"token313\",\"token314\",\"token315\",\"token316\",\"token317\",\"token318\",\"token319\",\"token320\",\"token321\",\"token322\",\"token323\",\"token324\",\"token325\",\"token326\",\"token327\",\"token328\",\"token329\",\"token330\",\"token331\",\"token332\",\"token333\",\"token334\",\"token335\",\"token336\",\"token337\",\"token338\",\"token339\",\"token340\",\"token341\",\"token342\",\"token343\",\"token344\",\"token345\",\"token346\",\"token347\",\"token348\",\"token349\",\"token350\",\"token351\",\"token352\",\"token353\",\"token354\",\"token355\",\"token356\",\"token357\",\"token358\",\"token359\",\"token360\",\"token361\",\"token362\",\"token363\",\"token364\",\"token365\",\"token366\",\"token367\",\"token368\",\"token369\",\"token370\",\"token371\",\"token372\",\"token373\",\"token374\",\"token375\",\"token376\",\"token377\",\"token378\",\"token379\",\"token380\",\"token381\",\"token382\",\"token383\",\"token384\",\"token385\",\"token386\",\"token387\",\"token388\",\"token389\",\"token390\",\"token391\",\"token392\",\"token393\",\"token394\",\"token395\",\"token396\",\"token397\",\"token398\",\"token399\",\"token400\",\"token401\",\"token402\",\"token403\",\"token404\",\"token405\",\"token406\",\"token407\",\"token408\",\"token409\",\"token410\",\"token411\",\"token412\",\"token413\",\"token414\",\"token415\",\"token416\",\"token417\",\"token418\",\"token419\",\"token420\",\"token421\",\"token422\",\"token423\",\"token424\",\"token425\",\"token426\",\"token427\",\"token428\",\"token429\",\"token430\",\"token431\",\"token432\",\"token433\",\"token434\",\"token435\",\"token436\",\"token437\",\"token438\",\"token439\",\"token440\",\"token441\",\"token442\",\"token443\",\"token444\",\"token445\",\"token446\",\"token447\",\"token448\",\"token449\",\"token450\",\"token451\",\"token452\",\"token453\",\"token454\",\"token455\",\"token456\",\"token457\",\"token458\",\"token459\",\"token460\",\"token461\",\"token462\",\"token463\",\"token464\",\"token465\",\"token466\",\"token467\",\"token468\",\"token469\",\"token470\",\"token471\",\"token472\",\"token473\",\"token474\",\"token475\",\"token476\",\"token477\",\"token478\",\"token479\",\"token480\",\"token481\",\"token482\",\"token483\",\"token484\",\"token485\",\"token486\",\"token487\",\"token488\",\"token489\",\"token490\",\"token491\",\"token492\",\"token493\",\"token494\",\"token495\",\"token496\",\"token497\",\"token498\",\"token499\",\"token500\"]}]",
                        200,
                        "{\"results\":[]}",
                        "{\"platform\":\"android\",\"tokens\":[\"token0\",\"token1\",\"token2\",\"token3\",\"token4\",\"token5\",\"token6\",\"token7\",\"token8\",\"token9\",\"token10\",\"token11\",\"token12\",\"token13\",\"token14\",\"token15\",\"token16\",\"token17\",\"token18\",\"token19\",\"token20\",\"token21\",\"token22\",\"token23\",\"token24\",\"token25\",\"token26\",\"token27\",\"token28\",\"token29\",\"token30\",\"token31\",\"token32\",\"token33\",\"token34\",\"token35\",\"token36\",\"token37\",\"token38\",\"token39\",\"token40\",\"token41\",\"token42\",\"token43\",\"token44\",\"token45\",\"token46\",\"token47\",\"token48\",\"token49\",\"token50\",\"token51\",\"token52\",\"token53\",\"token54\",\"token55\",\"token56\",\"token57\",\"token58\",\"token59\",\"token60\",\"token61\",\"token62\",\"token63\",\"token64\",\"token65\",\"token66\",\"token67\",\"token68\",\"token69\",\"token70\",\"token71\",\"token72\",\"token73\",\"token74\",\"token75\",\"token76\",\"token77\",\"token78\",\"token79\",\"token80\",\"token81\",\"token82\",\"token83\",\"token84\",\"token85\",\"token86\",\"token87\",\"token88\",\"token89\",\"token90\",\"token91\",\"token92\",\"token93\",\"token94\",\"token95\",\"token96\",\"token97\",\"token98\",\"token99\",\"token100\",\"token101\",\"token102\",\"token103\",\"token104\",\"token105\",\"token106\",\"token107\",\"token108\",\"token109\",\"token110\",\"token111\",\"token112\",\"token113\",\"token114\",\"token115\",\"token116\",\"token117\",\"token118\",\"token119\",\"token120\",\"token121\",\"token122\",\"token123\",\"token124\",\"token125\",\"token126\",\"token127\",\"token128\",\"token129\",\"token130\",\"token131\",\"token132\",\"token133\",\"token134\",\"token135\",\"token136\",\"token137\",\"token138\",\"token139\",\"token140\",\"token141\",\"token142\",\"token143\",\"token144\",\"token145\",\"token146\",\"token147\",\"token148\",\"token149\",\"token150\",\"token151\",\"token152\",\"token153\",\"token154\",\"token155\",\"token156\",\"token157\",\"token158\",\"token159\",\"token160\",\"token161\",\"token162\",\"token163\",\"token164\",\"token165\",\"token166\",\"token167\",\"token168\",\"token169\",\"token170\",\"token171\",\"token172\",\"token173\",\"token174\",\"token175\",\"token176\",\"token177\",\"token178\",\"token179\",\"token180\",\"token181\",\"token182\",\"token183\",\"token184\",\"token185\",\"token186\",\"token187\",\"token188\",\"token189\",\"token190\",\"token191\",\"token192\",\"token193\",\"token194\",\"token195\",\"token196\",\"token197\",\"token198\",\"token199\",\"token200\",\"token201\",\"token202\",\"token203\",\"token204\",\"token205\",\"token206\",\"token207\",\"token208\",\"token209\",\"token210\",\"token211\",\"token212\",\"token213\",\"token214\",\"token215\",\"token216\",\"token217\",\"token218\",\"token219\",\"token220\",\"token221\",\"token222\",\"token223\",\"token224\",\"token225\",\"token226\",\"token227\",\"token228\",\"token229\",\"token230\",\"token231\",\"token232\",\"token233\",\"token234\",\"token235\",\"token236\",\"token237\",\"token238\",\"token239\",\"token240\",\"token241\",\"token242\",\"token243\",\"token244\",\"token245\",\"token246\",\"token247\",\"token248\",\"token249\",\"token250\",\"token251\",\"token252\",\"token253\",\"token254\",\"token255\",\"token256\",\"token257\",\"token258\",\"token259\",\"token260\",\"token261\",\"token262\",\"token263\",\"token264\",\"token265\",\"token266\",\"token267\",\"token268\",\"token269\",\"token270\",\"token271\",\"token272\",\"token273\",\"token274\",\"token275\",\"token276\",\"token277\",\"token278\",\"token279\",\"token280\",\"token281\",\"token282\",\"token283\",\"token284\",\"token285\",\"token286\",\"token287\",\"token288\",\"token289\",\"token290\",\"token291\",\"token292\",\"token293\",\"token294\",\"token295\",\"token296\",\"token297\",\"token298\",\"token299\",\"token300\",\"token301\",\"token302\",\"token303\",\"token304\",\"token305\",\"token306\",\"token307\",\"token308\",\"token309\",\"token310\",\"token311\",\"token312\",\"token313\",\"token314\",\"token315\",\"token316\",\"token317\",\"token318\",\"token319\",\"token320\",\"token321\",\"token322\",\"token323\",\"token324\",\"token325\",\"token326\",\"token327\",\"token328\",\"token329\",\"token330\",\"token331\",\"token332\",\"token333\",\"token334\",\"token335\",\"token336\",\"token337\",\"token338\",\"token339\",\"token340\",\"token341\",\"token342\",\"token343\",\"token344\",\"token345\",\"token346\",\"token347\",\"token348\",\"token349\",\"token350\",\"token351\",\"token352\",\"token353\",\"token354\",\"token355\",\"token356\",\"token357\",\"token358\",\"token359\",\"token360\",\"token361\",\"token362\",\"token363\",\"token364\",\"token365\",\"token366\",\"token367\",\"token368\",\"token369\",\"token370\",\"token371\",\"token372\",\"token373\",\"token374\",\"token375\",\"token376\",\"token377\",\"token378\",\"token379\",\"token380\",\"token381\",\"token382\",\"token383\",\"token384\",\"token385\",\"token386\",\"token387\",\"token388\",\"token389\",\"token390\",\"token391\",\"token392\",\"token393\",\"token394\",\"token395\",\"token396\",\"token397\",\"token398\",\"token399\",\"token400\",\"token401\",\"token402\",\"token403\",\"token404\",\"token405\",\"token406\",\"token407\",\"token408\",\"token409\",\"token410\",\"token411\",\"token412\",\"token413\",\"token414\",\"token415\",\"token416\",\"token417\",\"token418\",\"token419\",\"token420\",\"token421\",\"token422\",\"token423\",\"token424\",\"token425\",\"token426\",\"token427\",\"token428\",\"token429\",\"token430\",\"token431\",\"token432\",\"token433\",\"token434\",\"token435\",\"token436\",\"token437\",\"token438\",\"token439\",\"token440\",\"token441\",\"token442\",\"token443\",\"token444\",\"token445\",\"token446\",\"token447\",\"token448\",\"token449\",\"token450\",\"token451\",\"token452\",\"token453\",\"token454\",\"token455\",\"token456\",\"token457\",\"token458\",\"token459\",\"token460\",\"token461\",\"token462\",\"token463\",\"token464\",\"token465\",\"token466\",\"token467\",\"token468\",\"token469\",\"token470\",\"token471\",\"token472\",\"token473\",\"token474\",\"token475\",\"token476\",\"token477\",\"token478\",\"token479\",\"token480\",\"token481\",\"token482\",\"token483\",\"token484\",\"token485\",\"token486\",\"token487\",\"token488\",\"token489\",\"token490\",\"token491\",\"token492\",\"token493\",\"token494\",\"token495\",\"token496\",\"token497\",\"token498\",\"token499\",\"token500\"]}",
                        "{\"results\":[]}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_b15101d6b2aa 已存在映射is_new=false",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_b15101d6b2aa",
                        "已存在映射is_new=false",
                        "token",
                        "[{\"platform\":\"android\",\"tokens\":[\"token0\"]}]",
                        200,
                        "{\"results\":[{\"token\":\"token0\",\"registration_id\":\"rid\",\"is_new\":false}]}",
                        "{\"platform\":\"android\",\"tokens\":[\"token0\"]}",
                        "{\"results\":[{\"token\":\"token0\",\"registration_id\":\"rid\",\"is_new\":false}]}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_a6d8e20902f5 未知平台透传",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_a6d8e20902f5",
                        "未知平台透传",
                        "token",
                        "[{\"platform\":\"future-platform\",\"tokens\":[\"token0\"]}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "{\"platform\":\"future-platform\",\"tokens\":[\"token0\"]}",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_d324af9a8d7a 沙箱配置false",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_d324af9a8d7a",
                        "沙箱配置false",
                        "token",
                        "[{\"platform\":\"ios\",\"tokens\":[\"token0\"],\"apns_production\":false}]",
                        200,
                        "{\"results\":[]}",
                        "{\"platform\":\"ios\",\"tokens\":[\"token0\"],\"apns_production\":false}",
                        "{\"results\":[]}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_0c7342223a4e 注册单项失败混合结果",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_0c7342223a4e",
                        "注册单项失败混合结果",
                        "token",
                        "[{\"platform\":\"android\",\"tokens\":[\"token0\"]}]",
                        200,
                        "{\"results\":[{\"token\":\"token0\",\"registration_id\":\"rid\",\"is_new\":false},{\"token\":\"bad\",\"code\":21003,\"message\":\"invalid"
                            + " token\"}]}",
                        "{\"platform\":\"android\",\"tokens\":[\"token0\"]}",
                        "{\"results\":[{\"token\":\"token0\",\"registration_id\":\"rid\",\"is_new\":false},{\"token\":\"bad\",\"code\":21003,\"message\":\"invalid"
                            + " token\"}]}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_9e76c34d12a5 注册厂商Token结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_9e76c34d12a5",
                        "注册厂商Token结构化错误解码",
                        "token",
                        "[{\"platform\":\"android\",\"tokens\":[\"tokenA\"]}]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_00a48e0d33e4 Brazil数据中心",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_00a48e0d33e4",
                        "Brazil数据中心",
                        "host",
                        "[\"brazil\"]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        "null",
                        "\"host\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_ba12151237d3 HTTP429错误",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_ba12151237d3",
                        "HTTP429错误",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        429,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_93eab3c67c09 HTTP500错误",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_93eab3c67c09",
                        "HTTP500错误",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        500,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_c48149375f68 Japan数据中心",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_c48149375f68",
                        "Japan数据中心",
                        "host",
                        "[\"japan\"]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        "null",
                        "\"host\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_5d4b8a45f876 同客户端并发隔离",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_5d4b8a45f876",
                        "同客户端并发隔离",
                        "concurrent",
                        "[]",
                        200,
                        "\"ECHO\"",
                        "null",
                        "null",
                        "\"concurrent\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_84cd3124faa6 敏感凭据不泄露",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_84cd3124faa6",
                        "敏感凭据不泄露",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        "null",
                        "\"logs\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_382a447fdc76 未知响应字段兼容",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_382a447fdc76",
                        "未知响应字段兼容",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\",\"future_field\":{\"x\":1}}",
                        "null",
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_d6f29da0febe 自定义BaseURL",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_d6f29da0febe",
                        "自定义BaseURL",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_bc871ae6520c 认证编码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_bc871ae6520c",
                        "认证编码",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        "null",
                        "\"auth\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_e990cb46d18c 超时错误",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_e990cb46d18c",
                        "超时错误",
                        "validate",
                        "[{\"to\":{\"registration_id\":[\"rid\"]},\"body\":{\"platform\":[\"android\"],\"notification\":{\"alert\":\"demo\"}}}]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        "null",
                        "\"timeout\"",
                        0.1,
                        0.7))),
        DynamicTest.dynamicTest(
            "contract_02b13ad92165 默认Singapore",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_02b13ad92165",
                        "默认Singapore",
                        "host",
                        "[\"default\"]",
                        200,
                        "{\"request_id\":\"r1\",\"msg_id\":\"123\"}",
                        "null",
                        "null",
                        "\"host\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_f4762dac11b5 VIP到期时间64位",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_f4762dac11b5",
                        "VIP到期时间64位",
                        "vip",
                        "[]",
                        200,
                        "{\"vip_status\":1,\"vip_end_time\":2147483648}",
                        "null",
                        "{\"vip_status\":1,\"vip_end_time\":2147483648}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_7381a2144b72 查询应用VIP状态结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_7381a2144b72",
                        "查询应用VIP状态结构化错误解码",
                        "vip",
                        "[]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_118ff86b6453 非VIP状态零值",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_118ff86b6453",
                        "非VIP状态零值",
                        "vip",
                        "[]",
                        200,
                        "{\"vip_status\":0,\"vip_end_time\":0}",
                        "null",
                        "{\"vip_status\":0,\"vip_end_time\":0}",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_a0fcc96cfdc2 multipart文件字节保真",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_a0fcc96cfdc2",
                        "multipart文件字节保真",
                        "voice_create",
                        "[\"en\",\"__NATIVE_TEMP_WAV__\"]",
                        200,
                        "{\"language\":\"en\",\"file_url\":\"https://example.test/a.wav\"}",
                        "null",
                        "null",
                        "\"multipart\"",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_96231d59a4b6 上传语音文件结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_96231d59a4b6",
                        "上传语音文件结构化错误解码",
                        "voice_create",
                        "[\"en\",\"__NATIVE_TEMP_WAV__\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_c7368dd7959e 删除语音模板结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_c7368dd7959e",
                        "删除语音模板结构化错误解码",
                        "voice_delete",
                        "[\"en\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_51abea20bc7a 查询语音列表结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_51abea20bc7a",
                        "查询语音列表结构化错误解码",
                        "voices",
                        "[]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_e47b273c2502 查询语音详情结构化错误解码",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_e47b273c2502",
                        "查询语音详情结构化错误解码",
                        "voice_get",
                        "[\"en\"]",
                        400,
                        "{\"error\":{\"code\":21003,\"message\":\"fixture-invalid\"}}",
                        "null",
                        "null",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_047f5e08356c 语音列表直接数组",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_047f5e08356c",
                        "语音列表直接数组",
                        "voices",
                        "[]",
                        200,
                        "[{\"language\":\"en\",\"file_url\":\"https://example.test/a.wav\"}]",
                        "null",
                        "[{\"language\":\"en\",\"file_url\":\"https://example.test/a.wav\"}]",
                        "null",
                        3,
                        0))),
        DynamicTest.dynamicTest(
            "contract_8847f227b271 语音列表空数组",
            () ->
                FullContract.run(
                    new FullContract.Spec(
                        "contract_8847f227b271",
                        "语音列表空数组",
                        "voices",
                        "[]",
                        200,
                        "[]",
                        "null",
                        "[]",
                        "null",
                        3,
                        0))))
        .stream();
  }
}
