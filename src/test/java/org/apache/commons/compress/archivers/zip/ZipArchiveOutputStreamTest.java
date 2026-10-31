/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress.archivers.zip;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.AbstractTempDirTest;
import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link ZipArchiveOutputStream}.
 */
class ZipArchiveOutputStreamTest extends AbstractTempDirTest {

    private static final class TestOutputStream extends OutputStream {

        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private IOException writeException;
        private IOException closeException;
        private int closeCount;

        @Override
        public void close() throws IOException {
            closeCount++;
            if (closeException != null) {
                throw closeException;
            }
        }

        @Override
        public void write(final int value) throws IOException {
            if (writeException != null) {
                throw writeException;
            }
            bytes.write(value);
        }
    }

    private static void assertDeflaterClosed(final ZipArchiveOutputStream outputStream) {
        try {
            outputStream.def.getBytesRead();
        } catch (final NullPointerException | IllegalStateException expected) {
            // OpenJDK versions report released native state with either NPE or IllegalStateException.
            return;
        }
        fail("Deflater native state has not been released");
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testCloseAfterFinishFailureReleasesDeflater(final boolean failOnClose) throws IOException {
        final TestOutputStream target = new TestOutputStream();
        final ZipArchiveOutputStream outputStream = new ZipArchiveOutputStream(target);
        try {
            outputStream.putArchiveEntry(new ZipArchiveEntry("entry"));
            outputStream.write('a');
            outputStream.closeArchiveEntry();
            assertDoesNotThrow(outputStream.def::getBytesRead);
            target.writeException = new IOException("write failed");
            target.closeException = failOnClose ? new IOException("close failed") : null;
            final IOException expected = failOnClose ? target.closeException : target.writeException;
            final IOException actual = assertThrows(IOException.class, outputStream::close);
            assertSame(expected, actual);
            assertEquals(0, actual.getSuppressed().length);
            assertEquals(1, target.closeCount);
            assertTrue(outputStream.isClosed());
            assertDeflaterClosed(outputStream);
        } finally {
            outputStream.def.end();
        }
    }

    @Test
    void testCloseWithUnclosedEntryReleasesDeflater() throws IOException {
        final TestOutputStream target = new TestOutputStream();
        final ZipArchiveOutputStream outputStream = new ZipArchiveOutputStream(target);
        try {
            outputStream.putArchiveEntry(new ZipArchiveEntry("entry"));
            outputStream.write('a');
            assertDoesNotThrow(outputStream.def::getBytesRead);
            assertEquals("This archive contains unclosed entries.", assertThrows(ArchiveException.class, outputStream::close).getMessage());
            assertEquals(1, target.closeCount);
            assertDeflaterClosed(outputStream);
        } finally {
            outputStream.def.end();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testDestroyReleasesDeflater(final boolean failOnClose) throws IOException {
        final TestOutputStream target = new TestOutputStream();
        final ZipArchiveOutputStream outputStream = new ZipArchiveOutputStream(target);
        try {
            outputStream.putArchiveEntry(new ZipArchiveEntry("entry"));
            outputStream.write('a');
            assertDoesNotThrow(outputStream.def::getBytesRead);
            if (failOnClose) {
                target.closeException = new IOException("close failed");
                assertSame(target.closeException, assertThrows(IOException.class, outputStream::destroy));
            } else {
                outputStream.destroy();
            }
            assertEquals(1, target.closeCount);
            assertDeflaterClosed(outputStream);
        } finally {
            outputStream.def.end();
        }
    }

    @Test
    void testFileBasics() throws IOException {
        final ZipArchiveOutputStream ref;
        try (ZipArchiveOutputStream outputStream = new ZipArchiveOutputStream(createTempFile())) {
            ref = outputStream;
            assertTrue(outputStream.isSeekable());
        }
        assertTrue(ref.isClosed());
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testFinishReleasesDeflaterWithoutClosingOutput(final boolean prematureFinish) throws IOException {
        final TestOutputStream target = new TestOutputStream();
        try (ZipArchiveOutputStream outputStream = new ZipArchiveOutputStream(target)) {
            outputStream.putArchiveEntry(new ZipArchiveEntry("entry"));
            outputStream.write('a');
            if (prematureFinish) {
                assertThrows(ArchiveException.class, outputStream::finish);
                assertDoesNotThrow(outputStream.def::getBytesRead);
            }
            outputStream.closeArchiveEntry();
            outputStream.finish();
            assertDeflaterClosed(outputStream);
            assertEquals(0, target.closeCount);
            outputStream.close();
            outputStream.close();
        }
        assertEquals(1, target.closeCount);
        try (ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(target.bytes.toByteArray()))) {
            assertEquals("entry", inputStream.getNextEntry().getName());
            assertEquals('a', inputStream.read());
            assertEquals(-1, inputStream.read());
            assertNull(inputStream.getNextEntry());
        }
    }

    @Test
    void testOptionDefaults() throws IOException {
        final ZipArchiveOutputStream ref;
        try (ZipArchiveOutputStream outputStream = new ZipArchiveOutputStream(createTempFile())) {
            ref = outputStream;
            assertTrue(outputStream.isSeekable());
            outputStream.setComment("");
            outputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
            outputStream.setMethod(ZipEntry.DEFLATED);
            outputStream.setFallbackToUTF8(false);
        }
        assertTrue(ref.isClosed());
    }

    @Test
    void testOutputStreamBasics() throws IOException {
        try (ZipArchiveOutputStream outputStream = new ZipArchiveOutputStream(new ByteArrayOutputStream())) {
            assertFalse(outputStream.isSeekable());
        }
    }

    @Test
    void testSetEncoding() throws IOException {
        try (ZipArchiveOutputStream outputStream = new ZipArchiveOutputStream(createTempFile())) {
            outputStream.setEncoding(StandardCharsets.UTF_8.name());
            assertEquals(StandardCharsets.UTF_8.name(), outputStream.getEncoding());
            outputStream.setEncoding(null);
            assertEquals(Charset.defaultCharset().name(), outputStream.getEncoding());
        }
    }
}
