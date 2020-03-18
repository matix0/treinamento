package com.stefanini.model;

import org.h2.tools.RunScript;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import java.util.stream.Stream;

public class EnderecoTest {
	private Validator validator;
    private SessionFactory factoryJpa;
    private Boolean h2Carregador = Boolean.FALSE;

    private  String uf = "DF";

    @Before
    public void setUp() {
        runScrip();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure()
                .build();
        factoryJpa = new MetadataSources(registry).buildMetadata().buildSessionFactory();

    }

    public void runScrip() {
        Connection conn = null;
        try {
            conn = DriverManager.
                    getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
            if (conn != null) {
                final Statement st = conn.createStatement();
                final ResultSet rs = st.executeQuery("show tables");
                while (rs.next()) {
                    h2Carregador = true;
                }
                if (!h2Carregador) {
                    ClassLoader classLoader = getClass().getClassLoader();
                    File file = new File(classLoader.getResource("db.sql").getFile());
                    System.out.println("Carregado o SCRIPT");
                    RunScript.execute(conn, new FileReader(file));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Buscando utilizando o Criteria Builder
     * Evitar Erros de sintaxe
     *
     * @param session
     * @param name
     * @return
     */
    
    private Endereco findEnderecoCriteria(Session session, String uf) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Endereco> q = cb.createQuery(Endereco.class);
        Root<Endereco> entityRoot = q.from(Endereco.class);
        q.select(entityRoot);
        ParameterExpression<String> e = cb.parameter(String.class);
        q.where(cb.equal(entityRoot.get("uf"), uf));
        return session.createQuery(q).getSingleResult();
    }
    
    /**
     * EFETUAR A  COM NameQuery
     */
    @Test()
    public void findEnderecoCriteria() {
        try (Session session = factoryJpa.openSession()) {
            Endereco Endereco1 = findEnderecoCriteria(session, uf);
            System.out.println("Endereço: " +Endereco1.getUf());
            System.out.println("Novo: " + Endereco1);
        }
    }

    /**
     * Buscando utilizando o Criteria Builder
     * Evitar Erros de sintaxe
     *
     * @param session
     * @param name
     * @return
     */
   
    private Long countEnderecoCriteria(Session session, String uf) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Endereco> entityRoot = q.from(Endereco.class);
        q.select(cb.count(entityRoot));
        ParameterExpression<String> p = cb.parameter(String.class);
        q.where(cb.equal(entityRoot.get("uf"), uf));
        System.out.println("QUERY : " + session.createQuery(q).getQueryString());
        return session.createQuery(q).getSingleResult();
    }
    
    /**
     * EFETUAR A  COM NameQuery
     */
    @Test()
    public void findCountEndereco() {
        try (Session session = factoryJpa.openSession()) {
            Long qtd = countEnderecoCriteria(session, uf);
            System.out.println("QTD: " + qtd);
        }
    }
    

    
}
