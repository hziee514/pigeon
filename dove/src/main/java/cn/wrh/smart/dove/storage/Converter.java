package cn.wrh.smart.dove.storage;

import java.util.Date;

import cn.wrh.smart.dove.dal.converter.CageStatusConverter;
import cn.wrh.smart.dove.dal.converter.DateConverter;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.util.Tuple;

/**
 * @author bruce.wu
 * @date 2018/7/23
 */
class Converter {

    private static final String TYPE_FLAG = "DOVE:";
    private static final String PREFIX_CAGE = "A:";
    private static final String PREFIX_EGG = "B:";
    private static final String FIELD_SPLITTER = "|";

    static BackupFile.Meta parseHeader(String text) throws FileFormatException {
        if (text == null) {
            throw new FileFormatException("Empty header text");
        }
        if (!text.startsWith(TYPE_FLAG)) {
            throw new FileFormatException("Unexpected file flag");
        }
        String[] fields = text.substring(TYPE_FLAG.length())
                .split(fieldSplitPattern());
        if (fields.length < 2) {
            throw new FileFormatException("Unexpected header fields: " + text);
        }
        return new BackupFile.Meta(fields[0], fields[1]);
    }

    public static Object parseLine(String text) throws FileFormatException {
        if (isCage(text)) {
            return toCage(text);
        }
        if (isEgg(text)) {
            return toEgg(text);
        }
        throw new FileFormatException("Unexpected line: " + text);
    }

    private static CageEntity toCage(String text) throws FileFormatException {
        String[] fields = text.substring(PREFIX_CAGE.length())
                .split(fieldSplitPattern());
        if (fields.length < 3) {
            throw new FileFormatException("Unexpected cage fields: " + text);
        }
        CageEntity entity = new CageEntity();
        entity.setSerialNumber(fields[0]);
        entity.setStatus(CageStatusConverter.toStatus(fields[1]));
        entity.setCreatedAt(DateConverter.toDate(fields[2]));
        return entity;
    }

    private static Tuple<String, EggEntity> toEgg(String text) throws FileFormatException {
        String[] fields = text.substring(2).split("[" + FIELD_SPLITTER + "]");
        EggEntity entity = new EggEntity();
        if (fields.length < 3) {
            throw new FileFormatException("Unexpected egg fields: " + text);
        }
        String cageSn = fields[0];
        entity.setCount(toCount(fields[1]));
        entity.setLayingAt(DateConverter.toDate(fields[2]));
        if (fields.length > 3) {
            entity.setReviewAt(DateConverter.toDate(fields[3]));
        }
        if (fields.length > 4) {
            entity.setHatchAt(DateConverter.toDate(fields[4]));
        }
        if (fields.length > 5) {
            entity.setSoldAt(DateConverter.toDate(fields[5]));
        }
        entity.determineStage();
        return new Tuple<>(cageSn, entity);
    }

    private static String toTimestamp(Date date) {
        String ts = DateConverter.toTimestamp(date);
        return ts == null ? "" : ts;
    }

    private static Integer toCount(String text) {
        return Integer.valueOf(text);
    }

    private static boolean isCage(String text) {
        return PREFIX_CAGE.indexOf(text) == 0;
    }

    private static boolean isEgg(String text) {
        return text.startsWith(PREFIX_EGG);
    }

    private static String fieldSplitPattern() {
        return pattern(FIELD_SPLITTER);
    }

    private static String pattern(String re) {
        return "[" + re + "]";
    }

}
