package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;

public class ArtsmiaDAO {

	/*
	 *  Questo metodo non fa uso dell idMap, perciò bisogna un po' modificarlo.
	 *  Per vedere la versione originale controllare su GitHub tdp 2020 il progetto master.
	 */
	
	public void listObjects(Map<Integer, ArtObject> idMap) {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getInt("object_id"))) {
				
					ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
							res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
							res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
							res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
					
					idMap.put(artObj.getId(), artObj);
				}
				
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getPeso(ArtObject ao1, ArtObject ao2) {
		
		String sql = "select count(*) as peso " + 
				"from exhibition_objects as eo1, exhibition_objects as eo2" + 
				"where eo1.exhibition_id = eo2.exhibition_id and" + 
				"eo1.object_id = ? and eo2.object_id = ?";
		
		Connection conn = DBConnect.getConnection();
		
		try {
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, ao1.getId());
			st.setInt(2, ao2.getId());
			
			ResultSet rs = st.executeQuery();
			
			if(rs.next()) {
				int peso = rs.getInt("peso");
				conn.close();
				return peso;
			}
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public List<Adiacenza> getAdiacenze() {
		
		List<Adiacenza> adiacenze = new ArrayList<>();
		
		String sql = "select eo1.object_id as obj1, eo2.object_id as obj2, count(*) as peso " + 
				"from exhibition_objects as eo1, exhibition_objects as eo2 " + 
				"where eo1.exhibition_id = eo2.exhibition_id and " + 
				"			eo2.object_id > eo1.object_id " + 
				"group by eo1.object_id, eo2.object_id ";
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				adiacenze.add(new Adiacenza(rs.getInt("obj1"), rs.getInt("obj2"), rs.getInt("peso")));
			}
			
			conn.close();
			return adiacenze;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
