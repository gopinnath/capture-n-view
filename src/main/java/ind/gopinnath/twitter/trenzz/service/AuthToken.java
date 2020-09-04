package ind.gopinnath.twitter.trenzz.service;

public class AuthToken {

	private String token_type;
	
	private String access_token;

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	@Override
	public String toString() {
		return "AuthToken [token_type=" + token_type + ", access_token=" + access_token + "]";
	}
	
	
}
