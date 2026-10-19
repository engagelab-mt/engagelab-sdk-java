package io.github.engagelab.nativecheck;

import static io.github.engagelab.nativecheck.FullQA.*;

import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;

@Tag("integration")
final class FullIntegrationTest {
  static void flow_devices(FullQA c) throws Exception {
    Object a = null;
    Object alias = null;
    Object b = null;
    Object binding = null;
    Object d1 = null;
    Object d2 = null;
    Object r = null;
    Object raw = null;
    Object tag1 = null;
    Object tag2 = null;
    Object tag3 = null;
    c.write();
    d1 = c.seedDevice();
    d2 = c.seedDevice();
    tag1 = qcat(c.prefix, "_a");
    tag2 = qcat(c.prefix, "_b");
    tag3 = qcat(c.prefix, "_del");
    alias = qcat(c.prefix, "_alias");
    c.rest("POST", qcat("/v4/devices/", qtxt(d1)), qm("tags", "", "alias", ""));
    c.seedTag(tag1, d1);
    c.seedAlias(alias, d1);
    r = c.sdk("device_get", qarr(d1));
    raw = c.device(d1);
    c.record(
        "qa_48e10c8c3688",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "known_tags",
            qsets(qg(r, "data", "tags"), qarr(tag1)),
            "known_alias",
            qeq(qg(r, "data", "alias"), alias),
            "wire_fields",
            qsame(qg(r, "data"), qg(r, "raw"))),
        qyes(true),
        "");
    r = c.sdk("alias_get", qarr(alias, qarr("android")));
    c.record(
        "qa_4228ae60b590",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "exact_binding",
            qsets(qg(r, "data", "registration_ids"), qarr(d1)),
            "wire_fields",
            qsame(qg(r, "data"), qg(r, "raw"))),
        qyes(true),
        "");
    r = c.sdk("alias_delete", qarr(alias, qarr("android")));
    raw = c.device(d1);
    binding = c.rest("GET", qcat(qcat("/v4/aliases/", alias), "?platform=android"), null);
    c.record(
        "qa_f76445835b7d",
        qm(
            "SDK_success",
            qyes(qg(r, "ok")),
            "device_alias_empty",
            qeq(qtxt(qg(raw, "data", "alias")), ""),
            "alias_unbound",
            qno(qincludes(qg(binding, "data", "registration_ids"), d1))),
        qyes(true),
        "");
    c.rest("POST", qcat("/v4/devices/", qtxt(d1)), qm("tags", ""));
    c.seedTag(tag2, d1);
    r =
        c.sdk(
            "device_set",
            qarr(d1, qm("tags", qm("add", qarr(tag1), "remove", qarr(tag2)), "alias", alias)));
    raw = c.device(d1);
    c.record(
        "qa_3a16670b310f",
        qm(
            "SDK_success",
            qyes(qg(r, "ok")),
            "tags_add_remove",
            qsets(qg(raw, "data", "tags"), qarr(tag1)),
            "alias_written",
            qeq(qg(raw, "data", "alias"), alias)),
        qyes(true),
        "");
    c.seedTag(tag2, d1);
    r = c.sdk("device_set", qarr(d1, qm("tags", "")));
    raw = c.device(d1);
    a = c.relation(tag1, d1);
    b = c.relation(tag2, d1);
    c.record(
        "qa_418e812084ee",
        qm(
            "SDK_success",
            qyes(qg(r, "ok")),
            "tags_empty",
            qeq(qsize(qg(raw, "data", "tags")), 0),
            "both_false",
            qboth(qno(qg(a, "data", "result")), qno(qg(b, "data", "result")))),
        qyes(true),
        "");
    c.seedTag(tag1, d1);
    c.seedTag(tag2, d2);
    r = c.sdk("tags", qarr());
    c.record(
        "qa_00cbd3d800bd",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "two_seeded_tags",
            qboth(qincludes(qg(r, "data", "tags"), tag1), qincludes(qg(r, "data", "tags"), tag2))),
        qyes(true),
        "");
    r = c.sdk("tag_status", qarr(tag1, d1));
    c.record(
        "qa_a925a3248bcd",
        qm("HTTP200", qeq(qg(r, "status"), 200), "typed_true", qyes(qg(r, "data", "result"))),
        qyes(true),
        "");
    r = c.sdk("tag_count", qarr(qarr(tag1, tag2), "android"));
    c.record(
        "qa_d5581d7d5601",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "two_known_single_counts",
            qboth(
                qeq(qg(r, "data", "tagsCount", tag1), 1), qeq(qg(r, "data", "tagsCount", tag2), 1)),
            "repeated_tags_query",
            qsets(qg(r, "query", "tags"), qarr(tag1, tag2))),
        qyes(true),
        "");
    r = c.sdk("tag_quota", qarr(qarr(tag1, tag2), "android"));
    c.record(
        "qa_5f8b9b9d7f78",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "repeated_tags_query",
            qsets(qg(r, "query", "tags"), qarr(tag1, tag2)),
            "nonempty_known_quotas",
            qboth(
                qincludes(qids(qg(r, "raw", "data", "tagUidQuotaDetail"), "tagName"), tag1),
                qincludes(qids(qg(r, "raw", "data", "tagUidQuotaDetail"), "tagName"), tag2)),
            "wire_fields",
            qsame(qg(r, "data"), qg(r, "raw"))),
        qyes(true),
        "");
    c.rest("POST", qcat("/v4/devices/", qtxt(d1)), qm("tags", ""));
    c.rest("POST", qcat("/v4/devices/", qtxt(d2)), qm("tags", ""));
    c.seedTag(tag1, d2);
    r =
        c.sdk(
            "tag_set", qarr(tag1, qm("registration_ids", qm("add", qarr(d1), "remove", qarr(d2)))));
    a = c.relation(tag1, d1);
    b = c.relation(tag1, d2);
    c.record(
        "qa_83ba432b89af",
        qm(
            "SDK_success",
            qyes(qg(r, "ok")),
            "A_true_B_false",
            qboth(qyes(qg(a, "data", "result")), qno(qg(b, "data", "result")))),
        qyes(true),
        "");
    c.seedTag(tag3, d1);
    r = c.sdk("tag_delete", qarr(tag3, qarr("android")));
    raw = c.rest("GET", "/v4/tags/", null);
    a = c.relation(tag3, d1);
    c.record(
        "qa_ff6fe44cc46d",
        qm(
            "SDK_success",
            qyes(qg(r, "ok")),
            "tag_absent",
            qno(qincludes(qg(raw, "data", "tags"), tag3)),
            "relation_false",
            qno(qg(a, "data", "result"))),
        qyes(true),
        "");
    r = c.sdk("device_status", qarr(qm("registration_ids", qarr(d1, d2))));
    c.record(
        "qa_edfec6f68242",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "two_known_offline_records",
            qboth(
                qeq(qsize(qg(r, "data")), 2),
                qsets(qids(qg(r, "data"), "regid"), qarr(d1, d2)),
                qno(qg(r, "data", 0, "online")),
                qno(qg(r, "data", 1, "online")))),
        qyes(true),
        "");
    r = c.sdk("device_delete", qarr(d2));
    raw = c.device(d2);
    a = c.relation(tag2, d2);
    c.record(
        "qa_42caca08cb39",
        qm(
            "SDK_success",
            qyes(qg(r, "ok")),
            "device_absent",
            qeq(qg(raw, "data", "error", "code"), 27002),
            "old_relations_inaccessible",
            qeq(qg(a, "data", "error", "code"), 27002)),
        qyes(true),
        "");
  }

  static void flow_plans(FullQA c) throws Exception {
    Object a = null;
    Object b = null;
    Object control = null;
    Object description = null;
    Object first = null;
    Object pair = null;
    Object pid = null;
    Object prefix = null;
    Object r = null;
    Object raw = null;
    c.write();
    first = c.seedPlans(101);
    prefix = c.prefix;
    r = c.sdk("plan_list", qarr(1, 101, null, prefix));
    c.record(
        "qa_bbef5f838820",
        qm(
            "SDK_HTTP200",
            qeq(qg(first, "status"), 200),
            "created_readback",
            qincludes(
                qids(qg(r, "data", "push_plan_info"), "plan_id"), qg(first, "data", "plan_id"))),
        qyes(true),
        "");
    c.record(
        "qa_0e01ec2f278a",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "count101",
            qeq(qg(r, "data", "total"), 101),
            "all_fields",
            qsame(qg(r, "data"), qg(r, "raw"))),
        qyes(true),
        "");
    c.record(
        "qa_2d67bd95bf58",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "total101",
            qeq(qg(r, "data", "total"), 101),
            "count101",
            qeq(qsize(qg(r, "data", "push_plan_info")), 101),
            "owned_ids_exact",
            qsets(qids(qg(r, "data", "push_plan_info"), "plan_id"), c.planIDs()),
            "wire_fields",
            qsame(qg(r, "data"), qg(r, "raw")),
            "query101",
            qeq(qg(r, "query", "page_size", 0), "101")),
        qyes(true),
        "");
    r = c.sdk("plan_list", qarr(1, 100, null, prefix));
    c.record(
        "qa_452338f7f0cb",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "page100",
            qeq(qsize(qg(r, "data", "push_plan_info")), 100),
            "wire_fields",
            qsame(qg(r, "data"), qg(r, "raw"))),
        qyes(true),
        "");
    a = c.sdk("plan_list", qarr(1, 1, null, prefix));
    c.record(
        "qa_d1c2dc88ee2a",
        qm(
            "HTTP200",
            qeq(qg(a, "status"), 200),
            "one_item",
            qeq(qsize(qg(a, "data", "push_plan_info")), 1),
            "wire_fields",
            qsame(qg(a, "data"), qg(a, "raw"))),
        qyes(true),
        "");
    b = c.sdk("plan_list", qarr(2, 1, null, prefix));
    c.record(
        "qa_1068750e639d",
        qm(
            "HTTP200",
            qeq(qg(b, "status"), 200),
            "one_item",
            qeq(qsize(qg(b, "data", "push_plan_info")), 1),
            "different_from_first",
            qno(
                qeq(
                    qg(a, "data", "push_plan_info", 0, "plan_id"),
                    qg(b, "data", "push_plan_info", 0, "plan_id"))),
            "wire_fields",
            qsame(qg(b, "data"), qg(b, "raw"))),
        qyes(true),
        "");
    r = c.sdk("plan_list", qarr(1, 0, null, prefix));
    control = c.listPlans(prefix, 0, 1, null);
    c.record(
        "qa_c32bda243404",
        qm("SDK_rejected", qno(qg(r, "ok")), "HTTP_code_message_match", c.errorsEqual(r, control)),
        qyes(true),
        "");
    pid = qg(first, "data", "plan_id");
    description = qcat(prefix, "_更新");
    r = c.sdk("plan_create", qarr(qm("plan_id", pid, "plan_description", description)));
    raw = c.listPlans(description, 101, 1, null);
    c.record(
        "qa_ee67b55b2f74",
        qm(
            "SDK_success",
            qyes(qg(r, "ok")),
            "single_updated",
            qboth(
                qeq(qg(raw, "data", "total"), 1),
                qeq(qg(raw, "data", "push_plan_info", 0, "plan_description"), description))),
        qyes(true),
        "");
    r = c.sdk("plan_list", qarr(1, 101, null, description));
    c.record(
        "qa_3e98ca439abe",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "one_UTF8_match",
            qboth(
                qeq(qg(r, "data", "total"), 1),
                qeq(qg(r, "data", "push_plan_info", 0, "plan_description"), description)),
            "UTF8_query",
            qeq(qg(r, "query", "search_description", 0), description)),
        qyes(true),
        "");
    r = c.sdk("plan_list", qarr(1, 101, 0, prefix));
    raw = c.listPlans(prefix, 101, 1, 0);
    c.record(
        "qa_a8afa2ba63c2",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "API_known_records",
            qeq(qg(r, "data", "total"), 101),
            "source_query",
            qeq(qg(r, "query", "send_source", 0), "0"),
            "REST_agrees",
            qsame(qg(r, "data"), qg(raw, "data"))),
        qyes(true),
        "");
    r = c.sdk("plan_list", qarr(1, 1, 0, description));
    c.record(
        "qa_6df4ebb6c583",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "all_four_queries",
            qboth(
                qeq(qg(r, "query", "page_index", 0), "1"),
                qeq(qg(r, "query", "page_size", 0), "1"),
                qeq(qg(r, "query", "send_source", 0), "0"),
                qeq(qg(r, "query", "search_description", 0), description)),
            "single_correct_item",
            qboth(
                qeq(qg(r, "data", "total"), 1),
                qeq(qg(r, "data", "push_plan_info", 0, "plan_id"), pid))),
        qyes(true),
        "");
    r = c.sdk("plan_delete", qarr(pid));
    raw = c.listPlans(prefix, 101, 1, null);
    c.record(
        "qa_793b07888b66",
        qm(
            "SDK_success",
            qyes(qg(r, "ok")),
            "plan_absent",
            qno(qincludes(qids(qg(raw, "data", "push_plan_info"), "plan_id"), pid))),
        qyes(true),
        "");
    pair = c.batchPlanPair(pid);
    r = c.sdk("plan_batch_delete", qarr(c.join(pair)));
    raw = c.listPlans(prefix, 101, 1, null);
    c.record(
        "qa_7c9f085a13ea",
        qm(
            "SDK_success",
            qyes(qg(r, "ok")),
            "comma_path",
            qeq(qg(r, "path"), qcat("/v4/push_plan/batch/", c.join(pair))),
            "both_absent",
            c.noneIn(pair, qids(qg(raw, "data", "push_plan_info"), "plan_id"))),
        qyes(true),
        "");
    r = c.planBoundary(1);
    c.record(
        "qa_0df5c6ed2605",
        qm("expected_outcome", qyes(qg(r, "verified"))),
        qyes(qyes(qg(r, "ready"))),
        "No unused one-character plan ID available");
    r = c.planBoundary(49);
    c.record(
        "qa_2e34db0cb38c",
        qm("expected_outcome", qyes(qg(r, "verified"))),
        qyes(qyes(qg(r, "ready"))),
        "No unused one-character plan ID available");
    r = c.planBoundary(50);
    c.record(
        "qa_eeb26ebb2b07",
        qm("expected_outcome", qyes(qg(r, "verified"))),
        qyes(qyes(qg(r, "ready"))),
        "No unused one-character plan ID available");
    r = c.planBoundary(51);
    c.record(
        "qa_d71d62850538",
        qm("expected_outcome", qyes(qg(r, "verified"))),
        qyes(qyes(qg(r, "ready"))),
        "No unused one-character plan ID available");
  }

  static void flow_batch(FullQA c) throws Exception {
    Object alias1 = null;
    Object alias2 = null;
    Object d1 = null;
    Object d2 = null;
    Object r = null;
    c.write();
    d1 = c.targetRID(0);
    d2 = c.targetRID(1);
    alias1 = qcat(c.prefix, "_1");
    alias2 = qcat(c.prefix, "_2");
    c.seedAlias(alias1, d1);
    c.seedAlias(alias2, d2);
    r = c.batch("batch_regid", qarr(d1, d2));
    c.record(
        "qa_81f620c4cc20",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "all_target_successes",
            c.batchResultsValid(r, qarr(d1, d2)),
            "wire_fields",
            qsame(qg(r, "data"), qg(r, "raw")),
            "message_linkage",
            qyes(qg(r, "messages_persisted"))),
        qyes(true),
        "");
    r = c.batch("batch_alias", qarr(alias1, alias2));
    c.record(
        "qa_d7b1b6768d39",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "all_target_successes",
            c.batchResultsValid(r, qarr(alias1, alias2)),
            "wire_fields",
            qsame(qg(r, "data"), qg(r, "raw")),
            "message_linkage",
            qyes(qg(r, "messages_persisted"))),
        qyes(true),
        "");
  }

  static void flow_negative(FullQA c) throws Exception {
    Object r = null;
    r = c.negative("empty");
    c.record("qa_f0179b307f6b", qm("rejected_like_REST", qyes(qg(r, "verified"))), qyes(true), "");
    r = c.negative("platform");
    c.record(
        "qa_65322fbec684", qm("legacy_error_like_REST", qyes(qg(r, "verified"))), qyes(true), "");
    r = c.negative("message");
    c.record(
        "qa_a62eda72d0a4",
        qm(
            "single_type_controls_valid",
            qyes(qg(r, "controls_valid")),
            "same_HTTP_code_message",
            qyes(qg(r, "verified"))),
        qyes(qyes(qg(r, "controls_valid"))),
        "Cannot validate mutual exclusion without accepted single-type controls");
    r = c.negative("notification");
    c.record(
        "qa_358fd717fbad",
        qm(
            "single_type_controls_valid",
            qyes(qg(r, "controls_valid")),
            "same_HTTP_code_message",
            qyes(qg(r, "verified"))),
        qyes(qyes(qg(r, "controls_valid"))),
        "Cannot validate mutual exclusion without accepted single-type controls");
    r = c.negative("live_activity");
    c.record(
        "qa_f27bda2df7c2",
        qm(
            "single_type_controls_valid",
            qyes(qg(r, "controls_valid")),
            "same_HTTP_code_message",
            qyes(qg(r, "verified"))),
        qyes(qyes(qg(r, "controls_valid"))),
        "Cannot validate mutual exclusion without accepted single-type controls");
  }

  static void flow_auth(FullQA c) throws Exception {
    Object r = null;
    r = c.sdk("vip", qarr());
    c.record(
        "qa_861607f5c661",
        qm(
            "HTTP200",
            qeq(qg(r, "status"), 200),
            "integer_status_and_64bit_expiry",
            c.vipValid(r),
            "wire_fields",
            qsame(qg(r, "data"), qg(r, "raw"))),
        qyes(true),
        "");
    r = c.invalidAuth();
    c.record("qa_99b351df6c47", qm("REST_and_SDK_reject", qyes(qg(r, "verified"))), qyes(true), "");
  }

  static void flow_token(FullQA c) throws Exception {
    Object r = null;
    c.write();
    r = c.apnsToken();
    c.record(
        "qa_113173a66889",
        qm(
            "fields_match_wire",
            qyes(qg(r, "verified")),
            "mapping_preserved_or_cleaned",
            qyes(qg(r, "cleanup"))),
        qyes(true),
        "");
  }

  static void flow_group(FullQA c) throws Exception {
    Object r = null;
    c.write();
    r = c.groupPush();
    c.record(
        "qa_992ab0e0747a",
        qm(
            "two_dynamic_members",
            qyes(qg(r, "verified")),
            "explicit_alias_only",
            qyes(qg(r, "explicit_target"))),
        qyes(true),
        "");
  }

  static void flow_image(FullQA c) throws Exception {
    Object r = null;
    c.write();
    r = c.imageUpload();
    c.record(
        "qa_b0478ec2b424",
        qm(
            "eligible_984x369_resource",
            qyes(qg(r, "resource_valid")),
            "HTTP_and_picture_ID_exact",
            qyes(qg(r, "verified"))),
        qyes(qyes(qg(r, "prerequisite"))),
        "QA/vendor image capability unavailable; inspect native response code");
  }

  static void flow_voice(FullQA c) throws Exception {
    Object r = null;
    c.write();
    r = c.prepareVoice();
    c.record(
        "qa_bd03c2bacf73",
        qm(
            "multipart_and_fields",
            qyes(qg(r, "upload_verified")),
            "detail_matches_upload",
            qyes(qg(r, "detail_verified"))),
        qyes(true),
        "");
    c.record(
        "qa_a4be39a4d66b",
        qm("direct_array_and_known_template", qyes(qg(r, "list_verified"))),
        qyes(true),
        "");
    c.record(
        "qa_c7377d6722fc", qm("file_url_matches", qyes(qg(r, "detail_verified"))), qyes(true), "");
    r = c.deleteVoice();
    c.record(
        "qa_95007431d632",
        qm("SDK_success", qyes(qg(r, "sdk_ok")), "list_and_detail_absent", qyes(qg(r, "verified"))),
        qyes(true),
        "");
  }

  static void flow_schedule(FullQA c) throws Exception {
    Object r = null;
    c.write();
    r = c.prepareSchedules();
    c.record(
        "qa_cc992174acf7",
        qm("native_business_assertions", qyes(qg(r, "create"))),
        qyes(qyes(qg(r, "create_ready"))),
        "Scheduled task, timezone/activity, or positive trigger prerequisite not established");
    c.record(
        "qa_fbe62ef8b3e7",
        qm("native_business_assertions", qyes(qg(r, "get"))),
        qyes(qyes(qg(r, "get_ready"))),
        "Scheduled task, timezone/activity, or positive trigger prerequisite not established");
    c.record(
        "qa_c69b89ef022d",
        qm("native_business_assertions", qyes(qg(r, "list"))),
        qyes(qyes(qg(r, "list_ready"))),
        "Scheduled task, timezone/activity, or positive trigger prerequisite not established");
    c.record(
        "qa_f47503d90583",
        qm("native_business_assertions", qyes(qg(r, "intelligent"))),
        qyes(qyes(qg(r, "intelligent_ready"))),
        "Scheduled task, timezone/activity, or positive trigger prerequisite not established");
    c.record(
        "qa_b1336ed19443",
        qm("native_business_assertions", qyes(qg(r, "messages"))),
        qyes(qyes(qg(r, "messages_ready"))),
        "Scheduled task, timezone/activity, or positive trigger prerequisite not established");
    c.record(
        "qa_0359444e46a9",
        qm("native_business_assertions", qyes(qg(r, "update"))),
        qyes(qyes(qg(r, "update_ready"))),
        "Scheduled task, timezone/activity, or positive trigger prerequisite not established");
    c.record(
        "qa_cbbfd7d59d01",
        qm("native_business_assertions", qyes(qg(r, "delete"))),
        qyes(qyes(qg(r, "delete_ready"))),
        "Scheduled task, timezone/activity, or positive trigger prerequisite not established");
  }

  static void flow_stats(FullQA c) throws Exception {
    Object r = null;
    c.write();
    r = c.prepareStatistics();
    c.record(
        "qa_06327bbf436b",
        qm("nonempty_known_business_data_and_fields", qyes(qg(r, "message"))),
        qyes(qyes(qg(r, "message_ready"))),
        "Known message/RID/plan relation or nonzero HMOS statistics not available in bounded"
            + " observation window");
    c.record(
        "qa_cc281fbd1340",
        qm("nonempty_known_business_data_and_fields", qyes(qg(r, "lifecycle"))),
        qyes(qyes(qg(r, "lifecycle_ready"))),
        "Known message/RID/plan relation or nonzero HMOS statistics not available in bounded"
            + " observation window");
    c.record(
        "qa_6f9ed781a410",
        qm("nonempty_known_business_data_and_fields", qyes(qg(r, "batch_lifecycle"))),
        qyes(qyes(qg(r, "batch_lifecycle_ready"))),
        "Known message/RID/plan relation or nonzero HMOS statistics not available in bounded"
            + " observation window");
    c.record(
        "qa_0c64e79794cc",
        qm("nonempty_known_business_data_and_fields", qyes(qg(r, "users"))),
        qyes(qyes(qg(r, "users_ready"))),
        "Known message/RID/plan relation or nonzero HMOS statistics not available in bounded"
            + " observation window");
    c.record(
        "qa_29349c0683f5",
        qm("nonempty_known_business_data_and_fields", qyes(qg(r, "plan"))),
        qyes(qyes(qg(r, "plan_ready"))),
        "Known message/RID/plan relation or nonzero HMOS statistics not available in bounded"
            + " observation window");
    c.record(
        "qa_cb8dc4d08772",
        qm("nonempty_known_business_data_and_fields", qyes(qg(r, "plan_messages"))),
        qyes(qyes(qg(r, "plan_messages_ready"))),
        "Known message/RID/plan relation or nonzero HMOS statistics not available in bounded"
            + " observation window");
  }

  static void flow_physical(FullQA c) throws Exception {
    Object r = null;
    r = c.physicalNotifications();
    c.record(
        "qa_e9617155db83",
        qm("native_API_and_physical_notification_assertions", qyes(qg(r, "push"))),
        qyes(qyes(qg(r, "push_ready"))),
        "Matching QA application and physical notification observation are required");
    c.record(
        "qa_f5308cfc2d09",
        qm("native_API_and_physical_notification_assertions", qyes(qg(r, "validate"))),
        qyes(qyes(qg(r, "validate_ready"))),
        "Matching QA application and physical notification observation are required");
    c.record(
        "qa_db1a22ee70da",
        qm("native_API_and_physical_notification_assertions", qyes(qg(r, "withdraw"))),
        qyes(qyes(qg(r, "withdraw_ready"))),
        "Matching QA application and physical notification observation are required");
  }

  final Map<String, Map<String, Object>> cache = new HashMap<>();

  @TestFactory
  Stream<DynamicTest> fullIntegration() {
    return Stream.of(
            DynamicTest.dynamicTest(
                "qa_f76445835b7d 删除别名",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_f76445835b7d",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_4228ae60b590 查询别名",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_4228ae60b590",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_d7b1b6768d39 按别名批量单推",
                () ->
                    FullQA.execute(
                        cache,
                        "batch",
                        "qa_d7b1b6768d39",
                        Arrays.asList("qa_81f620c4cc20", "qa_d7b1b6768d39"),
                        FullIntegrationTest::flow_batch)),
            DynamicTest.dynamicTest(
                "qa_81f620c4cc20 按注册ID批量单推",
                () ->
                    FullQA.execute(
                        cache,
                        "batch",
                        "qa_81f620c4cc20",
                        Arrays.asList("qa_81f620c4cc20", "qa_d7b1b6768d39"),
                        FullIntegrationTest::flow_batch)),
            DynamicTest.dynamicTest(
                "qa_42caca08cb39 删除设备",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_42caca08cb39",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_edfec6f68242 批量查询设备状态",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_edfec6f68242",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_48e10c8c3688 查询设备",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_48e10c8c3688",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_418e812084ee 清空标签后关系读回",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_418e812084ee",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_3a16670b310f 设置设备",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_3a16670b310f",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_992ab0e0747a 分组推送成功",
                () ->
                    FullQA.execute(
                        cache,
                        "group",
                        "qa_992ab0e0747a",
                        Arrays.asList("qa_992ab0e0747a"),
                        FullIntegrationTest::flow_group)),
            DynamicTest.dynamicTest(
                "qa_b0478ec2b424 上传OPPO大图URL",
                () ->
                    FullQA.execute(
                        cache,
                        "image",
                        "qa_b0478ec2b424",
                        Arrays.asList("qa_b0478ec2b424"),
                        FullIntegrationTest::flow_image)),
            DynamicTest.dynamicTest(
                "qa_6df4ebb6c583 全条件筛选",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_6df4ebb6c583",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_1068750e639d 分页第二页",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_1068750e639d",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_bbef5f838820 创建推送计划",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_bbef5f838820",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_793b07888b66 删除推送计划",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_793b07888b66",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_7c9f085a13ea 批量删除推送计划",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_7c9f085a13ea",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_3e98ca439abe 描述单条件筛选",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_3e98ca439abe",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_ee67b55b2f74 更新已有计划描述",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_ee67b55b2f74",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_a8afa2ba63c2 来源单条件筛选",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_a8afa2ba63c2",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_0e01ec2f278a 查询推送计划列表",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_0e01ec2f278a",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_cb8dc4d08772 查询计划关联消息",
                () ->
                    FullQA.execute(
                        cache,
                        "stats",
                        "qa_cb8dc4d08772",
                        Arrays.asList(
                            "qa_06327bbf436b",
                            "qa_cc281fbd1340",
                            "qa_6f9ed781a410",
                            "qa_0c64e79794cc",
                            "qa_29349c0683f5",
                            "qa_cb8dc4d08772"),
                        FullIntegrationTest::flow_stats)),
            DynamicTest.dynamicTest(
                "qa_0df5c6ed2605 计划ID长度1",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_0df5c6ed2605",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_2e34db0cb38c 计划ID长度49",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_2e34db0cb38c",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_eeb26ebb2b07 计划ID长度50",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_eeb26ebb2b07",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_d71d62850538 计划ID长度51",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_d71d62850538",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_c32bda243404 计划分页容量0",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_c32bda243404",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_d1c2dc88ee2a 计划分页容量1",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_d1c2dc88ee2a",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_452338f7f0cb 计划分页容量100",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_452338f7f0cb",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_2d67bd95bf58 计划分页容量101",
                () ->
                    FullQA.execute(
                        cache,
                        "plans",
                        "qa_2d67bd95bf58",
                        Arrays.asList(
                            "qa_bbef5f838820",
                            "qa_0e01ec2f278a",
                            "qa_2d67bd95bf58",
                            "qa_452338f7f0cb",
                            "qa_d1c2dc88ee2a",
                            "qa_1068750e639d",
                            "qa_c32bda243404",
                            "qa_ee67b55b2f74",
                            "qa_3e98ca439abe",
                            "qa_a8afa2ba63c2",
                            "qa_6df4ebb6c583",
                            "qa_793b07888b66",
                            "qa_7c9f085a13ea",
                            "qa_0df5c6ed2605",
                            "qa_2e34db0cb38c",
                            "qa_eeb26ebb2b07",
                            "qa_d71d62850538"),
                        FullIntegrationTest::flow_plans)),
            DynamicTest.dynamicTest(
                "qa_f27bda2df7c2 VoIP与live_activity互斥错误",
                () ->
                    FullQA.execute(
                        cache,
                        "negative",
                        "qa_f27bda2df7c2",
                        Arrays.asList(
                            "qa_f0179b307f6b",
                            "qa_65322fbec684",
                            "qa_a62eda72d0a4",
                            "qa_358fd717fbad",
                            "qa_f27bda2df7c2"),
                        FullIntegrationTest::flow_negative)),
            DynamicTest.dynamicTest(
                "qa_a62eda72d0a4 VoIP与message互斥错误",
                () ->
                    FullQA.execute(
                        cache,
                        "negative",
                        "qa_a62eda72d0a4",
                        Arrays.asList(
                            "qa_f0179b307f6b",
                            "qa_65322fbec684",
                            "qa_a62eda72d0a4",
                            "qa_358fd717fbad",
                            "qa_f27bda2df7c2"),
                        FullIntegrationTest::flow_negative)),
            DynamicTest.dynamicTest(
                "qa_358fd717fbad VoIP与notification互斥错误",
                () ->
                    FullQA.execute(
                        cache,
                        "negative",
                        "qa_358fd717fbad",
                        Arrays.asList(
                            "qa_f0179b307f6b",
                            "qa_65322fbec684",
                            "qa_a62eda72d0a4",
                            "qa_358fd717fbad",
                            "qa_f27bda2df7c2"),
                        FullIntegrationTest::flow_negative)),
            DynamicTest.dynamicTest(
                "qa_e9617155db83 创建通知推送",
                () ->
                    FullQA.execute(
                        cache,
                        "physical",
                        "qa_e9617155db83",
                        Arrays.asList("qa_e9617155db83", "qa_f5308cfc2d09", "qa_db1a22ee70da"),
                        FullIntegrationTest::flow_physical)),
            DynamicTest.dynamicTest(
                "qa_65322fbec684 平台类型非法错误",
                () ->
                    FullQA.execute(
                        cache,
                        "negative",
                        "qa_65322fbec684",
                        Arrays.asList(
                            "qa_f0179b307f6b",
                            "qa_65322fbec684",
                            "qa_a62eda72d0a4",
                            "qa_358fd717fbad",
                            "qa_f27bda2df7c2"),
                        FullIntegrationTest::flow_negative)),
            DynamicTest.dynamicTest(
                "qa_db1a22ee70da 撤回推送",
                () ->
                    FullQA.execute(
                        cache,
                        "physical",
                        "qa_db1a22ee70da",
                        Arrays.asList("qa_e9617155db83", "qa_f5308cfc2d09", "qa_db1a22ee70da"),
                        FullIntegrationTest::flow_physical)),
            DynamicTest.dynamicTest(
                "qa_f5308cfc2d09 校验推送",
                () ->
                    FullQA.execute(
                        cache,
                        "physical",
                        "qa_f5308cfc2d09",
                        Arrays.asList("qa_e9617155db83", "qa_f5308cfc2d09", "qa_db1a22ee70da"),
                        FullIntegrationTest::flow_physical)),
            DynamicTest.dynamicTest(
                "qa_f0179b307f6b 空必填目标错误",
                () ->
                    FullQA.execute(
                        cache,
                        "negative",
                        "qa_f0179b307f6b",
                        Arrays.asList(
                            "qa_f0179b307f6b",
                            "qa_65322fbec684",
                            "qa_a62eda72d0a4",
                            "qa_358fd717fbad",
                            "qa_f27bda2df7c2"),
                        FullIntegrationTest::flow_negative)),
            DynamicTest.dynamicTest(
                "qa_c69b89ef022d 分页查询定时任务",
                () ->
                    FullQA.execute(
                        cache,
                        "schedule",
                        "qa_c69b89ef022d",
                        Arrays.asList(
                            "qa_cc992174acf7",
                            "qa_fbe62ef8b3e7",
                            "qa_c69b89ef022d",
                            "qa_f47503d90583",
                            "qa_b1336ed19443",
                            "qa_0359444e46a9",
                            "qa_cbbfd7d59d01"),
                        FullIntegrationTest::flow_schedule)),
            DynamicTest.dynamicTest(
                "qa_cc992174acf7 创建定时任务",
                () ->
                    FullQA.execute(
                        cache,
                        "schedule",
                        "qa_cc992174acf7",
                        Arrays.asList(
                            "qa_cc992174acf7",
                            "qa_fbe62ef8b3e7",
                            "qa_c69b89ef022d",
                            "qa_f47503d90583",
                            "qa_b1336ed19443",
                            "qa_0359444e46a9",
                            "qa_cbbfd7d59d01"),
                        FullIntegrationTest::flow_schedule)),
            DynamicTest.dynamicTest(
                "qa_cbbfd7d59d01 删除定时任务",
                () ->
                    FullQA.execute(
                        cache,
                        "schedule",
                        "qa_cbbfd7d59d01",
                        Arrays.asList(
                            "qa_cc992174acf7",
                            "qa_fbe62ef8b3e7",
                            "qa_c69b89ef022d",
                            "qa_f47503d90583",
                            "qa_b1336ed19443",
                            "qa_0359444e46a9",
                            "qa_cbbfd7d59d01"),
                        FullIntegrationTest::flow_schedule)),
            DynamicTest.dynamicTest(
                "qa_f47503d90583 智能定时写后读回",
                () ->
                    FullQA.execute(
                        cache,
                        "schedule",
                        "qa_f47503d90583",
                        Arrays.asList(
                            "qa_cc992174acf7",
                            "qa_fbe62ef8b3e7",
                            "qa_c69b89ef022d",
                            "qa_f47503d90583",
                            "qa_b1336ed19443",
                            "qa_0359444e46a9",
                            "qa_cbbfd7d59d01"),
                        FullIntegrationTest::flow_schedule)),
            DynamicTest.dynamicTest(
                "qa_0359444e46a9 更新定时任务",
                () ->
                    FullQA.execute(
                        cache,
                        "schedule",
                        "qa_0359444e46a9",
                        Arrays.asList(
                            "qa_cc992174acf7",
                            "qa_fbe62ef8b3e7",
                            "qa_c69b89ef022d",
                            "qa_f47503d90583",
                            "qa_b1336ed19443",
                            "qa_0359444e46a9",
                            "qa_cbbfd7d59d01"),
                        FullIntegrationTest::flow_schedule)),
            DynamicTest.dynamicTest(
                "qa_fbe62ef8b3e7 查询定时任务详情",
                () ->
                    FullQA.execute(
                        cache,
                        "schedule",
                        "qa_fbe62ef8b3e7",
                        Arrays.asList(
                            "qa_cc992174acf7",
                            "qa_fbe62ef8b3e7",
                            "qa_c69b89ef022d",
                            "qa_f47503d90583",
                            "qa_b1336ed19443",
                            "qa_0359444e46a9",
                            "qa_cbbfd7d59d01"),
                        FullIntegrationTest::flow_schedule)),
            DynamicTest.dynamicTest(
                "qa_b1336ed19443 查询定时消息ID",
                () ->
                    FullQA.execute(
                        cache,
                        "schedule",
                        "qa_b1336ed19443",
                        Arrays.asList(
                            "qa_cc992174acf7",
                            "qa_fbe62ef8b3e7",
                            "qa_c69b89ef022d",
                            "qa_f47503d90583",
                            "qa_b1336ed19443",
                            "qa_0359444e46a9",
                            "qa_cbbfd7d59d01"),
                        FullIntegrationTest::flow_schedule)),
            DynamicTest.dynamicTest(
                "qa_6f9ed781a410 批量查询生命周期",
                () ->
                    FullQA.execute(
                        cache,
                        "stats",
                        "qa_6f9ed781a410",
                        Arrays.asList(
                            "qa_06327bbf436b",
                            "qa_cc281fbd1340",
                            "qa_6f9ed781a410",
                            "qa_0c64e79794cc",
                            "qa_29349c0683f5",
                            "qa_cb8dc4d08772"),
                        FullIntegrationTest::flow_stats)),
            DynamicTest.dynamicTest(
                "qa_cc281fbd1340 查询消息生命周期",
                () ->
                    FullQA.execute(
                        cache,
                        "stats",
                        "qa_cc281fbd1340",
                        Arrays.asList(
                            "qa_06327bbf436b",
                            "qa_cc281fbd1340",
                            "qa_6f9ed781a410",
                            "qa_0c64e79794cc",
                            "qa_29349c0683f5",
                            "qa_cb8dc4d08772"),
                        FullIntegrationTest::flow_stats)),
            DynamicTest.dynamicTest(
                "qa_06327bbf436b 查询消息统计",
                () ->
                    FullQA.execute(
                        cache,
                        "stats",
                        "qa_06327bbf436b",
                        Arrays.asList(
                            "qa_06327bbf436b",
                            "qa_cc281fbd1340",
                            "qa_6f9ed781a410",
                            "qa_0c64e79794cc",
                            "qa_29349c0683f5",
                            "qa_cb8dc4d08772"),
                        FullIntegrationTest::flow_stats)),
            DynamicTest.dynamicTest(
                "qa_0c64e79794cc 查询用户统计",
                () ->
                    FullQA.execute(
                        cache,
                        "stats",
                        "qa_0c64e79794cc",
                        Arrays.asList(
                            "qa_06327bbf436b",
                            "qa_cc281fbd1340",
                            "qa_6f9ed781a410",
                            "qa_0c64e79794cc",
                            "qa_29349c0683f5",
                            "qa_cb8dc4d08772"),
                        FullIntegrationTest::flow_stats)),
            DynamicTest.dynamicTest(
                "qa_29349c0683f5 查询计划统计",
                () ->
                    FullQA.execute(
                        cache,
                        "stats",
                        "qa_29349c0683f5",
                        Arrays.asList(
                            "qa_06327bbf436b",
                            "qa_cc281fbd1340",
                            "qa_6f9ed781a410",
                            "qa_0c64e79794cc",
                            "qa_29349c0683f5",
                            "qa_cb8dc4d08772"),
                        FullIntegrationTest::flow_stats)),
            DynamicTest.dynamicTest(
                "qa_ff6fe44cc46d 删除标签",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_ff6fe44cc46d",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_00cbd3d800bd 查询全部标签",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_00cbd3d800bd",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_d5581d7d5601 查询标签设备数",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_d5581d7d5601",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_5f8b9b9d7f78 查询标签配额",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_5f8b9b9d7f78",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_a925a3248bcd 查询设备标签关系",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_a925a3248bcd",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_83ba432b89af 设置标签绑定",
                () ->
                    FullQA.execute(
                        cache,
                        "devices",
                        "qa_83ba432b89af",
                        Arrays.asList(
                            "qa_48e10c8c3688",
                            "qa_4228ae60b590",
                            "qa_f76445835b7d",
                            "qa_3a16670b310f",
                            "qa_418e812084ee",
                            "qa_00cbd3d800bd",
                            "qa_a925a3248bcd",
                            "qa_d5581d7d5601",
                            "qa_5f8b9b9d7f78",
                            "qa_83ba432b89af",
                            "qa_ff6fe44cc46d",
                            "qa_edfec6f68242",
                            "qa_42caca08cb39"),
                        FullIntegrationTest::flow_devices)),
            DynamicTest.dynamicTest(
                "qa_113173a66889 注册厂商Token",
                () ->
                    FullQA.execute(
                        cache,
                        "token",
                        "qa_113173a66889",
                        Arrays.asList("qa_113173a66889"),
                        FullIntegrationTest::flow_token)),
            DynamicTest.dynamicTest(
                "qa_99b351df6c47 无效凭据拒绝",
                () ->
                    FullQA.execute(
                        cache,
                        "auth",
                        "qa_99b351df6c47",
                        Arrays.asList("qa_861607f5c661", "qa_99b351df6c47"),
                        FullIntegrationTest::flow_auth)),
            DynamicTest.dynamicTest(
                "qa_861607f5c661 查询应用VIP状态",
                () ->
                    FullQA.execute(
                        cache,
                        "auth",
                        "qa_861607f5c661",
                        Arrays.asList("qa_861607f5c661", "qa_99b351df6c47"),
                        FullIntegrationTest::flow_auth)),
            DynamicTest.dynamicTest(
                "qa_bd03c2bacf73 上传语音文件",
                () ->
                    FullQA.execute(
                        cache,
                        "voice",
                        "qa_bd03c2bacf73",
                        Arrays.asList(
                            "qa_bd03c2bacf73",
                            "qa_a4be39a4d66b",
                            "qa_c7377d6722fc",
                            "qa_95007431d632"),
                        FullIntegrationTest::flow_voice)),
            DynamicTest.dynamicTest(
                "qa_95007431d632 删除语音模板",
                () ->
                    FullQA.execute(
                        cache,
                        "voice",
                        "qa_95007431d632",
                        Arrays.asList(
                            "qa_bd03c2bacf73",
                            "qa_a4be39a4d66b",
                            "qa_c7377d6722fc",
                            "qa_95007431d632"),
                        FullIntegrationTest::flow_voice)),
            DynamicTest.dynamicTest(
                "qa_a4be39a4d66b 查询语音列表",
                () ->
                    FullQA.execute(
                        cache,
                        "voice",
                        "qa_a4be39a4d66b",
                        Arrays.asList(
                            "qa_bd03c2bacf73",
                            "qa_a4be39a4d66b",
                            "qa_c7377d6722fc",
                            "qa_95007431d632"),
                        FullIntegrationTest::flow_voice)),
            DynamicTest.dynamicTest(
                "qa_c7377d6722fc 查询语音详情",
                () ->
                    FullQA.execute(
                        cache,
                        "voice",
                        "qa_c7377d6722fc",
                        Arrays.asList(
                            "qa_bd03c2bacf73",
                            "qa_a4be39a4d66b",
                            "qa_c7377d6722fc",
                            "qa_95007431d632"),
                        FullIntegrationTest::flow_voice)))
        .filter(
            test -> {
              String selected = System.getenv("ENGAGELAB_SCENARIO_IDS");
              return selected == null
                  || selected.isEmpty()
                  || Arrays.asList(selected.split(","))
                      .contains(test.getDisplayName().split(" ", 2)[0]);
            });
  }
}
