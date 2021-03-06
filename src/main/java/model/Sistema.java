package model;

import java.util.ArrayList;
import model.exceptions.NoPuedesRealizarUnaOfertaSobreUnaSubastaDondeFuisteElUltimoPujanteException;
import model.exceptions.NoPuedesTenerMasDeCincoSubastasEnProgresoException;
import model.exceptions.PujaSobreUnaSubastaDeLaQueSeEsOwnerException;

public class Sistema {
	ArrayList<Subasta> subastas = new ArrayList<Subasta>();
	ArrayList<Usuario> usuarios = new ArrayList<Usuario>(); // registrados (Los anonimos solo operan ciertas funciones
															// que no requieren saber quienes son)
	Registro registro = new Registro(); // Se encarga de registro, verificar ingresos, etc
	Home home = new Home(); // Se encarga de aplicar filtros sobre las subastas y mostrarlos ordenados

	///////////////////////////////////////// CRUD
	///////////////////////////////////////// //////////////////////////////////////////////////////////////////

	// mmm, no, demasiado complejo llevarlo a Perfil
	// UN USUARIO REGISTRADO TIENE EL COMP. DE CREAR, BORRAR Y EDITAR
	// CUANDO SE REGISTRA UN OBJ. CAMBIA SU ESTADO A REGISTRADO
	// LUEGO PARA CREAR, EDITAR, BORRAR
	// OBJETO ESTADO --> ASI DESACOPLAMOS COMPORTAMIENTO EN SISTEMA

	public void crear(Subasta subasta, Usuario usuario) {
		if (puedeCrearSubasta(usuario))
			agregar(subasta, usuario);
	}
	
	// En vez de usuario podria usarse a subasta para preg. si se puede ofertar sobre ella
	public void realizarOferta(Subasta subasta, Usuario usuario) {
		if (estaAutenticado(usuario)) {
			if (usuario.puedeOfertar(subasta)) usuario.ofertar(subasta);
				else if (subasta.estaEnProgresoPara(usuario))  
					throw new PujaSobreUnaSubastaDeLaQueSeEsOwnerException();
				else if (subasta.pujoUltimo(usuario))  
					throw new NoPuedesRealizarUnaOfertaSobreUnaSubastaDondeFuisteElUltimoPujanteException();
		}
		else { }
		// Excepciones de no autenticado...No Logueado, usuario inexistente, etc
	}

	private void agregar(Subasta subasta, Usuario usuario) {
		subasta.setEstado(new NuevaSubasta());
		subasta.setPropietario(usuario);
		subastas.add(subasta);
	}

	private Boolean puedeCrearSubasta(Usuario usuario) {
		return estaAutenticado(usuario) && tieneMenosSubastasEnProgresoQueLaCantMaximaPermitida(usuario);
	}
	
	public void modificar(Subasta subasta, Usuario usuario) {
		if (sePuedeEditarSubasta(subasta, usuario)) editar(subasta);
	}
	
	public void eliminar(Subasta subasta, Usuario usuario) {
		if (sePuedeEditarSubasta(subasta, usuario)) eliminar(subasta);
	}
	
	// Las Excepciones van en este metodo, no en los internos
	private Boolean sePuedeEditarSubasta(Subasta subasta, Usuario usuario) {
		return estaAutenticado(usuario) && subasta.sePuedeModificar(usuario);
	}
	
	// mmm, como hacerlo?
	private void editar(Subasta subasta) {
		/// ..................
	}
	
	private void eliminar(Subasta subasta) {
		subastas.remove(subasta);
	}

	///////////////////////////////////////// REGISTRO
	///////////////////////////////////////// ///////////////////////////////////////////////////////////////

	// ya inicio sesión?
	private Boolean estaAutenticado(Usuario usuario) {
		return registro.inicioSesion(usuario); // Dispara las distintas excepciones de porque este metodo podria fallar
		// UsuarioDebeIniciarSesionException(), UsuarioInexistenteException, etc.
	}

	// Autenticarse
	// Con cuenta de Gmail o Usuario
	public void iniciarSesion() {
	}

	public void registrarse(Usuario usuario) {
		if (registro.sePuedeRegistrar(usuario)) {
			registro.registrar(usuario);
			this.agregar(usuario);
		}
	}

	////////////////////////////////////// BUSQUEDAS SUBASTAS
	////////////////////////////////////// /////////////////////////////////////////////////////

	public ArrayList<Subasta> subastasEnProgreso() {
		ArrayList<Subasta> enProgreso = new ArrayList<Subasta>();
		for (int i = 0; i < subastas.size(); i++) {
			if (subastas.get(i).estaEnProgreso())
				enProgreso.add(subastas.get(i));
		}
		return enProgreso;
	}

	public ArrayList<Subasta> buscarPorTitulo(String titulo) {
		return home.buscarPorTitulo(titulo, subastasEnProgreso());
	}

	public ArrayList<Subasta> buscarPorDescripcion(String descripcion) {
		return home.buscarPorDescripcion(descripcion, subastasEnProgreso());
	}

	public ArrayList<Subasta> buscarPopulares() {
		return home.subastasPopulares(subastasEnProgreso());
	}

	public ArrayList<Subasta> buscarProximasAFinalizar() {
		return home.subastasPorTerminar(subastas);
	}

	public ArrayList<Subasta> buscarRecientes() {
		return home.subastasRecientes(subastas);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////

	// private methods

	public ArrayList<Subasta> getSubastas() {
		return this.subastas;
	}

	public ArrayList<Usuario> getUsuarios() {
		return this.usuarios;
	}

	private void agregar(Usuario usuario) {
		usuarios.add(usuario);
		usuario.setPerfil(new Registrado()); // Para que estoy haciendo esto si despues no lo uso?
	}

	private ArrayList<Subasta> subastasEnProgreso(Usuario usuario) {
		ArrayList<Subasta> subastasEnProgreso = new ArrayList<Subasta>();
		for (int i = 0; i < subastas.size(); i++) {
			if (subastas.get(i).estaEnProgresoPara(usuario))
				subastasEnProgreso.add(subastas.get(i));
		}
		return subastasEnProgreso;
	}

	// Revisar este, se puede expresar de otra manera para que quede mejor la
	// excepcion
	private Boolean tieneMenosSubastasEnProgresoQueLaCantMaximaPermitida(Usuario usuario) {
		if (subastasEnProgreso(usuario).size() < 5)
			return true;
		else
			throw new NoPuedesTenerMasDeCincoSubastasEnProgresoException();
	}
	
	public ArrayList<Subasta> subastasEnLasQueParticipo(Usuario usuario) {
		ArrayList<Subasta> subastasUsuarioPostor = new ArrayList<Subasta>();
		for(int i=0; i < subastas.size(); i++) {
			if(subastas.get(i).tieneComoPostor(usuario)) subastasUsuarioPostor.add(subastas.get(i));
		}
		return subastasUsuarioPostor;
	}

}
