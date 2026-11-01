# COMPRESS-729: November 1 integration validation

This supplement documents the existing [contributor-fork validation PR #2](https://github.com/agamal026/commons-compress/pull/2), not an upstream submission or acceptance. It preserves fresh checks of the original correction combined with the November 1 upstream dependency changes. The [October 31 proof](https://github.com/agamal026/commons-compress/blob/46b6e4fe3ff023d54595c110bdf0aaecd4b06da7/contribution-review/COMPRESS-729/close-deflater-20261031/README.md) remains unchanged with its original dates, baseline and limits.

## Exact integration

| Identity | Revision |
| --- | --- |
| Original correction | `f0ad0a114f71b3708806873db969966733f0f20f` |
| Original upstream base | `12b02d906b7945bccdcbdd2cf7b4523accd542da` |
| New frozen upstream base | `69a29b75922f7e525412dc6b7454b70d1f2be2bd` |
| Integrated contribution | `940471cf4900817c59160a10fddc1356fd04ce96` |
| Source and actual tested tree | `455af7fbc64febc97892e3b26e0cfd38b6c5f3f9` |
| Actual CI checkout | `ca02e025e17bbafca302485c2454de2d0a2b3c7c` |

The source commit has parents original correction and new base. The actual CI merge has parents new base and integrated contribution; its tree was verified through the canonical Git commit and its checkout was observed in the three inspected Java logs and Dependency Review log. Source branch: `fix/COMPRESS-729-close-deflater-20261031`. Frozen verification base branch: `verify/COMPRESS-729-base-20261101`.

The two intervening upstream commits change zstd-jni from 1.5.7-24 to 1.5.7-25 and test-scoped memoryfilesystem from 2.8.2 to 2.8.3, plus their changelog entries. Complete upstream diffs and both integration comparisons were inspected. The integration does not change the correction's ZIP runtime or regression-test bytes. Relative to the new base, the contribution is still three files, 141 additions and two deletions. The branch moved non-forced with the expected previous SHA. No workflow, test selector, coverage threshold or dependency exclusion was changed.

## Completed matrix and inspection scope

[Java CI run 41323689495](https://github.com/agamal026/commons-compress/actions/runs/41323689495) completed successfully. Canonical latest-attempt job metadata confirms **all 19 actual jobs and their Maven build steps succeeded**: 18 ordinary Linux/Windows/macOS jobs on Java 8/11/17/21/25/27, plus the experimental Linux Java 28-ea job. No tolerated failure is being hidden behind the green workflow.

The complete decoded logs for **three selected jobs**, one per platform, were inspected. Exact test counts and per-gate execution below are claims about those logs, not an extrapolation to the other sixteen. All nineteen IDs and terminal results are retained in `summary.json`.

All three inspected builds execute the unchanged default command, with `MAVEN_ARGS` containing only `-ntp`:

```sh
mvn --errors --show-version --batch-mode --no-transfer-progress
```

| Inspected environment | Reported | Passed | Failures | Errors | Skipped | Changed ZIP class |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| Ubuntu 26.04.1 x64, Temurin 17.0.21+9, Maven 3.9.15 | 4,788 | 4,762 | 0 | 0 | 26 | 11/11 passed |
| Windows Server 2025 x64, Temurin 8.0.504+1, Maven 3.9.16 | 4,788 | 4,758 | 0 | 0 | 30 | 11/11 passed |
| macOS 26.6.2 aarch64, Temurin 25.0.5+8.0.LTS, Maven 3.10.0 | 4,788 | 4,760 | 0 | 0 | 28 | 11/11 passed |

Actual test-summary and build-success timestamps, in UTC on November 1:

| Job | Test summary | BUILD SUCCESS |
| --- | --- | --- |
| [Linux Java 17: 124899068921](https://github.com/agamal026/commons-compress/actions/runs/41323689495/job/124899068921) | 15:58:38.3809083 | 15:59:33.9982792 |
| [Windows Java 8: 124899068861](https://github.com/agamal026/commons-compress/actions/runs/41323689495/job/124899068861) | 15:59:28.0048841 | 16:00:33.2811921 |
| [macOS Java 25: 124899068936](https://github.com/agamal026/commons-compress/actions/runs/41323689495/job/124899068936) | 15:58:50.4407190 | 15:59:53.1821180 |

In the inspected Windows and macOS logs, the dependency-sensitive ZIP Zstd class passes 5/5, ZIP memory-filesystem class 15/15 and TAR memory-filesystem class 3/3. Zstd round trips report 1,223 cases with zero failures/errors and one skip. These are additional observed checks, not a claim to test every behavior of either dependency.

## Quality gates and limits

RAT, Checkstyle, PMD and japicmp complete in all three inspected logs; Checkstyle reports zero violations. Japicmp report generation and build success are observed, but its individual generated reports were not separately downloaded or audited. This is not an assertion that the entire snapshot has no source-incompatible changes relative to the released comparison version.

JaCoCo actually enforces its configured thresholds on the inspected Linux Java 17 and Windows Java 8 builds, each analyzing 523 classes. The inspected macOS Java 25 build **skips coverage checking because execution data is missing**. Do not describe it as coverage enforcement. Javadoc executes on inspected Java 17 and Java 25; the existing Java 8 profile omits it. Animal Sniffer's Java 8 API check executes in the Windows Java 8 build.

The SPDX forked lifecycle reports that Surefire has already run for the same configuration. That skips a duplicate execution after the genuine full test run; tests are counted once. Compiler notes, PMD rule/classpath warnings, CycloneDX schema warnings, SPDX NOASSERTION, metadata certificate warnings and cache races remain visible in the underlying logs. No warning-free claim.

No new local test or failing-baseline run was performed in this integration session. The original failing-baseline evidence is reused because the patch's runtime and tests are unchanged. Optional large-file integration profiles, SpotBugs, Failsafe and CodeQL are not claimed. Complete logs for the other sixteen Java jobs were not inspected, so their exact counts, Javadoc and coverage execution remain outside this detailed audit.

## Dependency Review

[Run 41323689543, actual job 124899068675](https://github.com/agamal026/commons-compress/actions/runs/41323689543/job/124899068675) and its checkout/review steps completed successfully. Its full decoded log identifies the same checkout and reports no flagged vulnerable or denied packages. The Dependency Changes section is empty: both dependency updates are already in the PR's new base, and this contribution has no dependency delta relative to that base. This check is therefore **not** a separate security certification of the newly selected dependencies or the whole dependency graph.

## Inspectable observations and limits

[summary.json](summary.json) records the exact source identities, all nineteen actual job IDs and their terminal build results, the three audited test totals and timestamps, and the inspection limits. These are transcribed selected observations, not raw API responses or archived complete logs. The complete decoded logs were read through GitHub; their future retention is controlled by GitHub. Canonical job links above remain available for inspection. No complete-log archive is claimed in this supplement.

## Review and delivery status

Serial automated integration review verified the restricted upstream diff, unchanged runtime/tests, source-tree identity, exact commands and the distinction between successful job status and deeply inspected test evidence. No native independent subagents were available; no new independent or human approval is asserted. The original automated source reviews retain their original scope. The integration commit and evidence commit include Assisted-By provenance for OpenAI ChatGPT (GPT-6 Astra Pro).

Applied primary-source lesson: [GitHub documents](https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows#pull_request) that ordinary PR checks use the merge result, and the default PR trigger types include opened, synchronize and reopened. Retargeting the frozen base was followed by a real integration commit, and the actual resulting checkout was verified rather than treating the earlier matrix as validation of new dependency bytes.

The external PR-creation authorization failure is unchanged. This supplemental proof does not retry that action or establish upstream submission, acceptance, a legal signature, reward eligibility or payment. The draft remains preserved for an authorized future delivery, with a fresh base/policy/competition check required at that time. Do not rerun this unchanged successful matrix merely to recover its result.
