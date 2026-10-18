# Framed output-counter baseline reproduction

This test-only checkpoint adds regression assertions to unchanged Apache Commons
Compress production source at `611cad06b66842692870c6ae98f50de7ce4f25d5`
(`1.29.0-SNAPSHOT`). It reproduces the framed LZ4/Snappy statistics defect already
reported in [COMPRESS-726 comment 18092163](https://issues.apache.org/jira/browse/COMPRESS-726?focusedCommentId=18092163&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel)
and explicitly excluded from [PR #777](https://github.com/apache/commons-compress/pull/777).

## Executed result

On 2026-10-18, Maven 3.9.11 and Eclipse Temurin 17.0.20.1+1 executed **89 selected
tests: 39 failures, 0 errors, 0 skips**. The expected failures are assertions of
returned output counts; 50 existing selected cases passed. Valid empty frames
report 15 uncompressed bytes for LZ4 and 10 for Snappy before returning output.
The two production files are byte-identical to the pinned upstream base.

The new shared class has five parameterized tests, ten invocations. Existing
LZ4 raw-block and round-trip tests add statistics assertions, including block
dependency and checksum options. Existing mixed Snappy and IWORK fixtures check
their output counts. The shared cases exercise intermediate reads, skips,
concatenation with skippable metadata, empty frames, and a later read failure.

```sh
mvn --batch-mode --no-transfer-progress --errors --show-version \
  -Dtest=FramedCompressorInputStreamStatisticsTest,FramedLZ4CompressorInputStreamTest,FramedLZ4CompressorRoundtripTest,FramedSnappyCompressorInputStreamTest \
  test
```

This command is expected to fail on this checkpoint. The environment needed its
existing network proxy and CA configured for Maven; TLS checks stayed enabled.
The first two setup attempts stopped before compilation and executed no tests.
They are recorded separately from the real regression run.

## Evidence and limits

[verification.json](verification.json) identifies every changed test blob, all
89 case outcomes, raw report hashes, timing, and the actual exit code.
[maven.log](maven.log) preserves the complete executed Maven console, with the
workspace prefix normalized. Runtime settings and unrelated workspace state are
not published.

This checkpoint does not claim a fixed build, upstream submission, dedicated new
Jira ticket, maintainer acceptance, or financial benefit. Production correction
and the required default Maven build follow separately.

Generated-by: ChatGPT (GPT-6 Astra Pro). The implementation and fixture review
are automated; no personal human review or legal signature is asserted.
