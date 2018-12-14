package com.dataexchange.client.ftp;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.isNullOrEmpty;
import static org.junit.Assert.fail;

@ActiveProfiles("download-ftp-modifiedDateAfterMinutes")
public class FtpDownloadModifiedDateAfterIntegrationTest extends FtpDownloadTestAbstract {

    @Test
    public void whenRemoteFileWithoutSemaphore_shouldNOTBeDownloadedAndRemoteFileWillStayThere() throws InterruptedException {
        int i = 0;
        while (i++ < 20) {
            if (!isNullOrEmpty(new File(outputFolder).listFiles())) {
                fail("Should not reach this point. The file must not be downloaded");
            }
            Thread.sleep(1000);
        }
        assertThat(new File(realRemoteFolder).listFiles()).hasSize(1);
        assertThat(outputFile).doesNotExist();
    }

    @Test(timeout = 20000)
    public void whenRemoteFileWithSemaphore_shouldBeDownloadedAndRemoteFileWillStayThere() throws InterruptedException {
        remoteSourceFile.setLastModified(LocalDateTime.now().minusMinutes(5).atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());

        if (waitForFilesInFolder(outputFolder)) {
            assertThat(new File(outputFolder).listFiles()).hasSize(1);
            LOGGER.info(new File(outputFolder).listFiles()[0].toString());
            assertThat(outputFile).exists();

            assertThat(new File(realRemoteFolder).listFiles()).isEmpty();
        }
    }
}
