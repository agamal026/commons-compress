# COMPRESS-729: ZIP compressor cleanup evidence

This packet records the correction for [COMPRESS-729](https://issues.apache.org/jira/browse/COMPRESS-729), where `ZipArchiveOutputStream.close()` can leave its Deflater open after archive finalization throws an ordinary `IOException`.

## Source and behavior

| Revision | Identity |
| --- | --- |
| Upstream base | `12b02d906b7945bccdcbdd2cf7b4523accd542da` |
| Corrected source | `f0ad0a114f71b3708806873db969966733f0f20f` |
| Corrected source tree | `d68837c8ebfdd586af5fbae8cdf38e9812483e1e` |
| Test-only failing baseline | `3077845fbfaa132937e428f21f95fb048802b170` |
| Baseline tree | `947b16b8ff27a062ddcd7fb77cc91a6428f44d0c` |
| Hosted PR merge checkout | `2109d9a12e3a980b4634f1dda7d4e63729491e62` |

The hosted merge tree was independently read through GitHub and exactly equals the corrected source tree. Local executions used the corresponding verified working trees; the local Git HEAD remained the upstream base. Source hashes were unchanged throughout each execution.

The production change closes `StreamCompressor` in the terminal `destroy()` path and retains the existing backing-output closure in `finally`. Successful `finish()` cleanup, ordinary finish leaving the output open, recovery from premature finish, and existing exception precedence remain intact. A standalone failing `finish()` still requires caller closure. The three-file diff contains the cleanup, seven added test invocations, and one unreleased changelog entry; no API, dependency, workflow or build configuration changes.

## Local executions on 2026-10-31

Maven 3.9.11, Ubuntu OpenJDK 17.0.20, Linux x86_64, unprivileged execution, UTC. Runtime options isolated the dependency cache and temporary directory and supplied task-private Maven settings. Settings are excluded from this packet.

| Execution, UTC | Reported | Passed | Failures | Errors | Skipped | Overall result |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| Baseline focused, 20:43:48-20:44:48 | 11 | 6 | 5 | 0 | 0 | Expected regression failures |
| Corrected focused, 21:04:28-21:04:43 | 11 | 11 | 0 | 0 | 0 | Success |
| Corrected default build, 21:07:54-21:11:07 | 4,788 | 4,762 | 0 | 0 | 26 | Failed at Javadoc executable discovery |

Focused command: `mvn --errors --show-version --batch-mode --no-transfer-progress -Dtest=ZipArchiveOutputStreamTest test`. The full run supplied no lifecycle goal or test selector, so the project's declared default executed.

All five baseline failures specifically report unreleased Deflater native state. They cover finalization write failure, simultaneous output-close failure, an unclosed entry, and direct destruction with successful or failing output closure. Existing exception identities and output-close behavior pass before those failing cleanup assertions. The other two new invocations preserve successful finish, premature-finish recovery, repeated close, and a valid ZIP round trip. Four existing class tests also pass.

The baseline and corrected test sources differ only in one explanatory comment. The state oracle accesses the real protected Deflater and accepts exactly NPE or IllegalStateException. Its pre-Java-25 behavior is based on inspected OpenJDK implementations, not a universal post-`end()` API guarantee. It uses no reflection, GC, timing or native-memory-pressure demonstration. Each failure test explicitly releases the Deflater after the assertion, including on the failing baseline.

The local full run completed its tests and preceding quality checks, but the installed Java distribution has no `bin/javadoc` launcher. A separate module version query confirmed `jdk.javadoc` 17.0.20 exists; that query did not generate the project's documentation. The full local build remains failed. Setup-only launcher failures are retained separately and contain no test execution.

## Hosted full default builds

[Owned validation PR2](https://github.com/agamal026/commons-compress/pull/2) triggered the existing unchanged workflow. [Java CI run 41242178097](https://github.com/agamal026/commons-compress/actions/runs/41242178097), attempt1, ran from 21:08:31 to 21:16:13 UTC on 2026-10-31 and completed successfully. All 19 actual logs show the merge checkout above and this command, with no test filters or skip flags:

```text
mvn --errors --show-version --batch-mode --no-transfer-progress
```

| Environment and JDK labels | Jobs | Reported per job | Passed per job | Failures/errors | Skipped per job |
| --- | ---: | ---: | ---: | ---: | ---: |
| Linux: 8, 11, 17, 21, 25, 27 | 6 | 4,788 | 4,762 | 0/0 | 26 |
| Windows: 8, 11, 17, 21, 25, 27 | 6 | 4,788 | 4,758 | 0/0 | 30 |
| macOS: 8, 11, 17, 21, 25, 27 | 6 | 4,788 | 4,760 | 0/0 | 28 |
| Linux: 28-ea, experimental | 1 | 4,788 | 4,762 | 0/0 | 26 |

Every job executes 253 class summaries, independently summed during review. The contribution class passes 11/11 without skips in every job. The repeated lifecycle caused by SPDX does not rerun Surefire; it explicitly skips an already-executed configuration and is counted once.

Actual runtime versions, Maven versions, job IDs, timestamps and per-job totals are in [receipt.json](receipt.json). Maven versions differ by runner: Linux 3.9.15, Windows 3.9.16, macOS 3.10.0. Java labels alone are not exact runtime identities.

### Quality gates and limits

- All 19 builds execute the configured build-plan, test, RAT, Checkstyle, PMD and Japicmp gates and reach `BUILD SUCCESS`.
- Javadoc succeeds in 16 jobs. The unchanged Java 8 profile omits it in three jobs.
- JaCoCo actually enforces the configured thresholds in nine Java 8/11/17 jobs, analyzing 523 classes. Ten Java 21+ jobs explicitly skip checks because execution data is absent. This is an unchanged configuration limitation; no coverage enforcement is claimed for those ten jobs. Early interpolation of the inherited `argLine` is a source-backed possible explanation, without a printed forked-JVM command proving the cause.
- The local Checkstyle report has 710 files and zero errors; PMD reports no violations or processing/configuration errors; RAT reports 740 approved files and zero unapproved or unknown files.
- Japicmp compares the snapshot with 1.28.0, not just this patch. Its local report contains 40 source-incompatible release-level class flags and no binary-incompatible class flags. `ZipArchiveOutputStream` is not among the modified API classes. Successful configured checks do not establish universal source compatibility of the entire snapshot.
- Existing compiler deprecation/unchecked notes, PMD warnings, module and SBOM diagnostics, SPDX `NOASSERTION` mappings, and nonfatal CI cache collisions remain in the logs. The local SPDX JSON-TOC error/fallback and macOS Java 11 metadata certificate warning are preserved. No warning-free or complete-license-attribution claim is made.
- SpotBugs, Failsafe and the optional large-file integration profiles were not executed in the recorded builds.

Two independent automated reviewers approved the exact three source hashes in one review cycle, then independently audited the baseline/corrected evidence and all 19 hosted logs. Automated review is not human review or upstream acceptance. OpenAI ChatGPT (GPT-6 Astra Pro) drafted the code, tests and this evidence; the source commit contains the required `Assisted-By:` trailer.

## Retained evidence

[validation-evidence.tar.gz](validation-evidence.tar.gz) contains 38 files: all 19 complete normalized hosted logs, local execution logs and receipts, extracted native test results, selected native quality reports, and manifests. It excludes runtime settings, credentials, raw Surefire environment properties and downloaded runtimes/dependencies.

Archive size: **456,577 bytes**. SHA-256: **`9b480348beb0da1aeafcb753d1acbd5f27cab76e1f8be8ef4207e364a219ee6d`**.

The archive normalizes BOM/line endings, replaces task-private paths or proxy hostnames, and omits checkout authentication-configuration lines. [MANIFEST.json](MANIFEST.json) records original captured-file and archived hashes. These are normalized technical logs, not a claim of byte-identical raw server archives.

This evidence branch adds documentation and proof files to the corrected source. Validation applies to the identified source tree, not to a claim that the evidence additions themselves were part of the tested tree.
