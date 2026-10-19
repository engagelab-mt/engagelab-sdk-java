# Formal testcase traceability

`coverage-map.json` records all 571 formal cases across Java (190), Go (188), and Python (193). Each row maps a TAPD ID to a stable native scenario, test entrypoint, source path, API and repository. Filter `language` to find this repository's 190 cases.

This is documentation for review and optional report post-processing. Test discovery, input data, assertions and pass/fail decisions do not load it. Deleting this directory does not change the runnable tests. Add new native API tests directly; a pre-existing formal case or TAPD connection is not required. Update mappings when a formal case is later added.

Execution results are deliberately excluded: local contract fixtures are not live QA evidence, and historical QA results must not be presented as a new execution. Shared case IDs do not imply a cross-repository runtime dependency.
