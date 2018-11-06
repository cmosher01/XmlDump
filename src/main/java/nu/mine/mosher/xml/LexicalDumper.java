package nu.mine.mosher.xml;

import org.apache.commons.csv.CSVPrinter;
import org.xml.sax.ext.LexicalHandler;

import java.io.IOException;

public class LexicalDumper implements LexicalHandler {
    private final CSVPrinter out;

    public LexicalDumper(final CSVPrinter out) {
        this.out = out;
    }

    private void w(final Object... o) {
        try {
            out.printRecord(o);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) {
        w("DTD", "START", name, publicId, systemId);
    }

    @Override
    public void endDTD() {
        w("DTD", "END");
    }

    @Override
    public void startEntity(String name) {
        w("ENTITY", "START", name);
    }

    @Override
    public void endEntity(String name) {
        w("ENTITY", "END", name);
    }

    @Override
    public void startCDATA() {
        w("CDATA", "START");
    }

    @Override
    public void endCDATA() {
        w("CDATA", "END");
    }

    @Override
    public void comment(char[] ch, int start, int length) {
        w("COMMENT", length, new String(ch, start, length));
    }
}
