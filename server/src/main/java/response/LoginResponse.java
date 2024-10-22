package response;

public record LoginResponse(String username, String authToken) {
    @Override
    public String toString() {
        return "LoginResponse{" +
                "username='" + username + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
