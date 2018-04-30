package waterloodevs.triviaapp;

/**
 * Created by Jeevan on 2018-04-01.
 */

public class UserInfo {

    String name;
    String email;
    String password;
    String walletaddress;
    int balance;

    public UserInfo(){
    }

    public UserInfo(String name, String email, String password, String walletaddress) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.walletaddress = walletaddress;
        this.balance = 0;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getWalletaddress() {
        return walletaddress;
    }

    public int getBalance() {
        return balance;
    }
}
