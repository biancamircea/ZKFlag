package ro.mta.toggleserverapi.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class encryptPass {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("1234");
        System.out.println("Parola criptată: " + encodedPassword);
    }
}
