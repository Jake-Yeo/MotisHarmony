/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author 1100007967
 */
public class Exit {

    public static void properlyExitProgram() throws Exception {
        Accounts.getLoggedInAccount().serializeAccount();
        System.exit(0);
    }
}
