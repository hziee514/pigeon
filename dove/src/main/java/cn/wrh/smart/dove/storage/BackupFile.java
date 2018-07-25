package cn.wrh.smart.dove.storage;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.stream.Stream;

import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;

/**
 *  Backup file format
 *  First line:
 *   flag, timestamp, versionName
 *   DOVE:yyyy-MM-dd HH:mm:ss|2.0.3
 *  Cage line:
 *   A:SN|STATUS|CREATED_AT
 *  Egg line:
 *   B:CAGE_SN|COUNT|LAYING_AT|REVIEW_AT|HATCH_AT|SOLD_AT
 *
 * @author bruce.wu
 * @date 2018/7/20
 */
public class BackupFile {

    public static Reader read(ContentResolver resolver, Uri uri) throws Exception {
        return new Reader(resolver, uri);
    }

    public static Writer write(ContentResolver resolver, Uri uri) throws Exception {
        return new Writer(resolver, uri);
    }

    public static Flowable<Object> flowable(ContentResolver resolver, Uri uri) {
        return Flowable.create((FlowableEmitter<Object> emitter) -> {
            try (Reader reader = read(resolver, uri)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    emitter.onNext(Converter.parseLine(line));
                }
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.BUFFER);
    }

    public static class Meta {

        private final String timestamp;
        private final String version;

        public Meta(String timestamp, String version) {
            this.timestamp = timestamp;
            this.version = version;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "Meta{" +
                    "timestamp='" + timestamp + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }
    }

    public static class Writer implements Closeable {

        private PrintWriter out;

        Writer(ContentResolver resolver, Uri uri) throws Exception {
            OutputStream os = resolver.openOutputStream(uri);
            if (os == null) {
                throw new FileFormatException("Can not open file to write");
            }
            this.out = new PrintWriter(new OutputStreamWriter(os));
        }

        public void write(Meta meta) {

        }

        public void write(CageEntity entity) {

        }

        public void write(String cageSn, EggEntity entity) {

        }

        @Override
        public void close() throws IOException {
            if (out != null) {
                out.close();
            }
        }

    }

    public static class Reader implements Closeable {

        private final BufferedReader in;
        private final Meta meta;

        Reader(ContentResolver resolver, Uri uri) throws Exception {
            InputStream is = resolver.openInputStream(uri);
            if (is == null) {
                throw new FileFormatException("Can not open file to read");
            }
            in = new BufferedReader(new InputStreamReader(is));
            this.meta = Converter.parseHeader(in.readLine());
        }

        public Stream<String> lines() {
            return in.lines();
        }

        public String readLine() throws IOException {
            return in.readLine();
        }

        public Meta getMeta() {
            return meta;
        }

        @Override
        public void close() throws IOException {
            if (in != null) {
                in.close();
            }
        }
    }

}
