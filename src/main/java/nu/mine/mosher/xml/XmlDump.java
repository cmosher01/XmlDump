package nu.mine.mosher.xml;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class XmlDump {
    private static BufferedInputStream streamFromFileName(final String fileName) throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(new File(fileName)));
    }

    private static BufferedOutputStream streamOut() {
        return new BufferedOutputStream(new FileOutputStream(FileDescriptor.out));
    }

    private static SAXParserFactory createSaxParserFactory() throws SAXException, ParserConfigurationException {
        final SAXParserFactory f = SAXParserFactory.newInstance();
        f.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", true);
        f.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
        f.setFeature("http://apache.org/xml/features/standard-uri-conformant", true);
        f.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        f.setFeature("http://xml.org/sax/features/xmlns-uris", true);
        f.setFeature("http://xml.org/sax/features/validation", true);
        f.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
        f.setNamespaceAware(true);
        f.setValidating(true);
        return f;
    }

    private static SAXParser createSaxParser(final CSVPrinter out) throws SAXException, ParserConfigurationException {
        final SAXParser p = createSaxParserFactory().newSAXParser();
        p.setProperty("http://xml.org/sax/properties/lexical-handler",  new LexicalDumper(out));
        return p;
    }

    private static XMLReader createSaxReader(final CSVPrinter out) throws SAXException, ParserConfigurationException {
        return createSaxParser(out).getXMLReader();
    }

    private static ErrorHandler errorPrinter(final CSVPrinter writer) {
        return new ErrorHandler() {
            private void w(final Object... o) throws SAXException {
                try {
                    writer.printRecord(o);
                } catch (IOException e) {
                    throw new SAXException(e);
                }
            }

            @Override
            public void warning(final SAXParseException exception) throws SAXException {
                w("WARN", exception.getMessage());
            }

            @Override
            public void error(final SAXParseException exception) throws SAXException {
                w("ERROR", exception.getMessage());
            }

            @Override
            public void fatalError(final SAXParseException exception) throws SAXException {
                w("FATAL", exception.getMessage());
            }
        };
    }

    private static void dump(final BufferedInputStream in, final BufferedOutputStream out) throws SAXException, ParserConfigurationException, IOException {
        try (final CSVPrinter writer = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8), CSVFormat.RFC4180)) {
            final XMLReader saxReader = createSaxReader(writer);
            saxReader.setContentHandler(new Dumper(writer));
            saxReader.setErrorHandler(errorPrinter(writer));
            saxReader.parse(new InputSource(in));
            writer.flush();
        }
    }

    public static void main(final String... args) throws SAXException, ParserConfigurationException, IOException {
        if (args.length < 1) {
            System.out.println("usage: xml-dump input.xml");
            System.out.flush();
            return;
        }

        dump(streamFromFileName(args[0]), streamOut());
    }
}
