# COMPRESS-729: experimental Java 28 log audit

Reviewed on 1 November 2026. This is a subsequent review of an already completed job, not a new test execution, source correction, upstream submission, or acceptance.

## Exact execution and source

- Existing Java CI run: [41323689495](https://github.com/agamal026/commons-compress/actions/runs/41323689495).
- Newly inspected complete decoded job log: [124899069030, Ubuntu / Java 28-ea](https://github.com/agamal026/commons-compress/actions/runs/41323689495/job/124899069030).
- Actual checkout in the log: `ca02e025e17bbafca302485c2454de2d0a2b3c7c`.
- Contribution source: `940471cf4900817c59160a10fddc1356fd04ce96`.
- Integration base: `69a29b75922f7e525412dc6b7454b70d1f2be2bd`.
- The [existing integration supplement](README.md) records the canonical checkout/source tree equality at `455af7fbc64febc97892e3b26e0cfd38b6c5f3f9`. This audit inspected the checkout identity in the additional log and reuses that previously verified tree relation.

The job used Ubuntu 26.04.1 x64, Eclipse Temurin `28.0.0+22.0.ea`, and Maven 3.9.15. Its actual command was:

```sh
mvn --errors --show-version --batch-mode --no-transfer-progress
```

`MAVEN_ARGS` was only `-ntp`. No test selector, coverage-threshold change, or added skip flag was used. Compilation targeted Java release 8.

## Executed test results

| Scope | Reported | Passed | Failures | Errors | Skipped | Original log time (UTC, November 1) |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| Complete Surefire run | 4,788 | 4,762 | 0 | 0 | 26 | 15:58:03.6889381 |
| ZipArchiveOutputStreamTest | 11 | 11 | 0 | 0 | 0 | 15:57:46.3113690 |
| ZipMemoryFileSystemTest | 15 | 15 | 0 | 0 | 0 | 15:56:59.4413873 |
| TarMemoryFileSystemTest | 3 | 3 | 0 | 0 | 0 | 15:56:57.7305014 |
| ZipCompressMethodZstdTest | 5 | 5 | 0 | 0 | 0 | 15:57:00.3434119 |
| ZstdRoundtripTest | 1,223 | 1,222 | 0 | 0 | 1 | 15:57:54.4888223 |

The listed classes are included in the complete total; they are not additional tests. Passed counts are reported totals minus failures, errors, and skips. The later SPDX-forked lifecycle explicitly reuses the already executed Surefire configuration; it is not a second 4,788-test run.

The Maven log reaches `BUILD SUCCESS` at **15:58:46.3750417 UTC**, with reported total time **2 minutes 21 seconds**. The experimental job therefore actually completed its build successfully; this conclusion does not rely solely on a workflow badge or the job's tolerated-failure setting.

## Quality gates and limits

RAT reports no issues, Checkstyle reports zero violations, and PMD, japicmp, and Javadoc execute through the successful default build. Generated japicmp reports were not separately inspected, so this is not a claim that every release-level compatibility warning is absent.

**JaCoCo coverage checking was not enforced in this Java 28 job.** `prepare-agent` prints an agent argument, but both `check` invocations explicitly skip because `target/jacoco.exec` is missing, at 15:58:17.1624206 and 15:58:22.7745771 UTC. An agent-setup message alone is not evidence that coverage data was collected. The cause was not diagnosed in this audit; no build configuration was changed.

The complete log retains nonfatal warnings about Java 8 source/target obsolescence, deprecated `sun.misc.Unsafe` use, restricted native-library loading in zstd-jni, CycloneDX metadata, SPDX `NOASSERTION`, module-descriptor inference, and PMD rule/classpath configuration. A cache-save collision after the successful build is not a test failure. No warning-free result is claimed.

The previous supplement inspected three platform logs. With this audit, four of the nineteen Java job logs have been inspected in full. All nineteen jobs and their Maven steps have successful canonical metadata, but exact test totals and per-gate execution for the other fifteen jobs have not been independently read from their logs. No extra local execution, optional large-file integration profile, SpotBugs, Failsafe, or CodeQL result is added by this audit.

## Evidence scope

This document is a manually transcribed observation record from the complete decoded GitHub job log. It is not the raw log or a complete-log archive. The original execution timestamps above remain November 1 at 15:56-15:58 UTC; reviewing or publishing this record does not change them.

The original correction, earlier baseline/control results, existing integration evidence, and source branch are unchanged. Upstream publication authorization remains a separate unresolved dependency. Review was serial automated review assisted by OpenAI ChatGPT (GPT-6 Astra Pro), not independent-agent review or human approval.
