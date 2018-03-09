package cs980.entity;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.StreamSupport;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.CborFileTypeException;
import edu.unh.cs.treccar_v2.read_data.CborRuntimeException;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class BasicEntityLinker {
	
	public ArrayList<String> generateCandidateSet(String paraID, String paraText, HashMap<String, String> nameDict){
		ArrayList<String> candidate = new ArrayList<String>();
		String[] tokensInText = paraText.split(" ");
		String token, bigramToken, trigramToken;
		for(String t:tokensInText){
			token = t.toLowerCase();
			if(nameDict.keySet().contains(token))
				candidate.add(nameDict.get(token));
		}
		for(int i=0; i<tokensInText.length-1; i++){
			bigramToken = (tokensInText[i]+" "+tokensInText[i+1]).toLowerCase();
			if(nameDict.keySet().contains(bigramToken))
				candidate.add(nameDict.get(bigramToken));
		}
		for(int i=0; i<tokensInText.length-2; i++){
			trigramToken = (tokensInText[i]+" "+tokensInText[i+1]+" "+tokensInText[i+2]).toLowerCase();
			if(nameDict.keySet().contains(trigramToken))
				candidate.add(nameDict.get(trigramToken));
		}
		return candidate;
	}
	
	public void rankEntities(String paraFilePath, String dictFilePath, String runfilePath){
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(dictFilePath)));
			HashMap<String, String> nameDict = (HashMap<String, String>)ois.readObject();
			ois.close();
			BufferedWriter runBw = new BufferedWriter(new FileWriter(new File(runfilePath)));
			HashMap<String, ArrayList<String>> paraCandidateEntitiesMap = new HashMap<String, ArrayList<String>>();
			Iterable<Data.Paragraph> paraIterator = DeserializeData.iterableParagraphs(
					new BufferedInputStream(new FileInputStream(new File(paraFilePath))));
			StreamSupport.stream(paraIterator.spliterator(), true).forEach(para -> {
				paraCandidateEntitiesMap.put(para.getParaId(), this.generateCandidateSet(para.getParaId(), para.getTextOnly(), nameDict));
			});
			for(String paraid:paraCandidateEntitiesMap.keySet()){
				ArrayList<String> candidateList = paraCandidateEntitiesMap.get(paraid);
				ArrayList<String> alreadySeenEntities = new ArrayList<String>();
				for(String ent:candidateList){
					if(!alreadySeenEntities.contains(ent)){
						alreadySeenEntities.add(ent);
						//System.out.println(paraid+" Q0 "+ent+" 0 "+Collections.frequency(candidateList, ent)+" ENTITY");
						runBw.write(paraid+" Q0 "+ent+" 0 "+Collections.frequency(candidateList, ent)+" ENTITY\n");
					}
				}
			}
			runBw.close();
			
		} catch (CborRuntimeException | CborFileTypeException
				| IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(new File("project.properties")));
			(new BasicEntityLinker()).rankEntities(p.getProperty("para-file"), p.getProperty("dict-file"), p.getProperty("entity-run"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
