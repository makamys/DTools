package makamys.dtools.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/** Why shade a dependency when you can implement it yourself:tm: */
public class CSVWriter implements Closeable {
    
    private char delimiter = ',';
    private char quote = '\"';
    
    private final Writer writer;
    private boolean hasWrittenRow = false;
    
    public CSVWriter(Writer writer) {
        this.writer = writer;
    }
    
    public CSVWriter withDelimiter(char delimiter) {
        if(hasWrittenRow) {
            throw new IllegalStateException("Cannot change delimiter character after a row has been written");
        }
        this.delimiter = delimiter;
        return this;
    }
    
    public CSVWriter withQuote(char quote) {
        if(hasWrittenRow) {
            throw new IllegalStateException("Cannot change quote character after a row has been written");
        }
        this.quote = quote;
        return this;
    }
    
    public void writeRow(String... columns) throws IOException {
        writer.write(String.join("" + delimiter, Arrays.stream(columns).map(this::escapeCSVString).toArray(String[]::new)) + "\n");
        hasWrittenRow = true;
    }
    
    private String escapeCSVString(String str) {
        if(str.contains("" + delimiter) || str.contains("" + quote)) {
            return quote + str.replace("" + quote, "" + quote + quote) + quote;
        } else {
            return str;
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
    
}
