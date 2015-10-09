package application.util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;


public class FileCharsetDecoder {

    public static Charset detectCharset(File f) {

		// UTF-8 should go first!
    	String[] charsetsToBeTested = {"UTF-8", System.getProperty("file.encoding"), "ISO-8859-1", "cp1252"};
    	
        Charset charset = null;

        for (String charsetName : charsetsToBeTested) {
            charset = detectCharset(f, Charset.forName(charsetName));
            
            if (charset != null) {
                break;
            }
        }

        return charset;
    }

	private static Charset detectCharset(File f, Charset charset) {
        try {
        	
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(f));

            CharsetDecoder decoder = charset.newDecoder();
            decoder.reset();

            byte[] buffer = new byte[(int)f.length()];
            boolean identified = false;
            while ((input.read(buffer) != -1) && (!identified)) {
                identified = identify(buffer, decoder);
            }

            input.close();

            if (identified) {
                return charset;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            return null;
        }
    }
	
    private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
        try {
        	CharBuffer cb = decoder.decode(ByteBuffer.wrap(bytes));
        }
        catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }	
	
}
