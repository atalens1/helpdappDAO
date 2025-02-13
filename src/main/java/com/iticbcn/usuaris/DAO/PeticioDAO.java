package com.iticbcn.usuaris.DAO;

import org.hibernate.SessionFactory;
import com.iticbcn.usuaris.Model.*;

public class PeticioDAO extends AbsDAO<Peticio> {

    public PeticioDAO(SessionFactory sessionFactory) {
        super(sessionFactory,Peticio.class);
    }

}