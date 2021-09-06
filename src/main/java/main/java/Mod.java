package main.java;

public class Mod {
	public String name;
	public String id;
	public Boolean install;
	public String version;
	
	public Mod( String _name, String _id, Boolean _install, String _version ) {
		this.name = _name;
		this.id = _id;
		this.install = _install;
		this.version = _version;
	}
}
