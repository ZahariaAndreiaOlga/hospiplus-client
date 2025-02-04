package org.hospi.hospiplusclient.utils;

public class TokenStorage {

    private static String jwtToken = null;
    private static String role = "user";

    public static void setJwtToken(String token){
        jwtToken = token;
    }

    public static String getJwtToken(){
        return jwtToken;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        TokenStorage.role = role;
    }

    public static void clearJwtToken(){
        jwtToken = null;
    }

}
