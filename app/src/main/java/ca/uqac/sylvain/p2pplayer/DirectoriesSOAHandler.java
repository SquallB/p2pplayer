package ca.uqac.sylvain.p2pplayer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class DirectoriesSOAHandler extends DefaultHandler {
    private List<CustomFile> files;
    private int depth;
    private String currentNodeText;
    private boolean directory;

    private final String DIRECTORIES = "directories";
    private final String FILES = "file";
    private final String STRING = "xsd:string";

    public DirectoriesSOAHandler(int depth) {
        this.files = new ArrayList<>();
        this.directory = true;
        this.depth = depth;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if (localName.equalsIgnoreCase(DIRECTORIES)) {
            directory = true;
        }
        else if(localName.equalsIgnoreCase(FILES)) {
            directory = false;
        }
    }
    @Override
    public void characters(char[] ch, int start, int length)  throws SAXException {
        // Retrieve the text content of the current node
        // that is being processed
        currentNodeText = new String(ch, start, length);
    }
    @Override
    public void endElement(String uri, String localName, String qName)  throws SAXException {
        if(localName.equalsIgnoreCase(STRING)) {
            files.add(new CustomFile(currentNodeText, depth, directory));
        }
    }

    public List<CustomFile> getFiles() {
        return files;
    }
}