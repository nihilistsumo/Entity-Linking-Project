package cs980.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import co.nstant.in.cbor.CborException;

/**
 *
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	try {
    		Properties p = new Properties();
			p.load(new InputStreamReader(new FileInputStream(new File("project.properties"))));
			//(new DictionaryBuilder()).buildNameDict(p.getProperty("page-file"), p.getProperty("dict-file"));
			(new BasicEntityLinker()).rankEntities(p.getProperty("para-file"), p.getProperty("dict-file"), p.getProperty("entity-run"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
