# Framed LZ4/Snappy counters: local validation

The published six-file fix is `7e6477880259d60d4dfa60a5e34e4ea7875176b1` (tree `9c0430eb9a40f5f493132589e6fef3b7ffcbe494`), based on upstream `611cad06b66842692870c6ae98f50de7ce4f25d5`. It gives each framed reader its own uncompressed-byte count without changing historical compressed-byte accounting.

| Execution | Tests | Failures | Errors | Skips | Result |
| --- | ---: | ---: | ---: | ---: | --- |
| Test-only upstream baseline | 89 | 39 | 0 | 0 | Counter defect reproduced |
| Identical tests with the fix | 89 | 0 | 0 | 0 | Passed |
| Default Maven build with the fix | 4,791 | 2 | 0 | 26 | Stopped on TAR ownership assumptions |
| Unmodified upstream ownership control | 2 | 2 | 0 | 0 | Same failures reproduced |

The [baseline packet](https://github.com/agamal026/commons-compress/blob/191c9e8ce0fb7c478f64bd8912fc56d9747eca5d/contribution-review/framed-statistics/baseline/README.md) contains the fail-before test sources and console evidence. All four test-file hashes are unchanged in the fixed run. The ten shared invocations and existing raw/mixed/multi-block regressions pass after the fix.

The focused run executed the prepared working tree before commit publication; its six file hashes match the published source exactly. The subsequent default Maven run executed the clean published commit. Runtime was Maven 3.9.11 and Temurin 17.0.20.1+1 on Linux.

## Full-build limitation and control

The two failures are `TarArchiveEntryTest.testLinuxFileInformationFromFile` and `TarArchiveEntryTest.testLinuxFileInformationFromPath`. They require nonzero fixture owner/group IDs, while this environment produces UID/GID 0 fixtures. Both fail with `expected: not equal but was: <0>` on an exact unmodified upstream archive. The local default build did not reach subsequent verification goals. No test exclusions were added.

A non-root setup could not execute because the environment maps only UID/GID 0. Hosted validation is therefore recorded separately in [owned draft PR #1](https://github.com/agamal026/commons-compress/pull/1). Its Maven workflow is unchanged from upstream; the owned base removes only two inherited schedule blocks. This packet makes no hosted-build success claim.

## Evidence

- [Machine-readable counts, source hashes and provenance](verification.json)
- [Focused Maven console](focused-maven.log)
- [Default Maven console, including both failures](default-maven.log)
- [Unchanged upstream ownership control console](upstream-ownership-control.log)
- [Independent automated source review](source-review.json)
- [Owned hosted-validation setup](ci-setup.json)

Implementation, tests and these records were generated using ChatGPT (GPT-6 Astra Pro). The separate source review is automated; no human review, legal signature, upstream acceptance, bounty eligibility or payment is asserted.
