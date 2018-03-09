package cs980.entity;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.StreamSupport;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.Data.PageMetadata;
import edu.unh.cs.treccar_v2.read_data.CborFileTypeException;
import edu.unh.cs.treccar_v2.read_data.CborRuntimeException;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class DictionaryBuilder {
	
	public void buildNameDict(String pageFilePath, String dictStoreFilePath){
		try {
			Iterable<Data.Page> pageIterator = DeserializeData.iterableAnnotations(
					new BufferedInputStream(new FileInputStream(new File(pageFilePath))));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dictStoreFilePath)));
			//HashMap<String, String> nameDictionary = new HashMap<String, String>();
			StreamSupport.stream(pageIterator.spliterator(), true).forEach(page -> { 
				/*
				PageMetadata pmd = page.getPageMetadata(); 
				ArrayList<String> anchorTexts = pmd.getInlinkAnchors();
				ArrayList<String> inLinks = pmd.getInlinkIds();
				*/
				for(Data.Page.SectionPathParagraphs spp:page.flatSectionPathsParagraphs()){
					Data.Paragraph p = spp.getParagraph();
					String anchor, link;
					for(Data.ParaBody pbd:p.getBodies()){
						if(pbd instanceof Data.ParaLink){
							anchor = ((Data.ParaLink)pbd).getAnchorText().toLowerCase().replaceAll(" ", "_");
							//if(anchor.length()>0 && !nameDictionary.containsKey(anchor)){
							if(anchor.length()>0){
								link = ((Data.ParaLink)pbd).getPageId();
								//nameDictionary.put(anchor, link);
								try {
									bw.write(anchor+" "+link+"\n");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							//System.out.println(anchor+" -> "+link);
						}
					}
				}
			} );
			/*
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(dictStoreFilePath)));
			oos.writeObject(nameDictionary);
			oos.close();
			*/
			bw.close();
			System.out.println("done");
		} catch (CborRuntimeException | CborFileTypeException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(new File("project.properties")));
			(new DictionaryBuilder()).buildNameDict(p.getProperty("page-file"), p.getProperty("dict-file"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
