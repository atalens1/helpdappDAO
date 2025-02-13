package com.iticbcn.usuaris.DAO;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.iticbcn.usuaris.Model.*;

public class UsuariDAO extends AbsDAO<Usuari>{

    private SessionFactory sessionFactory;

    public UsuariDAO(SessionFactory sessionFactory) {
        super(sessionFactory,Usuari.class);
    }

    public Usuari getUsuariByDNI(String dni) throws Exception {

        Usuari usuari = null;

        try (Session ses = sessionFactory.openSession()) {
            try {
                Query<Usuari> query = ses.createQuery("FROM Usuari WHERE dniUsuari = :dni", Usuari.class);
                query.setParameter("dni", dni);
                usuari = query.uniqueResult();
            } catch (JDBCException jdbcex) {
                handleException(ses, jdbcex, "Error de JDBC");                       
            } catch (HibernateException hbex) {
                handleException(ses, hbex, "Error d'Hibernate a la consulta");     
            } catch (Exception ex) {
                handleException(ses, ex, "Altres excepcions");    
            }
        } catch (SessionException sesexcp) {
            System.err.println("Error de Sessió: "+sesexcp.getMessage());
            throw sesexcp;
        } catch (HibernateException hbex) {
            System.err.println("Error d'Hibernate: "+hbex.getMessage());
            throw hbex;
        }

        return usuari;

    }
    
    private void handleException(Session ses, Exception ex, String errorMsg) throws Exception{
        if (ses.getTransaction() != null && ses.getTransaction().isActive()) {
            ses.getTransaction().rollback();
        }
        System.err.println(errorMsg + ": " + ex.getMessage());
        throw ex;
    }

    
}

