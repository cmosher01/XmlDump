package nu.mine.mosher.xml;

import org.apache.commons.csv.CSVPrinter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

import java.io.IOException;

public class Dumper implements ContentHandler {
    private final CSVPrinter out;

    public Dumper(final CSVPrinter out) {
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
    public void setDocumentLocator(Locator locator) {
        w("LOC", locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
    }

    @Override
    public void startDocument() {
        w("DOCUMENT", "START");
    }

    @Override
    public void endDocument() {
        w("DOCUMENT", "END");
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
        w("PREFIX", "START", prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) {
        w("PREFIX", "END", prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        w("ELEMENT", "START", ns(uri), localName, qName);
        dumpAttributes(atts);
    }

    private String ns(String uri) {
        return uri;
    }

    private void dumpAttributes(final Attributes atts) {
        for (int i = 0; i < atts.getLength(); ++i) {
            w("ATTRIBUTE", atts.getType(i), atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getValue(i));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        w("ELEMENT", "END", ns(uri), localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        w("CHARS", length, filter(new String(ch, start, length)));
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
        w("WHITE", length, filter(new String(ch, start, length)));
    }

    private String filter(String s) {
        if (s.trim().isEmpty()) {
            return "WHITESPACE";
        }
        return s;
    }

    @Override
    public void processingInstruction(String target, String data) {
        w("PROC", target, data);
    }

    @Override
    public void skippedEntity(String name) {
        w("SKIP", name);
    }
}
