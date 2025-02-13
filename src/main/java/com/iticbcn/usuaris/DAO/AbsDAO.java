package com.iticbcn.usuaris.DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import jakarta.validation.ConstraintViolationException;

public abstract class AbsDAO<T> implements IDAO<T> {

    private SessionFactory sessionFactory;
    private Class<T> classe;


    public AbsDAO(SessionFactory sessionFactory, Class<T> classe) {
        this.sessionFactory = sessionFactory;
        this.classe = classe;
    }

    @Override
    public void save(T entity) throws Exception {
        try (Session ses = sessionFactory.openSession()) {
            ses.beginTransaction();
            try {
                ses.persist(entity);
                ses.getTransaction().commit();
            } catch (JDBCException jdbcex) {
                handleException(ses, jdbcex, "Error de JDBC");                
            } catch (ConstraintViolationException cnsex) {
                handleException(ses, cnsex, "Error de restricció en claus");          
            } catch (HibernateException hbex) {
                handleException(ses, hbex, "Error d'Hibernate a la transacció");     
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
    }

    @Override
    public Optional<T> get(int id) throws Exception {
        T entity = null;
    
        try (Session ses = sessionFactory.openSession()) {
            try {
                entity = ses.find(classe, id);
            } catch (JDBCException jdbcex) {
                handleException(ses, jdbcex, "Error de JDBC");                       
            } catch (HibernateException hbex) {
                handleException(ses, hbex, "Error d'Hibernate a la consulta");     
            } catch (Exception ex) {
                handleException(ses, ex, "Altres excepcions");    
            }
        } catch (SessionException sesexcp) {
            System.err.println("Error de Sessió: " + sesexcp.getMessage());
            throw sesexcp;
        } catch (HibernateException hbex) {
            System.err.println("Error d'Hibernate: " + hbex.getMessage());
            throw hbex;
        }
    
        return Optional.ofNullable(entity);
    }
    
    @Override
    public void update(T entity) throws Exception {
        try (Session ses = sessionFactory.openSession()) {
            ses.beginTransaction();
            try {
                ses.merge(entity);
                ses.getTransaction().commit();
            } catch (JDBCException jdbcex) {
                handleException(ses, jdbcex, "Error de JDBC");                
            } catch (ConstraintViolationException cnsex) {
                handleException(ses, cnsex, "Error de restricció en claus");          
            } catch (HibernateException hbex) {
                handleException(ses, hbex, "Error d'Hibernate a la transacció");     
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
    }

    @Override
    public void delete(T entity) throws Exception {
        try (Session ses = sessionFactory.openSession()) {
            ses.beginTransaction();
            try {
                ses.remove(entity);
                ses.getTransaction().commit();
            } catch (JDBCException jdbcex) {
                handleException(ses, jdbcex, "Error de JDBC");                
            } catch (ConstraintViolationException cnsex) {
                handleException(ses, cnsex, "Error de restricció en claus");          
            } catch (HibernateException hbex) {
                handleException(ses, hbex, "Error d'Hibernate a la transacció");     
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
    }

    @Override
    public List<T> getAll() throws Exception {
    List<T> entities = new ArrayList<>();

    // Obre una sessió amb Hibernate
    try (Session ses = sessionFactory.openSession()) {
        try {
                Query<T> q = ses.createQuery("from " + classe.getName(), classe); 
                entities = q.list();
        } catch (Exception e) {
            // Gestionem l'error aquí (log, re-lançar excepció, etc.)
            throw new Exception("Error en obtenir les entitats.", e);
        }
    } catch (Exception e) {
        // Error a l'obrir la sessió Hibernate
        throw new Exception("Error al gestionar la sessió Hibernate.", e);
    }

    return entities;
}

    private void handleException(Session ses, Exception ex, String errorMsg) throws Exception{
        if (ses.getTransaction() != null && ses.getTransaction().isActive()) {
            ses.getTransaction().rollback();
        }
        System.err.println(errorMsg + ": " + ex.getMessage());
        throw ex;
    }


}
