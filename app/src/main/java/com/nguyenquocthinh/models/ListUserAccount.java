package com.nguyenquocthinh.models;

import java.util.ArrayList;
import java.util.List;

public class ListUserAccount {
    private static List<UserAccount> accounts = new ArrayList<>();

    static {
        accounts.add(new UserAccount("admin", "123", "admin"));
        accounts.add(new UserAccount("employee", "123", "employee"));
    }

    public static UserAccount login(String username, String password) {
        for (UserAccount acc : accounts) {
            if (acc.getUsername().equals(username) && acc.getPassword().equals(password)) {
                return acc;
            }
        }
        return null;
    }

    public static List<UserAccount> getAccounts() {
        return accounts;
    }
}
