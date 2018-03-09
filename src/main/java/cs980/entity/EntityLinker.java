package cs980.entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import co.nstant.in.cbor.model.Array;
import dev.CS980Assignment1;
import edu.unh.cs.treccar_v2.Data;

public class EntityLinker extends CS980Assignment1{

	public EntityLinker(String ind, String para, String out, String run,
			String retN, String mode, String i, String head) {
		super(ind, para, out, run, retN, mode, i, head);
		// TODO Auto-generated constructor stub
	}
	
	public void indexPara(IndexWriter iw, Data.Paragraph para) throws IOException {
		Document paradoc = new Document();
		paradoc.add(new StringField("paraid", para.getParaId(), Field.Store.YES));
		paradoc.add(new TextField("parabody", para.getTextOnly(), Field.Store.YES));
		String entityList = "";
		for(String entity:para.getEntitiesOnly()){
			if(entityList.length()<1)
				entityList = entity;
			else
				entityList = entityList+"@"+entity;
		}
		paradoc.add(new StringField("paraenty", entityList, Field.Store.YES));
		iw.addDocument(paradoc);
	}
	
	public void writeEntityQrels(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("lead-paragraph-ids.txt")));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("lead-paragraphs-entity.qrels")));
			IndexSearcher is = new IndexSearcher(DirectoryReader.open(FSDirectory.open((new File(this.getIndexDir()).toPath()))));
			Analyzer analyzer = new StandardAnalyzer();
			Document retParaDoc;
			QueryParser qp = new QueryParser("paraid", analyzer);
			String line = br.readLine();
			ArrayList<String> paraIDs = new ArrayList<String>();
			while(line!=null){
				paraIDs.add(line);
				line = br.readLine();
			}
			br.close();
			for(String para:paraIDs){
				if(is.search(qp.parse(para), 1).scoreDocs.length == 0)
					continue;
				retParaDoc = is.doc(is.search(qp.parse(para), 1).scoreDocs[0].doc);
				//ArrayList<String> entitiesInPara = new ArrayList<String>();
				String entityField = retParaDoc.get("paraenty");
				if(entityField.isEmpty())
					continue;
				for(String ent:entityField.split("@")){
					bw.write(para+" 0 "+ent.replaceAll(" ", "%20")+" 1\n");
					//entitiesInPara.add(ent);
				}
			}
			bw.close();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void searchEntityPages(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("lead-paragraph-ids.txt")));
			IndexSearcher is = new IndexSearcher(DirectoryReader.open(FSDirectory.open((new File(this.getIndexDir()).toPath()))));
			Analyzer analyzer = new StandardAnalyzer();
			Document retParaDoc;
			QueryParser qp = new QueryParser("paraid", analyzer);
			String line = br.readLine();
			ArrayList<String> paraIDs = new ArrayList<String>();
			while(line!=null){
				paraIDs.add(line);
				line = br.readLine();
			}
			br.close();
			for(String paraID:paraIDs){
				if(is.search(qp.parse(paraID), 1).scoreDocs.length == 0)
					continue;
				retParaDoc = is.doc(is.search(qp.parse(paraID), 1).scoreDocs[0].doc);
				//ArrayList<String> entitiesInPara = new ArrayList<String>();
				String paraText = retParaDoc.get("parabody");
				if(paraText.isEmpty())
					continue;
				//SpotlightFactory sf = new SpotlightFactory(new org.dbpedia.spotlight.model.SpotlightConfiguration("new"));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
