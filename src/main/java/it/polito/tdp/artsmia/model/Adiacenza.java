package it.polito.tdp.artsmia.model;

public class Adiacenza {
	
	public Integer obj1;
	public Integer obj2;
	public Integer peso;
	/**
	 * @param obj1
	 * @param obj2
	 * @param peso
	 */
	public Adiacenza(Integer obj1, Integer obj2, Integer peso) {
		super();
		this.obj1 = obj1;
		this.obj2 = obj2;
		this.peso = peso;
	}
	public Integer getObj1() {
		return obj1;
	}
	public void setObj1(Integer obj1) {
		this.obj1 = obj1;
	}
	public Integer getObj2() {
		return obj2;
	}
	public void setObj2(Integer obj2) {
		this.obj2 = obj2;
	}
	public Integer getPeso() {
		return peso;
	}
	public void setPeso(Integer peso) {
		this.peso = peso;
	}
	
	
	
}
