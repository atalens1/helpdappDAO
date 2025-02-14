package com.iticbcn.usuaris.Controllers;

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;

import com.iticbcn.usuaris.DAO.PeticioDAO;
import com.iticbcn.usuaris.DAO.UsuariDAO;
import com.iticbcn.usuaris.Model.Peticio;
import com.iticbcn.usuaris.Model.Usuari;
import com.iticbcn.usuaris.Views.InputView;

public class PeticioController {

    public static List<Peticio> LlistarPeticions(SessionFactory sf) throws Exception {

        PeticioDAO petdao = new PeticioDAO(sf);
        return petdao.getAll();
    }
    
    public static void AfegirPeticioUsuari(BufferedReader bf,SessionFactory sf) throws Exception {

        Peticio p = new Peticio();
        Boolean addUser = true;
        Set<Usuari> users = new HashSet<Usuari>();
        String opt ;

        UsuariDAO udao = new UsuariDAO(sf);

        p = InputView.NovaPeticioUsuari(bf);

        while (addUser) {
            String dni = InputView.DemanarDNIUsuari(bf);

        // Comprovar si ja existeix
            Usuari usuari = udao.getUsuariByDNI(dni);
                        
            if (usuari == null) {
                usuari = InputView.DemanarDadesUsuari(bf,dni);
                p.addUsuari(usuari);
            } else {
                System.out.println("Usuari existent trobat: " + usuari.getNomUsuari());
                users.add(usuari);
            }

            System.out.print("Vols introduir un altre usuari? (N per no introduir) ");
            opt = InputView.LecturaEntrada(bf);

            if (opt.equalsIgnoreCase("N")){
                addUser = false;
            }
        }

        PeticioDAO petdao = new PeticioDAO(sf);
        try {
            petdao.save(p);
            System.out.println("Petició afegida correctament");
        } catch (Exception e) {
            System.err.println("Error a l'inserir petició: " + e.getMessage());
        }
        
        //Aquesta condició permetrà afegir aquells usuaris existents a la petició

        if (!users.isEmpty()) {
        //En aquest punt creem un set d'usuaris per emmagatzemar temporalment els usuaris ja afegits
            Set<Usuari> uset = p.getUsuaris();
        //recorrem per afegir els usuaris existents a uset
            for (Usuari u:users) {
                uset.add(u);
            }
        //canviem el set d'usuaris per l'uset, que conté ja tots els usuaris
            p.setUsuaris(uset);
        //Modifiquem petició
            try {
                petdao.update(p);
                System.out.println("Usuaris modificats correctament");   
            } catch (Exception e) {
                System.err.println("Error afegint usuaris existents: " + e.getMessage());
            }
            
        }
    }

    public static void ModificarPeticio(BufferedReader bf, SessionFactory sf) throws Exception {
        int idPeticio;
        Peticio p = null;
        Set<Usuari> users = new HashSet<Usuari>();
        PeticioDAO petdao = new PeticioDAO(sf);
        UsuariDAO udao = new UsuariDAO(sf);
        boolean addUser = true;
    
        while (p == null) {  // Repetir fins que s'obtingui una petició vàlida
            idPeticio = InputView.DemanarIdPeticio(bf);
            p = petdao.get(idPeticio);
        }
            
        String entrada = InputView.DemanarAccioModificarPeticio(bf);

        if (entrada.equalsIgnoreCase("a")) {
            System.out.print("Indiqueu el nou estat (Tancada) o (En progrés): ");
            p.setEstatPeticio(InputView.LecturaEntrada(bf));
            try {
                petdao.update(p);
                System.out.println("Estat de petició modificat correctament");
            } catch (Exception e) {
                System.err.println("Error al modificar la petició" + e.getMessage());
            }
            
        } else if (entrada.equalsIgnoreCase("b")) {
            
            while (addUser) {
                String dni = InputView.DemanarDNIUsuari(bf);
            // Comprovar si ja existeix
                Usuari usuari = udao.getUsuariByDNI(dni);

                if (usuari == null) {
                    usuari = InputView.DemanarDadesUsuari(bf, dni);
                /*cal persistir l'usuari per separat, el mètode merge de petició no ho farà */
                    try {
                        udao.save(usuari);
                        System.out.println("usuari persistit correctament");
                    } catch (Exception e) {
                        System.err.println("Error afegint usuaris: " + e.getMessage());
                    }
                    
                } else {
                    System.out.println("Usuari existent trobat: " + usuari.getNomUsuari());

                }
                
                users.add(usuari);

                System.out.print("Vols introduir un altre usuari? (N per no introduir) ");

                if (InputView.LecturaEntrada(bf).equalsIgnoreCase("N")){
                    addUser = false;
                }
            }

            if (!users.isEmpty()) {
                //En aquest punt creem un set d'usuaris per emmagatzemar temporalment els usuaris ja afegits
                    Set<Usuari> uset = p.getUsuaris();
                //recorrem per afegir els usuaris existents a uset
                    for (Usuari u:users) {
                        uset.add(u);
                    }
                //canviem el set d'usuaris per l'uset, que conté ja tots els usuaris
                    p.setUsuaris(uset);
                //Modifiquem petició
                try {
                    petdao.update(p);
                    System.out.println("Usuaris afegits correctament");
                } catch (Exception e) {
                    System.err.println("Error afegint usuaris a la petició" + e.getMessage());
                }
                }
        }
    }

    public static void EsborrarPeticio (BufferedReader bf, SessionFactory sf) throws Exception {

        int idPeticio;
        Peticio p = null;
        PeticioDAO petdao = new PeticioDAO(sf);


        //Esborrem sols Peticio, respectem els usuaris

        while (p == null) {  // Repetir fins que s'obtingui una petició vàlida
            idPeticio = InputView.DemanarIdPeticio(bf);
            p = petdao.get(idPeticio);
        }

        String entrada = InputView.ConfirmacioEsborrament(bf, p.getDescPeticio()); 

        if (entrada.equalsIgnoreCase("s")) {
            try {
                petdao.delete(p);
                System.out.println("Petició esborrada correctament");
            } catch (Exception e) {
                System.err.println("Error esborrant petició: "+e.getMessage());
            }
            
        }
    }
      
}
