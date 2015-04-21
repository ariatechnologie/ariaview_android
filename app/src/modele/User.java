package modele;

public class User {
	private int id;
	private String login;
	private String password;
	private String site;
	
	public User(){
		
	}
	
	
	public User(int id, String login, String password, String site) {
		super();
		this.id = id;
		this.login = login;
		this.password = password;
		this.site = site;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSite() {
		return site;
	}
	
	public void setSite(String site) {
		this.site = site;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", login=" + login + ", password=" + password
				+ ", site=" + site + "]";
	}
	
	
	
}
