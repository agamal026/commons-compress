# Framed LZ4/Snappy counters: completed hosted validation

All **19 actual Maven jobs passed**, including the experimental Java 28-ea job, on [owned validation PR #1](https://github.com/agamal026/commons-compress/pull/1). The [Java CI workflow](https://github.com/agamal026/commons-compress/actions/runs/39147450534) completed at 17:06:21 UTC on October 18, 2026. Final metadata and the last full log were retrieved at 17:07:10 UTC.

Source commit: `7e6477880259d60d4dfa60a5e34e4ea7875176b1` (tree `9c0430eb9a40f5f493132589e6fef3b7ffcbe494`). Every job checked out merge `76d0130283eb142314cf11bb42825798e9279a05`, tree `75180283dda44c16550c644a7df3f88545424ac4`. The complete merge-to-source comparison has only four schedule-line deletions in two workflow files. Source, tests and Maven build inputs otherwise match byte for byte.

| Platform | Jobs | Tests per build | Failures | Errors | Skips |
| --- | ---: | ---: | ---: | ---: | ---: |
| Ubuntu, including Java 28-ea | 7 | 4,791 | 0 | 0 | 26 |
| Windows | 6 | 4,791 | 0 | 0 | 30 |
| macOS | 6 | 4,791 | 0 | 0 | 28 |

All 89 relevant tests pass without skips in every job. The ordinary matrix is Java 8, 11, 17, 21, 25 and 27 on all three platforms. Every job ran the unchanged default command:

```sh
mvn --errors --show-version --batch-mode --no-transfer-progress
```

Full logs show successful build-plan, RAT, Checkstyle, PMD and japicmp checks. Javadoc also passed in 16 builds; the existing Java 8 profile omits that goal in three builds. No added exclusions or substitute test commands were used.

CodeQL completed analysis and uploaded results. Its separate build skips tests and some checks, so it is not counted as regression execution or proof of zero security findings. Dependency Review attempt 1 stopped because the fork's dependency graph was disabled; after enabling that capability, only the failed job was retried and attempt 2 passed. Both logs remain in this packet.

## Evidence

- [Complete audit, per-job results, exact revisions and observation times](verification.json)
- [All 22 decoded job logs and their SHA256SUMS file](job-logs.zip)
- [Archive hash, member hashes and preservation details](log-manifest.json)
- [Fail-before baseline](https://github.com/agamal026/commons-compress/blob/191c9e8ce0fb7c478f64bd8912fc56d9747eca5d/contribution-review/framed-statistics/baseline/README.md)
- [Local fixed tests, unchanged-upstream ownership control and automated source review](https://github.com/agamal026/commons-compress/blob/6505ee953046ebda57a40d91d520214c1cf6b1bd/contribution-review/framed-statistics/local-verification/README.md)

This independent review was automated using ChatGPT (GPT-6 Astra Pro). These are owned-fork validation results; upstream CI, acceptance, human review and financial benefits require separate evidence.
