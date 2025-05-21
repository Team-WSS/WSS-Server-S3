package org.websoso.s3.core.strategy;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.io.TikaInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 정밀하게 MIME 타입을 감지하는 전략 구현체입니다.
 * <p>
 * 파일의 전체 내용을 분석하여 정확한 MIME 타입을 반환하며, Tika의 AutoDetectParser를 사용합니다.
 * </p>
 */
public class PreciseMimeTypeDetectionStrategy implements MimeTypeDetectionStrategy {

    private static final AutoDetectParser parser = new AutoDetectParser();

    @Override
    public String detect(InputStream inputStream) throws IOException {
        try (TikaInputStream tikaInputStream = TikaInputStream.get(inputStream)) {
            Metadata metadata = new Metadata();
            parser.parse(tikaInputStream, new BodyContentHandler(), metadata, new ParseContext());
            return metadata.get(Metadata.CONTENT_TYPE);
        } catch (Exception e) {
            throw new IOException("Precise MIME detection failed", e);
        }
    }

    @Override
    public String detect(File file) throws IOException {
        try (TikaInputStream tikaInputStream = TikaInputStream.get(file)) {
            Metadata metadata = new Metadata();
            parser.parse(tikaInputStream, new BodyContentHandler(), metadata, new ParseContext());
            return metadata.get(Metadata.CONTENT_TYPE);
        } catch (Exception e) {
            throw new IOException("Precise MIME detection failed for file: " + file.getName(), e);
        }
    }
}