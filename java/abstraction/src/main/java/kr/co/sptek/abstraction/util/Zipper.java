package kr.co.sptek.abstraction.util;

import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.File;
import java.io.IOException;

public class Zipper {

    private Zipper() {
    }

    public static void compress(String destination, String source, ArchiveFormat format, CompressionType compression) throws IOException {
        File dst = new File(destination);
        Archiver archiver = ArchiverFactory.createArchiver(format, compression);
        archiver.create( dst.getName(), new File(dst.getParent()), new File(source));
    }

    public static void compress(String destination, String source, ArchiveFormat format) throws IOException {
        File dst = new File(destination);
        Archiver archiver = ArchiverFactory.createArchiver(format);
        archiver.create( dst.getName(), new File(dst.getParent()), new File(source));
    }

    public static void decompress(String archive, String destination, ArchiveFormat format, CompressionType compression) throws IOException {
        Archiver archiver = ArchiverFactory.createArchiver(format, compression);
        archiver.extract(new File(archive), new File(destination));
    }

    public static void decompress(String archive, String destination, ArchiveFormat format) throws IOException {
        Archiver archiver = ArchiverFactory.createArchiver(format);
        archiver.extract(new File(archive), new File(destination));
    }
}
