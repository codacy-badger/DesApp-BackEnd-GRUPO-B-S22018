package model;

public class Pass {
	String nombre;
	
	public Pass(String nombre) {
		this.nombre = nombre;
	}

	public boolean esValido() {
		return nombre.length() >= 4 && nombre.length() <= 20 && true; // hay que verificar que contenga nros y Mayusculas (no sé como hacerlo) 
	}

}
